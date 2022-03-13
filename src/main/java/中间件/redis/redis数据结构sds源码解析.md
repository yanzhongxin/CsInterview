# redis3.0版本sds相关源码解析
[Redis的字符串的底层实现SDS](https://blog.csdn.net/qq_25448409/article/details/107136754)
[面试官：你看过Redis数据结构底层实现吗？ ](https://www.cnblogs.com/javazhiyin/p/11063944.html)
[redis String类型底层数据结构 sds](https://blog.csdn.net/z69183787/article/details/105821424)

## string结构体设计


```
struct sdshdr {
    // buf 中已占用空间的长度
    int len;
    // buf 中剩余可用空间的长度
    int free;
    // 实际string数据存储空间
    char buf[];
};
类型别名，用于指向 sdshdr 的 buf 属性
typedef char *sds;
```
## redis获取字符串长度底层实现

```
T = O(1) 时间复杂度

static inline size_t sdslen(const sds s) {
    sh是结构体指针，指向存储string的结构体。
    s是指向结构体内部数据存储空间的数组，通过s指针代表结构体字符串数组的地址，
    减去结构体的大小即是结构体的地址
    struct sdshdr *sh = (void*)(s-(sizeof(struct sdshdr)));
    return sh->len;
}
```

## redis分配一个新的字符串


```
输入一个字符串指针init 指向字符串
/*
 * 根据给定字符串 init ，创建一个包含同样字符串的 sds
 * 参数
 *  init ：如果输入为 NULL ，那么创建一个空白 sds
 *         否则，新创建的 sds 中包含和 init 内容相同字符串
 * 返回值
 *  sds ：创建成功返回 sdshdr 相对应的 sds
 *        创建失败返回 NULL
 * 复杂度
 *  T = O(N)
 */
输入:字符串指针，返回：buffer数组的字符串指针
sds sdsnew(const char *init) {
    size_t initlen = (init == NULL) ? 0 : strlen(init);
    return sdsnewlen(init, initlen);
}
分配一个sds结构体指针，利用zmalloc函数分配结构体内存
已使用len设置0，剩余free为length，buff数组最后一个位置设置为 \0 字符串结束标识符
返回指向buff数组的字符指针
sds sdsnewlen(const void *init, size_t initlen) {
    struct sdshdr *sh;
    // 根据是否有初始化内容，选择适当的内存分配方式
    // T = O(N)
    if (init) {
        // zmalloc 不初始化所分配的内存
        sh = zmalloc(sizeof(struct sdshdr)+initlen+1);
    } else {
        // zcalloc 将分配的内存全部初始化为 0
        sh = zcalloc(sizeof(struct sdshdr)+initlen+1);
    }
    // 内存分配失败，返回
    if (sh == NULL) return NULL;
    // 设置初始化initlen长度
    sh->len = ;
    // 新 sds 不预留任何空间
    sh->free = 0;
    // 如果有指定初始化内容，将它们复制到 sdshdr 的 buf 中
    // T = O(N)
    if (initlen && init)
        memcpy(sh->buf, init, initlen);
    // 以 \0 结尾
    sh->buf[initlen] = '\0';
    // 返回 buf 部分，而不是整个 sdshdr
    return (char*)sh->buf;
}
```
## 中间件.redis“释放”给定的sds结构体

```
/*
 * 彻底释放结构体的内存 free(realptr)
 * 复杂度
 *  T = O(N)/
void sdsfree(sds s) {
    if (s == NULL) return;
    zfree(s-sizeof(struct sdshdr));// 给出结构体的内存地址
}
zfree中调用 update_zmalloc_stat_free(oldsize+PREFIX_SIZE);
free(realptr);释放结构体内存

```


```
/*
 * 在不释放 SDS 的字符串空间的情况下，
 * 重置 SDS 所保存的字符串为空字符串。
 * 复杂度
 *  T = O(1)
 */
取出sds,重置长度为0，剩余长度为旧free+旧len，惰性释放，方便后来更改字符串
并不是真正释放这个结构体的存储空间，
void sdsclear(sds s) {
    // 取出 sdshdr
    struct sdshdr *sh = (void*) (s-(sizeof(struct sdshdr)));
    // 重新计算属性
    sh->free += sh->len;
    sh->len = 0;
    // 将结束符放到最前面（相当于惰性地删除 buf 中的内容）
    sh->buf[0] = '\0';
}
```

## sds字符串追加操作

```
sds sdscatsds(sds s, const sds t) {
    return sdscatlen(s, t, sdslen(t));
}

 * 将长度为 len 的字符串 t 追加到 sds 的字符串末尾
 * 返回值
 * sds ：追加成功返回新 sds ，失败返回 NULL
 * 复杂度
 *  T = O(N)
 追加思路：
 1.确认剩余buff空间 =len，能够存下新的字符串，否则扩容
 2.使用c语言的内存拷贝函数，memcpy(void *destin, void *source, unsigned n),
 从destin地址开始拷贝新的字符串
 3. 修改旧的sds结构体的len和free数值，buff尾部\0保证字符串结束
sds sdscatlen(sds s, const void *t, size_t len) {
    struct sdshdr *sh;
    size_t curlen = sdslen(s);
    // 扩展 sds 空间
    // T = O(N)
    //对 sds 中 buf 的长度进行扩展，确保在函数执行之后，buf 至少会有 addlen + 1 长度的空余空间
    如果剩余空间free大于新增加字符串len的话，直接返回旧的sds，否则的话一般扩容新长度newlength=(旧length+新增的字符串长度len)*2
    s = sdsMakeRoomFor(s,len);
    // 内存不足？直接返回
    if (s == NULL) return NULL;
    // 复制 t 中的内容到字符串后部
    // T = O(N)
    sh = (void*) (s-(sizeof(struct sdshdr)));
    memcpy(s+curlen, t, len);
    // 更新属性
    sh->len = curlen+len;
    sh->free = sh->free-len;
    // 添加新结尾符号
    s[curlen+len] = '\0';
    // 返回新 sds
    return s;
}
```
`********`
# redis list底层实现
[Redis进阶-List底层数据结构精讲](https://blog.csdn.net/yangshangwei/article/details/105744871)
[Redis列表list 底层原理 知乎](https://zhuanlan.zhihu.com/p/102422311)

# redis hash实现原理
[Redis之Hash数据结构底层原理 ziplist hashtable](https://blog.csdn.net/chongfa2008/article/details/119537064)

[w3c hash redis设计与实现](https://www.w3cschool.cn/hdclil/kpz7mozt.html)
# cache
自定义缓存框架完美集成到spring-boot中
## 这里主要利用spring-boot中META-INF下spring.factories

## 如何使用
这里主要的注解有三个
@Cache 它作用在类上来告诉框架这个类将采用缓存（这个类只能用在实现类上，也就是这个类必须要有接口）
@PUT 它作用从缓存中获取数据，及将数据存放在缓存中，它上面有两个参数expire 过期时间 不设则认为不过期，useAgs是否采用参数生成key
@DISABLED 它的作用是让缓存中的数据实现，主要和PUT配合使用 上面的参数有class,methodName 它们为了找到PUT作用的方法 useAgs和那个一样

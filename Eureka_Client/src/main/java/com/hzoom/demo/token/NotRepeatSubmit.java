package com.hzoom.demo.token;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防止重复提交
 *
 * 1、对于一些重要的操作需要防止客户端重复提交的(如非幂等性重要操作)，具体办法是当请求第一次提交时将sign作为key保存到redis，并设置超时时间，超时时间和Timestamp中设置的差值相同。当同一个请求第二次访问时会先检测redis是否存在该sign，如果存在则证明重复提交了，接口就不再继续调用了。如果sign在缓存服务器中因过期时间到了，而被删除了，此时当这个url再次请求服务器时，因token的过期时间和sign的过期时间一直，sign过期也意味着token过期，那样同样的url再访问服务器会因token错误会被拦截掉，这就是为什么sign和token的过期时间要保持一致的原因。拒绝重复调用机制确保URL被别人截获了也无法使用（如抓取数据）。
 *
 * 2、对于哪些接口需要防止重复提交可以自定义个注解来标记。
 *
 * 3、注意：所有的安全措施都用上的话有时候难免太过复杂，在实际项目中需要根据自身情况作出裁剪，比如可以只使用签名机制就可以保证信息不会被篡改，或者定向提供服务的时候只用Token机制就可以了。如何裁剪，全看项目实际情况和对接口安全性的要求
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotRepeatSubmit {
    /**
     * 过期时间，单位毫秒
     **/
    long value() default 5000;
}

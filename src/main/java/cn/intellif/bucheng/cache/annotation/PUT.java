package cn.intellif.bucheng.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PUT {
    //失效时间默认不失效(单位为秒)
    int expire() default -1;
    //是否需要参数默认需要
    boolean useAgs() default true;
}

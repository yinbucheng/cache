package cn.intellif.bucheng.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DISABLED {
    //需要失效的class类默认为当前的class类型
    Class clazz()  ;
    //需要失效的方法
    String methodName() default "";
    //是否需要参数默认不需要
    boolean useAgs() default false;
}

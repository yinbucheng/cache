package cn.intellif.bucheng.cache.core;

import cn.intellif.bucheng.cache.annotation.Cache;
import cn.intellif.bucheng.cache.annotation.DISABLED;
import cn.intellif.bucheng.cache.annotation.PUT;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class CacheCore implements BeanPostProcessor {

    @Autowired
    private CacheManager cacheManager;


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //判断类是否需要开启缓存
        final Class clazz = bean.getClass();
        Annotation annotation =  clazz.getAnnotation(Cache.class);
        if(annotation==null)
            return bean;
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>开始代理了");
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>方法被代理了........");
                PUT put = method.getAnnotation(PUT.class);
                DISABLED disabled = method.getAnnotation(DISABLED.class);
                //获取实现类上的方法
                if(put==null){
                    String methodName = method.getName();
                    Method tempMethod =  clazz.getMethod(methodName,method.getParameterTypes());
                    put = tempMethod.getAnnotation(PUT.class);
                }
                if(disabled==null){
                    String methodName = method.getName();
                    Method tempMethod = clazz.getMethod(methodName,method.getParameterTypes());
                    disabled = tempMethod.getAnnotation(DISABLED.class);
                }
                if(put==null&&disabled==null)
                    return method.invoke(bean,args);
                if(put!=null&&disabled!=null){
                    throw new RuntimeException("PUT 和 DISABLED 注解不能同时在同一个方法上面使用");
                }
                if(put!=null){
                    boolean flag =  put.useAgs();
                    String key = null;
                    if(flag) {
                        key =   createKey(clazz, method.getName(), args);
                    }else{
                        key = createKey(clazz,method.getName(),null);
                    }
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>key:"+key);
                    Object value = cacheManager.get(key);
                    if(value!=null&&!"".equals(value))
                        return value;
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>value:"+value);
                    Object result = method.invoke(bean,args);
                    if(result!=null&&!"".equals(result)){
                        int expireTime =  put.expire();
                        if(expireTime!=-1){
                            cacheManager.put(key,result);
                        }else{
                            cacheManager.setEx(key,result,expireTime*1000);
                        }
                    }
                    return result;
                }else if(disabled!=null){
                    Class tempClazz = clazz;
                    if(disabled.clazz()!=null){
                        tempClazz = disabled.getClass();
                    }
                    String methodName = disabled.methodName();
                    boolean flag = disabled.useAgs();
                    String key = null;
                    if(flag){
                        key = createKey(tempClazz,methodName,args);
                    }else{
                        key = createKey(clazz,methodName,null);
                    }
                    cacheManager.invalid(key);
                    return method.invoke(bean,args);
                }
                return method.invoke(bean,args);
            }
        });
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    /**
     * 构造出唯一的key
     * @param clazz 类型对象
     * @param methodName 方法名称
     * @param params 参数
     * @return
     */
      private String createKey(Class clazz,String methodName,Object[] params){
        StringBuilder sb = new StringBuilder(clazz.getName()).append(".").append(methodName);
        if(params!=null){
            int len = params.length;
            for(int i=0;i<len;i++){
                sb.append(params[i]);
                if(i!=len-1)
                    sb.append(":");
            }
        }
        return sb.toString();
    }



}

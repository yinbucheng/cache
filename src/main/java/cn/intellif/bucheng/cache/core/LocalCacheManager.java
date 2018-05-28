package cn.intellif.bucheng.cache.core;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地缓存
 * 作者：尹冲
 */
public class LocalCacheManager implements CacheManager{

    //最多存放的对象
    private static int MAX_SIZE=200;

    private ConcurrentHashMap<String,BeanDefination> cache = new ConcurrentHashMap<>();

    public LocalCacheManager() {
    }

    /**
     * 将数据放入到缓存中
     * @param key
     * @param value
     */
    public synchronized void put(String key,Object value){
        recycle();
        cache.put(key,new BeanDefination(System.currentTimeMillis(),value,-1L));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>将数据放入缓存中");
    }


    /**
     * 这里采用模糊匹配
     * @param key
     */
    public synchronized void invalid(String key){
        Set<String> keys = cache.keySet();
        if(key!=null){
            for(String temp:keys){
                if(temp.startsWith(key)){
                    cache.remove(temp);
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>删除缓存中数据为:key "+temp);
                }
            }
        }
    }

    /**
     *
     * @param key
     * @param value
     * @param outTime 过期时间单位是秒
     */
    public synchronized void setEx(String key,Object value,long outTime){
        recycle();
        cache.put(key,new BeanDefination(System.currentTimeMillis(),value,outTime*1000));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>将数据放入缓存并设置过期时间");
    }

    //开始回收
    private void recycle(){
        if(cache.size()>MAX_SIZE){
            Set<String> keys = cache.keySet();
            //回收过期的数据
            long currentTime = System.currentTimeMillis();
            Long totalTime = 0L;
            int count = 0;

            for(String key:keys){
                BeanDefination data = cache.get(key);
                long time = data.time;
                long outTime = data.outTime;
                if(outTime==-1L) {
                    totalTime+=time;
                    count++;
                    continue;
                }
                long temp = currentTime - time;
                if(temp>outTime) {
                      cache.remove(key);
                }
            }

            //如果还大于
           if(cache.size()>MAX_SIZE){
                long avgTime = totalTime/count;
               keys = cache.keySet();
               for(String key:keys){
                   BeanDefination data = cache.get(key);
                   long time = data.time;
                   if(time>avgTime){
                       cache.remove(key);
                   }
               }
           }
        }
    }

    public Object get(String key){
        BeanDefination data = cache.get(key);
        if(data==null)
            return null;
        long time = data.time;
        long outTime = data.outTime;
        if(outTime==-1L){
            return data.data;
        }
        long currentTime = System.currentTimeMillis();
        long temp = currentTime - time;
        if(temp>outTime){
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>获取失败，移除数据"+key);
            cache.remove(key);
            return null;
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>从缓存中获取数据:"+key);
        return data.data;
    }


    private class BeanDefination{
        //存放的时间
        private Long time;
        //缓存的数据
        private Object data;
        //过期时间长度 -1表示不过期
        private Long outTime;

        public BeanDefination(Long time, Object data,Long outTime) {
            this.time = time;
            this.data = data;
            this.outTime = outTime;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public Long getOutTime() {
            return outTime;
        }

        public void setOutTime(Long outTime) {
            this.outTime = outTime;
        }
    }
}

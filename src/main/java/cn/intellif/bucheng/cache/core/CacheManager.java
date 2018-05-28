package cn.intellif.bucheng.cache.core;

public interface CacheManager {
      void setEx(String key,Object value,long outTime);
      void invalid(String key);
      void put(String key,Object value);
      Object get(String key);
}

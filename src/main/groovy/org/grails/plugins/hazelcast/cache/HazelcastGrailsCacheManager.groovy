package org.grails.plugins.hazelcast.cache


import com.hazelcast.config.MapConfig
import com.hazelcast.config.NearCacheConfig
import com.hazelcast.core.IMap
import com.hazelcast.spring.cache.HazelcastCacheManager
import groovy.util.logging.Slf4j
import org.grails.plugin.cache.GrailsCacheManager
import org.grails.plugins.hazelcast.cache.HazelcastCacheConfiguration.HzCacheConfig
import org.springframework.cache.Cache

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Created by ma33fyza on 21.02.17.
 */
@Slf4j
class HazelcastGrailsCacheManager extends HazelcastCacheManager implements GrailsCacheManager{

    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>()

    @Override
    boolean cacheExists(String name) {
        getCacheNames().contains(name)
    }

    @Override
    boolean destroyCache(String name) {
        getCache(name).getCache().destroy()
        return true
    }

    @Override
    GrailsHazelcastCache getCache(String name, MapConfig config = null) {
        Cache cache = caches.get(name)
        if (cache == null) {
            if (config){
                hazelcastInstance.getConfig().addMapConfig(config)
            }
            IMap map = hazelcastInstance.getMap(name)
            cache = createGrailsHazelcastCache(map)
            Cache currentCache = caches.putIfAbsent(name, cache)
            if (currentCache != null) {
                cache = currentCache
            }
        }
        return cache;
    }

    GrailsHazelcastCache createGrailsHazelcastCache(IMap map){
        new GrailsHazelcastCache(map)
    }

    void setConfiguration(HazelcastCacheConfiguration configuration) {
        configuration.caches.each { String key, Map value ->
            hazelcastInstance.getConfig().addMapConfig(convert(value))
        }
    }


    MapConfig convert(Map config){
        if (config.name){
            new MapConfig(config.name)
                    .setEvictionPolicy(config.evictionPolicy)
                    .setTimeToLiveSeconds(config.timeToLiveSeconds)
                    .setMaxIdleSeconds(config.maxIdleSeconds)
                    .setNearCacheConfig(config.nearCacheConfig?new NearCacheConfig()
                        .setName(config.nearCacheConfig.name)
                        .setEvictionPolicy(config.nearCacheConfig.evictionPolicy)
                        .setTimeToLiveSeconds(config.nearCacheConfig.timeToLiveSeconds)
                        .setMaxIdleSeconds(config.nearCacheConfig.maxIdleSeconds):null
            )
        }
    }

}

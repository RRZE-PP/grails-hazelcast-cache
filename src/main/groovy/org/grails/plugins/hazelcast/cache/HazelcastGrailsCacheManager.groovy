package org.grails.plugins.hazelcast.cache

import com.hazelcast.core.IMap
import com.hazelcast.spring.cache.HazelcastCacheManager
import grails.plugin.cache.GrailsCacheManager
import groovy.util.logging.Slf4j
import org.springframework.cache.Cache

/**
 * Created by ma33fyza on 21.02.17.
 */
@Slf4j
class HazelcastGrailsCacheManager extends HazelcastCacheManager implements GrailsCacheManager{

    @Override
    boolean cacheExists(String name) {
        getCacheNames().contains(name)
    }

    @Override
    boolean destroyCache(String name) {
        if(log.traceEnabled)
            log.trace "Destroying cache with name $name not supported!"
        return false
    }

    @Override
    Cache getCache(String name) {
        Cache cache = caches.get(name)
        if (cache == null) {
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
}

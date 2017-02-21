package org.grails.plugin.hazelcast.cachemanager

import com.hazelcast.core.DistributedObject
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import grails.plugin.cache.GrailsCacheManager
import org.springframework.cache.Cache

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Created by ma33fyza on 21.02.17.
 */
class HazelcastGrailsCacheManager implements GrailsCacheManager{

    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>()
    HazelcastInstance hazelcastInstance


    @Override
    boolean cacheExists(String name) {
        getCacheNames().contains(name)
    }

    @Override
    boolean destroyCache(String name) {
        return false
    }

    @Override
    Cache getCache(String name) {
        Cache cache = caches.get(name);
        if (cache == null) {
            final IMap<Object, Object> map = hazelcastInstance.getMap(name);
            cache = new GrailsHazelcastCache(map);
            final Cache currentCache = caches.putIfAbsent(name, cache);
            if (currentCache != null) {
                cache = currentCache;
            }
        }
        return cache;
    }

    @Override
    Collection<String> getCacheNames() {
        Set<String> cacheNames = new HashSet<String>()
        final Collection<DistributedObject> distributedObjects = hazelcastInstance.getDistributedObjects()
        for (DistributedObject distributedObject : distributedObjects) {
            if (distributedObject instanceof IMap) {
                final IMap<?, ?> map = (IMap) distributedObject
                cacheNames.add(map.getName())
            }
        }
        return cacheNames
    }
}

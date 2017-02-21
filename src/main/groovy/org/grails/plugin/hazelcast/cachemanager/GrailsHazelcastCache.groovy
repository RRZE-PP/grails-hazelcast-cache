package org.grails.plugin.hazelcast.cachemanager

import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.cache.Cache

import grails.plugin.cache.GrailsCache;

import com.hazelcast.core.IMap;
import com.hazelcast.spring.cache.HazelcastCache;

@Slf4j
@InheritConstructors
class GrailsHazelcastCache extends HazelcastCache implements GrailsCache {

	Set<Class> excludes = [MetaClass]

	@Override
	Collection<Object> getAllKeys() {
		return cache.keySet()
	}

	
	IMap getCache(){
		getNativeCache()
	}

	@Override
	<T> T get(Object key, Class<T> type) {
		return super.get(key, type)
	}

	@Override
	Cache.ValueWrapper get(Object key) {
		return super.get(key);
	}

	@Override
	Cache.ValueWrapper putIfAbsent(Object key, Object value) {
		super.putIfAbsent(key,value)

	}

	@Override
	void put(Object key, Object value) {
		if (!excludes.any {it.isInstance(value)}) {
			if (log.isTraceEnabled()) log.trace "put key: ${key?.toString()} and value ${value?.toString()}"
			super.put(key, value)
			return
		}
		log.error "cannot put key: ${key?.toString()} and value ${value?.toString()}: not serializable"

	}
}

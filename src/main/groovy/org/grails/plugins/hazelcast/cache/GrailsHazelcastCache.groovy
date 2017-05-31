package org.grails.plugins.hazelcast.cache

import com.hazelcast.core.IMap
import com.hazelcast.spring.cache.HazelcastCache
import grails.plugin.cache.GrailsCache
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j

import java.util.concurrent.Callable

@Slf4j
@InheritConstructors
@CompileStatic
class GrailsHazelcastCache extends HazelcastCache implements GrailsCache {

	Set<Class> excludes = [MetaClass] as Set<Class>

	@Override
	Collection<Object> getAllKeys() {
		return cache.keySet()
	}

	
	IMap getCache(){
		(IMap) getNativeCache()
	}

	@Override
	void evict(Object key) {
		super.evict(key)
		if (log.isTraceEnabled()) log.trace "evict key: ${key?.toString()}"
	}

	@Override
	<T> T get(Object key, Callable<T> valueLoader) {
		T value = (T) get(key)?.get()
		if (value) {
			return value
		}
		if (valueLoader){
			try {
				value = valueLoader.call()
				put(key, toStoreValue(value))
			} catch (e){
				log.warn "cannot load key from valueLoader! cause: ${e.message}"
			}
		}
		return value
	}

	@Override
	void put(Object key, Object value) {
		if (!excludes.any {it.isInstance(value)}) {
			if (log.isTraceEnabled()) log.trace "put key: ${key?.toString()} and value ${value?.toString()}"
			super.put(key, value)
			return
		}
		log.warn "cannot put key: ${key?.toString()} and value ${value?.toString()}: not serializable"

	}
}

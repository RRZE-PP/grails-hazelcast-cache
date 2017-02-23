package org.grails.plugins.hazelcast.cache

import com.hazelcast.core.IMap
import com.hazelcast.spring.cache.HazelcastCache
import grails.plugin.cache.GrailsCache
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j

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
	void put(Object key, Object value) {
		if (!excludes.any {it.isInstance(value)}) {
			if (log.isTraceEnabled()) log.trace "put key: ${key?.toString()} and value ${value?.toString()}"
			super.put(key, value)
			return
		}
		log.warn "cannot put key: ${key?.toString()} and value ${value?.toString()}: not serializable"

	}
}

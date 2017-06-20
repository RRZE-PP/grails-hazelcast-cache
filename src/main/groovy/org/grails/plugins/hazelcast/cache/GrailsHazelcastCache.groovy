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

}

package org.grails.plugins.hazelcast.cache

import com.hazelcast.core.IMap
import com.hazelcast.spring.cache.HazelcastCache
import grails.plugin.cache.GrailsCache
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.InitializingBean

import java.util.concurrent.Callable

@Slf4j
@InheritConstructors
@CompileStatic
class GrailsHazelcastCache extends HazelcastCache implements GrailsCache, InitializingBean {


	protected Class hibernateProxyClass


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

//	@Override
//	protected Object fromStoreValue(Object value) {
//		if (super.fromStoreValue(value) == null){
//			return null
//		}
//		if (isHibernateProxy(value)) {
//			return value.invokeMethod("getHibernateLazyInitializer", null)?.invokeMethod("getImplementation", null)
//		}
//		value
//
//
//	}
//
//	@Override
//	protected Object toStoreValue(Object value) {
//		if (value == null) {
//			return super.toStoreValue(value)
//		}
//		def object
//		if ((object = getHibernateProxy(value))) {
//			return object
//		}
//		return value
//	}
//
//
//
//	protected getHibernateProxy(Object value){
//		if (hibernateProxyClass){
//			if(hibernateProxyClass.isInstance(value)){
//				return value.invokeMethod("writeReplace", null)
//			}
//		}
//		null
//	}
//
//	protected boolean isHibernateProxy(Object value){
//		if (hibernateProxyClass){
//			return hibernateProxyClass.isInstance(value)
//		}
//		false
//	}


	@Override
	void afterPropertiesSet() throws Exception {

		def cl = new GroovyClassLoader()
		try {
			hibernateProxyClass = cl.loadClass("org.hibernate.proxy.HibernateProxy")
		} catch(ClassNotFoundException e){
			log.warn("no hibernate")
		}
	}
}

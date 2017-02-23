package org.grails.plugins.hazelcast.cache

import com.hazelcast.core.DistributedObject
import com.hazelcast.core.IMap
import org.springframework.context.ApplicationContext
import com.hazelcast.config.MapConfig
import com.hazelcast.core.HazelcastInstance
import grails.plugin.cache.ConfigLoader
import grails.util.Holders

class HzCacheConfigLoader extends ConfigLoader {

	HazelcastInstance hazelcastInstance

	@Override
	public void reload(List<ConfigObject> configs, ApplicationContext ctx) {
		
		
		/*
		 * RELOAD NOT SUPPORTED
		 */
		
//		log.warn "RELOADING OF DISTRIBUTED HAZELCAST CHACHES NOT SUPPORTED!"

//		def cacheManager = ctx.grailsCacheManager
//
//		for (String name in cacheManager.cacheNames) {
//			cacheManager.destroyCache name
//		}
//		
//		List cacheNames = []
//		
//		HazelcastInstance instance = Hazelcast.getHazelcastInstanceByName('coma')
//		Config config = instance.getConfig()
//		instance.getLifecycleService().terminate()
//		instance = Hazelcast.getOrCreateHazelcastInstance(config)
//		for (ConfigObject co : configs) {
//			HzCacheConfigBuilder builder = new HzCacheConfigBuilder()
//			if (co.config instanceof Closure) {
//				builder.parse co.config
//			}
//			for (MapConfig mc : builder.caches){
//				config.addMapConfig(mc)
//			}
//			cacheNames.addAll builder.cacheNames
//		}
//	
//		for (String name : cacheNames){
//			cacheManager.getCache(name)
//		}
	}
	
	
	def loadConfig(){
		def config = hazelcastInstance.getConfig()
		for (ConfigObject co : loadOrderedConfigs(Holders.grailsApplication)) {
			HzCacheConfigBuilder builder = new HzCacheConfigBuilder()
			if (co.config instanceof Closure) {
				builder.parse co.config
			}
			for (MapConfig mc : builder.caches){
				config.addMapConfig(mc)
			}
		}
		
	}
}

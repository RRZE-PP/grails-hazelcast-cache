package org.grails.plugins.hazelcast.cache

import org.springframework.context.ApplicationContext;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig
import com.hazelcast.core.HazelcastInstance
import grails.plugin.cache.ConfigLoader
import grails.plugin.cache.GrailsCacheManager;
import grails.util.Holders

class HazelcastConfigLoader extends ConfigLoader {
	

	GrailsCacheManager cacheManager
	
	@Override
	public void reload(List<ConfigObject> configs, ApplicationContext ctx) {
		
		
		/*
		 * NOT SUPPORTING RELOAD
		 */
		
//		log.warn "RELOADING OF DISTRIBUTED HAZELCAST CHACHES NOT SUPPORTED!"
		
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
//			HazelcastConfigBuilder builder = new HazelcastConfigBuilder()
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

		def bean = Holders.config.grails.hazelcast.cache.hazelcastBean
		if (bean) {
			HazelcastInstance hazelcastInstance = Holders.applicationContext.getBean(bean)
			loadConfig(hazelcastInstance.getConfig())
		}

	}
	
	
	def loadConfig(Config config){
		def configs = loadOrderedConfigs(Holders.grailsApplication)
		for (ConfigObject co : configs) {
			HazelcastConfigBuilder builder = new HazelcastConfigBuilder()
			if (co.config instanceof Closure) {
				builder.parse co.config
			}
			for (MapConfig mc : builder.caches){
				config.addMapConfig(mc)
			}
		}
		
	}
}

package org.grails.plugins.hazelcast.cache

import com.hazelcast.config.MapConfig
import com.hazelcast.core.HazelcastInstance
import grails.util.Holders

class HzCacheConfigLoader{

	HazelcastInstance hazelcastInstance

//	def loadConfig(){
//		def config = hazelcastInstance.getConfig()
//		for (ConfigObject co : loadOrderedConfigs(Holders.grailsApplication)) {
//			HzCacheConfigBuilder builder = new HzCacheConfigBuilder()
//			if (co.config instanceof Closure) {
//				builder.parse co.config
//			}
//			for (MapConfig mc : builder.caches){
//				config.addMapConfig(mc)
//			}
//		}
//
//	}

}

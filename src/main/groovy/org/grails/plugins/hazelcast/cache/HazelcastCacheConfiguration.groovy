package org.grails.plugins.hazelcast.cache

import com.hazelcast.config.EvictionPolicy
import grails.plugin.cache.CachePluginConfiguration
import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.ConfigurationProperties

@CompileStatic
@ConfigurationProperties(value = 'grails.cache')
class HazelcastCacheConfiguration extends CachePluginConfiguration{

    String hazelcastInstance = 'hazelcast'
    Map<String, Map> caches = [:]

    class HzCacheConfig extends CachePluginConfiguration.CacheConfig {
        String name
        Integer maxElementsInMemory
        EvictionPolicy evictionPolicy
        Integer maxIdleSeconds
        Integer timeToLiveSeconds

        HzCacheConfig nearCacheConfig
    }
}

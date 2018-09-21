package org.grails.plugins.hazelcast.cache

import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import grails.plugins.*
import groovy.util.logging.Slf4j

@Slf4j
class HazelcastCacheGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.2.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Grails Hazelcast Cache" // Headline display name of the plugin
    def author = "Anthony Bach"
    def authorEmail = "anthony.bach@fau.de"
    def description = '''\
Hazelcast implementation of the Grails Cache plugin
'''
    def profiles = ['web']
    def loadAfter = ['cache']
    // URL to the plugin's documentation
    def documentation = "https://github.com/RRZE-PP/grails-hazelcast-cache"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "GITHUB", url: "https://github.com/RRZE-PP/grails-hazelcast-cache/issues" ]
    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/RRZE-PP/grails-hazelcast-cache" ]
    private boolean isCachingEnabled() {
        config.getProperty('grails.cache.enabled', Boolean, true)
    }

    Closure doWithSpring() { {->
            // TODO Implement runtime spring config (optional)
        if (!cachingEnabled) {
            log.warn 'Cache plugin is disabled'
            return
        }

//        def  hzConfig = grailsApplication.config.hazelcast
//        if (hzConfig) {
//            hzConfig.each { key, value ->
//                def beanName = value.beanName?:key
//                println "Registering hazelcast bean:${beanName} ..."
//                "${beanName}"(HazelcastInstanceFactory) {
//                    config = value.config
//                    instanceName = key
//                }
//                println org.grails.plugin.hazel.HazelFactory.getInstance(value)
//                println "... hazelcast bean:${key} was successfully registered"
//            }
//        }

        def cacheConfig = grailsApplication.config.grails.cache
        if (cacheConfig.hazelcastInstance) {
            log.info "load hazelcast instance"

//            xmlns cache: 'http://www.springframework.org/schema/cache'
//
//            cache.'annotation-driven'('cache-manager': 'grailsCacheManager' , 'key-generator': 'customCacheKeyGenerator',
//                    mode: 'proxy', 'proxy-target-class': true, 'error-handler':'hazelcastCacheErrorHandler')

            HazelcastInstance instance = Hazelcast.getHazelcastInstanceByName((String) cacheConfig.hazelcastInstance)
            log.info "load hazelcast instance"
            if (instance) {

                hzCacheKeyGenerator(HzCacheKeyGenerator)

                grailsCacheConfiguration(HazelcastCacheConfiguration)


                grailsCacheManager(HazelcastGrailsCacheManager) {
                    hazelcastInstance = instance
                    configuration = ref('grailsCacheConfiguration')
                }

//                grailsCacheConfigLoader(HzCacheConfigLoader) { bean ->
//                    bean.initMethod = 'loadConfig'
//                    hazelcastInstance = instance
//                }

                log.info "Hazelcast-Cache config loaded"
            } else {
                log.warn "Hazelcast instance with name ${cacheConfig.hazelcastInstance} not found!"
            }
        }
    }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}

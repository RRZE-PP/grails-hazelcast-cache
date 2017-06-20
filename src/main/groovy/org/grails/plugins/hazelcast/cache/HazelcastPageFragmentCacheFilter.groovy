package org.grails.plugins.hazelcast.cache

import grails.core.GrailsControllerClass
import grails.plugin.cache.web.PageInfo
import grails.plugin.cache.web.filter.PageFragmentCachingFilter
import grails.util.Holders
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.cache.Cache
import org.springframework.web.context.request.RequestContextHolder

/**
 * Created by ma33fyza on 14.06.17.
 */
class HazelcastPageFragmentCacheFilter extends PageFragmentCachingFilter {


    @Override
    protected int getTimeToLive(Cache.ValueWrapper element) {
        // MapConfig handles ttl
        return 0
    }

    @Override
    protected void put(Cache cache, String key, PageInfo pageInfo, Integer timeToLive) {
        // ignoring ttl

        ((GrailsHazelcastCache) cache).put(key, pageInfo)

        log.debug "Put element into cache [${cache.getName()}] with ttl [${timeToLive}]"
    }

    @Override
    protected void initContext() {
        GrailsWebRequest requestAttributes = (GrailsWebRequest) RequestContextHolder.getRequestAttributes();
        contextHolder.get().push(new ContentCacheParameters(requestAttributes));
    }

    static class ContentCacheParameters extends grails.plugin.cache.web.ContentCacheParameters {

        ContentCacheParameters(GrailsWebRequest request) {
            super(request)
        }

        @Override
        protected void initController() {
            controllerClass = (GrailsControllerClass) Holders.findApplication().getArtefactByLogicalPropertyName("Controller", controllerName)
        }
    }
}

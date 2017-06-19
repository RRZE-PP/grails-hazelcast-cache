package org.grails.plugins.hazelcast.cache

import grails.plugin.cache.web.PageInfo
import grails.plugin.cache.web.filter.PageFragmentCachingFilter
import org.springframework.cache.Cache

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

}

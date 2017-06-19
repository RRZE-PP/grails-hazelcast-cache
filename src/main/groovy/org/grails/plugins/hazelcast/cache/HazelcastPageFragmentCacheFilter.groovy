package org.grails.plugins.hazelcast.cache

import grails.plugin.cache.GrailsValueWrapper
import grails.plugin.cache.web.AlreadyGzippedException
import grails.plugin.cache.web.Header
import grails.plugin.cache.web.PageInfo
import grails.plugin.cache.web.filter.PageFragmentCachingFilter
import groovy.util.logging.Slf4j
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.springframework.cache.Cache

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.concurrent.TimeUnit

/**
 * Created by ma33fyza on 14.06.17.
 */
@Slf4j
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

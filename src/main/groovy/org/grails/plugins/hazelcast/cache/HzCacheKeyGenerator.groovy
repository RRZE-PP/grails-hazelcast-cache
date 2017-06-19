package org.grails.plugins.hazelcast.cache

import groovy.util.logging.Slf4j
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.springframework.aop.framework.AopProxyUtils
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.cache.interceptor.SimpleKeyGenerator
import org.springframework.util.ClassUtils

import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by ma33fyza on 14.06.17.
 */
@Slf4j
class HzCacheKeyGenerator implements KeyGenerator {

    private final ConcurrentHashMap hashCodeMap = new ConcurrentHashMap()

    boolean allowLocalCache

    @Override
    Object generate(Object target, Method method, Object... params) {
        Class objClass = AopProxyUtils.ultimateTargetClass(target)
        int targetObjectHashCode

        def hasHashCodeOverridden = hashCodeMap.get(objClass)
        if (hasHashCodeOverridden ==null){
            def hashCodeMethod = ClassUtils.getMethod(Object, 'hashCode')
            if(ClassUtils.getMostSpecificMethod(hashCodeMethod, objClass.declaringClass == Object)) {
                log.warn "${objClass} or its super classes does not override the hashCode() method! " +
                           "Please make sure the class has a proper hashCode() implementation " +
                           "otherwise caching may not work properly!"
                hasHashCodeOverridden = false
            }else {
                hasHashCodeOverridden = true
            }
            hashCodeMap.put(objClass, hasHashCodeOverridden)
        }

        if(hasHashCodeOverridden || allowLocalCache) {
            targetObjectHashCode = target.hashCode()
        } else {
            //TODO
            targetObjectHashCode = objClass.toString().intern().hashCode()*17// HashCodeBuilder.reflectionHashCode(target)
        }

        new HzCacheKey(objClass.getName().intern(),
                method.toString().intern(),targetObjectHashCode, SimpleKeyGenerator.generateKey(params))
    }


}

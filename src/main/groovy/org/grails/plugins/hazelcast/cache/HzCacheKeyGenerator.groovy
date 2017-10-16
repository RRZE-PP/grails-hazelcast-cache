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

//        def hasHashCodeOverridden = hashCodeMap.get(objClass)
//        if (hasHashCodeOverridden ==null){
//            def hashCodeMethod = ClassUtils.getMethod(Object, 'hashCode')
//            if(ClassUtils.getMostSpecificMethod(hashCodeMethod, objClass) == Object) {
//                log.warn "${objClass} or its super classes does not override the hashCode() method! " +
//                           "Please make sure the class has a proper hashCode() implementation " +
//                           "otherwise caching may not work properly!"
//                hasHashCodeOverridden = false
//            }else {
//                hasHashCodeOverridden = true
//            }
//            hashCodeMap.put(objClass, hasHashCodeOverridden)
//        }

        if(implementsHashCode(objClass) || allowLocalCache) {
            targetObjectHashCode = target.hashCode()
        } else {
            //TODO
            targetObjectHashCode = objClass.toString().intern().hashCode()*17// HashCodeBuilder.reflectionHashCode(target)
        }

        // checks all params for proper hashCode implementation
        params?.each {
            implementsHashCode(it.getClass())
        }

        new HzCacheKey(objClass.getName().intern(),
                method.toString().intern(),targetObjectHashCode, SimpleKeyGenerator.generateKey(params))
    }


    protected boolean implementsHashCode(Class aClass){
        def isImplemented = hashCodeMap.get(aClass)
        if (isImplemented == null) {
            def hashCodeMethod = ClassUtils.getMethod(Object, 'hashCode')
            if (ClassUtils.getMostSpecificMethod(hashCodeMethod, aClass) == Object) {
                log.warn "${aClass} or its super classes does not override the hashCode() method! " +
                        "Please make sure the class has a proper hashCode() implementation " +
                        "otherwise caching may not work properly!"
                hashCodeMap.put(aClass, false)
            } else {
                hashCodeMap.put(aClass, false)
            }
        }
        isImplemented
    }

}

package org.grails.plugins.hazelcast.cache;

import grails.plugin.cache.GrailsAnnotationCacheOperationSource;
import groovy.lang.GroovyObject;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by ma33fyza on 18.06.17.
 */
public class GroovyAnnotationCacheOperationSource extends GrailsAnnotationCacheOperationSource{

    @Override
    public Collection<CacheOperation> getCacheOperations(Method method, Class<?> targetClass) {
        if (ClassUtils.isUserLevelMethod(method)) {
            return super.getCacheOperations(method, targetClass);
        }
        return null;
    }
}

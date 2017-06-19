package org.grails.plugins.hazelcast.cache;

import grails.core.GrailsApplication;
import grails.plugin.cache.GrailsAnnotationCacheOperationSource;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * Created by ma33fyza on 18.06.17.
 */
public class CacheBeanPostProcessor extends grails.plugin.cache.CacheBeanPostProcessor {


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        log.info("postProcessBeanDefinitionRegistry start");

        AbstractBeanDefinition beanDef = findBeanDefinition(registry);
        if (beanDef == null) {
            log.error("Unable to find the AnnotationCacheOperationSource bean definition");
            return;
        }

        // change the class to the plugin's subclass
        beanDef.setBeanClass(GroovyAnnotationCacheOperationSource.class);

        // wire in the dependency for the grailsApplication
        MutablePropertyValues props = beanDef.getPropertyValues();
        if (props == null) {
            props = new MutablePropertyValues();
            beanDef.setPropertyValues(props);
        }
        props.addPropertyValue(GrailsApplication.APPLICATION_ID, new RuntimeBeanReference(GrailsApplication.APPLICATION_ID));

        log.debug("updated {}", beanDef);
    }
}

package org.grails.plugins.hazelcast.cache

import com.hazelcast.config.Config
import com.hazelcast.config.XmlConfigBuilder
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import grails.util.Holders
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.config.AbstractFactoryBean
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.util.Assert
import org.springframework.util.ClassUtils

/**
 * Created by anthony on 23.02.2017.
 */
@Slf4j
class HazelcastInstanceFactory extends AbstractFactoryBean<HazelcastInstance> {


    ConfigObject config

    String instanceName

    String xmlFileName

    ClassLoader classLoader = ClassUtils.getDefaultClassLoader()

    @Override
    Class<?> getObjectType() {
        return HazelcastInstance
    }

    @Override
    void afterPropertiesSet() throws Exception {
//        Assert.hasText(this.instanceName, "Property 'instanceName' is required")
        Thread.currentThread().setContextClassLoader(Hazelcast.class.getClassLoader());
        super.afterPropertiesSet()
    }

    @Override
    protected HazelcastInstance createInstance() throws Exception {

        Config config

        if (config && config instanceof Closure){
            HzConfigBuilder configBuilder = new HzConfigBuilder(instanceName)
            configBuilder.parse config
            config = configBuilder.current
        } else {
            if (xmlFileName){
                config = new XmlConfigBuilder(xmlFileName).build()
            } else {
                config = new Config(instanceName)
                //TODO default config
            }
        }

        config.setClassLoader(classLoader)


        return Hazelcast.getOrCreateHazelcastInstance(config)
    }


    ConfigObject getConfig(){
        config?:instanceName?Holders.config.hazelcast?.beans?."$instanceName"?.config?:null:null
    }

    @Override
    boolean isSingleton() {
        return true
    }

    @Override
    protected void destroyInstance(HazelcastInstance instance) throws Exception {
//        Hazelcast.getHazelcastInstanceByName(instanceName).shutdown()
    }
}

package org.grails.plugins.hazelcast.cache

import com.hazelcast.config.*
import org.apache.commons.collections.Closure
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by anthony on 23.02.2017.
 */
class HzConfigBuilder extends BuilderSupport{

    private static final Logger log = LoggerFactory.getLogger(HzCacheConfigBuilder)


    protected static final List CONFIG_PROPERTIES = [/*'instanceName',*/ 'classLoader']
    protected static final List GROUP_PROPERTIES = ['name', 'password']
    protected static final List NETWORK_PROPERTIES = ['interfaces', 'publicAddress', 'port', 'portCount', 'portAutoIncrement', 'reuseAddress', 'outboundPortDefs',
                                                        'outboundPorts']
    protected static final List MULTICAST_PROPERTIES = ['enabled', 'multicastGroup', 'multicastPort', 'multicastTimeoutSeconds', 'trustedInterfaces', 'multicastTimeToLive', 'loopbackModeEnabled']

    protected int unrecognizedElementDepth = 0
    protected Config current
    protected List stack = []

//    private String instanceName

    HzConfigBuilder(String instanceName){
//        this.instanceName = instanceName
        current =new Config(instanceName)
    }


    /**
     * Convenience method to parse a config closure.
     * @param c the closure
     */
    void parse(Closure c) {
        c.delegate = this
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c()

    }



    @Override
    protected Object createNode(Object name) {
        if (unrecognizedElementDepth) {
            unrecognizedElementDepth++
            log.warn "ignoring node $name contained in unrecognized parent node"
            return
        }

        switch (name) {
//            case 'config':
//                current = new Config(instanceName)
//                stack.push(name)
//                return
            case 'instanceName':
                stack.push(name)
                return
            case 'classLoader':
                stack.push(name)
                return
            case 'groupConfig':
                current.groupConfig = new GroupConfig()
                stack.push(name)
                return
            case 'networkConfig':
                current.networkConfig = new NetworkConfig()
                stack.push(name)
                return
            case 'joinConfig':
                current.networkConfig.join = new JoinConfig()
                stack.push(name)
                return
            case 'multicastConfig':
                current.networkConfig.join.multicastConfig = new MulticastConfig()
                stack.push(name)
                return
            case 'properties':
                stack.push(name)
                return
            case 'mapConfigs':
                stack.push(name)
                return
        }


        unrecognizedElementDepth++
        log.warn "Cannot create empty node with name '$name'"
    }

    @Override
    protected Object createNode(Object name, Object value) {
        if (unrecognizedElementDepth) {
            unrecognizedElementDepth++
            log.warn "ignoring node $name with value $value contained in unrecognized parent node"
            return
        }

        log.info "createNode $name, value: $value"
        String level = stack[-1]
        stack.push name

        switch (level) {
            case 'instanceName':
                current.instanceName = value
                break
            case 'classLoader':
                current.classLoader = value
                break
            case 'config':
                if (CONFIG_PROPERTIES.contains(name)){
                    current."$name" = value
                    return name
                }
                break
            case 'groupConfig':
                if (GROUP_PROPERTIES.contains(name)){
                    current.groupConfig."$name" = value
                    return name
                }
                break;
            case 'networkConfig':
                if (NETWORK_PROPERTIES.contains(name)){
                    current.networkConfig."$name" = value
                    return name
                }
                break;
            case 'joinConfig':
                break;
            case 'multicastConfig':
                if (MULTICAST_PROPERTIES.contains(name)){
                    current.networkConfig.join."$name" = value
                    return name
                }
                break;
            case 'properties':
                current.setProperty(name, value)
                return name
                break;
            case 'mapConfigs':
                if (value instanceof Closure){
                    HzCacheConfigBuilder b = new HzCacheConfigBuilder()
                    b.parse value
                    current.mapConfigs = b.caches
                    return name
                }
                break;

        }
        unrecognizedElementDepth++
        log.warn "Cannot create node with name '$name' and value '$value' for parent '$level'"

    }

    @Override
    protected createNode(name, Map attributes) {
        if (unrecognizedElementDepth) {
            unrecognizedElementDepth++
            log.warn "ignoring node $name with attributes $attributes contained in unrecognized parent node"
            return
        }

        log.trace "createNode $name + attributes: $attributes"
    }

    @Override
    protected createNode(name, Map attributes, value) {
        if (unrecognizedElementDepth) {
            unrecognizedElementDepth++
            log.warn "ignoring node $name with value $value and attributes $attributes contained in unrecognized parent node"
            return
        }

        log.trace "createNode $name + value: $value attributes: $attributes"
    }

    @Override
    protected void setParent(parent, child) {
        log.trace "setParent $parent, child: $child"
        // do nothing
    }

    @Override
    protected void nodeCompleted(parent, node) {
        log.trace "nodeCompleted $parent $node"

        if (unrecognizedElementDepth) {
            unrecognizedElementDepth--
        }
        else {
            stack.pop()
        }
    }




}

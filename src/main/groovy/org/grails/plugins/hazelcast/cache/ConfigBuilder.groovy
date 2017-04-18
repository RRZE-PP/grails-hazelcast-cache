package org.grails.plugins.hazelcast.cache

import groovy.util.logging.Slf4j

/**
 * Created by anthony on 04.04.2017.
 */
@Slf4j
class ConfigBuilder extends BuilderSupport{

    protected int unrecognizedElementDepth = 0
    protected List stack = []
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

        if (isNode(name)){
            stack.push(name)
            return
        }

        unrecognizedElementDepth++
        log.warn "Cannot create empty node with name '$name'"
    }

    protected boolean isNode(String name){
        false
    }

    @Override
    protected Object createNode(Object name, Object value) {
        if (unrecognizedElementDepth) {
            unrecognizedElementDepth++
            log.warn "ignoring node $name with value $value contained in unrecognized parent node"
            return
        }

        log.debug "createNode $name, value: $value"
        String level = stack[-1]
        stack.push name

        if (buildNode(name, value)){
            return name
        }

        unrecognizedElementDepth++
        log.warn "Cannot create node with name '$name' and value '$value' for parent '$level'"

    }

    protected buildNode(String name, value){

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
        log.trace "nodeCompleted $node"

        if (unrecognizedElementDepth) {
            unrecognizedElementDepth--
        }
        else {
            stack.pop()
        }
    }
}

package org.grails.plugins.hazelcast.cache;

import groovy.transform.ToString;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by ma33fyza on 17.06.17.
 */
public class HzCacheKey implements Serializable{

    private static final long serialVersionUID = 6111018785563168739L;

    private final String targetClassName;
    private final String targetMethodName;
    private final int targetObjectHashCode;
    private final Object simpleKey;


    public HzCacheKey(String targetClassName, String targetMethodName,
                       int targetObjectHashCode, Object simpleKey) {
        this.targetClassName = targetClassName;
        this.targetMethodName = targetMethodName;
        this.targetObjectHashCode = targetObjectHashCode;
        this.simpleKey = simpleKey;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 17)
                .append(targetClassName)
                .append(targetMethodName)
                .append(targetObjectHashCode)
                .append(simpleKey)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==this){
            return true;
        } else if (obj == null){
            return false;
        } else if (obj.hashCode() == this.hashCode()){
            if(obj instanceof HzCacheKey){
                HzCacheKey o = (HzCacheKey) obj;
                return new EqualsBuilder()
                        .append(targetClassName, o.targetClassName)
                        .append(targetMethodName, o.targetMethodName)
                        .append(targetObjectHashCode, o.targetObjectHashCode)
                        .append(simpleKey,o.simpleKey)
                        .isEquals();
            }
        }

        return false;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(HzCacheKey.class.getSimpleName());
        sb.append(" for: ");
        sb.append(targetMethodName);

        return sb.toString();
    }
}

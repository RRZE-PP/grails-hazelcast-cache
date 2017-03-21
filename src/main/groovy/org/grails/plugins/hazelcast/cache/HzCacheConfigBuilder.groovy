package org.grails.plugins.hazelcast.cache

import com.hazelcast.config.MapStoreConfig
import com.hazelcast.config.PartitioningStrategyConfig
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.MapConfig
import com.hazelcast.config.NearCacheConfig
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MaxSizeConfig.MaxSizePolicy

class HzCacheConfigBuilder extends BuilderSupport {

	private static final Logger log = LoggerFactory.getLogger(HzCacheConfigBuilder)
	
	
	protected static final List CACHE_PROPERTIES = ['name', 'maxIdleSeconds', 'timeToLiveSeconds', 'minEvictionCheckMillis', 'asyncBackupCount',
		'backupCount', 'mergePolicy', 'statisticsEnabled', 'readBackupData', 'optimizeQueries','evictionPolicy', 'quorumName', 'cacheDeserializedValues']
	protected static final List MAX_SIZE_PROPERTIES = ['size', 'maxSizePolicy']
	protected static final List NEAR_CACHE_PROPERTIES = ['name', 'maxIdleSeconds', 'timeToLiveSeconds', 'maxSize', 'evictionPolicy', 'evictionConfig', 'inMemoryFormat',
		'localUpdatePolicy', 'cacheLocalEntries', 'invalidateOnChange']
	protected static final List MAP_STORE_PROPERTIES = ['className', 'factoryClassName', 'writeDelaySeconds', 'writeBatchSize', 'enabled', 'initialLoadMode', 'writeCoalescing',
														 'implementation', 'factoryImplementation']
//	protected static final List HOT_RESTART_PROPERTIES = ['fsync', 'enabled']
	protected static final List PARTITIONING_STRATEGY_PROPERTIES = ['partitionStrategyClass', 'partitionStrategy']

	protected int unrecognizedElementDepth = 0
	protected MapConfig current
	protected List stack = []
	protected List caches = []
	List<String> cacheNames = []

	/**
	 * Convenience method to parse a config closure.
	 * @param c the closure
	 */
	void parse(Closure c) {
		c.delegate = this
		c.resolveStrategy = Closure.DELEGATE_FIRST
		c()

//		resolveCaches()
	}
	
	
		
	@Override
	protected Object createNode(Object name) {
		if (unrecognizedElementDepth) {
			unrecognizedElementDepth++
			log.warn "ignoring node $name contained in unrecognized parent node"
			return
		}
		switch (name) {
			case 'cache':
			case 'domain':
				current = new MapConfig()//[:]
				caches << current
				stack.push(name)
				return
			case 'maxSizeConfig':
				current.maxSizeConfig = new MaxSizeConfig()
				stack.push(name)
				return
			case 'nearCacheConfig':
				current.nearCacheConfig = new NearCacheConfig()
				stack.push(name)
				return
			case 'mapStoreConfig':
				current.mapStoreConfig = new MapStoreConfig()
				stack.push(name)
				return
//			case 'hotRestartConfig':
//				current.hotRestartConfig = new HotRestartConfig()
//				stack.push(name)
//				return
			case 'partitioningStrategyConfig':
				current.partitioningStrategyConfig = new PartitioningStrategyConfig()
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
		
		log.debug "createNode $name, value: $value"
		String level = stack[-1]
		stack.push name

		switch (level) {
			case 'domain':
			case 'cache':
				if (('name' == name || 'cache' == name || 'domain' == name) && value instanceof Class) {
					current['name'] = value.name
					return "name"
				} else if (name.equals("maxElementsInMemory")){
					def msc = new MaxSizeConfig()
					msc.setMaxSizePolicy(MaxSizePolicy.PER_NODE)
					msc.setSize(value)
					current.setMaxSizeConfig(msc)
					return "name"
				} else if (CACHE_PROPERTIES.contains(name)){
					current."$name" = value
					return name
				}
				break
			case 'maxSizeConfig':
				if (MAX_SIZE_PROPERTIES.contains(name)){
					current.maxSizeConfig."$name" = value
					return name
				}
				break;
			case 'nearCacheConfig':
				if (NEAR_CACHE_PROPERTIES.contains(name)){
					current.nearCacheConfig."$name" = value
					return name
				}
				break;
			case 'mapStoreConfig':
				if (MAP_STORE_PROPERTIES.contains(name)){
					current.mapStoreConfig."$name" = value
					return name
				}
				break;
//			case 'hotRestartConfig':
//				if (HOT_RESTART_PROPERTIES.contains(name)){
//					current.hotRestartConfig."$name" = value
//					return name
//				}
//				break;
			case 'partitioningStrategyConfig':
				if (PARTITIONING_STRATEGY_PROPERTIES.contains(name)){
					current.partitioningStrategyConfig."$name" = value
					return name
				}
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
		log.trace "nodeCompleted $node"

		if (unrecognizedElementDepth) {
			unrecognizedElementDepth--
		}
		else {
			stack.pop()
		}
	}
	
	def getCacheNames(){
		caches*.name
	}
}

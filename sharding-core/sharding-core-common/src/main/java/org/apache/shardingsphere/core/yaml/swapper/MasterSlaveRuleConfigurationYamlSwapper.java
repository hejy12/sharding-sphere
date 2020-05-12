/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.core.yaml.swapper;

import com.google.common.base.Strings;
import org.apache.shardingsphere.api.config.masterslave.LoadBalanceStrategyConfiguration;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveGroupConfiguration;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.core.yaml.config.masterslave.YamlMasterSlaveGroupConfiguration;
import org.apache.shardingsphere.core.yaml.config.masterslave.YamlMasterSlaveRuleConfiguration;
import org.apache.shardingsphere.underlying.common.yaml.swapper.YamlSwapper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Master-slave rule configuration YAML swapper.
 */
public final class MasterSlaveRuleConfigurationYamlSwapper implements YamlSwapper<YamlMasterSlaveRuleConfiguration, MasterSlaveRuleConfiguration> {
    
    @Override
    public YamlMasterSlaveRuleConfiguration swap(final MasterSlaveRuleConfiguration data) {
        YamlMasterSlaveRuleConfiguration result = new YamlMasterSlaveRuleConfiguration();
        result.setDataSources(data.getDataSources().stream().collect(Collectors.toMap(MasterSlaveGroupConfiguration::getName, this::swap, (a, b) -> b, LinkedHashMap::new)));
        return result;
    }
    
    private YamlMasterSlaveGroupConfiguration swap(final MasterSlaveGroupConfiguration group) {
        YamlMasterSlaveGroupConfiguration result = new YamlMasterSlaveGroupConfiguration();
        result.setName(group.getName());
        result.setMasterDataSourceName(group.getMasterDataSourceName());
        result.setSlaveDataSourceNames(group.getSlaveDataSourceNames());
        if (null != group.getLoadBalanceStrategyConfiguration()) {
            result.setLoadBalanceAlgorithmType(group.getLoadBalanceStrategyConfiguration().getType());
        }
        return result;
    }
    
    @Override
    public MasterSlaveRuleConfiguration swap(final YamlMasterSlaveRuleConfiguration yamlConfiguration) {
        Collection<MasterSlaveGroupConfiguration> groups = new LinkedList<>();
        for (Entry<String, YamlMasterSlaveGroupConfiguration> entry : yamlConfiguration.getDataSources().entrySet()) {
            groups.add(swap(entry.getKey(), entry.getValue()));
        }
        return new MasterSlaveRuleConfiguration(groups);
    }
    
    private MasterSlaveGroupConfiguration swap(final String name, final YamlMasterSlaveGroupConfiguration yamlGroup) {
        return new MasterSlaveGroupConfiguration(name, yamlGroup.getMasterDataSourceName(), yamlGroup.getSlaveDataSourceNames(), getLoadBalanceStrategyConfiguration(yamlGroup));
    }
    
    private LoadBalanceStrategyConfiguration getLoadBalanceStrategyConfiguration(final YamlMasterSlaveGroupConfiguration yamlGroup) {
        return Strings.isNullOrEmpty(yamlGroup.getLoadBalanceAlgorithmType()) ? null : new LoadBalanceStrategyConfiguration(yamlGroup.getLoadBalanceAlgorithmType(), yamlGroup.getProps());
    }
}

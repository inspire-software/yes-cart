/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.cluster.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.yes.cart.cluster.service.ModuleDirector;
import org.yes.cart.config.ActiveConfiguration;
import org.yes.cart.config.ActiveConfigurationDetector;
import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.domain.dto.impl.ConfigurationDTO;
import org.yes.cart.domain.dto.impl.ModuleDTO;
import org.yes.cart.env.Module;

import java.util.*;

/**
 * User: denispavlov
 * Date: 21/03/2018
 * Time: 22:30
 */
public class ModuleDirectorImpl implements ModuleDirector, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ModuleDirectorImpl.class);

    private ApplicationContext applicationContext;

    @Override
    public List<ModuleDTO> getModules() {

        final List<ModuleDTO> dtos = new ArrayList<>();
        final List<Module> modules = beansOfTypeIncludingAncestors(this.applicationContext, Module.class);
        if (CollectionUtils.isNotEmpty(modules)) {
            for (final Module module : modules) {
                dtos.add(
                        new ModuleDTO(
                                module.getFunctionalArea(),
                                module.getName(),
                                module.getSubName(),
                                module.getLoaded()
                        )
                );
            }
        }
        return dtos;
    }

    @Override
    public List<ConfigurationDTO> getConfigurations() {
        final List<ConfigurationDTO> dtos = new ArrayList<>();

        final List<Configuration> available = beansOfTypeIncludingAncestors(this.applicationContext, Configuration.class);
        final Map<String, ConfigurationContext> availableCtx = new TreeMap<>();
        if (CollectionUtils.isNotEmpty(available)) {
            for (final Configuration configuration : available) {
                final ConfigurationContext configurationCtx = configuration.getCfgContext();
                availableCtx.put(configurationCtx.getName(), configurationCtx);
            }
        }

        final List<ActiveConfigurationDetector> active = beansOfTypeIncludingAncestors(this.applicationContext, ActiveConfigurationDetector.class);
        final Map<String, List<String>> activeCfg = new HashMap<>();
        if (CollectionUtils.isNotEmpty(active)) {
            for (final ActiveConfigurationDetector detector : active) {
                for (final ActiveConfiguration activeConfiguration : detector.getActive()) {
                    final List<String> activeForName = activeCfg.computeIfAbsent(activeConfiguration.getName(), k -> new ArrayList<>());
                    activeForName.add(activeConfiguration.getTarget());
                }
            }
        }

        for (final ConfigurationContext av : availableCtx.values()) {

            final List<String> targets = activeCfg.get(av.getName());

            dtos.add(
                    new ConfigurationDTO(
                            av.getFunctionalArea(),
                            av.getName(),
                            av.getCfgInterface(),
                            av.isCfgDefault(),
                            new TreeMap<>(av.getProperties()),
                            targets
                    )
            );

        }

        return dtos;
    }

    private <T> List<T> beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> clazz) {

        final List<T> result = new LinkedList<>();
        result.addAll(lbf.getBeansOfType(clazz).values());
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                List<T> parentResult = beansOfTypeIncludingAncestors(
                        (ListableBeanFactory) hbf.getParentBeanFactory(), clazz);
                result.addAll(parentResult);
            }
        }
        return result;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {

        if (LOG.isInfoEnabled()) {

            final List<ModuleDTO> modules = getModules();

            LOG.info("== Module statistics ===========================================");
            LOG.info("");

            final Set<ModuleDTO> sortedModules = new TreeSet<>((a, b) -> a.getLoaded().compareTo(b.getLoaded()));
            sortedModules.addAll(modules);

            for (final ModuleDTO item : sortedModules) {
                LOG.info("[{}] {}:{} @ {}", item.getFunctionalArea(), item.getName(), item.getSubName(), item.getLoaded());
            }

            LOG.info("");
            LOG.info("================================================================");
            LOG.info("");

            final List<ConfigurationDTO> config = getConfigurations();

            LOG.info("== Configurations ==============================================");
            LOG.info("");

            final Set<ConfigurationDTO> sortedConfig = new TreeSet<>((a, b) -> {
                int res = a.getFunctionalArea().compareTo(b.getFunctionalArea());
                if (res == 0) {
                    res = a.getCfgInterface().compareTo(b.getCfgInterface());
                    if (res == 0) {
                        return a.getName().compareToIgnoreCase(b.getName());
                    }
                }
                return res;
            });
            sortedConfig.addAll(config);

            for (final ConfigurationDTO item : sortedConfig) {
                LOG.info("[{}] {}:{}{} {}",
                        item.getFunctionalArea(), item.getCfgInterface(), item.getName(),
                        item.isCfgDefault() ? " [default]" : "", item.getTargets() != null ? item.getTargets() : "");
            }

            LOG.info("");
            LOG.info("================================================================");
            LOG.info("");

        }

    }
}

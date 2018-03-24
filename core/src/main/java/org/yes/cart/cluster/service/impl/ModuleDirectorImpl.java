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
        final List<Module> modules = beansOfTypeIncludingAncestors(this.applicationContext);
        if (CollectionUtils.isNotEmpty(modules)) {
            for (final Module module : modules) {
                dtos.add(new ModuleDTO(module.getFunctionalArea(), module.getName(), module.getSubName(), module.getLoaded()));
            }
        }
        return dtos;
    }

    private List<Module> beansOfTypeIncludingAncestors(ListableBeanFactory lbf) {

        final List<Module> result = new LinkedList<>();
        result.addAll(lbf.getBeansOfType(Module.class).values());
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                List<Module> parentResult = beansOfTypeIncludingAncestors(
                        (ListableBeanFactory) hbf.getParentBeanFactory());
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

            final List<Module> modules = beansOfTypeIncludingAncestors(this.applicationContext);

            LOG.info("== Module statistics ===========================================");
            LOG.info("");

            final Set<Module> sortedMax = new TreeSet<>((a, b) -> a.getLoaded().compareTo(b.getLoaded()));
            sortedMax.addAll(modules);

            for (final Module item : sortedMax) {
                LOG.info("[{}] {}:{} @ {}", item.getFunctionalArea(), item.getName(), item.getSubName(), item.getLoaded());
            }

            LOG.info("");
            LOG.info("================================================================");
            LOG.info("");

        }

    }
}

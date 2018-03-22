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

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.cluster.service.ModuleDirector;
import org.yes.cart.domain.dto.impl.ModuleDTO;
import org.yes.cart.env.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 21/03/2018
 * Time: 22:30
 */
public class ModuleDirectorImpl implements ModuleDirector, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public List<ModuleDTO> getModules() {

        final List<ModuleDTO> dtos = new ArrayList<>();
        final Map<String, Module> modules = BeanFactoryUtils.beansOfTypeIncludingAncestors(this.applicationContext, Module.class);
        if (MapUtils.isNotEmpty(modules)) {
            for (final Module module : modules.values()) {
                dtos.add(new ModuleDTO(module.getFunctionalArea(), module.getName(), module.getSubName(), module.getLoaded()));
            }
        }
        return dtos;
    }


    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}

/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.cluster.service;

import org.yes.cart.domain.dto.impl.ConfigurationDTO;
import org.yes.cart.domain.dto.impl.ModuleDTO;

import java.util.List;

/**
 * User: denispavlov
 * Date: 21/03/2018
 * Time: 22:26
 */
public interface ModuleDirector {

    /**
     * Get currently loaded modules.
     *
     * @return modules
     */
    List<ModuleDTO> getModules();

    /**
     * Get currently available/active configurations.
     *
     * @return configurations
     */
    List<ConfigurationDTO> getConfigurations();

}

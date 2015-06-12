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

package org.yes.cart.web.support.service;

import org.yes.cart.domain.misc.Pair;

import java.util.Map;

/**
 * Service responsible to resolve label of central view in storefront.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/26/11
 * Time: 9:35 PM
 */
public interface CentralViewResolver {

    /**
     * Resolve central renderer label pair. First key is for specific template
     * that is resolved, second label is the default failover (to be used for
     * themes that do not support customised template)
     *
     * @param parameters            request parameters map
     * @return resolved main panel renderer label if resolved, otherwise null
     */
    Pair<String, String> resolveMainPanelRendererLabel(Map parameters);

}

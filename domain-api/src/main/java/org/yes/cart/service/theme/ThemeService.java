/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.service.theme;

import java.util.List;

/**
 * User: denispavlov
 * Date: 02/09/2014
 * Time: 18:35
 */
public interface ThemeService {

    /**
     * Get current shop theme chain.
     *
     * @param shopId shop PK
     *
     * @return chain of themes names
     */
    List<String> getThemeChainByShopId(Long shopId);

    /**
     * Get current shop markup chain.
     *
     * @param shopId shop PK
     *
     * @return chain of themes names
     */
    List<String> getMarkupChainByShopId(Long shopId);

    /**
     * Get current shop markup chain.
     *
     * @param shopId shop PK
     *
     * @return chain of themes names
     */
    List<String> getMailTemplateChainByShopId(Long shopId);

}

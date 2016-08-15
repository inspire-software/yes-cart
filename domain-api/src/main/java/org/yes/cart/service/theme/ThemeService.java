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
     * @param domain theme chain
     *
     * @return chain of themes names
     */
    List<String> getThemeChainByShopId(Long shopId, String domain);

    /**
     * Get current shop markup chain.
     *
     * @param shopId shop PK
     * @param domain theme chain
     *
     * @return chain of themes names
     */
    List<String> getMarkupChainByShopId(Long shopId, String domain);

    /**
     * Get current shop markup chain.
     *
     * Note that mail templates do not use current domain as in most cases email are generated in
     * batch job and have no current domain.
     *
     * @param shopId shop PK
     *
     * @return chain of themes names
     */
    List<String> getMailTemplateChainByShopId(Long shopId);

    /**
     * Get current shop reports templates chain.
     *
     * Note that report templates do not use current domain as in most cases reports are generated in
     * Admin or on demand and have no current domain.
     *
     * @param shopId shop PK
     *
     * @return chain of themes names
     */
    List<String> getReportsTemplateChainByShopId(Long shopId);

}

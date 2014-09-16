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

package org.yes.cart.service.federation;

import org.yes.cart.domain.dto.ShopDTO;

import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:31
 */
public interface ShopFederationStrategy {

    /**
     * @return master access to all features
     */
    boolean isCurrentUserSystemAdmin();

    /**
     * @param shopCode shop code
     *
     * @return true if current manager has access to this shop
     */
    boolean isShopAccessibleByCurrentManager(final String shopCode);

    /**
     * @return set of PK's of shops to which current manager has access
     */
    Set<Long> getAccessibleShopIdsByCurrentManager();

    /**
     * @return set of shops to which current manager has access
     */
    List<ShopDTO> getAccessibleShopsByCurrentManager();

}

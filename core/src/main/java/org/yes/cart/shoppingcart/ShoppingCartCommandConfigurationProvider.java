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

package org.yes.cart.shoppingcart;

import java.util.Set;

/**
 * A relay to the actual configuration visitors that modify shopping cart in a
 * certain way.
 *
 * User: denispavlov
 * Date: 24/01/2017
 * Time: 07:27
 */
public interface ShoppingCartCommandConfigurationProvider<T extends ShoppingCart> {

    /**
     * Provides a requested visitor that can mutate the cart in a specific way.
     *
     * @param visitorId visitor id
     *
     * @return visitor if one exists for such id
     */
    ShoppingCartCommandConfigurationVisitor<T> provide(String visitorId);

    /**
     * Get all ID's of visitors registered with this provider.
     *
     * @return visitors ID's
     */
    Set<String> getVisitorIds();

}

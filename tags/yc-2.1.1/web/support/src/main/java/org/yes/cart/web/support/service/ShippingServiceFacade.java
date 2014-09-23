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

package org.yes.cart.web.support.service;

import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.List;

/**
 * User: denispavlov
 * Date: 11/01/2014
 * Time: 11:55
 */
public interface ShippingServiceFacade {

    /**
     * Find all applicable carriers for given shopping cart. This method could be potentially cached
     * however the logic for selecting carriers may be delicate and depending on other factors, so need
     * to be careful - hence left uncached OOTB.
     *
     * @param shoppingCart current cart
     *
     * @return applicable carriers
     */
    List<Carrier> findCarriers(ShoppingCart shoppingCart);

    /**
     * Get SLA from list of carrier choices.
     *
     * @param shoppingCart current cart
     * @param carriersChoices choices given to this cart (e.g. from #findCarriers method)
     *
     * @return selected Carrier and SLA pair or pair with null's if none selected
     */
    Pair<Carrier, CarrierSla> getCarrierSla(ShoppingCart shoppingCart, List<Carrier> carriersChoices);

}

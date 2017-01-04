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

import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.ProductPriceModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 11/01/2014
 * Time: 11:55
 */
public interface ShippingServiceFacade {

    /**
     * Check if adddresses are required given current SLA selection.
     *
     * @param carrierSlaIds SLA ids
     *
     * @return billing (first) and shipping (second) not-required flags
     */
    Pair<Boolean, Boolean> isAddressNotRequired(Collection<Long> carrierSlaIds);

    /**
     * Determine if address is skippable. We can skip over the address step
     * straight to the carrier selection if at least one applicable SLA does not require
     * billing or shipping address.
     *
     * @param shoppingCart current cart
     *
     * @return true if we can skip address
     */
    boolean isSkippableAddress(ShoppingCart shoppingCart);

    /**
     * Find all applicable carriers for given shopping cart. This method could be potentially cached
     * however the logic for selecting carriers may be delicate and depending on other factors, so need
     * to be careful - hence left uncached OOTB.
     *
     * @param shoppingCart current cart
     * @param supplier supplier
     *
     * @return applicable carriers
     */
    List<Carrier> findCarriers(ShoppingCart shoppingCart, String supplier);

    /**
     * Get SLA from list of carrier choices.
     *
     * @param shoppingCart current cart
     * @param supplier supplier
     * @param carriersChoices choices given to this cart (e.g. from #findCarriers method)
     *
     * @return selected Carrier and SLA pair or pair with null's if none selected
     */
    Pair<Carrier, CarrierSla> getCarrierSla(ShoppingCart shoppingCart, String supplier, List<Carrier> carriersChoices);

    /**
     * Get cart total price model (or blank object) with respect to current shop tax display settings.
     *
     * If tax info is enabled then prices can be shown as net or gross.
     *
     * @param cart      current cart
     *
     * @return price (or blank object)
     */
    ProductPriceModel getCartShippingTotal(ShoppingCart cart);

    /**
     * Get cart total price model (or blank object) with respect to current shop tax display settings.
     *
     * If tax info is enabled then prices can be shown as net or gross.
     *
     * @param cart      current cart
     *
     * @return price (or blank object)
     */
    ProductPriceModel getCartShippingSupplierTotal(ShoppingCart cart, String supplier);

    /**
     * Get cart items suppliers.
     *
     * @param cart cart with items
     *
     * @return suppliers for cart items in the cart
     */
    Map<String, String> getCartItemsSuppliers(ShoppingCart cart);

}

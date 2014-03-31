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

package org.yes.cart.web.support.service.impl;

import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CarrierService;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.support.service.ShippingServiceFacade;

import java.util.List;

/**
 * User: denispavlov
 * Date: 11/01/2014
 * Time: 11:57
 */
public class ShippingServiceFacadeImpl implements ShippingServiceFacade {

    private final CarrierService carrierService;
    private final CarrierSlaService carrierSlaService;


    public ShippingServiceFacadeImpl(final CarrierService carrierService,
                                     final CarrierSlaService carrierSlaService) {
        this.carrierService = carrierService;
        this.carrierSlaService = carrierSlaService;
    }

    /** {@inheritDoc} */
    @Override
    public List<Carrier> findCarriers(final ShoppingCart shoppingCart) {
        // CPOINT: shipping logic in most cases is very business specific and should be put into this method
        return carrierService.findCarriers(
                null,
                null,
                null,
                shoppingCart.getCurrencyCode());
    }

    /** {@inheritDoc} */
    @Override
    public Pair<Carrier, CarrierSla> getCarrierSla(final ShoppingCart shoppingCart, final List<Carrier> carriersChoices) {

        final Long slaId = shoppingCart.getCarrierSlaId();

        if (slaId != null) {
            for (Carrier carrier : carriersChoices) {
                for (CarrierSla carrierSla : carrier.getCarrierSla()) {
                    if (slaId == carrierSla.getCarrierslaId()) {
                        return new Pair<Carrier, CarrierSla>(carrier, carrierSla);
                    }
                }
            }
        }

        return new Pair<Carrier, CarrierSla>(null, null);
    }

}

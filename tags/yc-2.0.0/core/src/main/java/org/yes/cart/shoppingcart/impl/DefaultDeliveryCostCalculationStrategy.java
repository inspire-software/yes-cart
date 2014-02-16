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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.shoppingcart.DeliveryCostCalculationStrategy;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 13-10-20
 * Time: 6:07 PM
 */
public class DefaultDeliveryCostCalculationStrategy implements DeliveryCostCalculationStrategy {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);

    private final GenericDAO<CarrierSla, Long> genericDao;

    public DefaultDeliveryCostCalculationStrategy(final GenericDAO<CarrierSla, Long> genericDao) {
        this.genericDao = genericDao;
    }

    /** {@inheritDoc} */
    public BigDecimal getDeliveryPrice(final ShoppingCart cart) {
        if (cart.getCarrierSlaId() != null) {
            final CarrierSla carrierSla = genericDao.findById(cart.getCarrierSlaId());
            if (carrierSla == null || carrierSla.getPrice() == null) {
                return BigDecimal.ZERO;
            }
            // TODO: V2 at this moment fixed or zero delivery prices are supported, so just return the price from sla
            return carrierSla.getPrice();
        }
        return ZERO;
    }

}

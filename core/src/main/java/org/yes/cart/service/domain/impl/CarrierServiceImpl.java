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

package org.yes.cart.service.domain.impl;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.domain.CarrierService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CarrierServiceImpl extends BaseGenericServiceImpl<Carrier> implements CarrierService {

    /**
     * Construct service.
     * @param genericDao  doa to use.
     */
    public CarrierServiceImpl(final GenericDAO<Carrier, Long> genericDao) {
        super(genericDao);
    }

    /**
     * {@inheritDoc}
     */
    public List<Carrier> findCarriersByShopId(final long shopId) {
        final List<Carrier> rez  = getGenericDao().findByNamedQuery("CARRIER.BY.SHOPID", shopId);
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public List<Carrier> findCarriersByShopIdAndCurrency(final long shopId, final String currency) {
        final List<Carrier> rez  = findCarriersByShopId(shopId);
        if (CollectionUtils.isEmpty(rez)) {
            return Collections.emptyList();
        }
        final List<Carrier> filtered = new ArrayList<Carrier>(rez);
        filterByCurrency(filtered, currency);
        return filtered;
    }

    /**
     * Filter list of carriers and their SLA by currency.
     *
     * @param currency currency to filter
     * @param carriers to filter
     */
    void filterByCurrency(final List<Carrier> carriers,  final String currency) {

        final Iterator<Carrier> carrierIt = carriers.iterator();
        while (carrierIt.hasNext()) {
            final Carrier carrier = carrierIt.next();
            final Iterator<CarrierSla> slaIt = carrier.getCarrierSla().iterator();
            while (slaIt.hasNext()) {
                final CarrierSla carrierSla = slaIt.next();
                if (!currency.equals(carrierSla.getCurrency())) {
                    slaIt.remove();
                }
            }
            if (carrier.getCarrierSla().isEmpty()) {
                carrierIt.remove();
            }
        }

    }
}

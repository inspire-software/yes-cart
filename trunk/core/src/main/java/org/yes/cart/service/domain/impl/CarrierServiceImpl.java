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

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.domain.CarrierService;

import java.util.ArrayList;
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
     * Find carriers, that can make delivery to given country and state.
     *
     * @param countryCode given country code.
     * @param stateCode   state code - optional.
     * @param city        city - optional.
     * @param currency currency to filter
     * @return list of carries with filtered SLA, that satisfy to given search criteria.
     */
    public List<Carrier> findCarriers(final String countryCode, final String stateCode, final String city, final String currency) {
        final List<Carrier> rez  = new ArrayList<Carrier>();
        rez.addAll(getGenericDao().findAll());
        filterByCurrency(rez, currency);
        return rez;  //TODOV2 implement more sofisticacted search
    }

    /**
     * Filter list of curreiers and his SLA by currency.
     * @param currency currency to filter
     * @param carriers to filter
     */
    void filterByCurrency(final List<Carrier> carriers,  final String currency) {

        for (Carrier carrier : carriers) {
            final List<CarrierSla> toRemove = new ArrayList<CarrierSla>();
            for(CarrierSla carrierSla : carrier.getCarrierSla()) {
                if (!currency.endsWith(carrierSla.getCurrency())) {
                     toRemove.add(carrierSla);
                }
            }
            carrier.getCarrierSla().removeAll(toRemove);
        }

        //remove carriers with empty sla list.
        final List<Carrier> carriersToRemove = new ArrayList<Carrier>();
        for (Carrier carrier : carriers) {
            if(carrier.getCarrierSla().isEmpty()) {
                carriersToRemove.add(carrier);
            }
        }
        carriers.removeAll(carriersToRemove);


    }
}

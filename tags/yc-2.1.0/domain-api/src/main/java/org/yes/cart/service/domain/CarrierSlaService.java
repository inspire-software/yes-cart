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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.CarrierSla;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface CarrierSlaService extends GenericService<CarrierSla> {

    /**
     * Get shipping SLA by carrier Id.
     *
     * @param carrierId given carrier id
     * @return list of SLA, that belongs to given carrier id
     */
    List<CarrierSla> findByCarrier(long carrierId);

    /**
     * Get sla by name. Sla name is unique
     * @param slaName given sla name.
     * @return {@link CarrierSla}
     *
     * @deprecated used only by Google Checkout which is now retired - remove this
     */
    @Deprecated
    CarrierSla findByName(final String slaName);

    /**
     * Find all sla for given currency.
     * @param currency given currency.
     * @return   list of found currency
     *
     * @deprecated used only by Google Checkout which is now retired - remove this
     */
    @Deprecated
    List<CarrierSla> findByCurrency(String currency);
}

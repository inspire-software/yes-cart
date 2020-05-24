/*
 * Copyright 2009 Inspire-Software.com
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

import org.yes.cart.domain.entity.Carrier;

import java.util.List;
import java.util.Map;

/**
 * Carrier service to work with carriers and his SLAs.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface CarrierService extends GenericService<Carrier> {


    /**
     * Find carrier by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. firstname, lastname)
     *
     * @return list of warehouses, that match search criteria or empty list if nobody found or null if no search criteria provided.
     */
    List<Carrier> findCarriers(int start,
                               int offset,
                               String sort,
                               boolean sortDescending,
                               Map<String, List> filter);

    /**
     * Find carrier by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. firstname, lastname)
     *
     * @return count
     */
    int findCarrierCount(Map<String, List> filter);


    /**
     * Find carriers, that are applicable for given shop in all currencies available.
     *
     * <p/>
     * @param shopId shop PK
     * @param includeDisabled include disabled
     *
     * @return list of carries with filtered SLA, that satisfy to given search criteria.
     */
    List<Carrier> findCarriersByShopId(long shopId, final boolean includeDisabled);

    /**
     * Find carriers, that are applicable for given shop.
     *
     * Complex custom logic should be implemented in the ShippingServiceFacade, which is part of
     * websupport module.
     * <p/>
     * @param shopId shop PK
     *
     * @return list of carries with filtered SLA, that satisfy to given search criteria.
     */
    List<Carrier> getCarriersByShopId(long shopId);




}

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

package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.CarrierDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoCarrierService extends GenericDTOService<CarrierDTO> {


    /**
     * Get all carriers and their shop assignments.
     *
     * @return list of carriers
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    Map<CarrierDTO, Map<ShopDTO, Boolean>> getAllWithShops()
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get all assigned to shop carriers.
     *
     * @param shopId shop id
     * @return list of assigned shops with corresponding disabled flag
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    Map<CarrierDTO, Boolean> findAllByShopId(final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Get the assigned to carrier shops
     *
     * @param carrierId carrier PK
     * @return list of assigned shops
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    Map<ShopDTO, Boolean> getAssignedCarrierShops(long carrierId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Get the available to carrier shops. Mean all roles minus assigned.
     *
     * @param carrierId carrier PK
     * @return list of available shops
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration error
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case if some problems with reflection
     */
    List<ShopDTO> getAvailableCarrierShops(long carrierId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Assign given shop to carrier
     *
     * @param carrierId user id
     * @param shopId  shop
     * @param soft true disables the link, false enabled the link right away
     */
    void assignToShop(long carrierId, long shopId, final boolean soft);

    /**
     * Unassign shop from carrier.
     * @param carrierId user id
     * @param shopId  shop
     * @param soft true disables the link but does not remove it, false removed the CarrierShop link completely
     */
    void unassignFromShop(long carrierId, long shopId, final boolean soft);


}

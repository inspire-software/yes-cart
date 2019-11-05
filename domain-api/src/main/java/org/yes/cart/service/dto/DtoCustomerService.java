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

import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoCustomerService extends GenericDTOService<CustomerDTO>, GenericAttrValueService {


    /**
     * Find customer by given search criteria.
     *
     *
     * @param email         optional email
     *
     * @return list of persons, that match search criteria or empty list if nobody found or null if no search criteria provided.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of dto mapping errors
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of config errors
     */
    List<CustomerDTO> findCustomer(String email) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Find customers by filter.
     *
     * @param shopIds   enforce access.
     * @param filter    filter for partial match.
     *
     * @return list of customers
     */
    SearchResult<CustomerDTO> findCustomer(Set<Long> shopIds,
                                           SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Reset password to given user and send generated password via email.
     *
     * @param customer customer to create
     * @param shopId   from what shop customer will have notification
     */
    void resetPassword(CustomerDTO customer, long shopId);


    /**
     * Update tags of customer.
     *
     * @param customerDTO customer
     * @param tags        tags to update
     */
    void updateCustomerTags(CustomerDTO customerDTO, String tags);



    /**
     * Get shop, which is assigned to customer.
     * @param customerId customer id
     * @return list of shops
     */
    Map<ShopDTO, Boolean> getAssignedShop(long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get shop, which is assigned to customer.
     * @param customerId customer id
     * @return list of shops
     */
    List<ShopDTO> getAvailableShop(long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Persist given DTO
     *
     * @param instance DTO to persist
     * @return persisted DTO
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    CustomerDTO createForShop(CustomerDTO instance, long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Grant given shop to user
     *
     * @param customerId user id
     * @param shopId  shop
     * @param soft true disables the link, false enabled the link right away
     */
    void grantShop(long customerId, long shopId, boolean soft);

    /**
     * Revoke shop from user.
     *
     * @param customerId user id
     * @param shopId  shop
     * @param soft true disables the link but does not remove it, false removed the CarrierShop link completely
     */
    void revokeShop(long customerId, long shopId, boolean soft);


}

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

package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoCustomerService extends GenericDTOService<CustomerDTO>, GenericAttrValueService {

    /**
     * Find customer by given search criteria. Search will be performed using like operation.
     *
     *
     * @param email      optional email
     * @param firstname  optional first name
     * @param lastname   optional last name
     * @param middlename optional middle name
     * @param tag        optional tag
     *
     * @return list of persons, that match search criteria or empty list if nobody found or null if no search criteria provided.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of dto mapping errors
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of config errors
     */
    List<CustomerDTO> findCustomer(String email,
                                   String firstname,
                                   String lastname,
                                   String middlename,
                                   String tag) throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Reset password to given user and send generated password via email.
     *
     * @param customer customer to create
     * @param shopId   from what shop customer will have notification
     */
    void remoteResetPassword(CustomerDTO customer, long shopId);


    /**
     * Update tags of customer.
     *
     * @param customerDTO customer
     * @param tags        tags to update
     */
    void updateCustomerTags(CustomerDTO customerDTO, String tags);

}

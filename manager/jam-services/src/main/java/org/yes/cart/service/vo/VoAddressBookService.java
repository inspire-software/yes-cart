/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.VoAddress;
import org.yes.cart.domain.vo.VoAddressBook;

/**
 * User: denispavlov
 * Date: 31/01/2017
 * Time: 14:42
 */
public interface VoAddressBookService {

    /**
     * Get address book by customer ID (email).
     *
     * @param customerId customer Id
     * @param formattingShopId shop that defines the formatting
     * @param lang language
     *
     * @return address book
     */
    VoAddressBook getAddressBook(long customerId, long formattingShopId, String lang) throws Exception;

    /**
     * Update given address.
     *
     * @param vo address to update
     * @return updated instance
     * @throws Exception
     */
    VoAddress update(VoAddress vo) throws Exception;

    /**
     * Create new address
     *
     * @param vo given instance to persist
     * @return persisted instance
     * @throws Exception
     */
    VoAddress create(VoAddress vo) throws Exception;

    /**
     * Remove address by id.
     *
     * @param id address id
     * @throws Exception
     */
    void remove(long id) throws Exception;


}

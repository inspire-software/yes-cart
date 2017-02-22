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

import org.yes.cart.domain.vo.VoPayment;

import java.util.List;

/**
 * User: denispavlov
 * Date: 05/09/2016
 * Time: 18:13
 */
public interface VoPaymentService {


    /**
     * Get all payments for given filter
     *
     * @param filter filter
     * @param max max results
     *
     * @return orders
     *
     * @throws Exception
     */
    List<VoPayment> getFiltered(String filter, int max) throws Exception;

    /**
     * Get all payments for given filter
     *
     * @param filter filter
     * @param operations operations
     * @param statuses statuses
     * @param max max results
     *
     * @return orders
     *
     * @throws Exception
     */
    List<VoPayment> getFiltered(String filter, List<String> operations, List<String> statuses, int max) throws Exception;

}

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

package org.yes.cart.payment.service;


import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface CustomerOrderPaymentService extends PaymentModuleGenericService<CustomerOrderPayment> {


    /**
     * Find payments by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param shops             optional shops to search in
     * @param filter            optional filters (e.g. firstname, lastname)
     *
     * @return list of payments, that match search criteria or empty list if nobody found or null if no search criteria provided.
     */
    List<CustomerOrderPayment> findPayments(int start,
                                            int offset,
                                            String sort,
                                            boolean sortDescending,
                                            Set<String> shops,
                                            Map<String, List> filter);

    /**
     * Find payments by given search criteria. Search will be performed using like operation.
     *
     * @param shops             optional shops to search in
     * @param filter            optional filters (e.g. orderNumber, lastname)
     *
     * @return count
     */
    int findCustomerOrderPaymentCount(Set<String> shops,
                                      Map<String, List> filter);


    /**
     * Find all payments by given parameters.
     * Warning order number or shipment number must be present.
     *
     * @param orderNumber            given order number. optional
     * @param shipmentNumber         given shipment/delivery number. optional
     * @param paymentProcessorResult status   of payment at payment processor . optional
     * @param transactionOperation   operation name at payment gateway. optional
     * @return list of payments
     */
    List<CustomerOrderPayment> findPayments(String orderNumber,
                                            String shipmentNumber,
                                            String paymentProcessorResult,
                                            String transactionOperation);

    /**
     * Find all payments by given parameters.
     * Warning order number or shipment number must be present.
     *
     * @param orderNumber            given order number. optional
     * @param shipmentNumber         given shipment/delivery number. optional
     * @param paymentProcessorResult statuses of payment at payment processor . optional
     * @param transactionOperation   operation names at payment gateway. optional
     * @return list of payments
     */
    List<CustomerOrderPayment> findPayments(String orderNumber,
                                            String shipmentNumber,
                                            String[] paymentProcessorResult,
                                            String[] transactionOperation);


    /**
     * Get order amount
     *
     * @param orderNumber given order number
     * @return order amount
     */
    BigDecimal getOrderAmount(String orderNumber);


}

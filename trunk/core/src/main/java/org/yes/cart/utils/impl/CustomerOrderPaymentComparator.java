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

package org.yes.cart.utils.impl;


import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;

import java.util.Comparator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerOrderPaymentComparator implements Comparator<CustomerOrderPayment> {

    /**
     * {@inheritDoc}
     */
    public int compare(final CustomerOrderPayment orderPayment1, final CustomerOrderPayment orderPayment2) {
        return (orderPayment1.getCustomerOrderPaymentId() < orderPayment2.getCustomerOrderPaymentId() ?
                -1 : (orderPayment1.getCustomerOrderPaymentId() == orderPayment2.getCustomerOrderPaymentId() ? 0 : 1));
    }
}

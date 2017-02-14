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

package org.yes.cart.payment.persistence.entity;

import java.util.Map;

/**
 * Payment callback object for PG's that send a callback to verify payment operation.
 * The rationale for this object is twofold: audit and replay failed callback capabilities
 *
 * User: denispavlov
 * Date: 12/02/2017
 * Time: 14:55
 */
public interface PaymentGatewayCallback extends Auditable {

    /**
     * PK.
     *
     * @return PK
     */
    long getPaymentGatewayCallbackId();

    /**
     * PK.
     *
     * @param paymentGatewayCallbackId PK
     */
    void setPaymentGatewayCallbackId(long paymentGatewayCallbackId);

    /**
     * Flag to indicate that this callback was processed.
     *
     * @return processed
     */
    boolean isProcessed();

    /**
     * Flag to indicate that this callback was processed.
     *
     * @param processed processed
     */
    void setProcessed(boolean processed);

    /**
     * Shop code.
     *
     * @return shop code
     */
    String getShopCode();

    /**
     * Shop code
     *
     * @param shopCode shop code
     */
    void setShopCode(String shopCode);

    /**
     * PG label.
     *
     * @return PG label
     */
    String getLabel();

    /**
     * PG label
     *
     * @param label PG label
     */
    void setLabel(String label);

    /**
     * Callback parameters.
     *
     * @return callback parameters.
     */
    Map getParameterMap();

    /**
     * Callback parameters.
     *
     * @param parameterMap callback parameters.
     */
    void setParameterMap(Map parameterMap);

    /**
     * Request dump.
     *
     * @return request dump.
     */
    String getRequestDump();

    /**
     * Request dump.
     *
     * @param requestDump request dump.
     */
    void setRequestDump(String requestDump);

}

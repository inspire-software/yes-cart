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

package org.yes.cart.payment.dto;

import java.io.Serializable;

/**
 * Payment gateway supported features.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 13:24:24
 */


public interface PaymentGatewayFeature extends Serializable {


    /**
     * Some payment gateway supports auth operation per
     * whole order, some per each shipment.
     *
     * @return true if supports per each shipment.
     */
    boolean isSupportAuthorizePerShipment();


    /**
     * Is payment gateway supports authorize. Payment processor may use
     * the Authorize Capture operation otherwise.
     *
     * @return true if supports
     */
    boolean isSupportAuthorize();

    /**
     * Is payment gateway supports captures on prev authorization.
     *
     * @return true if supports
     */
    boolean isSupportCapture();

    /**
     * Is payment gateway supports immediate sale operation, without delivery confirmation.
     *
     * @return true if supports
     */
    boolean isSupportAuthorizeCapture();

    /**
     * Is payment gateway supports void of a previous capture.
     *
     * @return true if supports
     */
    boolean isSupportVoid();


    /**
     * Is payment gateway supports reverse a previous authorization.
     *
     * @return true if supports
     */
    boolean isSupportReverseAuthorization();

    /**
     * Is payment gateway supports refunds a previous capture.
     *
     * @return true if supports
     */
    boolean isSupportRefund();

    /**
     * Is form will be processed on external site. If true, than need to provide external site urls (and callbacks).
     *
     * @return true
     */
    boolean isExternalFormProcessing();


    /**
     * Get additional supported features in key1=value1[,key2=value2] string.
     *
     * @return key=value string.
     */
    String getAdditionalFeatures();

    /**
     * @return online payment flag
     */
    boolean isOnlineGateway();

    /**
     * Auto capture means that this payment is self-authorised.
     * For online gateways it is always true since the payment is authorise by the PG service.
     * This setting is useful for offline PG configuration, for example with offline invoice PG which is on signed
     * contact, so all payments are essentially pre-authorised
     *
     * @return true if payment can be automatically captured without human interaction
     */
    boolean isAutoCapture();

    /**
     * Is need to pass details in case of html for construction.
     *
     * @return   true in case if need provide detail info
     */
    boolean isRequireDetails();

    /**
     *
     * @return true in case if pgw support amount capture more that authorized before.
     */
    boolean isSupportCaptureMore() ;

    /**
     *
     * @return true in case if pgw support amount capture less that authorized before.
     */
    boolean isSupportCaptureLess();

}

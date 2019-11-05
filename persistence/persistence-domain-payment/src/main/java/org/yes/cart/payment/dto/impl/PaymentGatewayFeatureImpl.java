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

package org.yes.cart.payment.dto.impl;

import org.yes.cart.payment.dto.PaymentGatewayFeature;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 13:24:24
 */
public class PaymentGatewayFeatureImpl implements PaymentGatewayFeature {

    protected final boolean supportAuthorize;
    protected final boolean supportCapture;
    protected final boolean supportAuthorizeCapture;
    protected final boolean supportVoid;
    protected final boolean supportReverseAuthorization;
    protected final boolean supportRefund;
    protected final boolean externalFormProcessing;
    protected final boolean onlineGateway;
    protected final boolean autoCapture;
    protected final boolean requireDetails;
    protected final boolean supportCaptureMore;
    protected final boolean supportCaptureLess;


    protected final boolean supportAuthorizePerShipment;
    protected final String additionalFeatures;




    /**
     * Construct PaymentGatewayFeature
     *
     * @param supportAuthorize            supports AUTH
     * @param supportAuthorizePerShipment supports AUTH per shipment
     * @param supportCapture              supports CAPTURE
     * @param supportAuthorizeCapture     supports AUTH_CAPTURE (AUTH+CAPTURE in single transaction)
     * @param supportVoid                 supports VOID (reverse CAPTURE)
     * @param supportReverseAuthorization supports reverse AUTH
     * @param supportRefund               supports REFUND
     * @param externalFormProcessing      transaction processing is done outside of sellers site (e.g. pay button
     *                                    that redirects to PG site and then returns customer to main site once
     *                                    transaction is completed)
     * @param additionalFeatures          custom features
     * @param onlineGateway               is this online payment gateway (if true, payment is processed by automatic
     *                                    process and is fully traceable, false means offline payment such as payments
     *                                    over the phone where traceability is reduced to makring that payment was taken
     *                                    by someone at some point)
     * @param requireDetails true in case if gateway require payment details for html form
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public PaymentGatewayFeatureImpl(final boolean supportAuthorize,
                                     final boolean supportAuthorizePerShipment,
                                     final boolean supportCapture,
                                     final boolean supportAuthorizeCapture,
                                     final boolean supportVoid,
                                     final boolean supportReverseAuthorization,
                                     final boolean supportRefund,
                                     final boolean externalFormProcessing,
                                     final boolean onlineGateway,
                                     final boolean autoCapture,
                                     final boolean requireDetails,
                                     final String additionalFeatures,
                                     final boolean supportCaptureMore,
                                     final boolean supportCaptureLess) {
        this.supportAuthorize = supportAuthorize;
        this.supportAuthorizePerShipment = supportAuthorizePerShipment;
        this.supportCapture = supportCapture;
        this.supportAuthorizeCapture = supportAuthorizeCapture;
        this.supportVoid = supportVoid;
        this.supportReverseAuthorization = supportReverseAuthorization;
        this.supportRefund = supportRefund;
        this.externalFormProcessing = externalFormProcessing;
        this.additionalFeatures = additionalFeatures;
        this.onlineGateway = onlineGateway;
        this.autoCapture = autoCapture;
        this.requireDetails = requireDetails;
        this.supportCaptureMore = supportCaptureMore;
        this.supportCaptureLess = supportCaptureLess;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isOnlineGateway() {
        return onlineGateway;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isAutoCapture() {
        return autoCapture;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isSupportAuthorizePerShipment() {
        return supportAuthorizePerShipment;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isSupportAuthorize() {
        return supportAuthorize;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isSupportCapture() {
        return supportCapture;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isSupportAuthorizeCapture() {
        return supportAuthorizeCapture;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isSupportVoid() {
        return supportVoid;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isSupportReverseAuthorization() {
        return supportReverseAuthorization;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isSupportRefund() {
        return supportRefund;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isExternalFormProcessing() {
        return externalFormProcessing;
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public String getAdditionalFeatures() {
        return additionalFeatures;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isRequireDetails() {
        return requireDetails;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isSupportCaptureMore() {
        return supportCaptureMore;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isSupportCaptureLess() {
        return supportCaptureLess;
    }

    /**
     * {@inheritDoc }
     */
    public String toString() {
        return "PaymentGatewayFeatureImpl supported features{" +
                (supportAuthorize ? " Authorize " : "") +
                (supportCapture ? " Capture " : "") +
                (supportAuthorizeCapture ? " AuthorizeCapture " : "") +
                (supportVoid ? " Void " : "") +
                (supportReverseAuthorization ? " ReverseAuthorization " : "") +
                (supportRefund ? " Refund " : "") +
                (externalFormProcessing ? " External form processing " : " Internal form processing") +
                (supportAuthorizePerShipment ? " AuthorizePerShipment " : "") +
                ", additionalFeatures=[" + additionalFeatures + ']' +
                '}';
    }
}

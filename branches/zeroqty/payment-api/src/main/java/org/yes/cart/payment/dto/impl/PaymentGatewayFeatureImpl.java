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

package org.yes.cart.payment.dto.impl;

import org.yes.cart.payment.dto.PaymentGatewayFeature;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 13:24:24
 */
public class PaymentGatewayFeatureImpl implements PaymentGatewayFeature {

    protected boolean supportAuthorize;
    protected boolean supportCapture;
    protected boolean supportAuthorizeCapture;
    protected boolean supportVoid;
    protected boolean supportReverseAuthorization;
    protected boolean supportRefund;
    protected boolean externalFormProcessing;
    protected boolean onlineGateway;
    protected boolean requireDetails;


    protected boolean supportAuthorizePerShipment;
    protected String additionalFeatures;




    /**
     * Construct PaymentGatewayFeature
     *
     * @param supportAuthorize
     * @param supportAuthorizePerShipment
     * @param supportCapture
     * @param supportAuthorizeCapture
     * @param supportVoid
     * @param supportReverseAuthorization
     * @param supportRefund
     * @param externalFormProcessing
     * @param additionalFeatures
     * @param onlineGateway               is this online payment gateway
     * @param requireDetails true in case if gateway require paymnet details for html form
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public PaymentGatewayFeatureImpl(
            final boolean supportAuthorize,
            final boolean supportAuthorizePerShipment,
            final boolean supportCapture,
            final boolean supportAuthorizeCapture,
            final boolean supportVoid,
            final boolean supportReverseAuthorization,
            final boolean supportRefund,
            final boolean externalFormProcessing,
            final boolean onlineGateway,
            final boolean requireDetails,
            final String additionalFeatures) {
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
        this.requireDetails = requireDetails;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isOnlineGateway() {
        return onlineGateway;
    }

    /**
     * {@inheritDoc }
     */
    public void setOnlineGateway(final boolean onlineGateway) {
        this.onlineGateway = onlineGateway;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isSupportAuthorizePerShipment() {
        return supportAuthorizePerShipment;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isSupportAuthorize() {
        return supportAuthorize;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isSupportCapture() {
        return supportCapture;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isSupportAuthorizeCapture() {
        return supportAuthorizeCapture;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isSupportVoid() {
        return supportVoid;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isSupportReverseAuthorization() {
        return supportReverseAuthorization;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isSupportRefund() {
        return supportRefund;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isExternalFormProcessing() {
        return externalFormProcessing;
    }


    /**
     * {@inheritDoc }
     */
    public String getAdditionalFeatures() {
        return additionalFeatures;
    }


    public void setSupportAuthorize(final boolean supportAuthorize) {
        this.supportAuthorize = supportAuthorize;
    }

    /**
     * {@inheritDoc }
     */
    public void setSupportCapture(final boolean supportCapture) {
        this.supportCapture = supportCapture;
    }

    /**
     * {@inheritDoc }
     */
    public void setSupportAuthorizeCapture(final boolean supportAuthorizeCapture) {
        this.supportAuthorizeCapture = supportAuthorizeCapture;
    }

    /**
     * {@inheritDoc }
     */
    public void setSupportVoid(final boolean supportVoid) {
        this.supportVoid = supportVoid;
    }

    /**
     * {@inheritDoc }
     */
    public void setSupportReverseAuthorization(final boolean supportReverseAuthorization) {
        this.supportReverseAuthorization = supportReverseAuthorization;
    }

    /**
     * {@inheritDoc }
     */
    public void setSupportRefund(final boolean supportRefund) {
        this.supportRefund = supportRefund;
    }

    /**
     * {@inheritDoc }
     */
    public void setExternalFormProcessing(final boolean externalFormProcessing) {
        this.externalFormProcessing = externalFormProcessing;
    }


    /**
     * {@inheritDoc }
     */
    public void setSupportAuthorizePerShipment(final boolean supportAuthorizePerShipment) {
        this.supportAuthorizePerShipment = supportAuthorizePerShipment;
    }

    /**
     * {@inheritDoc }
     */
    public void setAdditionalFeatures(final String additionalFeatures) {
        this.additionalFeatures = additionalFeatures;
    }

    /**
     * {@inheritDoc }
     */
    public boolean isRequireDetails() {
        return requireDetails;
    }

    /**
     * {@inheritDoc }
     */
    public void setRequireDetails(boolean requireDetails) {
        this.requireDetails = requireDetails;
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

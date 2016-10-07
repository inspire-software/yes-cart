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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;


/**
 * User: denispavlov
 * Date: 28/07/2016
 * Time: 17:54
 */
@Dto
public class VoPaymentGatewayFeature {

    @DtoField(readOnly = true)
    private boolean supportAuthorize;
    @DtoField(readOnly = true)
    private boolean supportCapture;
    @DtoField(readOnly = true)
    private boolean supportAuthorizeCapture;
    @DtoField(readOnly = true)
    private boolean supportVoid;
    @DtoField(readOnly = true)
    private boolean supportReverseAuthorization;
    @DtoField(readOnly = true)
    private boolean supportRefund;
    @DtoField(readOnly = true)
    private boolean externalFormProcessing;
    @DtoField(readOnly = true)
    private boolean onlineGateway;
    @DtoField(readOnly = true)
    private boolean requireDetails;
    @DtoField(readOnly = true)
    private boolean supportCaptureMore;
    @DtoField(readOnly = true)
    private boolean supportCaptureLess;


    @DtoField(readOnly = true)
    private boolean supportAuthorizePerShipment;
    @DtoField(readOnly = true)
    private String additionalFeatures;

    public boolean isSupportAuthorize() {
        return supportAuthorize;
    }

    public void setSupportAuthorize(final boolean supportAuthorize) {
        this.supportAuthorize = supportAuthorize;
    }

    public boolean isSupportCapture() {
        return supportCapture;
    }

    public void setSupportCapture(final boolean supportCapture) {
        this.supportCapture = supportCapture;
    }

    public boolean isSupportAuthorizeCapture() {
        return supportAuthorizeCapture;
    }

    public void setSupportAuthorizeCapture(final boolean supportAuthorizeCapture) {
        this.supportAuthorizeCapture = supportAuthorizeCapture;
    }

    public boolean isSupportVoid() {
        return supportVoid;
    }

    public void setSupportVoid(final boolean supportVoid) {
        this.supportVoid = supportVoid;
    }

    public boolean isSupportReverseAuthorization() {
        return supportReverseAuthorization;
    }

    public void setSupportReverseAuthorization(final boolean supportReverseAuthorization) {
        this.supportReverseAuthorization = supportReverseAuthorization;
    }

    public boolean isSupportRefund() {
        return supportRefund;
    }

    public void setSupportRefund(final boolean supportRefund) {
        this.supportRefund = supportRefund;
    }

    public boolean isExternalFormProcessing() {
        return externalFormProcessing;
    }

    public void setExternalFormProcessing(final boolean externalFormProcessing) {
        this.externalFormProcessing = externalFormProcessing;
    }

    public boolean isOnlineGateway() {
        return onlineGateway;
    }

    public void setOnlineGateway(final boolean onlineGateway) {
        this.onlineGateway = onlineGateway;
    }

    public boolean isRequireDetails() {
        return requireDetails;
    }

    public void setRequireDetails(final boolean requireDetails) {
        this.requireDetails = requireDetails;
    }

    public boolean isSupportCaptureMore() {
        return supportCaptureMore;
    }

    public void setSupportCaptureMore(final boolean supportCaptureMore) {
        this.supportCaptureMore = supportCaptureMore;
    }

    public boolean isSupportCaptureLess() {
        return supportCaptureLess;
    }

    public void setSupportCaptureLess(final boolean supportCaptureLess) {
        this.supportCaptureLess = supportCaptureLess;
    }

    public boolean isSupportAuthorizePerShipment() {
        return supportAuthorizePerShipment;
    }

    public void setSupportAuthorizePerShipment(final boolean supportAuthorizePerShipment) {
        this.supportAuthorizePerShipment = supportAuthorizePerShipment;
    }

    public String getAdditionalFeatures() {
        return additionalFeatures;
    }

    public void setAdditionalFeatures(final String additionalFeatures) {
        this.additionalFeatures = additionalFeatures;
    }
}

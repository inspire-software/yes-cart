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
 * Date: 01/09/2016
 * Time: 15:28
 */
@Dto
public class VoCustomerOrderTransitionResult {

    @DtoField(readOnly = true)
    private  String errorCode;

    @DtoField(readOnly = true)
    private  String orderNum;

    @DtoField(readOnly = true)
    private  String shippingNum;

    @DtoField(readOnly = true)
    private  String localizationKey;

    @DtoField(readOnly = true)
    private  Object [] localizedMessageParameters;

    @DtoField(readOnly = true)
    private  String errorMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(final String orderNum) {
        this.orderNum = orderNum;
    }

    public String getShippingNum() {
        return shippingNum;
    }

    public void setShippingNum(final String shippingNum) {
        this.shippingNum = shippingNum;
    }

    public String getLocalizationKey() {
        return localizationKey;
    }

    public void setLocalizationKey(final String localizationKey) {
        this.localizationKey = localizationKey;
    }

    public Object[] getLocalizedMessageParameters() {
        return localizedMessageParameters;
    }

    public void setLocalizedMessageParameters(final Object[] localizedMessageParameters) {
        this.localizedMessageParameters = localizedMessageParameters;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

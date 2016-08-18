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

import java.util.List;

/**
 * User: denispavlov
 * Date: 17/08/2016
 * Time: 08:23
 */
public class VoPaymentGateway extends VoPaymentGatewayInfo {

    private String shopCode;
    private VoPaymentGatewayFeature feature;
    private List<VoPaymentGatewayParameter> parameters;

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(final String shopCode) {
        this.shopCode = shopCode;
    }

    public VoPaymentGatewayFeature getFeature() {
        return feature;
    }

    public void setFeature(final VoPaymentGatewayFeature feature) {
        this.feature = feature;
    }

    public List<VoPaymentGatewayParameter> getParameters() {
        return parameters;
    }

    public void setParameters(final List<VoPaymentGatewayParameter> parameters) {
        this.parameters = parameters;
    }

    public VoPaymentGateway(final String label,
                            final String name,
                            final boolean active,
                            final String shopCode,
                            final VoPaymentGatewayFeature feature,
                            final List<VoPaymentGatewayParameter> parameters) {
        super(label, name, active);
        this.shopCode = shopCode;
        this.feature = feature;
        this.parameters = parameters;
    }
}

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

package org.yes.cart.domain.ro;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 28/03/2015
 * Time: 16:50
 */
@XmlRootElement(name = "shipping-option")
public class ShippingOptionRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    private String carriersla;
    private String billingAddressId;
    private String deliveryAddressId;

    @XmlElement(name = "carriersla")
    public String getCarriersla() {
        return carriersla;
    }

    public void setCarriersla(final String carriersla) {
        this.carriersla = carriersla;
    }

    @XmlElement(name = "billing-address-id")
    public String getBillingAddressId() {
        return billingAddressId;
    }

    public void setBillingAddressId(final String billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    @XmlElement(name = "delivery-address-id")
    public String getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(final String deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }
}

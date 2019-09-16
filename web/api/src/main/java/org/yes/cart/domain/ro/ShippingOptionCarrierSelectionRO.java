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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 14/09/2019
 * Time: 16:10
 */
@XmlRootElement(name = "shipping-option-carrier-selection")
public class ShippingOptionCarrierSelectionRO implements Serializable {

    private static final long serialVersionUID = 20190914L;

    private String carrierSlaId;
    private String supplier;

    @XmlAttribute(name = "carrier-sla-id")
    public String getCarrierSlaId() {
        return carrierSlaId;
    }

    public void setCarrierSlaId(final String carrierSlaId) {
        this.carrierSlaId = carrierSlaId;
    }

    @XmlAttribute(name = "supplier")
    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(final String supplier) {
        this.supplier = supplier;
    }
}

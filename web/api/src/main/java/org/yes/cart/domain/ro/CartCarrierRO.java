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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 30/12/2016
 * Time: 21:49
 */
@XmlRootElement(name = "cart-carrier")
public class CartCarrierRO implements Serializable {

    private String supplier;
    private String supplierName;
    private List<CarrierRO> carriers;

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(final String supplier) {
        this.supplier = supplier;
    }

    @XmlElement(name = "supplier-name")
    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(final String supplierName) {
        this.supplierName = supplierName;
    }

    @XmlElementWrapper(name = "carriers")
    @XmlElement(name = "carrier")
    public List<CarrierRO> getCarriers() {
        if (carriers == null) {
            carriers = new ArrayList<CarrierRO>();
        }
        return carriers;
    }

    public void setCarriers(final List<CarrierRO> carriers) {
        this.carriers = carriers;
    }
}

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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 02/04/2015
 * Time: 17:46
 */
@Dto
@XmlRootElement(name = "order-item")
public class OrderItemRO extends CartItemRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    private long customerOrderDetId;

    @XmlAttribute(name = "customer-order-det-id")
    public long getCustomerOrderDetId() {
        return customerOrderDetId;
    }

    public void setCustomerOrderDetId(final long customerOrderDetId) {
        this.customerOrderDetId = customerOrderDetId;
    }
}

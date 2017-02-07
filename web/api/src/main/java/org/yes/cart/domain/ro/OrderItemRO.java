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

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * User: denispavlov
 * Date: 02/04/2015
 * Time: 17:46
 */
@Dto
@XmlRootElement(name = "order-item")
public class OrderItemRO extends CartItemRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(readOnly = true)
    private long customerOrderDetId;

    @DtoField(readOnly = true)
    private String deliveryRemarks;
    @DtoField(readOnly = true)
    private Date deliveryEstimatedMin;
    @DtoField(readOnly = true)
    private Date deliveryEstimatedMax;
    @DtoField(readOnly = true)
    private Date deliveryGuaranteed;
    @DtoField(readOnly = true)
    private String b2bRemarks;


    @XmlAttribute(name = "customer-order-det-id")
    public long getCustomerOrderDetId() {
        return customerOrderDetId;
    }

    public void setCustomerOrderDetId(final long customerOrderDetId) {
        this.customerOrderDetId = customerOrderDetId;
    }

    @XmlElement(name = "item-delivery-remarks")
    public String getDeliveryRemarks() {
        return deliveryRemarks;
    }

    public void setDeliveryRemarks(final String deliveryRemarks) {
        this.deliveryRemarks = deliveryRemarks;
    }

    @XmlAttribute(name = "item-delivery-estimated-min")
    public Date getDeliveryEstimatedMin() {
        return deliveryEstimatedMin;
    }

    public void setDeliveryEstimatedMin(final Date deliveryEstimatedMin) {
        this.deliveryEstimatedMin = deliveryEstimatedMin;
    }

    @XmlAttribute(name = "item-delivery-estimated-max")
    public Date getDeliveryEstimatedMax() {
        return deliveryEstimatedMax;
    }

    public void setDeliveryEstimatedMax(final Date deliveryEstimatedMax) {
        this.deliveryEstimatedMax = deliveryEstimatedMax;
    }

    @XmlAttribute(name = "item-delivery-guaranteed")
    public Date getDeliveryGuaranteed() {
        return deliveryGuaranteed;
    }

    public void setDeliveryGuaranteed(final Date deliveryGuaranteed) {
        this.deliveryGuaranteed = deliveryGuaranteed;
    }

    @XmlElement(name = "item-b2b-remarks")
    public String getB2bRemarks() {
        return b2bRemarks;
    }

    public void setB2bRemarks(final String b2bRemarks) {
        this.b2bRemarks = b2bRemarks;
    }
}

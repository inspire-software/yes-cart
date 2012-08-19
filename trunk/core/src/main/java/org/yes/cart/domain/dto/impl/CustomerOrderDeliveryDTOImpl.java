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
package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.dto.matcher.impl.CustomerOrderDeliveryDetailMatcher;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * Dto to represent delivery from order.
 * @see org.yes.cart.domain.entity.CustomerOrderDelivery for more details.
 *
 * User: iazarny@yahoo.com
 * Date: 8/17/12
 * Time: 7:20 AM
 */
@Dto
public class CustomerOrderDeliveryDTOImpl implements CustomerOrderDeliveryDTO {

    private static final long serialVersionUID = 20120817L;

    @DtoField(value = "customerOrderDeliveryId", readOnly = true)
    private long customerOrderDeliveryId;

    @DtoField(value = "devileryNum")
    private String devileryNum;

    @DtoField(value = "refNo")
    private String refNo;

    @DtoField(value = "deliveryStatus")
    private String deliveryStatus;

    @DtoField(value = "carrierSla.name", readOnly = true)
    private String carrierSlaName;

    @DtoField(value = "carrierSla.carrier.name", readOnly = true)
    private String carrierName;

    @DtoField(value = "customerOrder.ordernum", readOnly = true)
    private String ordernum;


    @DtoField(value = "deliveryGroup", readOnly = true)
    private String deliveryGroup;

    @DtoField(value = "price", readOnly = true)
    private BigDecimal price;


    @DtoCollection(
            value = "detail",
            dtoBeanKey = "org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO",
            entityGenericType =  CustomerOrderDeliveryDet.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = CustomerOrderDeliveryDetailMatcher.class,
            readOnly = true
    )
    private Collection<CustomerOrderDeliveryDetailDTO> detail;

    /** {@inheritDoc} */
    public Collection<CustomerOrderDeliveryDetailDTO> getDetail() {
        return detail;
    }

    /** {@inheritDoc} */
    public void setDetail(final Collection<CustomerOrderDeliveryDetailDTO> detail) {
        this.detail = detail;
    }

    /** {@inheritDoc} */
    public BigDecimal getPrice() {
        return price;
    }

    /** {@inheritDoc} */
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    /** {@inheritDoc} */
    public long getCustomerOrderDeliveryId() {
        return customerOrderDeliveryId;
    }

    /** {@inheritDoc} */
    public void setCustomerOrderDeliveryId(final long customerOrderDeliveryId) {
        this.customerOrderDeliveryId = customerOrderDeliveryId;
    }

    /** {@inheritDoc} */
    public String getDevileryNum() {
        return devileryNum;
    }

    /** {@inheritDoc} */
    public void setDevileryNum(final String devileryNum) {
        this.devileryNum = devileryNum;
    }

    /** {@inheritDoc} */
    public String getRefNo() {
        return refNo;
    }

    /** {@inheritDoc} */
    public void setRefNo(final String refNo) {
        this.refNo = refNo;
    }

    /** {@inheritDoc} */
    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    /** {@inheritDoc} */
    public void setDeliveryStatus(final String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    /** {@inheritDoc} */
    public String getCarrierSlaName() {
        return carrierSlaName;
    }

    /** {@inheritDoc} */
    public void setCarrierSlaName(final String carrierSlaName) {
        this.carrierSlaName = carrierSlaName;
    }

    /** {@inheritDoc} */
    public String getCarrierName() {
        return carrierName;
    }

    /** {@inheritDoc} */
    public void setCarrierName(final String carrierName) {
        this.carrierName = carrierName;
    }

    /** {@inheritDoc} */
    public String getOrdernum() {
        return ordernum;
    }

    /** {@inheritDoc} */
    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    /** {@inheritDoc} */
    public String getDeliveryGroup() {
        return deliveryGroup;
    }

    /** {@inheritDoc} */
    public void setDeliveryGroup(final String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
    }

    /** {@inheritDoc} */
    public long getId() {
        return customerOrderDeliveryId;
    }
}

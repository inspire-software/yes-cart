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
package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * Represent one delivery from order with details.
 * @see org.yes.cart.domain.entity.CustomerOrderDelivery for more details.
 *
 * User: iazarny@yahoo.com
 * Date: 8/15/12
 * Time: 7:42 AM
 */
public interface CustomerOrderDeliveryDTO   extends Identifiable {



    /**
     * Shipment pk.
     *
     * @return pk value
     */
    long getCustomerOrderDeliveryId();

    /**
     * Set pk value.
     *
     * @param customerOrderDeliveryId pk value.
     */
    void setCustomerOrderDeliveryId(long customerOrderDeliveryId);

    /**
     * Get delivery number.
     *
     * @return delivery number
     */
    String getDeliveryNum();

    /**
     * Set delivery number
     *
     * @param deliveryNum delivery number
     */
    void setDeliveryNum(String deliveryNum);

    /**
     * Get external delivery number, if any.
     *
     * @return external delivery number
     */
    String getRefNo();

    /**
     * Set external delivery number.
     *
     * @param refNo external delivery number, if any.
     */
    void setRefNo(String refNo);

    /**
     * Calculated delivery price.
     *
     * @return delivery price.
     */
    BigDecimal getPrice();

    /**
     * Set delivery price.
     *
     * @param price delivery price.
     */
    void setPrice(BigDecimal price);


    /**
     * Get order delivery status
     *
     * @return order delivery status
     */
    String getDeliveryStatus();

    /**
     * Set order delivery status
     *
     * @param deliveryStatus order delivery status
     */
    void setDeliveryStatus(String deliveryStatus);


    /**
     * Get delivery items.
     *
     * @return delivery items.
     */
    Collection<CustomerOrderDeliveryDetailDTO> getDetail();

    /**
     * Set delivery items.
     *
     * @param detail delivery items.
     */
    void setDetail(Collection<CustomerOrderDeliveryDetailDTO> detail);


    /**
     * Get carrier SLA name.
     * @return Carrier's SLA name
     */
    String getCarrierSlaName();

    /**
     * Set carrier sla name.
     * @param carrierSlaName     sla name.
     */
    void setCarrierSlaName(String carrierSlaName);

    /**
     * Get carrier name.
     * @return carrier name
     */
    String getCarrierName();

    /**
     * Set carrier  name.
     * @param carrierName  name.
     */
    void setCarrierName(String carrierName);


    /**
     * Get the order number. Order number not a pk value, it
     * can be slighty different and depends from active {@link org.yes.cart.service.order.OrderNumberGenerator}
     * implementation.
     *
     * @return order number
     */
    String getOrdernum();

    /**
     * Set order number.
     *
     * @param ordernum order number to set.
     */
    void setOrdernum(String ordernum);


    /**
     * Get delivery group (type).
     *
     * @return delivery group.
     */
    String getDeliveryGroup();

    /**
     * Set Delvery group.
     *
     * @param deliveryGroup delivery group.
     */
    void setDeliveryGroup(String deliveryGroup);

    /**
     * Get shipping address.
     * @return shipping address.
     */
    String getShippingAddress();

    /**
     * Set shipping address.
     * @param shippingAddress address
     */
    void setShippingAddress(String shippingAddress);

    /**
     * Get billing address.
     * @return billing address.
     */
    String getBillingAddress();

    /**
     * Set billing address.
     * @param billingAddress billing address.
     */
    void setBillingAddress(String billingAddress);

    /**
     * Order currency.
     * @return currency
     */
    String getCurrency();

    /**
     * Set currency.
     * @param currency currency
     */
    void setCurrency(String currency);

    /**
     * Get shop name.
     * @return shop name.
     */
    String getShopName();

    /**
     * Set shop name.
     * @param shopName shop name.
     */
    void setShopName(String shopName);


    /**
     * Get label of payment gateway.
     * @return payment gateway.
     */
    String getPgLabel();

    /**
     * Set payment gateway.
     * @param pgLabel  payment gateway.
     */
    void setPgLabel(String pgLabel);


    /**
     * Is pwg support capture more than authorised.
     * @return true if support
     */
    boolean isSupportCaptureMore();

    /**
     * Set capture more flag.
     * @param supportCaptureMore flag to set
     */
    void setSupportCaptureMore(boolean supportCaptureMore) ;

    /**
     * support capture less, than authorised.
     * @return true if pgw supports.
     */
    boolean isSupportCaptureLess();

    /**
     * Set support capture less flag.
     * @param supportCaptureLess capture less flag.
     */
    void setSupportCaptureLess( boolean supportCaptureLess)  ;



}

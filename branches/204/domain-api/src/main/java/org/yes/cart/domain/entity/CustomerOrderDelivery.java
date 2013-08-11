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

package org.yes.cart.domain.entity;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * The Order Delivery. Each order has one or more deliveries.
 * Delivery/Shpiment split posible when not all skus present on warehouse
 * and skus have different availability (pre/back order, etc.), also
 * some products can not be shipped together, because of security.
 * Delivery address can be taken from order.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerOrderDelivery extends Auditable {


    /**
     * On fillfillment center.
     */
    String DELIVERY_STATUS_ON_FULLFILMENT = "ds.fullfillment";

    /**
     * Wait for particular date. Use for preorder.
     */
    String DELIVERY_STATUS_DATE_WAIT = "ds.wait.date";

    /**
     * Wait for inventory. Used for backorder.
     */
    String DELIVERY_STATUS_INVENTORY_WAIT = "ds.wait.inventory";


    /**
     * Inventory reserved
     */
    String DELIVERY_STATUS_INVENTORY_RESERVED = "ds.inventory.reserved";

    /**
     * Void reservation reserved
     */
    String DELIVERY_STATUS_INVENTORY_VOID_RESERVATION = "ds.inventory.void.reserv";


    /**
     * Inventory allocated
     */
    String DELIVERY_STATUS_INVENTORY_ALLOCATED = "ds.inventory.allocated";

    /**
     * Inventory deallocated
     */
    String DELIVERY_STATUS_INVENTORY_DEALLOCATED = "ds.inventory.deallocated";

    /**
     * Order is packing
     */
    String DELIVERY_STATUS_PACKING = "ds.packing";

    /**
     * Wait for shipment.
     */
    String DELIVERY_STATUS_SHIPMENT_READY = "ds.shipment.ready";

    /**
     * Delivery in progress.
     */
    String DELIVERY_STATUS_SHIPMENT_IN_PROGRESS = "ds.shipment.inprogress";

    /**
     * Delivered.
     */
    String DELIVERY_STATUS_SHIPPED = "ds.shipped";


    /**
     * Can be delivered.
     */
    String STANDARD_DELIVERY_GROUP = "D1";
    /**
     * Need wait for date.
     */
    String DATE_WAIT_DELIVERY_GROUP = "D2";
    /**
     * Need wait for inventory.
     */
    String INVENTORY_WAIT_DELIVERY_GROUP = "D3";
    /**
     * Electronic delivery.
     */
    String ELECTONIC_DELIVERY_GROUP = "D4";

    /**
     * Mixed physical delivery group.
     */
    String MIX_DELIVERY_GROUP = "D5";

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
    Collection<CustomerOrderDeliveryDet> getDetail();

    /**
     * Set delivery items.
     *
     * @param detail delivery items.
     */
    void setDetail(Collection<CustomerOrderDeliveryDet> detail);

    /**
     * Get delivery SLA.
     *
     * @return delivery SLA.
     */
    CarrierSla getCarrierSla();

    /**
     * Set delivery SLA.
     *
     * @param carrierSla delivery SLA.
     */
    void setCarrierSla(CarrierSla carrierSla);

    /**
     * GEt customer order.
     *
     * @return order.
     */
    CustomerOrder getCustomerOrder();

    /**
     * Set customer order.
     *
     * @param customerOrder customer order.
     */
    void setCustomerOrder(CustomerOrder customerOrder);


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


}
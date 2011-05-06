package org.yes.cart.domain.entity;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * The Order Delivery. Each eroder can have one or more deliveries.
 * Delivery/Shpiment split posible when not all skus present on warehouse
 * and skus have different availability (pre/back order, etc.), also
 * some products can not be shipped together, because of secutity.
 * Delivery address can be taken from order.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54

 */
public interface CustomerOrderDelivery extends Auditable {


    /**
     * On fillfillment center.
     */
    String DELIVERY_STATUS_ON_FULLFILMENT = "fullfillment";

    /**
     * Wait for particular date. Use for preorder.
     */
    String DELIVERY_STATUS_DATE_WAIT = "wait.date";

    /**
     * Wait for inventory. Used for backorder.
     */
    String DELIVERY_STATUS_INVENTORY_WAIT = "wait.inventory";


    /**
     * Inventory reserved
     */
    String DELIVERY_STATUS_INVENTORY_RESERVED = "inventory.reserved";

   /**
     * Inventory reserved
     */
    String DELIVERY_STATUS_INVENTORY_VOID_RESERVATION = "inventory.void.reserv";


    /**
     * Inventory allocated
     */
    String DELIVERY_STATUS_INVENTORY_ALLOCATED = "inventory.allocated";

    /**
     * Inventory allocated
     */
    String DELIVERY_STATUS_INVENTORY_DEALLOCATED = "inventory.deallocated";

    /**
     * Order is packing
     */
    String DELIVERY_STATUS_PACKING = "packing";

    /**
     * Wait for shipment.
     */
    String DELIVERY_STATUS_SHIPMENT_READY = "shipment.ready";

    /**
     * Delivery in progress.
     */
    String DELIVERY_STATUS_SHIPMENT_IN_PROGRESS = "shipment.inprogress";

    /**
     * Delivered.
     */
    String DELIVERY_STATUS_SHIPPED = "shipped";



    
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
    String getDevileryNum();

    /**
     * Set delivery number
     *
     * @param devileryNum delivery number
     */
    void setDevileryNum(String devileryNum);

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
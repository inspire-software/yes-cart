package org.yes.cart.domain.entity;

import org.yes.cart.domain.dto.CartItem;

import java.math.BigDecimal;


/**
 * CustomerOrderDeliveryDet represent an sku quantity and price  in particular
 * shipment. Currency can be obtained from order.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 *
 */
public interface CustomerOrderDeliveryDet extends Auditable, CartItem {

    /**
     * Get pk value.
     * @return pk value.
     */
    long getCustomerorderdeliveryId();

    /**
     * Set pk value.
     * @param customerorderdeliveryId pk value
     */
    void setCustomerorderdeliveryId(long customerorderdeliveryId);


    /**
     * Get quantity of sku.
     * @return  quantity of sku.
     */
    BigDecimal getQty();

    /**
     * Set  quantity of sku.
     * @param qty  quantity of sku.
     */
    void setQty(BigDecimal qty);

    /**
     * Get {@link ProductSku} sku
     * @return sku
     */
    ProductSku getSku();

    /**
     * Set sku.
     * @param sku product sku
     */
    void setSku(ProductSku sku);

    /**
     * Get order delivery.
     * @return delivery
     */
    CustomerOrderDelivery getDelivery();

    /**
     * Set delivery.
     * @param delivery delivery.
     */
    void setDelivery(CustomerOrderDelivery delivery);

    /**
     * Get sku price for this partucular order.
     * @return price.
     */
    BigDecimal getPrice();

    /**
     * Set Price.
     * @param price price to set.
     */
    void setPrice(BigDecimal price);

}
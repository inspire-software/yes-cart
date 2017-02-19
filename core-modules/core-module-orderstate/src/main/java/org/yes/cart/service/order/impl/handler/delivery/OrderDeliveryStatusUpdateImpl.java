package org.yes.cart.service.order.impl.handler.delivery;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * User: denispavlov
 * Date: 17/02/2017
 * Time: 12:13
 */
public class OrderDeliveryStatusUpdateImpl implements OrderDeliveryStatusUpdate {

    private final String orderNumber;
    private final String supplierCode;
    private final List<OrderDeliveryLineStatusUpdate> lineStatus;


    public OrderDeliveryStatusUpdateImpl(final String orderNumber,
                                         final String supplierCode,
                                         final List<OrderDeliveryLineStatusUpdate> lineStatus) {
        this.orderNumber = orderNumber;
        this.supplierCode = supplierCode;
        this.lineStatus = lineStatus;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public List<OrderDeliveryLineStatusUpdate> getLineStatus() {
        return lineStatus;
    }

    @Override
    public String toString() {
        return "OrderDeliveryStatusUpdateImpl{" +
                "orderNumber='" + orderNumber + '\'' +
                ", supplierCode='" + supplierCode + '\'' +
                ", lineStatus=" + lineStatus +
                '}';
    }
}

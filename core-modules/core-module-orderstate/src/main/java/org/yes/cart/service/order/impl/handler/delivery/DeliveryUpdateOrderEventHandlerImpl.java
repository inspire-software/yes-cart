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

package org.yes.cart.service.order.impl.handler.delivery;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.*;
import org.yes.cart.service.order.impl.OrderEventImpl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 16/02/2017
 * Time: 17:23
 */
public class DeliveryUpdateOrderEventHandlerImpl implements OrderEventHandler, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryUpdateOrderEventHandlerImpl.class);

    private OrderStateManager orderStateManager = null;
    private ApplicationContext applicationContext;

    private final WarehouseService warehouseService;

    private final SkuWarehouseService skuWarehouseService;

    private final ProductService productService;


    /**
     * Construct transition.
     *
     * @param warehouseService    warehouse service
     * @param skuWarehouseService sku on warehouse service to change quantity
     * @param productService      product service
     */
    public DeliveryUpdateOrderEventHandlerImpl(final WarehouseService warehouseService,
                                               final SkuWarehouseService skuWarehouseService,
                                               final ProductService productService) {
        this.warehouseService = warehouseService;
        this.skuWarehouseService = skuWarehouseService;
        this.productService = productService;
    }


    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private synchronized OrderStateManager getOrderStateManager() {
        if (orderStateManager == null) {
            orderStateManager = (OrderStateManager) applicationContext.getBean("orderStateManager");
        }
        return orderStateManager;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handle(final OrderEvent orderEvent) throws OrderException {

        synchronized (OrderEventHandler.syncMonitor) {

            final CustomerOrder customerOrder = orderEvent.getCustomerOrder();
            final OrderDeliveryStatusUpdate update = (OrderDeliveryStatusUpdate) orderEvent.getParams().get("update");

            LOG.info("Received delivery update for order {}, status {}:\n{}",
                    new Object[] { customerOrder.getOrdernum(), customerOrder.getOrderStatus(), update });

            if (update != null) {

                final Map<Long, OrderDeliveryLineStatusUpdate> detailsByPk = new HashMap<Long, OrderDeliveryLineStatusUpdate>();
                final Map<String, OrderDeliveryLineStatusUpdate> detailsBySKU = new HashMap<String, OrderDeliveryLineStatusUpdate>();

                for (final OrderDeliveryLineStatusUpdate lineUpdate : update.getLineStatus()) {

                    if (lineUpdate.getOrderLineRef() != null) {
                        detailsByPk.put(lineUpdate.getOrderLineRef(), lineUpdate);
                    }
                    if (lineUpdate.getSKU() != null) {
                        detailsBySKU.put(lineUpdate.getSKU(), lineUpdate);
                    }

                }

                final boolean allowDeliveryTransitions = CustomerOrder.ORDER_STATUS_IN_PROGRESS.equals(customerOrder.getOrderStatus()) ||
                        CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED.equals(customerOrder.getOrderStatus());


                if (update.getAdditionalData() != null) {
                    for (final Map.Entry<String, Pair<String, String>> data : update.getAdditionalData().entrySet()) {
                        if (data.getValue() != null) {
                            customerOrder.putValue(data.getKey(), data.getValue().getFirst(), data.getValue().getSecond());
                        } else {
                            customerOrder.putValue(data.getKey(), null, null);
                        }
                    }
                }

                for (final CustomerOrderDelivery delivery : customerOrder.getDelivery()) {

                    Date estimatedMin = null;
                    Date estimatedMax = null;
                    Date guaranteed = null;
                    Date confirmed = null;
                    boolean notGuaranteed = false;

                    boolean atLeastOneItemIsNotUpdated = false;

                    boolean allItemsDeliveredOrRejected = true;

                    // Apply updates to lines and work out estimated delivery time
                    for (final CustomerOrderDeliveryDet detail : delivery.getDetail()) {

                        OrderDeliveryLineStatusUpdate lineUpdate = detailsByPk.get(detail.getCustomerOrderDeliveryDetId());
                        if (lineUpdate == null) {
                            if (detail.getSupplierCode().equals(update.getSupplierCode())) {
                                lineUpdate = detailsBySKU.get(detail.getProductSkuCode());
                            }
                        }

                        if (lineUpdate != null) {

                            if (lineUpdate.getDeliveryConfirmed() != null) {
                                detail.setDeliveryConfirmed(lineUpdate.getDeliveryConfirmed());
                            } else {
                                if (lineUpdate.getDeliveryEstimatedMin() != null) {
                                    detail.setDeliveryEstimatedMin(lineUpdate.getDeliveryEstimatedMin());
                                }
                                if (lineUpdate.getDeliveryEstimatedMax() != null) {
                                    detail.setDeliveryEstimatedMax(lineUpdate.getDeliveryEstimatedMax());
                                }
                                if (lineUpdate.getDeliveryGuaranteed() != null) {
                                    detail.setDeliveryGuaranteed(lineUpdate.getDeliveryGuaranteed());
                                }
                            }

                            if (StringUtils.isNotBlank(lineUpdate.getOrderDeliveryStatus())) {
                                detail.setDeliveryRemarks(lineUpdate.getOrderDeliveryStatus());
                            }

                            if (lineUpdate.isRejected()) {
                                detail.setDeliveredQuantity(BigDecimal.ZERO);
                            } else if (lineUpdate.getDeliveredQty() != null) {
                                detail.setDeliveredQuantity(lineUpdate.getDeliveredQty());
                            }

                            if (StringUtils.isNotBlank(lineUpdate.getSupplierInvoiceNo())) {
                                detail.setSupplierInvoiceNo(lineUpdate.getSupplierInvoiceNo());
                            }
                            if (lineUpdate.getSupplierInvoiceDate() != null) {
                                detail.setSupplierInvoiceDate(lineUpdate.getSupplierInvoiceDate());
                            }

                            if (lineUpdate.getAdditionalData() != null) {
                                for (final Map.Entry<String, Pair<String, String>> data : lineUpdate.getAdditionalData().entrySet()) {
                                    if (data.getValue() != null) {
                                        detail.putValue(data.getKey(), data.getValue().getFirst(), data.getValue().getSecond());
                                    } else {
                                        detail.putValue(data.getKey(), null, null);
                                    }
                                }
                            }

                        } else {

                            // No estimates and not delivery info
                            if (detail.getDeliveryEstimatedMin() == null &&
                                    detail.getDeliveryEstimatedMax() == null &&
                                    detail.getDeliveryGuaranteed() == null &&
                                    detail.getDeliveryConfirmed() == null &&
                                    detail.getDeliveredQuantity() == null) {

                                atLeastOneItemIsNotUpdated = true;

                            }

                        }

                        estimatedMin = chooseLatestDate(estimatedMin, detail.getDeliveryEstimatedMin());
                        estimatedMax = chooseLatestDate(estimatedMax, detail.getDeliveryEstimatedMax());

                        if (guaranteed != null) {
                            notGuaranteed = !guaranteed.equals(detail.getDeliveryGuaranteed());
                        }

                        if (notGuaranteed) {
                            estimatedMin = chooseLatestDate(estimatedMin, guaranteed);
                            estimatedMax = chooseLatestDate(estimatedMax, guaranteed);
                        } else {
                            guaranteed = chooseLatestDate(guaranteed, detail.getDeliveryGuaranteed());
                        }

                        confirmed = chooseLatestDate(confirmed, detail.getDeliveryConfirmed());

                        if (detail.getDeliveredQuantity() == null) {
                            // All items MUST have confirmed quantity, which will allow this delivery to progress to
                            // SHIPPED, otherwise we keep it in packing (and it need be, this can be progressed manually
                            // from admin app)
                            allItemsDeliveredOrRejected = false;
                        }

                    }

                    if (estimatedMin != null || estimatedMax != null) {
                        notGuaranteed = true;
                    }

                    // Update estimated time on the delivery level
                    if (notGuaranteed) {
                        if (atLeastOneItemIsNotUpdated) {
                            // At least one item is not updated, so we only use min
                            delivery.setDeliveryEstimatedMin(estimatedMin);
                            delivery.setDeliveryEstimatedMax(null);
                            delivery.setDeliveryGuaranteed(null);
                        } else {
                            // All items have updates, so we use the best approximation values
                            delivery.setDeliveryEstimatedMin(estimatedMin);
                            delivery.setDeliveryEstimatedMax(estimatedMax);
                            delivery.setDeliveryGuaranteed(null);
                        }
                    } else {
                        // All lines have same guaranteed delivery date
                        delivery.setDeliveryEstimatedMin(null);
                        delivery.setDeliveryEstimatedMax(null);
                        delivery.setDeliveryGuaranteed(guaranteed);
                    }
                    if (allItemsDeliveredOrRejected) {
                        delivery.setDeliveryConfirmed(confirmed);
                    }

                    LOG.info("Attempting to auto-progress delivery {}. Status: {}",
                            customerOrder.getOrdernum(), delivery.getDeliveryStatus());

                    // Only transition orders that are in progress to avoid breaking the order state flow
                    // Updates are only allowed if we have a confirmed/paid order which has not been fully shipping
                    // Otherwise we only update line information, any manual intervention can still be done from admin app
                    if (allowDeliveryTransitions) {

                        // Transitions delivery if necessary. Since this is automated route we ignore all allocations block (date/inventory).
                        // If the updates are coming through it means that the order is accepted and it is suppliers responsibility
                        // to fulfil it. If supplier cannot fulfil the order, rejection flags can be sent later.
                        if (CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED.equals(delivery.getDeliveryStatus()) ||
                                CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT.equals(delivery.getDeliveryStatus()) ||
                                CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT.equals(delivery.getDeliveryStatus()) ||
                                CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(delivery.getDeliveryStatus())) {

                            // We only void reservation, since this is automated integration flow
                            voidReservation(delivery);
                            delivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

                            // We are sending updates, so we are packing the order be it part or full.
                            getOrderStateManager().fireTransition(new OrderEventImpl(OrderStateManager.EVT_RELEASE_TO_PACK, customerOrder, delivery));

                            LOG.info("Delivery {} was pending allocation, reservation was voided and delivery released to pack. Status: {}",
                                    customerOrder.getOrdernum(), delivery.getDeliveryStatus());

                        }

                        // We already had items allocated (standard inventory flow)
                        if (CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED.equals(delivery.getDeliveryStatus())) {

                            // We are sending updates, so we are packing the order be it part or full.
                            getOrderStateManager().fireTransition(new OrderEventImpl(OrderStateManager.EVT_RELEASE_TO_PACK, customerOrder, delivery));

                            LOG.info("Delivery {} was allocated, delivery released to pack. Status: {}",
                                    customerOrder.getOrdernum(), delivery.getDeliveryStatus());

                        }

                        // If we are packing it means that we have had partial update already, and if this time all lines
                        // have relevant statuses we can progress further up to shipping completed.
                        if (CustomerOrderDelivery.DELIVERY_STATUS_PACKING.equals(delivery.getDeliveryStatus()) &&
                                allItemsDeliveredOrRejected) {

                            // This update confirms that all items are delivered, so we went from PACK->READY->SHIPPED
                            // PACK->READY (payment taken)
                            getOrderStateManager().fireTransition(new OrderEventImpl(OrderStateManager.EVT_RELEASE_TO_SHIPMENT, customerOrder, delivery));

                            LOG.info("Delivery {} was in packing, all lines updated, releasing to shipment to take payment. Status: {}",
                                    customerOrder.getOrdernum(), delivery.getDeliveryStatus());

                            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS.equals(delivery.getDeliveryStatus())) {
                                // READY->SHIPPED
                                getOrderStateManager().fireTransition(new OrderEventImpl(OrderStateManager.EVT_SHIPMENT_COMPLETE, customerOrder, delivery));

                                LOG.info("Delivery {} was in shipping (payment successful), marking as complete. Status: {}",
                                        customerOrder.getOrdernum(), delivery.getDeliveryStatus());

                            } // else something is wrong with payment, so we leave for manual handling
                        }

                        // ELSE for all other statuses we only update the line statuses and wait until all lines have them
                    }

                }

                LOG.info("Finished delivery update for order {}, status {}:\n{}",
                        new Object[]{customerOrder.getOrdernum(), customerOrder.getOrderStatus(), update});

                return true; // Return true to indicate that we want to save changes.
            }

            return false; // no update
        }
    }

    Date chooseLatestDate(final Date current, final Date newCandidate) {
        if (current == null) {
            return newCandidate;
        }
        if (newCandidate == null) {
            return current;
        }
        if (newCandidate.after(current)) {
            return newCandidate;
        }
        return current;
    }


    /**
     * Allocate sku quantity on warehouses, that belong to shop, where order was made.
     *
     * @param orderDelivery reserve for this delivery
     * @throws OrderItemAllocationException in case if can not allocate quantity for each sku
     */
    void voidReservation(final CustomerOrderDelivery orderDelivery) throws OrderItemAllocationException {

        if (!CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(orderDelivery.getDeliveryGroup())) {

            final Collection<CustomerOrderDeliveryDet> deliveryDetails = orderDelivery.getDetail();

            final Map<String, Warehouse> warehouseByCode = warehouseService.getByShopIdMapped(
                    orderDelivery.getCustomerOrder().getShop().getShopId(), false);

            for (CustomerOrderDeliveryDet det : deliveryDetails) {

                final Product product = productService.getProductBySkuCode(det.getProductSkuCode());

                if (product == null || Product.AVAILABILITY_ALWAYS != product.getAvailability()) {

                    final String skuCode = det.getProductSkuCode();
                    final BigDecimal toAllocate = det.getQty();
                    final Warehouse selected = warehouseByCode.get(det.getSupplierCode());

                    if (selected == null) {
                        LOG.error(
                                "Warehouse is not found for delivery detail {}:{}",
                                orderDelivery.getDeliveryNum(), det.getProductSkuCode()
                        );

                        /**
                         * For allocation we always must have stock items with inventory supported availability
                         */
                        throw new OrderItemAllocationException(
                                skuCode,
                                toAllocate,
                                "ProcessAllocationOrderEventHandlerImpl. Can not allocate total qty = " + det.getQty()
                                        + " for sku = " + skuCode
                                        + " in delivery " + orderDelivery.getDeliveryNum());
                    }

                    // At this point we should have reserved the quantity, so we just releasing the reservation
                    skuWarehouseService.voidReservation(selected, skuCode, toAllocate);

                }
            }
        }

        orderDelivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

    }

}

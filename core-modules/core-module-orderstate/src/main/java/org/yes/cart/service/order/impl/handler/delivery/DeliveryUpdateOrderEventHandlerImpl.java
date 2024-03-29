/*
 * Copyright 2009 Inspire-Software.com
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.*;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.order.impl.handler.AbstractEventHandlerImpl;
import org.yes.cart.shoppingcart.InventoryResolver;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.log.Markers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 16/02/2017
 * Time: 17:23
 */
public class DeliveryUpdateOrderEventHandlerImpl extends AbstractEventHandlerImpl implements OrderEventHandler, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryUpdateOrderEventHandlerImpl.class);

    private OrderStateManager orderStateManager = null;
    private ApplicationContext applicationContext;

    private final WarehouseService warehouseService;

    private final InventoryResolver inventoryResolver;

    private final ProductService productService;


    /**
     * Construct transition.
     *
     * @param warehouseService    warehouse service
     * @param inventoryResolver   sku on warehouse service to change quantity
     * @param productService      product service
     */
    public DeliveryUpdateOrderEventHandlerImpl(final WarehouseService warehouseService,
                                               final InventoryResolver inventoryResolver,
                                               final ProductService productService) {
        this.warehouseService = warehouseService;
        this.inventoryResolver = inventoryResolver;
        this.productService = productService;
    }


    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public boolean handle(final OrderEvent orderEvent) throws OrderException {

        synchronized (OrderEventHandler.syncMonitor) {

            final CustomerOrder customerOrder = orderEvent.getCustomerOrder();
            final OrderDeliveryStatusUpdate update = (OrderDeliveryStatusUpdate) orderEvent.getParams().get("update");

            LOG.info("Received delivery update for order {}, status {}:\n{}",
                    customerOrder.getOrdernum(), customerOrder.getOrderStatus(), update);

            if (update != null) {

                final Map<Long, OrderDeliveryLineStatusUpdate> detailsByPk = new HashMap<>();
                final Map<String, List<OrderDeliveryLineStatusUpdate>> detailsBySKU = new HashMap<>();

                for (final OrderDeliveryLineStatusUpdate lineUpdate : update.getLineStatus()) {

                    if (lineUpdate.getOrderLineRef() != null) {
                        detailsByPk.put(lineUpdate.getOrderLineRef(), lineUpdate);
                    } else if (lineUpdate.getSKU() != null) {
                        final List<OrderDeliveryLineStatusUpdate> skuUpdates = detailsBySKU.computeIfAbsent(lineUpdate.getSKU(), k -> new ArrayList<>());
                        skuUpdates.add(lineUpdate);
                    }

                }

                final boolean allowDeliveryTransitions = CustomerOrder.ORDER_STATUS_IN_PROGRESS.equals(customerOrder.getOrderStatus()) ||
                        CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED.equals(customerOrder.getOrderStatus());


                if (update.getAdditionalData() != null) {
                    for (final Map.Entry<String, Pair<String, I18NModel>> data : update.getAdditionalData().entrySet()) {
                        if (data.getValue() != null) {
                            customerOrder.putValue(data.getKey(), data.getValue().getFirst(), data.getValue().getSecond());
                        } else {
                            customerOrder.putValue(data.getKey(), null, null);
                        }
                    }
                }

                for (final CustomerOrderDelivery delivery : customerOrder.getDelivery()) {

                    LocalDateTime estimatedMin = null;
                    LocalDateTime estimatedMax = null;
                    LocalDateTime guaranteed = null;
                    LocalDateTime confirmed = null;
                    boolean notGuaranteed = false;

                    boolean noItemUpdated = true;
                    boolean atLeastOneItemIsNotUpdated = false;
                    boolean allItemsDeliveredOrRejected = true;
                    boolean preventTransition = false;

                    final String deliveryNumber = update.getDeliveryNumber();

                    if (deliveryNumber == null || deliveryNumber.equals(delivery.getDeliveryNum())) {

                        LOG.info("Updating delivery details for delivery {} for order {}. update delivery number: {} (if number is not specified all deliveries are considered)",
                                delivery.getDeliveryNum(), customerOrder.getOrdernum(), deliveryNumber);

                        // Apply updates to lines and work out estimated delivery time
                        for (final CustomerOrderDeliveryDet detail : delivery.getDetail()) {

                            // match by line ID
                            OrderDeliveryLineStatusUpdate lineUpdate = detailsByPk.get(detail.getCustomerOrderDeliveryDetId());
                            if (lineUpdate == null) {
                                // otherwise, if supplier is matching
                                if (detail.getSupplierCode().equals(update.getSupplierCode())) {
                                    // check candidate line updates by SKU
                                    final List<OrderDeliveryLineStatusUpdate> candidates = detailsBySKU.get(detail.getProductSkuCode());
                                    if (CollectionUtils.isNotEmpty(candidates)) {
                                        for (final OrderDeliveryLineStatusUpdate candidate : candidates) {
                                            // soft select candidate if ordered quantity is undetermined
                                            if (candidate.getOrderedQty() == null && lineUpdate == null) {
                                                lineUpdate = candidate;
                                            } else if (MoneyUtils.isFirstEqualToSecond(candidate.getOrderedQty(), detail.getQty())) {
                                                // or if we have quantity match the we pick this line
                                                lineUpdate = candidate;
                                                break;
                                            }
                                        }
                                        if (lineUpdate != null) {
                                            // remove matched line update from available updates list
                                            candidates.remove(lineUpdate);
                                            if (candidates.isEmpty()) {
                                                detailsBySKU.remove(detail.getProductSkuCode());
                                            }
                                        }
                                    }
                                }
                            } else {
                                // this update is only for this line, remove it from available updates
                                detailsByPk.remove(detail.getCustomerOrderDeliveryDetId());
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
                                    for (final Map.Entry<String, Pair<String, I18NModel>> data : lineUpdate.getAdditionalData().entrySet()) {
                                        if (data.getValue() != null) {
                                            detail.putValue(data.getKey(), data.getValue().getFirst(), data.getValue().getSecond());
                                        } else {
                                            detail.putValue(data.getKey(), null, null);
                                        }
                                    }
                                }

                                noItemUpdated = false;

                            } else {

                                // No estimates and no delivery info
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
                                // SHIPPED, otherwise we keep it in packing (and if need be, this can be progressed manually
                                // from admin app)
                                allItemsDeliveredOrRejected = false;
                            }

                        }

                        if (!noItemUpdated) {

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

                        }

                    } else { // deliveryNumber is provided but not matching the delivery
                        // Need to prevent this delivery from progressing
                        allItemsDeliveredOrRejected = false;
                        preventTransition = true;
                    }

                    // Only transition orders that are in progress to avoid breaking the order state flow
                    // Updates are only allowed if we have a confirmed/paid order which has not been fully shipping
                    // Otherwise we only update line information, any manual intervention can still be done from admin app
                    if (allowDeliveryTransitions && !preventTransition && !noItemUpdated) {

                        LOG.info("Attempting to auto-progress delivery {} for order {}. Status: {}",
                                delivery.getDeliveryNum(), customerOrder.getOrdernum(), delivery.getDeliveryStatus());

                        // Transitions delivery if necessary. Since this is automated route we ignore all allocations block (date/inventory).
                        // If the updates are coming through it means that the order is accepted and it is suppliers responsibility
                        // to fulfil it. If supplier cannot fulfil the order, rejection flags can be sent later.
                        if (CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED.equals(delivery.getDeliveryStatus()) ||
                                CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT.equals(delivery.getDeliveryStatus()) ||
                                CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT.equals(delivery.getDeliveryStatus()) ||
                                CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT.equals(delivery.getDeliveryStatus())) {

                            // We only void reservation, since this is automated integration flow
                            voidReservation(orderEvent, delivery);

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
                    } else {

                        LOG.info("NOT attempting to auto-progress delivery {} for order {}. Status: {}. Allow transitions: {}, prevent transition: {}",
                                delivery.getDeliveryNum(), customerOrder.getOrdernum(), delivery.getDeliveryStatus(),
                                allowDeliveryTransitions, preventTransition);

                    }

                }

                if (!detailsByPk.isEmpty()) {
                    LOG.error(Markers.alert(), "Not all line specific updates could be processed: {}", detailsByPk);
                }
                if (!detailsBySKU.isEmpty()) {
                    LOG.error(Markers.alert(), "Not all SKU updates could be processed: {}", detailsBySKU);
                }

                LOG.info("Finished delivery update for order {}, status {}:\n{}",
                        customerOrder.getOrdernum(), customerOrder.getOrderStatus(), update);

                return true; // Return true to indicate that we want to save changes.
            }

            return false; // no update
        }
    }

    LocalDateTime chooseLatestDate(final LocalDateTime current, final LocalDateTime newCandidate) {
        if (current == null) {
            return newCandidate;
        }
        if (newCandidate == null) {
            return current;
        }
        if (newCandidate.isAfter(current)) {
            return newCandidate;
        }
        return current;
    }


    /**
     * Allocate sku quantity on warehouses, that belong to shop, where order was made.
     *
     * @param orderEvent    event
     * @param orderDelivery reserve for this delivery
     *
     * @throws OrderItemAllocationException in case if can not allocate quantity for each sku
     */
    void voidReservation(final OrderEvent orderEvent, final CustomerOrderDelivery orderDelivery) throws OrderItemAllocationException {

        if (!CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(orderDelivery.getDeliveryGroup())) {

            final Collection<CustomerOrderDeliveryDet> deliveryDetails = orderDelivery.getDetail();

            final Map<String, Warehouse> warehouseByCode = warehouseService.getByShopIdMapped(
                    orderDelivery.getCustomerOrder().getShop().getShopId(), false);

            for (CustomerOrderDeliveryDet det : deliveryDetails) {

                final String skuCode = det.getProductSkuCode();
                final BigDecimal toAllocate = det.getQty();
                final Warehouse selected = warehouseByCode.get(det.getSupplierCode());

                if (selected == null) {
                    LOG.warn(
                            Markers.alert(),
                            "Warehouse is not found for delivery detail {}:{}",
                            orderDelivery.getDeliveryNum(), det.getProductSkuCode()
                    );
                } else {
                    // At this point we should have reserved the quantity, so we just releasing the reservation
                    inventoryResolver.voidReservation(selected, skuCode, toAllocate);
                }
            }
        }

        transition(orderEvent, orderEvent.getCustomerOrder(), orderDelivery,
                CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED);

    }

}

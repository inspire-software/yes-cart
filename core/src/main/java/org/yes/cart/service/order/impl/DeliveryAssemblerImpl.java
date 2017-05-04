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

package org.yes.cart.service.order.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.Assert;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.domain.entity.CustomerOrderDet;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.*;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Order delivery assembler responsible for shipment creation.
 * Order delivery can be split by several reasons like security,
 * different products availability, inventory, etc.
 * Default delivery assembler can split shipments as shown in table
 * <table>
 * <tr><td> Availability &rarr;
 * <br> Inventory &darr;           </td><td>Pre Order        </td><td>Back Order</td><td>Standard</td> <td>Always</td></tr>
 * <tr><td> Inventory available             </td><td>D1(note 1) or D2 </td><td>D1        </td><td>D1      </td> <td>D4    </td></tr>
 * <tr><td> No Inventory available          </td><td>D3(note 2) or D2 </td><td>D3        </td><td>D3      </td> <td>D4    </td></tr>
 * <p/>
 * </table>
 * <p/>
 * Delivery group 1 - can be shipped
 * Delivery group 2 - awaiting for date, than check inventory
 * Delivery group 3 - awaiting for inventory
 * Delivery group 4 - electronic delivery
 * <p/>
 * Note 1 - in case if current date more that product start availability date and inventory available
 * Note 2 - in case if current date more that product start availability date and no inventory
 * <p/>
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DeliveryAssemblerImpl implements DeliveryAssembler {


    private final EntityFactory entityFactory;
    private final CarrierSlaService carrierSlaService;
    private final OrderSplittingStrategy orderSplittingStrategy;

    public DeliveryAssemblerImpl(final WarehouseService warehouseService,
                                 final CarrierSlaService carrierSlaService,
                                 final OrderSplittingStrategy orderSplittingStrategy) {
        this.orderSplittingStrategy = orderSplittingStrategy;
        this.entityFactory = warehouseService.getGenericDao().getEntityFactory();
        this.carrierSlaService = carrierSlaService;
    }


    /**
     * Create the order delivery or multiple deliveries.
     *
     * @param order        given order
     * @param shoppingCart cart
     * @return order with filled delivery
     */
    public CustomerOrder assembleCustomerOrder(final CustomerOrder order,
                                               final ShoppingCart shoppingCart,
                                               final Map<String, Boolean> onePhysicalDelivery) throws OrderAssemblyException {


        final Map<DeliveryBucket, List<CustomerOrderDet>> groups = getDeliveryGroups(order, onePhysicalDelivery);

        int idx = 0;

        for (Map.Entry<DeliveryBucket, List<CustomerOrderDet>> entry : groups.entrySet()) {

            final List<CustomerOrderDet> items = entry.getValue();

            final CustomerOrderDelivery customerOrderDelivery = createOrderDelivery(
                    order,
                    shoppingCart,
                    items,
                    entry.getKey(),
                    idx);

            if (CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(entry.getKey().getGroup())) {
                // this is electronic delivery
                customerOrderDelivery.setPrice(BigDecimal.ZERO);
                customerOrderDelivery.setListPrice(BigDecimal.ZERO);
                customerOrderDelivery.setPromoApplied(false);
                customerOrderDelivery.setAppliedPromo(null);
                customerOrderDelivery.setNetPrice(BigDecimal.ZERO);
                customerOrderDelivery.setGrossPrice(BigDecimal.ZERO);
                customerOrderDelivery.setTaxCode("");
                customerOrderDelivery.setTaxRate(BigDecimal.ZERO);

            } else {

                final Total cartTotal = shoppingCart.getTotal();

                final String shippingSlaGUID = customerOrderDelivery.getCarrierSla().getGuid();
                final int index = shoppingCart.indexOfShipping(shippingSlaGUID, entry.getKey());
                final CartItem shipping = index > -1 ? shoppingCart.getShippingList().get(index) : null;

                if (cartTotal == null
                        || shipping == null
                        || shipping.getListPrice() == null
                        || shipping.getPrice() == null
                        || shipping.getNetPrice() == null
                        || shipping.getGrossPrice() == null
                        || shipping.getTaxRate() == null
                        || shipping.getTaxCode() == null) {
                    throw new OrderAssemblyException("No delivery total for: " + entry.getKey());
                }

                customerOrderDelivery.setPrice(shipping.getPrice());
                customerOrderDelivery.setListPrice(shipping.getListPrice());
                customerOrderDelivery.setPromoApplied(shipping.isPromoApplied());
                customerOrderDelivery.setAppliedPromo(shipping.getAppliedPromo());
                customerOrderDelivery.setNetPrice(shipping.getNetPrice());
                customerOrderDelivery.setGrossPrice(shipping.getGrossPrice());
                customerOrderDelivery.setTaxCode(shipping.getTaxCode());
                customerOrderDelivery.setTaxRate(shipping.getTaxRate());
                customerOrderDelivery.setTaxExclusiveOfPrice(shipping.isTaxExclusiveOfPrice());

                final String requestedDateKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + customerOrderDelivery.getCarrierSla().getCarrierslaId() + entry.getKey().getSupplier();
                final long requestedDate = NumberUtils.toLong(shoppingCart.getOrderInfo().getDetailByKey(requestedDateKey), 0);
                if (requestedDate > System.currentTimeMillis()) {
                    customerOrderDelivery.setRequestedDeliveryDate(new Date(requestedDate));
                }


            }
            order.getDelivery().add(customerOrderDelivery);

            idx++;

        }


        return order;

    }

    /**
     * Create and add delivery detail to delivery.
     *
     * @param customerOrderDelivery delivery
     * @param orderDet              order detail
     */
    private void fillDeliveryDetail(final CustomerOrderDelivery customerOrderDelivery, final CustomerOrderDet orderDet) {

        final CustomerOrderDeliveryDet deliveryDet = entityFactory.getByIface(CustomerOrderDeliveryDet.class);

        deliveryDet.setDelivery(customerOrderDelivery);
        customerOrderDelivery.getDetail().add(deliveryDet);
        deliveryDet.setQty(orderDet.getQty());
        deliveryDet.setProductSkuCode(orderDet.getProductSkuCode());
        deliveryDet.setProductName(orderDet.getProductName());
        deliveryDet.setSupplierCode(orderDet.getSupplierCode());
        deliveryDet.setPrice(orderDet.getPrice());
        deliveryDet.setSalePrice(orderDet.getSalePrice());
        deliveryDet.setListPrice(orderDet.getListPrice());
        deliveryDet.setGift(orderDet.isGift());
        deliveryDet.setPromoApplied(orderDet.isPromoApplied());
        deliveryDet.setFixedPrice(orderDet.isFixedPrice());
        deliveryDet.setAppliedPromo(orderDet.getAppliedPromo());
        deliveryDet.setNetPrice(orderDet.getNetPrice());
        deliveryDet.setGrossPrice(orderDet.getGrossPrice());
        deliveryDet.setTaxCode(orderDet.getTaxCode());
        deliveryDet.setTaxRate(orderDet.getTaxRate());
        deliveryDet.setTaxExclusiveOfPrice(orderDet.isTaxExclusiveOfPrice());
        deliveryDet.setAllValues(orderDet.getAllValues());
        deliveryDet.setB2bRemarks(orderDet.getB2bRemarks());

    }

    /**
     * Create order delivery.
     *
     * @param order         delivery.
     * @param shoppingCart  shopping cart.
     * @param items         skus in this delivery
     * @param deliveryGroup delivery group
     * @param idx           index.
     * @return created order delivery, that has not filled details.
     */
    private CustomerOrderDelivery createOrderDelivery(final CustomerOrder order,
                                                      final ShoppingCart shoppingCart,
                                                      final List<CustomerOrderDet> items,
                                                      final DeliveryBucket deliveryGroup,
                                                      final int idx) {
        Assert.notNull(order, "Expecting order, but found null");
        Assert.notNull(shoppingCart, "Expecting shopping cart, but found null");
        final CustomerOrderDelivery customerOrderDelivery = entityFactory.getByIface(CustomerOrderDelivery.class);
        if (!shoppingCart.getCarrierSlaId().isEmpty()) {
            final Long carrierSlaId = shoppingCart.getCarrierSlaId().get(deliveryGroup.getSupplier());
            customerOrderDelivery.setCarrierSla(carrierSlaId != null ? carrierSlaService.getById(carrierSlaId) : null);
        } else {
            customerOrderDelivery.setCarrierSla(null);
        }

        customerOrderDelivery.setDeliveryNum(order.getOrdernum() + "-" + idx);
        customerOrderDelivery.setDeliveryGroup(deliveryGroup.getGroup());

        customerOrderDelivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT);
        customerOrderDelivery.setCustomerOrder(order);

        for (CustomerOrderDet orderDet : items) {
            fillDeliveryDetail(customerOrderDelivery, orderDet);
        }

        return customerOrderDelivery;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Boolean> isOrderMultipleDeliveriesAllowed(final CustomerOrder order) {

        return orderSplittingStrategy.isMultipleDeliveriesAllowed(order.getShop().getShopId(),
                new ArrayList<CartItem>(order.getOrderDetail()));

    }


    /**
     * Delivery sets determination.
     *
     * @param order               given order
     * @param onePhysicalDelivery true if need to create one physical delivery.
     * @return true in case if order can has single delivery.
     */
    Map<DeliveryBucket, List<CustomerOrderDet>> getDeliveryGroups(final CustomerOrder order,
                                                                  final Map<String, Boolean> onePhysicalDelivery)
            throws SkuUnavailableException {

        final Map<DeliveryBucket, List<CustomerOrderDet>> buckets =
            (Map) orderSplittingStrategy.determineDeliveryBuckets(order.getShop().getShopId(),
                new ArrayList<CartItem>(order.getOrderDetail()), onePhysicalDelivery);

        for (final Map.Entry<DeliveryBucket, List<CustomerOrderDet>> bucket : buckets.entrySet()) {

            if (CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP.equals(bucket.getKey().getGroup())) {

                final CustomerOrderDet first = bucket.getValue().get(0);

                throw new SkuUnavailableException(first.getProductSkuCode(), first.getProductName(), true);

            } else if (CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP.equals(bucket.getKey().getGroup())) {

                final CustomerOrderDet first = bucket.getValue().get(0);

                throw new SkuUnavailableException(first.getProductSkuCode(), first.getProductName(), false);

            }

        }

        return buckets;

    }

}

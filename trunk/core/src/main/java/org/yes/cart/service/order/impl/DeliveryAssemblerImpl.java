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

package org.yes.cart.service.order.impl;

import org.springframework.util.Assert;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.DeliveryAssembler;
import org.yes.cart.service.order.OrderAssemblyException;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.*;

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
    private final WarehouseService warehouseService;
    private final SkuWarehouseService skuWarehouseService;
    private final CarrierSlaService carrierSlaService;
    private final ProductService productService;

    public DeliveryAssemblerImpl(final WarehouseService warehouseService,
                                 final SkuWarehouseService skuWarehouseService,
                                 final CarrierSlaService carrierSlaService,
                                 final ProductService productService) {
        this.productService = productService;
        this.entityFactory = warehouseService.getGenericDao().getEntityFactory();
        this.warehouseService = warehouseService;
        this.skuWarehouseService = skuWarehouseService;
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
                                               final boolean onePhysicalDelivery) throws OrderAssemblyException {


        final Map<String, List<CustomerOrderDet>> groups = getDeliveryGroups(order, onePhysicalDelivery);

        int idx = 0;

        for (Map.Entry<String, List<CustomerOrderDet>> entry : groups.entrySet()) {

            final List<CustomerOrderDet> items = entry.getValue();

            final CustomerOrderDelivery customerOrderDelivery = createOrderDelivery(
                    order,
                    shoppingCart,
                    items,
                    entry.getKey(),
                    idx);

            if (order.getCustomer() == null || CustomerOrderDelivery.ELECTONIC_DELIVERY_GROUP.equals(entry.getKey())) {
                // this is electronic delivery
                customerOrderDelivery.setPrice(BigDecimal.ZERO);
                customerOrderDelivery.setListPrice(BigDecimal.ZERO);
                customerOrderDelivery.setPromoApplied(false);
                customerOrderDelivery.setAppliedPromo(null);

            } else {

                customerOrderDelivery.setPrice(shoppingCart.getTotal().getDeliveryCost());
                customerOrderDelivery.setListPrice(shoppingCart.getTotal().getDeliveryListCost());
                customerOrderDelivery.setPromoApplied(shoppingCart.getTotal().isDeliveryPromoApplied());
                customerOrderDelivery.setAppliedPromo(shoppingCart.getTotal().getAppliedDeliveryPromo());

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
        deliveryDet.setPrice(orderDet.getPrice());
        deliveryDet.setSalePrice(orderDet.getSalePrice());
        deliveryDet.setListPrice(orderDet.getListPrice());
        deliveryDet.setGift(orderDet.isGift());
        deliveryDet.setPromoApplied(orderDet.isPromoApplied());
        deliveryDet.setAppliedPromo(orderDet.getAppliedPromo());
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
                                                      final String deliveryGroup,
                                                      final int idx) {
        Assert.notNull(order, "Expecting order, but found null");
        Assert.notNull(shoppingCart, "Expecting shopping cart, but found null");
        final CustomerOrderDelivery customerOrderDelivery = entityFactory.getByIface(CustomerOrderDelivery.class);
        customerOrderDelivery.setCarrierSla(carrierSlaService.findById(shoppingCart.getCarrierSlaId() == null ? 0 : shoppingCart.getCarrierSlaId()));
        customerOrderDelivery.setDeliveryNum(order.getOrdernum() + "-" + idx);
        customerOrderDelivery.setDeliveryGroup(deliveryGroup);

        customerOrderDelivery.setDeliveryStatus(CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT);
        customerOrderDelivery.setCustomerOrder(order);

        for (CustomerOrderDet orderDet : items) {
            fillDeliveryDetail(customerOrderDelivery, orderDet);
        }

        return customerOrderDelivery;
    }

    /**
     * Is order can be with multiple deliveries.
     *
     * @param order given order
     * @return true in case if order can has multiple physical deliveries.
     */
    public boolean isOrderMultipleDeliveriesAllowed(final CustomerOrder order) {

        final Map<String, List<CustomerOrderDet>> deliveryGroups = getDeliveryGroups(order, false);

        return (getPhysicalDeliveriesQty(deliveryGroups) > 1);

    }


    /**
     * Delivery sets determination.
     *
     * @param order               given order
     * @param onePhysicalDelivery true if need to create one physical delivery.
     * @return true in case if order can has single delivery.
     */
    Map<String, List<CustomerOrderDet>> getDeliveryGroups(final CustomerOrder order, final boolean onePhysicalDelivery) {

        final List<Warehouse> warehouses = warehouseService.getByShopId(order.getShop().getShopId());

        final Map<String, List<CustomerOrderDet>> deliveryGroups = new HashMap<String, List<CustomerOrderDet>>();

        for (CustomerOrderDet customerOrderDet : order.getOrderDetail()) {

            final Pair<BigDecimal, BigDecimal> quantity = skuWarehouseService.getQuantity(
                    warehouses,
                    customerOrderDet.getProductSkuCode()
            );

            // qty on warehouse minus reserved qty.
            // actual reservation when payment was successful or courier payment gw was selected for payment.
            final BigDecimal rest = MoneyUtils.notNull(quantity.getFirst(), BigDecimal.ZERO)
                    .add(MoneyUtils.notNull(quantity.getSecond(), BigDecimal.ZERO).negate());

            final String deliveryGroup = getDeliveryGroup(rest, customerOrderDet);

            if (!deliveryGroups.containsKey(deliveryGroup)) {
                deliveryGroups.put(deliveryGroup, new ArrayList<CustomerOrderDet>());
            }

            deliveryGroups.get(deliveryGroup).add(customerOrderDet);

        }

        if (onePhysicalDelivery) {

            final int deliveryQty = getPhysicalDeliveriesQty(deliveryGroups);

            if (deliveryQty == 1) {

                return deliveryGroups;

            } else if (deliveryQty > 1) {
                final List<String> removeGroups = new ArrayList<String>();
                final List<CustomerOrderDet> collector = new ArrayList<CustomerOrderDet>(5);
                deliveryGroups.put(
                        CustomerOrderDelivery.MIX_DELIVERY_GROUP,
                        collector
                );


                for (Map.Entry<String, List<CustomerOrderDet>> entry : deliveryGroups.entrySet()) {
                    if (
                            !(CustomerOrderDelivery.ELECTONIC_DELIVERY_GROUP.equals(entry.getKey())
                                    ||
                                    CustomerOrderDelivery.MIX_DELIVERY_GROUP.equals(entry.getKey()))
                            ) {
                        removeGroups.add(entry.getKey());
                        collector.addAll(entry.getValue());

                    }
                }

                deliveryGroups.keySet().removeAll(removeGroups);

            }


        }

        return deliveryGroups;
    }


    private int getPhysicalDeliveriesQty(final Map<String, List<CustomerOrderDet>> deliveryMap) {
        int qty = deliveryMap.size();
        if (deliveryMap.get(CustomerOrderDelivery.ELECTONIC_DELIVERY_GROUP) != null) {
            qty--;
        }
        return qty;
    }


    /**
     * Get the delivery group of product sku.
     * <p/>
     * All physical goods, that has not limitation by quantity or availability
     * will be collected into {@link CustomerOrderDelivery}#STANDARD_DELIVERY_GROUP
     *
     * @param rest             not reserved quantity on warehouses
     * @param customerOrderDet order detail record
     * @return delivery group label
     */
    String getDeliveryGroup(final BigDecimal rest, final CustomerOrderDet customerOrderDet) {

        final Date now = new Date(); //TODO: V2 time machine

        final Product product = productService.getProductBySkuCode(customerOrderDet.getProductSkuCode());
        final int availability = product.getAvailability();

        if (availability == Product.AVAILABILITY_ALWAYS) {

            final Date availableFrom = product.getAvailablefrom();
            if ((availableFrom != null)
                    &&
                    (now.getTime() < availableFrom.getTime())) {

                return CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP;
            }

            if (product.getProducttype().isDigital()) {
                return CustomerOrderDelivery.ELECTONIC_DELIVERY_GROUP;
            }
            return CustomerOrderDelivery.STANDARD_DELIVERY_GROUP;
        }

        if (MoneyUtils.isFirstBiggerThanSecond(rest, BigDecimal.ZERO)) {
            //inventory available
            if (availability == Product.AVAILABILITY_PREORDER) {
                final Date availableFrom = product.getAvailablefrom();
                if ((availableFrom != null) && (now.getTime() < availableFrom.getTime())) {
                    return CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP;
                }
            }
            if (MoneyUtils.isFirstBiggerThanSecond(customerOrderDet.getQty(), rest)) {
                return CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP; //we can not cover all ordered qty from warehouses. TODO: V2 here possible additional split
            }
            return CustomerOrderDelivery.STANDARD_DELIVERY_GROUP;
        } else {
            //inventory NOT available
            if (availability == Product.AVAILABILITY_PREORDER) {
                final Date availableFrom = product.getAvailablefrom();
                if ((availableFrom != null) && (now.getTime() < availableFrom.getTime())) {
                    return CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP;
                }
            }
            return CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP;

        }


    }

}

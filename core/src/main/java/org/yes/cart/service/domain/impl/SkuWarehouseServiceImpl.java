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

package org.yes.cart.service.domain.impl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SkuWarehouseServiceImpl extends BaseGenericServiceImpl<SkuWarehouse> implements SkuWarehouseService, ApplicationContextAware {

    private ProductService productService;

    private ApplicationContext applicationContext;

    private OrderStateManager orderStateManager;

    private CustomerOrderService customerOrderService;


    /**
     * Construct sku warehouse service.
     *
     * @param genericDao dao to use.
     */
    public SkuWarehouseServiceImpl(
            final GenericDAO<SkuWarehouse, Long> genericDao) {
        super(genericDao);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "skuWarehouseService-productSkusOnWarehouse")
    public List<SkuWarehouse> getProductSkusOnWarehouse(final long productId, final long warehouseId) {
        return getGenericDao().findByNamedQuery(
                "SKUS.ON.WAREHOUSE",
                productId,
                warehouseId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "skuWarehouseService-productOnWarehouse")
    public Map<String, BigDecimal> getProductAvailableToSellQuantity(final Product product, final Collection<Warehouse> warehouses) {

        final Map<String, BigDecimal> qty = new HashMap<String, BigDecimal>();
        for (final ProductSku sku : product.getSku()) {
            qty.put(sku.getCode(), BigDecimal.ZERO);
        }

        if (qty.isEmpty() || CollectionUtils.isEmpty(warehouses)) {
            return qty;
        }

        final List<Long> whIds = new ArrayList<Long>();
        for (final Warehouse wh : warehouses) {
            whIds.add(wh.getWarehouseId());
        }

        final List<Object[]> skuQtyList = getGenericDao().findQueryObjectsByNamedQuery(
                "PRODUCT.SKU.QTY.ON.WAREHOUSES.BY.SHOP",
                product.getProductId(),
                whIds);

        for (final Object[] skuQty : skuQtyList) {
            final String skuCode = (String) skuQty[0];
            final BigDecimal stock = (BigDecimal) skuQty[1];
            final BigDecimal reserved = (BigDecimal) skuQty[2];

            BigDecimal total = qty.get(skuCode);
            qty.put(skuCode, total.add(MoneyUtils.notNull(stock)).subtract(MoneyUtils.notNull(reserved)));
        }

        return qty;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, BigDecimal> findProductSkuAvailableToSellQuantity(final ProductSku productSku, final Collection<Warehouse> warehouses) {

        final Map<String, BigDecimal> qty = new HashMap<String, BigDecimal>();
        qty.put(productSku.getCode(), BigDecimal.ZERO);

        if (CollectionUtils.isEmpty(warehouses)) {
            return qty;
        }

        final List<Long> whIds = new ArrayList<Long>();
        for (final Warehouse wh : warehouses) {
            whIds.add(wh.getWarehouseId());
        }

        final List<Object[]> skuQtyList = getGenericDao().findQueryObjectsByNamedQuery(
                "SKU.QTY.ON.WAREHOUSES.BY.SHOP",
                productSku.getSkuId(),
                whIds);

        for (final Object[] skuQty : skuQtyList) {
            final String skuCode = (String) skuQty[0];
            final BigDecimal stock = (BigDecimal) skuQty[1];
            final BigDecimal reserved = (BigDecimal) skuQty[2];

            BigDecimal total = qty.get(skuCode);
            qty.put(skuCode, total.add(MoneyUtils.notNull(stock)).subtract(MoneyUtils.notNull(reserved)));
        }

        return qty;
    }

    /**
     * Get the sku's Quantity - Reserved quantity pair.
     *
     * @param warehouses list of warehouses where
     * @param productSkuCode sku
     * @return pair of available and reserved quantity
     */
    public Pair<BigDecimal, BigDecimal> getQuantity(final List<Warehouse> warehouses, final String productSkuCode) {

        final List<Object> warehouseIdList = new ArrayList<Object>(warehouses.size());
        for (Warehouse wh : warehouses) {
            warehouseIdList.add(wh.getWarehouseId());
        }

        final List rez = getGenericDao().findQueryObjectsByNamedQuery(
                "SKU.QTY.ON.WAREHOUSES",
                productSkuCode,
                warehouseIdList
        );

        BigDecimal quantity = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);
        BigDecimal reserved = BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);

        if (!rez.isEmpty()) {
            final Object obj[] = (Object[]) rez.get(0);
            if (obj.length > 0 && obj[0] != null) {
                quantity = ((BigDecimal) obj[0]).setScale(Constants.DEFAULT_SCALE);
            }
            if (obj.length > 1 && obj[1] != null) {
                reserved = ((BigDecimal) obj[1]).setScale(Constants.DEFAULT_SCALE);
            }
        }

        return new Pair<BigDecimal, BigDecimal>(quantity, reserved);

    }


    /**
     * {@inheritDoc}
     */
    public BigDecimal reservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal reserveQty) {

        final SkuWarehouse skuWarehouse = findByWarehouseSkuForUpdate(warehouse, productSkuCode);

        if (skuWarehouse == null) {
            return reserveQty.setScale(Constants.DEFAULT_SCALE);
        } else {
            BigDecimal canReserve = skuWarehouse.getAvailableToSell();

            BigDecimal rest = canReserve.subtract(reserveQty);
            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(rest, BigDecimal.ZERO)) {
                skuWarehouse.setReserved(
                        MoneyUtils.notNull(skuWarehouse.getReserved(), BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE)).add(reserveQty));
                update(skuWarehouse);
                return BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);
            } else {
                skuWarehouse.setReserved(skuWarehouse.getQuantity());
                update(skuWarehouse);
                return rest.abs().setScale(Constants.DEFAULT_SCALE);
            }
        }

    }


    /**
     * {@inheritDoc}
     */
    public BigDecimal voidReservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal voidQty) {
        final SkuWarehouse skuWarehouse = findByWarehouseSkuForUpdate(warehouse, productSkuCode);

        if (skuWarehouse == null) {
            return voidQty.setScale(Constants.DEFAULT_SCALE);
        } else {
            BigDecimal canVoid = MoneyUtils.notNull(skuWarehouse.getReserved(), BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE)).min(voidQty);
            BigDecimal rest = MoneyUtils.notNull(skuWarehouse.getReserved(), BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE)).subtract(voidQty);
            skuWarehouse.setReserved(MoneyUtils.notNull(skuWarehouse.getReserved(), BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE)).subtract(canVoid));
            update(skuWarehouse);
            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(rest, BigDecimal.ZERO)) {
                return BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);
            } else {
                return rest.abs().setScale(Constants.DEFAULT_SCALE);
            }

        }
    }


    /**
     * {@inheritDoc}
     */
    public BigDecimal credit(final Warehouse warehouse, final String productSkuCode, final BigDecimal addQty) {
        final SkuWarehouse skuWarehouse = findByWarehouseSkuForUpdate(warehouse, productSkuCode);

        if (skuWarehouse == null) {
            final ProductSku sku = productService.getProductSkuByCode(productSkuCode);
            final SkuWarehouse newSkuWarehouse = getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
            newSkuWarehouse.setQuantity(addQty);
            newSkuWarehouse.setSku(sku);
            newSkuWarehouse.setWarehouse(warehouse);
            create(newSkuWarehouse);
        } else {
            skuWarehouse.setQuantity(skuWarehouse.getQuantity().add(addQty));
            update(skuWarehouse);
        }
        updateOrdersAwaitingForInventory(productSkuCode);
        return BigDecimal.ZERO;

    }

    /** {@inheritDoc}*/
    @CacheEvict(value = {
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public SkuWarehouse create(SkuWarehouse instance) {
        final SkuWarehouse rez = super.create(instance);
        return rez;
    }

    /** {@inheritDoc}*/
    @CacheEvict(value = {
            "skuWarehouseService-productOnWarehouse",
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public SkuWarehouse update(SkuWarehouse instance) {
        final SkuWarehouse rez = super.update(instance);
        getGenericDao().flush(); // Need to make changes immediately available
        return rez;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal debit(final Warehouse warehouse, final String productSkuCode, final BigDecimal debitQty) {

        final SkuWarehouse skuWarehouse = findByWarehouseSkuForUpdate(warehouse, productSkuCode);

        if (skuWarehouse == null) {
            return debitQty.setScale(Constants.DEFAULT_SCALE);
        } else {
            BigDecimal canDebit = skuWarehouse.getQuantity().min(debitQty);
            BigDecimal rest = skuWarehouse.getQuantity().subtract(debitQty);
            skuWarehouse.setQuantity(skuWarehouse.getQuantity().subtract(canDebit));
            update(skuWarehouse);
            if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(BigDecimal.ZERO, rest)) {
                return rest.abs().setScale(Constants.DEFAULT_SCALE);
            } else {
                return BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE);
            }
        }

    }

    private SkuWarehouse findByWarehouseSkuForUpdate(final Warehouse warehouse, final String productSkuCode) {
        final SkuWarehouse inventory = findByWarehouseSku(warehouse, productSkuCode);
        if (inventory != null) {
            return getGenericDao().findById(inventory.getSkuWarehouseId(), true);
        }
        return null;
    }


    /** {@inheritDoc} */
    public SkuWarehouse findByWarehouseSku(final Warehouse warehouse, final String productSkuCode) {
        return getGenericDao().findSingleByNamedQuery(
                "SKUS.ON.WAREHOUSE.BY.SKUCODE",
                productSkuCode,
                warehouse.getWarehouseId());
    }

    /** {@inheritDoc} */
    public List<String> findProductSkuForWhichInventoryChangedAfter(final Date lastUpdate) {
        return (List) getGenericDao().findQueryObjectByNamedQuery("SKUCODE.FOR.SKUWAREHOUSE.CHANGED.SINCE", lastUpdate);
    }

    /** {@inheritDoc} */
    public void updateOrdersAwaitingForInventory(final String productSkuCode) {

        if (isSkuAvailabilityPreorderOrBackorder(productSkuCode, true)) {

            final ResultsIterator<CustomerOrderDelivery> waitForInventory = getCustomerOrderService().findAwaitingDeliveries(
                    Arrays.asList(productSkuCode),
                    CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                    Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS, CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED)
            );

            try {
                while (waitForInventory.hasNext()) {

                    final CustomerOrderDelivery delivery = waitForInventory.next();

                    try {
                        boolean rez = getOrderStateManager().fireTransition(
                                new OrderEventImpl(OrderStateManager.EVT_DELIVERY_ALLOWED_TIMEOUT, delivery.getCustomerOrder(), delivery));
                        if (rez) {
                            customerOrderService.update(delivery.getCustomerOrder());
                            ShopCodeContext.getLog(this).info("Push delivery " + delivery.getDeliveryNum() + " back to life cycle , because of sku quantity is changed. Product sku code =" + productSkuCode);

                        } else {
                            ShopCodeContext.getLog(this).info("Cannot push delivery "
                                    + delivery.getDeliveryNum() + " back to life cycle , because of sku quantity is changed. Product sku code ="
                                    + productSkuCode
                                    + " Stop pushing");
                            break;
                        }
                    } catch (OrderException e) {
                        ShopCodeContext.getLog(this).warn("Cannot push orders, which are awaiting for inventory", e);
                    }

                }
            } catch (Exception exp) {
                waitForInventory.close();
                ShopCodeContext.getLog(this).error(exp.getMessage(), exp);
            }

        }

    }


    /** {@inheritDoc} */
    public boolean isSkuAvailabilityPreorderOrBackorder(final String productSkuCode, final boolean checkAvailabilityDates) {
        ProductSku sku = productService.getProductSkuByCode(productSkuCode);
        if (sku != null) {
            Product product = sku.getProduct();
            if (Product.AVAILABILITY_PREORDER == product.getAvailability() || Product.AVAILABILITY_BACKORDER == product.getAvailability()) {
                if (checkAvailabilityDates) {
                    if (product.getAvailablefrom() != null || product.getAvailableto() != null) {
                        final Date now = new Date();
                        if (product.getAvailablefrom() != null) {
                            return now.after(product.getAvailablefrom());
                        }
                        if (product.getAvailableto() != null) {
                            return now.before(product.getAvailableto());
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /** IoC.*/
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    private OrderStateManager getOrderStateManager() {
        if (orderStateManager == null) {
            orderStateManager = applicationContext.getBean("orderStateManager", OrderStateManager.class);
        }
        return orderStateManager;
    }

    private CustomerOrderService getCustomerOrderService() {
        if (customerOrderService == null) {
            customerOrderService =  applicationContext.getBean("customerOrderService", CustomerOrderService.class);
        }
        return customerOrderService;
    }


    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

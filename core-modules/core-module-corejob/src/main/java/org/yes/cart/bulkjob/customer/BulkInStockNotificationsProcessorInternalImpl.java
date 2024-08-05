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

package org.yes.cart.bulkjob.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkjob.cron.AbstractCronJobProcessorImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.impl.JobStatusListenerImpl;
import org.yes.cart.service.async.impl.JobStatusListenerWithLoggerImpl;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.*;
import org.yes.cart.utils.MoneyUtils;

import java.time.Instant;
import java.util.*;

/**
 * User: inspiresoftware
 * Date: 10/03/2024
 * Time: 14:42
 */
public class BulkInStockNotificationsProcessorInternalImpl extends AbstractCronJobProcessorImpl
        implements BulkInStockNotificationsProcessorInternal, JobStatusAware {

    private static final Logger LOG = LoggerFactory.getLogger(BulkInStockNotificationsProcessorInternalImpl.class);

    private static final String SENT_COUNTER = "Sent notifications";
    private static final String SKIPPED_COUNTER = "Skipped notifications";

    private ShopService shopService;
    private CustomerService customerService;
    private CustomerWishListService customerWishListService;
    private WarehouseService warehouseService;
    private SkuWarehouseService skuWarehouseService;
    private ProductSkuService productSkuService;
    private PriceService priceService;

    private final JobStatusListener listener = new JobStatusListenerWithLoggerImpl(new JobStatusListenerImpl(), LOG);

    /** {@inheritDoc} */
    @Override
    public JobStatus getStatus(final String token) {
        return listener.getLatestStatus();
    }

    /** {@inheritDoc} */
    @Override
    public Pair<JobStatus, Instant> processInternal(final Map<String, Object> context, final Job job, final JobDefinition definition) {

        listener.reset();

        final Properties properties = readContextAsProperties(context, job, definition);

        final Customer[] customer = new Customer[] { null };
        final Map<Shop, List<Pair<CustomerWishList, ProductSku>>> notifications = new HashMap<>();

        this.customerWishListService.findByCriteriaIterator(
                " where e.wlType = ?1 order by e.customer.customerId asc",
                new Object[] { CustomerWishList.REMIND_WHEN_WILL_BE_AVAILABLE },
                wishList -> {

                    final Customer wlCustomer = wishList.getCustomer();
                    if (customer[0] == null) {
                        customer[0] = customerService.findById(wlCustomer.getCustomerId());
                        notifications.clear();
                    }
                    if (customer[0].getCustomerId() != wlCustomer.getCustomerId()) {
                        // Last WL
                        final Map<Long, CustomerWishList> collector = new HashMap<>();
                        for (final Map.Entry<Shop, List<Pair<CustomerWishList, ProductSku>>> entry : notifications.entrySet()) {
                            self().createNotificationEmail(entry.getKey(), customer[0], entry.getValue());
                            for (final Pair<CustomerWishList, ProductSku> item : entry.getValue()) {
                                collector.put(item.getFirst().getCustomerwishlistId(), item.getFirst());
                            }
                        }
                        listener.count(SENT_COUNTER, collector.size());
                        self().removeSentNotifications(collector.values());
                        // reset
                        customer[0] = customerService.findById(wlCustomer.getCustomerId());
                        notifications.clear();
                    }

                    boolean addedAtLeastOnce = false;
                    for (final CustomerShop customerShop : customer[0].getShops()) {
                        if (customerShop.isDisabled()) {
                            continue;
                        }
                        final Map<String, Warehouse> codeWH = warehouseService.getByShopIdMapped(customerShop.getShop().getShopId(), false);
                        if (codeWH.containsKey(wishList.getSupplierCode())) {
                            final SkuWarehouse skuWarehouse = skuWarehouseService.findByWarehouseSku(codeWH.get(wishList.getSupplierCode()), wishList.getSkuCode());
                            if (skuWarehouse != null && skuWarehouse.isAvailableToSell(wishList.getQuantity(), false)) {
                                final Shop shop = shopService.getById(customerShop.getShop().getShopId());
                                final ProductSku sku = productSkuService.getProductSkuBySkuCode(wishList.getSkuCode());

                                final SkuPrice price = priceService.getMinimalPrice(
                                        sku.getProduct().getProductId(), sku.getCode(),
                                        shop.getShopId(), shop.getMaster() != null ? shop.getMaster().getShopId() : null,
                                        wishList.getRegularPriceCurrencyWhenAdded(),
                                        wishList.getQuantity(), false, null, wishList.getSupplierCode()
                                );

                                if (price != null && MoneyUtils.isPositive(price.getRegularPrice())) {
                                    final List<Pair<CustomerWishList, ProductSku>> list = notifications.computeIfAbsent(shop, k -> new ArrayList<>());
                                    list.add(Pair.of(wishList, sku));
                                    addedAtLeastOnce = true;
                                } else {
                                    LOG.debug("Creating mail notification for {}, shop: {} ... no price {} for {}",
                                            customer[0].getCustomerId(), customerShop.getShop().getShopId(),
                                            wishList.getSupplierCode(), wishList.getCustomerwishlistId());
                                }
                            } else {
                                LOG.debug("Creating mail notification for {}, shop: {} ... no quantity {} for {}",
                                        customer[0].getCustomerId(), customerShop.getShop().getShopId(),
                                        wishList.getSupplierCode(), wishList.getCustomerwishlistId());
                            }
                        } else {
                            LOG.debug("Creating mail notification for {}, shop: {} ... no warehouse {} for {}",
                                    customer[0].getCustomerId(), customerShop.getShop().getShopId(),
                                    wishList.getSupplierCode(), wishList.getCustomerwishlistId());
                        }
                    }

                    if (!addedAtLeastOnce) {
                        listener.count(SKIPPED_COUNTER, 1);
                    }

                    return true; // read fully
                }
        );

        if (customer[0] != null) {
            // Last WL
            final Map<Long, CustomerWishList> collector = new HashMap<>();
            for (final Map.Entry<Shop, List<Pair<CustomerWishList, ProductSku>>> entry : notifications.entrySet()) {
                self().createNotificationEmail(entry.getKey(), customer[0], entry.getValue());
                for (final Pair<CustomerWishList, ProductSku> item : entry.getValue()) {
                    collector.put(item.getFirst().getCustomerwishlistId(), item.getFirst());
                }
            }
            listener.count(SENT_COUNTER, collector.size());
            self().removeSentNotifications(collector.values());
        }


        listener.notifyCompleted();

        return new Pair<>(listener.getLatestStatus(), null);

    }

    @Override
    public void createNotificationEmail(final Shop shop, final Customer customer, final List<Pair<CustomerWishList, ProductSku>> items) {

        LOG.debug("Creating mail notification for {}, shop: {}, items: {}",
                customer.getCustomerId(), shop.getCode(),
                items.size());

    }

    @Override
    public void removeSentNotifications(final Collection<CustomerWishList> items) {

        for (final CustomerWishList item : items) {
            customerWishListService.delete(item);
        }

    }

    private BulkInStockNotificationsProcessorInternal self;

    private BulkInStockNotificationsProcessorInternal self() {
        if (self == null) {
            self = getSelf();
        }
        return self;
    }

    public BulkInStockNotificationsProcessorInternal getSelf() {
        return null;
    }


    /**
     * Spring IoC.
     *
     * @param shopService service
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * Spring IoC.
     *
     * @param customerService service
     */
    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Spring IoC.
     *
     * @param customerWishListService service
     */
    public void setCustomerWishListService(final CustomerWishListService customerWishListService) {
        this.customerWishListService = customerWishListService;
    }

    /**
     * Spring IoC.
     *
     * @param warehouseService service
     */
    public void setWarehouseService(final WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /**
     * Spring IoC.
     *
     * @param skuWarehouseService service
     */
    public void setSkuWarehouseService(final SkuWarehouseService skuWarehouseService) {
        this.skuWarehouseService = skuWarehouseService;
    }

    /**
     * Spring IoC.
     *
     * @param productSkuService service
     */
    public void setProductSkuService(final ProductSkuService productSkuService) {
        this.productSkuService = productSkuService;
    }

    /**
     * Spring IoC.
     *
     * @param priceService service
     */
    public void setPriceService(final PriceService priceService) {
        this.priceService = priceService;
    }
}

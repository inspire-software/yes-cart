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

package org.yes.cart.shoppingcart.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.DeliveryTimeEstimationVisitor;
import org.yes.cart.shoppingcart.MutableShoppingCart;

import java.util.*;

/**
 * User: denispavlov
 * Date: 07/02/2017
 * Time: 07:36
 */
public class DeliveryTimeEstimationVisitorImpl implements DeliveryTimeEstimationVisitor {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryTimeEstimationVisitorImpl.class);

    private final WarehouseService warehouseService;
    private final CarrierSlaService carrierSlaService;

    public DeliveryTimeEstimationVisitorImpl(final WarehouseService warehouseService,
                                             final CarrierSlaService carrierSlaService) {
        this.warehouseService = warehouseService;
        this.carrierSlaService = carrierSlaService;
    }

    /** {@inheritDoc} */
    public void visit(final CustomerOrder order) {

        final Collection<CustomerOrderDelivery> deliveries = order.getDelivery();
        if (CollectionUtils.isNotEmpty(deliveries)) {

            final Map<String, Warehouse> warehouseByCode = getFulfilmentCentresMap(order);

            for (final CustomerOrderDelivery delivery : deliveries) {

                determineDeliveryTime(delivery, warehouseByCode);

            }

        }

    }

    /** {@inheritDoc} */
    public void visit(final CustomerOrderDelivery delivery) {

        final Map<String, Warehouse> warehouseByCode = getFulfilmentCentresMap(delivery.getCustomerOrder());

        determineDeliveryTime(delivery, warehouseByCode);

    }

    @Override
    public void visit(final MutableShoppingCart shoppingCart) {

        final Map<String, Warehouse> warehouseByCode = getFulfilmentCentresMap(shoppingCart);

        for (final Map.Entry<String, Long> carrierSlaEntry : shoppingCart.getOrderInfo().getCarrierSlaId().entrySet()) {
            determineDeliveryAvailableTimeRange(shoppingCart, carrierSlaEntry.getValue(), warehouseByCode.get(carrierSlaEntry.getKey()));
        }

    }

    /**
     * Fulfilment centres map.
     *
     * @param order order
     *
     * @return applicable suppliers
     */
    protected Map<String, Warehouse> getFulfilmentCentresMap(final CustomerOrder order) {
        return warehouseService.getByShopIdMapped(order.getShop().getShopId(), false);
    }

    /**
     * Fulfilment centres map.
     *
     * @param cart cart
     *
     * @return applicable suppliers
     */
    protected Map<String, Warehouse> getFulfilmentCentresMap(final MutableShoppingCart cart) {
        return warehouseService.getByShopIdMapped(cart.getShoppingContext().getCustomerShopId(), false);
    }

    /**
     * Visit shopping cart for given carrier SLA selection.
     *
     * @param shoppingCart cart
     * @param carrierSlaId carrier SLA
     * @param ff           fulfilment centre
     */
    protected void determineDeliveryAvailableTimeRange(final MutableShoppingCart shoppingCart, final Long carrierSlaId, final Warehouse ff) {

        if (carrierSlaId == null) {
            return; // do nothing if we have no selection
        }

        final CarrierSla sla = carrierSlaService.findById(carrierSlaId);
        if (sla == null) {
            return; // do nothing if we have no selection
        }

        final String suffix = sla.getCarrierslaId() + (ff != null ? ff.getCode() : "");
        final String minKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + "Min" + suffix;
        final String maxKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + "Max" + suffix;
        final String excludedDatesKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + "DExcl" + suffix;
        final String excludedWeekdaysKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + "WExcl" + suffix;

        // Ensure we clean up any old data
        shoppingCart.getOrderInfo().putDetail(minKey, null);
        shoppingCart.getOrderInfo().putDetail(maxKey, null);
        shoppingCart.getOrderInfo().putDetail(excludedDatesKey, null);
        shoppingCart.getOrderInfo().putDetail(excludedWeekdaysKey, null);

        if (!sla.isNamedDay()) {
            return; // Only named date should have this calculation, as all others are automatic min/max
        }

        Calendar minDeliveryTime = now();

        minDeliveryTime.set(Calendar.HOUR_OF_DAY, 0);
        minDeliveryTime.set(Calendar.MINUTE, 0);
        minDeliveryTime.set(Calendar.SECOND, 0);
        minDeliveryTime.set(Calendar.MILLISECOND, 0);

        Calendar maxDeliveryTime = Calendar.getInstance();
        maxDeliveryTime.setTime(minDeliveryTime.getTime());

        if (ff != null && ff.getDefaultBackorderStockLeadTime() > 0) {
            for (final CartItem item : shoppingCart.getCartItemList()) {
                final String dgroup = item.getDeliveryBucket().getGroup();
                if (!CustomerOrderDelivery.STANDARD_DELIVERY_GROUP.equals(dgroup) &&
                        !CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(dgroup)) {
                    minDeliveryTime.add(Calendar.DAY_OF_YEAR, ff.getDefaultBackorderStockLeadTime());
                }
            }
        }

        final Map<Date, Date> slaExcludedDates = getCarrierSlaExcludedDates(sla);

        final int minDays = sla.getMinDays() != null ? sla.getMinDays() : 0;
        final int maxDays = sla.getMaxDays() != null ? sla.getMaxDays() : 0;

        if (minDays > 0) {
            minDeliveryTime.add(Calendar.DAY_OF_YEAR, minDays);
            skipWeekdayExclusions(sla, minDeliveryTime);
            skipDatesExclusions(sla, minDeliveryTime, slaExcludedDates);
        }

        Date min = minDeliveryTime.getTime();

        if (maxDays > minDays) {
            maxDeliveryTime.add(Calendar.DAY_OF_YEAR, maxDays - minDays + 60);  // +60days upfront for customer to choose
        }
        skipWeekdayExclusions(sla, maxDeliveryTime);

        Date max = maxDeliveryTime.getTime();

        // Save possible ranges min, max and excluded as long values
        shoppingCart.getOrderInfo().putDetail(minKey, String.valueOf(min.getTime()));
        shoppingCart.getOrderInfo().putDetail(maxKey, String.valueOf(max.getTime()));

        final Map<Date, Date> exclusions = sla.getExcludeDatesAsMap();
        if (!exclusions.isEmpty()) {

            final StringBuilder exclusionsInMinMax = new StringBuilder();

            minDeliveryTime.setTime(min);
            while (minDeliveryTime.getTime().before(max)) {

                minDeliveryTime.add(Calendar.DAY_OF_YEAR, 1);

                Date date = minDeliveryTime.getTime();

                skipDatesExclusions(sla, minDeliveryTime, exclusions);

                if (date.getTime() != minDeliveryTime.getTime().getTime()) {
                    // we skipped
                    final Calendar excluded = Calendar.getInstance();
                    excluded.setTime(date);

                    do {

                        if (exclusionsInMinMax.length() > 0) {
                            exclusionsInMinMax.append(',');
                        }
                        exclusionsInMinMax.append(excluded.getTime().getTime());
                        excluded.add(Calendar.DAY_OF_YEAR, 1);

                    } while (excluded.getTime().before(minDeliveryTime.getTime()));
                }

            }

            if (exclusionsInMinMax.length() > 0) {
                shoppingCart.getOrderInfo().putDetail(excludedDatesKey, exclusionsInMinMax.toString());
            }

            if (StringUtils.isNotBlank(sla.getExcludeWeekDays())) {
                shoppingCart.getOrderInfo().putDetail(excludedWeekdaysKey, sla.getExcludeWeekDays());
            }
        }


    }


    /**
     * Visit single delivery.
     *
     * @param customerOrderDelivery customer order delivery
     * @param warehouseByCode       all fulfilment centres
     */
    protected void determineDeliveryTime(final CustomerOrderDelivery customerOrderDelivery, final Map<String, Warehouse> warehouseByCode) {

        if (!CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(customerOrderDelivery.getDeliveryGroup())) {

            final CarrierSla sla = customerOrderDelivery.getCarrierSla();

            Calendar minDeliveryTime = now();
            if (sla.isNamedDay() && customerOrderDelivery.getRequestedDeliveryDate() != null && customerOrderDelivery.getRequestedDeliveryDate().after(minDeliveryTime.getTime())) {
                minDeliveryTime.setTime(customerOrderDelivery.getRequestedDeliveryDate()); // Set named day if we have a selection
            }

            minDeliveryTime.set(Calendar.HOUR_OF_DAY, 0);
            minDeliveryTime.set(Calendar.MINUTE, 0);
            minDeliveryTime.set(Calendar.SECOND, 0);
            minDeliveryTime.set(Calendar.MILLISECOND, 0);

            skipInventoryLeadTime(customerOrderDelivery, warehouseByCode, minDeliveryTime);

            final Map<Date, Date> slaExcludedDates = getCarrierSlaExcludedDates(sla);

            final int minDays = sla.getMinDays() != null ? sla.getMinDays() : 0;
            final int maxDays = sla.getMaxDays() != null ? sla.getMaxDays() : 0;

            if (minDays > 0) {
                minDeliveryTime.add(Calendar.DAY_OF_YEAR, minDays);
                skipWeekdayExclusions(sla, minDeliveryTime);
                skipDatesExclusions(sla, minDeliveryTime, slaExcludedDates);
            }

            Date guaranteed = null;
            Date min = null;
            Date max = null;

            if (sla.isGuaranteed()) {
                if (CustomerOrderDelivery.STANDARD_DELIVERY_GROUP.equals(customerOrderDelivery.getDeliveryGroup())) {
                    // guarantee is only for in stock items
                    guaranteed = minDeliveryTime.getTime();
                } else {
                    min = minDeliveryTime.getTime();
                }

            } else {
                min = minDeliveryTime.getTime();
                if (maxDays > minDays) {
                    minDeliveryTime.add(Calendar.DAY_OF_YEAR, maxDays - minDays);
                    skipWeekdayExclusions(sla, minDeliveryTime);
                }
                max = minDeliveryTime.getTime();
            }

            // Need to reset all in case we have dirty fields
            customerOrderDelivery.setDeliveryGuaranteed(guaranteed);
            customerOrderDelivery.setDeliveryEstimatedMin(min);
            customerOrderDelivery.setDeliveryEstimatedMax(max);

        }

    }

    /**
     * Skip lead time set by inventory
     *
     * @param customerOrderDelivery delivery
     * @param warehouseByCode       fulfilment centers
     * @param minDeliveryTime       start date (i.e. now)
     */
    protected void skipInventoryLeadTime(final CustomerOrderDelivery customerOrderDelivery, final Map<String, Warehouse> warehouseByCode, final Calendar minDeliveryTime) {

        if (CustomerOrderDelivery.STANDARD_DELIVERY_GROUP.equals(customerOrderDelivery.getDeliveryGroup())) {

            final Warehouse ff = warehouseByCode.get(customerOrderDelivery.getDetail().iterator().next().getSupplierCode());
            if (ff != null && ff.getDefaultStandardStockLeadTime() > 0) {
                minDeliveryTime.add(Calendar.DAY_OF_YEAR, ff.getDefaultStandardStockLeadTime());
            }

        } else {
            // Pre, Back and Mixed (very simplistic) TODO: account for product specific lead times and pre-order release dates
            final Warehouse ff = warehouseByCode.get(customerOrderDelivery.getDetail().iterator().next().getSupplierCode());
            if (ff != null && ff.getDefaultBackorderStockLeadTime() > 0) {
                minDeliveryTime.add(Calendar.DAY_OF_YEAR, ff.getDefaultBackorderStockLeadTime());
            }

        }
    }

    protected Calendar now() {
        return Calendar.getInstance();
    }

    protected Map<Date, Date> getCarrierSlaExcludedDates(final CarrierSla sla) {

        return sla.getExcludeDatesAsMap();

    }

    protected void skipWeekdayExclusions(final CarrierSla sla, final Calendar date) {

        if (StringUtils.isNotBlank(sla.getExcludeWeekDays())) {
            final List<String> excluded = new ArrayList<String>(Arrays.asList(StringUtils.split(sla.getExcludeWeekDays(), ',')));
            while (!excluded.isEmpty()) {
                final int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
                final Iterator<String> itExluded = excluded.iterator();
                boolean removed = false;
                while (itExluded.hasNext()) {
                    if (NumberUtils.toInt(itExluded.next()) == dayOfWeek) {
                        date.add(Calendar.DAY_OF_YEAR, 1); // This date is excluded
                        itExluded.remove();
                        removed = true;
                    }
                }
                if (!removed) {
                    return;
                }
            }
        }

    }


    protected void skipDatesExclusions(final CarrierSla sla, final Calendar date, final Map<Date, Date> exclusions) {

        if (!exclusions.isEmpty()) {

            final TreeSet<Date> startDates = new TreeSet<Date>(exclusions.keySet());

            while (true) {
                final Date thisDate = date.getTime();
                final Date beforeOfEqual = startDates.floor(thisDate);
                if (beforeOfEqual == null) {
                    return; // no exclusions before
                } else if (beforeOfEqual.before(thisDate)) {
                    final Date rangeEnd = exclusions.get(beforeOfEqual);
                    // Two cases here:
                    // 1) Single date - same as beforeOfEqual
                    // 2) Range - need to make sure it is before this date
                    if (thisDate.after(rangeEnd)) {
                        return; // This date is after the min in exclusions
                    } else {
                        thisDate.setTime(rangeEnd.getTime());
                        date.add(Calendar.DAY_OF_YEAR, 1);
                        skipWeekdayExclusions(sla, date);
                    }
                } else {
                    // equal, so need to move next day and check weekdays
                    date.add(Calendar.DAY_OF_YEAR, 1);
                    skipWeekdayExclusions(sla, date);
                }
            }

        }

    }


}

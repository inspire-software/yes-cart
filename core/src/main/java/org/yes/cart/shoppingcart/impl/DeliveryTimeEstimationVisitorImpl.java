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

    private static final int MAX_NAMED_DAY_IF_NOT_SET = 60; // +60days upfront for customer to choose by default

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
        final String requestedDateKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + suffix;
        final String minKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + "Min" + suffix;
        final String maxKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + "Max" + suffix;
        final String excludedDatesKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + "DExcl" + suffix;
        final String excludedWeekdaysKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + "WExcl" + suffix;

        // Get existing selection, so that we can check it against valid dates later
        long requestedDate = NumberUtils.toLong(shoppingCart.getOrderInfo().getDetailByKey(requestedDateKey));

        // Ensure we clean up any old data
        shoppingCart.getOrderInfo().putDetail(requestedDateKey, null);
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
        final int maxDays = sla.getMaxDays() != null ? sla.getMaxDays() : MAX_NAMED_DAY_IF_NOT_SET;

        if (minDays > 0) {
            minDeliveryTime.add(Calendar.DAY_OF_YEAR, minDays);
        }

        // Set hard lower limit, before skipping exclusions
        maxDeliveryTime.setTime(minDeliveryTime.getTime());

        skipWeekdayExclusions(sla, minDeliveryTime);
        skipDatesExclusions(sla, minDeliveryTime, slaExcludedDates);

        Date min = minDeliveryTime.getTime();

        // Ensure we are not below lower limit before we check exclusions
        if (requestedDate < min.getTime()) {
            requestedDate = min.getTime();
        }

        if (maxDays > minDays) {
            maxDeliveryTime.add(Calendar.DAY_OF_YEAR, maxDays - minDays);
        } else {
            maxDeliveryTime.add(Calendar.DAY_OF_YEAR, 1); // Misconfiguration - just show one day
        }
        // We want a hard limit on max, so we do not skip beyond maxDays
        // skipWeekdayExclusions(sla, maxDeliveryTime);

        Date max = maxDeliveryTime.getTime();

        final Map<Date, Date> exclusions = sla.getExcludeDatesAsMap();
        if (!exclusions.isEmpty() || !sla.getExcludeWeekDaysAsList().isEmpty()) {

            final StringBuilder exclusionsInMinMax = new StringBuilder();

            minDeliveryTime.setTime(min);
            Date lastValidDate = min;

            while (minDeliveryTime.getTime().before(max)) {

                minDeliveryTime.add(Calendar.DAY_OF_YEAR, 1);

                Date date = minDeliveryTime.getTime();

                skipWeekdayExclusions(sla, minDeliveryTime);
                skipDatesExclusions(sla, minDeliveryTime, exclusions);

                if (date.getTime() != minDeliveryTime.getTime().getTime()) {
                    // we skipped
                    final Calendar excluded = Calendar.getInstance();
                    excluded.setTime(date);

                    // Ensure requested date is not in exclusion range and if it is set the first day after the exclusion
                    if (date.getTime() <= requestedDate && requestedDate < minDeliveryTime.getTime().getTime()) {
                        requestedDate = minDeliveryTime.getTime().getTime();
                    }

                    do {

                        if (exclusionsInMinMax.length() > 0) {
                            exclusionsInMinMax.append(',');
                        }
                        exclusionsInMinMax.append(excluded.getTime().getTime());
                        excluded.add(Calendar.DAY_OF_YEAR, 1);

                    } while (excluded.getTime().before(minDeliveryTime.getTime()));
                } else {
                    // This date is not excluded
                    lastValidDate = date;
                }

            }

            // Save excluded dates as long values
            if (exclusionsInMinMax.length() > 0) {
                shoppingCart.getOrderInfo().putDetail(excludedDatesKey, exclusionsInMinMax.toString());
            }

            // Save excluded week days as long values
            if (StringUtils.isNotBlank(sla.getExcludeWeekDays())) {
                shoppingCart.getOrderInfo().putDetail(excludedWeekdaysKey, sla.getExcludeWeekDays());
            }

            // Tailing exclusions
            if (lastValidDate.before(max)) {
                max = lastValidDate;
            }
            if (requestedDate > lastValidDate.getTime()) {
                requestedDate = lastValidDate.getTime();
            }

        }

        // Ensure we are not above upper limit after we checked exclusions
        if (requestedDate > max.getTime()) {
            requestedDate = max.getTime();
        }

        // Save possible min - max range and preselect requested date, since this is named delivery
        shoppingCart.getOrderInfo().putDetail(requestedDateKey, String.valueOf(requestedDate));
        shoppingCart.getOrderInfo().putDetail(minKey, String.valueOf(min.getTime()));
        shoppingCart.getOrderInfo().putDetail(maxKey, String.valueOf(max.getTime()));

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

            minDeliveryTime.set(Calendar.HOUR_OF_DAY, 0);
            minDeliveryTime.set(Calendar.MINUTE, 0);
            minDeliveryTime.set(Calendar.SECOND, 0);
            minDeliveryTime.set(Calendar.MILLISECOND, 0);

            // Honour warehouse lead inventory
            skipInventoryLeadTime(customerOrderDelivery, warehouseByCode, minDeliveryTime);

            boolean namedDay = sla.isNamedDay();

            final Map<Date, Date> slaExcludedDates = getCarrierSlaExcludedDates(sla);

            final int minDays = sla.getMinDays() != null ? sla.getMinDays() : 0;
            final int maxDays = sla.getMaxDays() != null ? sla.getMaxDays() : (namedDay ? MAX_NAMED_DAY_IF_NOT_SET : 0);

            // Ensure delivery lead time
            if (minDays > 0) {
                minDeliveryTime.add(Calendar.DAY_OF_YEAR, minDays);
            }

            // Process exclusions to ensure that we do not set excluded day (For named day this should not make any changes
            // since we already checked it, this is just to ensure we do not have manual tampering with data)
            skipWeekdayExclusions(sla, minDeliveryTime);
            skipDatesExclusions(sla, minDeliveryTime, slaExcludedDates);

            // For named days ensure that requested date is valid
            if (namedDay) {
                // It must set and not before the lower limit
                if (customerOrderDelivery.getRequestedDeliveryDate() != null && !customerOrderDelivery.getRequestedDeliveryDate().before(minDeliveryTime.getTime())) {

                    final Calendar checkRequested = Calendar.getInstance();
                    checkRequested.setTime(customerOrderDelivery.getRequestedDeliveryDate());
                    checkRequested.set(Calendar.HOUR_OF_DAY, 0);
                    checkRequested.set(Calendar.MINUTE, 0);
                    checkRequested.set(Calendar.SECOND, 0);
                    checkRequested.set(Calendar.MILLISECOND, 0);

                    // Attempt to skip exclusion to see if the date changes
                    skipWeekdayExclusions(sla, checkRequested);
                    skipDatesExclusions(sla, checkRequested, slaExcludedDates);

                    if (customerOrderDelivery.getRequestedDeliveryDate().getTime() == checkRequested.getTime().getTime()) {
                        // We have not changed the date so it is valid, for named date we do not estimate
                        return;
                    }

                    // else the requested date is invalid, so we use the next available after the exclusions
                    minDeliveryTime.setTime(checkRequested.getTime());

                }

                // else requested date is less than minimal
            }

            Date guaranteed = null;
            Date min = null;
            Date max = null;

            // Named day assumes that it is guaranteed
            if (sla.isGuaranteed() || namedDay) {
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

        final List<Integer> skip = sla.getExcludeWeekDaysAsList();
        if (!skip.isEmpty()) {
            final List<Integer> excluded = new ArrayList<Integer>(skip);
            while (!excluded.isEmpty()) {
                final int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
                final Iterator<Integer> itExluded = excluded.iterator();
                boolean removed = false;
                while (itExluded.hasNext()) {
                    if (Integer.valueOf(dayOfWeek).equals(itExluded.next())) {
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

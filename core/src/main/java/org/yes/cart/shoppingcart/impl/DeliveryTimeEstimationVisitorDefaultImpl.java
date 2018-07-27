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
import org.yes.cart.util.DateUtils;
import org.yes.cart.util.TimeContext;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/**
 * User: denispavlov
 * Date: 07/02/2017
 * Time: 07:36
 */
public class DeliveryTimeEstimationVisitorDefaultImpl implements DeliveryTimeEstimationVisitor {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryTimeEstimationVisitorDefaultImpl.class);

    private static final int MAX_NAMED_DAY_IF_NOT_SET = 60; // +60days upfront for customer to choose by default

    private final WarehouseService warehouseService;
    private final CarrierSlaService carrierSlaService;

    public DeliveryTimeEstimationVisitorDefaultImpl(final WarehouseService warehouseService,
                                                    final CarrierSlaService carrierSlaService) {
        this.warehouseService = warehouseService;
        this.carrierSlaService = carrierSlaService;
    }

    /** {@inheritDoc} */
    @Override
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
    @Override
    public void visit(final CustomerOrderDelivery delivery) {

        final Map<String, Warehouse> warehouseByCode = getFulfilmentCentresMap(delivery.getCustomerOrder());

        determineDeliveryTime(delivery, warehouseByCode);

    }

    /** {@inheritDoc} */
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
        LocalDate requestedDate = DateUtils.ldFrom(NumberUtils.toLong(shoppingCart.getOrderInfo().getDetailByKey(requestedDateKey)));

        // Ensure we clean up any old data
        shoppingCart.getOrderInfo().putDetail(requestedDateKey, null);
        shoppingCart.getOrderInfo().putDetail(minKey, null);
        shoppingCart.getOrderInfo().putDetail(maxKey, null);
        shoppingCart.getOrderInfo().putDetail(excludedDatesKey, null);
        shoppingCart.getOrderInfo().putDetail(excludedWeekdaysKey, null);

        if (!sla.isNamedDay()) {
            return; // Only named date should have this calculation, as all others are automatic min/max
        }

        LocalDate minDeliveryTime = now();

        if (ff != null) {
            final Set<String> checkedGroups = new HashSet<>();
            LocalDate longestMinDeliveryTime = minDeliveryTime;
            for (final Map.Entry<DeliveryBucket, List<CartItem>> bucketAndItems : shoppingCart.getCartItemMap().entrySet()) {
                final DeliveryBucket bucket = bucketAndItems.getKey();
                if (ff.getCode().equals(bucket.getSupplier())) {
                    final String dgroup = bucket.getGroup();
                    if (!CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(dgroup) && !checkedGroups.contains(dgroup)) {
                        final LocalDate deliveryTime = skipInventoryLeadTime(dgroup, ff, shoppingCart, bucketAndItems.getValue(), minDeliveryTime);
                        if (deliveryTime.isAfter(longestMinDeliveryTime)) {
                            longestMinDeliveryTime = deliveryTime;
                        }
                        checkedGroups.add(dgroup);
                    }
                }
            }
            minDeliveryTime = longestMinDeliveryTime; // advance to longest lead time for given supplier
        }

        final Map<LocalDate, LocalDate> slaExcludedDates = getCarrierSlaExcludedDates(sla);

        final int minDays = sla.getMinDays() != null ? sla.getMinDays() : 0;
        final int maxDays = sla.getMaxDays() != null ? sla.getMaxDays() : MAX_NAMED_DAY_IF_NOT_SET;

        if (minDays > 0) {
            minDeliveryTime = minDeliveryTime.plusDays(minDays);
        }

        // Set hard lower limit, before skipping exclusions
        LocalDate maxDeliveryTime = minDeliveryTime;

        minDeliveryTime = skipWeekdayExclusions(sla, minDeliveryTime);
        minDeliveryTime = skipDatesExclusions(sla, minDeliveryTime, slaExcludedDates);

        LocalDate min = minDeliveryTime;

        // Ensure we are not below lower limit before we check exclusions
        if (requestedDate.isBefore(minDeliveryTime)) {
            requestedDate = minDeliveryTime;
        }

        if (maxDays > minDays) {
            maxDeliveryTime = maxDeliveryTime.plusDays(maxDays - minDays);
        } else {
            maxDeliveryTime = maxDeliveryTime.plusDays(1); // Misconfiguration - just show one day
        }
        // We want a hard limit on max, so we do not skip beyond maxDays
        // skipWeekdayExclusions(sla, maxDeliveryTime);

        LocalDate max = maxDeliveryTime;

        final Map<LocalDate, LocalDate> exclusions = sla.getExcludeDatesAsMap();
        if (!exclusions.isEmpty() || !sla.getExcludeWeekDaysAsList().isEmpty()) {

            final StringBuilder exclusionsInMinMax = new StringBuilder();

            LocalDate nextDay = min;
            LocalDate lastValidDate = min;

            while (nextDay.isBefore(max)) {

                nextDay = nextDay.plusDays(1);

                LocalDate expectedNextDay = nextDay;

                nextDay = skipWeekdayExclusions(sla, nextDay);
                nextDay = skipDatesExclusions(sla, nextDay, exclusions);

                if (!expectedNextDay.isEqual(nextDay)) {
                    // we skipped
                    LocalDate excluded = expectedNextDay;

                    // Ensure requested date is not in exclusion range and if it is set the first day after the exclusion
                    if (!requestedDate.isBefore(expectedNextDay) && requestedDate.isBefore(nextDay)) {
                        requestedDate = nextDay;
                    }

                    do {

                        if (exclusionsInMinMax.length() > 0) {
                            exclusionsInMinMax.append(',');
                        }
                        exclusionsInMinMax.append(DateUtils.millis(excluded));
                        excluded = excluded.plusDays(1);

                    } while (excluded.isBefore(nextDay));
                } else {
                    // This date is not excluded
                    lastValidDate = expectedNextDay;
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
            if (lastValidDate.isBefore(max)) {
                max = lastValidDate;
            }
            if (requestedDate.isAfter(lastValidDate)) {
                requestedDate = lastValidDate;
            }

        }

        // Ensure we are not above upper limit after we checked exclusions
        if (requestedDate.isAfter(max)) {
            requestedDate = max;
        }

        // Save possible min - max range and preselect requested date, since this is named delivery
        shoppingCart.getOrderInfo().putDetail(requestedDateKey, String.valueOf(DateUtils.millis(requestedDate)));
        shoppingCart.getOrderInfo().putDetail(minKey, String.valueOf(DateUtils.millis(min)));
        shoppingCart.getOrderInfo().putDetail(maxKey, String.valueOf(DateUtils.millis(max)));

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

            LocalDate minDeliveryTime = now();

            // Honour warehouse lead inventory
            final Warehouse ff = warehouseByCode.get(customerOrderDelivery.getDetail().iterator().next().getSupplierCode());
            minDeliveryTime = skipInventoryLeadTime(customerOrderDelivery.getDeliveryGroup(), ff, customerOrderDelivery, (Collection) customerOrderDelivery.getDetail(), minDeliveryTime);

            boolean namedDay = sla.isNamedDay();

            final Map<LocalDate, LocalDate> slaExcludedDates = getCarrierSlaExcludedDates(sla);

            final int minDays = sla.getMinDays() != null ? sla.getMinDays() : 0;
            final int maxDays = sla.getMaxDays() != null ? sla.getMaxDays() : (namedDay ? MAX_NAMED_DAY_IF_NOT_SET : 0);

            // Ensure delivery lead time
            if (minDays > 0) {
                minDeliveryTime = minDeliveryTime.plusDays(minDays);
            }

            // Process exclusions to ensure that we do not set excluded day (For named day this should not make any changes
            // since we already checked it, this is just to ensure we do not have manual tampering with data)
            minDeliveryTime = skipWeekdayExclusions(sla, minDeliveryTime);
            minDeliveryTime = skipDatesExclusions(sla, minDeliveryTime, slaExcludedDates);

            // For named days ensure that requested date is valid
            if (namedDay) {
                // It must set and not before the lower limit
                if (customerOrderDelivery.getRequestedDeliveryDate() != null && !customerOrderDelivery.getRequestedDeliveryDate().isBefore(minDeliveryTime.atStartOfDay())) {

                    LocalDate checkRequested = customerOrderDelivery.getRequestedDeliveryDate().toLocalDate();

                    // Attempt to skip exclusion to see if the date changes
                    checkRequested = skipWeekdayExclusions(sla, checkRequested);
                    checkRequested = skipDatesExclusions(sla, checkRequested, slaExcludedDates);

                    if (customerOrderDelivery.getRequestedDeliveryDate().isEqual(checkRequested.atStartOfDay())) {
                        // We have not changed the date so it is valid, for named date we do not estimate
                        return;
                    }

                    // else the requested date is invalid, so we use the next available after the exclusions
                    minDeliveryTime = checkRequested;

                }

                // else requested date is less than minimal
            }

            LocalDate guaranteed = null;
            LocalDate min = null;
            LocalDate max = null;

            // Named day assumes that it is guaranteed
            if (sla.isGuaranteed() || namedDay) {
                if (CustomerOrderDelivery.STANDARD_DELIVERY_GROUP.equals(customerOrderDelivery.getDeliveryGroup())) {
                    // guarantee is only for in stock items
                    guaranteed = minDeliveryTime;
                } else {
                    min = minDeliveryTime;
                }

            } else {
                min = minDeliveryTime;
                if (maxDays > minDays) {
                    minDeliveryTime = skipWeekdayExclusions(sla, minDeliveryTime.plusDays(maxDays - minDays));
                }
                max = minDeliveryTime;
            }

            // Need to reset all in case we have dirty fields
            customerOrderDelivery.setDeliveryGuaranteed(guaranteed != null ? guaranteed.atStartOfDay() : null);
            customerOrderDelivery.setDeliveryEstimatedMin(min != null ? min.atStartOfDay() : null);
            customerOrderDelivery.setDeliveryEstimatedMax(max != null ? max.atStartOfDay() : null);

        }

    }

    /**
     * Skip lead time set by inventory
     *
     * @param deliveryGroup         delivery group
     * @param warehouse             fulfilment center
     * @param itemsContainer        items container (either CustomerOrderDelivery or ShoppingCart)
     * @param items                 items
     * @param minDeliveryTime       start date (i.e. now)
     */
    protected LocalDate skipInventoryLeadTime(final String deliveryGroup, final Warehouse warehouse, final Object itemsContainer, final Collection<CartItem> items, final LocalDate minDeliveryTime) {

        LocalDate min = minDeliveryTime;
        if (CustomerOrderDelivery.STANDARD_DELIVERY_GROUP.equals(deliveryGroup)) {

            final Warehouse ff = warehouse;
            if (ff != null && ff.getDefaultStandardStockLeadTime() > 0) {
                min = min.plusDays(ff.getDefaultStandardStockLeadTime());
            }

        } else if (!CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(deliveryGroup)) {
            // Pre, Back and Mixed (very simplistic) TODO: account for product specific lead times and pre-order release dates
            final Warehouse ff = warehouse;
            if (ff != null && ff.getDefaultBackorderStockLeadTime() > 0) {
                min = min.plusDays(ff.getDefaultBackorderStockLeadTime());
            }

        }
        return min;
    }

    protected LocalDate now() {
        return TimeContext.getLocalDate();
    }

    /**
     * Carrier SLA exclusion configuration.
     *
     * @param sla SLA
     *
     * @return exclusion date ranges
     */
    protected Map<LocalDate, LocalDate> getCarrierSlaExcludedDates(final CarrierSla sla) {

        return sla.getExcludeDatesAsMap();

    }

    /**
     * Skip over weekday carrier SLA exclusions.
     *
     * @param sla SLA
     * @param date current date
     *
     * @return date to skip to
     */
    protected LocalDate skipWeekdayExclusions(final CarrierSla sla, final LocalDate date) {

        LocalDate thisDate = date;
        final List<Integer> skip = sla.getExcludeWeekDaysAsList();
        if (!skip.isEmpty()) {
            final List<DayOfWeek> excluded = new ArrayList<>(DateUtils.fromCalendarDaysOfWeekToISO(skip));
            while (!excluded.isEmpty()) {
                final DayOfWeek dayOfWeek = thisDate.getDayOfWeek();
                final Iterator<DayOfWeek> itExcluded = excluded.iterator();
                boolean removed = false;
                while (itExcluded.hasNext()) {
                    if (dayOfWeek == itExcluded.next()) {
                        thisDate = thisDate.plusDays(1);  // This date is excluded
                        itExcluded.remove();
                        removed = true;
                    }
                }
                if (!removed) {
                    return thisDate;
                }
            }
        }
        return thisDate;

    }


    /**
     * Skip over date ranges.
     *
     * @param sla        SLA
     * @param startDate  current date
     * @param exclusions exclusion ranges
     *
     * @return date to skip to
     */
    protected LocalDate skipDatesExclusions(final CarrierSla sla, final LocalDate startDate, final Map<LocalDate, LocalDate> exclusions) {

        LocalDate thisDate = startDate;
        if (!exclusions.isEmpty()) {

            final TreeSet<LocalDate> startDates = new TreeSet<>(exclusions.keySet());

            while (true) {
                final LocalDate beforeOrEqual = startDates.floor(thisDate);
                if (beforeOrEqual == null) {
                    return thisDate; // no exclusions before
                } else if (beforeOrEqual.isBefore(thisDate)) {
                    final LocalDate rangeEnd = exclusions.get(beforeOrEqual);
                    // Two cases here:
                    // 1) Single date - same as beforeOfEqual
                    // 2) Range - need to make sure it is before this date
                    if (thisDate.isAfter(rangeEnd)) {
                        return thisDate; // This date is after the min in exclusions
                    } else {
                        thisDate = skipWeekdayExclusions(sla, rangeEnd.plusDays(1L));
                    }
                } else {
                    // equal, so need to move next day and check weekdays
                    thisDate = skipWeekdayExclusions(sla, thisDate.plusDays(1L));
                }
            }

        }
        return thisDate;

    }


}

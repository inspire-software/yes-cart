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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.CarrierSlaEntity;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.MutableOrderInfo;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.utils.DateUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 07/02/2017
 * Time: 13:03
 */
public class DeliveryTimeEstimationVisitorDefaultImplTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testSkipInventoryLeadTimeNoFf() throws Exception {

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");


        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, null, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, null, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.MIX_DELIVERY_GROUP, null, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, null, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, null, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP, null, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP, null, null, null, calendar)
        ));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipInventoryLeadTimeFfNoLead() throws Exception {

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        context.checking(new Expectations() {{
            allowing(warehouse).getDefaultStandardStockLeadTime(); will(returnValue(0));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(0));
        }});

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.MIX_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipInventoryLeadTimeFfWithLead() throws Exception {

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        context.checking(new Expectations() {{
            allowing(warehouse).getDefaultStandardStockLeadTime(); will(returnValue(2));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(4));
        }});

        assertEquals("2017-02-09", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        assertEquals("2017-02-11", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.MIX_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        assertEquals("2017-02-11", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        assertEquals("2017-02-11", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        assertEquals("2017-02-11", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        assertEquals("2017-02-11", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipInventoryLeadTime(CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP, warehouse, null, null, calendar)
        ));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipWeekdayExclusionsNoneOf() throws Exception {

        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        context.checking(new Expectations() {{
            allowing(sla).getExcludeWeekDaysAsList(); will(returnValue(Collections.emptyList()));
        }});

        new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipWeekdayExclusions(sla, calendar);

        assertEquals("2017-02-07", DateUtils.formatSD(calendar));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipWeekdayExclusionsSatSun() throws Exception {

        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, null);

        context.checking(new Expectations() {{
            allowing(sla).getExcludeWeekDaysAsList(); will(returnValue(Arrays.asList(1,7)));
        }});

        assertEquals("2017-02-06", DateUtils.formatSD(visitor.skipWeekdayExclusions(sla, DateUtils.ldParseSDT("2017-02-06"))));
        assertEquals("2017-02-07", DateUtils.formatSD(visitor.skipWeekdayExclusions(sla, DateUtils.ldParseSDT("2017-02-07"))));
        assertEquals("2017-02-08", DateUtils.formatSD(visitor.skipWeekdayExclusions(sla, DateUtils.ldParseSDT("2017-02-08"))));
        assertEquals("2017-02-09", DateUtils.formatSD(visitor.skipWeekdayExclusions(sla, DateUtils.ldParseSDT("2017-02-09"))));
        assertEquals("2017-02-10", DateUtils.formatSD(visitor.skipWeekdayExclusions(sla, DateUtils.ldParseSDT("2017-02-10"))));
        assertEquals("2017-02-13", DateUtils.formatSD(visitor.skipWeekdayExclusions(sla, DateUtils.ldParseSDT("2017-02-11"))));
        assertEquals("2017-02-13", DateUtils.formatSD(visitor.skipWeekdayExclusions(sla, DateUtils.ldParseSDT("2017-02-12"))));
        assertEquals("2017-02-13", DateUtils.formatSD(visitor.skipWeekdayExclusions(sla, DateUtils.ldParseSDT("2017-02-13"))));

        this.context.assertIsSatisfied();

    }


    @Test
    public void testGetCarrierSlaExcludedDatesNoneOf() throws Exception {

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("");

        final Map<LocalDate, LocalDate> dates = new DeliveryTimeEstimationVisitorDefaultImpl(null, null).getCarrierSlaExcludedDates(sla);

        assertNotNull(dates);
        assertTrue(dates.isEmpty());

    }


    @Test
    public void testGetCarrierSlaExcludedDates() throws Exception {

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-01-08,2017-01-10:2017-01-15,2017-01-17");

        LocalDate date, date2;

        final Map<LocalDate, LocalDate> dates = new DeliveryTimeEstimationVisitorDefaultImpl(null, null).getCarrierSlaExcludedDates(sla);

        assertNotNull(dates);
        assertEquals(3, dates.size());
        date = DateUtils.ldParseSDT("2017-01-08");
        assertEquals(date, dates.get(date));
        date = DateUtils.ldParseSDT("2017-01-10");
        date2 = DateUtils.ldParseSDT("2017-01-15");
        assertEquals(date2, dates.get(date));
        date = DateUtils.ldParseSDT("2017-01-17");
        assertEquals(date, dates.get(date));

    }



    @Test
    public void testSkipDatesExclusionsNoneOf() throws Exception {

        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        context.checking(new Expectations() {{
            allowing(sla).getExcludeWeekDays();
            will(returnValue(""));
        }});

        ;

        assertEquals("2017-02-07", DateUtils.formatSD(
                new DeliveryTimeEstimationVisitorDefaultImpl(null, null).skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-07"), Collections.<LocalDate, LocalDate>emptyMap())
        ));

        this.context.assertIsSatisfied();

    }



    @Test
    public void testSkipDatesExclusionsAllWeekdays() throws Exception {

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, null);

        final Map<LocalDate, LocalDate> dates = visitor.getCarrierSlaExcludedDates(sla);

        assertEquals("2017-02-07", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-07"), dates)));
        assertEquals("2017-02-09", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-08"), dates)));
        assertEquals("2017-02-09", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-09"), dates)));
        assertEquals("2017-02-16", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-10"), dates)));
        assertEquals("2017-02-16", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-11"), dates)));
        assertEquals("2017-02-16", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-12"), dates)));
        assertEquals("2017-02-16", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-13"), dates)));
        assertEquals("2017-02-16", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-14"), dates)));
        assertEquals("2017-02-16", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-15"), dates)));
        assertEquals("2017-02-16", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-16"), dates)));
        assertEquals("2017-02-18", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-17"), dates)));
        assertEquals("2017-02-18", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-18"), dates)));

    }



    @Test
    public void testSkipDatesExclusionsNoThuSatSun() throws Exception {

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5,7");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, null);

        final Map<LocalDate, LocalDate> dates = visitor.getCarrierSlaExcludedDates(sla);

        assertEquals("2017-02-07", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-07"), dates)));
        assertEquals("2017-02-20", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-08"), dates)));
        // Must skip weekdays first! #skipDatesExclusions() is always called after #skipWeekdayExclusions()
        assertEquals("2017-02-09", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-09"), dates)));
        assertEquals("2017-02-20", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-10"), dates)));
        assertEquals("2017-02-20", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-11"), dates)));
        assertEquals("2017-02-20", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-12"), dates)));
        assertEquals("2017-02-20", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-13"), dates)));
        assertEquals("2017-02-20", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-14"), dates)));
        assertEquals("2017-02-20", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-15"), dates)));
        // Must skip weekdays first! #skipDatesExclusions() is always called after #skipWeekdayExclusions()
        assertEquals("2017-02-16", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-16"), dates)));
        assertEquals("2017-02-20", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-17"), dates)));
        // Must skip weekdays first! #skipDatesExclusions() is always called after #skipWeekdayExclusions()
        assertEquals("2017-02-18", DateUtils.formatSD(visitor.skipDatesExclusions(sla, DateUtils.ldParseSDT("2017-02-18"), dates)));

    }



    @Test
    public void testDeliveryTimeFullEstimatedNoFf() throws Exception {

        final WarehouseService warehouseService = this.context.mock(WarehouseService.class, "warehouseService");

        final Shop shop = this.context.mock(Shop.class, "shop");
        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5,7");
        sla.setGuaranteed(false);
        sla.setMinDays(1);
        sla.setMaxDays(5);


        LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(warehouseService, null) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(delivery).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getCarrierSla(); will(returnValue(sla));
            allowing(order).getShop(); will(returnValue(shop));
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(warehouseService).getByShopIdMapped(123L, false); will(returnValue(Collections.singletonMap("ABC", warehouse)));
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("CED"));
            // expectations:
            oneOf(delivery).setDeliveryGuaranteed(null);
            oneOf(delivery).setDeliveryEstimatedMin(with(equal(DateUtils.ldtParseSDT("2017-02-20"))));
            oneOf(delivery).setDeliveryEstimatedMax(with(equal(DateUtils.ldtParseSDT("2017-02-24"))));

        }});

        visitor.visit(delivery);

        this.context.assertIsSatisfied();

    }


    @Test
    public void testDeliveryTimeFullEstimatedNamedNoFf() throws Exception {

        final WarehouseService warehouseService = this.context.mock(WarehouseService.class, "warehouseService");

        final Shop shop = this.context.mock(Shop.class, "shop");
        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5,7");
        sla.setGuaranteed(false);
        sla.setNamedDay(true);
        sla.setMinDays(1);
        sla.setMaxDays(5);


        LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(warehouseService, null) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(delivery).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getCarrierSla(); will(returnValue(sla));
            allowing(delivery).getRequestedDeliveryDate(); will(returnValue(DateUtils.ldtParseSDT("2017-02-20")));
            allowing(order).getShop(); will(returnValue(shop));
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(warehouseService).getByShopIdMapped(123L, false); will(returnValue(Collections.singletonMap("ABC", warehouse)));
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("CED"));
            // expectations: NONE, requested date is valid

        }});

        visitor.visit(delivery);

        this.context.assertIsSatisfied();

    }



    @Test
    public void testDeliveryTimeFullEstimatedNamedNoFfInvalid() throws Exception {

        final WarehouseService warehouseService = this.context.mock(WarehouseService.class, "warehouseService");

        final Shop shop = this.context.mock(Shop.class, "shop");
        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5,7");
        sla.setGuaranteed(false);
        sla.setNamedDay(true);
        sla.setMinDays(1);
        sla.setMaxDays(5);


        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(warehouseService, null) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(delivery).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getCarrierSla(); will(returnValue(sla));
            allowing(delivery).getRequestedDeliveryDate(); will(returnValue(DateUtils.ldtParseSDT("2017-02-12")));
            allowing(order).getShop(); will(returnValue(shop));
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(warehouseService).getByShopIdMapped(123L, false); will(returnValue(Collections.singletonMap("ABC", warehouse)));
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("CED"));
            // expectations: NONE, requested date is valid
            oneOf(delivery).setDeliveryGuaranteed(with(equal(DateUtils.ldtParseSDT("2017-02-20"))));
            oneOf(delivery).setDeliveryEstimatedMin(null);
            oneOf(delivery).setDeliveryEstimatedMax(null);

        }});

        visitor.visit(delivery);

        this.context.assertIsSatisfied();

    }


    @Test
    public void testDeliveryTimeFullEstimatedNamedNoFfInvalidBackOrder() throws Exception {

        final WarehouseService warehouseService = this.context.mock(WarehouseService.class, "warehouseService");

        final Shop shop = this.context.mock(Shop.class, "shop");
        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5,7");
        sla.setGuaranteed(false);
        sla.setNamedDay(true);
        sla.setMinDays(1);
        sla.setMaxDays(5);


        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(warehouseService, null) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(delivery).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getCarrierSla(); will(returnValue(sla));
            allowing(delivery).getRequestedDeliveryDate(); will(returnValue(DateUtils.ldtParseSDT("2017-02-12")));
            allowing(order).getShop(); will(returnValue(shop));
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(warehouseService).getByShopIdMapped(123L, false); will(returnValue(Collections.singletonMap("ABC", warehouse)));
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("CED"));
            // expectations: NONE, requested date is valid
            oneOf(delivery).setDeliveryGuaranteed(null);
            oneOf(delivery).setDeliveryEstimatedMin(with(equal(DateUtils.ldtParseSDT("2017-02-20"))));
            oneOf(delivery).setDeliveryEstimatedMax(null);

        }});

        visitor.visit(delivery);

        this.context.assertIsSatisfied();

    }


    @Test
    public void testDeliveryTimeFullEstimatedFf() throws Exception {

        final WarehouseService warehouseService = this.context.mock(WarehouseService.class, "warehouseService");

        final Shop shop = this.context.mock(Shop.class, "shop");
        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5,7");
        sla.setGuaranteed(false);
        sla.setMinDays(1);
        sla.setMaxDays(5);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(warehouseService, null) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(delivery).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getCarrierSla(); will(returnValue(sla));
            allowing(order).getShop(); will(returnValue(shop));
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(warehouseService).getByShopIdMapped(123L, false); will(returnValue(Collections.singletonMap("ABC", warehouse)));
            allowing(warehouse).getDefaultStandardStockLeadTime(); will(returnValue(5));
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("ABC"));
            // expectations:
            oneOf(delivery).setDeliveryGuaranteed(null);
            oneOf(delivery).setDeliveryEstimatedMin(with(equal(DateUtils.ldtParseSDT("2017-02-20"))));
            oneOf(delivery).setDeliveryEstimatedMax(with(equal(DateUtils.ldtParseSDT("2017-02-24"))));

        }});

        visitor.visit(delivery);

        this.context.assertIsSatisfied();

    }


    @Test
    public void testDeliveryTimeFullGuaranteedNoFf() throws Exception {

        final WarehouseService warehouseService = this.context.mock(WarehouseService.class, "warehouseService");

        final Shop shop = this.context.mock(Shop.class, "shop");
        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5");
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(5);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(warehouseService, null) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(delivery).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getCarrierSla(); will(returnValue(sla));
            allowing(order).getShop(); will(returnValue(shop));
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(warehouseService).getByShopIdMapped(123L, false); will(returnValue(Collections.singletonMap("ABC", warehouse)));
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("CED"));
            // expectations:
            oneOf(delivery).setDeliveryGuaranteed(with(equal(DateUtils.ldtParseSDT("2017-02-18"))));
            oneOf(delivery).setDeliveryEstimatedMin(null);
            oneOf(delivery).setDeliveryEstimatedMax(null);

        }});

        visitor.visit(delivery);

        this.context.assertIsSatisfied();

    }


    @Test
    public void testDeliveryTimeFullGuaranteedFf() throws Exception {

        final WarehouseService warehouseService = this.context.mock(WarehouseService.class, "warehouseService");

        final Shop shop = this.context.mock(Shop.class, "shop");
        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5");
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(5);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(warehouseService, null) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(delivery).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getCarrierSla(); will(returnValue(sla));
            allowing(order).getShop(); will(returnValue(shop));
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(warehouseService).getByShopIdMapped(123L, false); will(returnValue(Collections.singletonMap("ABC", warehouse)));
            allowing(warehouse).getDefaultStandardStockLeadTime(); will(returnValue(5));
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("ABC"));
            // expectations:
            oneOf(delivery).setDeliveryGuaranteed(with(equal(DateUtils.ldtParseSDT("2017-02-18"))));
            oneOf(delivery).setDeliveryEstimatedMin(null);
            oneOf(delivery).setDeliveryEstimatedMax(null);

        }});

        visitor.visit(delivery);

        this.context.assertIsSatisfied();

    }

    @Test
    public void testDeliveryTimeFullGuaranteedBackorderNoFf() throws Exception {

        final WarehouseService warehouseService = this.context.mock(WarehouseService.class, "warehouseService");

        final Shop shop = this.context.mock(Shop.class, "shop");
        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5");
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(5);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(warehouseService, null) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(delivery).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getCarrierSla(); will(returnValue(sla));
            allowing(order).getShop(); will(returnValue(shop));
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(warehouseService).getByShopIdMapped(123L, false); will(returnValue(Collections.singletonMap("ABC", warehouse)));
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("CED"));
            // expectations:
            oneOf(delivery).setDeliveryGuaranteed(null);
            oneOf(delivery).setDeliveryEstimatedMin(with(equal(DateUtils.ldtParseSDT("2017-02-18"))));
            oneOf(delivery).setDeliveryEstimatedMax(null);

        }});

        visitor.visit(delivery);

        this.context.assertIsSatisfied();

    }

    @Test
    public void testDeliveryTimeFullGuaranteedBackorderFf() throws Exception {

        final WarehouseService warehouseService = this.context.mock(WarehouseService.class, "warehouseService");

        final Shop shop = this.context.mock(Shop.class, "shop");
        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5");
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(5);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(warehouseService, null) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(delivery).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getCarrierSla(); will(returnValue(sla));
            allowing(order).getShop(); will(returnValue(shop));
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(warehouseService).getByShopIdMapped(123L, false); will(returnValue(Collections.singletonMap("ABC", warehouse)));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(5));
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("ABC"));
            // expectations:
            oneOf(delivery).setDeliveryGuaranteed(null);
            oneOf(delivery).setDeliveryEstimatedMin(with(equal(DateUtils.ldtParseSDT("2017-02-18"))));
            oneOf(delivery).setDeliveryEstimatedMax(null);


        }});

        visitor.visit(delivery);

        this.context.assertIsSatisfied();

    }



    @Test
    public void testDetermineDeliveryAvailableTimeRangeNoSla() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{

        }});

        visitor.determineDeliveryAvailableTimeRange(shoppingCart, null, null);

        this.context.assertIsSatisfied();

    }

    @Test
    public void testDetermineDeliveryAvailableTimeRangeNoFf() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");

        final CarrierSlaEntity sla = new CarrierSlaEntity();

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(null));
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", null);
        }});

        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, null);

        this.context.assertIsSatisfied();

    }

    @Test
    public void testDetermineDeliveryAvailableTimeRange() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");

        final CarrierSlaEntity sla = new CarrierSlaEntity();

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue(null));
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
        }});

        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, warehouse);

        this.context.assertIsSatisfied();

    }

    @Test
    public void testDetermineDeliveryAvailableTimeRangeNamedNoFfExclusionsSkipFromStart() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(null));
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDate0", time("2017-02-18")); // Sat 18th Feb (this is min)
            oneOf(orderInfo).putDetail("deliveryDateMin0", time("2017-02-18")); // Sat 18th Feb (since we skip)
            oneOf(orderInfo).putDetail("deliveryDateMax0", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", time("2017-02-19")); // Sun 19th Feb Thu+Sun between 18th and 22nd
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", "1,5");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, null);

        this.context.assertIsSatisfied();

    }

    @Test
    public void testDetermineDeliveryAvailableTimeRangeNamedNoFfExclusionsSkipMiddle() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(null));
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDate0", time("2017-02-09"));  // Thu 9th Feb  (this is min)
            oneOf(orderInfo).putDetail("deliveryDateMin0", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            oneOf(orderInfo).putDetail("deliveryDateMax0", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            // 10th-15th + 17th + Sun between 8th and 22nd> Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fri 17th Feb  Sun 19th Feb
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19"));
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", "1");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, null);

        this.context.assertIsSatisfied();

    }

    @Test
    public void testDetermineDeliveryAvailableTimeRangeNamedNoFfExclusionsSkipMiddleInvalidRequested() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(time("2017-02-11"))); // Sat 11th Feb
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDate0", time("2017-02-16"));  // Thu 16th Feb  (this first available after 11th)
            oneOf(orderInfo).putDetail("deliveryDateMin0", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            oneOf(orderInfo).putDetail("deliveryDateMax0", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            // 10th-15th + 17th + Sun between 8th and 22nd> Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fir 17th Feb  Sun 19th Feb
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19"));
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", "1");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, null);

        this.context.assertIsSatisfied();

    }



    @Test
    public void testDetermineDeliveryAvailableTimeRangeNamedNoFfExclusionsSkipMiddleInvalidRequestedLastExcluded() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17,2017-02-22");
        sla.setExcludeWeekDays("1");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(time("2017-02-22"))); // Wed 22nd Feb
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDate0", time("2017-02-21"));  // Thu 21st Feb  (adjusted to be in range)
            oneOf(orderInfo).putDetail("deliveryDateMin0", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            oneOf(orderInfo).putDetail("deliveryDateMax0", time("2017-02-21")); // Tue 21st Feb (7th + 15days max, 22nd exluded)
            // 10th-15th + 17th + Sun between 8th and 22nd> Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fri 17th Feb  Sun 19th Feb  Wed 22nd Feb
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19","2017-02-22"));
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", "1");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, null);

        this.context.assertIsSatisfied();

    }



    @Test
    public void testDetermineDeliveryAvailableTimeRangeNamedNoFfExclusionsSkipMiddleInvalidRequestedAfterLast() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17,2017-02-22");
        sla.setExcludeWeekDays("1");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(time("2017-03-25"))); // Sat 25th Mar
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDate0", time("2017-02-21"));  // Thu 21st Feb  (adjusted to be in range)
            oneOf(orderInfo).putDetail("deliveryDateMin0", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            oneOf(orderInfo).putDetail("deliveryDateMax0", time("2017-02-21")); // Tue 21st Feb (7th + 15days max, 22nd exluded)
            // 10th-15th + 17th + Sun between 8th and 22nd> Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fri 17th Feb  Sun 19th Feb  Wed 22nd Feb
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19","2017-02-22"));
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", "1");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, null);

        this.context.assertIsSatisfied();

    }


    @Test
    public void testDetermineDeliveryAvailableTimeRangeNamedNoFfExclusionsSkipMiddleValidRequested() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(time("2017-02-18"))); // Sat 18th Feb
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", null);
            oneOf(orderInfo).putDetail("deliveryDate0", time("2017-02-18"));  // Thu 18th Feb  (selection preserved)
            oneOf(orderInfo).putDetail("deliveryDateMin0", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            oneOf(orderInfo).putDetail("deliveryDateMax0", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            // 10th-15th + 17th + Sun between 8th and 22nd> Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fri 17th Feb  Sun 19th Feb
            oneOf(orderInfo).putDetail("deliveryDateDExcl0", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19"));
            oneOf(orderInfo).putDetail("deliveryDateWExcl0", "1");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, null);

        this.context.assertIsSatisfied();

    }


    @Test
    public void testDetermineDeliveryAvailableTimeRangeNamedExclusionsSkipFromStart() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");
        final CartItem item1 = this.context.mock(CartItem.class, "item1");
        final DeliveryBucket bucket1 = this.context.mock(DeliveryBucket.class, "bucket1");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(shoppingCart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Collections.singletonList(item1))));
            allowing(item1).getDeliveryBucket(); will(returnValue(bucket1));
            allowing(bucket1).getSupplier(); will(returnValue("ABC"));
            allowing(bucket1).getGroup(); will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(0));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue(null));
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDate0ABC", time("2017-02-18")); // Sat 18th Feb (this is min)
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", time("2017-02-18")); // Sat 18th Feb (since we skip)
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", time("2017-02-19")); // Sun 19th Feb Thu+Sun between 18th and 22nd
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", "1,5");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, warehouse);

        this.context.assertIsSatisfied();

    }

    @Test
    public void testDetermineDeliveryAvailableTimeRangeNamedExclusionsSkipMiddle() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");
        final CartItem item1 = this.context.mock(CartItem.class, "item1");
        final DeliveryBucket bucket1 = this.context.mock(DeliveryBucket.class, "bucket1");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(shoppingCart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Collections.singletonList(item1))));
            allowing(item1).getDeliveryBucket(); will(returnValue(bucket1));
            allowing(bucket1).getSupplier(); will(returnValue("ABC"));
            allowing(bucket1).getGroup(); will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(0));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue(null));
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDate0ABC", time("2017-02-09"));  // Thu 9th Feb  (this is min)
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            // 10th-15th + 17th + Sun between 8th and 22nd>    Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fir 17th Feb  Sun 19th Feb
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19"));
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", "1");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, warehouse);

        this.context.assertIsSatisfied();

    }


    @Test
    public void testDetermineDeliveryAvailableTimeRangeNamedExclusionsSkipMiddleInvalidRequested() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");
        final CartItem item1 = this.context.mock(CartItem.class, "item1");
        final DeliveryBucket bucket1 = this.context.mock(DeliveryBucket.class, "bucket1");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(shoppingCart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Collections.singletonList(item1))));
            allowing(item1).getDeliveryBucket(); will(returnValue(bucket1));
            allowing(bucket1).getSupplier(); will(returnValue("ABC"));
            allowing(bucket1).getGroup(); will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(0));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue("1486771200000")); // Sat 11th Feb
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDate0ABC", time("2017-02-16"));  // Thu 16th Feb  (this first available after 11th)
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            // 10th-15th + 17th + Sun between 8th and 22nd>    Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fir 17th Feb  Sun 19th Feb
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19"));
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", "1");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, warehouse);

        this.context.assertIsSatisfied();

    }




    @Test
    public void testDetermineDeliveryAvailableTimeRangeNamedExclusionsSkipMiddleValidRequested() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");
        final CartItem item1 = this.context.mock(CartItem.class, "item1");
        final DeliveryBucket bucket1 = this.context.mock(DeliveryBucket.class, "bucket1");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(shoppingCart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Collections.singletonList(item1))));
            allowing(item1).getDeliveryBucket(); will(returnValue(bucket1));
            allowing(bucket1).getSupplier(); will(returnValue("ABC"));
            allowing(bucket1).getGroup(); will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(0));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue(time("2017-02-18"))); // Sat 18th Feb
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDate0ABC", time("2017-02-18"));  // Thu 18th Feb  (selection preserved)
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            // 10th-15th + 17th + Sun between 8th and 22nd>    Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fir 17th Feb  Sun 19th Feb
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19"));
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", "1");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, warehouse);

        this.context.assertIsSatisfied();

    }


    @Test
    public void testDetermineDeliveryAvailableTimeRangeNamedExclusionsLeadSkipFromStart() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");
        final CartItem item1 = this.context.mock(CartItem.class, "item1");
        final DeliveryBucket bucket1 = this.context.mock(DeliveryBucket.class, "bucket1");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(shoppingCart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Collections.singletonList(item1))));
            allowing(item1).getDeliveryBucket(); will(returnValue(bucket1));
            allowing(bucket1).getSupplier(); will(returnValue("ABC"));
            allowing(bucket1).getGroup(); will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(10));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue(null));
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDate0ABC", time("2017-02-18")); // Sat 18th Feb (this is min)
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", time("2017-02-18")); // Sat 18th Feb (since we skip)
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", time("2017-03-04")); // Sat 4th Mar (7th + 10days lead + 15days max)
            // Thu+Sun between 18th and 22nd >                 Sun 19th Feb  Thu 23rd Feb  Sun 26th Feb  Thu 2nd Mar
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", time("2017-02-19","2017-02-23","2017-02-26","2017-03-02"));
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", "1,5");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, warehouse);

        this.context.assertIsSatisfied();

    }

    @Test
    public void testDetermineDeliveryAvailableTimeRangeNamedExclusionsLeadSkipMiddle() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");
        final MutableOrderInfo orderInfo = this.context.mock(MutableOrderInfo.class, "orderInfo");
        final CartItem item1 = this.context.mock(CartItem.class, "item1");
        final DeliveryBucket bucket1 = this.context.mock(DeliveryBucket.class, "bucket1");

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-03-10:2017-03-15,2017-03-17");
        sla.setExcludeWeekDays("1");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(60);

        final LocalDate calendar = DateUtils.ldParseSDT("2017-02-07");

        final DeliveryTimeEstimationVisitorDefaultImpl visitor = new DeliveryTimeEstimationVisitorDefaultImpl(null, carrierSlaService) {
            @Override
            protected LocalDate now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(shoppingCart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Collections.singletonList(item1))));
            allowing(item1).getDeliveryBucket(); will(returnValue(bucket1));
            allowing(bucket1).getSupplier(); will(returnValue("ABC"));
            allowing(bucket1).getGroup(); will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(15));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue(null));
            // expectations:
            oneOf(orderInfo).putDetail("deliveryDate0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
            oneOf(orderInfo).putDetail("deliveryDate0ABC", time("2017-02-23"));  // Thu 23rd Feb  (this is min)
            oneOf(orderInfo).putDetail("deliveryDateMin0ABC", time("2017-02-23"));  // Thu 23rd Feb (15+1day lead 8th skipped)
            oneOf(orderInfo).putDetail("deliveryDateMax0ABC", time("2017-04-22")); // Sat Apr 22nd (22nd + 60days max)
            // 10th-15th + 17th + Sun between 23rd and 21st>   Sun 26th Feb  Sun 5th Mar   Fri 10th Mar  Sat 11th Mar  Sun 12th Mar  Mon 13th Mar  Tue 14th Mar  Wed 15th Mar  Fri 17th Mar  Sun 19th Mar  Sun 26th Mar  Sun 2nd Apr   Sun 9th Apr   Sun 16th Apr  Sun 23rd Apr
            oneOf(orderInfo).putDetail("deliveryDateDExcl0ABC", time("2017-02-26","2017-03-05","2017-03-10","2017-03-11","2017-03-12","2017-03-13","2017-03-14","2017-03-15","2017-03-17","2017-03-19","2017-03-26","2017-04-02","2017-04-09","2017-04-16","2017-04-23"));
            oneOf(orderInfo).putDetail("deliveryDateWExcl0ABC", "1");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, warehouse);

        this.context.assertIsSatisfied();

    }

    private String time(String date) throws Exception {
        return String.valueOf(DateUtils.millis(DateUtils.ldtParseSDT(date)));
    }

    private String time(String ... dates) throws Exception {
        final StringBuilder time = new StringBuilder();
        for (final String date : dates) {
            if (time.length() > 0) {
                time.append(',');
            }
            time.append(time(date));
        }
        return time.toString();
    }

}
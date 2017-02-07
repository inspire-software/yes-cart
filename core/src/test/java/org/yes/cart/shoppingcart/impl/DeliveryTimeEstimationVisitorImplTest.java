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
import org.yes.cart.service.domain.WarehouseService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 07/02/2017
 * Time: 13:03
 */
public class DeliveryTimeEstimationVisitorImplTest {

    private final Mockery context = new JUnit4Mockery();

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");


    @Test
    public void testSkipInventoryLeadTimeNoFf() throws Exception {

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        context.checking(new Expectations() {{
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("CED"));
        }});

        new DeliveryTimeEstimationVisitorImpl(null).skipInventoryLeadTime(delivery, Collections.singletonMap("ABC", warehouse), calendar);

        assertEquals("2017-02-07", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipInventoryLeadTimeFfNoLead() throws Exception {

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        context.checking(new Expectations() {{
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultStandardStockLeadTime(); will(returnValue(0));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(0));
        }});

        new DeliveryTimeEstimationVisitorImpl(null).skipInventoryLeadTime(delivery, Collections.singletonMap("ABC", warehouse), calendar);

        assertEquals("2017-02-07", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipInventoryLeadTimeFfStdLead() throws Exception {

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        context.checking(new Expectations() {{
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultStandardStockLeadTime(); will(returnValue(2));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(0));
        }});

        new DeliveryTimeEstimationVisitorImpl(null).skipInventoryLeadTime(delivery, Collections.singletonMap("ABC", warehouse), calendar);

        assertEquals("2017-02-09", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipInventoryLeadTimeFfBoLead() throws Exception {

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        context.checking(new Expectations() {{
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultStandardStockLeadTime(); will(returnValue(0));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(2));
        }});

        new DeliveryTimeEstimationVisitorImpl(null).skipInventoryLeadTime(delivery, Collections.singletonMap("ABC", warehouse), calendar);

        assertEquals("2017-02-07", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipInventoryLeadTimeFfBoLead2() throws Exception {

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        context.checking(new Expectations() {{
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.MIX_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultStandardStockLeadTime(); will(returnValue(0));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(2));
        }});

        new DeliveryTimeEstimationVisitorImpl(null).skipInventoryLeadTime(delivery, Collections.singletonMap("ABC", warehouse), calendar);

        assertEquals("2017-02-09", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipWeekdayExclusionsNone() throws Exception {

        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        context.checking(new Expectations() {{
            allowing(sla).getExcludeWeekDays(); will(returnValue(""));
        }});

        new DeliveryTimeEstimationVisitorImpl(null).skipWeekdayExclusions(sla, calendar);

        assertEquals("2017-02-07", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipWeekdayExclusionsSatSun() throws Exception {

        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();

        context.checking(new Expectations() {{
            allowing(sla).getExcludeWeekDays(); will(returnValue("1,7"));
        }});

        calendar.setTime(df.parse("2017-02-06"));
        new DeliveryTimeEstimationVisitorImpl(null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-06", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-07"));
        new DeliveryTimeEstimationVisitorImpl(null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-07", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-08"));
        new DeliveryTimeEstimationVisitorImpl(null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-08", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-09"));
        new DeliveryTimeEstimationVisitorImpl(null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-09", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-10"));
        new DeliveryTimeEstimationVisitorImpl(null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-10", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-11"));
        new DeliveryTimeEstimationVisitorImpl(null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-13", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-12"));
        new DeliveryTimeEstimationVisitorImpl(null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-13", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-13"));
        new DeliveryTimeEstimationVisitorImpl(null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-13", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }


    @Test
    public void testGetCarrierSlaExcludedDatesNone() throws Exception {

        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        context.checking(new Expectations() {{
            allowing(sla).getExcludeDates(); will(returnValue(""));
        }});

        final Map<Date, Date> dates = new DeliveryTimeEstimationVisitorImpl(null).getCarrierSlaExcludedDates(sla);

        assertNotNull(dates);
        assertTrue(dates.isEmpty());

        this.context.assertIsSatisfied();

    }


    @Test
    public void testGetCarrierSlaExcludedDates() throws Exception {

        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");
        Date date, date2;

        context.checking(new Expectations() {{
            allowing(sla).getExcludeDates(); will(returnValue("2017-01-08,2017-01-10:2017-01-15,2017-01-17"));
        }});

        final Map<Date, Date> dates = new DeliveryTimeEstimationVisitorImpl(null).getCarrierSlaExcludedDates(sla);

        assertNotNull(dates);
        assertEquals(3, dates.size());
        date = df.parse("2017-01-08");
        assertEquals(date, dates.get(date));
        date = df.parse("2017-01-10");
        date2 = df.parse("2017-01-15");
        assertEquals(date2, dates.get(date));
        date = df.parse("2017-01-17");
        assertEquals(date, dates.get(date));

        this.context.assertIsSatisfied();

    }



    @Test
    public void testSkipDatesExclusionsNone() throws Exception {

        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        context.checking(new Expectations() {{
            allowing(sla).getExcludeWeekDays();
            will(returnValue(""));
        }});

        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, Collections.<Date, Date>emptyMap());

        assertEquals("2017-02-07", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }



    @Test
    public void testSkipDatesExclusionsAllWeekdays() throws Exception {

        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();

        context.checking(new Expectations() {{
            allowing(sla).getExcludeDates(); will(returnValue("2017-02-08,2017-02-10:2017-02-15,2017-02-17"));
            allowing(sla).getExcludeWeekDays(); will(returnValue(""));
        }});

        final Map<Date, Date> dates = new DeliveryTimeEstimationVisitorImpl(null).getCarrierSlaExcludedDates(sla);

        calendar.setTime(df.parse("2017-02-07"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-07", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-08"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-09", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-09"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-09", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-10"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-11"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-12"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-13"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-14"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-15"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-16"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-17"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-18", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-18"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-18", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }



    @Test
    public void testSkipDatesExclusionsNoThuSatSun() throws Exception {

        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();

        context.checking(new Expectations() {{
            allowing(sla).getExcludeDates(); will(returnValue("2017-02-08,2017-02-10:2017-02-15,2017-02-17"));
            allowing(sla).getExcludeWeekDays(); will(returnValue("1,5,7"));
        }});

        final Map<Date, Date> dates = new DeliveryTimeEstimationVisitorImpl(null).getCarrierSlaExcludedDates(sla);

        calendar.setTime(df.parse("2017-02-07"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-07", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-08"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-09"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-09", df.format(calendar.getTime())); // Must skip weekdays first!

        calendar.setTime(df.parse("2017-02-10"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-11"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-12"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-13"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-14"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-15"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-16"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));  // Must skip weekdays first!

        calendar.setTime(df.parse("2017-02-17"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-18"));
        new DeliveryTimeEstimationVisitorImpl(null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-18", df.format(calendar.getTime()));  // Must skip weekdays first!

        this.context.assertIsSatisfied();

    }



    @Test
    public void testDeliveryTimeFullEstimatedNoFf() throws Exception {

        final WarehouseService warehouseService = this.context.mock(WarehouseService.class, "warehouseService");

        final Shop shop = this.context.mock(Shop.class, "shop");
        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrder order = this.context.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");
        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService) {
            @Override
            protected Calendar now() {
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
            allowing(sla).getExcludeDates(); will(returnValue("2017-02-08,2017-02-10:2017-02-15,2017-02-17"));
            allowing(sla).getExcludeWeekDays(); will(returnValue("1,5,7"));
            allowing(sla).isGuaranteed(); will(returnValue(false));
            allowing(sla).getMinDays(); will(returnValue(1));
            allowing(sla).getMaxDays(); will(returnValue(5));
            // expectations:
            one(delivery).setDeliveryGuaranteed(null);
            one(delivery).setDeliveryEstimatedMin(with(equal(df.parse("2017-02-20"))));
            one(delivery).setDeliveryEstimatedMax(with(equal(df.parse("2017-02-24"))));

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
        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService) {
            @Override
            protected Calendar now() {
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
            allowing(sla).getExcludeDates(); will(returnValue("2017-02-08,2017-02-10:2017-02-15,2017-02-17"));
            allowing(sla).getExcludeWeekDays(); will(returnValue("1,5,7"));
            allowing(sla).isGuaranteed(); will(returnValue(false));
            allowing(sla).getMinDays(); will(returnValue(1));
            allowing(sla).getMaxDays(); will(returnValue(5));
            // expectations:
            one(delivery).setDeliveryGuaranteed(null);
            one(delivery).setDeliveryEstimatedMin(with(equal(df.parse("2017-02-20"))));
            one(delivery).setDeliveryEstimatedMax(with(equal(df.parse("2017-02-24"))));

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
        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService) {
            @Override
            protected Calendar now() {
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
            allowing(sla).getExcludeDates(); will(returnValue("2017-02-08,2017-02-10:2017-02-15,2017-02-17"));
            allowing(sla).getExcludeWeekDays(); will(returnValue("1,5"));
            allowing(sla).isGuaranteed(); will(returnValue(true));
            allowing(sla).getMinDays(); will(returnValue(1));
            allowing(sla).getMaxDays(); will(returnValue(5));
            // expectations:
            one(delivery).setDeliveryGuaranteed(with(equal(df.parse("2017-02-18"))));
            one(delivery).setDeliveryEstimatedMin(null);
            one(delivery).setDeliveryEstimatedMax(null);

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
        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService) {
            @Override
            protected Calendar now() {
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
            allowing(sla).getExcludeDates(); will(returnValue("2017-02-08,2017-02-10:2017-02-15,2017-02-17"));
            allowing(sla).getExcludeWeekDays(); will(returnValue("1,5"));
            allowing(sla).isGuaranteed(); will(returnValue(true));
            allowing(sla).getMinDays(); will(returnValue(1));
            allowing(sla).getMaxDays(); will(returnValue(5));
            // expectations:
            one(delivery).setDeliveryGuaranteed(with(equal(df.parse("2017-02-18"))));
            one(delivery).setDeliveryEstimatedMin(null);
            one(delivery).setDeliveryEstimatedMax(null);

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
        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService) {
            @Override
            protected Calendar now() {
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
            allowing(sla).getExcludeDates(); will(returnValue("2017-02-08,2017-02-10:2017-02-15,2017-02-17"));
            allowing(sla).getExcludeWeekDays(); will(returnValue("1,5"));
            allowing(sla).isGuaranteed(); will(returnValue(true));
            allowing(sla).getMinDays(); will(returnValue(1));
            allowing(sla).getMaxDays(); will(returnValue(5));
            // expectations:
            one(delivery).setDeliveryGuaranteed(null);
            one(delivery).setDeliveryEstimatedMin(with(equal(df.parse("2017-02-18"))));
            one(delivery).setDeliveryEstimatedMax(null);

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
        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService) {
            @Override
            protected Calendar now() {
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
            allowing(sla).getExcludeDates(); will(returnValue("2017-02-08,2017-02-10:2017-02-15,2017-02-17"));
            allowing(sla).getExcludeWeekDays(); will(returnValue("1,5"));
            allowing(sla).isGuaranteed(); will(returnValue(true));
            allowing(sla).getMinDays(); will(returnValue(1));
            allowing(sla).getMaxDays(); will(returnValue(5));
            // expectations:
            one(delivery).setDeliveryGuaranteed(null);
            one(delivery).setDeliveryEstimatedMin(with(equal(df.parse("2017-02-18"))));
            one(delivery).setDeliveryEstimatedMax(null);


        }});

        visitor.visit(delivery);

        this.context.assertIsSatisfied();

    }

}
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

import java.text.SimpleDateFormat;
import java.util.*;

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

        new DeliveryTimeEstimationVisitorImpl(null, null).skipInventoryLeadTime(delivery, Collections.singletonMap("ABC", warehouse), calendar);

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

        new DeliveryTimeEstimationVisitorImpl(null, null).skipInventoryLeadTime(delivery, Collections.singletonMap("ABC", warehouse), calendar);

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

        new DeliveryTimeEstimationVisitorImpl(null, null).skipInventoryLeadTime(delivery, Collections.singletonMap("ABC", warehouse), calendar);

        assertEquals("2017-02-09", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipInventoryLeadTimeFfBackOrderLead() throws Exception {

        final Warehouse warehouse = this.context.mock(Warehouse.class, "warehouse");
        final CustomerOrderDelivery delivery = this.context.mock(CustomerOrderDelivery.class, "delivery");
        final CustomerOrderDeliveryDet det = this.context.mock(CustomerOrderDeliveryDet.class, "det");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        context.checking(new Expectations() {{
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultStandardStockLeadTime(); will(returnValue(0));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(2));
        }});

        new DeliveryTimeEstimationVisitorImpl(null, null).skipInventoryLeadTime(delivery, Collections.singletonMap("ABC", warehouse), calendar);

        assertEquals("2017-02-09", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipInventoryLeadTimeFfMixedLead() throws Exception {

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

        new DeliveryTimeEstimationVisitorImpl(null, null).skipInventoryLeadTime(delivery, Collections.singletonMap("ABC", warehouse), calendar);

        assertEquals("2017-02-09", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipWeekdayExclusionsNone() throws Exception {

        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        context.checking(new Expectations() {{
            allowing(sla).getExcludeWeekDaysAsList(); will(returnValue(Collections.emptyList()));
        }});

        new DeliveryTimeEstimationVisitorImpl(null, null).skipWeekdayExclusions(sla, calendar);

        assertEquals("2017-02-07", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }

    @Test
    public void testSkipWeekdayExclusionsSatSun() throws Exception {

        final CarrierSla sla = this.context.mock(CarrierSla.class, "sla");

        final Calendar calendar = Calendar.getInstance();

        context.checking(new Expectations() {{
            allowing(sla).getExcludeWeekDaysAsList(); will(returnValue(Arrays.asList(1,7)));
        }});

        calendar.setTime(df.parse("2017-02-06"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-06", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-07"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-07", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-08"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-08", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-09"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-09", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-10"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-10", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-11"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-13", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-12"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-13", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-13"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipWeekdayExclusions(sla, calendar);
        assertEquals("2017-02-13", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }


    @Test
    public void testGetCarrierSlaExcludedDatesNone() throws Exception {

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("");

        final Map<Date, Date> dates = new DeliveryTimeEstimationVisitorImpl(null, null).getCarrierSlaExcludedDates(sla);

        assertNotNull(dates);
        assertTrue(dates.isEmpty());

    }


    @Test
    public void testGetCarrierSlaExcludedDates() throws Exception {

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-01-08,2017-01-10:2017-01-15,2017-01-17");

        Date date, date2;

        final Map<Date, Date> dates = new DeliveryTimeEstimationVisitorImpl(null, null).getCarrierSlaExcludedDates(sla);

        assertNotNull(dates);
        assertEquals(3, dates.size());
        date = df.parse("2017-01-08");
        assertEquals(date, dates.get(date));
        date = df.parse("2017-01-10");
        date2 = df.parse("2017-01-15");
        assertEquals(date2, dates.get(date));
        date = df.parse("2017-01-17");
        assertEquals(date, dates.get(date));

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

        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, Collections.<Date, Date>emptyMap());

        assertEquals("2017-02-07", df.format(calendar.getTime()));

        this.context.assertIsSatisfied();

    }



    @Test
    public void testSkipDatesExclusionsAllWeekdays() throws Exception {

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");

        final Calendar calendar = Calendar.getInstance();

        final Map<Date, Date> dates = new DeliveryTimeEstimationVisitorImpl(null, null).getCarrierSlaExcludedDates(sla);

        calendar.setTime(df.parse("2017-02-07"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-07", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-08"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-09", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-09"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-09", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-10"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-11"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-12"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-13"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-14"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-15"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-16"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-17"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-18", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-18"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-18", df.format(calendar.getTime()));

    }



    @Test
    public void testSkipDatesExclusionsNoThuSatSun() throws Exception {

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5,7");

        final Calendar calendar = Calendar.getInstance();

        final Map<Date, Date> dates = new DeliveryTimeEstimationVisitorImpl(null, null).getCarrierSlaExcludedDates(sla);

        calendar.setTime(df.parse("2017-02-07"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-07", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-08"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-09"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-09", df.format(calendar.getTime())); // Must skip weekdays first!

        calendar.setTime(df.parse("2017-02-10"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-11"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-12"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-13"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-14"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-15"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-16"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-16", df.format(calendar.getTime()));  // Must skip weekdays first!

        calendar.setTime(df.parse("2017-02-17"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-20", df.format(calendar.getTime()));

        calendar.setTime(df.parse("2017-02-18"));
        new DeliveryTimeEstimationVisitorImpl(null, null).skipDatesExclusions(sla, calendar, dates);
        assertEquals("2017-02-18", df.format(calendar.getTime()));  // Must skip weekdays first!

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


        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService, null) {
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
            // expectations:
            one(delivery).setDeliveryGuaranteed(null);
            one(delivery).setDeliveryEstimatedMin(with(equal(df.parse("2017-02-20"))));
            one(delivery).setDeliveryEstimatedMax(with(equal(df.parse("2017-02-24"))));

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


        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService, null) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(delivery).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getCarrierSla(); will(returnValue(sla));
            allowing(delivery).getRequestedDeliveryDate(); will(returnValue(df.parse("2017-02-20")));
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


        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService, null) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(delivery).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getCarrierSla(); will(returnValue(sla));
            allowing(delivery).getRequestedDeliveryDate(); will(returnValue(df.parse("2017-02-12")));
            allowing(order).getShop(); will(returnValue(shop));
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(warehouseService).getByShopIdMapped(123L, false); will(returnValue(Collections.singletonMap("ABC", warehouse)));
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.STANDARD_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("CED"));
            // expectations: NONE, requested date is valid
            one(delivery).setDeliveryGuaranteed(with(equal(df.parse("2017-02-20"))));
            one(delivery).setDeliveryEstimatedMin(null);
            one(delivery).setDeliveryEstimatedMax(null);

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


        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService, null) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(delivery).getCustomerOrder(); will(returnValue(order));
            allowing(delivery).getCarrierSla(); will(returnValue(sla));
            allowing(delivery).getRequestedDeliveryDate(); will(returnValue(df.parse("2017-02-12")));
            allowing(order).getShop(); will(returnValue(shop));
            allowing(shop).getShopId(); will(returnValue(123L));
            allowing(warehouseService).getByShopIdMapped(123L, false); will(returnValue(Collections.singletonMap("ABC", warehouse)));
            allowing(delivery).getDeliveryGroup(); will(returnValue(CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP));
            allowing(delivery).getDetail(); will(returnValue(Collections.singleton(det)));
            allowing(det).getSupplierCode(); will(returnValue("CED"));
            // expectations: NONE, requested date is valid
            one(delivery).setDeliveryGuaranteed(null);
            one(delivery).setDeliveryEstimatedMin(with(equal(df.parse("2017-02-20"))));
            one(delivery).setDeliveryEstimatedMax(null);

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

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService, null) {
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

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5");
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(5);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService, null) {
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

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5");
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(5);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService, null) {
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

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5");
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(5);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService, null) {
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

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5");
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(5);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(warehouseService, null) {
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
            // expectations:
            one(delivery).setDeliveryGuaranteed(null);
            one(delivery).setDeliveryEstimatedMin(with(equal(df.parse("2017-02-18"))));
            one(delivery).setDeliveryEstimatedMax(null);


        }});

        visitor.visit(delivery);

        this.context.assertIsSatisfied();

    }



    @Test
    public void testDetermineDeliveryAvailableTimeRangeNoSla() throws Exception {

        final CarrierSlaService carrierSlaService = this.context.mock(CarrierSlaService.class, "carrierSlaService");

        final MutableShoppingCart shoppingCart = this.context.mock(MutableShoppingCart.class, "shoppingCart");

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
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

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(null));
            // expectations:
            one(orderInfo).putDetail("deliveryDate0", null);
            one(orderInfo).putDetail("deliveryDateMin0", null);
            one(orderInfo).putDetail("deliveryDateMax0", null);
            one(orderInfo).putDetail("deliveryDateDExcl0", null);
            one(orderInfo).putDetail("deliveryDateWExcl0", null);
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

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue(null));
            // expectations:
            one(orderInfo).putDetail("deliveryDate0ABC", null);
            one(orderInfo).putDetail("deliveryDateMin0ABC", null);
            one(orderInfo).putDetail("deliveryDateMax0ABC", null);
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
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

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(null));
            // expectations:
            one(orderInfo).putDetail("deliveryDate0", null);
            one(orderInfo).putDetail("deliveryDateMin0", null);
            one(orderInfo).putDetail("deliveryDateMax0", null);
            one(orderInfo).putDetail("deliveryDateDExcl0", null);
            one(orderInfo).putDetail("deliveryDateWExcl0", null);
            one(orderInfo).putDetail("deliveryDate0", time("2017-02-18")); // Sat 18th Feb (this is min)
            one(orderInfo).putDetail("deliveryDateMin0", time("2017-02-18")); // Sat 18th Feb (since we skip)
            one(orderInfo).putDetail("deliveryDateMax0", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            one(orderInfo).putDetail("deliveryDateDExcl0", time("2017-02-19")); // Sun 19th Feb Thu+Sun between 18th and 22nd
            one(orderInfo).putDetail("deliveryDateWExcl0", "1,5");
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

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(null));
            // expectations:
            one(orderInfo).putDetail("deliveryDate0", null);
            one(orderInfo).putDetail("deliveryDateMin0", null);
            one(orderInfo).putDetail("deliveryDateMax0", null);
            one(orderInfo).putDetail("deliveryDateDExcl0", null);
            one(orderInfo).putDetail("deliveryDateWExcl0", null);
            one(orderInfo).putDetail("deliveryDate0", time("2017-02-09"));  // Thu 9th Feb  (this is min)
            one(orderInfo).putDetail("deliveryDateMin0", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            one(orderInfo).putDetail("deliveryDateMax0", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            // 10th-15th + 17th + Sun between 8th and 22nd> Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fri 17th Feb  Sun 19th Feb
            one(orderInfo).putDetail("deliveryDateDExcl0", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19"));
            one(orderInfo).putDetail("deliveryDateWExcl0", "1");
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

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(time("2017-02-11"))); // Sat 11th Feb
            // expectations:
            one(orderInfo).putDetail("deliveryDate0", null);
            one(orderInfo).putDetail("deliveryDateMin0", null);
            one(orderInfo).putDetail("deliveryDateMax0", null);
            one(orderInfo).putDetail("deliveryDateDExcl0", null);
            one(orderInfo).putDetail("deliveryDateWExcl0", null);
            one(orderInfo).putDetail("deliveryDate0", time("2017-02-16"));  // Thu 16th Feb  (this first available after 11th)
            one(orderInfo).putDetail("deliveryDateMin0", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            one(orderInfo).putDetail("deliveryDateMax0", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            // 10th-15th + 17th + Sun between 8th and 22nd> Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fir 17th Feb  Sun 19th Feb
            one(orderInfo).putDetail("deliveryDateDExcl0", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19"));
            one(orderInfo).putDetail("deliveryDateWExcl0", "1");
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

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(time("2017-02-22"))); // Wed 22nd Feb
            // expectations:
            one(orderInfo).putDetail("deliveryDate0", null);
            one(orderInfo).putDetail("deliveryDateMin0", null);
            one(orderInfo).putDetail("deliveryDateMax0", null);
            one(orderInfo).putDetail("deliveryDateDExcl0", null);
            one(orderInfo).putDetail("deliveryDateWExcl0", null);
            one(orderInfo).putDetail("deliveryDate0", time("2017-02-21"));  // Thu 21st Feb  (adjusted to be in range)
            one(orderInfo).putDetail("deliveryDateMin0", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            one(orderInfo).putDetail("deliveryDateMax0", time("2017-02-21")); // Tue 21st Feb (7th + 15days max, 22nd exluded)
            // 10th-15th + 17th + Sun between 8th and 22nd> Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fri 17th Feb  Sun 19th Feb  Wed 22nd Feb
            one(orderInfo).putDetail("deliveryDateDExcl0", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19","2017-02-22"));
            one(orderInfo).putDetail("deliveryDateWExcl0", "1");
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

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(time("2017-03-25"))); // Sat 25th Mar
            // expectations:
            one(orderInfo).putDetail("deliveryDate0", null);
            one(orderInfo).putDetail("deliveryDateMin0", null);
            one(orderInfo).putDetail("deliveryDateMax0", null);
            one(orderInfo).putDetail("deliveryDateDExcl0", null);
            one(orderInfo).putDetail("deliveryDateWExcl0", null);
            one(orderInfo).putDetail("deliveryDate0", time("2017-02-21"));  // Thu 21st Feb  (adjusted to be in range)
            one(orderInfo).putDetail("deliveryDateMin0", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            one(orderInfo).putDetail("deliveryDateMax0", time("2017-02-21")); // Tue 21st Feb (7th + 15days max, 22nd exluded)
            // 10th-15th + 17th + Sun between 8th and 22nd> Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fri 17th Feb  Sun 19th Feb  Wed 22nd Feb
            one(orderInfo).putDetail("deliveryDateDExcl0", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19","2017-02-22"));
            one(orderInfo).putDetail("deliveryDateWExcl0", "1");
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

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(orderInfo).getDetailByKey("deliveryDate0"); will(returnValue(time("2017-02-18"))); // Sat 18th Feb
            // expectations:
            one(orderInfo).putDetail("deliveryDate0", null);
            one(orderInfo).putDetail("deliveryDateMin0", null);
            one(orderInfo).putDetail("deliveryDateMax0", null);
            one(orderInfo).putDetail("deliveryDateDExcl0", null);
            one(orderInfo).putDetail("deliveryDateWExcl0", null);
            one(orderInfo).putDetail("deliveryDate0", time("2017-02-18"));  // Thu 18th Feb  (selection preserved)
            one(orderInfo).putDetail("deliveryDateMin0", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            one(orderInfo).putDetail("deliveryDateMax0", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            // 10th-15th + 17th + Sun between 8th and 22nd> Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fri 17th Feb  Sun 19th Feb
            one(orderInfo).putDetail("deliveryDateDExcl0", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19"));
            one(orderInfo).putDetail("deliveryDateWExcl0", "1");
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

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1,5");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(0));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue(null));
            // expectations:
            one(orderInfo).putDetail("deliveryDate0ABC", null);
            one(orderInfo).putDetail("deliveryDateMin0ABC", null);
            one(orderInfo).putDetail("deliveryDateMax0ABC", null);
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDate0ABC", time("2017-02-18")); // Sat 18th Feb (this is min)
            one(orderInfo).putDetail("deliveryDateMin0ABC", time("2017-02-18")); // Sat 18th Feb (since we skip)
            one(orderInfo).putDetail("deliveryDateMax0ABC", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", time("2017-02-19")); // Sun 19th Feb Thu+Sun between 18th and 22nd
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", "1,5");
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

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(0));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue(null));
            // expectations:
            one(orderInfo).putDetail("deliveryDate0ABC", null);
            one(orderInfo).putDetail("deliveryDateMin0ABC", null);
            one(orderInfo).putDetail("deliveryDateMax0ABC", null);
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDate0ABC", time("2017-02-09"));  // Thu 9th Feb  (this is min)
            one(orderInfo).putDetail("deliveryDateMin0ABC", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            one(orderInfo).putDetail("deliveryDateMax0ABC", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            // 10th-15th + 17th + Sun between 8th and 22nd>    Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fir 17th Feb  Sun 19th Feb
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19"));
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", "1");
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

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(0));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue("1486771200000")); // Sat 11th Feb
            // expectations:
            one(orderInfo).putDetail("deliveryDate0ABC", null);
            one(orderInfo).putDetail("deliveryDateMin0ABC", null);
            one(orderInfo).putDetail("deliveryDateMax0ABC", null);
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDate0ABC", time("2017-02-16"));  // Thu 16th Feb  (this first available after 11th)
            one(orderInfo).putDetail("deliveryDateMin0ABC", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            one(orderInfo).putDetail("deliveryDateMax0ABC", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            // 10th-15th + 17th + Sun between 8th and 22nd>    Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fir 17th Feb  Sun 19th Feb
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19"));
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", "1");
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

        final CarrierSlaEntity sla = new CarrierSlaEntity();
        sla.setExcludeDates("2017-02-08,2017-02-10:2017-02-15,2017-02-17");
        sla.setExcludeWeekDays("1");
        sla.setNamedDay(true);
        sla.setGuaranteed(true);
        sla.setMinDays(1);
        sla.setMaxDays(15);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(0));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue(time("2017-02-18"))); // Sat 18th Feb
            // expectations:
            one(orderInfo).putDetail("deliveryDate0ABC", null);
            one(orderInfo).putDetail("deliveryDateMin0ABC", null);
            one(orderInfo).putDetail("deliveryDateMax0ABC", null);
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDate0ABC", time("2017-02-18"));  // Thu 18th Feb  (selection preserved)
            one(orderInfo).putDetail("deliveryDateMin0ABC", time("2017-02-09")); // Thu 9th Feb (1day lead 8th skipped)
            one(orderInfo).putDetail("deliveryDateMax0ABC", time("2017-02-22")); // Wed 22nd Feb (7th + 15days max)
            // 10th-15th + 17th + Sun between 8th and 22nd>    Sun 10th Feb  Sat 11th Feb  Sun 12th Feb  Mon 13th Feb  Tue 14th Feb  Wed 15th Feb  Fir 17th Feb  Sun 19th Feb
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", time("2017-02-10","2017-02-11","2017-02-12","2017-02-13","2017-02-14","2017-02-15","2017-02-17","2017-02-19"));
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", "1");
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

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(shoppingCart).getCartItemList(); will(returnValue(Collections.singletonList(item1)));
            allowing(item1).getDeliveryBucket(); will(returnValue(bucket1));
            allowing(bucket1).getGroup(); will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(10));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue(null));
            // expectations:
            one(orderInfo).putDetail("deliveryDate0ABC", null);
            one(orderInfo).putDetail("deliveryDateMin0ABC", null);
            one(orderInfo).putDetail("deliveryDateMax0ABC", null);
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDate0ABC", time("2017-02-18")); // Sat 18th Feb (this is min)
            one(orderInfo).putDetail("deliveryDateMin0ABC", time("2017-02-18")); // Sat 18th Feb (since we skip)
            one(orderInfo).putDetail("deliveryDateMax0ABC", time("2017-03-04")); // Sat 4th Mar (7th + 10days lead + 15days max)
            // Thu+Sun between 18th and 22nd >                 Sun 19th Feb  Thu 23rd Feb  Sun 26th Feb  Thu 2nd Mar
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", time("2017-02-19","2017-02-23","2017-02-26","2017-03-02"));
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", "1,5");
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

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse("2017-02-07"));

        final DeliveryTimeEstimationVisitorImpl visitor = new DeliveryTimeEstimationVisitorImpl(null, carrierSlaService) {
            @Override
            protected Calendar now() {
                return calendar;
            }
        };

        context.checking(new Expectations() {{
            allowing(carrierSlaService).findById(123L); will(returnValue(sla));
            allowing(shoppingCart).getOrderInfo(); will(returnValue(orderInfo));
            allowing(shoppingCart).getCartItemList(); will(returnValue(Collections.singletonList(item1)));
            allowing(item1).getDeliveryBucket(); will(returnValue(bucket1));
            allowing(bucket1).getGroup(); will(returnValue(CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP));
            allowing(warehouse).getCode(); will(returnValue("ABC"));
            allowing(warehouse).getDefaultBackorderStockLeadTime(); will(returnValue(15));
            allowing(orderInfo).getDetailByKey("deliveryDate0ABC"); will(returnValue(null));
            // expectations:
            one(orderInfo).putDetail("deliveryDate0ABC", null);
            one(orderInfo).putDetail("deliveryDateMin0ABC", null);
            one(orderInfo).putDetail("deliveryDateMax0ABC", null);
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", null);
            one(orderInfo).putDetail("deliveryDate0ABC", time("2017-02-23"));  // Thu 23rd Feb  (this is min)
            one(orderInfo).putDetail("deliveryDateMin0ABC", time("2017-02-23"));  // Thu 23rd Feb (15+1day lead 8th skipped)
            one(orderInfo).putDetail("deliveryDateMax0ABC", time("2017-04-22")); // Sat Apr 22nd (22nd + 60days max)
            // 10th-15th + 17th + Sun between 23rd and 21st>   Sun 26th Feb  Sun 5th Mar   Fri 10th Mar  Sat 11th Mar  Sun 12th Mar  Mon 13th Mar  Tue 14th Mar  Wed 15th Mar  Fri 17th Mar  Sun 19th Mar  Sun 26th Mar  Sun 2nd Apr   Sun 9th Apr   Sun 16th Apr  Sun 23rd Apr
            one(orderInfo).putDetail("deliveryDateDExcl0ABC", time("2017-02-26","2017-03-05","2017-03-10","2017-03-11","2017-03-12","2017-03-13","2017-03-14","2017-03-15","2017-03-17","2017-03-19","2017-03-26","2017-04-02","2017-04-09","2017-04-16","2017-04-23"));
            one(orderInfo).putDetail("deliveryDateWExcl0ABC", "1");
        }});


        visitor.determineDeliveryAvailableTimeRange(shoppingCart, 123L, warehouse);

        this.context.assertIsSatisfied();

    }

    private String time(String date) throws Exception {
        return String.valueOf(df.parse(date).getTime());
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
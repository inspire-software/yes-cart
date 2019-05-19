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

package org.yes.cart.orderexport.mail;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.impl.AbstractTestDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.orderexport.OrderAutoExportProcessor;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.utils.TimeContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 20/04/2018
 * Time: 15:21
 */
public class OrderExporterTest extends AbstractTestDAO {

    private ShopService shopService;
    private CustomerOrderService customerOrderService;
    private CarrierSlaService carrierSlaService;
    private OrderAutoExportProcessor processor;

    private TestEmailNotificationTaskExecutor emailNotificationTaskExecutor;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        shopService = ctx().getBean("shopService", ShopService.class);
        customerOrderService = ctx().getBean("customerOrderService", CustomerOrderService.class);
        carrierSlaService = ctx().getBean("carrierSlaService", CarrierSlaService.class);
        processor = ctx().getBean("orderAutoExportProcessorInternal", OrderAutoExportProcessor.class);
        emailNotificationTaskExecutor = ctx().getBean("emailNotificationTaskExecutor", TestEmailNotificationTaskExecutor.class);
        emailNotificationTaskExecutor.getQueue().clear();
    }

    @Test
    public void testEmailExporter() throws Exception {

        final Shop shop = createShop();

        addConfig(shop, AttributeNamesKeys.Shop.ORDER_EXPORTER_MAIL_SUPPORTED_SUPPLIERS,
                "FC1.EMAILNOTIFY=fc1@fc1.com\n" +
                        "FC2.EMAILNOTIFY=fc2@fc2.com"
        );

        final String testOrder = "EE00001";

        // initial order
        CustomerOrder order = createOrder(shop, testOrder);

        assertNull(order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertTrue(emailNotificationTaskExecutor.getQueue().isEmpty());

        // run first time no eligibility
        order = runProcessorAndGetFreshOrder(testOrder);

        assertNull(order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertTrue("Order must not have AUDIT info", order.getAllValues().isEmpty());
        assertTrue(emailNotificationTaskExecutor.getQueue().isEmpty());

        // prepare eligibility but status is not configured
        applyEligibility(order, "EMAILNOTIFY", "os.no.email");

        assertEquals("EMAILNOTIFY", order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertTrue(emailNotificationTaskExecutor.getQueue().isEmpty());

        order = runProcessorAndGetFreshOrder(testOrder);

        assertNull(order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertEquals("Order must have 2 AUDIT records because we record execution even though actual email is not sent",
                2, order.getAllValues().size());
        assertTrue(emailNotificationTaskExecutor.getQueue().isEmpty());

        // prepare eligibility with configured status
        applyEligibility(order, "EMAILNOTIFY", "os.trigger.email");

        assertEquals("EMAILNOTIFY", order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertTrue(emailNotificationTaskExecutor.getQueue().isEmpty());

        order = runProcessorAndGetFreshOrder(testOrder);

        assertNull(order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertEquals("Order must have 4 AUDIT records",
                4, order.getAllValues().size());
        assertFalse(emailNotificationTaskExecutor.getQueue().isEmpty());
        assertEquals("Eamil for each delivery is expected", 2, emailNotificationTaskExecutor.getQueue().size());

    }



    @Test
    public void testManualStateExporter() throws Exception {

        final Shop shop = createShop();

        addConfig(shop, AttributeNamesKeys.Shop.ORDER_EXPORTER_MANUAL_STATE_PROXY,
                "INITPAID=EMAILNOTIFY|BLOCK\n" +
                        "DELIVERY=EMAILNOTIFY|UNBLOCK"
        );

        final String testOrder = "ME00001";

        // initial order
        CustomerOrder order = createOrder(shop, testOrder);

        assertNull(order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertTrue(emailNotificationTaskExecutor.getQueue().isEmpty());

        // run first time no eligibility
        order = runProcessorAndGetFreshOrder(testOrder);

        assertNull(order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertTrue("Order must not have AUDIT info", order.getAllValues().isEmpty());

        // prepare eligibility with blocking
        applyEligibility(order, "INITPAID", "os.any");

        assertEquals("INITPAID", order.getEligibleForExport());
        assertFalse(order.isBlockExport());

        order = runProcessorAndGetFreshOrder(testOrder);

        assertEquals("EMAILNOTIFY", order.getEligibleForExport());
        assertTrue(order.isBlockExport());
        assertEquals("Order must have 1 AUDIT",
                1, order.getAllValues().size());

        // prepare eligibility non-blocking
        applyEligibility(order, "DELIVERY", "os.any");

        assertEquals("DELIVERY", order.getEligibleForExport());
        assertFalse(order.isBlockExport());

        order = runProcessorAndGetFreshOrder(testOrder);

        assertEquals("EMAILNOTIFY", order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertEquals("Order must have 1 AUDIT",
                2, order.getAllValues().size());

    }


    @Test
    public void testExporterChain() throws Exception {

        final Shop shop = createShop();

        addConfig(shop, AttributeNamesKeys.Shop.ORDER_EXPORTER_MANUAL_STATE_PROXY,
                "INITPAID=EMAILNOTIFY|BLOCK\n" +
                        "DELIVERY=EMAILNOTIFY|UNBLOCK"
        );

        addConfig(shop, AttributeNamesKeys.Shop.ORDER_EXPORTER_MAIL_SUPPORTED_SUPPLIERS,
                "FC1.EMAILNOTIFY=fc1@fc1.com\n" +
                        "FC2.EMAILNOTIFY=fc2@fc2.com"
        );


        final String testOrder = "CH00001";

        // initial order
        CustomerOrder order = createOrder(shop, testOrder);

        assertNull(order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertTrue(emailNotificationTaskExecutor.getQueue().isEmpty());

        // run first time no eligibility
        order = runProcessorAndGetFreshOrder(testOrder);

        assertNull(order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertTrue("Order must not have AUDIT info", order.getAllValues().isEmpty());

        // prepare eligibility with blocking
        applyEligibility(order, "INITPAID", "os.trigger.email");

        assertEquals("INITPAID", order.getEligibleForExport());
        assertFalse(order.isBlockExport());

        order = runProcessorAndGetFreshOrder(testOrder);

        assertEquals("EMAILNOTIFY", order.getEligibleForExport());
        assertTrue(order.isBlockExport());
        assertEquals("Order must have 1 AUDIT",
                1, order.getAllValues().size());

        // Manual approval from Admin
        approveManualExport(order);

        assertEquals("EMAILNOTIFY", order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertEquals("Order must have 1 AUDIT",
                1, order.getAllValues().size());

        // Exporter picks up order in the next cycle
        order = runProcessorAndGetFreshOrder(testOrder);

        assertNull(order.getEligibleForExport());
        assertFalse(order.isBlockExport());
        assertEquals("Order must have 3 AUDIT records, 1 for block + 2x mail",
                3, order.getAllValues().size());
        assertFalse(emailNotificationTaskExecutor.getQueue().isEmpty());
        assertEquals("Eamil for each delivery is expected", 2, emailNotificationTaskExecutor.getQueue().size());


    }


    private Shop createShop() {

        final Shop shop = shopService.getGenericDao().getEntityFactory().getByIface(Shop.class);

        shop.setCode(UUID.randomUUID().toString().substring(0, 4));
        shop.setGuid(shop.getCode());
        shop.setName("Test");
        shop.setFspointer("");

        shopService.create(shop);

        return shop;
    }

    private Shop addConfig(final Shop shop , final String key, final String value) {

        final AttrValueShop av = shopService.getGenericDao().getEntityFactory().getByIface(AttrValueShop.class);

        av.setShop(shop);
        av.setAttributeCode(key);
        av.setVal(value);

        shop.getAttributes().add(av);

        shopService.update(shop);

        return shop;

    }

    private void approveManualExport(final CustomerOrder customerOrder) {

        customerOrder.setBlockExport(false);
        for (final CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
            delivery.setBlockExport(false);
        }

        customerOrderService.update(customerOrder);


    }

    private void applyEligibility(final CustomerOrder customerOrder, final String eligibility, final String status) {

        customerOrder.setEligibleForExport(eligibility);
        customerOrder.setOrderStatus(status);
        customerOrder.setBlockExport(false);
        for (final CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
            delivery.setEligibleForExport(eligibility);
            delivery.setBlockExport(false);
        }

        customerOrderService.update(customerOrder);

    }

    private CustomerOrder createOrder(final Shop shop, final String number) {

        final CustomerOrder order = customerOrderService.getGenericDao().getEntityFactory().getByIface(CustomerOrder.class);

        order.setShop(shop);
        order.setOrdernum(number);
        order.setOrderTimestamp(TimeContext.getLocalDateTime());
        order.setCartGuid(number);
        order.setGuid(number);
        order.setEmail("test@test.com");
        order.setFirstname("test");
        order.setLastname("test");
        order.setCurrency("EUR");
        order.setPrice(new BigDecimal("99.99"));
        order.setListPrice(new BigDecimal("99.99"));
        order.setNetPrice(new BigDecimal("99.99"));
        order.setGrossPrice(new BigDecimal("99.99"));
        order.setLocale("en");
        order.setOrderStatus("os.status");

        addDelivery(order, "-1", "FC1");
        addDelivery(order, "-2", "FC2");

        customerOrderService.create(order);

        return order;

    }

    private CustomerOrder addDelivery(final CustomerOrder order, final String suffix, final String supplier) {

        final CustomerOrderDelivery delivery = customerOrderService.getGenericDao().getEntityFactory().getByIface(CustomerOrderDelivery.class);
        delivery.setCustomerOrder(order);
        delivery.setDeliveryNum(order.getOrdernum() + suffix);
        delivery.setGuid(delivery.getDeliveryNum());
        delivery.setPrice(new BigDecimal("4.99"));
        delivery.setListPrice(new BigDecimal("4.99"));
        delivery.setNetPrice(new BigDecimal("4.99"));
        delivery.setGrossPrice(new BigDecimal("4.99"));
        delivery.setTaxRate(new BigDecimal("0.00"));
        delivery.setTaxCode("VAT");
        delivery.setDeliveryStatus("ds.status");
        delivery.setDeliveryGroup("D1");
        delivery.setCarrierSla(carrierSlaService.findAll().get(0));

        final CustomerOrderDeliveryDet deliveryDet = customerOrderService.getGenericDao().getEntityFactory().getByIface(CustomerOrderDeliveryDet.class);
        deliveryDet.setDelivery(delivery);
        deliveryDet.setQty(new BigDecimal("1"));
        deliveryDet.setPrice(new BigDecimal("19.99"));
        deliveryDet.setSalePrice(new BigDecimal("19.99"));
        deliveryDet.setListPrice(new BigDecimal("19.99"));
        deliveryDet.setNetPrice(new BigDecimal("19.99"));
        deliveryDet.setGrossPrice(new BigDecimal("19.99"));
        deliveryDet.setTaxRate(new BigDecimal("0.00"));
        deliveryDet.setTaxCode("VAT");
        deliveryDet.setProductSkuCode("SKU");
        deliveryDet.setProductName("Name");
        deliveryDet.setSupplierCode(supplier);

        delivery.getDetail().add(deliveryDet);

        order.getDelivery().add(delivery);

        return order;

    }

    private CustomerOrder runProcessorAndGetFreshOrder(final String ordernum) {
        processor.run();
        try {
            /*
                Sleep function to simulate delay, so that we do not get audit info collisions with
                timestamp unique keys. Processor will never run on the same order more than once
                within the same second in real world scenario.
             */
            Thread.sleep(1000L);
        } catch (InterruptedException exp) {
            // nothing
        }
        return customerOrderService.findByReference(ordernum);
    }

}
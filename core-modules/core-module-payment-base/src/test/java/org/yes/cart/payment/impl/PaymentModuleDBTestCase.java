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

package org.yes.cart.payment.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.*;
import org.yes.cart.payment.service.impl.BasePaymentModuleDBTestCase;
import org.yes.cart.shoppingcart.Total;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class PaymentModuleDBTestCase extends BasePaymentModuleDBTestCase {

    protected String testContextName() {
        return "classpath:test-core-module-payment-base.xml";
    }

    protected ApplicationContext createContext() {
        return new ClassPathXmlApplicationContext(testContextName());
    }

    protected CustomerOrderDeliveryDet createDeliveryItem(
            String productSkuName, String skuCode, BigDecimal price, BigDecimal qty) {
        CustomerOrderDeliveryDet deliveryDet = new CustomerOrderDeliveryDetEntity();
        deliveryDet.setPrice(price);
        deliveryDet.setGrossPrice(price);
        deliveryDet.setNetPrice(price);
        deliveryDet.setListPrice(price);
        deliveryDet.setQty(qty);
        deliveryDet.setProductSkuCode(skuCode);
        deliveryDet.setProductName(productSkuName);
        return deliveryDet;
    }

    protected CarrierSla createCarrierSla() {
        CarrierSla carrierSla = new CarrierSlaEntity();
        carrierSla.setGuid("NDD_4321");
        carrierSla.setCarrierslaId(4321);
        carrierSla.setName("Next dey delivery");
        return carrierSla;
    }

    protected CustomerOrderDelivery createDelivery0(String orderNum) {
        CustomerOrderDelivery delivery = new CustomerOrderDeliveryEntity();
        delivery.setCarrierSla(createCarrierSla());
        delivery.setPrice(new BigDecimal("13.99"));
        delivery.setNetPrice(new BigDecimal("13.99"));
        delivery.setGrossPrice(new BigDecimal("13.99"));
        delivery.setListPrice(new BigDecimal("13.99"));
        delivery.setPromoApplied(false);
        delivery.setDeliveryNum(orderNum + "-0");
        delivery.getDetail().add(
                createDeliveryItem("product sku 1", "skuCode1", new BigDecimal("12.12"), new BigDecimal("3"))
        );
        delivery.getDetail().add(
                createDeliveryItem("product sku 2", "skuCode1", new BigDecimal("15.01"), new BigDecimal("1"))
        );
        return delivery;
    }

    protected CustomerOrderDelivery createDelivery1(String orderNum) {
        CustomerOrderDelivery delivery = new CustomerOrderDeliveryEntity();
        delivery.setCarrierSla(createCarrierSla());
        delivery.setPrice(new BigDecimal("13.99"));
        delivery.setNetPrice(new BigDecimal("13.99"));
        delivery.setGrossPrice(new BigDecimal("13.99"));
        delivery.setListPrice(new BigDecimal("13.99"));
        delivery.setDeliveryNum(orderNum + "-1");
        delivery.getDetail().add(
                createDeliveryItem("product sku 1", "skuCode2", new BigDecimal("25.00"), new BigDecimal("2"))
        );
        return delivery;
    }

    protected Map createCardParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ccHolderName", "JOHN DOU");
        params.put("ccNumber", getVisaCardNumber());
        params.put("ccExpireMonth", "12");  // paypal test account
        params.put("ccExpireYear", "2020"); // paypal test account
        params.put("ccSecCode", "111");
        params.put("ccType", "Visa");
        params.put("ccHolderName", "JOHN DOU");
        return params;
    }

    protected CustomerOrder createCustomerOrder(String orderNum) {
        CustomerOrder customerOrder = new CustomerOrderEntity();
        customerOrder.setOrderTimestamp(new Date());
        customerOrder.setCurrency("USD");
        customerOrder.setOrdernum(orderNum);
        customerOrder.setCartGuid(UUID.randomUUID().toString());
        customerOrder.setCustomer(createCustomer());
        customerOrder.setShop(createShop());
        customerOrder.setBillingAddress("2684 Lacy Street Suite 208, Los Angeles, 90031,  CA");
        customerOrder.setShippingAddress("713 Happy Street Suite 101, Los Angeles, 90555,  CA");
        customerOrder.getDelivery().add(createDelivery0(orderNum));
        customerOrder.getDelivery().add(createDelivery1(orderNum));

        BigDecimal gross = Total.ZERO;
        BigDecimal net = Total.ZERO;
        for (final CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
            for (final CustomerOrderDeliveryDet detail : delivery.getDetail()) {
                gross = gross.add(detail.getGrossPrice().multiply(detail.getQty()));
                net = net.add(detail.getNetPrice().multiply(detail.getQty()));
            }
        }
        customerOrder.setGrossPrice(gross);
        customerOrder.setNetPrice(net);

        customerOrder.setOrderStatus(CustomerOrder.ORDER_STATUS_IN_PROGRESS);

        customerOrder.setBillingAddressDetails(customerOrder.getCustomer().getDefaultAddress(Address.ADDR_TYPE_BILLING));
        customerOrder.setShippingAddressDetails(customerOrder.getCustomer().getDefaultAddress(Address.ADDR_TYPE_SHIPPING));
        customerOrder.setEmail(customerOrder.getCustomer().getEmail());

        return customerOrder;
    }

    protected Shop createShop() {
        Shop shop = new ShopEntity();
        shop.setShopId(777L);
        shop.setCode("SHOP777");
        return shop;
    }

    protected Customer createCustomer() {
        Customer customer = new CustomerEntity();
        customer.setEmail("john.dou@domain.com");
        customer.setFirstname("John");
        customer.setLastname("Dou");
        customer.setPassword("rawpassword");
        Address address = createAddress1();
        customer.getAddress().add(address);
        address = createAddress2();
        customer.getAddress().add(address);
        return customer;
    }

    private Address createAddress2() {
        Address address;
        address = new AddressEntity();
        address.setFirstname("Jane");
        address.setLastname("Dou");
        address.setCity("Los Angeles");
        address.setAddrline1("713 Happy Street Suite 101");
        address.setCountryCode("US");
        address.setStateCode("CA");
        address.setPostcode("90555");
        address.setAddressType(Address.ADDR_TYPE_SHIPPING);
        address.setDefaultAddress(true);
        return address;
    }

    private Address createAddress1() {
        Address address = new AddressEntity();
        address.setFirstname("John");
        address.setLastname("Dou");
        address.setCity("Los Angeles");
        address.setAddrline1("2684 Lacy Street Suite 208");
        address.setCountryCode("US");
        address.setStateCode("CA");
        address.setPostcode("90031");
        address.setAddressType(Address.ADDR_TYPE_BILLING);
        address.setDefaultAddress(true);
        return address;
    }

    /**
     * Get visa card number for testing. Usually gateways has different card for testing
     *
     * @return card number
     */
    public abstract String getVisaCardNumber();
}
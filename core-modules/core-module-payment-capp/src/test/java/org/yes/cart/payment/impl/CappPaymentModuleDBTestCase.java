package org.yes.cart.payment.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.*;
import org.yes.cart.payment.service.impl.BasePaymentModuleDBTestCase;

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
public abstract class CappPaymentModuleDBTestCase extends BasePaymentModuleDBTestCase {

    protected ApplicationContext createContext() {
        return new ClassPathXmlApplicationContext("test-core-module-payment-capp.xml");
    }

    protected CustomerOrderDeliveryDet createDeliveryItem(
            String productSkuName, String skuCode, BigDecimal price, BigDecimal qty) {
        CustomerOrderDeliveryDet deliveryDet = new CustomerOrderDeliveryDetEntity();
        ProductSku sku = new ProductSkuEntity();
        sku.setCode(skuCode);
        sku.setName(productSkuName);
        deliveryDet.setPrice(price);
        deliveryDet.setQty(qty);
        deliveryDet.setSku(sku);
        return deliveryDet;
    }

    protected CarrierSla createCarrierSla() {
        CarrierSla carrierSla = new CarrierSlaEntity();
        carrierSla.setCarrierslaId(4321);
        carrierSla.setName("Next dey delivery");
        carrierSla.setPrice(new BigDecimal("13.99"));
        carrierSla.setCurrency("USD");
        return carrierSla;
    }

    protected CustomerOrderDelivery createDelivery0(String orderNum) {
        CustomerOrderDelivery delivery = new CustomerOrderDeliveryEntity();
        delivery.setCarrierSla(createCarrierSla());
        delivery.setPrice(delivery.getCarrierSla().getPrice());
        delivery.setDevileryNum(orderNum + "-0");
        delivery.getDetail().add(
                createDeliveryItem("product sku 1. Three items in delivery 0", "skuCode1", new BigDecimal("12.12"), new BigDecimal("3"))
        );
        delivery.getDetail().add(
                createDeliveryItem("product sku 2. One item in delivery 0", "skuCode1", new BigDecimal("15.01"), new BigDecimal("1"))
        );
        return delivery;
    }

    protected CustomerOrderDelivery createDelivery1(String orderNum) {
        CustomerOrderDelivery delivery = new CustomerOrderDeliveryEntity();
        delivery.setCarrierSla(createCarrierSla());
        delivery.setPrice(delivery.getCarrierSla().getPrice());
        delivery.setDevileryNum(orderNum + "-1");
        delivery.getDetail().add(
                createDeliveryItem("product sku 1. Two items in delivery 1", "skuCode2", new BigDecimal("25.00"), new BigDecimal("2"))
        );
        return delivery;
    }

    protected Map createCardParameters() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ccHolderName", "JOHN DOU");
        params.put("ccNumber", getVisaCardNumber());
        params.put("ccExpireMonth", "02");  // paypal test account
        params.put("ccExpireYear", "2016"); // paypal test account
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
        customerOrder.setBillingAddress("2684 Lacy Street Suite 208, Los Angeles, 90031,  CA");
        customerOrder.setShippingAddress("713 Happy Street Suite 101, Los Angeles, 90555,  CA");
        customerOrder.getDelivery().add(createDelivery0(orderNum));
        customerOrder.getDelivery().add(createDelivery1(orderNum));
        customerOrder.setOrderStatus(CustomerOrder.ORDER_STATUS_IN_PROGRESS);
        return customerOrder;
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
        address.setAddressType(Address.ADDR_TYPE_SHIPING);
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
     * Get visa card number for testing. Usually gateways has differend card for testing
     *
     * @return card number
     */
    public abstract String getVisaCardNumber();
}
package org.yes.cart.service.order.impl;

import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class OrderAssemblerImplTest extends BaseCoreDBTestCase {

    private OrderAssembler orderAssembler;
    private GenericDAO<CustomerOrder, Long> customerOrderDao;

    @Before
    public void setUp() throws Exception {
        orderAssembler = (OrderAssembler) ctx.getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        customerOrderDao = (GenericDAO<CustomerOrder, Long>) ctx.getBean("customerOrderDao");
    }

    @Test
    public void testAssembleCustomerOrder() {

        Customer customer = createCustomer(ctx, "testAssembleCustomerOrder2");

        ShoppingCart shoppingCart = getShoppingCart(ctx, customer.getEmail());

        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart);

        assertNotNull(customerOrder);

        customerOrder = customerOrderDao.create(customerOrder);

        assertNotNull(customerOrder);

        assertNotNull(customerOrder.getBillingAddress());

        assertEquals("By default billing and shipping addresses the same",
                customerOrder.getBillingAddress(),
                customerOrder.getShippingAddress());

        assertTrue("By Default billind address is shipping address ",
                customerOrder.getBillingAddress().contains("shipping addr"));

        assertEquals(
                "Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_NONE,
                customerOrder.getOrderStatus());

        assertNotNull("Order num must be set", customerOrder.getOrdernum());


        assertTrue("Order in pending state must not have delivery", customerOrder.getDelivery().isEmpty());

        assertEquals("Shopping cart guid and order guid are equals",
                shoppingCart.getGuid(),
                customerOrder.getGuid());

        assertEquals(8, customerOrder.getOrderDetail().size());

        assertFalse(customerOrder.isMultipleShipmentOption());


    }


    public static ShoppingCart getShoppingCart2(final ApplicationContext context, final String customerEmail) {

        ShoppingCart shoppingCart = getShoppingCart(context, customerEmail);

        Map<String, String> param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST10-SKU");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "17.00");

        new SetSkuQuantityToCartEventCommandImpl(context,
                param)
                .execute(shoppingCart);


        return shoppingCart;

    }

    public static ShoppingCart getShoppingCart(final ApplicationContext context, final String customerEmail) {

        ShoppingCart shoppingCart = new ShoppingCartImpl();

        Map<String, String> params;

        params = new HashMap<String, String>();
        params.put(LoginCommandImpl.EMAIL, customerEmail);
        params.put(LoginCommandImpl.NAME, "John Doe");


        new SetShopCartCommandImpl(context, Collections.singletonMap(SetShopCartCommandImpl.CMD_KEY, 10))
                .execute(shoppingCart);

        new ChangeCurrencyEventCommandImpl(context, Collections.singletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, "USD"))
                .execute(shoppingCart);

        new LoginCommandImpl(null, params)
                .execute(shoppingCart);

        new AddSkuToCartEventCommandImpl(context, Collections.singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST1"))
                .execute(shoppingCart);

        new AddSkuToCartEventCommandImpl(context, Collections.singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST3"))
                .execute(shoppingCart);

        new AddSkuToCartEventCommandImpl(context, Collections.singletonMap(AddSkuToCartEventCommandImpl.CMD_KEY, "CC_TEST3"))
                .execute(shoppingCart);

        Map<String, String> param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST4");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");

        new SetSkuQuantityToCartEventCommandImpl(context,
                param)
                .execute(shoppingCart);


        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST5");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "200.00");

        new SetSkuQuantityToCartEventCommandImpl(context,
                param)
                .execute(shoppingCart);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST6");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "3.00");

        new SetSkuQuantityToCartEventCommandImpl(context,
                param)
                .execute(shoppingCart);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST7");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");

        new SetSkuQuantityToCartEventCommandImpl(context,
                param)
                .execute(shoppingCart);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST8");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");

        new SetSkuQuantityToCartEventCommandImpl(context,
                param)
                .execute(shoppingCart);

        param = new HashMap<String, String>();
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_KEY, "CC_TEST9");
        param.put(SetSkuQuantityToCartEventCommandImpl.CMD_PARAM_QTY, "1.00");

        new SetSkuQuantityToCartEventCommandImpl(context,
                param)
                .execute(shoppingCart);

        new SetCarrierSlaCartCommandImpl(null, Collections.singletonMap(SetCarrierSlaCartCommandImpl.CMD_KEY, "1"))
                .execute(shoppingCart);


        return shoppingCart;
    }

    public static Customer createCustomer(final ApplicationContext context, final String prefix) {


        CustomerService customerService;
        ShopService shopService;
        AttributeService attributeService;
        AddressService addressService;


        shopService = (ShopService) context.getBean(ServiceSpringKeys.SHOP_SERVICE);
        customerService = (CustomerService) context.getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        attributeService = (AttributeService) context.getBean(ServiceSpringKeys.ATTRIBUTE_SERVICE);
        addressService = (AddressService) context.getBean(ServiceSpringKeys.ADDRESS_SERVICE);

        GenericDAO<Customer, Long> customerDao = (GenericDAO<Customer, Long>) context.getBean("customerDao");

        Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setEmail(prefix + "jd@domain.com");
        customer.setFirstname(prefix + "John");
        customer.setLastname(prefix + "Doe");
        customer.setPassword("rawpassword");


        final AttrValueCustomer attrValueCustomer = customerService.getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
        attrValueCustomer.setCustomer(customer);
        attrValueCustomer.setVal("555-55-51");
        attrValueCustomer.setAttribute(attributeService.findByAttributeCode(AttributeNamesKeys.CUSTOMER_PHONE));
        customer.getAttribute().add(attrValueCustomer);
        customer = customerService.create(customer, shopService.getById(10L));


        Address address = addressService.getGenericDao().getEntityFactory().getByIface(Address.class);
        address.setFirstname("John");
        address.setLastname("Doe");
        address.setCity("Vancouver");
        address.setAddrline1("line1");
        address.setAddrline2("shipping addr");
        address.setCountryCode("CA");
        address.setAddressType(Address.ADDR_TYPE_SHIPING);
        address.setCustomer(customer);
        address.setPhoneList("555-55-51");
        addressService.create(address);

        address = addressService.getGenericDao().getEntityFactory().getByIface(Address.class);
        address.setFirstname("John");
        address.setLastname("Doe");
        address.setCity("Vancouver");
        address.setAddrline1("line1");
        address.setAddrline2("billing addr");
        address.setCountryCode("CA");
        address.setAddressType(Address.ADDR_TYPE_BILLING);
        address.setCustomer(customer);
        address.setPhoneList("555-55-52");
        addressService.create(address);

//        customer = customerService.findCustomer("jd@domain.com");

        customer = customerDao.findSingleByCriteria(
                Restrictions.eq("email", prefix + "jd@domain.com"));


        return customer;

    }

    public static Customer createCustomer(final ApplicationContext context) {

        return createCustomer(context, UUID.randomUUID().toString());


    }


}

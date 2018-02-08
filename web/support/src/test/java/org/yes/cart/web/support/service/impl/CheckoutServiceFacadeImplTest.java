package org.yes.cart.web.support.service.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.ProductPriceModel;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingContext;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.shoppingcart.impl.ShoppingContextImpl;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 06/02/2018
 * Time: 15:28
 */
public class CheckoutServiceFacadeImplTest {

    private final Mockery mockery = new JUnit4Mockery();

    private ShoppingContext noTaxInfo() {
        final ShoppingContextImpl ctx = new ShoppingContextImpl();
        ctx.setTaxInfoEnabled(false);
        ctx.setTaxInfoUseNet(false);
        ctx.setTaxInfoShowAmount(false);
        return ctx;
    }

    private ShoppingContext grossPercentTaxInfo() {
        final ShoppingContextImpl ctx = new ShoppingContextImpl();
        ctx.setTaxInfoEnabled(true);
        ctx.setTaxInfoUseNet(false);
        ctx.setTaxInfoShowAmount(false);
        return ctx;
    }

    private ShoppingContext netPercentTaxInfo() {
        final ShoppingContextImpl ctx = new ShoppingContextImpl();
        ctx.setTaxInfoEnabled(true);
        ctx.setTaxInfoUseNet(true);
        ctx.setTaxInfoShowAmount(false);
        return ctx;
    }

    @Test
    public void testGetOrderTotalSubTaxZero() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(Total.ZERO));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
        }});

        final ProductPriceModel model = facade.getOrderTotalSub(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_SUB_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderTotalSubNoTaxInfoNet() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(noTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalSub(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_SUB_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderTotalSubNoTaxInfoGross() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("82.32")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(noTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalSub(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_SUB_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("82.32", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }



    @Test
    public void testGetOrderTotalSubTaxInfoNetExcl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(netPercentTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalSub(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_SUB_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("83.32", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderTotalSubTaxInfoNetIncl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(grossPercentTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalSub(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_SUB_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderTotalSubNoTaxInfoGrossExcl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("83.32")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(netPercentTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalSub(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_SUB_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("83.32", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderTotalSubNoTaxInfoGrossIncl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("82.32")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(grossPercentTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalSub(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_SUB_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }




    @Test
    public void testGetOrderTotalAmountTaxZero() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(Total.ZERO));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderTotalAmountTaxZeroSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(Total.ZERO));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("129.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("129.99", model.getRegularPrice().toPlainString());
        assertEquals("99.99", model.getSalePrice().toPlainString());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderTotalAmountNoTaxInfoNet() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(noTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderTotalAmountNoTaxInfoNetSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("129.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(noTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("129.99", model.getRegularPrice().toPlainString());
        assertEquals("99.99", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }
    @Test
    public void testGetOrderTotalAmountNoTaxInfoGross() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("82.32")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(noTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderTotalAmountNoTaxInfoGrossSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("82.32")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("129.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(noTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("129.99", model.getRegularPrice().toPlainString());
        assertEquals("99.99", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderTotalAmountTaxInfoNetExcl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(netPercentTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderTotalAmountTaxInfoNetExclSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("129.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(netPercentTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("129.99", model.getRegularPrice().toPlainString());
        assertEquals("99.99", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderTotalAmountTaxInfoNetIncl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(grossPercentTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderTotalAmountTaxInfoNetInclSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("129.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(grossPercentTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("129.99", model.getRegularPrice().toPlainString());
        assertEquals("99.99", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderTotalAmountNoTaxInfoGrossExcl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("82.32")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(netPercentTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderTotalAmountNoTaxInfoGrossExclSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("83.32")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("129.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(netPercentTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("129.99", model.getRegularPrice().toPlainString());
        assertEquals("99.99", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderTotalAmountNoTaxInfoGrossIncl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("82.32")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(grossPercentTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderTotalAmountNoTaxInfoGrossInclSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderTotal(final CustomerOrder customerOrder) {
                assertSame(customerOrder, order);
                return total;
            }

            @Override
            String determineTaxCode(final CustomerOrder customerOrder) {
                return "TAX";
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("82.32")));
            allowing(total).getTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getListTotalAmount(); will(returnValue(new BigDecimal("129.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(grossPercentTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderTotalAmount(order, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.ORDER_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("129.99", model.getRegularPrice().toPlainString());
        assertEquals("99.99", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }



    @Test
    public void testGetOrderDeliveryTotalSubTaxZero() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getSubTotalTax(); will(returnValue(Total.ZERO));
            allowing(total).getSubTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalSub(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderDeliveryTotalSubNoTaxInfoNet() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getSubTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getSubTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getSubTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(noTaxInfo()));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalSub(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderDeliveryTotalSubNoTaxInfoGross() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getSubTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getSubTotal(); will(returnValue(new BigDecimal("82.32")));
            allowing(total).getSubTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(noTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalSub(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("82.32", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }



    @Test
    public void testGetOrderDeliveryTotalSubTaxInfoNetExcl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getSubTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getSubTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getSubTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(netPercentTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalSub(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("83.32", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderDeliveryTotalSubTaxInfoNetIncl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getSubTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getSubTotal(); will(returnValue(new BigDecimal("99.99")));
            allowing(total).getSubTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(grossPercentTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalSub(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderDeliveryTotalSubNoTaxInfoGrossExcl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getSubTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getSubTotal(); will(returnValue(new BigDecimal("83.32")));
            allowing(total).getSubTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(netPercentTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalSub(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("83.32", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderDeliveryTotalSubNoTaxInfoGrossIncl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getSubTotalTax(); will(returnValue(new BigDecimal("16.67")));
            allowing(total).getSubTotal(); will(returnValue(new BigDecimal("83.21")));
            allowing(total).getSubTotalAmount(); will(returnValue(new BigDecimal("99.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(grossPercentTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalSub(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_TOTAL_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("99.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("16.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }




    @Test
    public void testGetOrderDeliveryTotalShippingTaxZero() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };

        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(Total.ZERO));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("9.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("9.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderDeliveryTotalShippingTaxZeroSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(Total.ZERO));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("19.99")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("9.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("19.99", model.getRegularPrice().toPlainString());
        assertEquals("9.99", model.getSalePrice().toPlainString());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderDeliveryTotalShippingNoTaxInfoNet() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(new BigDecimal("1.67")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("9.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(noTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("9.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderDeliveryTotalShippingNoTaxInfoNetSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(new BigDecimal("1.67")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("19.99")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(noTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("19.99", model.getRegularPrice().toPlainString());
        assertEquals("9.99", model.getSalePrice().toPlainString());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }
    @Test
    public void testGetOrderDeliveryTotalShippingNoTaxInfoGross() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(new BigDecimal("1.67")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("8.32")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("8.32")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(noTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("8.32", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderDeliveryTotalShippingNoTaxInfoGrossSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(new BigDecimal("1.67")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("16.65")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("8.32")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(noTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("16.65", model.getRegularPrice().toPlainString());
        assertEquals("8.32", model.getSalePrice().toPlainString());

        assertFalse(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertNull(model.getPriceTaxCode());
        assertNull(model.getPriceTaxRate());
        assertFalse(model.isPriceTaxExclusive());
        assertNull(model.getPriceTax());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderDeliveryTotalShippingTaxInfoNetExcl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(new BigDecimal("1.67")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("9.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(netPercentTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("8.32", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("1.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderDeliveryTotalShippingTaxInfoNetExclSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(new BigDecimal("1.67")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("19.99")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("9.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(netPercentTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("16.65", model.getRegularPrice().toPlainString());
        assertEquals("8.32", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("1.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderDeliveryTotalShippingTaxInfoNetIncl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(new BigDecimal("1.67")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("9.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(grossPercentTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("9.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("1.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderDeliveryTotalShippingTaxInfoNetInclSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(new BigDecimal("1.67")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("19.99")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("9.99")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(grossPercentTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("19.99", model.getRegularPrice().toPlainString());
        assertEquals("9.99", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertFalse(model.isPriceTaxExclusive());
        assertEquals("1.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderDeliveryTotalShippingNoTaxInfoGrossExcl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(new BigDecimal("1.67")));
            allowing(total).getTotal(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("8.32")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("8.32")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(netPercentTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("8.32", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("1.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderDeliveryTotalShippingNoTaxInfoGrossExclSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(new BigDecimal("1.67")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("16.65")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("8.32")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(netPercentTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("16.65", model.getRegularPrice().toPlainString());
        assertEquals("8.32", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertTrue(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("1.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetOrderDeliveryTotalShippingNoTaxInfoGrossIncl() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(new BigDecimal("1.67")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("8.32")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("8.32")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(grossPercentTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("9.99", model.getRegularPrice().toPlainString());
        assertNull(model.getSalePrice());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("1.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }


    @Test
    public void testGetOrderDeliveryTotalShippingNoTaxInfoGrossInclSale() throws Exception {

        final Total total = this.mockery.mock(Total.class, "total");
        final CustomerOrder order = this.mockery.mock(CustomerOrder.class, "order");
        final CustomerOrderDelivery delivery = this.mockery.mock(CustomerOrderDelivery.class, "delivery");
        final ShoppingCart cart = this.mockery.mock(ShoppingCart.class, "cart");

        final CheckoutServiceFacadeImpl facade = new CheckoutServiceFacadeImpl(null, null, null, null, null, null, null, null) {
            @Override
            public Total getOrderDeliveryTotal(final CustomerOrder customerOrder, final CustomerOrderDelivery orderDelivery) {
                assertSame(customerOrder, order);
                assertSame(orderDelivery, delivery);
                return total;
            }
        };


        this.mockery.checking(new Expectations() {{
            allowing(total).getDeliveryTax(); will(returnValue(new BigDecimal("1.67")));
            allowing(total).getDeliveryCostAmount(); will(returnValue(new BigDecimal("9.99")));
            allowing(total).getDeliveryListCost(); will(returnValue(new BigDecimal("16.65")));
            allowing(total).getDeliveryCost(); will(returnValue(new BigDecimal("8.32")));
            allowing(order).getCurrency(); will(returnValue("EUR"));
            allowing(cart).getShoppingContext(); will(returnValue(grossPercentTaxInfo()));
            allowing(delivery).getTaxCode(); will(returnValue("TAX"));
        }});

        final ProductPriceModel model = facade.getOrderDeliveryTotalShipping(order, delivery, cart);

        assertNotNull(model);

        assertEquals(CheckoutServiceFacadeImpl.DELIVERY_SHIPPING_REF, model.getRef());

        assertEquals("EUR", model.getCurrency());
        assertEquals("1", model.getQuantity().toPlainString());

        assertEquals("19.98", model.getRegularPrice().toPlainString());
        assertEquals("9.99", model.getSalePrice().toPlainString());

        assertTrue(model.isTaxInfoEnabled());
        assertFalse(model.isTaxInfoUseNet());
        assertFalse(model.isTaxInfoShowAmount());

        assertEquals("TAX", model.getPriceTaxCode());
        assertEquals("20.00", model.getPriceTaxRate().toPlainString());
        assertTrue(model.isPriceTaxExclusive());
        assertEquals("1.67", model.getPriceTax().toPlainString());

        mockery.assertIsSatisfied();

    }




}
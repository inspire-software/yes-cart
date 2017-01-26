package org.yes.cart.service.domain.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.promotion.PromotionContext;
import org.yes.cart.promotion.PromotionContextFactory;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.ShoppingCartCalculator;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.DefaultAmountCalculationStrategy;
import org.yes.cart.shoppingcart.impl.TotalImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 01/10/2015
 * Time: 09:28
 */
public class ShoppingCartCalculatorImplTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testCalculatePriceRegisteredNoTaxConfigured() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCostCalculationStrategy");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotionContextFactory");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart currentCart = context.mock(ShoppingCart.class, "currentCart");
        final ShoppingContext currentCartCtx = context.mock(ShoppingContext.class, "currentCartCtx");

        final Shop shop = context.mock(Shop.class, "shop");

        final Customer customer = context.mock(Customer.class, "customer");
        final PromotionContext promotionContext = context.mock(PromotionContext.class, "promotionContext");

        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");

        // ZERO total for cart.getCarrierSlaId() == null
        final Total deliveryCost = new TotalImpl();
        // Ignore this as it is not used in calculations
        final Total itemsTotal = new TotalImpl();

        context.checking(new Expectations() {{

            allowing(currentCart).getCurrentLocale(); will(returnValue("en"));
            allowing(currentCart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(currentCart).getShoppingContext(); will(returnValue(currentCartCtx));
            allowing(currentCartCtx).getCustomerName(); will(returnValue("Bob Doe"));
            allowing(currentCartCtx).getShopId(); will(returnValue(123L));
            allowing(currentCartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(currentCartCtx).getCustomerShopId(); will(returnValue(123L));
            allowing(currentCartCtx).getCustomerShopCode(); will(returnValue("SHOP10"));
            allowing(currentCartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(currentCartCtx).getStateCode(); will(returnValue("GB-GB"));
            allowing(currentCartCtx).getCustomerEmail(); will(returnValue("bob.doe@somewhere.com"));
            allowing(currentCartCtx).getCustomerShops(); will(returnValue(Arrays.asList("SHOP10")));
            allowing(currentCartCtx).getLatestViewedSkus(); will(returnValue(null));
            allowing(currentCartCtx).getLatestViewedCategories(); will(returnValue(null));
            allowing(currentCartCtx).getResolvedIp(); will(returnValue("127.0.0.1"));

            allowing(shopService).getById(123L); will(returnValue(shop));
            allowing(customerService).getCustomerByEmail("bob.doe@somewhere.com", shop); will(returnValue(customer));

            allowing(promotionContextFactory).getInstance("SHOP10", "EUR"); will(returnValue(promotionContext));

            allowing(promotionContext).applyItemPromo(with(customer), with(any(MutableShoppingCart.class)));

            allowing(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-GB", "SKU0001"); will(returnValue(tax));
            // NULL tax config
            allowing(tax).getCode(); will(returnValue(""));
            allowing(tax).getRate(); will(returnValue(BigDecimal.ZERO));
            allowing(tax).isExcluded(); will(returnValue(false));

            allowing(deliveryCostCalculationStrategy).calculate(with(any(MutableShoppingCart.class))); will(returnValue(deliveryCost));

            allowing(promotionContext).applyOrderPromo(with(customer), with(any(MutableShoppingCart.class)), with(any(Total.class))); will(returnValue(itemsTotal));

            allowing(promotionContext).applyShippingPromo(with(customer), with(any(MutableShoppingCart.class)), with(any(Total.class)));

        }});

        // Need to use real one to "see" how each dependency behaves
        final AmountCalculationStrategy strategy = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService, shopService);
        final ShoppingCartCalculator calculator = new ShoppingCartCalculatorImpl(strategy);

        final ShoppingCartCalculator.PriceModel model = calculator.calculatePrice(currentCart, "SKU0001", new BigDecimal("9.99"));

        assertNotNull(model);
        assertEquals("9.99", model.getGrossPrice().toPlainString());
        assertEquals("9.99", model.getNetPrice().toPlainString());
        assertEquals("0.00", model.getTaxAmount().toPlainString());
        assertEquals("", model.getTaxCode());
        assertEquals("0", model.getTaxRate().toPlainString());
        assertFalse(model.isTaxExclusive());

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculatePriceAnonymousNoTaxConfigured() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCostCalculationStrategy");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotionContextFactory");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart currentCart = context.mock(ShoppingCart.class, "currentCart");
        final ShoppingContext currentCartCtx = context.mock(ShoppingContext.class, "currentCartCtx");

        final PromotionContext promotionContext = context.mock(PromotionContext.class, "promotionContext");

        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");

        // ZERO total for cart.getCarrierSlaId() == null
        final Total deliveryCost = new TotalImpl();
        // Ignore this as it is not used in calculations
        final Total itemsTotal = new TotalImpl();

        context.checking(new Expectations() {{

            allowing(currentCart).getCurrentLocale(); will(returnValue("en"));
            allowing(currentCart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(currentCart).getShoppingContext(); will(returnValue(currentCartCtx));
            allowing(currentCartCtx).getCustomerName(); will(returnValue(null));
            allowing(currentCartCtx).getShopId(); will(returnValue(123L));
            allowing(currentCartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(currentCartCtx).getCustomerShopId(); will(returnValue(123L));
            allowing(currentCartCtx).getCustomerShopCode(); will(returnValue("SHOP10"));
            allowing(currentCartCtx).getCountryCode(); will(returnValue(null));
            allowing(currentCartCtx).getStateCode(); will(returnValue(null));
            allowing(currentCartCtx).getCustomerEmail(); will(returnValue(null));
            allowing(currentCartCtx).getCustomerShops(); will(returnValue(Collections.emptyList()));
            allowing(currentCartCtx).getLatestViewedSkus(); will(returnValue(null));
            allowing(currentCartCtx).getLatestViewedCategories(); will(returnValue(null));
            allowing(currentCartCtx).getResolvedIp(); will(returnValue("127.0.0.1"));

            allowing(promotionContextFactory).getInstance("SHOP10", "EUR"); will(returnValue(promotionContext));

            allowing(promotionContext).applyItemPromo(with((Customer) null), with(any(MutableShoppingCart.class)));

            allowing(taxProvider).determineTax("SHOP10", "EUR", null, null, "SKU0001"); will(returnValue(tax));
            // NULL tax config
            allowing(tax).getCode(); will(returnValue(""));
            allowing(tax).getRate(); will(returnValue(BigDecimal.ZERO));
            allowing(tax).isExcluded(); will(returnValue(false));

            allowing(deliveryCostCalculationStrategy).calculate(with(any(MutableShoppingCart.class))); will(returnValue(deliveryCost));

            allowing(promotionContext).applyOrderPromo(with((Customer) null), with(any(MutableShoppingCart.class)), with(any(Total.class))); will(returnValue(itemsTotal));

            allowing(promotionContext).applyShippingPromo(with((Customer) null), with(any(MutableShoppingCart.class)), with(any(Total.class)));

        }});

        // Need to use real one to "see" how each dependency behaves
        final AmountCalculationStrategy strategy = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService, shopService);
        final ShoppingCartCalculator calculator = new ShoppingCartCalculatorImpl(strategy);

        final ShoppingCartCalculator.PriceModel model = calculator.calculatePrice(currentCart, "SKU0001", new BigDecimal("9.99"));

        assertNotNull(model);
        assertEquals("9.99", model.getGrossPrice().toPlainString());
        assertEquals("9.99", model.getNetPrice().toPlainString());
        assertEquals("0.00", model.getTaxAmount().toPlainString());
        assertEquals("", model.getTaxCode());
        assertEquals("0", model.getTaxRate().toPlainString());
        assertFalse(model.isTaxExclusive());

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculatePriceRegisteredTaxInclusive() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCostCalculationStrategy");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotionContextFactory");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart currentCart = context.mock(ShoppingCart.class, "currentCart");
        final ShoppingContext currentCartCtx = context.mock(ShoppingContext.class, "currentCartCtx");

        final Shop shop = context.mock(Shop.class, "shop");

        final Customer customer = context.mock(Customer.class, "customer");
        final PromotionContext promotionContext = context.mock(PromotionContext.class, "promotionContext");

        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");

        // ZERO total for cart.getCarrierSlaId() == null
        final Total deliveryCost = new TotalImpl();
        // Ignore this as it is not used in calculations
        final Total itemsTotal = new TotalImpl();

        context.checking(new Expectations() {{

            allowing(currentCart).getCurrentLocale(); will(returnValue("en"));
            allowing(currentCart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(currentCart).getShoppingContext(); will(returnValue(currentCartCtx));
            allowing(currentCartCtx).getCustomerName(); will(returnValue("Bob Doe"));
            allowing(currentCartCtx).getShopId(); will(returnValue(123L));
            allowing(currentCartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(currentCartCtx).getCustomerShopId(); will(returnValue(123L));
            allowing(currentCartCtx).getCustomerShopCode(); will(returnValue("SHOP10"));
            allowing(currentCartCtx).getCountryCode(); will(returnValue("GB"));
            allowing(currentCartCtx).getStateCode(); will(returnValue("GB-GB"));
            allowing(currentCartCtx).getCustomerEmail(); will(returnValue("bob.doe@somewhere.com"));
            allowing(currentCartCtx).getCustomerShops(); will(returnValue(Arrays.asList("SHOP10")));
            allowing(currentCartCtx).getLatestViewedSkus(); will(returnValue(null));
            allowing(currentCartCtx).getLatestViewedCategories(); will(returnValue(null));
            allowing(currentCartCtx).getResolvedIp(); will(returnValue("127.0.0.1"));

            allowing(shopService).getById(123L); will(returnValue(shop));
            allowing(customerService).getCustomerByEmail("bob.doe@somewhere.com", shop); will(returnValue(customer));

            allowing(promotionContextFactory).getInstance("SHOP10", "EUR"); will(returnValue(promotionContext));

            allowing(promotionContext).applyItemPromo(with(customer), with(any(MutableShoppingCart.class)));

            allowing(taxProvider).determineTax("SHOP10", "EUR", "GB", "GB-GB", "SKU0001"); will(returnValue(tax));
            allowing(tax).getCode(); will(returnValue("VAT"));
            allowing(tax).getRate(); will(returnValue(new BigDecimal("20")));
            allowing(tax).isExcluded(); will(returnValue(false));

            allowing(deliveryCostCalculationStrategy).calculate(with(any(MutableShoppingCart.class))); will(returnValue(deliveryCost));

            allowing(promotionContext).applyOrderPromo(with(customer), with(any(MutableShoppingCart.class)), with(any(Total.class))); will(returnValue(itemsTotal));

            allowing(promotionContext).applyShippingPromo(with(customer), with(any(MutableShoppingCart.class)), with(any(Total.class)));

        }});

        // Need to use real one to "see" how each dependency behaves
        final AmountCalculationStrategy strategy = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService, shopService);
        final ShoppingCartCalculator calculator = new ShoppingCartCalculatorImpl(strategy);

        final ShoppingCartCalculator.PriceModel model = calculator.calculatePrice(currentCart, "SKU0001", new BigDecimal("9.99"));

        assertNotNull(model);
        assertEquals("9.99", model.getGrossPrice().toPlainString());
        assertEquals("8.32", model.getNetPrice().toPlainString());
        assertEquals("1.67", model.getTaxAmount().toPlainString());
        assertEquals("VAT", model.getTaxCode());
        assertEquals("20", model.getTaxRate().toPlainString());
        assertFalse(model.isTaxExclusive());

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculatePriceAnonymousNoTaxInclusive() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCostCalculationStrategy");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotionContextFactory");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart currentCart = context.mock(ShoppingCart.class, "currentCart");
        final ShoppingContext currentCartCtx = context.mock(ShoppingContext.class, "currentCartCtx");

        final PromotionContext promotionContext = context.mock(PromotionContext.class, "promotionContext");

        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");

        // ZERO total for cart.getCarrierSlaId() == null
        final Total deliveryCost = new TotalImpl();
        // Ignore this as it is not used in calculations
        final Total itemsTotal = new TotalImpl();

        context.checking(new Expectations() {{

            allowing(currentCart).getCurrentLocale(); will(returnValue("en"));
            allowing(currentCart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(currentCart).getShoppingContext(); will(returnValue(currentCartCtx));
            allowing(currentCartCtx).getCustomerName(); will(returnValue(null));
            allowing(currentCartCtx).getShopId(); will(returnValue(123L));
            allowing(currentCartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(currentCartCtx).getCustomerShopId(); will(returnValue(123L));
            allowing(currentCartCtx).getCustomerShopCode(); will(returnValue("SHOP10"));
            allowing(currentCartCtx).getCountryCode(); will(returnValue(null));
            allowing(currentCartCtx).getStateCode(); will(returnValue(null));
            allowing(currentCartCtx).getCustomerEmail(); will(returnValue(null));
            allowing(currentCartCtx).getCustomerShops(); will(returnValue(Collections.emptyList()));
            allowing(currentCartCtx).getLatestViewedSkus(); will(returnValue(null));
            allowing(currentCartCtx).getLatestViewedCategories(); will(returnValue(null));
            allowing(currentCartCtx).getResolvedIp(); will(returnValue("127.0.0.1"));

            allowing(promotionContextFactory).getInstance("SHOP10", "EUR"); will(returnValue(promotionContext));

            allowing(promotionContext).applyItemPromo(with((Customer) null), with(any(MutableShoppingCart.class)));

            allowing(taxProvider).determineTax("SHOP10", "EUR", null, null, "SKU0001"); will(returnValue(tax));
            allowing(tax).getCode(); will(returnValue("VAT"));
            allowing(tax).getRate(); will(returnValue(new BigDecimal("20")));
            allowing(tax).isExcluded(); will(returnValue(false));

            allowing(deliveryCostCalculationStrategy).calculate(with(any(MutableShoppingCart.class))); will(returnValue(deliveryCost));

            allowing(promotionContext).applyOrderPromo(with((Customer) null), with(any(MutableShoppingCart.class)), with(any(Total.class))); will(returnValue(itemsTotal));

            allowing(promotionContext).applyShippingPromo(with((Customer) null), with(any(MutableShoppingCart.class)), with(any(Total.class)));

        }});

        // Need to use real one to "see" how each dependency behaves
        final AmountCalculationStrategy strategy = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService, shopService);
        final ShoppingCartCalculator calculator = new ShoppingCartCalculatorImpl(strategy);

        final ShoppingCartCalculator.PriceModel model = calculator.calculatePrice(currentCart, "SKU0001", new BigDecimal("9.99"));

        assertNotNull(model);
        assertEquals("9.99", model.getGrossPrice().toPlainString());
        assertEquals("8.32", model.getNetPrice().toPlainString());
        assertEquals("1.67", model.getTaxAmount().toPlainString());
        assertEquals("VAT", model.getTaxCode());
        assertEquals("20", model.getTaxRate().toPlainString());
        assertFalse(model.isTaxExclusive());

        context.assertIsSatisfied();
    }




    @Test
    public void testCalculatePriceRegisteredTaxExclusive() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCostCalculationStrategy");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotionContextFactory");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart currentCart = context.mock(ShoppingCart.class, "currentCart");
        final ShoppingContext currentCartCtx = context.mock(ShoppingContext.class, "currentCartCtx");

        final Shop shop = context.mock(Shop.class, "shop");

        final Customer customer = context.mock(Customer.class, "customer");
        final PromotionContext promotionContext = context.mock(PromotionContext.class, "promotionContext");

        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");

        // ZERO total for cart.getCarrierSlaId() == null
        final Total deliveryCost = new TotalImpl();
        // Ignore this as it is not used in calculations
        final Total itemsTotal = new TotalImpl();

        context.checking(new Expectations() {{

            allowing(currentCart).getCurrentLocale(); will(returnValue("en"));
            allowing(currentCart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(currentCart).getShoppingContext(); will(returnValue(currentCartCtx));
            allowing(currentCartCtx).getCustomerName(); will(returnValue("Bob Doe"));
            allowing(currentCartCtx).getShopId(); will(returnValue(123L));
            allowing(currentCartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(currentCartCtx).getCustomerShopId(); will(returnValue(123L));
            allowing(currentCartCtx).getCustomerShopCode(); will(returnValue("SHOP10"));
            allowing(currentCartCtx).getCountryCode(); will(returnValue("US"));
            allowing(currentCartCtx).getStateCode(); will(returnValue("US-US"));
            allowing(currentCartCtx).getCustomerEmail(); will(returnValue("bob.doe@somewhere.com"));
            allowing(currentCartCtx).getCustomerShops(); will(returnValue(Arrays.asList("SHOP10")));
            allowing(currentCartCtx).getLatestViewedSkus(); will(returnValue(null));
            allowing(currentCartCtx).getLatestViewedCategories(); will(returnValue(null));
            allowing(currentCartCtx).getResolvedIp(); will(returnValue("127.0.0.1"));

            allowing(shopService).getById(123L); will(returnValue(shop));
            allowing(customerService).getCustomerByEmail("bob.doe@somewhere.com", shop); will(returnValue(customer));

            allowing(promotionContextFactory).getInstance("SHOP10", "EUR"); will(returnValue(promotionContext));

            allowing(promotionContext).applyItemPromo(with(customer), with(any(MutableShoppingCart.class)));

            allowing(taxProvider).determineTax("SHOP10", "EUR", "US", "US-US", "SKU0001"); will(returnValue(tax));
            allowing(tax).getCode(); will(returnValue("FED"));
            allowing(tax).getRate(); will(returnValue(new BigDecimal("12")));
            allowing(tax).isExcluded(); will(returnValue(true));

            allowing(deliveryCostCalculationStrategy).calculate(with(any(MutableShoppingCart.class))); will(returnValue(deliveryCost));

            allowing(promotionContext).applyOrderPromo(with(customer), with(any(MutableShoppingCart.class)), with(any(Total.class))); will(returnValue(itemsTotal));

            allowing(promotionContext).applyShippingPromo(with(customer), with(any(MutableShoppingCart.class)), with(any(Total.class)));

        }});

        // Need to use real one to "see" how each dependency behaves
        final AmountCalculationStrategy strategy = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService, shopService);
        final ShoppingCartCalculator calculator = new ShoppingCartCalculatorImpl(strategy);

        final ShoppingCartCalculator.PriceModel model = calculator.calculatePrice(currentCart, "SKU0001", new BigDecimal("9.99"));

        assertNotNull(model);
        assertEquals("11.19", model.getGrossPrice().toPlainString());
        assertEquals("9.99", model.getNetPrice().toPlainString());
        assertEquals("1.20", model.getTaxAmount().toPlainString());
        assertEquals("FED", model.getTaxCode());
        assertEquals("12", model.getTaxRate().toPlainString());
        assertTrue(model.isTaxExclusive());

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculatePriceAnonymousNoTaxExclusive() throws Exception {

        final TaxProvider taxProvider = context.mock(TaxProvider.class, "taxProvider");
        final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy = context.mock(DeliveryCostCalculationStrategy.class, "deliveryCostCalculationStrategy");
        final PromotionContextFactory promotionContextFactory = context.mock(PromotionContextFactory.class, "promotionContextFactory");
        final CustomerService customerService = context.mock(CustomerService.class, "customerService");
        final ShopService shopService = context.mock(ShopService.class, "shopService");

        final ShoppingCart currentCart = context.mock(ShoppingCart.class, "currentCart");
        final ShoppingContext currentCartCtx = context.mock(ShoppingContext.class, "currentCartCtx");

        final PromotionContext promotionContext = context.mock(PromotionContext.class, "promotionContext");

        final TaxProvider.Tax tax = context.mock(TaxProvider.Tax.class, "tax");

        // ZERO total for cart.getCarrierSlaId() == null
        final Total deliveryCost = new TotalImpl();
        // Ignore this as it is not used in calculations
        final Total itemsTotal = new TotalImpl();

        context.checking(new Expectations() {{

            allowing(currentCart).getCurrentLocale(); will(returnValue("en"));
            allowing(currentCart).getCurrencyCode(); will(returnValue("EUR"));
            allowing(currentCart).getShoppingContext(); will(returnValue(currentCartCtx));
            allowing(currentCartCtx).getCustomerName(); will(returnValue(null));
            allowing(currentCartCtx).getShopId(); will(returnValue(123L));
            allowing(currentCartCtx).getShopCode(); will(returnValue("SHOP10"));
            allowing(currentCartCtx).getCustomerShopId(); will(returnValue(123L));
            allowing(currentCartCtx).getCustomerShopCode(); will(returnValue("SHOP10"));
            allowing(currentCartCtx).getCountryCode(); will(returnValue(null));
            allowing(currentCartCtx).getStateCode(); will(returnValue(null));
            allowing(currentCartCtx).getCustomerEmail(); will(returnValue(null));
            allowing(currentCartCtx).getCustomerShops(); will(returnValue(Collections.emptyList()));
            allowing(currentCartCtx).getLatestViewedSkus(); will(returnValue(null));
            allowing(currentCartCtx).getLatestViewedCategories(); will(returnValue(null));
            allowing(currentCartCtx).getResolvedIp(); will(returnValue("127.0.0.1"));

            allowing(promotionContextFactory).getInstance("SHOP10", "EUR"); will(returnValue(promotionContext));

            allowing(promotionContext).applyItemPromo(with((Customer) null), with(any(MutableShoppingCart.class)));

            allowing(taxProvider).determineTax("SHOP10", "EUR", null, null, "SKU0001"); will(returnValue(tax));
            allowing(tax).getCode(); will(returnValue("FED"));
            allowing(tax).getRate(); will(returnValue(new BigDecimal("12")));
            allowing(tax).isExcluded(); will(returnValue(true));

            allowing(deliveryCostCalculationStrategy).calculate(with(any(MutableShoppingCart.class))); will(returnValue(deliveryCost));

            allowing(promotionContext).applyOrderPromo(with((Customer) null), with(any(MutableShoppingCart.class)), with(any(Total.class))); will(returnValue(itemsTotal));

            allowing(promotionContext).applyShippingPromo(with((Customer) null), with(any(MutableShoppingCart.class)), with(any(Total.class)));

        }});

        // Need to use real one to "see" how each dependency behaves
        final AmountCalculationStrategy strategy = new DefaultAmountCalculationStrategy(taxProvider, deliveryCostCalculationStrategy, promotionContextFactory, customerService, shopService);
        final ShoppingCartCalculator calculator = new ShoppingCartCalculatorImpl(strategy);

        final ShoppingCartCalculator.PriceModel model = calculator.calculatePrice(currentCart, "SKU0001", new BigDecimal("9.99"));

        assertNotNull(model);
        assertEquals("11.19", model.getGrossPrice().toPlainString());
        assertEquals("9.99", model.getNetPrice().toPlainString());
        assertEquals("1.20", model.getTaxAmount().toPlainString());
        assertEquals("FED", model.getTaxCode());
        assertEquals("12", model.getTaxRate().toPlainString());
        assertTrue(model.isTaxExclusive());

        context.assertIsSatisfied();
    }

}
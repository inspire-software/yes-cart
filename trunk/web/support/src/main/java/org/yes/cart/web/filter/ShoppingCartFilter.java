/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.web.filter;


import org.springframework.aop.TargetSource;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.shoppingcart.CartDetuplizationException;
import org.yes.cart.web.support.shoppingcart.CartTuplizer;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

/**
 * Shopping cart  filter responsible to restore shopping cart from cookies, if it possible.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 6:13:57 PM
 */
public class ShoppingCartFilter extends AbstractFilter implements Filter {

    private final TargetSource tuplizerPool;

    private final AmountCalculationStrategy calculationStrategy;

    private final ShoppingCartCommandFactory cartCommandFactory;


    /**
     * @param applicationDirector app director.
     * @param tuplizerPool        pool of tuplizer to manage cookie to object to cookie transformation
     * @param calculationStrategy calculation strategy
     * @param cartCommandFactory  cart command factory
     */
    public ShoppingCartFilter(final ApplicationDirector applicationDirector,
                              final TargetSource tuplizerPool,
                              final AmountCalculationStrategy calculationStrategy,
                              final ShoppingCartCommandFactory cartCommandFactory) {
        super(applicationDirector);
        this.tuplizerPool = tuplizerPool;
        this.calculationStrategy = calculationStrategy;
        this.cartCommandFactory = cartCommandFactory;
    }


    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest request,
                                   final ServletResponse response) throws IOException, ServletException {


        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        CartTuplizer tuplizer = null;
        try {
            tuplizer = (CartTuplizer) tuplizerPool.getTarget();
            ShoppingCart cart = new ShoppingCartImpl();
            try {
                ShoppingCart restored = tuplizer.detuplize(httpRequest);
                if (restored != null) {
                    cart = restored;
                }
            } catch (CartDetuplizationException e) {
                ShopCodeContext.getLog(this).warn("Cart not restored from cookies");
            }
            cart.initialise(calculationStrategy);
            setDefaultValuesIfNecessary(ApplicationDirector.getCurrentShop(), cart);
            ApplicationDirector.setShoppingCart(cart);

        } catch (Exception e) {
            ShopCodeContext.getLog(this).error("Can process request", e);
        } finally {
            if (tuplizer != null) {
                try {
                    tuplizerPool.releaseTarget(tuplizer);
                } catch (Exception e) {
                    ShopCodeContext.getLog(this).error("Can return object to pool ", e);
                }
            }
        }

        return request;
    }


    /**
     * Set default values. Mostly for new cart.
     *
     * @param shop shop
     * @param cart cart
     */
    private void setDefaultValuesIfNecessary(final Shop shop, final ShoppingCart cart) {

        if (cart.getCurrencyCode() == null && shop != null) { // new cart only may satisfy this condition

            cartCommandFactory.execute(cart, new HashMap<String, Object>() {{
                put(ShoppingCartCommand.CMD_SETSHOP, shop.getShopId());
                put(ShoppingCartCommand.CMD_CHANGECURRENCY, shop.getDefaultCurrency());
            }});

        }

    }



    /**
     * {@inheritDoc}
     */
    public void doAfter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        ApplicationDirector.setShoppingCart(null);
    }


}

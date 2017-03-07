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

package org.yes.cart.web.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.shoppingcart.support.CartDetuplizationException;
import org.yes.cart.shoppingcart.support.CartTuplizer;
import org.yes.cart.web.application.ApplicationDirector;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Shopping cart  filter responsible to restore shopping cart from cookies, if it possible.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 6:13:57 PM
 */
public class ShoppingCartFilter extends AbstractFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartFilter.class);

    private final TargetSource tuplizerPool;

    private final AmountCalculationStrategy calculationStrategy;

    private final ShoppingCartCommandFactory cartCommandFactory;


    /**
     * @param tuplizerPool        pool of tuplizer to manage cookie to object to cookie transformation
     * @param calculationStrategy calculation strategy
     * @param cartCommandFactory  cart command factory
     */
    public ShoppingCartFilter(final TargetSource tuplizerPool,
                              final AmountCalculationStrategy calculationStrategy,
                              final ShoppingCartCommandFactory cartCommandFactory) {
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
            MutableShoppingCart cart = null;
            try {
                MutableShoppingCart restored = (MutableShoppingCart) tuplizer.detuplize(httpRequest);
                if (restored != null) {
                    cart = restored;
                }
            } catch (CartDetuplizationException e) {
                LOG.warn("Cart not restored from cookies");
            } catch (Exception e) {
                LOG.error("Cart not restored from cookies", e);
            }
            if (cart == null) {
                cart = new ShoppingCartImpl();
            }
            cart.initialise(calculationStrategy);
            setDefaultValuesIfNecessary(ApplicationDirector.getCurrentShop(), cart);
            ApplicationDirector.setShoppingCart(cart);
            request.setAttribute("ShoppingCart", cart);

        } catch (Exception e) {
            LOG.error("Can process request", e);
        } finally {
            if (tuplizer != null) {
                try {
                    tuplizerPool.releaseTarget(tuplizer);
                } catch (Exception e) {
                    LOG.error("Can return object to pool ", e);
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

        final Map<String, Object> params = new HashMap<String, Object>();
        if (cart.getCurrencyCode() == null && shop != null) { // new cart only may satisfy this condition

            params.put(ShoppingCartCommand.CMD_SETSHOP, shop.getShopId());
            params.put(ShoppingCartCommand.CMD_CHANGECURRENCY, shop.getDefaultCurrency());

            cartCommandFactory.execute(cart, params);

        }

        params.put(ShoppingCartCommand.CMD_INTERNAL_SETIP, ApplicationDirector.getShopperIPAddress());
        cartCommandFactory.execute(ShoppingCartCommand.CMD_INTERNAL_SETIP, cart, params);

    }



    /**
     * {@inheritDoc}
     */
    public void doAfter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        ApplicationDirector.setShoppingCart(null);
        servletRequest.removeAttribute("ShoppingCart");
    }


}

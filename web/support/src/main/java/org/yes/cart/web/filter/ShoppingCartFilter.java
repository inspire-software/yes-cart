/*
 * Copyright 2009 Inspire-Software.com
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;
import org.yes.cart.shoppingcart.support.CartDetuplizationException;
import org.yes.cart.shoppingcart.support.CartTuplizer;
import org.yes.cart.utils.log.Markers;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.utils.HttpUtil;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    private static final List<SimpleGrantedAuthority> ROLE_CUSTOMER = Collections.singletonList(new SimpleGrantedAuthority("ROLE_SF_CUSTOMER"));
    private static final List<SimpleGrantedAuthority> ROLE_MANAGER = Collections.singletonList(new SimpleGrantedAuthority("ROLE_SF_MANAGER"));

    private final ShopService shopService;

    private final CartTuplizer cartTuplizer;

    private final AmountCalculationStrategy calculationStrategy;
    private final ShoppingCartCommandFactory cartCommandFactory;


    /**
     * @param shopService         shop service
     * @param cartTuplizer        cart tuplizer to manage cookie to object to cookie transformation
     * @param calculationStrategy calculation strategy
     * @param cartCommandFactory  cart command factory
     */
    public ShoppingCartFilter(final ShopService shopService,
                              final CartTuplizer cartTuplizer,
                              final AmountCalculationStrategy calculationStrategy,
                              final ShoppingCartCommandFactory cartCommandFactory) {
        this.shopService = shopService;
        this.cartTuplizer = cartTuplizer;
        this.calculationStrategy = calculationStrategy;
        this.cartCommandFactory = cartCommandFactory;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ServletRequest doBefore(final ServletRequest request,
                                   final ServletResponse response) throws IOException, ServletException {

        final Shop shop = ApplicationDirector.getCurrentShop();
        if (shop != null) {

            final HttpServletRequest httpRequest = (HttpServletRequest) request;

            if (shop.isSfPageTraceOn()) {
                LOG.info("HTTP Headers: {}", HttpUtil.dumpRequest(httpRequest));
            }

            MutableShoppingCart cart = null;
            try {
                MutableShoppingCart restored = (MutableShoppingCart) cartTuplizer.detuplize(httpRequest);
                if (restored != null) {
                    cart = restored;
                }
            } catch (CartDetuplizationException e) {
                LOG.warn("Cart failed to restore", e);
            } catch (Exception e) {
                LOG.error("Cart failed to restore", e);
            }
            if (cart == null) {
                LOG.warn(Markers.alert(), "Tuplizer failed, using default implementation for cart");
                cart = new ShoppingCartImpl();
                cart.initialise(calculationStrategy);
            }
            setDefaultValuesIfNecessary(shop, cart);
            ApplicationDirector.setCurrentCustomerShop(getCurrentCustomerShop(shop, cart));
            ApplicationDirector.setShoppingCart(cart);
            request.setAttribute("ShoppingCart", cart);
            if (cart.getLogonState() == ShoppingCart.LOGGED_IN) {
                final SecurityContextImpl ctx = new SecurityContextImpl();
                ctx.setAuthentication(new UsernamePasswordAuthenticationToken(cart.getCustomerLogin(), null, ROLE_CUSTOMER));
                SecurityContextHolder.setContext(ctx);
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        return request;
    }

    /**
     * Determine current customer shop.
     *
     * @param shop current shop
     * @param cart current cart
     *
     * @return current customer shop
     */
    private Shop getCurrentCustomerShop(final Shop shop, final ShoppingCart cart) {
        if (cart != null && cart.getShoppingContext().getCustomerShopId() != shop.getShopId()) {
            return this.shopService.getById(cart.getShoppingContext().getCustomerShopId());
        }
        return shop;
    }


    /**
     * Set default values. Mostly for new cart.
     *
     * @param shop shop
     * @param cart cart
     */
    private void setDefaultValuesIfNecessary(final Shop shop, final ShoppingCart cart) {

        final Map<String, Object> params = new HashMap<>();
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
    @Override
    public void doAfter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        ApplicationDirector.setShoppingCart(null);
        servletRequest.removeAttribute("ShoppingCart");
        SecurityContextHolder.clearContext();
    }


}

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


import org.springframework.aop.target.CommonsPoolTargetSource;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.shoppingcart.impl.WebShoppingCartImpl;
import org.yes.cart.web.support.util.cookie.CookieTuplizer;
import org.yes.cart.web.support.util.cookie.UnableToObjectizeCookieException;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

/**
 * Shopping cart  filter responsible to restore shopping cart from cookies, if it possible.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 6:13:57 PM
 */
public class ShoppingCartFilter extends AbstractFilter implements Filter {

    //private final CookieTuplizer tuplizer;
    private final CommonsPoolTargetSource tuplizerPool;

    private final AmountCalculationStrategy calculationStrategy;


    /**
     * @param tuplizerPool        pool of tuplizer to manage cookie to object to cookie transformation
     * @param applicationDirector app director.
     * @param calculationStrategy calculation strategy
     */
    public ShoppingCartFilter(
            final ApplicationDirector applicationDirector,
            final CommonsPoolTargetSource tuplizerPool,
            final AmountCalculationStrategy calculationStrategy) {
        super(applicationDirector);
        this.tuplizerPool = tuplizerPool;
        this.calculationStrategy = calculationStrategy;
    }


    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest request,
                                   final ServletResponse response) throws IOException, ServletException {


        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        CookieTuplizer tuplizer = null;
        try {
            tuplizer = (CookieTuplizer) tuplizerPool.getTarget();
            ShoppingCart cart = new WebShoppingCartImpl();
            try {
                cart = tuplizer.toObject(
                        httpRequest.getCookies(),
                        cart);
            } catch (UnableToObjectizeCookieException e) {
                ShopCodeContext.getLog(this).warn("Cart not restored from cookies");
            }
            cart.setProcessingStartDate(new Date());
            cart.setCalculationStrategy(calculationStrategy);
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
     * {@inheritDoc}
     */
    public void doAfter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        ApplicationDirector.setShoppingCart(null);
    }


}

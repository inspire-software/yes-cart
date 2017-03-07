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

package org.yes.cart.shoppingcart.support.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.support.CartTuplizationException;
import org.yes.cart.shoppingcart.support.CartTuplizer;
import org.yes.cart.shoppingcart.support.ShoppingCartPersister;

import java.text.MessageFormat;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/14/11
 * Time: 9:04 PM
 */
public class BasicShoppingCartPersisterImpl implements ShoppingCartPersister<Map, Map> {

    private static final Logger LOG = LoggerFactory.getLogger(BasicShoppingCartPersisterImpl.class);

    private final TargetSource tuplizerPool;


    /**
     * Construct shopping cart persister phaze listener
     *
     * @param tuplizerPool        pool of tuplizer to manage cookie to object to cookie transformation
     */
    public BasicShoppingCartPersisterImpl(final TargetSource tuplizerPool) {
        this.tuplizerPool = tuplizerPool;
    }


    /**
     * {@inheritDoc}
     */
    public void persistShoppingCart(final Map httpServletRequest,
                                    final Map httpServletResponse,
                                    final ShoppingCart shoppingCart) {

        CartTuplizer tuplizer = null;
        try {

            tuplizer = (CartTuplizer<Map, Map>) tuplizerPool.getTarget();
            try {
                tuplizer.tuplize(httpServletRequest, httpServletResponse, shoppingCart);
            } catch (CartTuplizationException e) {
                LOG.error(MessageFormat.format("Unable to create cookies from {0} cart", shoppingCart), e);
            }

        } catch (Exception e) {
            LOG.error("Can process request: " + e.getMessage(), e);
        } finally {
            if (tuplizer != null) {
                try {
                    tuplizerPool.releaseTarget(tuplizer);
                } catch (Exception e) {
                    LOG.error("Can return object to pool: " + e.getMessage(), e);
                }
            }
        }

    }

}

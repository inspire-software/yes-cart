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

package org.yes.cart.shoppingcart.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 03/06/2015
 * Time: 10:48
 */
public class SetIpInternalCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final Logger LOG = LoggerFactory.getLogger(SetIpInternalCommandImpl.class);

    public SetIpInternalCommandImpl(final ShoppingCartCommandRegistry registry) {
        super(registry);
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {

            final String oldIp = shoppingCart.getShoppingContext().getResolvedIp();
            final String newIp = (String) parameters.get(getCmdKey());

            // only check valid IP
            if (StringUtils.isNotBlank(newIp)) {

                if (StringUtils.isBlank(oldIp) || !oldIp.equals(newIp)) {

                    LOG.debug("Cart {} is accessed from IP {}", shoppingCart.getGuid(), newIp);

                    shoppingCart.getShoppingContext().setResolvedIp(newIp);
                    shoppingCart.markDirty();

                }

            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return ShoppingCartCommand.CMD_INTERNAL_SETIP;
    }
}

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

package org.yes.cart.shoppingcart.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;
import org.yes.cart.util.ShopCodeContext;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SetCarrierSlaCartCommandImpl  extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20100313L;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     */
    public SetCarrierSlaCartCommandImpl(final ShoppingCartCommandRegistry registry) {
        super(registry);
    }

    /**
     * @return command key
     */
    public String getCmdKey() {
        return CMD_SETCARRIERSLA;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.containsKey(getCmdKey())) {
            final String val = (String) parameters.get(getCmdKey());
            final Long slaPkvalue = val != null ? NumberUtils.createLong(val) : null;
            if ((slaPkvalue == null && shoppingCart.getOrderInfo().getCarrierSlaId() != null)
                    || (slaPkvalue != null && !slaPkvalue.equals(shoppingCart.getOrderInfo().getCarrierSlaId()))) {
                ShopCodeContext.getLog(this).debug("Set carrier sla to {}", slaPkvalue);

                shoppingCart.getOrderInfo().setCarrierSlaId(slaPkvalue);
                setAddressNotRequiredFlags(shoppingCart, parameters);

                if (slaPkvalue != null) {
                    setAddressParametersIfRequired(shoppingCart, parameters);
                } else {
                    shoppingCart.getOrderInfo().setBillingAddressId(null);
                    shoppingCart.getOrderInfo().setDeliveryAddressId(null);
                    shoppingCart.getShoppingContext().setCountryCode(null);
                    shoppingCart.getShoppingContext().setStateCode(null);
                }
                recalculate(shoppingCart);
                markDirty(shoppingCart);
            }
        }
    }

    private void setAddressParametersIfRequired(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        final boolean notRequiredBilling = shoppingCart.getOrderInfo().isBillingAddressNotRequired();
        if (notRequiredBilling) {
            if (shoppingCart.getOrderInfo().getBillingAddressId() != null) {
                shoppingCart.getOrderInfo().setBillingAddressId(null);
            }
        } else {
            final Address billing = (Address) parameters.get(CMD_SETCARRIERSLA_P_BILLING_ADDRESS);
            if (billing != null) {
                if (!Long.valueOf(billing.getAddressId()).equals(shoppingCart.getOrderInfo().getBillingAddressId())) {
                    shoppingCart.getOrderInfo().setBillingAddressId(billing.getAddressId());
                    shoppingCart.getShoppingContext().setCountryCode(billing.getCountryCode());
                    shoppingCart.getShoppingContext().setStateCode(billing.getStateCode());
                }
            } else if (shoppingCart.getOrderInfo().getBillingAddressId() != null) {
                shoppingCart.getOrderInfo().setBillingAddressId(null);
                shoppingCart.getShoppingContext().setCountryCode(null);
                shoppingCart.getShoppingContext().setStateCode(null);
            }
        }

        final boolean notRequiredShipping = shoppingCart.getOrderInfo().isDeliveryAddressNotRequired();
        if (notRequiredShipping) {
            if (shoppingCart.getOrderInfo().getDeliveryAddressId() != null) {
                shoppingCart.getOrderInfo().setDeliveryAddressId(null);
            }
        } else {
            final Address delivery = (Address) parameters.get(CMD_SETCARRIERSLA_P_DELIVERY_ADDRESS);
            if (delivery != null) {
                if (!Long.valueOf(delivery.getAddressId()).equals(shoppingCart.getOrderInfo().getDeliveryAddressId())) {
                    shoppingCart.getOrderInfo().setDeliveryAddressId(delivery.getAddressId());
                    if (!shoppingCart.isSeparateBillingAddress() && !shoppingCart.isBillingAddressNotRequired()) {
                        shoppingCart.getOrderInfo().setBillingAddressId(delivery.getAddressId());
                        shoppingCart.getShoppingContext().setCountryCode(delivery.getCountryCode());
                        shoppingCart.getShoppingContext().setStateCode(delivery.getStateCode());
                    }
                }
            } else if (shoppingCart.getOrderInfo().getDeliveryAddressId() != null) {
                shoppingCart.getOrderInfo().setDeliveryAddressId(null);
            }
        }
    }

    private void setAddressNotRequiredFlags(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        if (parameters.get(CMD_SETCARRIERSLA_P_BILLING_NOT_REQUIRED) instanceof Boolean) {
            shoppingCart.getOrderInfo().setBillingAddressNotRequired((Boolean) parameters.get(CMD_SETCARRIERSLA_P_BILLING_NOT_REQUIRED));
        } else if (parameters.get(CMD_SETCARRIERSLA_P_BILLING_NOT_REQUIRED) instanceof String) {
            shoppingCart.getOrderInfo().setBillingAddressNotRequired(Boolean.valueOf((String) parameters.get(CMD_SETCARRIERSLA_P_BILLING_NOT_REQUIRED)));
        } else {
            shoppingCart.getOrderInfo().setBillingAddressNotRequired(false);
        }
        if (parameters.get(CMD_SETCARRIERSLA_P_DELIVERY_NOT_REQUIRED) instanceof Boolean) {
            shoppingCart.getOrderInfo().setDeliveryAddressNotRequired((Boolean) parameters.get(CMD_SETCARRIERSLA_P_DELIVERY_NOT_REQUIRED));
        } else if (parameters.get(CMD_SETCARRIERSLA_P_DELIVERY_NOT_REQUIRED) instanceof String) {
            shoppingCart.getOrderInfo().setDeliveryAddressNotRequired(Boolean.valueOf((String) parameters.get(CMD_SETCARRIERSLA_P_DELIVERY_NOT_REQUIRED)));
        } else {
            shoppingCart.getOrderInfo().setDeliveryAddressNotRequired(false);
        }
    }
}

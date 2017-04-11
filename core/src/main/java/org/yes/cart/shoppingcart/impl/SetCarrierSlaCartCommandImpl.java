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
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.shoppingcart.DeliveryTimeEstimationVisitor;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SetCarrierSlaCartCommandImpl extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20100313L;

    private static final Logger LOG = LoggerFactory.getLogger(SetCarrierSlaCartCommandImpl.class);

    private final DeliveryTimeEstimationVisitor deliveryTimeEstimationVisitor;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     * @param deliveryTimeEstimationVisitor visitor for estimating available delivery time
     */
    public SetCarrierSlaCartCommandImpl(final ShoppingCartCommandRegistry registry,
                                        final DeliveryTimeEstimationVisitor deliveryTimeEstimationVisitor) {
        super(registry);
        this.deliveryTimeEstimationVisitor = deliveryTimeEstimationVisitor;
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
            final String slaIdsRaw = (String) parameters.get(getCmdKey());
            final String[] slaIds = StringUtils.split(slaIdsRaw, '|');

            if (slaIds != null && slaIds.length > 0) {

                final Map<String, Long> selection = new HashMap<String, Long>();
                for (final String slaIdRaw : slaIds) {

                    final int sepPos = slaIdRaw.indexOf('-');
                    final String[] slaId = sepPos == -1 ? new String[] { slaIdRaw } : new String[] { slaIdRaw.substring(0, sepPos), slaIdRaw.substring(sepPos + 1) };

                    final long slaPkvalue = NumberUtils.toLong(slaId[0]);
                    final String supplier = slaId.length > 1 ? slaId[1] : "";

                    final Long current = shoppingCart.getCarrierSlaId().get(supplier);

                    if ((slaPkvalue <= 0L && current != null && current > 0L) ||
                            (slaPkvalue > 0L && (current == null || !current.equals(slaPkvalue)))) {
                        selection.put(supplier, slaPkvalue);
                    }

                }

                if (!selection.isEmpty()) {

                    for (final Map.Entry<String, Long> slaSelection : selection.entrySet()) {
                        LOG.debug("Set carrier sla to {} for '{}'", slaSelection.getValue(), slaSelection.getKey());
                        if (slaSelection.getValue() <= 0L) {
                            shoppingCart.getOrderInfo().putCarrierSlaId(slaSelection.getKey(), null);
                        } else {
                            shoppingCart.getOrderInfo().putCarrierSlaId(slaSelection.getKey(), slaSelection.getValue());
                        }
                    }

                    final boolean hasSelection = !shoppingCart.getCarrierSlaId().isEmpty();

                    setAddressNotRequiredFlags(shoppingCart, parameters);

                    if (hasSelection) {
                        setAddressParametersIfRequired(shoppingCart, parameters);
                    } else {
                        shoppingCart.getOrderInfo().setBillingAddressId(null);
                        shoppingCart.getOrderInfo().setDeliveryAddressId(null);
                        shoppingCart.getShoppingContext().setCountryCode(null);
                        shoppingCart.getShoppingContext().setStateCode(null);
                    }

                    // if this is named delivery determine available dates
                    this.deliveryTimeEstimationVisitor.visit(shoppingCart);

                    recalculate(shoppingCart);
                    markDirty(shoppingCart);
                }
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

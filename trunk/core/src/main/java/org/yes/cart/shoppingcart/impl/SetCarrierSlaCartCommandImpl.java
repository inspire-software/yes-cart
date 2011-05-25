package org.yes.cart.shoppingcart.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SetCarrierSlaCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 20100313L;

    private static final Logger LOG = LoggerFactory.getLogger(SetCarrierSlaCartCommandImpl.class);

    public static String CMD_KEY = "setCarrierSlaCmd";

    private Integer slaPkvalue = null;


    /**
     * Construct command.
     * @param applicationContext application context
     * @param parameters page parameters
     */
    public SetCarrierSlaCartCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super();
        final String val = (String) parameters.get(CMD_KEY);
        if (val != null) {
            slaPkvalue = NumberUtils.createInteger(val);
        }

    }

    /**
     * Execute command on shopping cart to perform changes.
     *
     * @param shoppingCart the shopping cart
     */
    public void execute(final ShoppingCart shoppingCart) {
        if(LOG.isDebugEnabled()) {
            LOG.debug("Set carrier sla to " + slaPkvalue);
        }
        shoppingCart.getOrderInfo().setCarrierSlaId(slaPkvalue);
    }

    /**
     * @return command key
     */
    public String getCmdKey() {
        return CMD_KEY;
    }
}

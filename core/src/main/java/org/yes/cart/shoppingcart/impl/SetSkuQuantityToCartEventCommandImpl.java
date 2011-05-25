package org.yes.cart.shoppingcart.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;

/**
 *
 * Set sku quantity in cart command class.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SetSkuQuantityToCartEventCommandImpl extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20110312L;

    private static final Logger LOG = LoggerFactory.getLogger(SetSkuQuantityToCartEventCommandImpl.class);

    public static String CMD_KEY = "addQuantityToCartCmd";

    public static String CMD_PARAM_QTY = "qty";

    private String skuQty;


    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return CMD_KEY;
    }

    /**
     * Construct command.
     * @param applicationContext application context
     * @param parameters parameters
     */
    public SetSkuQuantityToCartEventCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super(applicationContext, parameters);
        skuQty = (String) parameters.get(CMD_PARAM_QTY);
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final ShoppingCart shoppingCart) {
        if (getProductSkuDTO() != null) {
            shoppingCart.setProductSkuToCart(getProductSkuDTO(), new BigDecimal(skuQty));
            recalculatePrice(shoppingCart);
            if (LOG.isDebugEnabled()) {
                LOG.debug(MessageFormat.format("Add product sku with code {0} and qty {1} to cart",
                        getProductSkuDTO().getCode(),
                        skuQty));
            }
        }
    }

}

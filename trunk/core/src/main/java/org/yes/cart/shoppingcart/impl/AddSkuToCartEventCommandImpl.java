package org.yes.cart.shoppingcart.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Default implementation of the add to cart visitor.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:17:10 PM
 */
public class AddSkuToCartEventCommandImpl extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20100122L;

    private static final Logger LOG = LoggerFactory.getLogger(AddSkuToCartEventCommandImpl.class);

    public static String CMD_KEY = "addToCartCmd";


    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return CMD_KEY;
    }

    public AddSkuToCartEventCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super(applicationContext, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final ShoppingCart shoppingCart) {
        if (getProductSkuDTO() != null) {
            shoppingCart.addProductSkuToCart(getProductSkuDTO(), BigDecimal.ONE);
            recalculatePrice(shoppingCart);
            setModifiedDate(shoppingCart);
            if (LOG.isDebugEnabled()) {
                LOG.debug(
                        MessageFormat.format(
                                "Added one item of sku code {0} to cart",
                                getProductSkuDTO().getCode()
                                )
                );                
            }
        }
    }
}

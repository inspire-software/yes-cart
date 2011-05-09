package org.yes.cart.shoppingcart.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.yes.cart.domain.dto.ShoppingCart;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;

/**
 *
 * Remove one sku from cart.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class RemoveSkuFromCartCommandImpl extends AbstractSkuCartCommandImpl{

    private static final long serialVersionUID = 20100313L;

    private static final Logger LOG = LoggerFactory.getLogger(RemoveSkuFromCartCommandImpl.class);

    public static String CMD_KEY = "removeOneSkuCmd";


    /** {@inheritDoc} */
    public String getCmdKey() {
        return CMD_KEY;
    }

   /**
     *
     * @param applicationContext application context
     * @param parameters page parameters
     */
    public RemoveSkuFromCartCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super(applicationContext, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final ShoppingCart shoppingCart) {
        if (getProductSkuDTO() != null) {
            final String skuCode = getProductSkuDTO().getCode();
            if(!shoppingCart.removeCartItemQuantity(getProductSkuDTO(), BigDecimal.ONE)) {
                LOG.warn(MessageFormat.format("Can not remove one sku with code {0} from cart",
                        skuCode));
            }

            recalculatePrice(shoppingCart);
        }
    }



}

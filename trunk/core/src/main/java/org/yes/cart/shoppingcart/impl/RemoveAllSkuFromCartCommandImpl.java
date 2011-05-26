package org.yes.cart.shoppingcart.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.text.MessageFormat;
import java.util.Map;

/**
 *
 * Remove all sku from cart by given sku code.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class RemoveAllSkuFromCartCommandImpl extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20100313L;

    public static String CMD_KEY = "removeAllSkuCmd";

    private static final Logger LOG = LoggerFactory.getLogger(RemoveAllSkuFromCartCommandImpl.class);


    /** {@inheritDoc} */
    public String getCmdKey() {
        return CMD_KEY;
    }

   /**
     *
     * @param applicationContext application context
     * @param parameters page parameters
     */
    public RemoveAllSkuFromCartCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super(applicationContext, parameters);
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final ShoppingCart shoppingCart) {
        if (getProductSkuDTO() != null) {
            if(!shoppingCart.removeCartItem(getProductSkuDTO())) {
                LOG.warn(MessageFormat.format("Can not remove all skus with code {0} from cart",
                        getProductSkuDTO().getCode()));

            } else  {
                setModifiedDate(shoppingCart);
            }
        }
    }
}

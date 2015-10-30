package org.yes.cart.web.resource;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.service.CheckoutServiceFacade;

import java.io.ByteArrayOutputStream;

/**
 * User: denispavlov
 * Date: 29/10/2015
 * Time: 22:00
 */
public class OrderReceiptPdfResource extends AbstractDynamicResource {

    private final CheckoutServiceFacade checkoutServiceFacade;

    public OrderReceiptPdfResource(final CheckoutServiceFacade checkoutServiceFacade) {
        super("application/pdf");
        this.checkoutServiceFacade = checkoutServiceFacade;
    }

    @Override
    protected byte[] getData(final Attributes attributes) {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();

        if (cart != null && cart.getLogonState() == ShoppingCart.LOGGED_IN) {

            final String ordernum = attributes.getParameters().get("ordernum").toString();
            if (StringUtils.isNotBlank(ordernum)) {

                final CustomerOrder order = checkoutServiceFacade.findByReference(ordernum);
                if (order != null && cart.getCustomerEmail().equals(order.getCustomer().getEmail())) {

                    try {
                        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        checkoutServiceFacade.printOrderByReference(ordernum, baos);
                        baos.close();
                        return baos.toByteArray();
                    } catch (Exception exp) {
                        ShopCodeContext.getLog(this).error("Receipt for " + ordernum
                                + " error, caused by " + exp.getMessage(), exp);
                    }

                }

            }
        }

        return new byte[0];

    }
}

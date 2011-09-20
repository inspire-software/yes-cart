package org.yes.cart.web.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.shoppingcart.impl.WebShoppingCartImpl;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.util.cookie.CookieTuplizer;
import org.yes.cart.web.support.util.cookie.UnableToObjectizeCookieException;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Shopping cart  filter responsible to restore shopping cart from cookies, if it possible.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 6:13:57 PM
 */
public class ShoppingCartFilter extends AbstractFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartFilter.class);

    private final CookieTuplizer tuplizer;


    /**
     * @param tuplizer   tuplizer to manage cookie to object to cookie transformation
     * @param applicationDirector app director.
     */
    public ShoppingCartFilter(
            final ApplicationDirector applicationDirector,
            final CookieTuplizer tuplizer) {
        super(applicationDirector);
        this.tuplizer = tuplizer;
    }


    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest request,
                                   final ServletResponse response) throws IOException, ServletException {


        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        synchronized (tuplizer) {    //TODO here can be a bottle neck, so may be need to use a pool
            ShoppingCart cart = new WebShoppingCartImpl();
            try {
                cart = tuplizer.toObject(
                            httpRequest.getCookies(),
                            cart);
            } catch (UnableToObjectizeCookieException e) {
                if(LOG.isWarnEnabled()) {
                    LOG.warn("Cart not restored from cookies");
                }
            }
            cart.setProcessingStartDate(new Date());
            ApplicationDirector.setShoppingCart(cart);
        }
        return request;
    }



    /**
     * {@inheritDoc}
     */
    public void doAfter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        // NOTHING
    }


}

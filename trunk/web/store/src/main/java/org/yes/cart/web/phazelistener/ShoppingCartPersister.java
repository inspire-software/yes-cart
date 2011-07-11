package org.yes.cart.web.phazelistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.util.cookie.CookieTuplizer;
import org.yes.cart.web.support.util.cookie.UnableToCookielizeObjectException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.text.MessageFormat;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/14/11
 * Time: 9:04 PM
 */
public class ShoppingCartPersister  {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartPersister.class);

    private final CookieTuplizer cookieTuplizer;

    /**
     * Construct shopping cart persister phaze listener
     *
     * @param cookieTuplizer tuplizer to use
     */
    public ShoppingCartPersister(final CookieTuplizer cookieTuplizer) {
        this.cookieTuplizer = cookieTuplizer;
    }


    /**
     * {@inheritDoc}
     */
    /*public void afterPhase(PhaseEvent phaseEvent) {

        final ShoppingCart shoppingCart = (ShoppingCart) facesContext.getApplication()
                .getVariableResolver().resolveVariable(facesContext, WebParametersKeys.SESSION_SHOPPING_CART);

        final Cookie[] oldCookies = httpServletRequest.getCookies();
        try {
            final Cookie[] cookies = cookieTuplizer.toCookies(oldCookies, shoppingCart);
            for (Cookie cookie : cookies) {
                httpServletResponse.addCookie(cookie);
            }
        } catch (UnableToCookielizeObjectException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(MessageFormat.format("Unable to create cookies from {0}", shoppingCart), e);
            }
        }

    }  */

}

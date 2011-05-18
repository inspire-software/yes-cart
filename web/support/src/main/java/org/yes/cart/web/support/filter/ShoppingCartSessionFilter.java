package org.yes.cart.web.support.filter;


import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.ExpireCartCommandImpl;
import org.yes.cart.web.support.shoppingcart.RequestRuntimeContainer;
import org.yes.cart.web.support.shoppingcart.VisitableShoppingCart;
import org.yes.cart.web.support.shoppingcart.impl.VisitableShoppingCartProxy;
import org.yes.cart.web.support.util.cookie.CookieTuplizer;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;

/**
 * Shooping cart filter will create a lazy shopping cart proxy.
 * <p/>
 * User: dogma
 * Date: 2011-May-17
 * Time: 6:13:57 PM
 */
public class ShoppingCartSessionFilter extends AbstractFilter implements Filter {

    private final CookieTuplizer tuplizer;
    private final ShoppingCartCommandFactory shoppingCartCommandFactory;


    /**
     * @param tuplizer                   tuplizer to manage cookie to object to cookie transformation
     * @param container                  current runtime container
     * @param shoppingCartCommandFactory command factory to get expire command and perform expire action on long term
     *                                   shopping cart
     */
    public ShoppingCartSessionFilter(final CookieTuplizer tuplizer,
                                     final RequestRuntimeContainer container,
                                     final ShoppingCartCommandFactory shoppingCartCommandFactory) {
        super(container);
        this.tuplizer = tuplizer;
        this.shoppingCartCommandFactory = shoppingCartCommandFactory;
    }


    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest request,
                                   final ServletResponse response) throws IOException, ServletException {


        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        final VisitableShoppingCart defaultCart = getRequestRuntimeContainer().getShoppingCart();

        final VisitableShoppingCart fromCookiesCart = new VisitableShoppingCartProxy(
                httpRequest,
                defaultCart,
                tuplizer);


        getRequestRuntimeContainer().setShoppingCart(fromCookiesCart);

        expireCustomerOnShoppingCart(httpRequest);

        return request;
    }


    /**
     * Expire customen in case if new session where create
     *
     * @param httpRequest http request
     */
    private void expireCustomerOnShoppingCart(final HttpServletRequest httpRequest) {

        if (httpRequest.getSession().isNew()) {
            final ShoppingCartCommand expireCommand = shoppingCartCommandFactory.create(
                    Collections.singletonMap(ExpireCartCommandImpl.CMD_KEY, ExpireCartCommandImpl.CMD_KEY)
            );
            //expireCommand.execute(getRequestRuntimeContainer().getShoppingCart());
            //TODO
        }

    }


    /**
     * {@inheritDoc}
     */
    public void doAfter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {
        // NOTHING
    }


}

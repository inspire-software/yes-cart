package org.yes.cart.shoppingcart.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.Collections;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class LoginCommandImpl  extends AbstractCartCommandImpl  implements ShoppingCartCommand {

    private static final long serialVersionUID = 20101026L;

    public static final String CMD_KEY = "loginCmd";
    public static final String EMAIL = "email";
    public static final String NAME = "name";

    private final Map parameters;

    /** {@inheritDoc} */
    public void execute(final ShoppingCart shoppingCart) {



        final User user = new User((String) parameters.get(EMAIL), StringUtils.EMPTY, true, true, true, true, Collections.EMPTY_LIST);

        final Authentication authentication = new UsernamePasswordAuthenticationToken(user, StringUtils.EMPTY, Collections.EMPTY_LIST );

        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(authentication); //current request cycle, for other particular filter is responsible
        
        shoppingCart.getShoppingContext().getSecurityContext().setAuthentication(authentication);
        shoppingCart.getShoppingContext().setCustomerName((String) parameters.get(NAME));
        setModifiedDate(shoppingCart);

    }

    /** {@inheritDoc} */
    public String getCmdKey() {
        return CMD_KEY;
    }

    /**
     *
     * @param applicationContext application context
     * @param parameters page parameters
     */
    public LoginCommandImpl(final ApplicationContext applicationContext, final Map parameters) {
        super();
        this.parameters = parameters;
    }


}

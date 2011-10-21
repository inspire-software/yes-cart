package org.yes.cart.web.application;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.LoginCommandImpl;
import org.yes.cart.shoppingcart.impl.LogoutCommandImpl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 9:49 PM
 */
public class StorefrontWebSession extends AuthenticatedWebSession {

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;


    /**
     * Construct.
     *
     * @param request The current request object
     */
    public StorefrontWebSession(Request request) {
        super(request);
        ((StorefrontApplication) getApplication()).getSpringComponentInjector().inject(this);   // allow to use @SpringBean
    }

    /**
     * {@inheritDoc}
     */
    public boolean authenticate(final String username, final String password) {
        if (customerService.isCustomerExists(username) &&
                customerService.isPasswordValid(username, password)) {
            executeLoginCommand(
                    ApplicationDirector.getShoppingCart(),
                    customerService.findCustomer(username));
            return true;
        }
        new LogoutCommandImpl(null, null).execute(ApplicationDirector.getShoppingCart());
        return false;
    }


    /**
     * Execute login command.
     *
     * @param shoppingCart shopping cart
     * @param customer     customer.
     */
    protected void executeLoginCommand(final ShoppingCart shoppingCart, final Customer customer) {
        shoppingCartCommandFactory.create(
                new HashMap<String, String>() {{
                    put(LoginCommandImpl.EMAIL, customer.getEmail());
                    put(LoginCommandImpl.NAME, customer.getFirstname() + " " + customer.getLastname());
                    put(LoginCommandImpl.CMD_KEY, LoginCommandImpl.CMD_KEY);
                }}
        ).execute(shoppingCart);
    }


    /**
     * {@inheritDoc}
     */
    public Roles getRoles() {
        if (isSignedIn()) {
            return new Roles(Roles.USER);
        }
        return null;
    }

}

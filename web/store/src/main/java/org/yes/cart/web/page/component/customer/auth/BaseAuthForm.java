package org.yes.cart.web.page.component.customer.auth;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.LoginCommandImpl;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 3:29 PM
 */
public class BaseAuthForm extends Form {

    protected static final int MIN_LEN = 6;
    protected static final int MAX_LEN = 256;
    private static final Logger LOG = LoggerFactory.getLogger(BaseAuthForm.class);


    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;

    @SpringBean(name = ServiceSpringKeys.ATTRIBUTE_SERVICE)
    private AttributeService attributeService;

    //@SpringBean(name = WebParametersKeys.SESSION_OBJECT_NAME)
    //private RequestRuntimeContainer requestRuntimeContainer;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;


    public BaseAuthForm(String id) {
        super(id);
    }


    /**
     * Check is customer already registered.
     *
     * @param customerEmail email to check
     * @return true in case if email unique.
     */
    protected boolean isCustomerExists(final String customerEmail) {
        return customerService.isCustomerExists(customerEmail);
    }

    /**
     * Check is provided password for customer valid.
     *
     * @param customerEmail email to check
     * @param password      password
     * @return true in case if email unique.
     */
    protected boolean isPasswordValid(final String customerEmail, final String password) {
        return customerService.isPasswordValid(customerEmail, password);
    }


    /**
     * Get {@link CustomerService} service.
     *
     * @return {@link CustomerService} service
     */
    protected CustomerService getCustomerService() {
        return customerService;
    }

    /**
     * Get attribute service.
     *
     * @return {@link AttributeService}
     */
    protected AttributeService getAttributeService() {
        return attributeService;
    }



    /**
     * Sign in user if possible.
     *
     * @param username The username
     * @param password The password
     * @return True if signin was successful
     */
    protected boolean signIn(final String username, final String password) {
        return AuthenticatedWebSession.get().signIn(username, password);
    }

}
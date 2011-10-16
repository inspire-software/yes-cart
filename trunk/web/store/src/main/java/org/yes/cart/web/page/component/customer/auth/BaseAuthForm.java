package org.yes.cart.web.page.component.customer.auth;

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
     * @param customerEmail email to check
     * @return true in case if email unique.
     */
    protected boolean isCustomerExists(final String customerEmail) {
        return customerService.isCustomerExists(customerEmail);
    }

    /**
     * Check is provided password for customer valid.
     * @param customerEmail email to check
     * @param password password
     * @return true in case if email unique.
     */
    protected boolean isPasswordValid(final String customerEmail, final String password) {
        return customerService.isPasswordValid(customerEmail, password);
    }


    /**
     * Get {@link CustomerService} service.
     * @return {@link CustomerService} service
     */
    protected CustomerService getCustomerService() {
        return customerService;
    }

    /**
     * Get attribute service.
     * @return {@link AttributeService}
     */
    protected AttributeService getAttributeService() {
        return attributeService;
    }

    /**
     * Execute login command.
     * @param shoppingCart shopping cart
     * @param email customer email.
     */
    protected void executeLoginCommand(final ShoppingCart shoppingCart, final String email) {
        final Customer customer = getCustomerService().findCustomer(email);
        if (customer == null) {
            LOG.error(MessageFormat.format("Can not get customer for given {0} email", email));
        } else {
            executeLoginCommand(shoppingCart, customer);
        }
    }

    /**
     * Execute login command.
     * @param shoppingCart shopping cart
     * @param customer customer.
     */
    protected void executeLoginCommand(final ShoppingCart shoppingCart, final Customer customer) {
        final Map<String, String> map = new HashMap<String, String>();
        map.put(LoginCommandImpl.EMAIL, customer.getEmail());
        map.put(LoginCommandImpl.NAME, customer.getFirstname() + " " + customer.getLastname());
        map.put(LoginCommandImpl.CMD_KEY, LoginCommandImpl.CMD_KEY);
        final ShoppingCartCommand loginCommand = shoppingCartCommandFactory.create(map);
        loginCommand.execute(shoppingCart);
    }

}
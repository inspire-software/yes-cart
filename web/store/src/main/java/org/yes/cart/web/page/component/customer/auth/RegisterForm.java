package org.yes.cart.web.page.component.customer.auth;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 3:33 PM
 */
public class RegisterForm extends BaseAuthForm {

    private final long serialVersionUid =   20111016L;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String EMAIL_INPUT = "email";
    private static final String FIRSTNAME_INPUT = "firstname";
    private static final String LASTNAME_INPUT = "lastname";
    private static final String PHONE_INPUT = "phone";
    private static final String REGISTER_LINK = "registerLink";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    /**
     * Construct form.
     *
     * @param id                       form id.
     * @param successfulPage           page to go in case of successful
     * @param successfulPageParameters parapemters for successful page
     */
    public RegisterForm(final String id,
                        final Class<? extends Page> successfulPage,
                        final PageParameters successfulPageParameters) {

        super(id);

        final TextField<String> emailField;
        final TextField<String> firstNameField;
        final TextField<String> lastNameField;
        final TextField<String> phoneField;


        emailField = new TextField<String>(EMAIL_INPUT, new Model<String>(StringUtils.EMPTY));
        emailField
                .setRequired(true)
                .add(StringValidator.lengthBetween(MIN_LEN, MAX_LEN))
                .add(EmailAddressValidator.getInstance());

        firstNameField = new TextField<String>(FIRSTNAME_INPUT, new Model<String>(StringUtils.EMPTY));
        firstNameField.setRequired(true);

        lastNameField = new TextField<String>(LASTNAME_INPUT, new Model<String>(StringUtils.EMPTY));
        lastNameField.setRequired(true);

        phoneField = new TextField<String>(PHONE_INPUT, new Model<String>(StringUtils.EMPTY));
        phoneField.setRequired(true)
                .add(StringValidator.lengthBetween(6, 13)); //NBC

        add(emailField);
        add(firstNameField);
        add(lastNameField);
        add(phoneField);

        add(
                new SubmitLink(REGISTER_LINK) {

                    /**
                     * Create new customer.
                     * @return new customer.
                     */
                    private Customer createNewCustomer() {

                        Customer customer = getCustomerService().getGenericDao().getEntityFactory().getByIface(Customer.class);
                        customer.setEmail(emailField.getInput());
                        customer.setFirstname(firstNameField.getInput());
                        customer.setLastname(lastNameField.getInput());


                        final AttrValueCustomer attrValueCustomer = getCustomerService().getGenericDao().getEntityFactory().getByIface(AttrValueCustomer.class);
                        attrValueCustomer.setCustomer(customer);
                        attrValueCustomer.setVal(phoneField.getInput());
                        attrValueCustomer.setAttribute(getAttributeService().findByAttributeCode(
                                AttributeNamesKeys.CUSTOMER_PHONE));

                        customer.getAttribute().add(attrValueCustomer);

                        customer = getCustomerService().create(customer, ApplicationDirector.getCurrentShop());

                        return customer;
                    }


                    @Override
                    public void onSubmit() {

                        if (!getCustomerService().isCustomerExists(emailField.getInput())) {

                            final ShoppingCart shoppingCart = ApplicationDirector.getShoppingCart();

                            final Customer customer = createNewCustomer();

                            executeLoginCommand(shoppingCart, customer);

                            setResponsePage(successfulPage, successfulPageParameters);

                        } else {
                            error(
                                    getLocalizer().getString("customerExists", this)
                            );
                        }
                    }
                }
        );

    }

}

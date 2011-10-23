package org.yes.cart.web.page.component.customer.password;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/23/11
 * Time: 8:17 PM
 */
public class PasswordPanel extends BaseComponent {

    private static final String PASSWORD_SENT = "newPasswordSent";

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String PASSWORD_FORM = "passwordForm";
    private final static String SAVE_LINK = "saveLink";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;


    /**
     * Construct form to reset password.
     *
     * @param s             panel id
     * @param customerModel customer model
     */
    public PasswordPanel(final String s, final IModel<Customer> customerModel) {

        super(s, customerModel);

        add(
                new Form<Customer>(PASSWORD_FORM, customerModel) {

                    @Override
                    protected void onSubmit() {
                        customerService.resetPassword(getModelObject(), ApplicationDirector.getCurrentShop());
                        info(getLocalizer().getString(PASSWORD_SENT, this));
                        super.onSubmit();
                    }

                }
                        .add(
                                new SubmitLink(SAVE_LINK)
                        )
        );

    }

}

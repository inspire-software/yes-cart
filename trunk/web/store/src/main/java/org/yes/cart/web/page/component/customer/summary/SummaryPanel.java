package org.yes.cart.web.page.component.customer.summary;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.web.page.component.BaseComponent;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/23/11
 * Time: 9:20 PM
 */
public class SummaryPanel extends BaseComponent {
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String SUMMARY_FORM = "summaryForm";

    private static final String EMAIL_LABEL = "email";
    private static final String FIRSTNAME_INPUT = "firstname";
    private static final String LASTNAME_INPUT = "lastname";
    private final static String SAVE_LINK = "saveLink";

// ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;


    /**
     * Construct customer summary panel.
     *
     * @param id            panel id
     * @param customerModel model of customer
     */
    public SummaryPanel(final String id, final IModel<Customer> customerModel) {

        super(id, customerModel);

        final Customer customer = customerModel.getObject();

        add(
                new Form<Customer>(SUMMARY_FORM, customerModel) {

                    @Override
                    protected void onSubmit() {
                        customerService.update(getModelObject());
                        info("Customer profile updated"); //todo localize me
                        super.onSubmit();
                    }
                }.add(
                        new Label(EMAIL_LABEL, customer.getEmail())
                ).add(
                        new TextField(FIRSTNAME_INPUT, new PropertyModel(customer, "firstname")).setRequired(true)
                ).add(
                        new TextField(LASTNAME_INPUT, new PropertyModel(customer, "lastname")).setRequired(true)
                ).add(
                        new SubmitLink(SAVE_LINK)
                )
        );
    }

}

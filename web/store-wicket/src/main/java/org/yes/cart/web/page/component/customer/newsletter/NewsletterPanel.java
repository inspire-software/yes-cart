package org.yes.cart.web.page.component.customer.newsletter;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.customer.auth.BaseAuthForm;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.util.HashMap;

/**
 * User: denispavlov
 * Date: 03/11/2015
 * Time: 13:47
 */
public class NewsletterPanel extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String EMAIL_INPUT = "email";
    private static final String SIGNUP_BUTTON = "signUpBtn";
    private static final String SIGNUP_FORM = "signUpForm";
    private static final String CONTENT = "newsletterContent";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    public NewsletterPanel(final String id) {
        super(id);
        add(new SingUpForm(SIGNUP_FORM));
    }

    @Override
    protected void onBeforeRender() {

        final PageParameters params = getPage().getPageParameters();
        final boolean signupok = params.get("signupok").toBoolean();

        if (signupok) {
            info(getLocalizer().getString("newsletterEmailSent", this));
        }

        final String lang = getLocale().getLanguage();

        String loginformInfo = getContentInclude(getCurrentShopId(), "newsletter_newsletterform_content_include", lang);
        get(SIGNUP_FORM).get(CONTENT).replaceWith(new Label(CONTENT, loginformInfo).setEscapeModelStrings(false));

        super.onBeforeRender();

    }



    public final class SingUpForm extends StatelessForm {

        private String email;


        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }


        public SingUpForm(final String id) {
            super(id);

            setModel(new CompoundPropertyModel<SingUpForm>(SingUpForm.this));

            final TextField<String> emailInput = (TextField<String>) new TextField<String>(EMAIL_INPUT)
                    .setRequired(true)
                    .add(StringValidator.lengthBetween(BaseAuthForm.MIN_LEN, BaseAuthForm.MAX_LEN))
                    .add(EmailAddressValidator.getInstance());

            add(
                    emailInput
            );

            add(
                    new Button(SIGNUP_BUTTON) {

                        @Override
                        public void onSubmit() {

                            if (!SingUpForm.this.hasError()) {
                                customerServiceFacade.registerNewsletter(getCurrentShop(), getEmail(), new HashMap<String, Object>());

                                final PageParameters params = new PageParameters(getPage().getPageParameters());
                                params.add("signupok", Boolean.TRUE);

                                setResponsePage(getPage().getPageClass(), params);
                            }

                        }

                    }
            );

            add(new Label(CONTENT, ""));

        }
    }


    private String getContentInclude(long shopId, String contentUri, String lang) {
        String content = contentServiceFacade.getContentBody(contentUri, shopId, lang);
        if (content == null) {
            content = "";
        }
        return content;
    }


}

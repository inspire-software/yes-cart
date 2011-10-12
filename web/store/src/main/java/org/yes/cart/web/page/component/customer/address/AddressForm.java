package org.yes.cart.web.page.component.customer.address;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.State;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.CountryService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.StateService;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.List;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 12/10/11
 * Time: 18:01
 */
public class AddressForm  extends Form<Address> {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String FIRSTNAME = "firstName";
    private final static String LASTNAME = "lastName";
    private final static String LINE1 = "line1";
    private final static String LINE2 = "line2";
    private final static String CITY = "city";
    private final static String COUNTRY = "country";
    private final static String STATE = "state";
    private final static String POSTCODE = "postcode";
    private final static String PHONELIST = "phoneList";
    private final static String ADD_ADDRESS = "addAddress";
    private final static String CANCELL_LINK = "cancel";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    private final static int SMALL_LENGTH = 16;
    private final static int MEDIUM_LENGTH = 128;
    private final static int LARGE_LENGTH = 256;

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_SERVICE)
    private CustomerService customerService;

    @SpringBean(name = ServiceSpringKeys.ADDRESS_SERVICE)
    private AddressService addressService;

    //@SpringBean(name = WebParametersKeys.SESSION_OBJECT_NAME)
    //private RequestRuntimeContainer requestRuntimeContainer;

    @SpringBean(name = ServiceSpringKeys.COUNTRY_SERVICE)
    private CountryService countryService;

    @SpringBean(name = ServiceSpringKeys.STATE_SERVICE)
    private StateService stateService;


    private final Class<? extends Page> succsessPage;
    private final PageParameters succsessPageParameters;

    /**
     * Create address form.
     *
     * @param s             form id
     * @param addressIModel address model.
     * @param addressType   address type
     * @param succsessPage succsess page class
     * @param succsessPageParameters succsess page parameters
     * @param cancelPage optional cancel page class
     * @param cancelPageParameters optional  cancel page parameters
     */
    public AddressForm(final String s,
                       final IModel<Address> addressIModel,
                       final String addressType,
                       final Class<? extends Page> succsessPage,
                       final PageParameters succsessPageParameters,
                       final Class<? extends Page> cancelPage,
                       final PageParameters cancelPageParameters) {

        super(s, addressIModel);

        this.succsessPage = succsessPage;
        this.succsessPageParameters = succsessPageParameters;


        /*final Address address = addressIModel.getObject();

        final Customer customer = customerService.findCustomer(
               null //TODO getRequestRuntimeContainer().getShoppingCart().getCustomerEmail()
        );


        preprocessAddress(address, addressType, customer);



        add(new TextField<String>(FIRSTNAME, new PropertyModel<String>(address, "firstname")).setRequired(true).add(new StringValidator.MaximumLengthValidator(MEDIUM_LENGTH)));
        add(new TextField<String>(LASTNAME, new PropertyModel<String>(address, "lastname")).setRequired(true).add(new StringValidator.MaximumLengthValidator(MEDIUM_LENGTH)));
        add(new TextField<String>(LINE1, new PropertyModel<String>(address, "addrline1")).setRequired(true).add(new StringValidator.MaximumLengthValidator(LARGE_LENGTH)));
        add(new TextField<String>(LINE2, new PropertyModel<String>(address, "addrline2")).add(new StringValidator.MaximumLengthValidator(LARGE_LENGTH)));
        add(new TextField<String>(CITY, new PropertyModel<String>(address, "city")).setRequired(true).add(new StringValidator.MaximumLengthValidator(MEDIUM_LENGTH)));
        add(new TextField<String>(POSTCODE, new PropertyModel<String>(address, "postcode")).setRequired(true).add(new StringValidator.MaximumLengthValidator(SMALL_LENGTH)));
        add(new TextField<String>(PHONELIST, new PropertyModel<String>(address, "phoneList")).setRequired(true).add(new StringValidator.MaximumLengthValidator(LARGE_LENGTH)));


        final List<State> stateList = getStateList(address.getCountryCode());
        final AbstractChoice<State, State> stateDropDownChoice = new DropDownChoice<State>(
                STATE,
                new StateModel(new PropertyModel(address, "countryCode"), stateList),
                stateList).setChoiceRenderer(new StateRenderer());
        stateDropDownChoice.setRequired(!stateList.isEmpty());
        add(
                stateDropDownChoice
        );


        final List<Country> countryList = countryService.findAll();
        add(
                new DropDownChoice<Country>(
                        COUNTRY,
                        new CountryModel(new PropertyModel(address, "countryCode"), countryList),
                        countryList) {

                    @Override
                    protected void onSelectionChanged(final Country country) {
                        super.onSelectionChanged(country);
                        stateDropDownChoice.setChoices(getStateList(country.getCountryCode()));
                        stateDropDownChoice.setRequired(!stateDropDownChoice.getChoices().isEmpty());
                        address.setStateCode(StringUtils.EMPTY);
                    }

                    @Override
                    protected boolean wantOnSelectionChangedNotifications() {
                        return true;
                    }

                }.setChoiceRenderer(new CountryRenderer()).setRequired(true)
        );

        add(
                new SubmitLink(ADD_ADDRESS)
        );

        add(
                new SubmitLink(CANCELL_LINK) {

                    @Override
                    public void onSubmit() {
                        setResponsePage(cancelPage, cancelPageParameters);
                    }

                }.setDefaultFormProcessing(false).setVisible(cancelPage != null)
        );     */



    }


}

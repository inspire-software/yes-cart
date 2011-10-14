package org.yes.cart.web.page.component.customer.address;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.web.page.component.BaseComponent;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 15/10/11
 * Time: 14:34
 */
public class AddressShowView  extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String LINE1 = "addrLine1";
    private final static String LINE2 = "addrLine2";
    private final static String LINE3 = "addrLine3";
    private final static String LINE4 = "addrLine4";
    private final static String LINE5 = "addrLine5";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //



    /**
     * Construct panel to show address.
     * @param id component id
     * @param address address to show
     */
    public AddressShowView(final String id, final Address address) {
        super(id);

        add(new Label(LINE1, address.getFirstname() +  ", " + address.getLastname()));
        add(new Label(LINE2, address.getAddrline1()));
        if (StringUtils.isBlank(address.getAddrline2())) {
            add(new Label(LINE3, StringUtils.EMPTY));
        } else {
            add(new Label(LINE3, address.getAddrline1()));
        }
        add(new Label(LINE4, address.getCity() +  ", " + address.getStateCode()));
        add(new Label(LINE5, address.getPostcode() +  ", " + address.getCountryCode()));


    }
}

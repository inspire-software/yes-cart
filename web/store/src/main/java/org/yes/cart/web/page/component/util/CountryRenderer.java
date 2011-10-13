package org.yes.cart.web.page.component.util;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.yes.cart.domain.entity.Country;

/**
 * User: Igor Azarny iazarnmy@yahoo.com
 * Date: 15-Oct-2011
 * Time: 11:01:06 AM
 */

public class CountryRenderer extends ChoiceRenderer<Country> {

    /** {@inheritDoc} */
    @Override
    public Object getDisplayValue(final Country country) {
        return country.getName();
    }

    /** {@inheritDoc} */
    @Override
    public String getIdValue(final Country country, final int i) {
        return country.getCountryCode();
    }

}

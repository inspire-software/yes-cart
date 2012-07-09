package org.yes.cart.web.page.component.util;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.yes.cart.domain.entity.CarrierSla;

/**
 * User: Igor Azarny iazarnmy@yahoo.com
 * Date: 15-Oct-2011
 * Time: 11:01:06 AM
 */

public class CarrierSlaRenderer extends ChoiceRenderer<CarrierSla> {
    
    /** {@inheritDoc} */
    @Override
    public Object getDisplayValue(final CarrierSla carrierSla) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(carrierSla.getName());
        stringBuilder.append('(');
        stringBuilder.append(carrierSla.getMaxDays());
        stringBuilder.append(") ");
        stringBuilder.append(carrierSla.getPrice()); //TODO calculate shipping price
        stringBuilder.append(' ');
        stringBuilder.append(carrierSla.getCurrency());        
        return stringBuilder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public String getIdValue(final CarrierSla carrierSla, final int i) {
        return String.valueOf(carrierSla.getCarrierslaId());
    }
    
    
}

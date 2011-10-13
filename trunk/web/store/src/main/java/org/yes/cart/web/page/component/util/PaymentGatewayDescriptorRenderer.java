package org.yes.cart.web.page.component.util;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;

/**
 * User: Igor Azarny iazarnmy@yahoo.com
 * Date: 15-Oct-2011
 * Time: 11:01:06 AM
 */
public class PaymentGatewayDescriptorRenderer extends ChoiceRenderer<PaymentGatewayDescriptor> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDisplayValue(final PaymentGatewayDescriptor carrier) {
        return carrier.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdValue(final PaymentGatewayDescriptor carrier, final int i) {
        return String.valueOf(carrier.getLabel());
    }

}

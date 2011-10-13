package org.yes.cart.web.page.component.util;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;

import java.util.List;

/**
 * User: Gordon.Freeman (A) gordon-minus-freeman@ukr.net
 * Date: 2/27/11
 * Time: 7:36 PM
 */
public class PaymentGatewayDescriptorModel implements IModel<PaymentGatewayDescriptor> {

    private PaymentGatewayDescriptor paymentGatewayDescriptor;

    private final PropertyModel<String> propertyModel;

    /**
     * Construct payment gateway descriptor model.
     * @param propertyModel property model
     * @param pgList list of available payment gateway descriptors
     */
    public PaymentGatewayDescriptorModel(final PropertyModel<String> propertyModel, final List<PaymentGatewayDescriptor> pgList) {
        this.propertyModel = propertyModel;
        final String singleSelectedKey = (String) propertyModel.getObject();
        if (StringUtils.isNotBlank(singleSelectedKey)) {
            for (PaymentGatewayDescriptor pgd : pgList) {
                if (singleSelectedKey.equals(pgd.getLabel())) {
                    paymentGatewayDescriptor = pgd;
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public PaymentGatewayDescriptor getObject() {
        return paymentGatewayDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public void setObject(final PaymentGatewayDescriptor paymentGatewayDescriptor) {
        this.paymentGatewayDescriptor = paymentGatewayDescriptor;
        if (paymentGatewayDescriptor == null) {
            propertyModel.setObject(null);
        } else {
            propertyModel.setObject(paymentGatewayDescriptor.getLabel());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void detach() {
        if (paymentGatewayDescriptor instanceof IDetachable) {
            ((IDetachable) paymentGatewayDescriptor).detach();
        }
    }
}

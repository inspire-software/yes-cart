/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.page.component.util;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;

import java.util.List;

/**
 * User: Gordon.Freeman (A) gordon-minus-freeman@ukr.net
 * Date: 2/27/11
 * Time: 7:36 PM
 */
public class PaymentGatewayDescriptorModel implements IModel<Pair<PaymentGatewayDescriptor, String>> {

    private Pair<PaymentGatewayDescriptor, String> paymentGatewayDescriptor;

    private final PropertyModel<String> propertyModel;

    /**
     * Construct payment gateway descriptor model.
     * @param propertyModel property model
     * @param pgList list of available payment gateway descriptors
     */
    public PaymentGatewayDescriptorModel(final PropertyModel<String> propertyModel, final List<Pair<PaymentGatewayDescriptor, String>> pgList) {
        this.propertyModel = propertyModel;
        final String singleSelectedKey = (String) propertyModel.getObject();
        if (StringUtils.isNotBlank(singleSelectedKey)) {
            for (Pair<PaymentGatewayDescriptor, String> pgd : pgList) {
                if (singleSelectedKey.equals(pgd.getFirst().getLabel())) {
                    paymentGatewayDescriptor = pgd;
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Pair<PaymentGatewayDescriptor, String> getObject() {
        return paymentGatewayDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public void setObject(final Pair<PaymentGatewayDescriptor, String> paymentGatewayDescriptor) {
        this.paymentGatewayDescriptor = paymentGatewayDescriptor;
        if (paymentGatewayDescriptor == null) {
            propertyModel.setObject(null);
        } else {
            propertyModel.setObject(paymentGatewayDescriptor.getFirst().getLabel());
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

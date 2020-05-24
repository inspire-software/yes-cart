/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.PaymentGatewayParameterType;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayParameterEntity;
import org.yes.cart.payment.service.PaymentGatewayParameterService;
import org.yes.cart.service.async.JobStatusListener;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class PaymentGatewayParameterXmlEntityHandler extends AbstractXmlEntityHandler<PaymentGatewayParameterType, PaymentGatewayParameter> implements XmlEntityImportHandler<PaymentGatewayParameterType, PaymentGatewayParameter> {

    private PaymentGatewayParameterService paymentGatewayParameterService;

    public PaymentGatewayParameterXmlEntityHandler() {
        super("payment-gateway-parameter");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final PaymentGatewayParameter param) {
        this.paymentGatewayParameterService.delete(param);
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final PaymentGatewayParameter domain, final PaymentGatewayParameterType xmlType, final EntityImportModeType mode) {

        domain.setValue(xmlType.getValue());
        domain.setBusinesstype(xmlType.getBusinessType());
        domain.setSecure(xmlType.isSecure());

        if (domain.getPaymentGatewayParameterId() == 0L) {
            this.paymentGatewayParameterService.create(domain);
        } else {
            this.paymentGatewayParameterService.update(domain);
        }
        
    }

    @Override
    protected PaymentGatewayParameter getOrCreate(final JobStatusListener statusListener, final PaymentGatewayParameterType xmlType) {
        PaymentGatewayParameter payment = this.paymentGatewayParameterService.findSingleByCriteria(" where e.pgLabel = ?1 and e.name = ?2", xmlType.getPaymentGateway(), xmlType.getCode());
        if (payment != null) {
            return payment;
        }
        payment = new PaymentGatewayParameterEntity();
        payment.setGuid(xmlType.getGuid());
        payment.setPgLabel(xmlType.getPaymentGateway());
        payment.setName(xmlType.getCode());

        return payment;
    }

    @Override
    protected EntityImportModeType determineImportMode(final PaymentGatewayParameterType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final PaymentGatewayParameter domain) {
        return domain.getPaymentGatewayParameterId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param paymentGatewayParameterService payment service
     */
    public void setPaymentGatewayParameterService(final PaymentGatewayParameterService paymentGatewayParameterService) {
        this.paymentGatewayParameterService = paymentGatewayParameterService;
    }
}

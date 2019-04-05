/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.PaymentGatewayCallbackRequestParameterType;
import org.yes.cart.bulkimport.xml.internal.PaymentGatewayCallbackType;
import org.yes.cart.payment.persistence.entity.PaymentGatewayCallback;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayCallbackEntity;
import org.yes.cart.payment.service.PaymentModuleGenericService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class PaymentGatewayCallbackXmlEntityHandler extends AbstractXmlEntityHandler<PaymentGatewayCallbackType, PaymentGatewayCallback> implements XmlEntityImportHandler<PaymentGatewayCallbackType, PaymentGatewayCallback> {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayCallbackXmlEntityHandler.class);

    private PaymentModuleGenericService<PaymentGatewayCallback> paymentGatewayCallbackService;

    public PaymentGatewayCallbackXmlEntityHandler() {
        super("payment-gateway-callback");
    }

    @Override
    protected void delete(final PaymentGatewayCallback callback) {
        this.paymentGatewayCallbackService.delete(callback);
    }

    @Override
    protected void saveOrUpdate(final PaymentGatewayCallback domain, final PaymentGatewayCallbackType xmlType, final EntityImportModeType mode) {

        if (domain.getPaymentGatewayCallbackId() == 0L) {
            this.paymentGatewayCallbackService.create(domain);
        } else {
            LOG.warn("Payment callback with GUID {} already exists and will not be updated", xmlType.getGuid());
        }
        
    }

    @Override
    protected PaymentGatewayCallback getOrCreate(final PaymentGatewayCallbackType xmlType) {
        PaymentGatewayCallback payment = this.paymentGatewayCallbackService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (payment != null) {
            return payment;
        }
        payment = new PaymentGatewayCallbackEntity();
        payment.setGuid(xmlType.getGuid());
        payment.setShopCode(xmlType.getShopCode());

        payment.setLabel(xmlType.getPaymentGateway());
        payment.setRequestDump(xmlType.getRequestDump());
        if (xmlType.getRequestParameters() != null) {
            final Map<String, String[]> params = new LinkedHashMap<>();
            for (final PaymentGatewayCallbackRequestParameterType param : xmlType.getRequestParameters().getRequestParameter()) {
                params.put(param.getName(), param.getValue().toArray(new String[param.getValue().size()]));
            }
            payment.setParameterMap(params);
        }

        return payment;
    }

    @Override
    protected EntityImportModeType determineImportMode(final PaymentGatewayCallbackType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final PaymentGatewayCallback domain) {
        return domain.getPaymentGatewayCallbackId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param paymentGatewayCallbackService payment service
     */
    public void setPaymentGatewayCallbackService(final PaymentModuleGenericService<PaymentGatewayCallback> paymentGatewayCallbackService) {
        this.paymentGatewayCallbackService = paymentGatewayCallbackService;
    }
}

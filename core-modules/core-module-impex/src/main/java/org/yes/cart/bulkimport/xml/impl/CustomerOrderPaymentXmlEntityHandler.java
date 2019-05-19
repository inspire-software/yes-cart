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

import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.CustomerOrderPaymentType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.entity.impl.CustomerOrderPaymentEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.utils.DateUtils;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class CustomerOrderPaymentXmlEntityHandler extends AbstractXmlEntityHandler<CustomerOrderPaymentType, CustomerOrderPayment> implements XmlEntityImportHandler<CustomerOrderPaymentType, CustomerOrderPayment> {

    private CustomerOrderPaymentService customerOrderPaymentService;

    public CustomerOrderPaymentXmlEntityHandler() {
        super("customer-order-payment");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final CustomerOrderPayment payment) {
        this.customerOrderPaymentService.delete(payment);
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final CustomerOrderPayment domain, final CustomerOrderPaymentType xmlType, final EntityImportModeType mode) {

        if (domain.getCustomerOrderPaymentId() == 0L) {
            this.customerOrderPaymentService.create(domain);
        } else {
            statusListener.notifyWarning("Payment with GUID {} already exists and will not be updated", xmlType.getGuid());
        }
        
    }

    @Override
    protected CustomerOrderPayment getOrCreate(final JobStatusListener statusListener, final CustomerOrderPaymentType xmlType) {
        CustomerOrderPayment payment = this.customerOrderPaymentService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (payment != null) {
            return payment;
        }
        payment = new CustomerOrderPaymentEntity();
        payment.setGuid(xmlType.getGuid());
        payment.setShopCode(xmlType.getShopCode());

        payment.setCardType(xmlType.getCard().getType());
        payment.setCardHolderName(xmlType.getCard().getHolderName());
        payment.setCardExpireMonth(xmlType.getCard().getExpireMonth());
        payment.setCardExpireYear(xmlType.getCard().getExpireYear());

        payment.setOrderDate(DateUtils.ldtParseSDT(xmlType.getOrderDate()));

        payment.setOrderCurrency(xmlType.getOrderAmount().getCurrency());
        payment.setPaymentAmount(xmlType.getOrderAmount().getAmount());
        payment.setTaxAmount(xmlType.getOrderAmount().getTax());

        payment.setOrderNumber(xmlType.getOrderNumber());
        payment.setOrderShipment(xmlType.getOrderShipment());
        payment.setTransactionGatewayLabel(xmlType.getPaymentGateway());
        payment.setShopperIpAddress(xmlType.getCustomerIp());

        payment.setTransactionReferenceId(xmlType.getTransaction().getReference());
        payment.setTransactionRequestToken(xmlType.getTransaction().getRequestToken());
        payment.setTransactionAuthorizationCode(xmlType.getTransaction().getAuthCode());
        payment.setTransactionOperation(xmlType.getTransaction().getOperation());
        payment.setTransactionOperationResultCode(xmlType.getTransaction().getOperationResultCode());
        payment.setTransactionOperationResultMessage(xmlType.getTransaction().getOperationResultMessage());

        payment.setPaymentProcessorResult(xmlType.getTransaction().getPaymentProcessorResult());
        payment.setPaymentProcessorBatchSettlement(xmlType.getTransaction().isBatchSettlement());

        return payment;
    }

    @Override
    protected EntityImportModeType determineImportMode(final CustomerOrderPaymentType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final CustomerOrderPayment domain) {
        return domain.getCustomerOrderPaymentId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param customerOrderPaymentService payment service
     */
    public void setCustomerOrderPaymentService(final CustomerOrderPaymentService customerOrderPaymentService) {
        this.customerOrderPaymentService = customerOrderPaymentService;
    }
}

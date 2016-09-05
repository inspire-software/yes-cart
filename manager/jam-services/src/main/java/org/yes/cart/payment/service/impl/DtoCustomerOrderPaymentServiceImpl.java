package org.yes.cart.payment.service.impl;

import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;
import org.yes.cart.payment.service.DtoCustomerOrderPaymentService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 05/09/2016
 * Time: 18:33
 */
public class DtoCustomerOrderPaymentServiceImpl extends PaymentModuleGenericServiceImpl<CustomerOrderPayment> implements DtoCustomerOrderPaymentService {


    public DtoCustomerOrderPaymentServiceImpl(final PaymentModuleGenericDAO<CustomerOrderPayment, Long> genericDao) {
        super(genericDao);
    }

    /** {@inheritDoc} */
    @Override
    public List<CustomerOrderPayment> findBy(final String filter, final int page, final int pageSize) {
        return null;
    }
}

package org.yes.cart.payment.service;

import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;

import java.util.List;

/**
 * User: denispavlov
 * Date: 05/09/2016
 * Time: 18:32
 */
public interface DtoCustomerOrderPaymentService {

    /**
     * Find payments.
     *
     * @param filter filter
     * @param page page start
     * @param pageSize max results
     *
     * @return payments
     */
    List<CustomerOrderPayment> findBy(String filter, int page, int pageSize);

}

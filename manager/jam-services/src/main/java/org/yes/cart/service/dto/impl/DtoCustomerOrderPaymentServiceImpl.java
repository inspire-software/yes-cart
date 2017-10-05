package org.yes.cart.service.dto.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;
import org.yes.cart.payment.service.DtoCustomerOrderPaymentService;
import org.yes.cart.payment.service.impl.PaymentModuleGenericServiceImpl;
import org.yes.cart.utils.HQLUtils;

import java.util.Arrays;
import java.util.Date;
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


    private final static char[] ORDER_OR_CUSTOMER_OR_DETAILS = new char[] { '#', '?', '@' };
    private final static char[] PAYMENT_STATUS = new char[] { '~', '-', '+', '*' };
    static {
        Arrays.sort(ORDER_OR_CUSTOMER_OR_DETAILS);
        Arrays.sort(PAYMENT_STATUS);
    }

    @Override
    public List<CustomerOrderPayment> findBy(final String filter, final List<String> operations, final List<String> statuses, final int page, final int pageSize) {

        final String orderBy = " order by e.createdTimestamp desc, e.orderNumber";

        if (StringUtils.isNotBlank(filter)) {

            final Pair<String, String> orderNumberOrCustomerOrDetails =
                    ComplexSearchUtils.checkSpecialSearch(filter, ORDER_OR_CUSTOMER_OR_DETAILS);
            final Pair<Date, Date> dateSearch = orderNumberOrCustomerOrDetails == null ?
                    ComplexSearchUtils.checkDateRangeSearch(filter) : null;

            if (orderNumberOrCustomerOrDetails != null) {

                if ("#".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // order/transaction number search
                    final String orderNumber = orderNumberOrCustomerOrDetails.getSecond();

                    return getGenericDao().findRangeByCriteria(
                            " where (e.orderNumber like ?1 or e.orderShipment like ?1 or lower(e.transactionReferenceId) like ?1 or lower(e.transactionRequestToken) like ?1 or lower(e.transactionAuthorizationCode) like ?1) and (?2 = 0 or e.paymentProcessorResult in (?3))  and (?4 = 0 or e.transactionOperation in (?5)) " + orderBy,
                            page * pageSize, pageSize,
                            HQLUtils.criteriaIlikeAnywhere(orderNumber),
                            HQLUtils.criteriaInTest(statuses),
                            HQLUtils.criteriaIn(statuses),
                            HQLUtils.criteriaInTest(operations),
                            HQLUtils.criteriaIn(operations)
                    );
                } else if ("?".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // customer search
                    final String customer = orderNumberOrCustomerOrDetails.getSecond();

                    return getGenericDao().findRangeByCriteria(
                            " where (lower(e.cardNumber) like ?1 or lower(e.cardHolderName) like ?1 or lower(e.shopperIpAddress) like ?1) and (?2 = 0 or e.paymentProcessorResult in (?3))  and (?4 = 0 or e.transactionOperation in (?5)) " + orderBy,
                            page * pageSize, pageSize,
                            HQLUtils.criteriaIlikeAnywhere(customer),
                            HQLUtils.criteriaInTest(statuses),
                            HQLUtils.criteriaIn(statuses),
                            HQLUtils.criteriaInTest(operations),
                            HQLUtils.criteriaIn(operations)
                    );
                } else if ("@".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // details search
                    final String details = orderNumberOrCustomerOrDetails.getSecond();

                    return getGenericDao().findRangeByCriteria(
                            " where (lower(e.transactionOperation) like ?1 or lower(e.transactionOperationResultCode) like ?1 or lower(e.transactionOperation) like ?1) and (?2 = 0 or e.paymentProcessorResult in (?3))  and (?4 = 0 or e.transactionOperation in (?5)) " + orderBy,
                            page * pageSize, pageSize,
                            HQLUtils.criteriaIlikeAnywhere(details),
                            HQLUtils.criteriaInTest(statuses),
                            HQLUtils.criteriaIn(statuses),
                            HQLUtils.criteriaInTest(operations),
                            HQLUtils.criteriaIn(operations)
                    );
                }

            } else if (dateSearch != null) {

                final Date from = dateSearch.getFirst();
                final Date to = dateSearch.getSecond();

                // time search
                return getGenericDao().findRangeByCriteria(
                        " where (?1 is null or e.createdTimestamp >= ?1) and (?2 is null or e.createdTimestamp <= ?2) and (?3 = 0 or e.paymentProcessorResult in (?4))  and (?5 = 0 or e.transactionOperation in (?6)) " + orderBy,
                        page * pageSize, pageSize,
                        from, to,
                        HQLUtils.criteriaInTest(statuses),
                        HQLUtils.criteriaIn(statuses),
                        HQLUtils.criteriaInTest(operations),
                        HQLUtils.criteriaIn(operations)
                );

            } else {

                final String search = filter;

                return getGenericDao().findRangeByCriteria(
                        " where (e.orderNumber like ?1 or e.orderShipment like ?1 or lower(e.cardHolderName) like ?1 or lower(e.transactionGatewayLabel) like ?1 or lower(e.transactionOperation) like ?1) and (?2 = 0 or e.paymentProcessorResult in (?3))  and (?4 = 0 or e.transactionOperation in (?5)) " + orderBy,
                        page * pageSize, pageSize,
                        HQLUtils.criteriaIlikeAnywhere(search),
                        HQLUtils.criteriaInTest(statuses),
                        HQLUtils.criteriaIn(statuses),
                        HQLUtils.criteriaInTest(operations),
                        HQLUtils.criteriaIn(operations)
                );

            }

        }

        return getGenericDao().findRangeByCriteria(
                " where (?1 = 0 or e.paymentProcessorResult in (?2))  and (?3 = 0 or e.transactionOperation in (?4)) " + orderBy,
                page * pageSize, pageSize,
                HQLUtils.criteriaInTest(statuses),
                HQLUtils.criteriaIn(statuses),
                HQLUtils.criteriaInTest(operations),
                HQLUtils.criteriaIn(operations)
        );
    }
}

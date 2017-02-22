package org.yes.cart.service.dto.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;
import org.yes.cart.payment.service.DtoCustomerOrderPaymentService;
import org.yes.cart.payment.service.impl.PaymentModuleGenericServiceImpl;

import java.util.*;

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
    private final static List<String> INCOMING_PAYMENTS = Arrays.asList(
            PaymentGateway.AUTH,
            PaymentGateway.AUTH_CAPTURE,
            PaymentGateway.CAPTURE
    );
    private final static List<String> OUTGOING_PAYMENTS = Arrays.asList(
            PaymentGateway.REFUND,
            PaymentGateway.REVERSE_AUTH,
            PaymentGateway.VOID_CAPTURE
    );
    private final static Map<String, List<String>> OPERATION_IN = new HashMap<String, List<String>>() {{
        put("-", OUTGOING_PAYMENTS);
        put("+", INCOMING_PAYMENTS);
        put("*", ListUtils.union(INCOMING_PAYMENTS, OUTGOING_PAYMENTS));
    }};
    private final static char[] PAYMENT_OPERATION = new char[] { '$' };
    private final static List<String> PENDING_PAYMENTS = Arrays.asList(
            Payment.PAYMENT_STATUS_PROCESSING,
            Payment.PAYMENT_STATUS_FAILED,
            Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED
    );
    private final static List<String> FAILED_PAYMENTS = Arrays.asList(
            Payment.PAYMENT_STATUS_FAILED,
            Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED
    );
    private final static List<String> COMPLETED_PAYMENTS = Arrays.asList(
            Payment.PAYMENT_STATUS_OK
    );
    private final static Map<String, List<String>> STATUS_IN = new HashMap<String, List<String>>() {{
        put("~", PENDING_PAYMENTS);
        put("-", FAILED_PAYMENTS);
        put("+", COMPLETED_PAYMENTS);
        put("*", ListUtils.union(PENDING_PAYMENTS, ListUtils.union(FAILED_PAYMENTS, COMPLETED_PAYMENTS)));
    }};
    private final static char[] PAYMENT_STATUS = new char[] { '~', '-', '+', '*' };
    static {
        Arrays.sort(ORDER_OR_CUSTOMER_OR_DETAILS);
        Arrays.sort(PAYMENT_STATUS);
    }
    private final static Order[] PAYMENT_ORDER = new Order[] { Order.asc("createdTimestamp"), Order.asc("orderNumber") };

    /** {@inheritDoc} */
    @Override
    public List<CustomerOrderPayment> findBy(final String filter, final int page, final int pageSize) {

        final List<Criterion> criteria = new ArrayList<Criterion>();

        if (StringUtils.isNotBlank(filter)) {

            Pair<String, String> paymentOperation = ComplexSearchUtils.checkSpecialSearch(filter, PAYMENT_OPERATION);
            if (paymentOperation != null && paymentOperation.getSecond().length() < 2) {
                paymentOperation = null;
            }
            final Pair<String, String> orderNumberOrCustomerOrDetails =
                    paymentOperation == null ? ComplexSearchUtils.checkSpecialSearch(filter, ORDER_OR_CUSTOMER_OR_DETAILS) : ComplexSearchUtils.checkSpecialSearch(paymentOperation.getSecond().substring(1), ORDER_OR_CUSTOMER_OR_DETAILS);
            final Pair<Date, Date> dateSearch = orderNumberOrCustomerOrDetails == null ?
                    (paymentOperation == null ? ComplexSearchUtils.checkDateRangeSearch(filter) : ComplexSearchUtils.checkDateRangeSearch(paymentOperation.getSecond().substring(1)))
                    : null;

            if (orderNumberOrCustomerOrDetails != null) {

                if ("#".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // order/transaction number search
                    final String orderNumber = orderNumberOrCustomerOrDetails.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("orderNumber", orderNumber, MatchMode.ANYWHERE),
                            Restrictions.ilike("orderShipment", orderNumber, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionReferenceId", orderNumber, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionRequestToken", orderNumber, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionAuthorizationCode", orderNumber, MatchMode.ANYWHERE)
                    ));
                } else if ("?".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // customer search
                    final String customer = orderNumberOrCustomerOrDetails.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("cardNumber", customer, MatchMode.ANYWHERE),
                            Restrictions.ilike("cardHolderName", customer, MatchMode.ANYWHERE),
                            Restrictions.ilike("shopperIpAddress", customer, MatchMode.ANYWHERE)
                    ));
                } else if ("@".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // address search
                    final String address = orderNumberOrCustomerOrDetails.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("transactionOperation", address, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionOperationResultCode", address, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionOperationResultMessage", address, MatchMode.ANYWHERE)
                    ));
                }

            } else if (dateSearch != null) {

                final Date from = dateSearch.getFirst();
                final Date to = dateSearch.getSecond();

                // time search
                if (from != null) {
                    criteria.add(Restrictions.ge("createdTimestamp", from));
                }
                if (to != null) {
                    criteria.add(Restrictions.le("createdTimestamp", to));
                }

            } else {

                final Pair<String, String> orderStatus =
                        paymentOperation == null ? ComplexSearchUtils.checkSpecialSearch(filter, PAYMENT_STATUS) : ComplexSearchUtils.checkSpecialSearch(paymentOperation.getSecond().substring(1), PAYMENT_STATUS);

                final String search;
                final List<String> in;
                if (orderStatus != null) {
                    search = orderStatus.getFirst().equals(orderStatus.getSecond()) ? null : orderStatus.getSecond();
                    in = STATUS_IN.get(orderStatus.getFirst());
                } else {
                    search = filter;
                    in = null;
                }

                if (StringUtils.isNotBlank(search)) {
                    // basic search
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("orderNumber", search, MatchMode.ANYWHERE),
                            Restrictions.ilike("orderShipment", search, MatchMode.ANYWHERE),
                            Restrictions.ilike("cardHolderName", search, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionGatewayLabel", search, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionOperation", search, MatchMode.ANYWHERE)
                    ));
                }
                if (in != null) {
                    criteria.add(Restrictions.in("paymentProcessorResult", in));
                }

            }

            if (paymentOperation != null) {

                if (paymentOperation.getSecond().startsWith("-")) {
                    criteria.add(Restrictions.in("transactionOperation", OUTGOING_PAYMENTS));
                } else if (paymentOperation.getSecond().startsWith("+")) {
                    criteria.add(Restrictions.in("transactionOperation", INCOMING_PAYMENTS));
                }

            }

        }


        return getGenericDao().findByCriteria(page * pageSize, pageSize, criteria.toArray(new Criterion[criteria.size()]), PAYMENT_ORDER);
    }

    @Override
    public List<CustomerOrderPayment> findBy(final String filter, final List<String> operations, final List<String> statuses, final int page, final int pageSize) {

        final List<Criterion> criteria = new ArrayList<Criterion>();

        if (StringUtils.isNotBlank(filter)) {

            final Pair<String, String> orderNumberOrCustomerOrDetails =
                    ComplexSearchUtils.checkSpecialSearch(filter, ORDER_OR_CUSTOMER_OR_DETAILS);
            final Pair<Date, Date> dateSearch = orderNumberOrCustomerOrDetails == null ?
                    ComplexSearchUtils.checkDateRangeSearch(filter) : null;

            if (orderNumberOrCustomerOrDetails != null) {

                if ("#".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // order/transaction number search
                    final String orderNumber = orderNumberOrCustomerOrDetails.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("orderNumber", orderNumber, MatchMode.ANYWHERE),
                            Restrictions.ilike("orderShipment", orderNumber, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionReferenceId", orderNumber, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionRequestToken", orderNumber, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionAuthorizationCode", orderNumber, MatchMode.ANYWHERE)
                    ));
                } else if ("?".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // customer search
                    final String customer = orderNumberOrCustomerOrDetails.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("cardNumber", customer, MatchMode.ANYWHERE),
                            Restrictions.ilike("cardHolderName", customer, MatchMode.ANYWHERE),
                            Restrictions.ilike("shopperIpAddress", customer, MatchMode.ANYWHERE)
                    ));
                } else if ("@".equals(orderNumberOrCustomerOrDetails.getFirst())) {
                    // address search
                    final String address = orderNumberOrCustomerOrDetails.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("transactionOperation", address, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionOperationResultCode", address, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionOperationResultMessage", address, MatchMode.ANYWHERE)
                    ));
                }

            } else if (dateSearch != null) {

                final Date from = dateSearch.getFirst();
                final Date to = dateSearch.getSecond();

                // time search
                if (from != null) {
                    criteria.add(Restrictions.ge("createdTimestamp", from));
                }
                if (to != null) {
                    criteria.add(Restrictions.le("createdTimestamp", to));
                }

            } else {

                final String search = filter;

                if (StringUtils.isNotBlank(search)) {
                    // basic search
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("orderNumber", search, MatchMode.ANYWHERE),
                            Restrictions.ilike("orderShipment", search, MatchMode.ANYWHERE),
                            Restrictions.ilike("cardHolderName", search, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionGatewayLabel", search, MatchMode.ANYWHERE),
                            Restrictions.ilike("transactionOperation", search, MatchMode.ANYWHERE)
                    ));
                }

            }

        }

        if (CollectionUtils.isNotEmpty(statuses)) {
            criteria.add(Restrictions.in("paymentProcessorResult", statuses));
        }

        if (CollectionUtils.isNotEmpty(operations)) {
            criteria.add(Restrictions.in("transactionOperation", operations));
        }

        return getGenericDao().findByCriteria(page * pageSize, pageSize, criteria.toArray(new Criterion[criteria.size()]), PAYMENT_ORDER);
    }
}

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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.dto.CustomerOrderDetailDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CustomerOrderDTOImpl;
import org.yes.cart.domain.dto.impl.CustomerOrderDeliveryDTOImpl;
import org.yes.cart.domain.dto.impl.CustomerOrderDeliveryDetailDTOImpl;
import org.yes.cart.domain.dto.impl.CustomerOrderDetailDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.Result;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.CustomerOrderTransitionService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.payment.PaymentModulesManager;

import java.text.MessageFormat;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCustomerOrderServiceImpl extends AbstractDtoServiceImpl<CustomerOrderDTO, CustomerOrderDTOImpl, CustomerOrder>
        implements DtoCustomerOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(DtoCustomerOrderServiceImpl.class);

    protected final Assembler orderDeliveryDetailAssembler;
    protected final Assembler orderDetailAssembler;
    protected final Assembler orderDeliveryAssembler;
    protected final PaymentModulesManager paymentModulesManager;
    protected final CustomerOrderTransitionService transitionService;
    protected final CustomerOrderPaymentService customerOrderPaymentService;


    /**
     * Construct service.
     *
     * @param dtoFactory                  {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param customerOrderGenericService generic service
     * @param adaptersRepository          value converter
     * @param transitionService           transition service
     * @param customerOrderPaymentService payment service
     */
    public DtoCustomerOrderServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<CustomerOrder> customerOrderGenericService,
            final AdaptersRepository adaptersRepository,
            final PaymentModulesManager paymentModulesManager,
            final CustomerOrderTransitionService transitionService,
            final CustomerOrderPaymentService customerOrderPaymentService) {
        super(dtoFactory, customerOrderGenericService, adaptersRepository);
        this.transitionService = transitionService;
        this.customerOrderPaymentService = customerOrderPaymentService;
        orderDeliveryDetailAssembler = DTOAssembler.newAssembler(CustomerOrderDeliveryDetailDTOImpl.class, CustomerOrderDeliveryDet.class);
        orderDeliveryAssembler = DTOAssembler.newAssembler(CustomerOrderDeliveryDTOImpl.class, CustomerOrderDelivery.class);
        orderDetailAssembler = DTOAssembler.newAssembler(CustomerOrderDetailDTOImpl.class, CustomerOrderDet.class);
        this.paymentModulesManager = paymentModulesManager;
    }

    /**
     * {@inheritDoc}
     */
    public CustomerOrderDTO create(final CustomerOrderDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnableToCreateInstanceException("Customer order cannot be created via back end", null);
    }


    /**
     * {@inheritDoc}
     */
    public Class<CustomerOrderDTO> getDtoIFace() {
        return CustomerOrderDTO.class;
    }

    public Class<CustomerOrderDTOImpl> getDtoImpl() {
        return CustomerOrderDTOImpl.class;
    }

    public Class<CustomerOrder> getEntityIFace() {
        return CustomerOrder.class;
    }

    /**
     * {@inheritDoc}
     */
    public Result updateOrderSetConfirmed(final String orderNum) {
        final CustomerOrder order = getService().findSingleByCriteria(Restrictions.eq("ordernum", orderNum));
        if (order == null) {
            return new Result(orderNum, null, "OR-0001", "Order with number [" + orderNum + "] not found",
                    "error.order.not.found", orderNum);
        }

        final boolean isWaiting = CustomerOrder.ORDER_STATUS_WAITING.equals(order.getOrderStatus());

        if (isWaiting) {

            try {
                transitionService.transitionOrder(
                        OrderStateManager.EVT_PAYMENT_CONFIRMED, orderNum, null, Collections.emptyMap());
            } catch (OrderException e) {
                LOG.error(
                        MessageFormat.format(
                                "Cannot confirm payment for order with number [ {0} ] ",
                                orderNum
                        ),
                        e);
                return new Result(orderNum, null, "OR-0003", "Cannot confirm payment for order with number [" + orderNum + "]   ",
                        "error.order.payment.confirm.fatal", orderNum, e.getMessage());
            }
        } else {

            return new Result(orderNum, null, "OR-0003", "Cannot confirm payment for order with number [" + orderNum + "]   ",
                    "error.order.payment.confirm.fatal", orderNum, order.getOrderStatus());

        }

        return new Result(orderNum, null);
    }

    /**
     * {@inheritDoc}
     */
    public Result updateOrderSetCancelled(final String orderNum) {
        final CustomerOrder order = getService().findSingleByCriteria(Restrictions.eq("ordernum", orderNum));
        if (order == null) {
            return new Result(orderNum, null, "OR-0001", "Order with number [" + orderNum + "] not found",
                    "error.order.not.found", orderNum);
        }

        final boolean isWaitingRefund =
                CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT.equals(order.getOrderStatus()) ||
                        CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT.equals(order.getOrderStatus());

        final boolean isCancellable = !isWaitingRefund &&
                !CustomerOrder.ORDER_STATUS_CANCELLED.equals(order.getOrderStatus()) &&
                !CustomerOrder.ORDER_STATUS_RETURNED.equals(order.getOrderStatus());

        if (isCancellable) {
            // We always cancel with refund since we may have completed payments
            try {
                transitionService.transitionOrder(
                        OrderStateManager.EVT_CANCEL_WITH_REFUND, orderNum, null, Collections.emptyMap());
            } catch (OrderException e) {
                LOG.error(
                        MessageFormat.format(
                                "Order with number [ {0} ] cannot be canceled ",
                                orderNum
                        ),
                        e);
                return new Result(orderNum, null, "OR-0002", "Order with number [" + orderNum + "] cannot be cancelled  ",
                        "error.order.cancel.fatal", orderNum, e.getMessage());
            }
        } else if (isWaitingRefund) {
                // Retry processing refund
            try {
                transitionService.transitionOrder(
                        OrderStateManager.EVT_REFUND_PROCESSED, orderNum, null, Collections.emptyMap());
            } catch (OrderException e) {
                LOG.error(
                        MessageFormat.format(
                                "Order with number [ {0} ] cannot be canceled ",
                                orderNum
                        ),
                        e);
                return new Result(orderNum, null, "OR-0004", "Order with number [" + orderNum + "] cannot be cancelled (retry) ",
                        "error.order.cancel.retry.fatal", orderNum, e.getMessage());
            }
        } else {

            return new Result(orderNum, null, "OR-0007", "Order with number [" + orderNum + "] unable to cancel",
                    "error.order.cancel.fatal", orderNum);

        }

        return new Result(orderNum, null);
    }

    /**
     * {@inheritDoc}
     */
    public Result updateOrderSetCancelledManual(final String orderNum, final String message) {
        final CustomerOrder order = getService().findSingleByCriteria(Restrictions.eq("ordernum", orderNum));
        if (order == null) {
            return new Result(orderNum, null, "OR-0001", "Order with number [" + orderNum + "] not found",
                    "error.order.not.found", orderNum);
        }

        if (StringUtils.isBlank(message)) {
            return new Result(orderNum, null, "OR-0006", "Manual refund for order with number [" + orderNum + "] must have authorisation code",
                    "error.order.cancel.retry.manual.fatal.no.notes", orderNum);
        }

        final boolean isWaitingRefund =
                CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT.equals(order.getOrderStatus()) ||
                        CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT.equals(order.getOrderStatus());

        if (isWaitingRefund) {
                // Retry processing refund
            try {
                transitionService.transitionOrder(
                        OrderStateManager.EVT_REFUND_PROCESSED, orderNum, null,
                        new HashMap() {{
                            put("forceManualProcessing", Boolean.TRUE);
                            put("forceManualProcessingMessage", message);
                        }});
            } catch (OrderException e) {
                LOG.error(
                        MessageFormat.format(
                                "Order with number [ {0} ] cannot be canceled ",
                                orderNum
                        ),
                        e);
                return new Result(orderNum, null, "OR-0005", "Order with number [" + orderNum + "] cannot be cancelled (retry manual) ",
                        "error.order.cancel.retry.manual.fatal", orderNum, e.getMessage());
            }
        } else {

            return new Result(orderNum, null, "OR-0007", "Order with number [" + orderNum + "] unable to cancel",
                    "error.order.cancel.fatal", orderNum);

        }

        return new Result(orderNum, null);
    }

    /**
     * {@inheritDoc}
     */
    public Result updateExternalDeliveryRefNo(final String orderNum, final String deliveryNum, final String newRefNo) {

        final CustomerOrder order = getService().findSingleByCriteria(Restrictions.eq("ordernum", orderNum));

        if (order == null) {
            return new Result(orderNum, deliveryNum, "DL-0001", "Order with number [" + orderNum + "] not found",
                    "error.order.not.found", orderNum);
        }

        final CustomerOrderDelivery delivery = order.getCustomerOrderDelivery(deliveryNum);
        if (delivery == null) {
            return new Result(orderNum, deliveryNum, "DL-0002", "Order with number [" + orderNum + "] has not delivery with number [" + deliveryNum + "]",
                    "error.delivery.not.found", orderNum, deliveryNum);
        }

        delivery.setRefNo(newRefNo);
        getService().update(order);

        return new Result(orderNum, deliveryNum);

    }


    /**
     * {@inheritDoc}
     */
    public Result updateDeliveryStatus(final String orderNum, final String deliveryNum,
                                       final String currentStatus, final String destinationStatus) {

        final CustomerOrder order = getService().findSingleByCriteria(Restrictions.eq("ordernum", orderNum));

        if (order == null) {
            return new Result(orderNum, deliveryNum, "DL-0001", "Order with number [" + orderNum + "] not found",
                    "error.order.not.found", orderNum);
        }
        final CustomerOrderDelivery delivery = order.getCustomerOrderDelivery(deliveryNum);
        if (delivery == null) {
            return new Result(orderNum, deliveryNum, "DL-0002", "Order with number [" + orderNum + "] has not delivery with number [" + deliveryNum + "]",
                    "error.delivery.not.found", orderNum, deliveryNum);
        }
        if (!delivery.getDeliveryStatus().equals(currentStatus)) {
            return new Result(orderNum, deliveryNum, "DL-0003", "Order with number [" + orderNum + "] delivery number [" + deliveryNum + "] in [" + delivery.getDeliveryStatus() + "] state, but required [" + currentStatus + "]. Updated by [" + order.getUpdatedBy() + "]",
                    "error.delivery.in.wrong.state", orderNum, deliveryNum, delivery.getDeliveryStatus(), currentStatus, order.getUpdatedBy());
        }

        try {

            final boolean needToPersist;

            if (CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED.equals(currentStatus) &&
                    CustomerOrderDelivery.DELIVERY_STATUS_PACKING.equals(destinationStatus)) {

                transitionService.transitionOrder(
                        OrderStateManager.EVT_RELEASE_TO_PACK, orderNum, deliveryNum, Collections.emptyMap());

            } else if (CustomerOrderDelivery.DELIVERY_STATUS_PACKING.equals(currentStatus) &&
                    CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY.equals(destinationStatus)) {

                transitionService.transitionOrder(
                        OrderStateManager.EVT_PACK_COMPLETE, orderNum, deliveryNum, Collections.emptyMap());

            } else if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY.equals(currentStatus) &&
                    CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS.equals(destinationStatus)) {

                transitionService.transitionOrder(
                        OrderStateManager.EVT_RELEASE_TO_SHIPMENT, orderNum, deliveryNum, Collections.emptyMap());

            } else if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT.equals(currentStatus) &&
                    CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS.equals(destinationStatus)) {

                transitionService.transitionOrder(
                        OrderStateManager.EVT_RELEASE_TO_SHIPMENT, orderNum, deliveryNum, Collections.emptyMap());

            } else if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS.equals(currentStatus) &&
                    CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED.equals(destinationStatus)) {

                transitionService.transitionOrder(
                        OrderStateManager.EVT_SHIPMENT_COMPLETE, orderNum, deliveryNum, Collections.emptyMap());

            } else if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS_WAITING_PAYMENT.equals(currentStatus) &&
                    CustomerOrderDelivery.DELIVERY_STATUS_SHIPPED.equals(destinationStatus)) {

                // same as shipping in progress to complete
                transitionService.transitionOrder(
                        OrderStateManager.EVT_SHIPMENT_COMPLETE, orderNum, deliveryNum, Collections.emptyMap());

            } else {

                return new Result(orderNum, deliveryNum, "DL-0004", "Transition from [" + currentStatus + "] to [" + destinationStatus + "] delivery state is illegal",
                        "error.illegal.transition", currentStatus, destinationStatus);

            }

            return new Result(orderNum, deliveryNum);

        } catch (OrderException e) {

            LOG.error(
                    MessageFormat.format(
                            "Order with number [ {0} ] delivery number [ {1} ] in [ {2} ] can not be transited to  [ {3} ] status ",
                            orderNum, deliveryNum, delivery.getDeliveryStatus(), currentStatus
                    ),
                    e);

            return new Result(orderNum, deliveryNum, "DL-0004", "Order with number [" + orderNum + "] delivery number [" + deliveryNum + "] in [" + delivery.getDeliveryStatus() + "] can not be transited to  [" + currentStatus + "] status ",
                    "error.delivery.transition.fatal", orderNum, deliveryNum, delivery.getDeliveryStatus(), currentStatus, e.getMessage());


        }

    }


    /**
     * {@inheritDoc}
     */
    public Result updateDeliveryStatusManual(final String orderNum, final String deliveryNum,
                                             final String currentStatus, final String destinationStatus,
                                             final String message) {


        final CustomerOrder order = getService().findSingleByCriteria(Restrictions.eq("ordernum", orderNum));

        if (order == null) {
            return new Result(orderNum, deliveryNum, "DL-0001", "Order with number [" + orderNum + "] not found",
                    "error.order.not.found", orderNum);
        }
        final CustomerOrderDelivery delivery = order.getCustomerOrderDelivery(deliveryNum);
        if (delivery == null) {
            return new Result(orderNum, deliveryNum, "DL-0002", "Order with number [" + orderNum + "] has not delivery with number [" + deliveryNum + "]",
                    "error.delivery.not.found", orderNum, deliveryNum);
        }
        if (!delivery.getDeliveryStatus().equals(currentStatus)) {
            return new Result(orderNum, deliveryNum, "DL-0003", "Order with number [" + orderNum + "] delivery number [" + deliveryNum + "] in [" + delivery.getDeliveryStatus() + "] state, but required [" + currentStatus + "]. Updated by [" + order.getUpdatedBy() + "]",
                    "error.delivery.in.wrong.state", orderNum, deliveryNum, delivery.getDeliveryStatus(), currentStatus, order.getUpdatedBy());
        }
        if (StringUtils.isBlank(message)) {
            return new Result(orderNum, deliveryNum, "DL-0005", "Manual operation for order with number [" + orderNum + "] delivery number [" + deliveryNum + "] requires manual authorisation code",
                    "error.delivery.manual.no.notes", orderNum, deliveryNum);
        }

        try {

            if (CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_READY_WAITING_PAYMENT.equals(currentStatus) &&
                    CustomerOrderDelivery.DELIVERY_STATUS_SHIPMENT_IN_PROGRESS.equals(destinationStatus)) {

                transitionService.transitionOrder(
                        OrderStateManager.EVT_RELEASE_TO_SHIPMENT, orderNum, deliveryNum,
                        new HashMap() {{
                            put("forceManualProcessing", Boolean.TRUE);
                            put("forceManualProcessingMessage", message);
                        }});

            } else {

                return new Result(orderNum, deliveryNum, "DL-0004", "Transition from [" + currentStatus + "] to [" + destinationStatus + "] delivery state is illegal",
                        "error.illegal.transition", currentStatus, destinationStatus);

            }

            return new Result(orderNum, deliveryNum);

        } catch (OrderException e) {

            LOG.error(
                    MessageFormat.format(
                            "Order with number [ {0} ] delivery number [ {1} ] in [ {2} ] can not be transited to  [ {3} ] status ",
                            orderNum, deliveryNum, delivery.getDeliveryStatus(), currentStatus
                    ),
                    e);

            return new Result(orderNum, deliveryNum, "DL-0004", "Order with number [" + orderNum + "] delivery number [" + deliveryNum + "] in [" + delivery.getDeliveryStatus() + "] can not be transited to  [" + currentStatus + "] status ",
                    "error.delivery.transition.fatal", orderNum, deliveryNum, delivery.getDeliveryStatus(), currentStatus, e.getMessage());


        }

    }

    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderDeliveryDTO> findDeliveryByOrderNumber(final String orderNum)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        return findDeliveryByOrderNumber(orderNum, null);

    }



    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderDeliveryDTO> findDeliveryByOrderNumber(final String orderNum, final String deliveryNum)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CustomerOrder> orderList = ((CustomerOrderService) service).findCustomerOrdersByCriteria(
                0, null, null, null, null, null, null, orderNum);

        if (CollectionUtils.isNotEmpty(orderList)) {
            final CustomerOrder customerOrder = orderList.get(0);
            final Shop pgShop = customerOrder.getShop().getMaster() != null ? customerOrder.getShop().getMaster() : customerOrder.getShop();
            final PaymentGateway paymentGateway = paymentModulesManager.getPaymentGateway(customerOrder.getPgLabel(), pgShop.getCode());
            if (paymentGateway == null) {
                LOG.error("Cannot determine capture less/more because gateway {} is not resolved for shop {}, could it be disabled?", customerOrder.getPgLabel(), customerOrder.getShop().getCode());
            }

            final List<CustomerOrderDeliveryDTO> rez = new ArrayList<CustomerOrderDeliveryDTO>(customerOrder.getDelivery().size());

            for (CustomerOrderDelivery delivery : customerOrder.getDelivery()) {

                if (StringUtils.isBlank(deliveryNum) || (StringUtils.isNotBlank(deliveryNum) && delivery.getDeliveryNum().equals(deliveryNum))) {
                    final CustomerOrderDeliveryDTO dto = dtoFactory.getByIface(CustomerOrderDeliveryDTO.class);
                    orderDeliveryAssembler.assembleDto(dto, delivery, getAdaptersRepository(), dtoFactory);
                    if (paymentGateway != null) {
                        final PaymentGatewayFeature pgwFeatures = paymentGateway.getPaymentGatewayFeatures();
                        dto.setSupportCaptureMore(pgwFeatures.isSupportCaptureMore());
                        dto.setSupportCaptureLess(pgwFeatures.isSupportCaptureLess());
                    }
                    rez.add(dto);
                }

            }
            return rez;

        } else {
            LOG.warn("Customer order not found. Order number is " + orderNum);
        }
        return Collections.emptyList();

    }


    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderDeliveryDetailDTO> findDeliveryDetailsByOrderNumber(final String orderNum) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<CustomerOrder> orderList = ((CustomerOrderService) service).findCustomerOrdersByCriteria(
                0, null, null, null, null, null, null, orderNum);

        if (CollectionUtils.isNotEmpty(orderList)) {
            final CustomerOrder customerOrder = orderList.get(0);
            final List<CustomerOrderDeliveryDet> allDeliveryDet = new ArrayList<CustomerOrderDeliveryDet>();
            for (CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
                allDeliveryDet.addAll(orderDelivery.getDetail());
            }
            final List<CustomerOrderDeliveryDetailDTO> rez = new ArrayList<CustomerOrderDeliveryDetailDTO>(allDeliveryDet.size());

            if (CollectionUtils.isNotEmpty(allDeliveryDet)) {
                for (CustomerOrderDeliveryDet entity : allDeliveryDet) {
                    CustomerOrderDeliveryDetailDTO dto = dtoFactory.getByIface(CustomerOrderDeliveryDetailDTO.class);
                    orderDeliveryDetailAssembler.assembleDto(dto, entity, getAdaptersRepository(), dtoFactory);
                    rez.add(dto);
                }
            } else {
                // RFQ and drafts
                for (CustomerOrderDet entity : customerOrder.getOrderDetail()) {
                    CustomerOrderDeliveryDetailDTO dto = dtoFactory.getByIface(CustomerOrderDetailDTO.class);
                    orderDetailAssembler.assembleDto(dto, entity, getAdaptersRepository(), dtoFactory);
                    rez.add(dto);
                }
            }

            return rez;
        } else {
            LOG.warn("Customer order not found. Order num is " + orderNum);
        }
        return Collections.emptyList();

    }


    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderDTO> findCustomerOrdersByCriteria(
            final long customerId,
            final String firstName,
            final String lastName,
            final String email,
            final String orderStatus,
            final Date fromDate,
            final Date toDate,
            final String orderNum
    ) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CustomerOrder> orders = ((CustomerOrderService) service).findCustomerOrdersByCriteria(
                customerId,
                firstName,
                lastName,
                email,
                orderStatus,
                fromDate,
                toDate,
                orderNum
        );
        final List<CustomerOrderDTO> ordersDtos = new ArrayList<CustomerOrderDTO>(orders.size());
        fillDTOs(orders, ordersDtos);
        Collections.sort(ordersDtos, ORDERS_REV_CHRONOLOGICAL);
        return ordersDtos;
    }

    private static final Comparator<CustomerOrderDTO> ORDERS_REV_CHRONOLOGICAL = new Comparator<CustomerOrderDTO>() {
        public int compare(final CustomerOrderDTO o1, final CustomerOrderDTO o2) {
            return -o1.getOrderTimestamp().compareTo(o2.getOrderTimestamp());
        }
    };


    private final static char[] ORDER_OR_CUSTOMER_OR_ADDRESS_OR_SKU = new char[] { '#', '?', '@', '!', '*' };
    private final static List<String> OPEN_ORDERS = Arrays.asList(
            CustomerOrder.ORDER_STATUS_PENDING,
            CustomerOrder.ORDER_STATUS_WAITING,
            CustomerOrder.ORDER_STATUS_WAITING_PAYMENT,
            CustomerOrder.ORDER_STATUS_IN_PROGRESS,
            CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED
    );
    private final static List<String> CANCELLED_ORDERS = Arrays.asList(
            CustomerOrder.ORDER_STATUS_CANCELLED,
            CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT,
            CustomerOrder.ORDER_STATUS_RETURNED,
            CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT
    );
    private final static List<String> PAYMENT_ORDERS = Arrays.asList(
            CustomerOrder.ORDER_STATUS_WAITING_PAYMENT,
            CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT,
            CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT
    );
    private final static List<String> COMPLETED_ORDERS = Arrays.asList(
            CustomerOrder.ORDER_STATUS_COMPLETED
    );
    private final static Map<String, List<String>> STATUS_IN = new HashMap<String, List<String>>() {{
        put("~", OPEN_ORDERS);
        put("-", CANCELLED_ORDERS);
        put("$", PAYMENT_ORDERS);
        put("+", COMPLETED_ORDERS);
        put("*", ListUtils.union(OPEN_ORDERS, ListUtils.union(CANCELLED_ORDERS, ListUtils.union(PAYMENT_ORDERS, COMPLETED_ORDERS))));
    }};
    private final static char[] ORDER_STATUS = new char[] { '~', '-', '$', '+', '*' };
    static {
        Arrays.sort(ORDER_OR_CUSTOMER_OR_ADDRESS_OR_SKU);
        Arrays.sort(ORDER_STATUS);
    }

    private final static Order[] ORDERS_ORDER = new Order[] { Order.desc("orderTimestamp"), Order.desc("ordernum") };

    @Override
    public List<CustomerOrderDTO> findBy(final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<CustomerOrderDTO> orders = new ArrayList<>();

        final List<Criterion> criteria = new ArrayList<Criterion>();

        if (StringUtils.isNotBlank(filter)) {

            final Pair<String, String> orderNumberOrCustomerOrAddressOrSku = ComplexSearchUtils.checkSpecialSearch(filter, ORDER_OR_CUSTOMER_OR_ADDRESS_OR_SKU);
            final Pair<Date, Date> dateSearch = orderNumberOrCustomerOrAddressOrSku == null ? ComplexSearchUtils.checkDateRangeSearch(filter) : null;

            if (orderNumberOrCustomerOrAddressOrSku != null) {

                if ("*".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // If this by PK then to by PK
                    final long byPk = NumberUtils.toLong(orderNumberOrCustomerOrAddressOrSku.getFirst());
                    if (page == 0 && byPk > 0) {
                        final CustomerOrderDTO order = getById(byPk);
                        if (order != null) {
                            orders.add(order);
                        }
                    }
                    return orders;
                } else if ("#".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // order number search
                    final String orderNumber = orderNumberOrCustomerOrAddressOrSku.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("ordernum", orderNumber, MatchMode.ANYWHERE),
                            Restrictions.ilike("cartGuid", orderNumber, MatchMode.ANYWHERE)
                    ));
                } else if ("?".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // customer search
                    final String customer = orderNumberOrCustomerOrAddressOrSku.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("email", customer, MatchMode.ANYWHERE),
                            Restrictions.ilike("firstname", customer, MatchMode.ANYWHERE),
                            Restrictions.ilike("lastname", customer, MatchMode.ANYWHERE)
                    ));
                } else if ("@".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // address search
                    final String address = orderNumberOrCustomerOrAddressOrSku.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("billingAddress", address, MatchMode.ANYWHERE),
                            Restrictions.ilike("shippingAddress", address, MatchMode.ANYWHERE)
                    ));
                } else if ("!".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    if (page > 0) {
                        return Collections.emptyList();
                    }
                    return findByReservation(orderNumberOrCustomerOrAddressOrSku.getSecond());
                }

            } else if (dateSearch != null) {

                final Date from = dateSearch.getFirst();
                final Date to = dateSearch.getSecond();

                // time search
                if (from != null) {
                    criteria.add(Restrictions.ge("orderTimestamp", from));
                }
                if (to != null) {
                    criteria.add(Restrictions.le("orderTimestamp", to));
                }

            } else {

                final Pair<String, String> orderStatus = ComplexSearchUtils.checkSpecialSearch(filter, ORDER_STATUS);

                final String search;
                final List<String> in;
                if (orderStatus != null) {
                    search = orderStatus.getFirst().equals(orderStatus.getSecond()) ? null : orderStatus.getSecond();
                    in = STATUS_IN.get(orderStatus.getFirst());
                } else {
                    search = filter;
                    in = OPEN_ORDERS; // default is open orders only
                }

                if (StringUtils.isNotBlank(search)) {
                    // basic search
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("ordernum", search, MatchMode.ANYWHERE),
                            Restrictions.ilike("email", search, MatchMode.ANYWHERE),
                            Restrictions.ilike("lastname", search, MatchMode.ANYWHERE)
                    ));
                }
                criteria.add(Restrictions.in("orderStatus", in));

            }

        } else {

            criteria.add(Restrictions.in("orderStatus", OPEN_ORDERS));  // default is open orders only

        }

        final List<CustomerOrder> entities = service.getGenericDao().findByCriteria(page * pageSize, pageSize, criteria.toArray(new Criterion[criteria.size()]), ORDERS_ORDER);

        fillDTOs(entities, orders);

        return orders;
    }


    @Override
    public List<CustomerOrderDTO> findBy(final String filter, final List<String> statuses, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<CustomerOrderDTO> orders = new ArrayList<>();

        final List<Criterion> criteria = new ArrayList<Criterion>();

        if (StringUtils.isNotBlank(filter)) {

            final Pair<String, String> orderNumberOrCustomerOrAddressOrSku = ComplexSearchUtils.checkSpecialSearch(filter, ORDER_OR_CUSTOMER_OR_ADDRESS_OR_SKU);
            final Pair<Date, Date> dateSearch = orderNumberOrCustomerOrAddressOrSku == null ? ComplexSearchUtils.checkDateRangeSearch(filter) : null;

            if (orderNumberOrCustomerOrAddressOrSku != null) {

                if ("*".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // If this by PK then to by PK
                    final long byPk = NumberUtils.toLong(orderNumberOrCustomerOrAddressOrSku.getSecond());
                    if (page == 0 && byPk > 0) {
                        final CustomerOrderDTO order = getById(byPk);
                        if (order != null) {
                            orders.add(order);
                        }
                    }
                    return orders;
                } else if ("#".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // order number search
                    final String orderNumber = orderNumberOrCustomerOrAddressOrSku.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("ordernum", orderNumber, MatchMode.ANYWHERE),
                            Restrictions.ilike("cartGuid", orderNumber, MatchMode.ANYWHERE)
                    ));
                } else if ("?".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // customer search
                    final String customer = orderNumberOrCustomerOrAddressOrSku.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("email", customer, MatchMode.ANYWHERE),
                            Restrictions.ilike("firstname", customer, MatchMode.ANYWHERE),
                            Restrictions.ilike("lastname", customer, MatchMode.ANYWHERE)
                    ));
                } else if ("@".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // address search
                    final String address = orderNumberOrCustomerOrAddressOrSku.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("billingAddress", address, MatchMode.ANYWHERE),
                            Restrictions.ilike("shippingAddress", address, MatchMode.ANYWHERE)
                    ));
                } else if ("!".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    if (page > 0) {
                        return Collections.emptyList();
                    }
                    return findByReservation(orderNumberOrCustomerOrAddressOrSku.getSecond());
                }

            } else if (dateSearch != null) {

                final Date from = dateSearch.getFirst();
                final Date to = dateSearch.getSecond();

                // time search
                if (from != null) {
                    criteria.add(Restrictions.ge("orderTimestamp", from));
                }
                if (to != null) {
                    criteria.add(Restrictions.le("orderTimestamp", to));
                }

            } else {

                final String search = filter;

                if (StringUtils.isNotBlank(search)) {
                    // basic search
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("ordernum", search, MatchMode.ANYWHERE),
                            Restrictions.ilike("email", search, MatchMode.ANYWHERE),
                            Restrictions.ilike("lastname", search, MatchMode.ANYWHERE)
                    ));
                }

            }

        }

        if (CollectionUtils.isNotEmpty(statuses)) {
            criteria.add(Restrictions.in("orderStatus", statuses));
        }

        final List<CustomerOrder> entities = service.getGenericDao().findByCriteria(page * pageSize, pageSize, criteria.toArray(new Criterion[criteria.size()]), ORDERS_ORDER);

        fillDTOs(entities, orders);

        return orders;
    }


    private List<CustomerOrderDTO> findByReservation(final String sku) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<String> orderStatusThatCouldHaveReservations = Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS, CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED);
        final List<String> skus = Collections.singletonList(sku);
        final CustomerOrderService cos = (CustomerOrderService) service;
        final Set<Long> deliveryIds = new HashSet<Long>();

        deliveryIds.addAll(cos.findAwaitingDeliveriesIds(
                skus,
                CustomerOrderDelivery.DELIVERY_STATUS_ALLOCATION_WAIT,
                orderStatusThatCouldHaveReservations
        ));
        deliveryIds.addAll(cos.findAwaitingDeliveriesIds(
                skus,
                CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                orderStatusThatCouldHaveReservations
        ));
        deliveryIds.addAll(cos.findAwaitingDeliveriesIds(
                skus,
                CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT,
                orderStatusThatCouldHaveReservations
        ));


        final List<CustomerOrderDTO> orders = new ArrayList<>();

        if (!deliveryIds.isEmpty()) {

            final List<CustomerOrder> entities = cos.findCustomerOrdersByDeliveryIds(deliveryIds);
            fillDTOs(entities, orders);

        }

        return orders;

    }

    @Override
    public Map<String, String> getOrderPgLabels(final String locale) {


        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false, "DEFAULT");

        final Map<String, String> available = new HashMap<String, String>();

        for (final PaymentGatewayDescriptor descriptor : descriptors) {

            final PaymentGateway gateway = paymentModulesManager.getPaymentGateway(descriptor.getLabel(), "DEFAULT");
            available.put(descriptor.getLabel(), gateway.getName(locale));

        }

        return available;
    }

    @Override
    protected void assemblyPostProcess(final CustomerOrderDTO dto, final CustomerOrder entity) {
        dto.setAmount(customerOrderPaymentService.getOrderAmount(entity.getOrdernum()));
        super.assemblyPostProcess(dto, entity);
    }
}

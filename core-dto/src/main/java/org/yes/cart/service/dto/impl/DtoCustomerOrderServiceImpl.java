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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.CustomerOrderTransitionService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.MessageFormatUtils;

import java.time.LocalDateTime;
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
    protected final CustomerService customerService;


    /**
     * Construct service.
     *
     * @param dtoFactory                  {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param customerOrderGenericService generic service
     * @param adaptersRepository          value converter
     * @param transitionService           transition service
     * @param customerOrderPaymentService payment service
     * @param customerService             customer service
     */
    public DtoCustomerOrderServiceImpl(final DtoFactory dtoFactory,
                                       final GenericService<CustomerOrder> customerOrderGenericService,
                                       final AdaptersRepository adaptersRepository,
                                       final PaymentModulesManager paymentModulesManager,
                                       final CustomerOrderTransitionService transitionService,
                                       final CustomerOrderPaymentService customerOrderPaymentService,
                                       final CustomerService customerService) {
        super(dtoFactory, customerOrderGenericService, adaptersRepository);
        this.transitionService = transitionService;
        this.customerOrderPaymentService = customerOrderPaymentService;
        orderDeliveryDetailAssembler = DTOAssembler.newAssembler(CustomerOrderDeliveryDetailDTOImpl.class, CustomerOrderDeliveryDet.class);
        orderDeliveryAssembler = DTOAssembler.newAssembler(CustomerOrderDeliveryDTOImpl.class, CustomerOrderDelivery.class);
        orderDetailAssembler = DTOAssembler.newAssembler(CustomerOrderDetailDTOImpl.class, CustomerOrderDet.class);
        this.paymentModulesManager = paymentModulesManager;
        this.customerService = customerService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomerOrderDTO create(final CustomerOrderDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnableToCreateInstanceException("Customer order cannot be created via back end", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Class<CustomerOrderDTO> getDtoIFace() {
        return CustomerOrderDTO.class;
    }

    @Override
    public Class<CustomerOrderDTOImpl> getDtoImpl() {
        return CustomerOrderDTOImpl.class;
    }

    @Override
    public Class<CustomerOrder> getEntityIFace() {
        return CustomerOrder.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result updateOrderSetConfirmed(final String orderNum) {
        final CustomerOrder order = ((CustomerOrderService) service).findByReference(orderNum);
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
                final String error = MessageFormatUtils.format(
                        "Cannot confirm payment for order with number [ {} ] ",
                        orderNum
                );
                LOG.error(error, e);
                return new Result(orderNum, null, "OR-0003", error,
                        "error.order.payment.confirm.fatal", orderNum, e.getMessage());
            }
        } else {

            final String error = MessageFormatUtils.format(
                    "Cannot confirm payment for order with number [ {} ] ",
                    orderNum
            );

            return new Result(orderNum, null, "OR-0003", error,
                    "error.order.payment.confirm.fatal", orderNum, order.getOrderStatus());

        }

        return new Result(orderNum, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result updateOrderSetCancelled(final String orderNum) {
        final CustomerOrder order = ((CustomerOrderService) service).findByReference(orderNum);
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

                final String error = MessageFormatUtils.format(
                        "Order with number [ {} ] cannot be canceled ",
                        orderNum
                );

                LOG.error(error, e);
                return new Result(orderNum, null, "OR-0002", error,
                        "error.order.cancel.fatal", orderNum, e.getMessage());
            }
        } else if (isWaitingRefund) {
                // Retry processing refund
            try {
                transitionService.transitionOrder(
                        OrderStateManager.EVT_REFUND_PROCESSED, orderNum, null, Collections.emptyMap());
            } catch (OrderException e) {

                final String error = MessageFormatUtils.format(
                        "Order with number [ {} ] cannot be canceled (retry) ",
                        orderNum
                );

                LOG.error(error, e);
                return new Result(orderNum, null, "OR-0004", error,
                        "error.order.cancel.retry.fatal", orderNum, e.getMessage());
            }
        } else {

            final String error = MessageFormatUtils.format(
                    "Order with number [ {} ] unable to cancel ",
                    orderNum
            );

            return new Result(orderNum, null, "OR-0007", error,
                    "error.order.cancel.fatal", orderNum);

        }

        return new Result(orderNum, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result updateOrderSetCancelledManual(final String orderNum, final String message) {
        final CustomerOrder order = ((CustomerOrderService) service).findByReference(orderNum);
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

                final String error = MessageFormatUtils.format(
                        "Order with number [ {} ] cannot be canceled  (retry manual) ",
                        orderNum
                );

                LOG.error(error, e);
                return new Result(orderNum, null, "OR-0005", error,
                        "error.order.cancel.retry.manual.fatal", orderNum, e.getMessage());
            }
        } else {

            final String error = MessageFormatUtils.format(
                    "Order with number [ {} ] unable to cancel ",
                    orderNum
            );

            return new Result(orderNum, null, "OR-0007", error,
                    "error.order.cancel.fatal", orderNum);

        }

        return new Result(orderNum, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Result updateExternalDeliveryRefNo(final String orderNum, final String deliveryNum, final String newRefNo) {

        final CustomerOrder order = ((CustomerOrderService) service).findByReference(orderNum);

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
    @Override
    public Result updateDeliveryStatus(final String orderNum, final String deliveryNum,
                                       final String currentStatus, final String destinationStatus) {

        final CustomerOrder order = ((CustomerOrderService) service).findByReference(orderNum);

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

            final String error = MessageFormatUtils.format(
                    "Order with number [ {} ] delivery number [ {} ] in [ {} ] can not be transitioned to  [ {} ] status ",
                    orderNum, deliveryNum, delivery.getDeliveryStatus(), currentStatus
            );

            LOG.error(error, e);

            return new Result(orderNum, deliveryNum, "DL-0004", error,
                    "error.delivery.transition.fatal", orderNum, deliveryNum, delivery.getDeliveryStatus(), currentStatus, e.getMessage());


        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Result updateDeliveryStatusManual(final String orderNum, final String deliveryNum,
                                             final String currentStatus, final String destinationStatus,
                                             final String message) {


        final CustomerOrder order = ((CustomerOrderService) service).findByReference(orderNum);

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

            final String error = MessageFormatUtils.format(
                    "Order with number [ {} ] delivery number [ {} ] in [ {} ] can not be transited to  [ {} ] status ",
                    orderNum, deliveryNum, delivery.getDeliveryStatus(), currentStatus
            );

            LOG.error(error, e);

            return new Result(orderNum, deliveryNum, "DL-0004", error,
                    "error.delivery.transition.fatal", orderNum, deliveryNum, delivery.getDeliveryStatus(), currentStatus, e.getMessage());


        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomerOrderDeliveryDTO> findDeliveryByOrderNumber(final String orderNum)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        return findDeliveryByOrderNumber(orderNum, null);

    }



    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomerOrderDeliveryDTO> findDeliveryByOrderNumber(final String orderNum, final String deliveryNum)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final CustomerOrder customerOrder = ((CustomerOrderService) service).findByReference(orderNum);

        if (customerOrder != null) {
            final Shop pgShop = customerOrder.getShop().getMaster() != null ? customerOrder.getShop().getMaster() : customerOrder.getShop();
            final PaymentGateway paymentGateway = paymentModulesManager.getPaymentGateway(customerOrder.getPgLabel(), pgShop.getCode());
            if (paymentGateway == null) {
                LOG.error("Cannot determine capture less/more because gateway {} is not resolved for shop {}, could it be disabled?", customerOrder.getPgLabel(), customerOrder.getShop().getCode());
            }

            final List<CustomerOrderDeliveryDTO> rez = new ArrayList<>(customerOrder.getDelivery().size());

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
            LOG.warn("Customer order not found. Order number is {}", orderNum);
        }
        return Collections.emptyList();

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<CustomerOrderDeliveryDetailDTO> findDeliveryDetailsByOrderNumber(final String orderNum) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final CustomerOrder customerOrder = ((CustomerOrderService) service).findByReference(orderNum);

        if (customerOrder != null) {
            final List<CustomerOrderDeliveryDet> allDeliveryDet = new ArrayList<>();
            for (CustomerOrderDelivery orderDelivery : customerOrder.getDelivery()) {
                allDeliveryDet.addAll(orderDelivery.getDetail());
            }
            final List<CustomerOrderDeliveryDetailDTO> rez = new ArrayList<>(allDeliveryDet.size());

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
            LOG.warn("Customer order not found. Order num is {}", orderNum);
        }
        return Collections.emptyList();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomerOrderDTO findByOrderNumber(final String orderNum) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final CustomerOrder order = ((CustomerOrderService) service).findByReference(orderNum);
        if (order != null) {
            return fillDTO(order);
        }
        return null;
        
    }

    private final static char[] ORDER_OR_CUSTOMER_OR_ADDRESS_OR_SKU = new char[] { '#', '?', '@', '!', '*', '^' };
    static {
        Arrays.sort(ORDER_OR_CUSTOMER_OR_ADDRESS_OR_SKU);
    }


    @Override
    public SearchResult<CustomerOrderDTO> findOrders(final Set<Long> shopIds, final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter", "statuses");
        final List filterParam = params.get("filter");
        final List statusesParam = params.get("statuses");

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final Map<String, List> currentFilter = new HashMap<>();


        if (CollectionUtils.isNotEmpty(filterParam) && filterParam.get(0) instanceof String && StringUtils.isNotBlank((String) filterParam.get(0))) {

            final String textFilter = (String) filterParam.get(0);
            final Pair<String, String> orderNumberOrCustomerOrAddressOrSku = ComplexSearchUtils.checkSpecialSearch(textFilter, ORDER_OR_CUSTOMER_OR_ADDRESS_OR_SKU);
            final Pair<LocalDateTime, LocalDateTime> dateSearch = orderNumberOrCustomerOrAddressOrSku == null ? ComplexSearchUtils.checkDateRangeSearch(textFilter) : null;

            if (orderNumberOrCustomerOrAddressOrSku != null) {

                if ("*".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // If this by PK then to by PK
                    final String refNumber = orderNumberOrCustomerOrAddressOrSku.getSecond();

                    currentFilter.put("customerorderId", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(NumberUtils.toLong(refNumber.trim()))));

                } else if ("#".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // order number search
                    final String orderNumber = orderNumberOrCustomerOrAddressOrSku.getSecond();

                    SearchContext.JoinMode.OR.setMode(currentFilter);
                    currentFilter.put("ordernum", Collections.singletonList(orderNumber));
                    currentFilter.put("cartGuid", Collections.singletonList(orderNumber));

                } else if ("?".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // customer search
                    final String customer = orderNumberOrCustomerOrAddressOrSku.getSecond();

                    SearchContext.JoinMode.OR.setMode(currentFilter);
                    currentFilter.put("email", Collections.singletonList(customer));
                    currentFilter.put("firstname", Collections.singletonList(customer));
                    currentFilter.put("lastname", Collections.singletonList(customer));

                } else if ("@".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // address search
                    final String address = orderNumberOrCustomerOrAddressOrSku.getSecond();

                    SearchContext.JoinMode.OR.setMode(currentFilter);
                    currentFilter.put("billingAddress", Collections.singletonList(address));
                    currentFilter.put("shippingAddress", Collections.singletonList(address));

                } else if ("^".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {
                    // shop search
                    final String shopCode = orderNumberOrCustomerOrAddressOrSku.getSecond();

                    final List<Long> customerIds = new ArrayList<>();
                    customerService.findByCriteriaIterator(
                            " where lower(e.tag) like ?1 or lower(e.companyName1) like ?1 or lower(e.companyName2) like ?1",
                            new Object[] { HQLUtils.criteriaIlikeAnywhere(shopCode) },
                            customer -> {
                                customerIds.add(customer.getCustomerId());
                                return true; // read all
                            }
                    );

                    if (customerIds.isEmpty()) {

                        currentFilter.put("shop.code", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(shopCode)));

                    } else {

                        SearchContext.JoinMode.OR.setMode(currentFilter);
                        currentFilter.put("shop.code", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(shopCode)));
                        currentFilter.put("customer.customerId", Collections.singletonList(SearchContext.MatchMode.ANY.toParam(customerIds)));

                    }

                } else if ("!".equals(orderNumberOrCustomerOrAddressOrSku.getFirst())) {

                    final List<Long> ids = findIdsByReservation(orderNumberOrCustomerOrAddressOrSku.getSecond());

                    if (ids.isEmpty()) {
                        return new SearchResult<>(filter, Collections.emptyList(), 0);
                    }
                    currentFilter.put("customerorderId", Collections.singletonList(SearchContext.MatchMode.ANY.toParam(ids)));

                }

            } else if (dateSearch != null) {

                final LocalDateTime from = dateSearch.getFirst();
                final LocalDateTime to = dateSearch.getSecond();

                final List range = new ArrayList(2);
                if (from != null) {
                    range.add(SearchContext.MatchMode.GT.toParam(from));
                }
                if (to != null) {
                    range.add(SearchContext.MatchMode.LE.toParam(to));
                }

                currentFilter.put("orderTimestamp", range);

            } else {

                // basic search
                final String basic = textFilter;

                SearchContext.JoinMode.OR.setMode(currentFilter);
                currentFilter.put("ordernum", Collections.singletonList(basic));
                currentFilter.put("email", Collections.singletonList(basic));
                currentFilter.put("firstname", Collections.singletonList(basic));
                currentFilter.put("lastname", Collections.singletonList(basic));

            }

        }

        final CustomerOrderService customerOrderService = (CustomerOrderService) service;

        // Filter by order status only if it is not search by PK
        if (CollectionUtils.isNotEmpty(statusesParam) && !currentFilter.containsKey("customerorderId")) {
            currentFilter.put("orderStatus", statusesParam);
        }

        final int count = customerOrderService.findCustomerOrderCount(shopIds, currentFilter);
        if (count > startIndex) {

            final List<CustomerOrderDTO> entities = new ArrayList<>();
            final List<CustomerOrder> orders = customerOrderService.findCustomerOrder(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), shopIds, currentFilter);

            fillDTOs(orders, entities);

            return new SearchResult<>(filter, entities, count);

        }
        return new SearchResult<>(filter, Collections.emptyList(), count);
    }

    private List<Long> findIdsByReservation(final String sku) {

        final List<String> orderStatusThatCouldHaveReservations = Arrays.asList(
                CustomerOrder.ORDER_STATUS_WAITING,
                CustomerOrder.ORDER_STATUS_APPROVE,
                CustomerOrder.ORDER_STATUS_WAITING_PAYMENT,
                CustomerOrder.ORDER_STATUS_IN_PROGRESS,
                CustomerOrder.ORDER_STATUS_PARTIALLY_SHIPPED
        );
        final List<String> skus = Collections.singletonList(sku);
        final CustomerOrderService cos = (CustomerOrderService) service;
        final Set<Long> deliveryIds = new HashSet<>();

        deliveryIds.addAll(cos.findAwaitingDeliveriesIds(
                skus,
                CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_RESERVED,
                orderStatusThatCouldHaveReservations
        ));
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


        if (!deliveryIds.isEmpty()) {

            return cos.findCustomerOrderIdsByDeliveryIds(deliveryIds);

        }

        return Collections.emptyList();

    }

    @Override
    public Map<String, String> getOrderPgLabels(final String locale) {


        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false, "DEFAULT");

        final Map<String, String> available = new HashMap<>();

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


    @Override
    public void updateOrderExportStatus(final String lang, final long id, final boolean export) {

        final CustomerOrder order = getService().findById(id);

        order.setBlockExport(false);
        if (!export) { // unset eligibility to prevent export when unblocked
            order.setEligibleForExport(null);
        }
        for (final CustomerOrderDelivery delivery : order.getDelivery()) {
            delivery.setBlockExport(false);
            if (!export) { // unset eligibility to prevent export when unblocked
                delivery.setEligibleForExport(null);
            }
        }
        getService().update(order);

    }
}

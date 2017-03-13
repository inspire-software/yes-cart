/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo.impl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Result;
import org.yes.cart.domain.vo.*;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.service.dto.DtoPromotionService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.order.OrderFlow;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoCustomerOrderService;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 19:31
 */
public class VoCustomerOrderServiceImpl implements VoCustomerOrderService {

    private final DtoCustomerOrderService dtoCustomerOrderService;
    private final DtoPromotionService dtoPromotionService;

    private final CustomerOrderPaymentService customerOrderPaymentService;

    private final OrderFlow orderFlow;
    private final OrderFlow deliveryFlow;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    public VoCustomerOrderServiceImpl(final DtoCustomerOrderService dtoCustomerOrderService,
                                      final DtoPromotionService dtoPromotionService,
                                      final CustomerOrderPaymentService customerOrderPaymentService,
                                      final OrderFlow orderFlow,
                                      final OrderFlow deliveryFlow,
                                      final FederationFacade federationFacade,
                                      final VoAssemblySupport voAssemblySupport) {
        this.dtoCustomerOrderService = dtoCustomerOrderService;
        this.dtoPromotionService = dtoPromotionService;
        this.customerOrderPaymentService = customerOrderPaymentService;
        this.orderFlow = orderFlow;
        this.deliveryFlow = deliveryFlow;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    @Override
    public List<VoCustomerOrderInfo> getFiltered(final String lang, final String filter, final int max) throws Exception {

        final List<VoCustomerOrderInfo> results = new ArrayList<>();

        final boolean removeSupplierDetails = !isSupplierDetailsViewable();

        Map<String, String> pgNames = Collections.emptyMap();
        int start = 0;
        do {
            final List<CustomerOrderDTO> batch = dtoCustomerOrderService.findBy(filter, start, max);
            if (batch.isEmpty()) {
                break;
            } else if (start == 0) {
                pgNames = dtoCustomerOrderService.getOrderPgLabels(lang);
            }
            federationFacade.applyFederationFilter(batch, CustomerOrderDTO.class);

            for (final CustomerOrderDTO order : batch) {

                final VoCustomerOrderInfo vo =
                        voAssemblySupport.assembleVo(VoCustomerOrderInfo.class, CustomerOrderDTO.class, new VoCustomerOrderInfo(), order);

                vo.setPgName(pgNames.get(vo.getPgLabel()));
                vo.setOrderStatusNextOptions(determineOrderStatusNextOptions(vo));
                vo.setOrderPaymentStatus(determinePaymentStatus(vo));

                if (removeSupplierDetails) {
                    removeOrderDetails(vo, "SUPPLIER");
                }

                results.add(vo);
            }
            start++;
        } while (results.size() < max && max != Integer.MAX_VALUE);
        return results.size() > max ? results.subList(0, max) : results;
    }



    @Override
    public List<VoCustomerOrderInfo> getFiltered(final String lang, final String filter, final List<String> statuses, final int max) throws Exception {

        final List<VoCustomerOrderInfo> results = new ArrayList<>();

        final boolean removeSupplierDetails = !isSupplierDetailsViewable();

        Map<String, String> pgNames = Collections.emptyMap();
        int start = 0;
        do {
            final List<CustomerOrderDTO> batch = dtoCustomerOrderService.findBy(filter, statuses, start, max);
            if (batch.isEmpty()) {
                break;
            } else if (start == 0) {
                pgNames = dtoCustomerOrderService.getOrderPgLabels(lang);
            }
            federationFacade.applyFederationFilter(batch, CustomerOrderDTO.class);

            for (final CustomerOrderDTO order : batch) {

                final VoCustomerOrderInfo vo =
                        voAssemblySupport.assembleVo(VoCustomerOrderInfo.class, CustomerOrderDTO.class, new VoCustomerOrderInfo(), order);

                vo.setPgName(pgNames.get(vo.getPgLabel()));
                vo.setOrderStatusNextOptions(determineOrderStatusNextOptions(vo));
                vo.setOrderPaymentStatus(determinePaymentStatus(vo));

                if (removeSupplierDetails) {
                    removeOrderDetails(vo, "SUPPLIER");
                }

                results.add(vo);
            }
            start++;
        } while (results.size() < max && max != Integer.MAX_VALUE);
        return results.size() > max ? results.subList(0, max) : results;
    }


    private String determinePaymentStatus(final VoCustomerOrderInfo vo) {
        if (MoneyUtils.isFirstBiggerThanSecond(vo.getOrderTotal(), BigDecimal.ZERO)) {
            if (MoneyUtils.isFirstBiggerThanSecond(vo.getOrderPaymentBalance(), vo.getOrderTotal())) {
                // more payments than order total
                return "pt.refund.pending";
            } else if (MoneyUtils.isFirstBiggerThanSecond(vo.getOrderTotal(), vo.getOrderPaymentBalance())) {
                if (MoneyUtils.isFirstBiggerThanSecond(vo.getOrderPaymentBalance(), BigDecimal.ZERO)) {
                    // less payments than order total
                    return "pt.partial";
                }
                if (CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT.equals(vo.getOrderStatus()) ||
                        CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT.equals(vo.getOrderStatus())) {
                    // more payments than order total
                    return "pt.refund.pending";
                } else if (CustomerOrder.ORDER_STATUS_CANCELLED.equals(vo.getOrderStatus()) ||
                        CustomerOrder.ORDER_STATUS_RETURNED.equals(vo.getOrderStatus())) {
                    // no balance, no payment
                    return "pt.none";
                }
                // no payments
                return "pt.pending";
            }
            if (CustomerOrder.ORDER_STATUS_CANCELLED_WAITING_PAYMENT.equals(vo.getOrderStatus()) ||
                    CustomerOrder.ORDER_STATUS_RETURNED_WAITING_PAYMENT.equals(vo.getOrderStatus())) {
                // more payments than order total
                return "pt.refund.pending";
            }
            // exact payment
            return "pt.full";
        } else if (MoneyUtils.isFirstBiggerThanSecond(vo.getOrderPaymentBalance(), BigDecimal.ZERO)) {
            // payments on zero order
            return "pt.refund.pending";
        }
        // no balance, no payment
        return "pt.none";

    }

    private boolean isSupplierDetailsViewable() {
        return federationFacade.isCurrentUserSystemAdmin() || federationFacade.isCurrentUser("ROLE_SMSHOPADMIN");
    }

    private void removeOrderDetails(final VoCustomerOrderInfo vo, final String displayValue) {
        final Iterator<MutablePair<String, MutablePair<String, String>>> it = vo.getAllValues().iterator();
        while (it.hasNext()) {
            final MutablePair<String, MutablePair<String, String>> next = it.next();
            if (displayValue.equals(next.getSecond().getSecond())) {
                it.remove();
            }
        }
    }

    private void removeAllDetails(final VoCustomerOrder vo, final String displayValue) {

        removeOrderDetails(vo, displayValue);

        for (final VoCustomerOrderLine line : vo.getLines()) {
            removeLineDetails(line, displayValue);
        }

    }

    private void removeLineDetails(final VoCustomerOrderLine vo, final String displayValue) {
        final Iterator<MutablePair<String, MutablePair<String, String>>> it = vo.getAllValues().iterator();
        while (it.hasNext()) {
            final MutablePair<String, MutablePair<String, String>> next = it.next();
            if (displayValue.equals(next.getSecond().getSecond())) {
                it.remove();
            }
        }
    }

    private List<String> determineOrderStatusNextOptions(final VoCustomerOrderInfo vo) {
        return orderFlow.getNext(vo.getPgLabel(), vo.getOrderStatus());
    }

    private List<String> determineOrderStatusNextOptions(final VoCustomerOrderDeliveryInfo vo) {
        return deliveryFlow.getNext(vo.getPgLabel(), vo.getDeliveryStatus());
    }

    @Override
    public VoCustomerOrder getById(final String lang, final long orderId) throws Exception {

        if (federationFacade.isManageable(orderId, CustomerOrderDTO.class)) {

            final CustomerOrderDTO order = dtoCustomerOrderService.getById(orderId);

            final Map<String, String> pgNames = dtoCustomerOrderService.getOrderPgLabels(lang);
            final VoCustomerOrder vo = voAssemblySupport.assembleVo(VoCustomerOrder.class, CustomerOrderDTO.class, new VoCustomerOrder(), order);
            vo.setPgName(pgNames.get(vo.getPgLabel()));
            vo.setOrderStatusNextOptions(determineOrderStatusNextOptions(vo));
            vo.setOrderPaymentStatus(determinePaymentStatus(vo));

            final List<CustomerOrderDeliveryDTO> deliveries = dtoCustomerOrderService.findDeliveryByOrderNumber(order.getOrdernum());
            vo.setDeliveries(voAssemblySupport.assembleVos(VoCustomerOrderDeliveryInfo.class, CustomerOrderDeliveryDTO.class, deliveries));

            final List<CustomerOrderDeliveryDetailDTO> lines = dtoCustomerOrderService.findDeliveryDetailsByOrderNumber(order.getOrdernum());
            vo.setLines(voAssemblySupport.assembleVos(VoCustomerOrderLine.class, CustomerOrderDeliveryDetailDTO.class, lines));

            final Set<String> promoCodes = new HashSet<>();
            if (CollectionUtils.isNotEmpty(vo.getAppliedPromo())) {
                promoCodes.addAll(vo.getAppliedPromo());
            }
            for (final VoCustomerOrderDeliveryInfo vod : vo.getDeliveries()) {
                if (CollectionUtils.isNotEmpty(vod.getAppliedPromo())) {
                    promoCodes.addAll(vod.getAppliedPromo());
                }
                vod.setDeliveryStatusNextOptions(determineOrderStatusNextOptions(vod));
            }
            for (final VoCustomerOrderLine vol : vo.getLines()) {
                if (CollectionUtils.isNotEmpty(vol.getAppliedPromo())) {
                    promoCodes.addAll(vol.getAppliedPromo());
                }
            }

            if (!promoCodes.isEmpty()) {
                final List<PromotionDTO> promotions = dtoPromotionService.findByCodes(promoCodes);
                vo.setPromotions(voAssemblySupport.assembleVos(VoPromotion.class, PromotionDTO.class, promotions));
            } else {
                vo.setPromotions(Collections.EMPTY_LIST);
            }

            vo.setPayments(voAssemblySupport.assembleVos(VoPayment.class, CustomerOrderPayment.class, customerOrderPaymentService.findBy(vo.getOrdernum(), null, (String) null, (String) null)));

            final boolean removeSupplierDetails = !isSupplierDetailsViewable();
            if (removeSupplierDetails) {
                removeAllDetails(vo, "SUPPLIER");
            }

            return vo;

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    @Override
    public VoCustomerOrderTransitionResult transitionOrder(final String transition, final String ordernum, final String message) throws Exception {

        if (federationFacade.isManageable(ordernum, CustomerOrderDTO.class)) {

            final Result result = this.orderFlow.getAction(transition).doTransition(ordernum, message);
            return voAssemblySupport.assembleVo(VoCustomerOrderTransitionResult.class, Result.class, new VoCustomerOrderTransitionResult(), result);

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }

    @Override
    public VoCustomerOrderTransitionResult transitionDelivery(final String transition, final String ordernum, final String deliverynum, final String message) throws Exception {

        if (federationFacade.isManageable(ordernum, CustomerOrderDTO.class)) {

            final Map<String, String> params = new HashMap<>();
            params.put("ordernum", ordernum);
            params.put("message", message);

            final Result result = this.deliveryFlow.getAction(transition).doTransition(deliverynum, params);
            return voAssemblySupport.assembleVo(VoCustomerOrderTransitionResult.class, Result.class, new VoCustomerOrderTransitionResult(), result);

        } else {
            throw new AccessDeniedException("Access is denied");
        }

    }
}

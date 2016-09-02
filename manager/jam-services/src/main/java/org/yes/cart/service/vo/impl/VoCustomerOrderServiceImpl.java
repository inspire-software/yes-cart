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

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.domain.misc.Result;
import org.yes.cart.domain.vo.VoCustomerOrderInfo;
import org.yes.cart.domain.vo.VoCustomerOrderTransitionResult;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.order.OrderFlow;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoCustomerOrderService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 19:31
 */
public class VoCustomerOrderServiceImpl implements VoCustomerOrderService {

    private final DtoCustomerOrderService dtoCustomerOrderService;

    private final OrderFlow orderFlow;
    private final OrderFlow deliveryFlow;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    public VoCustomerOrderServiceImpl(final DtoCustomerOrderService dtoCustomerOrderService,
                                      final OrderFlow orderFlow,
                                      final OrderFlow deliveryFlow,
                                      final FederationFacade federationFacade,
                                      final VoAssemblySupport voAssemblySupport) {
        this.dtoCustomerOrderService = dtoCustomerOrderService;
        this.orderFlow = orderFlow;
        this.deliveryFlow = deliveryFlow;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    @Override
    public List<VoCustomerOrderInfo> getFiltered(final String lang, final String filter, final int max) throws Exception {

        final List<VoCustomerOrderInfo> results = new ArrayList<>();

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

                results.add(vo);
            }
            start += max;
        } while (results.size() < max);
        return results.size() > max ? results.subList(0, max) : results;
    }

    private List<String> determineOrderStatusNextOptions(final VoCustomerOrderInfo vo) {
        return orderFlow.getNext(vo.getPgLabel(), vo.getOrderStatus());
    }

    @Override
    public VoCustomerOrderInfo getById(final String lang, final long orderId) throws Exception {

        if (federationFacade.isManageable(orderId, CustomerOrderDTO.class)) {

            final CustomerOrderDTO dto = dtoCustomerOrderService.getById(orderId);
            final Map<String, String> pgNames = dtoCustomerOrderService.getOrderPgLabels(lang);
            final VoCustomerOrderInfo vo = voAssemblySupport.assembleVo(VoCustomerOrderInfo.class, CustomerOrderDTO.class, new VoCustomerOrderInfo(), dto);
            vo.setPgName(pgNames.get(vo.getPgLabel()));
            vo.setOrderStatusNextOptions(determineOrderStatusNextOptions(vo));

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
}

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

import org.yes.cart.domain.vo.VoPayment;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.service.DtoCustomerOrderPaymentService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoPaymentService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 05/09/2016
 * Time: 18:15
 */
public class VoPaymentServiceImpl implements VoPaymentService {

    private final DtoCustomerOrderPaymentService dtoCustomerOrderPaymentService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;


    public VoPaymentServiceImpl(final DtoCustomerOrderPaymentService dtoCustomerOrderPaymentService,
                                final FederationFacade federationFacade,
                                final VoAssemblySupport voAssemblySupport) {
        this.dtoCustomerOrderPaymentService = dtoCustomerOrderPaymentService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }

    @Override
    public List<VoPayment> getFiltered(final String filter, final int max) throws Exception {

        final List<VoPayment> results = new ArrayList<>();

        int start = 0;
        do {
            final List<CustomerOrderPayment> batch = dtoCustomerOrderPaymentService.findBy(filter, start, max);
            if (batch.isEmpty()) {
                break;
            }
            federationFacade.applyFederationFilter(batch, CustomerOrderPayment.class);

            results.addAll(voAssemblySupport.assembleVos(VoPayment.class, CustomerOrderPayment.class, batch));
            start++;
        } while (results.size() < max && max != Integer.MAX_VALUE);
        return results.size() > max ? results.subList(0, max) : results;
    }

    @Override
    public List<VoPayment> getFiltered(final String filter, final List<String> operations, final List<String> statuses, final int max) throws Exception {

        final List<VoPayment> results = new ArrayList<>();

        int start = 0;
        do {
            final List<CustomerOrderPayment> batch = dtoCustomerOrderPaymentService.findBy(filter, operations, statuses, start, max);
            if (batch.isEmpty()) {
                break;
            }
            federationFacade.applyFederationFilter(batch, CustomerOrderPayment.class);

            results.addAll(voAssemblySupport.assembleVos(VoPayment.class, CustomerOrderPayment.class, batch));
            start++;
        } while (results.size() < max && max != Integer.MAX_VALUE);
        return results.size() > max ? results.subList(0, max) : results;
    }
}

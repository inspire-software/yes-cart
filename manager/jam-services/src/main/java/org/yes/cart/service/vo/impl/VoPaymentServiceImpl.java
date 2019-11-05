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
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.domain.vo.VoPayment;
import org.yes.cart.domain.vo.VoSearchContext;
import org.yes.cart.domain.vo.VoSearchResult;
import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.payment.service.DtoCustomerOrderPaymentService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoPaymentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public VoSearchResult<VoPayment> getFilteredPayments(final VoSearchContext filter) throws Exception {


        final VoSearchResult<VoPayment> result = new VoSearchResult<>();
        final List<VoPayment> results = new ArrayList<>();
        result.setSearchContext(filter);
        result.setItems(results);

        Set<String> shopCodes = null;
        if (!federationFacade.isCurrentUserSystemAdmin()) {
            shopCodes = federationFacade.getAccessibleShopCodesByCurrentManager();
            if (CollectionUtils.isEmpty(shopCodes)) {
                return result;
            }
        }

        final SearchContext searchContext = new SearchContext(
                filter.getParameters(),
                filter.getStart(),
                Math.min(filter.getSize(), 100),
                filter.getSortBy(),
                filter.isSortDesc(),
                "filter", "statuses", "operations"
        );

        final SearchResult<CustomerOrderPayment> batch = dtoCustomerOrderPaymentService.findPayments(shopCodes, searchContext);

        if (!batch.getItems().isEmpty()) {

            results.addAll(voAssemblySupport.assembleVos(VoPayment.class, CustomerOrderPayment.class, batch.getItems()));

        }

        result.setTotal(batch.getTotal());

        return result;

    }
}

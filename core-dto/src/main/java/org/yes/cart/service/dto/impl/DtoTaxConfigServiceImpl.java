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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.dto.TaxConfigDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.TaxConfigDTOImpl;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.domain.entity.TaxConfig;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.TaxConfigService;
import org.yes.cart.service.dto.DtoTaxConfigService;

import java.util.*;

/**
 * User: denispavlov
 * Date: 29/10/2014
 * Time: 11:01
 */
public class DtoTaxConfigServiceImpl
    extends AbstractDtoServiceImpl<TaxConfigDTO, TaxConfigDTOImpl, TaxConfig>
        implements DtoTaxConfigService {


    private final GenericService<Tax> taxService;

    /**
     * Construct base dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param taxConfigGenericService  {@link org.yes.cart.service.domain.GenericService}
     * @param taxGenericService  {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoTaxConfigServiceImpl(final DtoFactory dtoFactory,
                                   final GenericService<TaxConfig> taxConfigGenericService,
                                   final GenericService<Tax> taxGenericService,
                                   final AdaptersRepository adaptersRepository) {
        super(dtoFactory, taxConfigGenericService, adaptersRepository);
        this.taxService = taxGenericService;
    }

    private final static char[] LOCATION = new char[] { '@' };
    private final static char[] SKU = new char[] { '#', '!' };
    static {
        Arrays.sort(LOCATION);
        Arrays.sort(SKU);
    }

    @Override
    public SearchResult<TaxConfigDTO> findTaxConfigs(final long taxId, final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter");
        final List filterParam = params.get("filter");

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final TaxConfigService taxConfigService = (TaxConfigService) service;

        if (taxId > 0L) {

            final Map<String, List> currentFilter = new HashMap<>();
            if (CollectionUtils.isNotEmpty(filterParam) && filterParam.get(0) instanceof String && StringUtils.isNotBlank((String) filterParam.get(0))) {

                final String textFilter = ((String) filterParam.get(0)).trim();

                if (StringUtils.isNotBlank(textFilter)) {

                    final Pair<String, String> locationSearch = ComplexSearchUtils.checkSpecialSearch(textFilter, LOCATION);
                    final Pair<String, String> skuSearch = locationSearch != null ? null : ComplexSearchUtils.checkSpecialSearch(textFilter, SKU);

                    if (locationSearch != null) {

                        final String location = locationSearch.getSecond();

                        SearchContext.JoinMode.OR.setMode(currentFilter);
                        currentFilter.put("countryCode", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(location)));
                        currentFilter.put("stateCode", Collections.singletonList(location));

                    } else if (skuSearch != null) {

                        final String sku = skuSearch.getSecond();

                        if ("!".equals(sku)) {

                            SearchContext.JoinMode.OR.setMode(currentFilter);
                            currentFilter.put("productCode", Collections.singletonList(SearchContext.MatchMode.EMPTY.toParam(null)));
                            currentFilter.put("productCode", Collections.singletonList(SearchContext.MatchMode.NULL.toParam(null)));

                        } else {

                            if ("!".equals(skuSearch.getFirst())) {
                                currentFilter.put("productCode", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(sku)));
                            } else {
                                currentFilter.put("productCode", Collections.singletonList(sku));
                            }

                        }

                    } else {

                        SearchContext.JoinMode.OR.setMode(currentFilter);
                        currentFilter.put("countryCode", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(textFilter)));
                        currentFilter.put("stateCode", Collections.singletonList(textFilter));
                        currentFilter.put("productCode", Collections.singletonList(textFilter));

                    }


                }

            }


            currentFilter.put("taxIds", Collections.singletonList(taxId));


            final int count = taxConfigService.findTaxConfigCount(currentFilter);
            if (count > startIndex) {

                final List<TaxConfigDTO> entities = new ArrayList<>();
                final List<TaxConfig> configs = taxConfigService.findTaxConfigs(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), currentFilter);

                fillDTOs(configs, entities);

                return new SearchResult<>(filter, entities, count);

            }

        }
        return new SearchResult<>(filter, Collections.emptyList(), 0);

    }

    @Override
    protected void createPostProcess(final TaxConfigDTO dto, final TaxConfig entity) {
        entity.setTax(taxService.findById(dto.getTaxId()));
    }

    /** {@inheritDoc} */
    @Override
    public Class<TaxConfigDTO> getDtoIFace() {
        return TaxConfigDTO.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<TaxConfigDTOImpl> getDtoImpl() {
        return TaxConfigDTOImpl.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<TaxConfig> getEntityIFace() {
        return TaxConfig.class;
    }
}

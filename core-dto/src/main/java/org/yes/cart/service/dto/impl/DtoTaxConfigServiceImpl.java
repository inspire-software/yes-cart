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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.domain.dto.TaxConfigDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.TaxConfigDTOImpl;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.domain.entity.TaxConfig;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.TaxConfigService;
import org.yes.cart.service.dto.DtoTaxConfigService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: denispavlov
 * Date: 29/10/2014
 * Time: 11:01
 */
public class DtoTaxConfigServiceImpl
    extends AbstractDtoServiceImpl<TaxConfigDTO, TaxConfigDTOImpl, TaxConfig>
        implements DtoTaxConfigService {


    private final ShopService shopService;
    private final GenericService<Tax> taxService;

    /**
     * Construct base dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param taxConfigGenericService  {@link org.yes.cart.service.domain.GenericService}
     * @param shopService              {@link org.yes.cart.service.domain.ShopService}
     * @param taxGenericService  {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoTaxConfigServiceImpl(final DtoFactory dtoFactory,
                                   final GenericService<TaxConfig> taxConfigGenericService,
                                   final AdaptersRepository adaptersRepository,
                                   final GenericService<Tax> taxGenericService,
                                   final ShopService shopService) {
        super(dtoFactory, taxConfigGenericService, adaptersRepository);
        this.taxService = taxGenericService;
        this.shopService = shopService;
    }

    private final static char[] LOCATION = new char[] { '@' };
    private final static char[] SKU = new char[] { '#', '!' };
    static {
        Arrays.sort(LOCATION);
        Arrays.sort(SKU);
    }

    @Override
    public SearchResult<TaxConfigDTO> findTaxConfigs(final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter", "taxIds", "shopCode", "currency");
        final String textFilter = FilterSearchUtils.getStringFilter(params.get("filter"));
        final List taxIds = params.containsKey("taxIds") ? (List) params.get("taxIds").stream().map(id -> NumberUtils.toLong(String.valueOf(id))).collect(Collectors.toList()) : null;
        final String shopCode = FilterSearchUtils.getStringFilter(params.get("shopCode"));
        final String currency = FilterSearchUtils.getStringFilter(params.get("currency"));

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final TaxConfigService taxConfigService = (TaxConfigService) service;

        if ((StringUtils.isNotBlank(shopCode) && StringUtils.isNotBlank(currency)) || taxIds != null) {

            final Map<String, List> currentFilter = new HashMap<>();
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


            if (taxIds == null) {
                final Shop currentShop = shopService.getShopByCode(shopCode);
                if (currentShop == null) {
                    return new SearchResult<>(filter, Collections.emptyList(), 0);
                }
                if (currentShop.getMaster() != null) {
                    currentFilter.put("shopCode", Collections.singletonList(currentShop.getMaster().getCode()));
                } else {
                    currentFilter.put("shopCode", Collections.singletonList(shopCode));
                }
                currentFilter.put("currency", Collections.singletonList(currency));
            } else {
                currentFilter.put("taxIds", taxIds);
            }

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

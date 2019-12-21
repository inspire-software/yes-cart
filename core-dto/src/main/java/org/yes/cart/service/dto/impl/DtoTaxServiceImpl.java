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
import org.yes.cart.domain.dto.TaxDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.TaxDTOImpl;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.TaxService;
import org.yes.cart.service.dto.DtoTaxService;
import org.yes.cart.utils.HQLUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 * Date: 29/10/2014
 * Time: 10:55
 */
public class DtoTaxServiceImpl
    extends AbstractDtoServiceImpl<TaxDTO, TaxDTOImpl, Tax>
        implements DtoTaxService {

    private final ShopService shopService;

    /**
     * Construct base dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param taxGenericService        {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository       {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     * @param shopService              shop service
     */
    public DtoTaxServiceImpl(final DtoFactory dtoFactory,
                             final GenericService<Tax> taxGenericService,
                             final AdaptersRepository adaptersRepository,
                             final ShopService shopService) {
        super(dtoFactory, taxGenericService, adaptersRepository);
        this.shopService = shopService;
    }

    private final static char[] RATE = new char[] { '%' };
    private final static char[] EXCLUSIVE_INCLUSIVE = new char[] { '-', '+' };
    static {
        Arrays.sort(RATE);
        Arrays.sort(EXCLUSIVE_INCLUSIVE);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResult<TaxDTO> findTaxes(final String shopCode, final String currency, final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter", "types", "actions");
        final List filterParam = params.get("filter");

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final TaxService taxService = (TaxService) service;

        if (StringUtils.isNotBlank(shopCode) && StringUtils.isNotBlank(currency)) {
            // only allow lists for shop+currency selection
            final Map<String, List> currentFilter = new HashMap<>();
            if (CollectionUtils.isNotEmpty(filterParam) && filterParam.get(0) instanceof String && StringUtils.isNotBlank((String) filterParam.get(0))) {

                final String textFilter = ((String) filterParam.get(0)).trim();

                if (StringUtils.isNotBlank(textFilter)) {

                    final Pair<String, String> exclOrIncSearch = ComplexSearchUtils.checkSpecialSearch(textFilter, EXCLUSIVE_INCLUSIVE);
                    final Pair<String, BigDecimal> rateSearch = ComplexSearchUtils.checkNumericSearch(exclOrIncSearch != null ? textFilter.substring(1) : textFilter, RATE, 2);

                    if (exclOrIncSearch != null) {

                        currentFilter.put("exclusiveOfPrice", Collections.singletonList("+".equals(exclOrIncSearch.getFirst())));

                        final boolean all = exclOrIncSearch.getFirst().equals(exclOrIncSearch.getSecond().substring(0, 1));

                        if (!all) {

                            if (rateSearch != null) {

                                currentFilter.put("taxRate", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(rateSearch.getSecond())));

                            } else {

                                final String search = exclOrIncSearch.getSecond();

                                SearchContext.JoinMode.OR.setMode(currentFilter);
                                currentFilter.put("code", Collections.singletonList(search));
                                currentFilter.put("description", Collections.singletonList(search));

                            }

                        }

                    } else {

                        if (rateSearch != null) {

                            currentFilter.put("taxRate", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(rateSearch.getSecond())));

                        } else {

                            SearchContext.JoinMode.OR.setMode(currentFilter);
                            currentFilter.put("code", Collections.singletonList(textFilter));
                            currentFilter.put("description", Collections.singletonList(textFilter));

                        }
                    }

                }

            }

            final Shop currentShop = shopService.getShopByCode(shopCode);
            if (currentShop == null) {
                return new SearchResult<>(filter, Collections.emptyList(), 0);
            }
            if (currentShop.getMaster() != null) {
                currentFilter.put("shopCodes", Collections.singletonList(currentShop.getMaster().getCode()));
            } else {
                currentFilter.put("shopCodes", Collections.singletonList(shopCode));
            }
            currentFilter.put("currencies", Collections.singletonList(currency));

            final int count = taxService.findTaxCount(currentFilter);
            if (count > startIndex) {

                final List<TaxDTO> entities = new ArrayList<>();
                final List<Tax> taxes = taxService.findTaxes(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), currentFilter);

                fillDTOs(taxes, entities);

                return new SearchResult<>(filter, entities, count);

            }

        }

        return new SearchResult<>(filter, Collections.emptyList(), 0);
    }

    /** {@inheritDoc} */
    @Override
    public List<TaxDTO> findBy(final String shopCode, final String currency, final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<TaxDTO> dtos = new ArrayList<>();


        if (StringUtils.isNotBlank(shopCode) && StringUtils.isNotBlank(currency)) {
            // only allow lists for shop+currency selection

            List<Tax> entities;

            if (StringUtils.isNotBlank(filter)) {

                final Pair<String, String> exclOrIncSearch = ComplexSearchUtils.checkSpecialSearch(filter, EXCLUSIVE_INCLUSIVE);
                final Pair<String, BigDecimal> rateSearch = ComplexSearchUtils.checkNumericSearch(exclOrIncSearch != null ? filter.substring(1) : filter, RATE, 2);

                if (exclOrIncSearch != null) {

                    final boolean all = exclOrIncSearch.getFirst().equals(exclOrIncSearch.getSecond().substring(0, 1));

                    if (all) {

                        entities = getService().getGenericDao().findRangeByCriteria(
                                " where e.shopCode = ?1 and e.currency = ?2 and e.exclusiveOfPrice = ?3 order by e.code",
                                page * pageSize, pageSize,
                                shopCode,
                                currency,
                                "+".equals(exclOrIncSearch.getFirst())
                        );

                    } else {

                        if (rateSearch != null) {

                            entities = getService().getGenericDao().findRangeByCriteria(
                                    " where e.shopCode = ?1 and e.currency = ?2 and e.exclusiveOfPrice = ?3 and e.taxRate = ?4 order by e.code",
                                    page * pageSize, pageSize,
                                    shopCode,
                                    currency,
                                    "+".equals(exclOrIncSearch.getFirst()),
                                    rateSearch.getSecond()
                            );

                        } else {

                            final String search = exclOrIncSearch.getSecond();

                            entities = getService().getGenericDao().findRangeByCriteria(
                                    " where e.shopCode = ?1 and e.currency = ?2 and e.exclusiveOfPrice = ?3 and (lower(e.code) like ?4 or lower(e.description) like ?4) order by e.code",
                                    page * pageSize, pageSize,
                                    shopCode,
                                    currency,
                                    "+".equals(exclOrIncSearch.getFirst()),
                                    HQLUtils.criteriaIlikeAnywhere(search)
                            );

                        }

                    }

                } else {

                    if (rateSearch != null) {
                        entities = getService().getGenericDao().findRangeByCriteria(
                                " where e.shopCode = ?1 and e.currency = ?2 and e.taxRate = ?3 order by e.code",
                                page * pageSize, pageSize,
                                shopCode,
                                currency,
                                rateSearch.getSecond()
                        );
                    } else {
                        entities = getService().getGenericDao().findRangeByCriteria(
                                " where e.shopCode = ?1 and e.currency = ?2 and (lower(e.code) like ?3 or lower(e.description) like ?3) order by e.code",
                                page * pageSize, pageSize,
                                shopCode,
                                currency,
                                HQLUtils.criteriaIlikeAnywhere(filter)
                        );
                    }
                }

            } else {

                entities = getService().getGenericDao().findRangeByCriteria(
                        " where e.shopCode = ?1 and e.currency = ?2 order by e.code",
                        page * pageSize, pageSize,
                        shopCode,
                        currency
                );

            }

            fillDTOs(entities, dtos);
        }

        return dtos;
    }

    /** {@inheritDoc} */
    @Override
    public Class<TaxDTO> getDtoIFace() {
        return TaxDTO.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<TaxDTOImpl> getDtoImpl() {
        return TaxDTOImpl.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<Tax> getEntityIFace() {
        return Tax.class;
    }


}

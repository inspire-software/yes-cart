/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Tax;
import org.yes.cart.domain.entity.TaxConfig;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.TaxConfigService;
import org.yes.cart.service.domain.TaxService;
import org.yes.cart.utils.HQLUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: denispavlov
 * Date: 27/10/2014
 * Time: 20:08
 */
public class TaxConfigServiceImpl extends BaseGenericServiceImpl<TaxConfig> implements TaxConfigService {

    private final TaxService taxService;

    public TaxConfigServiceImpl(final GenericDAO<TaxConfig, Long> genericDao,
                                final TaxService taxService) {
        super(genericDao);
        this.taxService = taxService;
    }

    /** {@inheritDoc} */
    @Override
    public Long getTaxIdBy(final String shopCode, final String currency, final String countryCode, final String stateCode, final String productCode) {

        final List<Tax> shopTaxes = taxService.getTaxesByShopCode(shopCode, currency);
        if (shopTaxes.isEmpty()) {
            return null;
        }

        final List<TaxConfig> taxConfigs = getGenericDao().findByNamedQuery("TAXCONFIG.BY.COUNTRYCODE.STATECODE.PRODUCTCODE.IN.TAXES", shopTaxes, countryCode, stateCode, productCode);
        if (taxConfigs.isEmpty()) {
            return null;
        }

        taxConfigs.sort(PRIORITY);

        return taxConfigs.get(0).getTax().getTaxId();

    }

    /**
     * The sorting rule:
     * 1. Product state specific tax
     * 2. Product country specific tax
     * 3. Product specific tax
     * 4. State specific tax
     * 5. Country specific tax
     * 6. Shop specific tax
     */
    static final Comparator<TaxConfig> PRIORITY = new Comparator<TaxConfig>() {

        private int UP = -1;
        private int DOWN = 1;

        @Override
        public int compare(final TaxConfig tc1, final TaxConfig tc2) {

            final String tc1country = StringUtils.isEmpty(tc1.getCountryCode()) ? null : tc1.getCountryCode();
            final String tc1state = StringUtils.isEmpty(tc1.getStateCode()) ? null : tc1.getStateCode();
            final String tc1product = StringUtils.isEmpty(tc1.getProductCode()) ? null : tc1.getProductCode();

            final String tc2country = StringUtils.isEmpty(tc2.getCountryCode()) ? null : tc2.getCountryCode();
            final String tc2state = StringUtils.isEmpty(tc2.getStateCode()) ? null : tc2.getStateCode();
            final String tc2product = StringUtils.isEmpty(tc2.getProductCode()) ? null : tc2.getProductCode();


            if (tc1product != null) { // product specific tax
                if (tc1state != null) { // product state specific tax
                    return UP; // should be only 1 product state specific tax
                } else if (tc1country != null) { // product country specific tax
                    // product state specific tax wins
                    return tc2product != null && tc2state != null ? DOWN : UP;
                } // else product shop specific tax
                return tc2product != null && (tc2country != null || tc2state != null) ? DOWN : UP;
            } else if (tc1state != null) { // state specific tax
                if (tc2product != null) {
                    return DOWN; // product specific tax is higher up
                }
                return UP; // should be only 1 state specific tax
            } else if (tc1country != null) { // country specific tax
                if (tc2product != null || tc2state != null) {
                    return DOWN; // product specific or state specific tax is higher up
                }
                return UP;
            } // else shop specific (all nulls)
            return DOWN;
        }
    };




    private Pair<String, Object[]> findTaxConfigQuery(final boolean count,
                                                      final String sort,
                                                      final boolean sortDescending,
                                                      final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(c.taxConfigId) from TaxConfigEntity c ");
        } else {
            hqlCriteria.append("select c from TaxConfigEntity c ");
        }

        final List shopCode = currentFilter != null ? currentFilter.remove("shopCode") : null;
        if (shopCode != null) {
            final List currency = currentFilter.remove("currency");
            final List<Tax> shopTaxes = taxService.getTaxesByShopCode((String) shopCode.get(0), (String) currency.get(0));
            final List<Long> shopTaxIds;
            if (!shopTaxes.isEmpty()) {
                shopTaxIds = shopTaxes.stream().map(Tax::getTaxId).collect(Collectors.toList());
            } else {
                shopTaxIds = Collections.singletonList(0L);
            }
            hqlCriteria.append(" where (c.tax.taxId in (?1)) ");
            params.add(shopTaxIds);
        }

        final List taxIds = currentFilter != null ? currentFilter.remove("taxIds") : null;
        if (taxIds != null) {
            if (params.isEmpty()) {
                hqlCriteria.append(" where (c.tax.taxId in (?").append(params.size() + 1).append(")) ");
            } else {
                hqlCriteria.append(" and (c.tax.taxId in (?").append(params.size() + 1).append(")) ");
            }
            params.add(taxIds);
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "c", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by c." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }



    /** {@inheritDoc} */
    @Override
    public List<TaxConfig> findTaxConfigs(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findTaxConfigQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /** {@inheritDoc} */
    @Override
    public int findTaxConfigCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findTaxConfigQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }

    private void cleanRegionalTaxCodes(final TaxConfig entity) {
        if (entity.getCountryCode() != null && StringUtils.isBlank(entity.getCountryCode())) {
            entity.setCountryCode(null);
        }
        if (entity.getStateCode() != null && StringUtils.isBlank(entity.getStateCode())) {
            entity.setStateCode(null);
        }
        if (entity.getProductCode() != null && StringUtils.isBlank(entity.getProductCode())) {
            entity.setProductCode(null);
        }
    }

    private void regenerateGuid(final TaxConfig entity) {
        final StringBuilder guid = new StringBuilder();
        if (entity.getTax() != null) {
            guid.append(entity.getTax().getGuid());
        }
        if (entity.getCountryCode() != null) {
            guid.append('_').append(entity.getCountryCode());
        }
        if (entity.getStateCode() != null) {
            guid.append('_').append(entity.getStateCode());
        }
        if (entity.getProductCode() != null) {
            guid.append('_').append(entity.getProductCode());
        }
        entity.setGuid(guid.toString());
    }

    /** {@inheritDoc} */
    @Override
    public TaxConfig create(final TaxConfig instance) {
        cleanRegionalTaxCodes(instance);
        regenerateGuid(instance);
        return super.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    public TaxConfig update(final TaxConfig instance) {
        cleanRegionalTaxCodes(instance);
        regenerateGuid(instance);
        return super.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final TaxConfig instance) {
        super.delete(instance);
    }
}

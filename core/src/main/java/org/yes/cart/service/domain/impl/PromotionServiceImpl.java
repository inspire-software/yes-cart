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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.utils.TimeContext;
import org.yes.cart.utils.HQLUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-19
 * Time: 11:08 PM
 */
public class PromotionServiceImpl extends BaseGenericServiceImpl<Promotion> implements PromotionService {

    public PromotionServiceImpl(final GenericDAO<Promotion, Long> genericDao) {
        super(genericDao);
    }

    /** {@inheritDoc} */
    @Override
    public List<Promotion> getPromotionsByShopCode(final String shopCode, final String currency, final boolean active) {
        if (active) {
            final LocalDateTime now = now();
            return getGenericDao().findByNamedQuery("PROMOTION.BY.SHOPCODE.CURRENCY.ACTIVE", shopCode, currency, active, now, now);
        }
        return getGenericDao().findByNamedQuery("PROMOTION.BY.SHOPCODE.CURRENCY", shopCode, currency);
    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promotion findPromotionByCode(final String code, final boolean active) {

        final Promotion promotion = getGenericDao().findSingleByCriteria(" where e.code = ?1", code);
        if (promotion != null && promotion.isAvailable(now())) {
            return promotion;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findPromotionIdByCode(final String code) {
        List<Object> list = getGenericDao().findQueryObjectByNamedQuery("PROMOTION.ID.BY.CODE", code);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }



    private Pair<String, Object[]> findPromotionQuery(final boolean count,
                                                      final String sort,
                                                      final boolean sortDescending,
                                                      final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(p.promotionId) from PromotionEntity p ");
        } else {
            hqlCriteria.append("select p from PromotionEntity p ");
        }

        final List shopIds = currentFilter != null ? currentFilter.remove("shopCodes") : null;
        if (shopIds != null) {
            hqlCriteria.append(" where (p.shopCode in (?1)) ");
            params.add(shopIds);
        }
        final List currencies = currentFilter != null ? currentFilter.remove("currencies") : null;
        if (currencies != null) {
            if (params.isEmpty()) {
                hqlCriteria.append(" where (p.currency in (?").append(params.size() + 1).append(")) ");
            } else {
                hqlCriteria.append(" and (p.currency in (?").append(params.size() + 1).append(")) ");
            }
            params.add(currencies);
        }

        final List promoTypes = currentFilter != null ? currentFilter.remove("promoTypes") : null;
        if (promoTypes != null) {
            if (params.isEmpty()) {
                hqlCriteria.append(" where (p.promoType in (?").append(params.size() + 1).append(")) ");
            } else {
                hqlCriteria.append(" and (p.promoType in (?").append(params.size() + 1).append(")) ");
            }
            params.add(promoTypes);
        }

        final List promoActions = currentFilter != null ? currentFilter.remove("promoActions") : null;
        if (promoActions != null) {
            if (params.isEmpty()) {
                hqlCriteria.append(" where (p.promoAction in (?").append(params.size() + 1).append(")) ");
            } else {
                hqlCriteria.append(" and (p.promoAction in (?").append(params.size() + 1).append(")) ");
            }
            params.add(promoActions);
        }

        final List active = currentFilter != null ? currentFilter.remove("active") : null;
        if (active != null) {
            final Boolean isActive = (Boolean) active.get(0);
            final StringBuilder activeCriteria = new StringBuilder();
            if (isActive) {
                activeCriteria
                        .append(" (p.enabled in ?").append(params.size() + 1).append(" and ")
                        .append(" (p.enabledFrom is null or p.enabledFrom <= ?").append(params.size() + 2).append(") and ")
                        .append(" (p.enabledTo is null or p.enabledTo >= ?").append(params.size() + 3).append(")) ");
            } else {
                activeCriteria
                        .append(" (p.enabled in ?").append(params.size() + 1).append(" or ")
                        .append(" (p.enabledFrom > ?").append(params.size() + 2).append(") or ")
                        .append(" (p.enabledTo < ?").append(params.size() + 3).append(")) ");
            }
            if (params.isEmpty()) {
                hqlCriteria.append(" where ").append(activeCriteria);
            } else {
                hqlCriteria.append(" and ").append(activeCriteria);
            }
            final LocalDateTime now = now();
            params.add(isActive);
            params.add(now);
            params.add(now);
        }


        HQLUtils.appendFilterCriteria(hqlCriteria, params, "p", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by p." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }




    /**
     * {@inheritDoc}
     */
    @Override
    public List<Promotion> findPromotions(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findPromotionQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findPromotionCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findPromotionQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }
}

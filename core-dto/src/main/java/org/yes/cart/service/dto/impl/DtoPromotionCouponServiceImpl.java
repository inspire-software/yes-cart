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
import org.yes.cart.domain.dto.PromotionCouponDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.PromotionCouponDTOImpl;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.service.dto.DtoPromotionCouponService;
import org.yes.cart.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 5:49 PM
 */
public class DtoPromotionCouponServiceImpl
    extends AbstractDtoServiceImpl<PromotionCouponDTO, PromotionCouponDTOImpl, PromotionCoupon>
        implements DtoPromotionCouponService {

    /**
     * Construct base dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param promotionCouponGenericService  {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoPromotionCouponServiceImpl(final DtoFactory dtoFactory,
                                         final GenericService<PromotionCoupon> promotionCouponGenericService,
                                         final AdaptersRepository adaptersRepository) {
        super(dtoFactory, promotionCouponGenericService, adaptersRepository);
    }

    @Override
    public SearchResult<PromotionCouponDTO> findCoupons(final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter", "promotionId");
        final String textFilter = FilterSearchUtils.getStringFilter(params.get("filter"));
        final long promotionId = FilterSearchUtils.getIdFilter(params.get("promotionId"));

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final PromotionCouponService promotionCouponService = (PromotionCouponService) service;

        if (promotionId > 0L) {

            final Map<String, List> currentFilter = new HashMap<>();
            if (StringUtils.isNotBlank(textFilter)) {

                if (StringUtils.isNotBlank(textFilter)) {

                    final Pair<LocalDateTime, LocalDateTime> dateSearch = ComplexSearchUtils.checkDateRangeSearch(textFilter);

                    if (dateSearch != null) {

                        final LocalDateTime from = dateSearch.getFirst();
                        final LocalDateTime to = dateSearch.getSecond();

                        final List range = new ArrayList(2);
                        if (from != null) {
                            range.add(SearchContext.MatchMode.GT.toParam(DateUtils.iFrom(from)));
                        }
                        if (to != null) {
                            range.add(SearchContext.MatchMode.LE.toParam(DateUtils.iFrom(to)));
                        }

                        currentFilter.put("createdTimestamp", range);

                    } else {

                        currentFilter.put("code", Collections.singletonList(textFilter));

                    }

                }

            }


            currentFilter.put("promotionIds", Collections.singletonList(promotionId));


            final int count = promotionCouponService.findPromotionCouponCount(currentFilter);
            if (count > startIndex) {

                final List<PromotionCouponDTO> entities = new ArrayList<>();
                final List<PromotionCoupon> coupons = promotionCouponService.findPromotionCoupons(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), currentFilter);

                fillDTOs(coupons, entities);

                return new SearchResult<>(filter, entities, count);

            }

        }
        return new SearchResult<>(filter, Collections.emptyList(), 0);
    }

    /** {@inheritDoc} */
    @Override
    public PromotionCouponDTO create(PromotionCouponDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        if (instance.getCode() != null) {
            // generate one specific
            ((PromotionCouponService) service).create(instance.getPromotionId(), instance.getCode(), instance.getUsageLimit(), instance.getUsageLimitPerCustomer());
        } else {
            // generate many single usage
            ((PromotionCouponService) service).create(instance.getPromotionId(), instance.getUsageLimit(), 1, 0);
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public PromotionCouponDTO update(PromotionCouponDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        // Coupons must not be updated via UI because we may cripple usage integrity. Make users generate new/delete coupons instead.
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public void removeAll(final long promotionId) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        ((PromotionCouponService) service).removeAll(promotionId);

    }

    /** {@inheritDoc} */
    @Override
    public Class<PromotionCouponDTO> getDtoIFace() {
        return PromotionCouponDTO.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<PromotionCouponDTOImpl> getDtoImpl() {
        return PromotionCouponDTOImpl.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<PromotionCoupon> getEntityIFace() {
        return PromotionCoupon.class;
    }
}

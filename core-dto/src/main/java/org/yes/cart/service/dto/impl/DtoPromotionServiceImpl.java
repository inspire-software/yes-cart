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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.PromotionDTOImpl;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.promotion.PromotionTester;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.dto.DtoPromotionService;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 5:49 PM
 */
public class DtoPromotionServiceImpl
    extends AbstractDtoServiceImpl<PromotionDTO, PromotionDTOImpl, Promotion>
        implements DtoPromotionService {

    private final ShopService shopService;
    private final PromotionTester promotionTester;

    /**
     * Construct base dto service.
     * @param dtoFactory               {@link DtoFactory}
     * @param promotionGenericService  {@link GenericService}
     * @param adaptersRepository       {@link AdaptersRepository}
     * @param shopService              shop service
     * @param promotionTester          promotion tester
     */
    public DtoPromotionServiceImpl(final DtoFactory dtoFactory,
                                   final GenericService<Promotion> promotionGenericService,
                                   final AdaptersRepository adaptersRepository,
                                   final ShopService shopService,
                                   final PromotionTester promotionTester) {
        super(dtoFactory, promotionGenericService, adaptersRepository);
        this.shopService = shopService;
        this.promotionTester = promotionTester;
    }


    private final static char[] TAG_OR_CODE_OR_CONDITION_OR_ACTION = new char[] { '#', '?', '!' };
    private final static char[] ENABLED = new char[] { '+', '-' };
    static {
        Arrays.sort(TAG_OR_CODE_OR_CONDITION_OR_ACTION);
        Arrays.sort(ENABLED);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResult<PromotionDTO> findPromotions(final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter", "types", "actions", "shopCode", "currency");
        final String textFilter = FilterSearchUtils.getStringFilter(params.get("filter"));
        final List typesParam = params.get("types");
        final List actionsParam = params.get("actions");
        final String shopCode = FilterSearchUtils.getStringFilter(params.get("shopCode"));
        final String currency = FilterSearchUtils.getStringFilter(params.get("currency"));

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final PromotionService promotionService = (PromotionService) service;

        if (StringUtils.isNotBlank(shopCode) && StringUtils.isNotBlank(currency)) {
            // only allow lists for shop+currency selection
            final Map<String, List> currentFilter = new HashMap<>();
            if (StringUtils.isNotBlank(textFilter)) {

                final Pair<LocalDateTime, LocalDateTime> dateSearch = ComplexSearchUtils.checkDateRangeSearch(textFilter);

                if (dateSearch != null) {

                    final LocalDateTime from = dateSearch.getFirst();
                    final LocalDateTime to = dateSearch.getSecond();

                    if (from != null) {
                        currentFilter.put("enabledFrom", Collections.singletonList(SearchContext.MatchMode.GE.toParam(from)));
                    }
                    if (to != null) {
                        currentFilter.put("enabledTo", Collections.singletonList(SearchContext.MatchMode.LE.toParam(to)));
                    }

                } else {

                    final Pair<String, String> enabled = ComplexSearchUtils.checkSpecialSearch(textFilter, ENABLED);

                    if (enabled != null) {
                        currentFilter.put("active", Collections.singletonList("+".equals(enabled.getFirst())));
                    }

                    if (enabled == null || !enabled.getFirst().equals(enabled.getSecond())) {

                        final Pair<String, String> tagOrCodeOrConditionOrAction = ComplexSearchUtils.checkSpecialSearch(enabled != null ? enabled.getSecond() : textFilter, TAG_OR_CODE_OR_CONDITION_OR_ACTION);

                        if (tagOrCodeOrConditionOrAction != null) {

                            if ("#".equals(tagOrCodeOrConditionOrAction.getFirst())) {

                                SearchContext.JoinMode.OR.setMode(currentFilter);
                                currentFilter.put("code", Collections.singletonList(tagOrCodeOrConditionOrAction.getSecond()));
                                currentFilter.put("tag", Collections.singletonList(tagOrCodeOrConditionOrAction.getSecond()));

                            } else if ("?".equals(tagOrCodeOrConditionOrAction.getFirst())) {

                                SearchContext.JoinMode.OR.setMode(currentFilter);
                                currentFilter.put("eligibilityCondition", Collections.singletonList(tagOrCodeOrConditionOrAction.getSecond()));
                                currentFilter.put("promoActionContext", Collections.singletonList(tagOrCodeOrConditionOrAction.getSecond()));

                            }

                        } else {

                            final String basic = enabled != null ? enabled.getSecond() : textFilter;

                            SearchContext.JoinMode.OR.setMode(currentFilter);
                            currentFilter.put("code", Collections.singletonList(basic));
                            currentFilter.put("name", Collections.singletonList(basic));
                            currentFilter.put("description", Collections.singletonList(basic));

                        }
                    }
                }

            }

            final Shop currentShop = shopService.getShopByCode(shopCode);
            if (currentShop == null) {
                return new SearchResult<>(filter, Collections.emptyList(), 0);
            }
            final List<String> shopCodes = new ArrayList<>(2);
            shopCodes.add(shopCode);
            if (currentShop.getMaster() != null && !currentShop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_B2B_STRICT_PROMOTIONS)) {
                shopCodes.add(currentShop.getMaster().getCode());
            }

            currentFilter.put("shopCodes", shopCodes);
            currentFilter.put("currencies", Collections.singletonList(currency));

            if (CollectionUtils.isNotEmpty(typesParam)) {
                currentFilter.put("promoTypes", typesParam);
            }
            if (CollectionUtils.isNotEmpty(actionsParam)) {
                currentFilter.put("promoActions", actionsParam);
            }

            final int count = promotionService.findPromotionCount(currentFilter);
            if (count > startIndex) {

                final List<PromotionDTO> entities = new ArrayList<>();
                final List<Promotion> promotions = promotionService.findPromotions(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), currentFilter);

                fillDTOs(promotions, entities);

                return new SearchResult<>(filter, entities, count);

            }

        }

        return new SearchResult<>(filter, Collections.emptyList(), 0);
    }


    /** {@inheritDoc} */
    @Override
    public List<PromotionDTO> findByCodes(final Set<String> codes) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Promotion> promos = service.findByCriteria(" where e.code in (?1)", codes);
        final List<PromotionDTO> dtos = new ArrayList<>();
        fillDTOs(promos, dtos);
        return dtos;
    }

    /** {@inheritDoc} */
    @Override
    protected void createPostProcess(final PromotionDTO dto, final Promotion entity) {
        // we store comma separated lists of promo codes on cart item, so we cannot allow commas
        entity.setCode(dto.getCode().replace(',','_'));
        entity.setShopCode(dto.getShopCode());
        entity.setCurrency(dto.getCurrency());
        entity.setPromoType(dto.getPromoType());
        entity.setPromoAction(dto.getPromoAction());
        if (dto.getEligibilityCondition() == null) {
            entity.setEligibilityCondition(Boolean.TRUE.toString());
        } else {
            entity.setEligibilityCondition(dto.getEligibilityCondition());
        }
        entity.setPromoActionContext(dto.getPromoActionContext());
        entity.setCanBeCombined(dto.isCanBeCombined());
        entity.setCouponTriggered(dto.isCouponTriggered());
    }

    /** {@inheritDoc} */
    @Override
    protected void updatePostProcess(final PromotionDTO dto, final Promotion entity) {
        if (!entity.isEnabled()) { // We allow modifications if entity is disabled
            entity.setCanBeCombined(dto.isCanBeCombined());
            entity.setCouponTriggered(dto.isCouponTriggered());
            if (dto.getEligibilityCondition() == null) {
                entity.setEligibilityCondition(Boolean.TRUE.toString());
            } else {
                entity.setEligibilityCondition(dto.getEligibilityCondition());
            }
            entity.setPromoActionContext(dto.getPromoActionContext());
        }
    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart testPromotions(final String shopCode,
                                       final String currency,
                                       final String language,
                                       final String customer,
                                       final String supplier,
                                       final Map<String, BigDecimal> products,
                                       final String shipping,
                                       final List<String> coupons,
                                       final Instant time) {
        return promotionTester.testPromotions(shopCode, currency, language, customer, supplier, products, shipping, coupons, time);
    }

    /** {@inheritDoc} */
    @Override
    public Class<PromotionDTO> getDtoIFace() {
        return PromotionDTO.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<PromotionDTOImpl> getDtoImpl() {
        return PromotionDTOImpl.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<Promotion> getEntityIFace() {
        return Promotion.class;
    }
}

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
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.PromotionDTOImpl;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.promotion.PromotionTester;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.dto.DtoPromotionService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.utils.TimeContext;
import org.yes.cart.utils.HQLUtils;

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

    /** {@inheritDoc} */
    @Override
    public List<PromotionDTO> findByParameters(final String code,
                                               final String shopCode,
                                               final String currency,
                                               final String tag,
                                               final String type,
                                               final String action,
                                               final Boolean enabled)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Promotion> promos = ((PromotionService) service).findByParameters(code, shopCode, currency, tag, type, action, enabled);
        final List<PromotionDTO> dtos = new ArrayList<>();
        fillDTOs(promos, dtos);
        return dtos;
    }


    private final static char[] TAG_OR_CODE_OR_CONDITION_OR_ACTION = new char[] { '#', '?', '!' };
    private final static char[] ENABLED = new char[] { '+', '-' };
    static {
        Arrays.sort(TAG_OR_CODE_OR_CONDITION_OR_ACTION);
        Arrays.sort(ENABLED);
    }

    @Override
    public List<PromotionDTO> findBy(final String shopCode, final String currency, final String filter, final List<String> types, final List<String> actions, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<PromotionDTO> dtos = new ArrayList<>();


        if (StringUtils.isNotBlank(shopCode) && StringUtils.isNotBlank(currency)) {
            // only allow lists for shop+currency selection

            final Shop currentShop = shopService.getShopByCode(shopCode);
            if (currentShop == null) {
                return Collections.emptyList();
            }
            final List<String> shopCodes = new ArrayList<>(2);
            shopCodes.add(shopCode);
            if (currentShop.getMaster() != null && !currentShop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.SHOP_B2B_STRICT_PROMOTIONS)) {
                shopCodes.add(currentShop.getMaster().getCode());
            }


            List<Promotion> entities = Collections.emptyList();

            final String orderBy = " order by e.enabledFrom, e.enabledTo, e.name";

            if (StringUtils.isNotBlank(filter)) {

                final Pair<LocalDateTime, LocalDateTime> dateSearch = ComplexSearchUtils.checkDateRangeSearch(filter);

                if (dateSearch != null) {

                    entities = getService().getGenericDao().findRangeByCriteria(
                            " where e.shopCode in ?1 and e.currency = ?2 and (?3 is null or e.enabledFrom is null or e.enabledFrom <= ?3) and (?4 is null or e.enabledTo is null or e.enabledTo >= ?4) and (?5 = 0 or e.promoType in (?6)) and (?7 = 0 or e.promoAction in (?8)) " + orderBy,
                            page * pageSize, pageSize,
                            shopCodes,
                            currency,
                            dateSearch.getFirst(),
                            dateSearch.getSecond(),
                            HQLUtils.criteriaInTest(types),
                            HQLUtils.criteriaIn(types),
                            HQLUtils.criteriaInTest(actions),
                            HQLUtils.criteriaIn(actions)
                    );

                } else {

                    final Pair<String, String> enabled = ComplexSearchUtils.checkSpecialSearch(filter, ENABLED);

                    boolean enabledOnly = enabled != null && "+".equals(enabled.getFirst());
                    boolean disabledOnly = enabled != null && "-".equals(enabled.getFirst());

                    if (enabled == null || !enabled.getFirst().equals(enabled.getSecond())) {

                        final Pair<String, String> tagOrCodeOrConditionOrAction = ComplexSearchUtils.checkSpecialSearch(enabled != null ? enabled.getSecond() : filter, TAG_OR_CODE_OR_CONDITION_OR_ACTION);

                        if (tagOrCodeOrConditionOrAction != null) {

                            if ("#".equals(tagOrCodeOrConditionOrAction.getFirst())) {

                                if (enabledOnly) {
                                    final LocalDateTime now = now();
                                    entities = getService().getGenericDao().findRangeByCriteria(
                                            " where e.shopCode in ?1 and e.currency = ?2 and e.enabled = ?3 and (e.enabledFrom is null or e.enabledFrom <= ?4) and (e.enabledTo is null or e.enabledTo >= ?4) and (lower(e.code) like ?5 or lower(e.tag) like ?5)  and (?6 = 0 or e.promoType in (?7)) and (?8 = 0 or e.promoAction in (?9)) " + orderBy,
                                            page * pageSize, pageSize,
                                            shopCodes,
                                            currency,
                                            Boolean.TRUE,
                                            now,
                                            HQLUtils.criteriaIlikeAnywhere(tagOrCodeOrConditionOrAction.getSecond()),
                                            HQLUtils.criteriaInTest(types),
                                            HQLUtils.criteriaIn(types),
                                            HQLUtils.criteriaInTest(actions),
                                            HQLUtils.criteriaIn(actions)
                                    );
                                } else if (disabledOnly) {
                                    final LocalDateTime now = now();
                                    entities = getService().getGenericDao().findRangeByCriteria(
                                            " where e.shopCode in ?1 and e.currency = ?2 and (e.enabled = ?3 or e.enabledFrom > ?4 or e.enabledTo < ?4) and (lower(e.code) like ?5 or lower(e.tag) like ?5)  and (?6 = 0 or e.promoType in (?7)) and (?8 = 0 or e.promoAction in (?9)) " + orderBy,
                                            page * pageSize, pageSize,
                                            shopCodes,
                                            currency,
                                            Boolean.FALSE,
                                            now,
                                            HQLUtils.criteriaIlikeAnywhere(tagOrCodeOrConditionOrAction.getSecond()),
                                            HQLUtils.criteriaInTest(types),
                                            HQLUtils.criteriaIn(types),
                                            HQLUtils.criteriaInTest(actions),
                                            HQLUtils.criteriaIn(actions)
                                    );
                                } else {
                                    entities = getService().getGenericDao().findRangeByCriteria(
                                            " where e.shopCode in ?1 and e.currency = ?2 and (lower(e.code) like ?3 or lower(e.tag) like ?3)  and (?4 = 0 or e.promoType in (?5)) and (?6 = 0 or e.promoAction in (?7)) " + orderBy,
                                            page * pageSize, pageSize,
                                            shopCodes,
                                            currency,
                                            HQLUtils.criteriaIlikeAnywhere(tagOrCodeOrConditionOrAction.getSecond()),
                                            HQLUtils.criteriaInTest(types),
                                            HQLUtils.criteriaIn(types),
                                            HQLUtils.criteriaInTest(actions),
                                            HQLUtils.criteriaIn(actions)
                                    );
                                }



                            } else if ("?".equals(tagOrCodeOrConditionOrAction.getFirst())) {

                                if (enabledOnly) {
                                    final LocalDateTime now = now();
                                    entities = getService().getGenericDao().findRangeByCriteria(
                                            " where e.shopCode in ?1 and e.currency = ?2 and e.enabled = ?3 and (e.enabledFrom is null or e.enabledFrom <= ?4) and (e.enabledTo is null or e.enabledTo >= ?4) and (lower(e.eligibilityCondition) like ?5 or lower(e.promoActionContext) like ?5)  and (?6 = 0 or e.promoType in (?7)) and (?8 = 0 or e.promoAction in (?9))  " + orderBy,
                                            page * pageSize, pageSize,
                                            shopCodes,
                                            currency,
                                            Boolean.TRUE,
                                            now,
                                            HQLUtils.criteriaIlikeAnywhere(tagOrCodeOrConditionOrAction.getSecond()),
                                            HQLUtils.criteriaInTest(types),
                                            HQLUtils.criteriaIn(types),
                                            HQLUtils.criteriaInTest(actions),
                                            HQLUtils.criteriaIn(actions)
                                    );
                                } else if (disabledOnly) {
                                    final LocalDateTime now = now();
                                    entities = getService().getGenericDao().findRangeByCriteria(
                                            " where e.shopCode in ?1 and e.currency = ?2 and (e.enabled = ?3 or e.enabledFrom > ?4 or e.enabledTo < ?4) and (lower(e.eligibilityCondition) like ?5 or lower(e.promoActionContext) like ?5)  and (?6 = 0 or e.promoType in (?7)) and (?8 = 0 or e.promoAction in (?9))  " + orderBy,
                                            page * pageSize, pageSize,
                                            shopCodes,
                                            currency,
                                            Boolean.FALSE,
                                            now,
                                            HQLUtils.criteriaIlikeAnywhere(tagOrCodeOrConditionOrAction.getSecond()),
                                            HQLUtils.criteriaInTest(types),
                                            HQLUtils.criteriaIn(types),
                                            HQLUtils.criteriaInTest(actions),
                                            HQLUtils.criteriaIn(actions)
                                    );
                                } else {
                                    entities = getService().getGenericDao().findRangeByCriteria(
                                            " where e.shopCode in ?1 and e.currency = ?2 and (lower(e.eligibilityCondition) like ?3 or lower(e.promoActionContext) like ?3)  and (?4 = 0 or e.promoType in (?5)) and (?6 = 0 or e.promoAction in (?7))  " + orderBy,
                                            page * pageSize, pageSize,
                                            shopCodes,
                                            currency,
                                            HQLUtils.criteriaIlikeAnywhere(tagOrCodeOrConditionOrAction.getSecond()),
                                            HQLUtils.criteriaInTest(types),
                                            HQLUtils.criteriaIn(types),
                                            HQLUtils.criteriaInTest(actions),
                                            HQLUtils.criteriaIn(actions)
                                    );
                                }

                            }

                        } else {

                            if (enabledOnly) {
                                final LocalDateTime now = now();
                                entities = getService().getGenericDao().findRangeByCriteria(
                                        " where e.shopCode in ?1 and e.currency = ?2 and e.enabled = ?3 and (e.enabledFrom is null or e.enabledFrom <= ?4) and (e.enabledTo is null or e.enabledTo >= ?4) and (lower(e.code) like ?5 or lower(e.name) like ?5 or lower(e.description) like ?5)  and (?6 = 0 or e.promoType in (?7)) and (?8 = 0 or e.promoAction in (?9))  " + orderBy,
                                        page * pageSize, pageSize,
                                        shopCodes,
                                        currency,
                                        Boolean.TRUE,
                                        now,
                                        HQLUtils.criteriaIlikeAnywhere(enabled != null ? enabled.getSecond() : filter),
                                        HQLUtils.criteriaInTest(types),
                                        HQLUtils.criteriaIn(types),
                                        HQLUtils.criteriaInTest(actions),
                                        HQLUtils.criteriaIn(actions)
                                );
                            } else if (disabledOnly) {
                                final LocalDateTime now = now();
                                entities = getService().getGenericDao().findRangeByCriteria(
                                        " where e.shopCode in ?1 and e.currency = ?2 and (e.enabled = ?3 or e.enabledFrom > ?4 or e.enabledTo < ?4) and (lower(e.code) like ?5 or lower(e.name) like ?5 or lower(e.description) like ?5)  and (?6 = 0 or e.promoType in (?7)) and (?8 = 0 or e.promoAction in (?9))  " + orderBy,
                                        page * pageSize, pageSize,
                                        shopCodes,
                                        currency,
                                        Boolean.FALSE,
                                        now,
                                        HQLUtils.criteriaIlikeAnywhere(enabled != null ? enabled.getSecond() : filter),
                                        HQLUtils.criteriaInTest(types),
                                        HQLUtils.criteriaIn(types),
                                        HQLUtils.criteriaInTest(actions),
                                        HQLUtils.criteriaIn(actions)
                                );
                            } else {
                                entities = getService().getGenericDao().findRangeByCriteria(
                                        " where e.shopCode in ?1 and e.currency = ?2 and (lower(e.code) like ?3 or lower(e.name) like ?3 or lower(e.description) like ?3)  and (?4 = 0 or e.promoType in (?5)) and (?6 = 0 or e.promoAction in (?7))  " + orderBy,
                                        page * pageSize, pageSize,
                                        shopCodes,
                                        currency,
                                        HQLUtils.criteriaIlikeAnywhere(enabled != null ? enabled.getSecond() : filter),
                                        HQLUtils.criteriaInTest(types),
                                        HQLUtils.criteriaIn(types),
                                        HQLUtils.criteriaInTest(actions),
                                        HQLUtils.criteriaIn(actions)
                                );
                            }

                        }
                    } else {

                        if (enabledOnly) {
                            final LocalDateTime now = now();
                            entities = getService().getGenericDao().findRangeByCriteria(
                                    " where e.shopCode in ?1 and e.currency = ?2 and e.enabled = ?3 and (e.enabledFrom is null or e.enabledFrom <= ?4) and (e.enabledTo is null or e.enabledTo >= ?4)  and (?5 = 0 or e.promoType in (?6)) and (?7 = 0 or e.promoAction in (?8))  " + orderBy,
                                    page * pageSize, pageSize,
                                    shopCodes,
                                    currency,
                                    Boolean.TRUE,
                                    now,
                                    HQLUtils.criteriaInTest(types),
                                    HQLUtils.criteriaIn(types),
                                    HQLUtils.criteriaInTest(actions),
                                    HQLUtils.criteriaIn(actions)
                            );
                        } else {
                            final LocalDateTime now = now();
                            entities = getService().getGenericDao().findRangeByCriteria(
                                    " where e.shopCode in ?1 and e.currency = ?2 and (e.enabled = ?3 or e.enabledFrom > ?4 or e.enabledTo < ?4)  and (?5 = 0 or e.promoType in (?6)) and (?7 = 0 or e.promoAction in (?8))   " + orderBy,
                                    page * pageSize, pageSize,
                                    shopCodes,
                                    currency,
                                    Boolean.FALSE,
                                    now,
                                    HQLUtils.criteriaInTest(types),
                                    HQLUtils.criteriaIn(types),
                                    HQLUtils.criteriaInTest(actions),
                                    HQLUtils.criteriaIn(actions)
                            );
                        }

                    }
                }

            } else {

                entities = getService().getGenericDao().findRangeByCriteria(
                        " where e.shopCode in ?1 and e.currency = ?2  and (?3 = 0 or e.promoType in (?4)) and (?5 = 0 or e.promoAction in (?6)) " + orderBy,
                        page * pageSize, pageSize,
                        shopCodes,
                        currency,
                        HQLUtils.criteriaInTest(types),
                        HQLUtils.criteriaIn(types),
                        HQLUtils.criteriaInTest(actions),
                        HQLUtils.criteriaIn(actions)
                );

            }

            fillDTOs(entities, dtos);
        }

        return dtos;
    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }


    @Override
    public List<PromotionDTO> findByCodes(final Set<String> codes) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Promotion> promos = service.findByCriteria(" where e.code in (?1)", codes);
        final List<PromotionDTO> dtos = new ArrayList<>();
        fillDTOs(promos, dtos);
        return dtos;
    }

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
                                       final Map<String, BigDecimal> products,
                                       final String shipping,
                                       final List<String> coupons,
                                       final Instant time) {
        return promotionTester.testPromotions(shopCode, currency, language, customer, products, shipping, coupons, time);
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

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
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.PromotionDTOImpl;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.service.dto.DtoPromotionService;

import java.util.*;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 5:49 PM
 */
public class DtoPromotionServiceImpl
    extends AbstractDtoServiceImpl<PromotionDTO, PromotionDTOImpl, Promotion>
        implements DtoPromotionService {

    /**
     * Construct base dto service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param promotionGenericService  {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository {@link com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository}
     */
    public DtoPromotionServiceImpl(final DtoFactory dtoFactory,
                                   final GenericService<Promotion> promotionGenericService,
                                   final AdaptersRepository adaptersRepository) {
        super(dtoFactory, promotionGenericService, adaptersRepository);
    }

    /** {@inheritDoc} */
    public List<PromotionDTO> findByParameters(final String code,
                                               final String shopCode,
                                               final String currency,
                                               final String tag,
                                               final String type,
                                               final String action,
                                               final Boolean enabled)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Promotion> promos = ((PromotionService) service).findByParameters(code, shopCode, currency, tag, type, action, enabled);
        final List<PromotionDTO> dtos = new ArrayList<PromotionDTO>();
        fillDTOs(promos, dtos);
        return dtos;
    }


    private final static char[] TAG_OR_CODE_OR_CONDITION_OR_ACTION = new char[] { '#', '?', '!' };
    private final static char[] ENABLED = new char[] { '+', '-' };
    static {
        Arrays.sort(TAG_OR_CODE_OR_CONDITION_OR_ACTION);
        Arrays.sort(ENABLED);
    }
    private final static Order[] PROMO_ORDER = new Order[] { Order.asc("enabledFrom"), Order.asc("enabledTo"), Order.asc("name") };

    @Override
    public List<PromotionDTO> findBy(final String shopCode, final String currency, final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<PromotionDTO> dtos = new ArrayList<>();


        if (StringUtils.hasLength(shopCode) && StringUtils.hasLength(currency)) {
            // only allow lists for shop+currency selection

            final List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(Restrictions.eq("shopCode", shopCode));
            criteria.add(Restrictions.eq("currency", currency));
            if (StringUtils.hasLength(filter)) {

                final Pair<Date, Date> dateSearch = ComplexSearchUtils.checkDateRangeSearch(filter);

                if (dateSearch != null) {

                    if (dateSearch.getFirst() != null) {

                        criteria.add(Restrictions.le("enabledFrom", dateSearch.getFirst()));

                    }

                    if (dateSearch.getSecond() != null) {

                        criteria.add(Restrictions.ge("enabledTo", dateSearch.getSecond()));

                    }


                } else {

                    final Pair<String, String> enabled = ComplexSearchUtils.checkSpecialSearch(filter, ENABLED);

                    boolean enabledOnly = enabled != null && "+".equals(enabled.getFirst());
                    boolean disabledOnly = enabled != null && "-".equals(enabled.getFirst());

                    if (enabledOnly) {
                        final Date now = new Date();
                        criteria.add(Restrictions.eq("enabled", Boolean.TRUE));
                        criteria.add(Restrictions.or(
                                Restrictions.isNull("enabledFrom"),
                                Restrictions.le("enabledFrom", now)
                        ));
                        criteria.add(Restrictions.or(
                                Restrictions.isNull("enabledTo"),
                                Restrictions.gt("enabledTo", now)
                        ));
                    }
                    if (disabledOnly) {
                        final Date now = new Date();
                        criteria.add(Restrictions.or(
                                Restrictions.eq("enabled", Boolean.FALSE),
                                Restrictions.gt("enabledFrom", now),
                                Restrictions.lt("enabledTo", now)
                        ));
                    }

                    if (enabled == null || !enabled.getFirst().equals(enabled.getSecond())) {

                        final Pair<String, String> tagOrCodeOrConditionOrAction = ComplexSearchUtils.checkSpecialSearch(enabled != null ? enabled.getSecond() : filter, TAG_OR_CODE_OR_CONDITION_OR_ACTION);

                        if (tagOrCodeOrConditionOrAction != null) {

                            if ("#".equals(tagOrCodeOrConditionOrAction.getFirst())) {

                                criteria.add(Restrictions.or(
                                        Restrictions.ilike("code", tagOrCodeOrConditionOrAction.getSecond(), MatchMode.ANYWHERE),
                                        Restrictions.ilike("tag", tagOrCodeOrConditionOrAction.getSecond(), MatchMode.ANYWHERE)
                                ));

                            } else if ("!".equals(tagOrCodeOrConditionOrAction.getFirst())) {

                                criteria.add(Restrictions.or(
                                        Restrictions.ilike("promoType", tagOrCodeOrConditionOrAction.getSecond(), MatchMode.EXACT),
                                        Restrictions.ilike("promoAction", tagOrCodeOrConditionOrAction.getSecond(), MatchMode.EXACT)
                                ));

                            } else if ("?".equals(tagOrCodeOrConditionOrAction.getFirst())) {

                                criteria.add(Restrictions.or(
                                        Restrictions.ilike("eligibilityCondition", tagOrCodeOrConditionOrAction.getSecond(), MatchMode.ANYWHERE),
                                        Restrictions.ilike("promoActionContext", tagOrCodeOrConditionOrAction.getSecond(), MatchMode.ANYWHERE)
                                ));

                            }

                        } else {

                            criteria.add(Restrictions.or(
                                    Restrictions.ilike("code", filter, MatchMode.ANYWHERE),
                                    Restrictions.ilike("name", filter, MatchMode.ANYWHERE),
                                    Restrictions.ilike("description", filter, MatchMode.ANYWHERE)
                            ));

                        }
                    }
                }

            }

            final List<Promotion> entities = getService().getGenericDao().findByCriteria(
                    page * pageSize, pageSize, criteria.toArray(new Criterion[criteria.size()]), PROMO_ORDER);

            fillDTOs(entities, dtos);
        }

        return dtos;
    }


    @Override
    public List<PromotionDTO> findBy(final String shopCode, final String currency, final String filter, final List<String> types, final List<String> actions, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<PromotionDTO> dtos = new ArrayList<>();


        if (StringUtils.hasLength(shopCode) && StringUtils.hasLength(currency)) {
            // only allow lists for shop+currency selection

            final List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(Restrictions.eq("shopCode", shopCode));
            criteria.add(Restrictions.eq("currency", currency));
            if (StringUtils.hasLength(filter)) {

                final Pair<Date, Date> dateSearch = ComplexSearchUtils.checkDateRangeSearch(filter);

                if (dateSearch != null) {

                    if (dateSearch.getFirst() != null) {

                        criteria.add(Restrictions.le("enabledFrom", dateSearch.getFirst()));

                    }

                    if (dateSearch.getSecond() != null) {

                        criteria.add(Restrictions.ge("enabledTo", dateSearch.getSecond()));

                    }


                } else {

                    final Pair<String, String> enabled = ComplexSearchUtils.checkSpecialSearch(filter, ENABLED);

                    boolean enabledOnly = enabled != null && "+".equals(enabled.getFirst());
                    boolean disabledOnly = enabled != null && "-".equals(enabled.getFirst());

                    if (enabledOnly) {
                        final Date now = new Date();
                        criteria.add(Restrictions.eq("enabled", Boolean.TRUE));
                        criteria.add(Restrictions.or(
                                Restrictions.isNull("enabledFrom"),
                                Restrictions.le("enabledFrom", now)
                        ));
                        criteria.add(Restrictions.or(
                                Restrictions.isNull("enabledTo"),
                                Restrictions.gt("enabledTo", now)
                        ));
                    }
                    if (disabledOnly) {
                        final Date now = new Date();
                        criteria.add(Restrictions.or(
                                Restrictions.eq("enabled", Boolean.FALSE),
                                Restrictions.gt("enabledFrom", now),
                                Restrictions.lt("enabledTo", now)
                        ));
                    }

                    if (enabled == null || !enabled.getFirst().equals(enabled.getSecond())) {

                        final Pair<String, String> tagOrCodeOrConditionOrAction = ComplexSearchUtils.checkSpecialSearch(enabled != null ? enabled.getSecond() : filter, TAG_OR_CODE_OR_CONDITION_OR_ACTION);

                        if (tagOrCodeOrConditionOrAction != null) {

                            if ("#".equals(tagOrCodeOrConditionOrAction.getFirst())) {

                                criteria.add(Restrictions.or(
                                        Restrictions.ilike("code", tagOrCodeOrConditionOrAction.getSecond(), MatchMode.ANYWHERE),
                                        Restrictions.ilike("tag", tagOrCodeOrConditionOrAction.getSecond(), MatchMode.ANYWHERE)
                                ));

                            } else if ("?".equals(tagOrCodeOrConditionOrAction.getFirst())) {

                                criteria.add(Restrictions.or(
                                        Restrictions.ilike("eligibilityCondition", tagOrCodeOrConditionOrAction.getSecond(), MatchMode.ANYWHERE),
                                        Restrictions.ilike("promoActionContext", tagOrCodeOrConditionOrAction.getSecond(), MatchMode.ANYWHERE)
                                ));

                            }

                        } else {

                            criteria.add(Restrictions.or(
                                    Restrictions.ilike("code", filter, MatchMode.ANYWHERE),
                                    Restrictions.ilike("name", filter, MatchMode.ANYWHERE),
                                    Restrictions.ilike("description", filter, MatchMode.ANYWHERE)
                            ));

                        }
                    }
                }

            }

            if (CollectionUtils.isNotEmpty(types)) {
                criteria.add(Restrictions.in("promoType", types));
            }
            if (CollectionUtils.isNotEmpty(actions)) {
                criteria.add(Restrictions.in("promoAction", actions));
            }

            final List<Promotion> entities = getService().getGenericDao().findByCriteria(
                    page * pageSize, pageSize, criteria.toArray(new Criterion[criteria.size()]), PROMO_ORDER);

            fillDTOs(entities, dtos);
        }

        return dtos;
    }


    @Override
    public List<PromotionDTO> findByCodes(final Set<String> codes) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Promotion> promos = service.findByCriteria(Restrictions.in("code", codes));
        final List<PromotionDTO> dtos = new ArrayList<PromotionDTO>();
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
        entity.setEligibilityCondition(dto.getEligibilityCondition());
        entity.setPromoActionContext(dto.getPromoActionContext());
        entity.setCanBeCombined(dto.isCanBeCombined());
        entity.setCouponTriggered(dto.isCouponTriggered());
    }

    @Override
    protected void updatePostProcess(final PromotionDTO dto, final Promotion entity) {
        if (!entity.isEnabled()) { // We allow modifications if entity is disabled
            entity.setCanBeCombined(dto.isCanBeCombined());
            entity.setCouponTriggered(dto.isCouponTriggered());
            entity.setEligibilityCondition(dto.getEligibilityCondition());
            entity.setPromoActionContext(dto.getPromoActionContext());
        }
    }

    /** {@inheritDoc} */
    public Class<PromotionDTO> getDtoIFace() {
        return PromotionDTO.class;
    }

    /** {@inheritDoc} */
    public Class<PromotionDTOImpl> getDtoImpl() {
        return PromotionDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<Promotion> getEntityIFace() {
        return Promotion.class;
    }
}

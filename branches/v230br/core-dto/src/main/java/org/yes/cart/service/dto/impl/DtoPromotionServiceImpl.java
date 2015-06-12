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
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.PromotionDTOImpl;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.service.dto.DtoPromotionService;

import java.util.ArrayList;
import java.util.List;

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

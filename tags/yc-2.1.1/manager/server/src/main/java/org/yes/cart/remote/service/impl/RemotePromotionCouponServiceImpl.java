/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.PromotionCouponDTO;
import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemotePromotionCouponService;
import org.yes.cart.service.dto.DtoPromotionCouponService;
import org.yes.cart.service.dto.DtoPromotionService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 7:23 PM
 */
public class RemotePromotionCouponServiceImpl extends AbstractRemoteService<PromotionCouponDTO>
        implements RemotePromotionCouponService {

    private DtoPromotionCouponService dtoPromotionCouponService;

    public RemotePromotionCouponServiceImpl(final GenericDTOService<PromotionCouponDTO> promotionCouponDTOGenericDTOService) {
        super(promotionCouponDTOGenericDTOService);
        this.dtoPromotionCouponService = (DtoPromotionCouponService) promotionCouponDTOGenericDTOService;
    }

    /** {@inheritDoc} */
    public List<PromotionCouponDTO> getCouponsByPromotionId(Long promotionId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoPromotionCouponService.getCouponsByPromotionId(promotionId);
    }

    /** {@inheritDoc} */
    public byte[] getCouponsByPromotionIdExport(Long promotionId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoPromotionCouponService.getCouponsByPromotionIdExport(promotionId);
    }
}

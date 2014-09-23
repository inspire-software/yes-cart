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

import org.yes.cart.domain.dto.PromotionDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemotePromotionService;
import org.yes.cart.service.dto.DtoPromotionService;
import org.yes.cart.service.dto.GenericDTOService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 7:23 PM
 */
public class RemotePromotionServiceImpl extends AbstractRemoteService<PromotionDTO>
        implements RemotePromotionService {

    private DtoPromotionService dtoPromotionService;

    public RemotePromotionServiceImpl(final GenericDTOService<PromotionDTO> promotionDTOGenericDTOService) {
        super(promotionDTOGenericDTOService);
        this.dtoPromotionService = (DtoPromotionService) promotionDTOGenericDTOService;
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
        return dtoPromotionService.findByParameters(code, shopCode, currency, tag, type, action, enabled);
    }
}

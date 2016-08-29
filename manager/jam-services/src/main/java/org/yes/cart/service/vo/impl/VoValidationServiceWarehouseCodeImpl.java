/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo.impl;

import org.hibernate.criterion.Restrictions;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.vo.VoValidationService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 29/08/2016
 * Time: 15:31
 */
public class VoValidationServiceWarehouseCodeImpl extends AbstractVoValidationServiceSubjectCodeFieldImpl implements VoValidationService {

    private final WarehouseService warehouseService;

    public VoValidationServiceWarehouseCodeImpl(final WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Override
    protected Long getDuplicateId(final long currentId, final String valueToCheck) {
        final List<Warehouse> wh = this.warehouseService.findByCriteria(Restrictions.eq("code", valueToCheck));
        return wh != null && wh.size() > 0 && wh.get(0).getWarehouseId() != currentId ? wh.get(0).getWarehouseId() : null;
    }
}

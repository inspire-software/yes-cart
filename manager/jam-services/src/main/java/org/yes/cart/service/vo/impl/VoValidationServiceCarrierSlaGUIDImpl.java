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
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.vo.VoValidationService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 29/08/2016
 * Time: 15:31
 */
public class VoValidationServiceCarrierSlaGUIDImpl extends AbstractVoValidationServiceSubjectCodeFieldImpl implements VoValidationService {

    private final CarrierSlaService carrierSlaService;

    public VoValidationServiceCarrierSlaGUIDImpl(final CarrierSlaService carrierSlaService) {
        this.carrierSlaService = carrierSlaService;
    }

    @Override
    protected Long getDuplicateId(final long currentId, final String valueToCheck) {
        final List<CarrierSla> sla = this.carrierSlaService.findByCriteria(Restrictions.eq("guid", valueToCheck));
        return sla != null && sla.size() > 0 && sla.get(0).getCarrierslaId() != currentId ? sla.get(0).getCarrierslaId() : null;
    }
}

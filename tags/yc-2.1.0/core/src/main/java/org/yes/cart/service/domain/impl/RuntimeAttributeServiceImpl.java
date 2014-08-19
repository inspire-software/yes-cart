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

package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Restrictions;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.service.domain.AttributeGroupService;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.EtypeService;
import org.yes.cart.service.domain.RuntimeAttributeService;

import java.util.Set;

/**
 * User: denispavlov
 * Date: 07/11/2013
 * Time: 15:55
 */
public class RuntimeAttributeServiceImpl implements RuntimeAttributeService {

    private final AttributeService attributeService;
    private final EtypeService etypeService;
    private final AttributeGroupService attributeGroupService;


    public RuntimeAttributeServiceImpl(final AttributeService attributeService,
                                       final EtypeService etypeService,
                                       final AttributeGroupService attributeGroupService) {
        this.attributeService = attributeService;
        this.etypeService = etypeService;
        this.attributeGroupService = attributeGroupService;
    }

    /** {@inheritDoc} */
    @Override
    public Attribute create(final String attributeCode, final String attributeGroup, final String eType) {
        final Set<String> codes = attributeService.getAllAttributeCodes();
        if (!codes.contains(attributeCode)) {
            final Attribute attribute = attributeService.getGenericDao().getEntityFactory().getByIface(Attribute.class);
            attribute.setCode(attributeCode);
            attribute.setName(attributeCode);
            attribute.setAttributeGroup(attributeGroupService.getAttributeGroupByCode(attributeGroup));
            attribute.setEtype(etypeService.findSingleByCriteria(Restrictions.eq("businesstype", eType)));
            attribute.setGuid(attributeCode);
            attributeService.create(attribute);
            return attribute;
        }
        return attributeService.findByAttributeCode(attributeCode);
    }
}

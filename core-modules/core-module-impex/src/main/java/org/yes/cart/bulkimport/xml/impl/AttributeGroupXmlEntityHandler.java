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

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.AttributeGroupType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.domain.entity.AttributeGroup;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.AttributeGroupService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class AttributeGroupXmlEntityHandler extends AbstractXmlEntityHandler<AttributeGroupType, AttributeGroup> implements XmlEntityImportHandler<AttributeGroupType, AttributeGroup> {

    private AttributeGroupService attributeGroupService;

    public AttributeGroupXmlEntityHandler() {
        super("attribute-group");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final AttributeGroup group) {
        this.attributeGroupService.delete(group);
        this.attributeGroupService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final AttributeGroup domain, final AttributeGroupType xmlType, final EntityImportModeType mode) {
        domain.setName(xmlType.getName());
        domain.setDescription(xmlType.getDescription());
        if (domain.getAttributegroupId() == 0L) {
            this.attributeGroupService.create(domain);
        } else {
            this.attributeGroupService.update(domain);
        }
        this.attributeGroupService.getGenericDao().flush();
        this.attributeGroupService.getGenericDao().evict(domain);
    }

    @Override
    protected AttributeGroup getOrCreate(final JobStatusListener statusListener, final AttributeGroupType xmlType) {
        AttributeGroup group = this.attributeGroupService.findSingleByCriteria(" where e.code = ?1", xmlType.getCode());
        if (group != null) {
            return group;
        }
        group = this.attributeGroupService.getGenericDao().getEntityFactory().getByIface(AttributeGroup.class);
        group.setGuid(xmlType.getCode());
        group.setCode(xmlType.getCode());
        return group;
    }

    @Override
    protected EntityImportModeType determineImportMode(final AttributeGroupType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final AttributeGroup domain) {
        return domain.getAttributegroupId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param attributeGroupService group service
     */
    public void setAttributeGroupService(final AttributeGroupService attributeGroupService) {
        this.attributeGroupService = attributeGroupService;
    }
}

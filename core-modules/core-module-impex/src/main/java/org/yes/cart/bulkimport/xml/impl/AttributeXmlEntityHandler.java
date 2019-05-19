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
import org.yes.cart.bulkimport.xml.internal.AttributeType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.AttributeGroupService;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.EtypeService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class AttributeXmlEntityHandler extends AbstractXmlEntityHandler<AttributeType, Attribute> implements XmlEntityImportHandler<AttributeType, Attribute> {

    private AttributeService attributeService;
    private AttributeGroupService attributeGroupService;
    private EtypeService etypeService;

    public AttributeXmlEntityHandler() {
        super("attribute");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final Attribute attribute) {
        this.attributeService.delete(attribute);
        this.attributeService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final Attribute domain, final AttributeType xmlType, final EntityImportModeType mode) {
        domain.setMandatory(xmlType.isMandatory());
        domain.setSecure(xmlType.isSecure());
        domain.setAllowduplicate(xmlType.isAllowDuplicate());
        domain.setAllowfailover(xmlType.isAllowFailover());
        domain.setStore(xmlType.isStore());
        domain.setSearch(xmlType.isSearch());
        domain.setPrimary(xmlType.isPrimary());
        domain.setNavigation(xmlType.isNavigation());
        domain.setVal(xmlType.getVal());
        domain.setRegexp(xmlType.getRegexp());
        domain.setValidationFailedMessage(processI18n(xmlType.getValidationFailedMessage(), domain.getValidationFailedMessage()));
        if (xmlType.getRank() != null) {
            domain.setRank(xmlType.getRank());
        }
        domain.setChoiceData(processI18n(xmlType.getChoiceData(), domain.getChoiceData()));
        domain.setName(xmlType.getName());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        domain.setDescription(xmlType.getDescription());
        if (domain.getAttributeId() == 0L) {
            this.attributeService.create(domain);
        } else {
            this.attributeService.update(domain);
        }
        this.attributeService.getGenericDao().flush();
        this.attributeService.getGenericDao().evict(domain);
    }

    @Override
    protected Attribute getOrCreate(final JobStatusListener statusListener, final AttributeType xmlType) {
        Attribute attribute = this.attributeService.findSingleByCriteria(" where e.code = ?1", xmlType.getCode());
        if (attribute != null) {
            return attribute;
        }
        attribute = this.attributeService.getGenericDao().getEntityFactory().getByIface(Attribute.class);
        attribute.setGuid(xmlType.getCode());
        attribute.setCode(xmlType.getCode());
        attribute.setAttributeGroup(this.attributeGroupService.getAttributeGroupByCode(xmlType.getGroup()));
        attribute.setEtype(this.etypeService.findSingleByCriteria(" where e.businesstype = ?1", xmlType.getEtype()));
        return attribute;
    }

    @Override
    protected EntityImportModeType determineImportMode(final AttributeType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Attribute domain) {
        return domain.getAttributeId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param attributeService attribute service
     */
    public void setAttributeService(final AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    /**
     * Spring IoC.
     *
     * @param attributeGroupService group service
     */
    public void setAttributeGroupService(final AttributeGroupService attributeGroupService) {
        this.attributeGroupService = attributeGroupService;
    }

    /**
     * Spring IoC.
     *
     * @param etypeService etype service
     */
    public void setEtypeService(final EtypeService etypeService) {
        this.etypeService = etypeService;
    }
}

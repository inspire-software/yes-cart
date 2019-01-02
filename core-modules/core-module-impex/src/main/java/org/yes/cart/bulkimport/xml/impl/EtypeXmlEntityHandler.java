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
import org.yes.cart.bulkimport.xml.internal.ETypeType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.domain.entity.Etype;
import org.yes.cart.service.domain.EtypeService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class EtypeXmlEntityHandler extends AbstractXmlEntityHandler<ETypeType, Etype> implements XmlEntityImportHandler<ETypeType> {

    private EtypeService etypeService;

    public EtypeXmlEntityHandler() {
        super("e-type");
    }

    @Override
    protected void delete(final Etype type) {
        this.etypeService.delete(type);
        this.etypeService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final Etype domain, final ETypeType xmlType, final EntityImportModeType mode) {
        domain.setJavatype(xmlType.getJavaType());
        domain.setBusinesstype(xmlType.getBusinessType());
        if (domain.getEtypeId() == 0L) {
            this.etypeService.create(domain);
        } else {
            this.etypeService.update(domain);
        }
        this.etypeService.getGenericDao().flush();
        this.etypeService.getGenericDao().evict(domain);
    }

    @Override
    protected Etype getOrCreate(final ETypeType xmlType) {
        Etype type = this.etypeService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (type != null) {
            return type;
        }
        type = this.etypeService.getGenericDao().getEntityFactory().getByIface(Etype.class);
        type.setGuid(xmlType.getGuid());
        return type;
    }

    @Override
    protected EntityImportModeType determineImportMode(final ETypeType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Etype domain) {
        return domain.getEtypeId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param etypeService e-type service
     */
    public void setEtypeService(final EtypeService etypeService) {
        this.etypeService = etypeService;
    }
}

/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.bulkimport.xml.internal.DataDescriptorType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.DataDescriptorService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class DataDescriptorXmlEntityHandler extends AbstractXmlEntityHandler<DataDescriptorType, DataDescriptor> implements XmlEntityImportHandler<DataDescriptorType, DataDescriptor> {

    private DataDescriptorService dataDescriptorService;

    public DataDescriptorXmlEntityHandler() {
        super("data-descriptor");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final DataDescriptor descriptor) {
        this.dataDescriptorService.delete(descriptor);
        this.dataDescriptorService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final DataDescriptor domain, final DataDescriptorType xmlType, final EntityImportModeType mode) {

        domain.setType(xmlType.getType().value());
        domain.setValue(xmlType.getValue());

        if (domain.getDatadescriptorId() == 0L) {
            this.dataDescriptorService.create(domain);
        } else {
            this.dataDescriptorService.update(domain);
        }
        this.dataDescriptorService.getGenericDao().flush();
        this.dataDescriptorService.getGenericDao().evict(domain);
    }

    @Override
    protected DataDescriptor getOrCreate(final JobStatusListener statusListener, final DataDescriptorType xmlType) {
        DataDescriptor descriptor = this.dataDescriptorService.findSingleByCriteria(" where e.name = ?1", xmlType.getName());
        if (descriptor != null) {
            return descriptor;
        }
        descriptor = this.dataDescriptorService.getGenericDao().getEntityFactory().getByIface(DataDescriptor.class);
        descriptor.setCreatedBy(xmlType.getCreatedBy());
        descriptor.setCreatedTimestamp(processInstant(xmlType.getCreatedTimestamp()));
        descriptor.setName(xmlType.getName());
        return descriptor;
    }

    @Override
    protected EntityImportModeType determineImportMode(final DataDescriptorType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final DataDescriptor domain) {
        return domain.getDatadescriptorId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param dataDescriptorService data descriptor service
     */
    public void setDataDescriptorService(final DataDescriptorService dataDescriptorService) {
        this.dataDescriptorService = dataDescriptorService;
    }

}

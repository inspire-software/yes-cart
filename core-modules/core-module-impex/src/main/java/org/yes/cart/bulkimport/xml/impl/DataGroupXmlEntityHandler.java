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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.CollectionImportModeType;
import org.yes.cart.bulkimport.xml.internal.DataDescriptorType;
import org.yes.cart.bulkimport.xml.internal.DataGroupType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.domain.entity.DataGroup;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.DataGroupService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class DataGroupXmlEntityHandler extends AbstractXmlEntityHandler<DataGroupType, DataGroup> implements XmlEntityImportHandler<DataGroupType, DataGroup> {

    private DataGroupService dataGroupService;

    private XmlEntityImportHandler<DataDescriptorType, DataDescriptor> descriptorXmlEntityImportHandler;

    public DataGroupXmlEntityHandler() {
        super("data-group");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final DataGroup datagroup) {
        this.dataGroupService.delete(datagroup);
        this.dataGroupService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final DataGroup domain, final DataGroupType xmlType, final EntityImportModeType mode) {

        domain.setType(xmlType.getType().value());
        domain.setQualifier(xmlType.getQualifier());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));

        processDataDescriptors(statusListener, domain, xmlType);

        if (domain.getDatagroupId() == 0L) {
            this.dataGroupService.create(domain);
        } else {
            this.dataGroupService.update(domain);
        }
        this.dataGroupService.getGenericDao().flush();
        this.dataGroupService.getGenericDao().evict(domain);
    }

    private void processDataDescriptors(final JobStatusListener statusListener, final DataGroup domain, final DataGroupType xmlType) {

        if (xmlType.getDescriptors() != null) {

            final CollectionImportModeType collectionMode = xmlType.getDescriptors().getImportMode() != null ? xmlType.getDescriptors().getImportMode() : CollectionImportModeType.REPLACE;
            if (collectionMode == CollectionImportModeType.REPLACE) {
                domain.setDescriptors(null);
            }

            final List<String> groupDescriptors = new ArrayList<>();
            if (domain.getDescriptors() != null) {
                final String[] items = StringUtils.split(domain.getDescriptors(), ',');
                if (items != null) {
                    for (final String name : items) {
                        groupDescriptors.add(name.trim());
                    }
                }
            }

            int insertIndex = groupDescriptors.size();
            for (final DataDescriptorType dataDescriptor : xmlType.getDescriptors().getDataDescriptor()) {
                final EntityImportModeType itemMode = dataDescriptor.getImportMode() != null ? dataDescriptor.getImportMode() : EntityImportModeType.MERGE;
                if (itemMode != EntityImportModeType.DELETE) {

                    processDataDescriptorsSave(statusListener, domain, dataDescriptor);

                    final String descName = dataDescriptor.getName().trim();
                    int pos = groupDescriptors.indexOf(descName);
                    if (pos == -1) { // adding new
                        if (insertIndex >= groupDescriptors.size()) {
                            groupDescriptors.add(descName);
                            insertIndex = groupDescriptors.size();
                        } else {
                            groupDescriptors.add(insertIndex, descName);
                        }
                    } else {
                        insertIndex = pos + 1; // desc exists, so keep the insertIndex as one after
                    }
                }
            }
            domain.setDescriptors(StringUtils.join(groupDescriptors, ','));

        }

    }

    private void processDataDescriptorsSave(final JobStatusListener statusListener, final DataGroup domain, final DataDescriptorType dataDescriptor) {

        if (StringUtils.isNotBlank(dataDescriptor.getValue())) {

            descriptorXmlEntityImportHandler.handle(statusListener, null, (ImpExTuple) new XmlImportTupleImpl(dataDescriptor.getName(), dataDescriptor), null, null);

        }

    }

    @Override
    protected DataGroup getOrCreate(final JobStatusListener statusListener, final DataGroupType xmlType) {
        DataGroup group = this.dataGroupService.findSingleByCriteria(" where e.name = ?1", xmlType.getName());
        if (group != null) {
            return group;
        }
        group = this.dataGroupService.getGenericDao().getEntityFactory().getByIface(DataGroup.class);
        group.setCreatedBy(xmlType.getCreatedBy());
        group.setCreatedTimestamp(processInstant(xmlType.getCreatedTimestamp()));
        group.setName(xmlType.getName());
        return group;
    }

    @Override
    protected EntityImportModeType determineImportMode(final DataGroupType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final DataGroup domain) {
        return domain.getDatagroupId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param dataGroupService data group service
     */
    public void setDataGroupService(final DataGroupService dataGroupService) {
        this.dataGroupService = dataGroupService;
    }

    /**
     * Spring IoC.
     *
     * @param descriptorXmlEntityImportHandler data descriptor handler
     */
    public void setDescriptorXmlEntityImportHandler(final XmlEntityImportHandler<DataDescriptorType, DataDescriptor> descriptorXmlEntityImportHandler) {
        this.descriptorXmlEntityImportHandler = descriptorXmlEntityImportHandler;
    }
}

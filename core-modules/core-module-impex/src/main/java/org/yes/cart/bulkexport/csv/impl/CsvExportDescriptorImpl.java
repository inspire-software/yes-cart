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

package org.yes.cart.bulkexport.csv.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkexport.csv.CsvExportColumn;
import org.yes.cart.bulkexport.csv.CsvExportDescriptor;
import org.yes.cart.bulkexport.csv.CsvExportFile;
import org.yes.cart.bulkexport.model.ExportColumn;
import org.yes.cart.bulkexport.model.ExportContext;

import java.io.Serializable;
import java.util.*;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 13:25
 */
public class CsvExportDescriptorImpl implements CsvExportDescriptor, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(CsvExportDescriptorImpl.class);

    private CsvExportFile exportFileDescriptor;

    private Collection<CsvExportColumn> columns;

    private Map<String, ExportColumn> columnByName;
    private Map<String, List<ExportColumn>> columnsByType;

    private ExportContext context;
    private String entityType;
    private Class entityTypeClass;

    private String selectSql;

    private boolean initialised = false;

    /**
     * Default constructor.
     */
    public CsvExportDescriptorImpl() {
        exportFileDescriptor = new CsvExportFileImpl();
        columns = new ArrayList<CsvExportColumn>();
        context = new CsvExportContextImpl();
    }

    /** {@inheritDoc} */
    public ExportContext getContext() {
        if (context == null) {
            context = new CsvExportContextImpl();
        }
        return context;
    }

    /**
     * @param context import context
     */
    public void setContext(final ExportContext context) {
        this.context = context;
    }

    /** {@inheritDoc} */
    public String getEntityType() {
        return entityType;
    }

    /** {@inheritDoc} */
    public Class getEntityTypeClass() {
        if (entityTypeClass == null) {
            if (StringUtils.isNotBlank(entityType)) {
                try {
                    entityTypeClass = Class.forName(entityType);
                } catch (ClassNotFoundException e) {
                    LOG.error("Unable to work out entity type for descriptor {}", this);
                    entityTypeClass = Object.class;
                }
            } else {
                LOG.error("Entity type is not specified for descriptor {}", this);
                entityTypeClass = Object.class;
            }
        }
        return entityTypeClass;
    }

    /**
     * @param entityInterface entity interface for factory
     */
    public void setEntityType(final String entityInterface) {
        this.entityType = entityInterface;
    }

    /** {@inheritDoc} */
    public String getSelectSql() {
        return selectSql;
    }

    /**
     * @param selectSql select SQL to lookup existing records
     */
    public void setSelectSql(final String selectSql) {
        this.selectSql = selectSql;
    }

    /**
     * {@inheritDoc}
     */
    public CsvExportFile getExportFileDescriptor() {
        return exportFileDescriptor;
    }

    /**
     * Set the {@link org.yes.cart.bulkexport.model.ExportFile}
     * for more details.
     *
     * @param exportFileDescriptor import file descriptor.
     */
    protected void setExportFileDescriptor(CsvExportFile exportFileDescriptor) {
        this.exportFileDescriptor = exportFileDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ExportColumn> getColumns() {
        if (!initialised) {
            this.reloadMappings();
        }
        if (columns == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection((Collection) columns);
    }

    /**
     * {@inheritDoc}
     */
    public ExportColumn getColumn(final String columnName) {
        if (!initialised) {
            this.reloadMappings();
        }
        return columnByName.get(columnName);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ExportColumn> getColumns(String fieldType) {
        if (!initialised) {
            this.reloadMappings();
        }
        final Collection<ExportColumn> cols = columnsByType.get(fieldType);
        if (cols == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(cols);
    }

    /**
     * Set the collection of export columns.
     *
     * @param columns collection of export columns to set.
     */
    protected void setColumns(Collection<CsvExportColumn> columns) {
        this.columns = new ArrayList<CsvExportColumn>();
        this.columns.addAll(columns);
        this.reloadMappings();
    }

    private void reloadMappings() {
        initialised = true;
        columnByName = new HashMap<String, ExportColumn>();
        columnsByType = new HashMap<String, List<ExportColumn>>();

        for (CsvExportColumn exportColumn : columns) {
            exportColumn.setParentDescriptor(this);
            List<ExportColumn> byType = columnsByType.get(exportColumn.getFieldType());
            if (byType == null) {
                byType = new ArrayList<ExportColumn>();
                columnsByType.put(exportColumn.getFieldType(), byType);
            }
            byType.add(exportColumn);
            if (exportColumn.getName() != null && exportColumn.getName().length() > 0) {
                columnByName.put(exportColumn.getName(), exportColumn);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "CsvExportDescriptorImpl{" +
                "exportFileDescriptor=" + exportFileDescriptor +
                ", exportColumns=" + columns +
                ", columnByName=" + columnByName +
                ", columnsByType=" + columnsByType +
                ", entityType='" + entityType + '\'' +
                ", selectSql='" + selectSql + '\'' +
                ", initialised=" + initialised +
                '}';
    }
}

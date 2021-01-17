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

package org.yes.cart.bulkexport.csv.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkexport.csv.CsvExportColumn;
import org.yes.cart.bulkexport.csv.CsvExportContext;
import org.yes.cart.bulkexport.csv.CsvExportDescriptor;
import org.yes.cart.bulkexport.csv.CsvExportFile;

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

    private Map<String, CsvExportColumn> columnByName;
    private Map<String, List<CsvExportColumn>> columnsByType;

    private CsvExportContext context;
    private String entityType;
    private Class entityTypeClass;

    private String selectCmd;

    private CsvExportDescriptor parentDescriptor;

    private boolean initialised = false;

    private String source;

    /**
     * Default constructor.
     */
    public CsvExportDescriptorImpl() {
        exportFileDescriptor = new CsvExportFileImpl();
        columns = new ArrayList<>();
        context = new CsvExportContextImpl();
    }

    /** {@inheritDoc} */
    @Override
    public CsvExportContext getContext() {
        if (context == null) {
            context = new CsvExportContextImpl();
        }
        return context;
    }

    /**
     * @param context import context
     */
    public void setContext(final CsvExportContext context) {
        this.context = context;
    }

    /** {@inheritDoc} */
    @Override
    public String getEntityType() {
        return entityType;
    }

    /** {@inheritDoc} */
    @Override
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
    @Override
    public String getSelectCmd() {
        return selectCmd;
    }

    /**
     * @param selectCmd select SQL to lookup existing records
     */
    public void setSelectCmd(final String selectCmd) {
        this.selectCmd = selectCmd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public Collection<CsvExportColumn> getColumns() {
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
    @Override
    public CsvExportColumn getColumn(final String columnName) {
        if (!initialised) {
            this.reloadMappings();
        }
        return columnByName.get(columnName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<CsvExportColumn> getColumns(String fieldType) {
        if (!initialised) {
            this.reloadMappings();
        }
        final Collection<CsvExportColumn> cols = columnsByType.get(fieldType);
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
        this.columns = new ArrayList<>();
        this.columns.addAll(columns);
        this.reloadMappings();
    }

    private void reloadMappings() {
        initialised = true;
        columnByName = new HashMap<>();
        columnsByType = new HashMap<>();

        for (CsvExportColumn exportColumn : columns) {
            exportColumn.setParentDescriptor(this);
            if (exportColumn.getDescriptor() != null) {
                exportColumn.getDescriptor().setParentDescriptor(this);
            }
            final List<CsvExportColumn> byType = columnsByType.computeIfAbsent(exportColumn.getFieldType(), k -> new ArrayList<>());
            byType.add(exportColumn);
            if (exportColumn.getName() != null && exportColumn.getName().length() > 0) {
                columnByName.put(exportColumn.getName(), exportColumn);
            }
        }
        if (this.parentDescriptor != null) {
            for (CsvExportColumn parentColumn : this.parentDescriptor.getColumns()) {
                final List<CsvExportColumn> byType = columnsByType.computeIfAbsent(parentColumn.getFieldType(), k -> new ArrayList<>());
                byType.add(parentColumn);
                if (parentColumn.getName() != null && parentColumn.getName().length() > 0) {
                    columnByName.put("parent." + parentColumn.getName(), parentColumn);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CsvExportDescriptor getParentDescriptor() {
        return parentDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParentDescriptor(final CsvExportDescriptor parentDescriptor) {
        this.parentDescriptor = parentDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSource() {
        return source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSource(final String source) {
        this.source = source;
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
                ", selectSql='" + selectCmd + '\'' +
                ", initialised=" + initialised +
                ", source='" + source + '\'' +
                '}';
    }
}

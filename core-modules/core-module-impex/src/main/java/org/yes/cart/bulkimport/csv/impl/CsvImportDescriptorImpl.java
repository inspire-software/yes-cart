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

package org.yes.cart.bulkimport.csv.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportContext;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.csv.CsvImportFile;

import java.io.Serializable;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 12:06 PM
 */
public class CsvImportDescriptorImpl implements CsvImportDescriptor, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(CsvImportDescriptorImpl.class);

    private CsvImportFile importFileDescriptor;

    private Collection<CsvImportColumn> columns;

    private CsvImportColumn pkColumn;
    private Map<String, CsvImportColumn> columnByName;
    private Map<String, List<CsvImportColumn>> columnsByType;

    private String importDirectory;

    private ImportMode mode;
    private CsvImportContext context;
    private String entityType;
    private Class entityTypeClass;

    private String selectCmd;
    private String insertCmd;
    private String deleteCmd;

    private boolean initialised = false;

    /**
     * Default constructor.
     */
    public CsvImportDescriptorImpl() {
        importFileDescriptor = new CsvImportFileImpl();
        columns = new ArrayList<>();
        mode = ImportMode.MERGE;
        context = new CsvImportContextImpl();
    }

    /** {@inheritDoc} */
    @Override
    public ImportMode getMode() {
        if (mode == null) {
            mode = ImportMode.MERGE;
        }
        return mode;
    }

    /**
     * @param mode import mode
     */
    public void setMode(final ImportMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Invalid import mode");
        }
        this.mode = mode;
    }

    /** {@inheritDoc} */
    @Override
    public String getModeName() {
        return mode.name();
    }

    /**
     * @param mode import mode name
     */
    public void setModeName(final String mode) {
        try {
            this.mode = ImportMode.valueOf(mode);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Invalid import mode", iae);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CsvImportContext getContext() {
        if (context == null) {
            context = new CsvImportContextImpl();
        }
        return context;
    }

    /**
     * @param context import context
     */
    public void setContext(final CsvImportContext context) {
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

    /** {@inheritDoc} */
    @Override
    public String getInsertCmd() {
        return insertCmd;
    }

    /**
     * @param insertCmd insert SQL for quick and dirty inserts
     */
    public void setInsertCmd(final String insertCmd) {
        this.insertCmd = insertCmd;
    }

    /** {@inheritDoc} */
    @Override
    public String getDeleteCmd() {
        return deleteCmd;
    }

    /**
     * @param deleteCmd delete SQL used for DELETE mode
     */
    public void setDeleteCmd(final String deleteCmd) {
        this.deleteCmd = deleteCmd;
    }

    CsvImportColumn getPrimaryKeyColumn() {
        if (!initialised) {
            this.reloadMappings();
        }
        return pkColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CsvImportFile getImportFileDescriptor() {
        return importFileDescriptor;
    }

    /**
     * Set the {@link org.yes.cart.bulkimport.model.ImportFile}
     * for more details.
     *
     * @param importFileDescriptor import file descriptor.
     */
    protected void setImportFileDescriptor(CsvImportFile importFileDescriptor) {
        this.importFileDescriptor = importFileDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<CsvImportColumn> getColumns() {
        if (!initialised) {
            this.reloadMappings();
        }
        if (columns == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(columns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CsvImportColumn getColumn(final String columnName) {
        if (!initialised) {
            this.reloadMappings();
        }
        return columnByName.get(columnName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<CsvImportColumn> getColumns(String fieldType) {
        if (!initialised) {
            this.reloadMappings();
        }
        final Collection<CsvImportColumn> cols = columnsByType.get(fieldType);
        if (cols == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(cols);
    }

    /**
     * Set the collection of import columns.
     *
     * @param columns collection of import columns to set.
     */
    protected void setColumns(Collection<CsvImportColumn> columns) {
        this.columns = new ArrayList<>();
        this.columns.addAll(columns);
        this.reloadMappings();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getImportDirectory() {
        return importDirectory;
    }

    /**
     * Set the import folder.
     *
     * @param importDirectory import folder to use.
     */
    @Override
    public void setImportDirectory(final String importDirectory) {
        this.importDirectory = importDirectory;
    }

    private void reloadMappings() {
        initialised = true;
        pkColumn = null;
        columnByName = new HashMap<>();
        columnsByType = new HashMap<>();

        for (CsvImportColumn importColumn : columns) {
            importColumn.setParentDescriptor(this);
            final List<CsvImportColumn> byType = columnsByType.computeIfAbsent(importColumn.getFieldType(), k -> new ArrayList<>());
            byType.add(importColumn);
            if (pkColumn == null && importColumn.getLookupQuery() != null && CsvImpExColumn.FIELD.equals(importColumn.getFieldType())) {
                pkColumn = importColumn;
            }
            if (importColumn.getName() != null && importColumn.getName().length() > 0) {
                columnByName.put(importColumn.getName(), importColumn);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "CsvImportDescriptorImpl{" +
                "importFileDescriptor=" + importFileDescriptor +
                ", importColumns=" + columns +
                ", pkColumn=" + pkColumn +
                ", columnByName=" + columnByName +
                ", columnsByType=" + columnsByType +
                ", importDirectory='" + importDirectory + '\'' +
                ", entityType='" + entityType + '\'' +
                ", selectSql='" + selectCmd + '\'' +
                ", insertSql='" + insertCmd + '\'' +
                ", deleteSql='" + deleteCmd + '\'' +
                ", initialised=" + initialised +
                '}';
    }
}
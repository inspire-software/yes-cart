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

package org.yes.cart.bulkimport.csv.impl;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.bulkcommon.service.ImportService;
import org.yes.cart.bulkcommon.service.support.query.LookUpQuery;
import org.yes.cart.bulkcommon.service.support.query.LookUpQueryParameterStrategy;
import org.yes.cart.bulkimport.csv.CsvFileReader;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.csv.CsvImportTuple;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.service.impl.AbstractImportService;
import org.yes.cart.bulkimport.service.support.csv.EntityCacheKeyStrategy;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.utils.MessageFormatUtils;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * Perform import from csv files. Import based on xml import description, that include
 * import file mask and column description. Csv import support following
 * import types:
 * <p/>
 * simple - when csv row can be present in table without any modification
 * with fk - when some of the column need to use lookup query to find actual value, that base on value in csv
 * example carrier and carries sla, carrier sla need to get Carrier object as foreign key.
 * with sub import - one of the column has a underlaying table values in csv
 * example - shop and shop url, in this case {@link CsvImportColumn} has a
 * {@link ImportDescriptor}. At this moment rows in cell are split by comma by default.
 */
public class CsvBulkImportServiceImpl extends AbstractImportService<CsvImportDescriptor> implements ImportService {

    private static final String DELETE_COUNTER = "Records deleted";
    private static final String MERGE_COUNTER = "Records upserted";
    private static final String SKIP_COUNTER = "Records skipped";

    private GenericDAO<Object, Long> genericDAO;

    private GenericConversionService extendedConversionService;

    private CsvValueAdapter valueDataAdapter;

    private CsvValueAdapter valueStringAdapter;
    private LookUpQueryParameterStrategy<CsvImportDescriptor, CsvImportTuple, CsvValueAdapter> descriptorInsertLookUpQueryParameterStrategy;
    private LookUpQueryParameterStrategy<CsvImportDescriptor, CsvImportTuple, CsvValueAdapter> columnLookUpQueryParameterStrategy;
    private EntityCacheKeyStrategy cacheKey;

    public CsvBulkImportServiceImpl(final FederationFacade federationFacade) {
        super(federationFacade);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSingleFileImportInternal(final JobStatusListener statusListener,
                                           final File fileToImport,
                                           final String importDescriptorName,
                                           final CsvImportDescriptor importDescriptor) throws Exception {


        final CsvFileReader csvFileReader = new CsvFileReaderImpl();

        try {


            final Map<String, Pair<Object, Boolean>> entityCache = new HashMap<>();

            final CsvImportDescriptor.ImportMode mode = importDescriptor.getMode();
            statusListener.notifyInfo("import file : {} in {} mode", fileToImport.getAbsolutePath(), mode);

            final String filename = fileToImport.getName();
            long lineNumber = 0;
            int blankLines = 0;

            csvFileReader.open(
                    fileToImport.getAbsolutePath(),
                    importDescriptor.getImportFileDescriptor().getColumnDelimiter(),
                    importDescriptor.getImportFileDescriptor().getTextQualifier(),
                    importDescriptor.getImportFileDescriptor().getFileEncoding(),
                    importDescriptor.getImportFileDescriptor().isIgnoreFirstLine());

            String[] line;
            while ((line = csvFileReader.readLine()) != null) {
                if (isNotBlank(line)) {
                    final CsvImportTuple tuple = new CsvImportTupleImpl(filename, lineNumber++, line);
                    if (mode == CsvImportDescriptor.ImportMode.DELETE) {
                        doImportDelete(statusListener, tuple, importDescriptorName, importDescriptor);
                    } else {
                        doImportMerge(statusListener, tuple, importDescriptorName, importDescriptor, null, entityCache);
                    }
                } else {
                    blankLines++;
                }
            }
            statusListener.notifyInfo("total data lines : {} ({})",
                    (importDescriptor.getImportFileDescriptor().isIgnoreFirstLine() ? csvFileReader.getRowsRead() - 1 : csvFileReader.getRowsRead()),
                    fileToImport.getAbsolutePath());
            if (blankLines > 0) {
                statusListener.notifyInfo("blank lines detected : {} ({})",
                        (importDescriptor.getImportFileDescriptor().isIgnoreFirstLine() ? csvFileReader.getRowsRead() - 1 : csvFileReader.getRowsRead()),
                        fileToImport.getAbsolutePath());
            }

        } finally {

            csvFileReader.close();

        }

    }

    private boolean isNotBlank(final String[] line) {
        if (line == null || line.length == 0) {
            return false;
        }
        for (final String element : line) {
            if (StringUtils.isNotEmpty(element)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Delete single line.
     */
    void doImportDelete(final JobStatusListener statusListener,
                        final CsvImportTuple tuple,
                        final String csvImportDescriptorName,
                        final CsvImportDescriptor descriptor) throws Exception {
        List<Object> objects = null;
        try {


            if (descriptor.getDeleteCmd() != null) {

                // No need to validate sub imports
                validateAccessBeforeUpdate(null, null); // only allowed by system admins

                executeNativeQuery(descriptor, null, tuple, descriptor.getDeleteCmd());

                statusListener.count(DELETE_COUNTER);

            } else {

                objects = getExistingEntities(descriptor, descriptor.getSelectCmd(), null, tuple);

                if (CollectionUtils.isNotEmpty(objects)) {

                    for (final Object object : objects) {
                        /*
                            Note: for correct data federation processing we need ALL-OR-NOTHING update for all import.
                                  Once validation fails we fail the whole import with a rollback. Necessary to facilitate
                                  objects with complex relationships to shop (e.g. products, SKU)
                         */

                        // No need to validate sub imports
                        // Preliminary validation - not always applicable for transient object (e.g. products need category assignments)
                        validateAccessBeforeUpdate(object, descriptor.getEntityTypeClass());

                        genericDAO.delete(object);

                        genericDAO.flushClear();

                        statusListener.count(DELETE_COUNTER);
                    }

                }

            }
            statusListener.notifyPing("Deleting tuple: " + tuple.getSourceId()); // make sure we do not time out

        } catch (AccessDeniedException ade) {

            statusListener.notifyError(
                    "Access denied during import row : {} \ndescriptor {} \nobject is {}",
                    ade,
                    tuple,
                    csvImportDescriptorName,
                    objects);
            genericDAO.clear();

            throw new Exception(ade.getMessage(), ade);


        } catch (Exception e) {

            statusListener.notifyError(
                    "during import row : {} \ndescriptor {} \nerror {}\nobject is {}",
                    e,
                    tuple,
                    csvImportDescriptorName,
                    e.getMessage(),
                    objects
                );
            genericDAO.clear();

            throw e;
        }
    }


    /*
     * Import single line.
     * This method can be called recursive in case of sub imports.
     */
    void doImportMerge(final JobStatusListener statusListener,
                       final CsvImportTuple tuple,
                       final String csvImportDescriptorName,
                       final CsvImportDescriptor descriptor,
                       final Object masterObject,
                       final Map<String, Pair<Object, Boolean>> entityCache) throws Exception {
        Object object = null;
        try {


            if (descriptor.getInsertCmd() != null) {

                if (descriptor.getMode() == CsvImportDescriptor.ImportMode.INSERT_ONLY) {

                    // this is dirty hack , because of import speed
                    if (masterObject == null) {
                        // No need to validate sub imports
                        validateAccessBeforeUpdate(null, null); // only allowed by system admins
                    }
                    executeNativeQuery(descriptor, masterObject, tuple, descriptor.getInsertCmd());

                    statusListener.count(MERGE_COUNTER);

                } else {

                    throw new IllegalArgumentException("Insert SQL can only be specified in INSERT_ONLY mode (Current mode: "
                            + descriptor.getMode() + ") ... skipping insert");

                }

            } else {

                final boolean restrictInsert = descriptor.getMode() == CsvImportDescriptor.ImportMode.UPDATE_ONLY;
                final boolean restrictUpdate = descriptor.getMode() == CsvImportDescriptor.ImportMode.INSERT_ONLY;

                final Pair<Object, Boolean> objectAndState = getEntity(tuple, null, masterObject, descriptor, entityCache);
                object = objectAndState != null ? objectAndState.getFirst() : null;
                final boolean insert = objectAndState != null ? objectAndState.getSecond() : false;

                if (insert && restrictInsert) {

                    statusListener.notifyPing("Skipping tuple (insert restricted): " + tuple);

                    statusListener.count(SKIP_COUNTER);

                } else if (!insert && restrictUpdate) {

                    statusListener.notifyPing("Skipping tuple (update restricted): " + tuple);

                    statusListener.count(SKIP_COUNTER);

                } else {

                    final boolean valueChanged = fillEntityFields(tuple, object, insert, descriptor.getColumns(CsvImpExColumn.FIELD));
                    final Boolean fkChanged = fillEntityForeignKeys(tuple, object, insert, descriptor.getColumns(CsvImpExColumn.FK_FIELD), masterObject, descriptor, entityCache);

                    if (fkChanged == null) {

                        statusListener.notifyPing("Skipping tuple (unresolved foreign key): " + tuple);

                        statusListener.count(SKIP_COUNTER);

                    } else {

                        /*
                            Note: for correct data federation processing we need ALL-OR-NOTHING update for all import.
                                  Once validation fails we fail the whole import with a rollback. Necessary to facilitate
                                  objects with complex relationships to shop (e.g. products, SKU)
                         */

                        if (masterObject == null) {
                            // No need to validate sub imports
                            // Preliminary validation - not always applicable for transient object (e.g. products need category assignments)
                            validateAccessBeforeUpdate(object, descriptor.getEntityTypeClass());
                        }

                        if (valueChanged || fkChanged) {

                            genericDAO.saveOrUpdate(object); // If no changed are made then we do not need to save

                            statusListener.count(MERGE_COUNTER);

                        } else {

                            statusListener.notifyPing("Skipping tuple (no change): " + tuple.getSourceId());

                            statusListener.count(SKIP_COUNTER);

                        }

                        performSubImport(statusListener, tuple, csvImportDescriptorName, descriptor, object,
                                descriptor.getColumns(CsvImpExColumn.SLAVE_INLINE_FIELD), entityCache);
                        performSubImport(statusListener, tuple, csvImportDescriptorName, descriptor, object,
                                descriptor.getColumns(CsvImpExColumn.SLAVE_TUPLE_FIELD), entityCache);

                        if (masterObject == null) {
                            // No need to validate sub imports
                            // This validation is after sub imports to facilitate objects with complex relationships to shop (e.g. products)
                            validateAccessAfterUpdate(object, descriptor.getEntityTypeClass());
                        }

                        genericDAO.flushClear();

                    }
                }

            }
            statusListener.notifyPing("Importing tuple: " + tuple.getSourceId()); // make sure we do not time out

        } catch (AccessDeniedException ade) {

            statusListener.notifyError(
                    "Access denied during import row : {} \ndescriptor {} \nobject is {} \nmaster object is {}",
                    tuple,
                    csvImportDescriptorName,
                    object,
                    masterObject
            );
            genericDAO.clear();

            throw new Exception(ade.getMessage(), ade);


        } catch (Exception e) {

            statusListener.notifyError(
                    "during import row : {} \ndescriptor {} \nerror {}\nobject is {} \nmaster object is {}",
                    e,
                    tuple,
                    csvImportDescriptorName,
                    e.getMessage(),
                    object,
                    masterObject
                );
            genericDAO.clear();

            throw e;
        }
    }

    private void executeNativeQuery(final CsvImportDescriptor descriptor,
                                    final Object masterObject,
                                    final CsvImportTuple tuple,
                                    final String queryTemplate) {

        if (queryTemplate.contains(";\n")) {
            for (final String statement : queryTemplate.split(";\n")) {
                if (StringUtils.isNotBlank(statement)) {
                    final LookUpQuery query = descriptorInsertLookUpQueryParameterStrategy.getQuery(
                            descriptor, masterObject, tuple, valueStringAdapter, statement
                    );
                    genericDAO.executeNativeUpdate(query.getQueryString(), query.getParameters());
                }
            }
        } else {
            final LookUpQuery query = descriptorInsertLookUpQueryParameterStrategy.getQuery(
                    descriptor, masterObject, tuple, valueStringAdapter, queryTemplate
            );
            genericDAO.executeNativeUpdate(query.getQueryString(), query.getParameters());
        }
    }

    private void performSubImport(final JobStatusListener statusListener,
                                  final CsvImportTuple tuple,
                                  final String csvImportDescriptorName,
                                  final CsvImportDescriptor importDescriptor,
                                  final Object object,
                                  final Collection<CsvImportColumn> slaves,
                                  final Map<String, Pair<Object, Boolean>> entityCache) throws Exception {
        for (CsvImportColumn slaveTable : slaves) {
            final List<CsvImportTuple> subTuples = tuple.getSubTuples(importDescriptor, slaveTable, valueDataAdapter);
            CsvImportDescriptor innerCsvImportDescriptor = slaveTable.getDescriptor();
            for (CsvImportTuple subTuple : subTuples) {
                doImportMerge(statusListener,
                        subTuple,
                        csvImportDescriptorName,
                        innerCsvImportDescriptor,
                        object,
                        entityCache);
            }
        }
    }


    /**
     * Fill the given entity object with line information using import column descriptions.
     *
     * @param tuple         given csv line
     * @param object        entity object
     * @param insert        entity insert (true), update (false)
     * @param importColumns particular type column collection
     *
     * @return true if this tuple has changes, false otherwise
     *
     * @throws Exception in case if something wrong with reflection (IntrospectionException,
     *                   InvocationTargetException,
     *                   IllegalAccessException)
     */
    private boolean fillEntityFields(final CsvImportTuple tuple,
                                     final Object object,
                                     final boolean insert,
                                     final Collection<CsvImportColumn> importColumns) throws Exception {

        final Class clz = object.getClass();

        PropertyDescriptor propertyDescriptor = null;

        boolean updated = false;

        for (CsvImportColumn importColumn : importColumns) {
            try {
                if (StringUtils.isNotBlank(importColumn.getName())) { //can be just lookup query

                    if (importColumn.isInsertOnly() && !insert) {
                        continue; // skip update since this is insert only
                    }
                    if (importColumn.isUpdateOnly() && insert) {
                        continue; // skip insert since this is update only
                    }

                    Object writeObject = object;

                    if (importColumn.getName().indexOf('.') == -1) {
                        // direct property
                        propertyDescriptor = new PropertyDescriptor(importColumn.getName(), clz);
                    } else {
                        // object path
                        final String[] chain = importColumn.getName().split("\\.");
                        for (int i = 0; i < chain.length - 1; i++) {
                            propertyDescriptor = new PropertyDescriptor(chain[i], writeObject.getClass());
                            writeObject = propertyDescriptor.getReadMethod().invoke(writeObject);
                        }
                        propertyDescriptor = new PropertyDescriptor(chain[chain.length - 1], writeObject.getClass());
                    }


                    Object singleObjectValue = tuple.getColumnValue(importColumn, valueDataAdapter);
                    if (importColumn.getLanguage() != null) {
                        final I18NModel current = (I18NModel) propertyDescriptor.getReadMethod().invoke(object);
                        final I18NModel model = current == null ? new StringI18NModel() : current.copy();
                        model.putValue(importColumn.getLanguage(),
                                singleObjectValue instanceof String && StringUtils.isNotBlank((String) singleObjectValue) ? (String) singleObjectValue : null);

                        singleObjectValue = model.getAllValues().size() > 0 ? model : null;
                    }
                    if (singleObjectValue != null && !propertyDescriptor.getPropertyType().isAssignableFrom(singleObjectValue.getClass())) {
                        // if we have mismatch try on the fly conversion - this happens if someone omits <data-type> for non String values
                        singleObjectValue =
                                extendedConversionService.convert(
                                        singleObjectValue,
                                        TypeDescriptor.valueOf(singleObjectValue.getClass()),
                                        TypeDescriptor.valueOf((propertyDescriptor.getPropertyType())
                                    ));
                    }

                    final Object current = propertyDescriptor.getReadMethod().invoke(writeObject);

                    boolean valueChanged = isValueChanged(singleObjectValue, current);

                    if (valueChanged) {
                        propertyDescriptor.getWriteMethod().invoke(writeObject, singleObjectValue);
                    }

                    updated = updated || valueChanged;

                }
            } catch (Exception exp) {

                final String propName = importColumn.getName();
                final String propType = importColumn.getDataType();

                throw new Exception(MessageFormatUtils.format(
                        "Failed to process property name '{}' type '{}' object is '{}'",
                        propName,
                        propType,
                        object
                ), exp);
            }
        }

        return updated;

    }

    private boolean isValueChanged(final Object singleObjectValue, final Object current) {

        if (singleObjectValue instanceof Identifiable) {

            // Foreign key has been set or has changed
            final Object currentId = current != null ? genericDAO.getEntityIdentifier(current) : null;
            final Object newValueId = genericDAO.getEntityIdentifier(singleObjectValue);


            return currentId == null || !currentId.equals(newValueId);

        } else if (singleObjectValue instanceof BigDecimal) {

            // BigDecimal value has been set or changed
            return current == null || (((BigDecimal) current).compareTo((BigDecimal) singleObjectValue) != 0);

        } else if (singleObjectValue instanceof Date) {

            // Sometimes Date is Timestamp (or other impl, so it does not equal)
            return current == null || ((Date) current).getTime() != ((Date) singleObjectValue).getTime();

        } else if (singleObjectValue != null) {

            // Generic comparison of values (this should be all built-in in Java types)
            return current == null || !current.equals(singleObjectValue);

        } // else single value is null

        // New value is null but current is not
        return current != null;

    }

    /**
     * Fill the given entity object with line information using import column descriptions.
     *
     * @param tuple            given csv line
     * @param object           entity object
     * @param insert           entity insert (true), update (false)
     * @param importColumns    particular type column collection
     * @param masterObject     master object , that set from main import in case of sub import
     * @param importDescriptor import descriptor
     * @param entityCache      runtime cache
     *
     * @return null if this tuple should be skipped, true if there are changes, false otherwise
     *
     * @throws Exception in case if something wrong with reflection (IntrospectionException,
     *                   InvocationTargetException,
     *                   IllegalAccessException)
     */
    private Boolean fillEntityForeignKeys(final CsvImportTuple tuple,
                                          final Object object,
                                          final boolean insert,
                                          final Collection<CsvImportColumn> importColumns,
                                          final Object masterObject,
                                          final CsvImportDescriptor importDescriptor,
                                          final Map<String, Pair<Object, Boolean>> entityCache) throws Exception {

        CsvImportColumn currentColumn = null;
        final Class clz = object.getClass();
        Object singleObjectValue = null;
        boolean updated = false;
        PropertyDescriptor propertyDescriptor = null;

        try {
            for (CsvImportColumn importColumn : importColumns) {

                if (importColumn.isInsertOnly() && !insert) {
                    continue; // skip update since this is insert only
                }
                if (importColumn.isUpdateOnly() && insert) {
                    continue; // skip insert since this is update only
                }

                currentColumn = importColumn;

                if (importColumn.isUseMasterObject()) {
                    singleObjectValue = masterObject;
                } else {
                    final Pair<Object, Boolean> singleObjectValueAndState = getEntity(tuple, importColumn, masterObject, importDescriptor, entityCache);
                    singleObjectValue = singleObjectValueAndState != null ? singleObjectValueAndState.getFirst() : null;
                    if (singleObjectValueAndState != null && singleObjectValueAndState.getSecond()) {
                        if (currentColumn.isSkipUpdateForUnresolved()) {
                            return null; // This is new FK object and we should skip this tuple and not abort with exception
                        }
                        throw new Exception("Unable to resolve entity for tuple " + tuple.getSourceId() + " column + " + importColumn);
                    }
                }
                propertyDescriptor = new PropertyDescriptor(importColumn.getName(), clz);
                final Object oldValue = propertyDescriptor.getReadMethod().invoke(object);

                boolean valueChanged = isValueChanged(singleObjectValue, oldValue);

                if (valueChanged) {
                    propertyDescriptor.getWriteMethod().invoke(object, singleObjectValue);
                }

                updated = updated || valueChanged;

            }

            return updated;

        } catch (Exception exp) {

            final String propName = propertyDescriptor != null ? propertyDescriptor.getName() : null;
            final String propType = propertyDescriptor != null ? propertyDescriptor.getPropertyType().getName() : null;

            throw new Exception(MessageFormatUtils.format(
                    "Failed to process property name '{}' type '{}' object is '{}' caused by column '{}' with value '{}'",
                    propName,
                    propType,
                    object,
                    currentColumn,
                    singleObjectValue
            ), exp);
        }

    }

    /**
     * Get the entity object with following strategy: first attempt - locate
     * existing entity, if it not found create new instance.
     *
     * @param tuple            csv row to get the parameter value for lookup query.
     * @param column           import column, the describe lookup query to locale the entity
     * @param masterObject     in case of subimport will be not null, but will be used with flag only
     * @param importDescriptor import descriptor
     * @param entityCache      runtime cache
     *
     * @return new or existing entity first => object, second => isNew flag
     *
     * @throws ClassNotFoundException in case if entity interface is wrong.
     */
    private Pair<Object, Boolean> getEntity(final CsvImportTuple tuple,
                                            final CsvImportColumn column,
                                            final Object masterObject,
                                            final CsvImportDescriptor importDescriptor,
                                            final Map<String, Pair<Object, Boolean>> entityCache) throws ClassNotFoundException {

        if (column == null) {
            // no caching for prime select
            final Object prime = getExistingEntity(importDescriptor, importDescriptor.getSelectCmd(), masterObject, tuple);
            if (prime == null) {
                return new Pair<>(genericDAO.getEntityFactory().getByKey(importDescriptor.getEntityType()), Boolean.TRUE);
            }
            return new Pair<>(prime, Boolean.FALSE);
        }

        final String key = cacheKey.keyFor(importDescriptor, column, masterObject, tuple, valueStringAdapter);

        Pair<Object, Boolean> object = null;

        if (key != null) {
            object = entityCache.get(key);
        }

        if (object == null) {

            if (column.getLookupQuery() == null || column.getLookupQuery().length() == 0) {
                throw new IllegalArgumentException("Missing look up query for field: " + column.getName()
                        + " at index: " + column.getColumnIndex() + " in tuple: " + tuple);
            }
            final Object existing = getExistingEntity(importDescriptor, column.getLookupQuery(), masterObject, tuple);
            if (existing == null) {
                if (column.getEntityType() != null) {
                    object = new Pair<>(genericDAO.getEntityFactory().getByIface(
                            Class.forName(column.getEntityType())),
                            Boolean.TRUE
                    );
                } else {
                    return null; // no cache for nulls
                }
            } else {
                object = new Pair<>(existing, Boolean.FALSE);
            }
            entityCache.put(key, object);
        }
        return object;
    }


    /**
     * Try to get existing entity for update. In case of sub import master object will be used in parameters if
     * {@link CsvImpExColumn#isUseMasterObject()} set to true.
     *
     * @param importDescriptor descriptor
     * @param queryTemplate    template to use with tuple columns as parameter values
     * @param masterObject in case of subimport will be not null, but will be used with flag only
     * @param tuple        csv row to get the parameter value for lookup query.
     *
     * @return existing entity or null if not found
     */
    private Object getExistingEntity(final CsvImportDescriptor importDescriptor,
                                     final String queryTemplate,
                                     final Object masterObject,
                                     final CsvImportTuple tuple) {

        final LookUpQuery query = columnLookUpQueryParameterStrategy.getQuery(importDescriptor, masterObject, tuple, valueDataAdapter, queryTemplate);
        return genericDAO.findSingleByQuery(query.getQueryString(), query.getParameters());

    }

    /**
     * Try to get existing entity for update. In case of sub import master object will be used in parameters if
     * {@link CsvImportColumn#isUseMasterObject()} set to true.
     *
     * @param importDescriptor descriptor
     * @param queryTemplate    template to use with tuple columns as parameter values
     * @param masterObject in case of subimport will be not null, but will be used with flag only
     * @param tuple        csv row to get the parameter value for lookup query.
     *
     * @return existing entity or null if not found
     */
    private List<Object> getExistingEntities(final CsvImportDescriptor importDescriptor,
                                             final String queryTemplate,
                                             final Object masterObject,
                                             final CsvImportTuple tuple) {

        final LookUpQuery query = columnLookUpQueryParameterStrategy.getQuery(importDescriptor, masterObject, tuple, valueDataAdapter, queryTemplate);
        return genericDAO.findByQuery(query.getQueryString(), query.getParameters());

    }

    /**
     * IoC. Set the {@link org.yes.cart.dao.GenericDAO} instance.
     *
     * @param genericDAO {@link GenericDAO} to use.
     */
    public void setGenericDAO(GenericDAO<Object, Long> genericDAO) {
        this.genericDAO = genericDAO;
    }

    /**
     * IoC. Set {@link GenericConversionService}.
     *
     * @param extendedConversionService {@link GenericConversionService}  to use.
     */
    public void setExtendedConversionService(final GenericConversionService extendedConversionService) {
        this.extendedConversionService = extendedConversionService;
    }

    /**
     * IoC.
     *
     * @param valueDataAdapter {@link CsvValueAdapter}  to use.
     */
    public void setValueDataAdapter(final CsvValueAdapter valueDataAdapter) {
        this.valueDataAdapter = valueDataAdapter;
    }

    /**
     * IoC.
     *
     * @param valueStringAdapter {@link CsvValueAdapter}  to use.
     */
    public void setValueStringAdapter(final CsvValueAdapter valueStringAdapter) {
        this.valueStringAdapter = valueStringAdapter;
    }

    /**
     * IoC.
     *
     * @param cacheKey {@link EntityCacheKeyStrategy}  to use.
     */
    public void setCacheKey(final EntityCacheKeyStrategy cacheKey) {
        this.cacheKey = cacheKey;
    }

    /**
     * IoC.
     *
     * @param descriptorInsertLookUpQueryParameterStrategy {@link LookUpQueryParameterStrategy}  to use.
     */
    public void setDescriptorInsertLookUpQueryParameterStrategy(final LookUpQueryParameterStrategy<CsvImportDescriptor, CsvImportTuple, CsvValueAdapter> descriptorInsertLookUpQueryParameterStrategy) {
        this.descriptorInsertLookUpQueryParameterStrategy = descriptorInsertLookUpQueryParameterStrategy;
    }

    /**
     * IoC.
     *
     * @param columnLookUpQueryParameterStrategy {@link LookUpQueryParameterStrategy}  to use.
     */
    public void setColumnLookUpQueryParameterStrategy(final LookUpQueryParameterStrategy<CsvImportDescriptor, CsvImportTuple, CsvValueAdapter> columnLookUpQueryParameterStrategy) {
        this.columnLookUpQueryParameterStrategy = columnLookUpQueryParameterStrategy;
    }


}
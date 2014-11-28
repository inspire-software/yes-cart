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

package org.yes.cart.bulkimport.csv.impl;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.yes.cart.bulkimport.csv.CsvFileReader;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.csv.CsvImportTuple;
import org.yes.cart.bulkimport.model.*;
import org.yes.cart.bulkimport.service.ImportService;
import org.yes.cart.bulkimport.service.impl.AbstractImportService;
import org.yes.cart.bulkimport.service.support.EntityCacheKeyStrategy;
import org.yes.cart.bulkimport.service.support.LookUpQuery;
import org.yes.cart.bulkimport.service.support.LookUpQueryParameterStrategy;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.util.misc.ExceptionUtil;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
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
 * example - shop and shop url, in this case {@link ImportColumn} has a
 * {@link ImportDescriptor}. At this moment rows in cell are split by comma by default.
 */
public class CsvBulkImportServiceImpl extends AbstractImportService implements ImportService {

    private GenericDAO<Object, Long> genericDAO;

    private GenericConversionService extendedConversionService;

    private ValueAdapter valueDataAdapter;
    private ValueAdapter valueStringAdapter = new CsvPlainStringValueAdapter();
    private final LookUpQueryParameterStrategy descriptorInsert = new CsvDescriptorNativeInsertStrategy();
    private final LookUpQueryParameterStrategy columnLookUp = new CsvColumnLookUpQueryStrategy();
    private EntityCacheKeyStrategy cacheKey;

    public CsvBulkImportServiceImpl(final FederationFacade federationFacade) {
        super(federationFacade);
    }

    /**
     * IoC. Set {@link GenericConversionService}.
     *
     * @param extendedConversionService {@link GenericConversionService}  to use.
     */
    public void setExtendedConversionService(final GenericConversionService extendedConversionService) {
        this.extendedConversionService = extendedConversionService;
        valueDataAdapter = new CsvImportValueAdapter(extendedConversionService);
    }

    /**
     * Perform bulk import.
     * Service has s set of import descriptors, eac of them may perform the import
     * on files. Files selected by regular expression . If <code>fileName</code>
     * not empty, than only one may be imported instead of set, that satisfy
     * regular expressions.
     *
     * @param context job context
     * @return {@link BulkImportResult}
     */
    public BulkImportResult doImport(final JobContext context) {


        final Logger log = ShopCodeContext.getLog(this);
        final JobStatusListener statusListener = context.getListener();
        final Set<String> importedFiles = context.getAttribute(JobContextKeys.IMPORT_FILE_SET);
        final String fileName = context.getAttribute(JobContextKeys.IMPORT_FILE);
        final CsvImportDescriptor csvImportDescriptor = context.getAttribute(JobContextKeys.IMPORT_DESCRIPTOR);
        final String csvImportDescriptorName = context.getAttribute(JobContextKeys.IMPORT_DESCRIPTOR_NAME);

        try {
            entityCache.clear();

            final File[] filesToImport = getFilesToImport(csvImportDescriptor, fileName);
            if (filesToImport == null) {
                final String msgWarn = MessageFormat.format(
                        "no files with mask {0} to import",
                        csvImportDescriptor.getImportFileDescriptor().getFileNameMask());
                statusListener.notifyWarning(msgWarn);
                log.warn(msgWarn);
            } else {
                final String msgInfo = MessageFormat.format(
                        "Import descriptor {0} has {1} file(s) with mask {2} to import",
                        csvImportDescriptorName,
                        filesToImport.length,
                        csvImportDescriptor.getImportFileDescriptor().getFileNameMask());
                statusListener.notifyMessage(msgInfo);
                log.info(msgInfo);
                if (csvImportDescriptor.getSelectSql() == null) {
                    final String msgErr = "import can not be started, because select-sql is empty";
                    log.error(msgErr);
                    statusListener.notifyError(msgErr);
                    return BulkImportResult.ERROR;
                }
                doImport(statusListener, filesToImport, csvImportDescriptorName, csvImportDescriptor, importedFiles);
            }
        } catch (Exception e) {

            /**
             * Programmatically rollback for any error during import - ALL or NOTHING.
             * But we do not throw exception since this is in a separate thread so not point
             * Need to finish gracefully with error status
             */
            if (!TransactionAspectSupport.currentTransactionStatus().isRollbackOnly()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }

            final String msgError = MessageFormat.format(
                    "unexpected error {0}",
                    e.getMessage());
            log.error(msgError, e);
            statusListener.notifyError(msgError);
            return BulkImportResult.ERROR;
        } finally {
            entityCache.clear();
        }
        return BulkImportResult.OK;
    }


    /**
     * Perform import for each file.
     *
     * @param statusListener      error report
     * @param filesToImport       array of files to import
     * @param csvImportDescriptorName file name of the descriptor
     * @param csvImportDescriptor import descriptor.
     * @param importedFiles       imported files.
     */
    void doImport(final JobStatusListener statusListener,
                  final File[] filesToImport,
                  final String csvImportDescriptorName,
                  final CsvImportDescriptor csvImportDescriptor,
                  final Set<String> importedFiles) throws Exception {
        // Need to add all file to the set for proper clean up after job in case exception occurs
        for (File fileToImport : filesToImport) {
            importedFiles.add(fileToImport.getAbsolutePath());
        }
        for (File fileToImport : filesToImport) {
            doImport(statusListener, fileToImport, csvImportDescriptorName, csvImportDescriptor);
        }
    }

    /**
     * Perform import for single file.
     *
     * @param statusListener      error report
     * @param fileToImport        array of files to import
     * @param csvImportDescriptorName file name of the descriptor
     * @param csvImportDescriptor import descriptor.
     */
    void doImport(final JobStatusListener statusListener,
                  final File fileToImport,
                  final String csvImportDescriptorName,
                  final CsvImportDescriptor csvImportDescriptor) throws Exception {

        final Logger log = ShopCodeContext.getLog(this);
        final String msgInfoImp = MessageFormat.format("import file : {0}", fileToImport.getAbsolutePath());
        statusListener.notifyMessage(msgInfoImp);
        log.info(msgInfoImp);

        CsvFileReader csvFileReader = new CsvFileReaderImpl();
        try {
            final String filename = fileToImport.getName();
            long lineNumber = 0;

            csvFileReader.open(
                    fileToImport.getAbsolutePath(),
                    csvImportDescriptor.getImportFileDescriptor().getColumnDelimiter(),
                    csvImportDescriptor.getImportFileDescriptor().getTextQualifier(),
                    csvImportDescriptor.getImportFileDescriptor().getFileEncoding(),
                    csvImportDescriptor.getImportFileDescriptor().isIgnoreFirstLine());

            String[] line;
            while ((line = csvFileReader.readLine()) != null) {
                final CsvImportTuple tuple = new CsvImportTupleImpl(filename, lineNumber++, line);
                doImport(statusListener, tuple, csvImportDescriptorName, csvImportDescriptor, null);
            }
            final String msgInfoLines = MessageFormat.format("total data lines : {0}",
                    (csvImportDescriptor.getImportFileDescriptor().isIgnoreFirstLine() ? csvFileReader.getRowsRead() - 1 : csvFileReader.getRowsRead()));
            statusListener.notifyMessage(msgInfoLines);
            log.info(msgInfoLines);

            csvFileReader.close();
        } catch (FileNotFoundException e) {
            final String msgErr = MessageFormat.format(
                    "can not find the file : {0} {1}",
                    fileToImport.getAbsolutePath(),
                    e.getMessage());
            log.error(msgErr);
            statusListener.notifyError(msgErr);
        } catch (UnsupportedEncodingException e) {
            final String msgErr = MessageFormat.format(
                    "wrong file encoding in xml descriptor : {0} {1}",
                    csvImportDescriptor.getImportFileDescriptor().getFileEncoding(),
                    e.getMessage());
            log.error(msgErr);
            statusListener.notifyError(msgErr);

        } catch (IOException e) {
            final String msgErr = MessageFormat.format("con not close the csv file : {0} {1}",
                    fileToImport.getAbsolutePath(),
                    e.getMessage());
            log.error(msgErr);
            statusListener.notifyError(msgErr);
        }

    }


    /**
     * Import single line.
     * This method can be called recursive in case of sum imports.
     *
     * @param statusListener   error report
     * @param tuple            single line from csv file
     * @param csvImportDescriptorName file name of the descriptor
     * @param descriptor       import descriptor
     * @param masterObject     optional master object if found sub import
     */
    public void doImport(final JobStatusListener statusListener,
                         final ImportTuple tuple,
                         final String csvImportDescriptorName,
                         final ImportDescriptor descriptor,
                         final Object masterObject) throws Exception {
        Object object = null;
        final Logger log = ShopCodeContext.getLog(this);
        final CsvImportDescriptor importDescriptor = (CsvImportDescriptor) descriptor;
        try {


            if (importDescriptor.getInsertSql() != null) {
                // this is dirty hack , because of import speed
                if (masterObject == null) {
                    // No need to validate sub imports
                    validateAccessBeforeUpdate(null, null); // only allowed by system admins
                }
                executeNativeInsert(importDescriptor, masterObject, tuple);


            } else {

                object = getEntity(tuple, null, masterObject, importDescriptor);


                fillEntityFields(tuple, object, importDescriptor.getImportColumns(FieldTypeEnum.FIELD));
                fillEntityForeignKeys(tuple, object, importDescriptor.getImportColumns(FieldTypeEnum.FK_FIELD), masterObject, importDescriptor);

                /*
                    Note: for correct data federation processing we need ALL-OR-NOTHING update for all import.
                          Once validation fails we fail the whole import with a rollback. Necessary to facilitate
                          objects with complex relationships to shop (e.g. products, SKU)
                 */

                if (masterObject == null) {
                    // No need to validate sub imports
                    // Preliminary validation - not always applicable for transient object (e.g. products need category assignments)
                    validateAccessBeforeUpdate(object, importDescriptor.getEntityTypeClass());
                }
                genericDAO.saveOrUpdate(object);
                performSubImport(statusListener, tuple, csvImportDescriptorName, importDescriptor, object, importDescriptor.getImportColumns(FieldTypeEnum.SLAVE_INLINE_FIELD));
                performSubImport(statusListener, tuple, csvImportDescriptorName, importDescriptor, object, importDescriptor.getImportColumns(FieldTypeEnum.SLAVE_TUPLE_FIELD));

                if (masterObject == null) {
                    genericDAO.refresh(object);
                    // No need to validate sub imports
                    // This validation is after sub imports to facilitate objects with complex relationships to shop (e.g. products)
                    validateAccessAfterUpdate(object, importDescriptor.getEntityTypeClass());
                }

                genericDAO.flushClear();

            }
            statusListener.notifyPing("Importing tuple: " + tuple.getSourceId()); // make sure we do not time out

        } catch (AccessDeniedException ade) {

            String message = MessageFormat.format(
                    "Access denied during import row : {0} \ndescriptor {1} \nobject is {2} \nmaster object is {3}",
                    tuple,
                    csvImportDescriptorName,
                    object,
                    masterObject
            );
            log.error(message, ade);
            statusListener.notifyError(message);
            genericDAO.clear();

            throw new Exception(message, ade);


        } catch (Exception e) {

            String additionalInfo = e.getMessage();
            String message = MessageFormat.format(
                    "during import row : {0} \ndescriptor {1} \nerror {2}\n{3} \nadditional info {4} \nobject is {5} \nmaster object is {6}",
                    tuple,
                    csvImportDescriptorName,
                    e.getMessage(),
                    ExceptionUtil.stackTraceToString(e),
                    additionalInfo,
                    object,
                    masterObject
            );
            log.error(message, e);
            statusListener.notifyError(message);
            genericDAO.clear();

            throw new Exception(message, e);
        }
    }

    private void executeNativeInsert(final ImportDescriptor descriptor, final Object masterObject, final ImportTuple tuple) {
        final LookUpQuery query = descriptorInsert.getQuery(descriptor, masterObject, tuple, valueStringAdapter, descriptor.getInsertSql());
        if (query.getQueryString().indexOf(";\n") == -1) {
            genericDAO.executeNativeUpdate(query.getQueryString());
        } else {
            for (final String statement : query.getQueryString().split(";\n")) {
                genericDAO.executeNativeUpdate(statement);
            }
        }
    }

    private void performSubImport(final JobStatusListener statusListener,
                                  final ImportTuple tuple,
                                  final String csvImportDescriptorName,
                                  final ImportDescriptor importDescriptor,
                                  final Object object,
                                  final Collection<ImportColumn> slaves) throws Exception {
        for (ImportColumn slaveTable : slaves) {
            final List<ImportTuple> subTuples = tuple.getSubTuples(importDescriptor, slaveTable, valueDataAdapter);
            CsvImportDescriptor innerCsvImportDescriptor = (CsvImportDescriptor) slaveTable.getImportDescriptor();
            for (ImportTuple subTuple : subTuples) {
                doImport(statusListener,
                        subTuple,
                        csvImportDescriptorName,
                        innerCsvImportDescriptor,
                        object);
            }
        }
    }


    /**
     * Fill the given entity object with line information using import column descriptions.
     *
     * @param tuple         given csv line
     * @param object        entity object
     * @param importColumns particular type column collection
     * @throws Exception in case if something wrong with reflection (IntrospectionException,
     *                   InvocationTargetException,
     *                   IllegalAccessException)
     */
    private void fillEntityFields(
            final ImportTuple tuple,
            final Object object,
            final Collection<ImportColumn> importColumns) throws Exception {

        final Class clz = object.getClass();

        PropertyDescriptor propertyDescriptor = null;

        for (ImportColumn importColumn : importColumns) {
            try {
                if (StringUtils.isNotBlank(importColumn.getName())) { //can be just lookup query

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
                        final I18NModel model = new StringI18NModel((String) propertyDescriptor.getReadMethod().invoke(object));
                        model.putValue(importColumn.getLanguage(), String.valueOf(singleObjectValue));
                        singleObjectValue = model.toString();
                    }
                    if (singleObjectValue != null && !singleObjectValue.getClass().equals(propertyDescriptor.getPropertyType())) {
                        // if we have mismatch try on the fly conversion
                        singleObjectValue =
                                extendedConversionService.convert(
                                        singleObjectValue,
                                        TypeDescriptor.valueOf(singleObjectValue.getClass()),
                                        TypeDescriptor.valueOf((propertyDescriptor.getPropertyType())
                                    ));
                    }

                    propertyDescriptor.getWriteMethod().invoke(writeObject, singleObjectValue);
                }
            } catch (Exception exp) {

                final String propName = propertyDescriptor != null ? propertyDescriptor.getName() : null;
                final String propType = propertyDescriptor != null ? propertyDescriptor.getPropertyType().getName() : null;

                throw new Exception(MessageFormat.format(
                        "Failed to process property name {0} type {1} object is {2}",
                        propName,
                        propType,
                        object
                ), exp);
            }
        }

    }

    /**
     * Fill the given entity object with line information using import column descriptions.
     *
     * @param tuple            given csv line
     * @param object           entity object
     * @param importColumns    particular type column collection
     * @param masterObject     master object , that set from main import in case of sub import
     * @param importDescriptor import descriptor
     * @throws Exception in case if something wrong with reflection (IntrospectionException,
     *                   InvocationTargetException,
     *                   IllegalAccessException)
     */
    private void fillEntityForeignKeys(final ImportTuple tuple,
                                       final Object object,
                                       final Collection<ImportColumn> importColumns,
                                       final Object masterObject,
                                       final ImportDescriptor importDescriptor) throws Exception {

        ImportColumn currentColumn = null;
        final Class clz = object.getClass();
        Object singleObjectValue = null;
        PropertyDescriptor propertyDescriptor = null;

        try {
            for (ImportColumn importColumn : importColumns) {
                currentColumn = importColumn;

                if (importColumn.isUseMasterObject()) {
                    singleObjectValue = masterObject;
                } else {
                    singleObjectValue = getEntity(tuple, importColumn, masterObject, importDescriptor);
                }
                propertyDescriptor = new PropertyDescriptor(importColumn.getName(), clz);
                final Object oldValue = propertyDescriptor.getReadMethod().invoke(object);
                if (oldValue instanceof Identifiable || singleObjectValue instanceof Identifiable) {
                    final Object oldValuePK = oldValue != null ? genericDAO.getEntityIdentifier(oldValue) : null;
                    final Object newValuePK = singleObjectValue != null ? genericDAO.getEntityIdentifier(singleObjectValue) : null;

                    if (oldValuePK == null || !oldValuePK.equals(newValuePK)) {
                        // Update the object only if the value has changed
                        propertyDescriptor.getWriteMethod().invoke(object, singleObjectValue);
                    }
                } else {
                    // This is not identifiable, possibly primitive (PK) so write always
                    propertyDescriptor.getWriteMethod().invoke(object, singleObjectValue);
                }

            }
        } catch (Exception exp) {

            final String propName = propertyDescriptor != null ? propertyDescriptor.getName() : null;
            final String propType = propertyDescriptor != null ? propertyDescriptor.getPropertyType().getName() : null;

            throw new Exception(MessageFormat.format(
                    "Failed to process property name {0} type {1} object is {2} caused by column {0} with value {1}",
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
     * @return new or existing entity
     * @throws ClassNotFoundException in case if entity interface is wrong.
     */
    private Object getEntity(final ImportTuple tuple,
                             final ImportColumn column,
                             final Object masterObject,
                             final ImportDescriptor importDescriptor) throws ClassNotFoundException {

        if (column == null) {
            // no caching for prime select
            final Object prime = getExistingEntity(importDescriptor, importDescriptor.getSelectSql(), masterObject, tuple);
            if (prime == null) {
                return genericDAO.getEntityFactory().getByKey(importDescriptor.getEntityType());
            }
            return prime;
        }

        final String key = cacheKey.keyFor(importDescriptor, column, masterObject, tuple, valueStringAdapter);

        Object object = null;

        if (key != null) {
            object = entityCache.get(key);
        }

        if (object == null) {

            if (column.getLookupQuery() == null || column.getLookupQuery().length() == 0) {
                throw new IllegalArgumentException("Missing look up query for field: " + column.getName()
                        + " at index: " + column.getColumnIndex() + " in tuple: " + tuple);
            }
            object = getExistingEntity(importDescriptor, column.getLookupQuery(), masterObject, tuple);
            if (object == null) {
                if (column.getEntityType() != null) {
                    object = genericDAO.getEntityFactory().getByIface(
                            Class.forName(column.getEntityType())
                    );
                } else {
                    return null; // no cache for nulls
                }
            }
            if (object != null) {
                entityCache.put(key, object);
            }
        }
        return object;
    }


    private Map<String, Object> entityCache = new HashMap<String, Object>();


    /**
     * Try to get existing entity for update. In case of sub import master object will be used in parameters if
     * {@link ImportColumn#isUseMasterObject()} set to true.
     *
     * @param importDescriptor descriptor
     * @param queryTemplate    template to use with tuple columns as parameter values
     * @param masterObject in case of subimport will be not null, but will be used with flag only
     * @param tuple        csv row to get the parameter value for lookup query.
     *
     * @return existing entity or null if not found
     */
    private Object getExistingEntity(final ImportDescriptor importDescriptor,
                                     final String queryTemplate,
                                     final Object masterObject,
                                     final ImportTuple tuple) {

        final LookUpQuery query = columnLookUp.getQuery(importDescriptor, masterObject, tuple, valueDataAdapter, queryTemplate);
        final Object object = genericDAO.findSingleByQuery(query.getQueryString(), query.getParameters());
        return object;

    }

    /**
     * IoC. Set the {@link org.yes.cart.dao.GenericDAO} instance.
     *
     * @param genericDAO {@link GenericDAO} to use.
     */
    public void setGenericDAO(GenericDAO<Object, Long> genericDAO) {
        this.genericDAO = genericDAO;
        this.cacheKey = new ColumnLookUpQueryCacheKeyStrategy(columnLookUp, genericDAO);
    }

}
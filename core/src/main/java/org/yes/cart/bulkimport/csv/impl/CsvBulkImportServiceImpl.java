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


import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.yes.cart.bulkimport.csv.CsvFileReader;
import org.yes.cart.bulkimport.csv.CsvImportTuple;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.model.FieldTypeEnum;
import org.yes.cart.bulkimport.model.ImportColumn;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.model.ImportTuple;
import org.yes.cart.bulkimport.service.BulkImportService;
import org.yes.cart.bulkimport.service.BulkImportStatusListener;
import org.yes.cart.bulkimport.service.impl.AbstractImportService;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.stream.xml.XStreamProvider;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.util.misc.ExceptionUtil;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class CsvBulkImportServiceImpl extends AbstractImportService implements BulkImportService, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private GenericDAO<Object, Long> genericDAO;

    private String pathToImportDescriptor;

    private GenericConversionService extendedConversionService;

    private PropertyDescriptor propertyDescriptor;

    private XStreamProvider importDescriptorXStreamProvider;

    private ApplicationContext applicationContext;

    /**
     * IoC. Set {@link GenericConversionService}.
     *
     * @param extendedConversionService {@link GenericConversionService}  to use.
     */
    public void setExtendedConversionService(final GenericConversionService extendedConversionService) {
        this.extendedConversionService = extendedConversionService;
    }


    /**
     * {@inheritDoc}
     */
    public void setPathToImportDescriptor(final String pathToImportDescriptor) {
        this.pathToImportDescriptor = pathToImportDescriptor;
    }


    /**
     * Perform bulk import.
     * Service has s set of import descriptors, eac of them may perform the import
     * on files. Files selected by regular expression . If <code>fileName</code>
     * not empty, than only one may be imported instead of set, that satisfy
     * regular expressions.
     *
     * @param statusListener error report place holder
     * @param importedFiles imported files
     * @param fileName      optional file  name
     * @return {@link BulkImportResult}
     */
    public BulkImportResult doImport(final BulkImportStatusListener statusListener, final Set<String> importedFiles,
                                     final String fileName, final String pathToImportFolder) {

        try {
            entityCache.clear();

            final InputStream inputStream = new FileInputStream(pathToImportDescriptor);
            final CsvImportDescriptor csvImportDescriptor = (CsvImportDescriptor) getXStream().fromXML(inputStream);
            if (StringUtils.isNotBlank(pathToImportFolder)) {
                csvImportDescriptor.setImportDirectory(pathToImportFolder);
            }

            final File[] filesToImport = getFilesToImport(csvImportDescriptor, fileName);
            if (filesToImport == null) {
                final String msgWarn = MessageFormat.format(
                        "no files with mask {0} to import",
                        csvImportDescriptor.getImportFileDescriptor().getFileNameMask());
                statusListener.notifyWarning(msgWarn);
                LOG.warn(msgWarn);
            } else {
                final String msgInfo = MessageFormat.format(
                        "Import descriptor {0} has {1} file(s) with mask {2} to import",
                        pathToImportDescriptor,
                        filesToImport.length,
                        csvImportDescriptor.getImportFileDescriptor().getFileNameMask());
                statusListener.notifyMessage(msgInfo);
                LOG.info(msgInfo);
                if (csvImportDescriptor.getPrimaryKeyColumn() == null) {
                    final String msgErr = "import can not be started, because PK column is empty";
                    statusListener.notifyError(msgErr);
                    LOG.error(msgErr);
                    return BulkImportResult.ERROR;
                }
                doImport(statusListener, filesToImport, csvImportDescriptor, importedFiles);
            }
        } catch (Exception e) {
            final String msgError = MessageFormat.format(
                    "unexpected error {0}",
                    e.getMessage());
            LOG.error(msgError, e);
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
     * @param csvImportDescriptor import descriptor.
     * @param importedFiles       imported files.
     */
    void doImport(final BulkImportStatusListener statusListener,
                  final File[] filesToImport,
                  final CsvImportDescriptor csvImportDescriptor,
                  final Set<String> importedFiles) {
        for (File fileToImport : filesToImport) {
            doImport(statusListener, fileToImport, csvImportDescriptor);
            importedFiles.add(fileToImport.getAbsolutePath());
        }
    }

    /**
     * Perform import for single file.
     *
     * @param statusListener      error report
     * @param fileToImport        array of files to import
     * @param csvImportDescriptor import descriptor.
     */
    void doImport(final BulkImportStatusListener statusListener, final File fileToImport, final CsvImportDescriptor csvImportDescriptor) {

        final ImportColumn pkColumn = csvImportDescriptor.getPrimaryKeyColumn();

        final String msgInfoImp = MessageFormat.format("import file : {0}", fileToImport.getAbsolutePath());
        statusListener.notifyMessage(msgInfoImp);
        LOG.info(msgInfoImp);

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
                /*applicationContext.getBean("bulkImportServiceImpl", BulkImportService.class).*/
                doImport(statusListener, tuple, csvImportDescriptor, pkColumn, null);
            }
            final String msgInfoLines = MessageFormat.format("total lines : {0}", csvFileReader.getRowsRead());
            statusListener.notifyMessage(msgInfoLines);
            LOG.info(msgInfoLines);

            csvFileReader.close();
        } catch (FileNotFoundException e) {
            final String msgErr = MessageFormat.format(
                    "can not find the file : {0} {1}",
                    fileToImport.getAbsolutePath(),
                    e.getMessage());
            statusListener.notifyError(msgErr);
            LOG.error(msgErr);
        } catch (UnsupportedEncodingException e) {
            final String msgErr = MessageFormat.format(
                    "wrong file encoding in xml descriptor : {0} {1}",
                    csvImportDescriptor.getImportFileDescriptor().getFileEncoding(),
                    e.getMessage());
            statusListener.notifyError(msgErr);
            LOG.error(msgErr);

        } catch (IOException e) {
            final String msgErr = MessageFormat.format("con not close the csv file : {0} {1}",
                    fileToImport.getAbsolutePath(),
                    e.getMessage());
            statusListener.notifyError(msgErr);
            LOG.error(msgErr);
        }

    }


    /**
     * Import single line.
     * This method can be called recursive in case of sum imports.
     *
     * @param statusListener   error report
     * @param tuple            single line from csv file
     * @param importDescriptor import descriptor
     * @param pkColumn         column to locate object.
     * @param masterObject     optional master object if found sub import
     */
    public void doImport(final BulkImportStatusListener statusListener,
                         final ImportTuple tuple,
                         final CsvImportDescriptor importDescriptor,
                         final ImportColumn pkColumn,
                         final Object masterObject) {
        Object object = null;
        final String[] line = ((CsvImportTuple) tuple).getData();
        try {


            if (importDescriptor.getInsertSql() != null && masterObject != null) {
                // this is dirty hack , because of import speed
                executeNativeInsert(importDescriptor, masterObject, tuple);


            } else {

                object = getEntity(line, pkColumn, masterObject, importDescriptor);


                fillEntityFields(line, object, importDescriptor.getImportColumns(FieldTypeEnum.FIELD));
                fillEntityForeignKeys(line, object, importDescriptor.getImportColumns(FieldTypeEnum.FK_FIELD), masterObject, importDescriptor);

                genericDAO.saveOrUpdate(object);
                performSubImport(statusListener, tuple, importDescriptor, object, importDescriptor.getImportColumns(FieldTypeEnum.SIMPLE_SLAVE_FIELD));
                performSubImport(statusListener, tuple, importDescriptor, object, importDescriptor.getImportColumns(FieldTypeEnum.KEYVALUE_SLAVE_FIELD));
                genericDAO.flushClear();

            }
            statusListener.notifyPing(); // make sure we do not time out

        } catch (Exception e) {
            String additionalInfo = null;
            if (propertyDescriptor != null) {
                additionalInfo = MessageFormat.format(
                        "Property name {0} type {1} object is {2}",
                        propertyDescriptor.getName(),
                        propertyDescriptor.getPropertyType().getName(),
                        object
                );
            }
            String message = MessageFormat.format(
                    "during import row : {0} \ndescriptor {1} \nerror {2} \nadditional info {3} \nobject is {4} \nmaster object is {5}",
                    getRowAsString(line, importDescriptor.getImportFileDescriptor().getColumnDelimiter()),
                    pathToImportDescriptor,
                    e.getMessage(),
                    additionalInfo,
                    object,
                    masterObject
            );
            statusListener.notifyError(message);
            statusListener.notifyError("Tuple: " + tuple.toString());
            LOG.error(message, e);
        }
    }

    private static final Pattern MATCH_COLUMNS_IN_NATIVE_SQL = Pattern.compile("(\\{[a-zA-Z\\d]*\\})");

    private void executeNativeInsert(final CsvImportDescriptor descriptor, final Object masterObject, final ImportTuple tuple) {

        final String nativeQuery = descriptor.getInsertSql();
        final Matcher matcher = MATCH_COLUMNS_IN_NATIVE_SQL.matcher(nativeQuery);

        final StringBuilder sql = new StringBuilder();
        int lastIndex = 0;
        while (matcher.find()) {
            final String columnName = matcher.group(0);
            sql.append(nativeQuery.substring(lastIndex, matcher.start(0)));
            lastIndex = matcher.end(0);
            if ("{masterObjectId}".equals(columnName)) {
                if (masterObject != null) {
                    sql.append(((Identifiable) masterObject).getId());
                } else {
                    sql.append("NULL");
                }
            } else {
                final String realColumnName = columnName.substring(1, columnName.length() - 1);
                final ImportColumn column = descriptor.getImportColumn(realColumnName);
                if (column != null) {
                    final String value = StringEscapeUtils.escapeSql((String) tuple.getColumnValue(column));
                    sql.append(value);
                } else {
                    sql.append("NULL");
                }
            }
        }
        sql.append(nativeQuery.substring(lastIndex));
        genericDAO.executeNativeUpdate(sql.toString());
    }

    private void performSubImport(final BulkImportStatusListener statusListener,
                                  final ImportTuple tuple,
                                  final CsvImportDescriptor importDescriptor,
                                  final Object object,
                                  final Collection<ImportColumn> slaves) {
        for (ImportColumn slaveTable : slaves) {
            final List<ImportTuple> subTuples = tuple.getSubTuples(importDescriptor, slaveTable);
            CsvImportDescriptor innerCsvImportDescriptor = (CsvImportDescriptor) slaveTable.getImportDescriptor();
            ImportColumn slavePkColumn = innerCsvImportDescriptor.getPrimaryKeyColumn();
            for (ImportTuple subTuple : subTuples) {
                /*applicationContext.getBean("bulkImportServiceImpl", BulkImportService.class).*/
                doImport(statusListener,
                        subTuple,
                        innerCsvImportDescriptor,
                        slavePkColumn,
                        object);
            }
        }
    }


    /**
     * Fill the given entity object with line information using import column descriptions.
     *
     * @param line          given csv line
     * @param object        entity object
     * @param importColumns particular type column collection
     * @throws Exception in case if something wrong with reflection (IntrospectionException,
     *                   InvocationTargetException,
     *                   IllegalAccessException)
     */
    private void fillEntityFields(
            final String[] line,
            final Object object,
            final Collection<ImportColumn> importColumns) throws Exception {

        final Class clz = object.getClass();

        for (ImportColumn importColumn : importColumns) {
            if (StringUtils.isNotBlank(importColumn.getName())) { //can be just lookup query
                propertyDescriptor = new PropertyDescriptor(importColumn.getName(), clz);
                String singleStringValue;
                if (importColumn.getColumnIndex() == -1) { //constant expected
                    singleStringValue = importColumn.getValueConstant();
                } else {
                    singleStringValue = importColumn.getValue(line[importColumn.getColumnIndex()]);
                }

                final Object singleObjectValue =
                        extendedConversionService.convert(
                                singleStringValue,
                                TypeDescriptor.valueOf(String.class),
                                TypeDescriptor.valueOf((propertyDescriptor.getPropertyType())
                                ));


                propertyDescriptor.getWriteMethod().invoke(object, singleObjectValue);
            }
        }

    }

    /**
     * Fill the given entity object with line information using import column descriptions.
     *
     * @param line             given csv line
     * @param object           entity object
     * @param importColumns    particular type column collection
     * @param masterObject     master object , that set from main import in case of sub import
     * @param importDescriptor import descriptor
     * @throws Exception in case if something wrong with reflection (IntrospectionException,
     *                   InvocationTargetException,
     *                   IllegalAccessException)
     */
    private void fillEntityForeignKeys(final String[] line,
                                       final Object object,
                                       final Collection<ImportColumn> importColumns,
                                       final Object masterObject,
                                       final ImportDescriptor importDescriptor) throws Exception {

        ImportColumn currentColumn = null;
        final Class clz = object.getClass();
        Object singleObjectValue = null;

        try {
            for (ImportColumn importColumn : importColumns) {
                currentColumn = importColumn;

                if (importColumn.isUseMasterObject()) {
                    singleObjectValue = masterObject;
                } else {
                    singleObjectValue = getEntity(line, importColumn, masterObject, importDescriptor);
                }
                propertyDescriptor = new PropertyDescriptor(importColumn.getName(), clz);
                propertyDescriptor.getWriteMethod().invoke(object, singleObjectValue);
            }
        } catch (Exception e) {
            throw new Exception(
                    MessageFormat.format(
                            " \nroot cause: {0} \nvalue {1} \nstack trace is {2}",
                            currentColumn,
                            singleObjectValue,
                            ExceptionUtil.toString(e)),
                    e);
        }

    }


    /**
     * Get the entity object with following strategy: first attempt - locate
     * existing entity, if it not found create new instance.
     *
     * @param line             csv row to get the parameter value for lookup query.
     * @param column           import column, the describe lookup query to locale the entity
     * @param masterObject     in case of subimport will be not null, but will be used with flag only
     * @param importDescriptor import descriptor
     * @return new or existing entity
     * @throws ClassNotFoundException in case if entity interface is wrong.
     */
    private Object getEntity(final String[] line,
                             final ImportColumn column,
                             final Object masterObject,
                             final ImportDescriptor importDescriptor) throws ClassNotFoundException {

        final StringBuilder sb = new StringBuilder();
        final Object params = column.getColumnIndex() > -1 ? getQueryParametersValue(line[column.getColumnIndex()], column) : "N/A";
        sb.append(column.getName()).append('_').append(column.getColumnIndex()).append('_').append(column.getLookupQuery());
        if (params instanceof Object[]) {
            for (Object obj : (Object[]) params) {
                sb.append('_').append(obj);
            }
        } else {
            sb.append('_').append(params);
        }

        if (masterObject instanceof Identifiable && column.isUseMasterObject()) {
            sb.append('_').append(((Identifiable) masterObject).getId());
        }
        final String key = sb.toString();


        Object object = null;

        if (key != null) {
            object = entityCache.get(key);
        }

        if (object == null) {
            object = getExistingEntity(line, column, masterObject);
            if (object == null) {
                if (column.getFieldType() == FieldTypeEnum.FK_FIELD && column.getEntityType() != null) {
                    object = genericDAO.getEntityFactory().getByIface(
                            Class.forName(column.getEntityType())
                    );

                } else {
                    object = genericDAO.getEntityFactory().getByIface(
                            Class.forName(importDescriptor.getEntityType())
                    );
                }
            }
            if (object != null) {
                entityCache.put(key, object);
                //System.out.println("Put with " + key + " " + object );
            }

            //} else {
            //System.out.println("Hit with " + key);
        }
        return object;
    }


    private Map<String, Object> entityCache = new HashMap<String, Object>();
    //new MapMaker().concurrencyLevel(1).softKeys().softValues().expiration(3, TimeUnit.MINUTES). makeMap();


    /**
     * Try to get existing entity for update. In case of sub import master object will be used in parameters if
     * {@link ImportColumn#isUseMasterObject()} set to true.
     *
     * @param line         csv row to get the parameter value for lookup query.
     * @param column       impornt column, the describe lookup query to locale the entity
     * @param masterObject in case of subimport will be not null, but will be used with flag only
     * @return existing entity or null if not found
     */
    private Object getExistingEntity(String[] line, ImportColumn column, Object masterObject) {
        Object object;
        if (column.getColumnIndex() == -1) {
            object = genericDAO.findSingleByQuery(
                    column.getLookupQuery(), new Object[0]);    //Query with constants

        } else {
            final Object queryParameters = getQueryParametersValue(line[column.getColumnIndex()], column);
            object = genericDAO.findSingleByQuery(
                    column.getLookupQuery(),
                    getQueryParameters(column.isUseMasterObject(), masterObject, queryParameters)
            );
        }
        return object;

    }


    private Object getQueryParametersValue(final String rawValue, final ImportColumn column) {
        if (column.getGroupCount(rawValue) > 1) {
            return column.getValues(rawValue);
        }
        return column.getValue(rawValue);
    }


    /**
     * Get the look up query parameters, if  useMasterObject set to true  masterObject will be added
     * to the end of rowParameters array
     *
     * @param useMasterObject use master object flag
     * @param masterObject    master object
     * @param rowParameters   original parameters for lookup query.
     * @return parameters for look up query
     */
    private Object[] getQueryParameters(boolean useMasterObject, Object masterObject, Object... rowParameters) {
        if (rowParameters != null && rowParameters.length == 1 && rowParameters[0] == null) {
            rowParameters = new Object[0];
        }
        if (useMasterObject) {
            Object[] params = new Object[rowParameters.length + 1];
            System.arraycopy(rowParameters, 0, params, 0, rowParameters.length);
            params[params.length - 1] = masterObject;
            return params;
        }
        return rowParameters;

    }

    /**
     * Get the string representation of array.
     *
     * @param row single row from csv file or cell
     * @return string representation of array
     */
    private String getRowAsString(final String[] row, final char delim) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (row != null) {
            for (String column : row) {
                stringBuilder.append(column);
                stringBuilder.append(delim);
            }
        } else {
            stringBuilder.append("row is null");
        }
        return stringBuilder.toString();
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
     * IoC. XStream provider for import descriptor files.
     *
     * @param importDescriptorXStreamProvider xStream provider
     */
    public void setImportDescriptorXStreamProvider(final XStreamProvider importDescriptorXStreamProvider) {
        this.importDescriptorXStreamProvider = importDescriptorXStreamProvider;
    }

    protected XStream getXStream() {
        return importDescriptorXStreamProvider.provide();
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
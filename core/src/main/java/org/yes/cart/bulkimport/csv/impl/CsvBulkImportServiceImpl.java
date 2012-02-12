package org.yes.cart.bulkimport.csv.impl;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.yes.cart.bulkimport.csv.CsvFileReader;
import org.yes.cart.bulkimport.csv.CvsImportDescriptor;
import org.yes.cart.bulkimport.model.FieldTypeEnum;
import org.yes.cart.bulkimport.model.ImportColumn;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.service.BulkImportService;
import org.yes.cart.bulkimport.service.impl.AbstractImportService;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.util.misc.ExceptionUtil;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

/**
 * Pefrorm import from csv files. Import based on xml import description, that include
 * import file mask and column descrition. Csv import support following
 * import types:
 * <p/>
 * simple - when csv row can be present in table without any modification
 * with fk - when some of the column need to use lookup query to find actual value, that base on value in csv
 * example carrier and carries sla, carrier sla need to get Carrier object as foreign key.
 * with sub import - one of the column has a underlaying table values in csv
 * example - shop and shop url, in this case {@link ImportColumn} has a
 * {@link ImportDescriptor}. At this moment rows in cell splitted by comma by default.
 */
public class CsvBulkImportServiceImpl extends AbstractImportService implements BulkImportService {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    private GenericDAO<Object, Long> genericDAO;

    private String pathToImportDescriptor;

    private GenericConversionService extendedConversionService;

    private PropertyDescriptor propertyDescriptor;


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
     * @param errorReport   error report place holder
     * @param importedFiles imported files
     * @param fileName      optional file  name
     * @return {@link BulkImportResult}
     */
    public BulkImportResult doImport(final StringBuilder errorReport, final Set<String> importedFiles,
                                     final String fileName, final String pathToImportFolder) {

        try {
            final InputStream inputStream = new FileInputStream(pathToImportDescriptor);
            final CvsImportDescriptor cvsImportDescriptor = (CvsImportDescriptor) getXStream().fromXML(inputStream);
            if (StringUtils.isNotBlank(pathToImportFolder)) {
                cvsImportDescriptor.setImportFolder(pathToImportFolder);
            }

            final File[] filesToImport = getFilesToImport(cvsImportDescriptor, fileName);
            if (filesToImport == null) {
                errorReport.append(
                        MessageFormat.format(
                                "\nWARNING no files with mask {0} to import",
                                cvsImportDescriptor.getImportFile().getFileNameMask()));
            } else {
                errorReport.append(
                        MessageFormat.format(
                                "\nINFO Import descriptor {0} has {1} file(s) with mask {2} to import",
                                pathToImportDescriptor,
                                filesToImport.length,
                                cvsImportDescriptor.getImportFile().getFileNameMask()));
                if (cvsImportDescriptor.getPrimaryKeyColumn() == null) {
                    errorReport.append(
                            "ERROR: import can not be started, because can not fild column to locate primary key");
                    return BulkImportResult.ERROR;
                }
                doImport(errorReport, filesToImport, cvsImportDescriptor, importedFiles);
            }
        } catch (Exception e) {
            LOG.error("Unexpected error during bulk import ", e);
            errorReport.append(
                    MessageFormat.format(
                            "\nERROR unexpected error {0}",
                            e.getMessage()));
            return BulkImportResult.ERROR;
        }
        return BulkImportResult.OK;
    }


    /**
     * Perform import for each file.
     *
     * @param errorReport         error report
     * @param filesToImport       array of files to import
     * @param cvsImportDescriptor import descriptor.
     * @param importedFiles       imported files.
     */
    void doImport(final StringBuilder errorReport,
                  final File[] filesToImport,
                  final CvsImportDescriptor cvsImportDescriptor,
                  final Set<String> importedFiles) {
        for (File fileToImport : filesToImport) {
            doImport(errorReport, fileToImport, cvsImportDescriptor);
            importedFiles.add(fileToImport.getAbsolutePath());
        }
    }

    /**
     * Perform import for single file.
     *
     * @param errorReport         error report
     * @param fileToImport        array of files to import
     * @param cvsImportDescriptor import descriptor.
     */
    void doImport(final StringBuilder errorReport, final File fileToImport, final CvsImportDescriptor cvsImportDescriptor) {

        final ImportColumn pkColumn = cvsImportDescriptor.getPrimaryKeyColumn();

        errorReport.append(
                MessageFormat.format("\nINFO import file : {0}", fileToImport.getAbsolutePath()));

        CsvFileReader csvFileReader = new CsvFileReaderImpl();
        try {
            csvFileReader.open(
                    fileToImport.getAbsolutePath(),
                    cvsImportDescriptor.getImportFile().getColumnDelimeter(),
                    cvsImportDescriptor.getImportFile().getTextQualifier(),
                    cvsImportDescriptor.getImportFile().getFileEncoding(),
                    cvsImportDescriptor.getImportFile().isIgnoreFirstLine());

            String[] line;
            while ((line = csvFileReader.readLine()) != null) {
                doImport(errorReport, line, cvsImportDescriptor, pkColumn, null);
            }
            errorReport.append(MessageFormat.format("\nINFO total lines : {0}", csvFileReader.getRowsRead()));

            csvFileReader.close();
        } catch (FileNotFoundException e) {
            errorReport.append(
                    MessageFormat.format(
                            "\nERROR can not find the file : {0} {1}",
                            fileToImport.getAbsolutePath(),
                            e.getMessage()));
        } catch (UnsupportedEncodingException e) {
            errorReport.append(
                    MessageFormat.format(
                            "\nERROR wrong file encoding in xml descriptor : {0} {1}",
                            cvsImportDescriptor.getImportFile().getFileEncoding(),
                            e.getMessage()));

        } catch (IOException e) {
            errorReport.append(
                    MessageFormat.format("\nWARNING con not close the csv file : {0} {1}",
                            fileToImport.getAbsolutePath(),
                            e.getMessage()));

        }

    }


    /**
     * Import single line. This method can be called recursive in case of sum imports.
     *
     * @param errorReport      error report
     * @param line             single line from csv file
     * @param importDescriptor import desciptor
     * @param pkColumn         column to locate object.
     * @param masterObject     optional master object if found sub import
     */
    void doImport(final StringBuilder errorReport,
                  final String[] line,
                  final CvsImportDescriptor importDescriptor,
                  final ImportColumn pkColumn,
                  final Object masterObject) {
        try {
            final Object object = getEntity(line, pkColumn, masterObject, importDescriptor);
            fillEntityFields(line, object, importDescriptor.getImportColumns(FieldTypeEnum.FIELD));
            fillEntityForeignKeys(line, object, importDescriptor.getImportColumns(FieldTypeEnum.FK_FIELD), masterObject, importDescriptor);
            genericDAO.saveOrUpdate(object);
            performSubImport(errorReport, line, importDescriptor, object, importDescriptor.getImportColumns(FieldTypeEnum.SIMPLE_SLAVE_FIELD));
            genericDAO.flushClear();
        } catch (Exception e) {
            String additionalInfo = null;
            if (propertyDescriptor != null) {
                additionalInfo = MessageFormat.format(
                        "Property name {0} type {1}",
                        propertyDescriptor.getName(),
                        propertyDescriptor.getPropertyType().getName());
            }
            String message = MessageFormat.format(
                    "\nERROR duiring import row : {0} \ndescriptor {1} \nerror {2} \nadditional info {3}",
                    getRowAsString(line, importDescriptor.getImportFile().getColumnDelimeter()),
                    pathToImportDescriptor,
                    e.getMessage(),
                    additionalInfo
            );
            LOG.warn(message, e);
            e.printStackTrace();
            errorReport.append(message);
        }
    }

    private void performSubImport(final StringBuilder errorReport,
                                  final String[] line,
                                  final CvsImportDescriptor importDescriptor,
                                  final Object object,
                                  final Collection<ImportColumn> slaves) {
        for (ImportColumn slaveTable : slaves) {
            final String allValues = slaveTable.getValue(line[slaveTable.getColumnIndex()]);
            final String[] rows = allValues.split(",");
            CvsImportDescriptor innerCvsImportDescriptor = (CvsImportDescriptor) slaveTable.getImportDescriptor();
            ImportColumn slavePkColumn = innerCvsImportDescriptor.getPrimaryKeyColumn();
            for (String row : rows) {
                doImport(errorReport,
                        row.split(String.valueOf(importDescriptor.getImportFile().getColumnDelimeter())),
                        innerCvsImportDescriptor,
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
                    singleStringValue = importColumn.getConstant();
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
     * @param masterObject     master oject , that set from main import in case of sub import
     * @param importDescriptor import desciptor
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
     * existing entity, if it not nound create new instance.
     *
     * @param line             csv row to get the parameter value for lookup query.
     * @param column           impornt column, the describe lookup query to locale the entity
     * @param masterObject     in case of subimport will be not null, but will be used with flag only
     * @param importDescriptor import desciptor
     * @return new or existing entity
     * @throws ClassNotFoundException in case if enity interface is wrong.
     */
    private Object getEntity(final String[] line,
                             final ImportColumn column,
                             final Object masterObject,
                             final ImportDescriptor importDescriptor) throws ClassNotFoundException {
        Object object = getExistingEntity(line, column, masterObject);
        if (object == null) {
            object = genericDAO.getEntityFactory().getByIface(
                    Class.forName(importDescriptor.getEntityIntface())
            );
        }
        return object;
    }

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
            if (column.getGroupCount(line[column.getColumnIndex()]) > 1) {
                object = genericDAO.findSingleByQuery(
                        column.getLookupQuery(),
                        getQueryParameters(column.isUseMasterObject(), masterObject, column.getValues(line[column.getColumnIndex()]))
                );
            } else {
                object = genericDAO.findSingleByQuery(
                        column.getLookupQuery(),
                        getQueryParameters(column.isUseMasterObject(), masterObject, column.getValue(line[column.getColumnIndex()]))
                );
            }
        }
        return object;
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




    protected XStream getXStream() {
        XStream xStream = new XStream(new DomDriver());
        xStream.alias("import-descriptor", CvsImportDescriptorImpl.class);
        xStream.alias("file-descriptor", CsvImportFileImpl.class);
        xStream.alias("column-descriptor", CsvImportColumnImpl.class);
        return xStream;
    }


}

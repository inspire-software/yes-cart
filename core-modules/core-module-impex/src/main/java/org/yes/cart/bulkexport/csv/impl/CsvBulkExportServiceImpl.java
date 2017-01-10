package org.yes.cart.bulkexport.csv.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.yes.cart.bulkcommon.model.ImpExColumn;
import org.yes.cart.bulkcommon.model.ValueAdapter;
import org.yes.cart.bulkcommon.service.ExportService;
import org.yes.cart.bulkcommon.service.support.LookUpQuery;
import org.yes.cart.bulkcommon.service.support.LookUpQueryParameterStrategy;
import org.yes.cart.bulkexport.csv.*;
import org.yes.cart.bulkexport.model.ExportColumn;
import org.yes.cart.bulkexport.model.ExportDescriptor;
import org.yes.cart.bulkexport.model.ExportTuple;
import org.yes.cart.bulkimport.model.ImportColumn;
import org.yes.cart.bulkimport.service.impl.AbstractExportService;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobContext;
import org.yes.cart.service.async.model.JobContextKeys;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.util.ExceptionUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 14:27
 */
public class CsvBulkExportServiceImpl extends AbstractExportService implements ExportService {

    private GenericDAO<Object, Long> genericDAO;

    private ValueAdapter valueDataAdapter;

    private ValueAdapter valueLanguageAdapter;
    private LookUpQueryParameterStrategy columnLookUpQueryParameterStrategy;


    public CsvBulkExportServiceImpl(final FederationFacade federationFacade) {
        super(federationFacade);
    }

    /**
     * {@inheritDoc}
     */
    public BulkExportResult doExport(final JobContext context) {

        final JobStatusListener statusListener = context.getListener();

        final CsvExportDescriptor csvExportDescriptor = context.getAttribute(JobContextKeys.EXPORT_DESCRIPTOR);
        final String csvExportDescriptorName = context.getAttribute(JobContextKeys.EXPORT_DESCRIPTOR_NAME);
        final String csvExportRoot = context.getAttribute(JobContextKeys.EXPORT_DIRECTORY_ROOT);
        final String csvExportOverrideFile = context.getAttribute(JobContextKeys.EXPORT_FILE);

        try {

            String fileToExport = csvExportDescriptor.getExportFileDescriptor().getFileName();
            if (StringUtils.isNotBlank(csvExportOverrideFile)) {
                fileToExport = csvExportOverrideFile;
            } else {
                if (fileToExport.contains(ROOT_PLACEHOLDER)) {
                    fileToExport = fileToExport.replace(ROOT_PLACEHOLDER, csvExportRoot);
                }
                if (fileToExport.contains(TIMESTAMP_PLACEHOLDER)) {
                    final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    fileToExport = fileToExport.replace(TIMESTAMP_PLACEHOLDER, format.format(new Date()));
                    if (new File(fileToExport).exists()) {
                        // Only do this for timestamped files, otherwise we assume that files are re-writable
                        final String msgErr = MessageFormat.format(
                                "export file already exists: {0}",
                                fileToExport);
                        statusListener.notifyError(msgErr);
                        return BulkExportResult.ERROR;
                    }
                }
            }

            final String msgInfo = MessageFormat.format(
                    "Export descriptor {0} specifies file {1} to export",
                    csvExportDescriptorName,
                    fileToExport);
            statusListener.notifyMessage(msgInfo);
            if (csvExportDescriptor.getSelectSql() == null) {
                final String msgErr = "export can not be started, because select-sql is empty";
                statusListener.notifyError(msgErr);
                return ExportService.BulkExportResult.ERROR;
            }
            doExport(statusListener, csvExportDescriptorName, csvExportDescriptor, fileToExport);

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
            statusListener.notifyError(msgError, e);
            return ExportService.BulkExportResult.ERROR;
        }
        return ExportService.BulkExportResult.OK;
    }

    private static final String ROOT_PLACEHOLDER = "{root}";
    private static final String TIMESTAMP_PLACEHOLDER = "{timestamp}";

    /**
     * Perform export for single file.
     *
     * @param statusListener      error report
     * @param csvExportDescriptorName file name of the descriptor
     * @param csvExportDescriptor export descriptor.
     * @param fileToExport        file to export
     */
    void doExport(final JobStatusListener statusListener,
                  final String csvExportDescriptorName,
                  final CsvExportDescriptor csvExportDescriptor, final String fileToExport) throws Exception {

        final String msgInfoImp = MessageFormat.format("export file : {0}", fileToExport);
        statusListener.notifyMessage(msgInfoImp);

        CsvFileWriter csvFileWriter = new CsvFileWriterImpl();
        try {
            final String filename = fileToExport;

            final List<String> headers = new ArrayList<String>(csvExportDescriptor.getColumns().size());
            for (final ExportColumn column : csvExportDescriptor.getColumns()) {
                headers.add(column.getColumnHeader());
            }

            csvFileWriter.open(
                    filename,
                    headers.toArray(new String[headers.size()]),
                    csvExportDescriptor.getExportFileDescriptor().getColumnDelimiter(),
                    csvExportDescriptor.getExportFileDescriptor().getTextQualifier(),
                    csvExportDescriptor.getExportFileDescriptor().getFileEncoding(),
                    csvExportDescriptor.getExportFileDescriptor().isPrintHeader());


            final ResultsIterator<Object> results = getExistingEntities(csvExportDescriptor, csvExportDescriptor.getSelectSql(), null, null);
            while (results.hasNext()) {
                final CsvExportTuple tuple = new CsvExportTupleImpl(results.next());
                csvFileWriter.writeLine(doExportTuple(statusListener, tuple, csvExportDescriptorName, csvExportDescriptor, null));
            }
            final String msgInfoLines = MessageFormat.format("total data lines : {0}",
                    (csvExportDescriptor.getExportFileDescriptor().isPrintHeader() ? csvFileWriter.getRowsWritten() - 1 : csvFileWriter.getRowsWritten()));
            statusListener.notifyMessage(msgInfoLines);

        } catch (UnsupportedEncodingException e) {
            final String msgErr = MessageFormat.format(
                    "wrong file encoding in xml descriptor : {0} {1}",
                    csvExportDescriptor.getExportFileDescriptor().getFileEncoding(),
                    e.getMessage());
            statusListener.notifyError(msgErr, e);

        } catch (IOException e) {
            final String msgErr = MessageFormat.format("cannot write the csv file : {0} {1}",
                    fileToExport,
                    e.getMessage());
            statusListener.notifyError(msgErr, e);
        } finally {
            try {
                csvFileWriter.close();
            } catch (IOException ioe) {
                final String msgErr = MessageFormat.format("cannot close the csv file : {0} {1}",
                        fileToExport,
                        ioe.getMessage());
                statusListener.notifyError(msgErr, ioe);
            }
        }

    }

    /*
     * Export single line.
     * This method can be called recursive in case of sub exports.
     */
    String[] doExportTuple(final JobStatusListener statusListener,
                           final ExportTuple tuple,
                           final String csvExportDescriptorName,
                           final CsvExportDescriptor descriptor,
                           final Object masterObject) throws Exception {

        Object object = tuple.getData();
        try {

            if (masterObject == null) {
                // No need to validate sub exports
                // Preliminary validation - not always applicable for transient object (e.g. products need category assignments)
                try {
                    validateAccessBeforeExport(tuple.getData(), descriptor.getEntityTypeClass());
                } catch (AccessDeniedException ade) {
                    String message = MessageFormat.format(
                            "Access denied during export row : {0} \ndescriptor {1} \nobject is {2}",
                            tuple,
                            csvExportDescriptorName,
                            object
                    );
                    statusListener.notifyWarning(message);
                    return null;
                }
            }


            final String[] csv = new String[descriptor.getColumns().size()];
            int i = 0;
            for (final ExportColumn column : descriptor.getColumns()) {

                if (ImpExColumn.SLAVE_TUPLE_FIELD.equals(column.getFieldType()) || ImpExColumn.SLAVE_INLINE_FIELD.equals(column.getFieldType())) {

                    final CsvStringWriter subWriter = new CsvStringWriterImpl();
                    subWriter.open(
                            null,
                            ((CsvExportFile) column.getDescriptor().getExportFileDescriptor()).getColumnDelimiter(),
                            ((CsvExportFile) column.getDescriptor().getExportFileDescriptor()).getTextQualifier(),
                            column.getDescriptor().getExportFileDescriptor().getFileEncoding(),
                            false
                    );

                    if (StringUtils.isNotBlank(column.getDescriptor().getSelectSql())) {
                        final ResultsIterator<Object> subResult = getExistingEntities(column.getDescriptor(), column.getDescriptor().getSelectSql(), tuple.getData(), tuple);
                        while (subResult.hasNext()) {
                            final CsvExportTuple subItem = new CsvExportTupleImpl(subResult.next());
                            subWriter.writeLine(
                                    doExportTuple(statusListener, subItem, csvExportDescriptorName, (CsvExportDescriptor) column.getDescriptor(), tuple.getData())
                            );
                        }

                    } else {
                        final List<CsvExportTuple> sub = tuple.getSubTuples(descriptor, column, valueDataAdapter);
                        for (final CsvExportTuple subItem : sub) {
                            subWriter.writeLine(
                                    doExportTuple(statusListener, subItem, csvExportDescriptorName, (CsvExportDescriptor) column.getDescriptor(), tuple.getData())
                            );
                        }
                    }

                    csv[i] = subWriter.close().trim();

                } else {

                    if (column.getLanguage() != null) {
                        final String data = (String) tuple.getColumnValue(column, valueLanguageAdapter);
                        final I18NModel model = new StringI18NModel(data);
                        csv[i] = model.getValue(column.getLanguage());
                    } else {
                        final String data = (String) tuple.getColumnValue(column, valueDataAdapter);
                        csv[i] = data;
                    }
                }
                i++;
            }

            statusListener.notifyPing("Exporting tuple: " + tuple.getSourceId()); // make sure we do not time out

            return csv;

        } catch (Exception e) {

            String additionalInfo = e.getMessage();
            String message = MessageFormat.format(
                    "during export row : {0} \ndescriptor {1} \nerror {2}\n{3} \nadditional info {4} \nobject is {5} \nmaster object is {6}",
                    tuple,
                    csvExportDescriptorName,
                    e.getMessage(),
                    ExceptionUtil.stackTraceToString(e),
                    additionalInfo,
                    object,
                    masterObject
            );
            statusListener.notifyError(message, e);
            genericDAO.clear();

            throw new Exception(message, e);
        }

    }


    /**
     * Try to get existing entity for update. In case of sub import master object will be used in parameters if
     * {@link ImportColumn#isUseMasterObject()} set to true.
     *
     * @param exportDescriptor descriptor
     * @param queryTemplate    template to use with tuple columns as parameter values
     * @param masterObject in case of subexport will be not null, but will be used with flag only
     * @param tuple       data row to get the parameter value for lookup query.
     *
     * @return existing entity or null if not found
     */
    private ResultsIterator<Object> getExistingEntities(final ExportDescriptor exportDescriptor,
                                                        final String queryTemplate,
                                                        final Object masterObject,
                                                        final ExportTuple tuple) {

        final LookUpQuery query = columnLookUpQueryParameterStrategy.getQuery(exportDescriptor, masterObject, tuple, valueDataAdapter, queryTemplate);
        final ResultsIterator<Object> object = genericDAO.findByQueryIterator(query.getQueryString(), query.getParameters());
        return object;

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
     * IoC.
     *
     * @param valueDataAdapter {@link ValueAdapter}  to use.
     */
    public void setValueDataAdapter(final ValueAdapter valueDataAdapter) {
        this.valueDataAdapter = valueDataAdapter;
    }

    /**
     * IoC.
     *
     * @param valueLanguageAdapter {@link ValueAdapter}  to use.
     */
    public void setValueLanguageAdapter(final ValueAdapter valueLanguageAdapter) {
        this.valueLanguageAdapter = valueLanguageAdapter;
    }

    /**
     * IoC.
     *
     * @param columnLookUpQueryParameterStrategy {@link LookUpQueryParameterStrategy}  to use.
     */
    public void setColumnLookUpQueryParameterStrategy(final LookUpQueryParameterStrategy columnLookUpQueryParameterStrategy) {
        this.columnLookUpQueryParameterStrategy = columnLookUpQueryParameterStrategy;
    }

}

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
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.bulkcommon.service.ExportService;
import org.yes.cart.bulkcommon.service.support.query.LookUpQuery;
import org.yes.cart.bulkcommon.service.support.query.LookUpQueryParameterStrategy;
import org.yes.cart.bulkexport.csv.*;
import org.yes.cart.bulkexport.service.impl.AbstractExportService;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.federation.FederationFacade;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 14:27
 */
public class CsvBulkExportServiceImpl extends AbstractExportService<CsvExportDescriptor> implements ExportService {

    private GenericDAO<Object, Long> genericDAO;

    private CsvValueAdapter valueDataAdapter;

    private CsvValueAdapter valueLanguageAdapter;
    private LookUpQueryParameterStrategy<CsvExportDescriptor, CsvExportTuple, CsvValueAdapter> columnLookUpQueryParameterStrategy;


    public CsvBulkExportServiceImpl(final FederationFacade federationFacade) {
        super(federationFacade);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doExport(final JobStatusListener statusListener,
                            final String csvExportDescriptorName,
                            final CsvExportDescriptor csvExportDescriptor,
                            final String fileToExport) throws Exception {

        statusListener.notifyMessage("export file : {}", fileToExport);

        CsvFileWriter csvFileWriter = new CsvFileWriterImpl();
        ResultsIterator<Object> results = null;
        try {
            final String filename = fileToExport;

            final List<String> headers = new ArrayList<>(csvExportDescriptor.getColumns().size());
            for (final CsvExportColumn column : csvExportDescriptor.getColumns()) {
                headers.add(column.getColumnHeader());
            }

            csvFileWriter.open(
                    filename,
                    headers.toArray(new String[headers.size()]),
                    csvExportDescriptor.getExportFileDescriptor().getColumnDelimiter(),
                    csvExportDescriptor.getExportFileDescriptor().getTextQualifier(),
                    csvExportDescriptor.getExportFileDescriptor().getLineEnd(),
                    csvExportDescriptor.getExportFileDescriptor().getFileEncoding(),
                    csvExportDescriptor.getExportFileDescriptor().isPrintHeader());


            results = getExistingEntities(csvExportDescriptor, csvExportDescriptor.getSelectCmd(), null, null);
            while (results.hasNext()) {
                final Object entity = results.next();
                final CsvExportTuple tuple = new CsvExportTupleImpl(entity);
                csvFileWriter.writeLine(doExportTuple(statusListener, tuple, csvExportDescriptorName, csvExportDescriptor, null));
                releaseEntity(entity);
            }
            statusListener.notifyMessage("total data lines : {}",
                    (csvExportDescriptor.getExportFileDescriptor().isPrintHeader() ? csvFileWriter.getRowsWritten() - 1 : csvFileWriter.getRowsWritten()));

        } catch (UnsupportedEncodingException e) {
            statusListener.notifyError("wrong file encoding in xml descriptor : {} {}", e,
                    csvExportDescriptor.getExportFileDescriptor().getFileEncoding(),
                    e.getMessage());

        } catch (IOException e) {
            statusListener.notifyError("cannot write the csv file : {} {}", e,
                    fileToExport,
                    e.getMessage());
        } finally {
            try {
                if (results != null) {
                    results.close();
                }
            } catch (Exception exp) {
                statusListener.notifyError("cannot close the csv resultset : {} {}", exp,
                        fileToExport,
                        exp.getMessage());
            }
            try {
                csvFileWriter.close();
            } catch (IOException ioe) {
                statusListener.notifyError("cannot close the csv file : {} {}", ioe,
                        fileToExport,
                        ioe.getMessage());
            }
        }

    }

    /*
     * Export single line.
     * This method can be called recursive in case of sub exports.
     */
    protected String[] doExportTuple(final JobStatusListener statusListener,
                                     final CsvExportTuple tuple,
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
                    statusListener.notifyPing(
                            "Access denied during export row : {} \ndescriptor {} \nobject is {}",
                            tuple,
                            csvExportDescriptorName,
                            object
                    );
                    return null;
                }
            }


            final String[] csv = new String[descriptor.getColumns().size()];
            int i = 0;
            for (final CsvExportColumn column : descriptor.getColumns()) {

                if (CsvImpExColumn.SLAVE_TUPLE_FIELD.equals(column.getFieldType()) || CsvImpExColumn.SLAVE_INLINE_FIELD.equals(column.getFieldType())) {

                    final CsvExportFile subDescriptor = ((CsvExportFile) column.getDescriptor().getExportFileDescriptor());
                    final CsvStringWriter subWriter = new CsvStringWriterImpl();
                    subWriter.open(
                            null,
                            subDescriptor.getColumnDelimiter(),
                            subDescriptor.getTextQualifier(),
                            subDescriptor.getLineEnd(),
                            column.getDescriptor().getExportFileDescriptor().getFileEncoding(),
                            false
                    );

                    if (StringUtils.isNotBlank(column.getDescriptor().getSelectCmd())) {
                        ResultsIterator<Object> subResult = null;

                        try {
                            subResult = getExistingEntities(column.getDescriptor(), column.getDescriptor().getSelectCmd(), tuple.getData(), tuple);
                            while (subResult.hasNext()) {
                                final Object subEntity = subResult.next();
                                final CsvExportTuple subItem = new CsvExportTupleImpl(subEntity);
                                subWriter.writeLine(
                                        doExportTuple(statusListener, subItem, csvExportDescriptorName, (CsvExportDescriptor) column.getDescriptor(), tuple.getData())
                                );
                                releaseEntity(subEntity);
                            }
                        } finally {
                            if (subResult != null) {
                                subResult.close();
                            }
                        }

                    } else {
                        final List<CsvExportTuple> sub = tuple.getSubTuples(descriptor, column, valueDataAdapter);
                        for (final CsvExportTuple subItem : sub) {
                            subWriter.writeLine(
                                    doExportTuple(statusListener, subItem, csvExportDescriptorName, (CsvExportDescriptor) column.getDescriptor(), tuple.getData())
                            );
                        }
                    }

                    String out = subWriter.close();
                    if (StringUtils.isNotBlank(subDescriptor.getLineEnd()) &&
                            out.endsWith(subDescriptor.getLineEnd())) {
                        out = out.substring(0, out.length() - subDescriptor.getLineEnd().length());
                    }
                    csv[i] = out.trim();

                } else {

                    if (column.getLanguage() != null) {
                        final String data = (String) tuple.getColumnValue(column, valueLanguageAdapter);
                        csv[i] = data;
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

            statusListener.notifyError(
                    "during export row : {} \ndescriptor {} \nerror {}\nobject is {} \nmaster object is {}",
                    e,
                    tuple,
                    csvExportDescriptorName,
                    e.getMessage(),
                    object,
                    masterObject);
            genericDAO.clear();

            throw e;
        }

    }


    /**
     * Try to get existing entity for update. In case of sub import master object will be used in parameters if
     * {@link CsvExportColumn#isUseMasterObject()} set to true.
     *
     * @param exportDescriptor  descriptor
     * @param queryTemplate     template to use with tuple columns as parameter values
     * @param masterObject      in case of subexport will be not null, but will be used with flag only
     * @param tuple             data row to get the parameter value for lookup query.
     *
     * @return existing entity or null if not found
     */
    private ResultsIterator<Object> getExistingEntities(final CsvExportDescriptor exportDescriptor,
                                                        final String queryTemplate,
                                                        final Object masterObject,
                                                        final CsvExportTuple tuple) {

        final LookUpQuery query = columnLookUpQueryParameterStrategy.getQuery(exportDescriptor, masterObject, tuple, valueDataAdapter, queryTemplate);
        return genericDAO.findByQueryIterator(query.getQueryString(), query.getParameters());

    }

    /**
     * Release exported object from session.
     *
     * @param entity entity
     */
    private void releaseEntity(final Object entity) {
        genericDAO.evict(entity);
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
     * @param valueDataAdapter {@link CsvValueAdapter}  to use.
     */
    public void setValueDataAdapter(final CsvValueAdapter valueDataAdapter) {
        this.valueDataAdapter = valueDataAdapter;
    }

    /**
     * IoC.
     *
     * @param valueLanguageAdapter {@link CsvValueAdapter}  to use.
     */
    public void setValueLanguageAdapter(final CsvValueAdapter valueLanguageAdapter) {
        this.valueLanguageAdapter = valueLanguageAdapter;
    }

    /**
     * IoC.
     *
     * @param columnLookUpQueryParameterStrategy {@link LookUpQueryParameterStrategy}  to use.
     */
    public void setColumnLookUpQueryParameterStrategy(final LookUpQueryParameterStrategy<CsvExportDescriptor, CsvExportTuple, CsvValueAdapter> columnLookUpQueryParameterStrategy) {
        this.columnLookUpQueryParameterStrategy = columnLookUpQueryParameterStrategy;
    }

}

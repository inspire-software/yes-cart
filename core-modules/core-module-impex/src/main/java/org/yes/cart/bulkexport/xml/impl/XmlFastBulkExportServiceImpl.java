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

package org.yes.cart.bulkexport.xml.impl;

import org.yes.cart.bulkcommon.service.ExportService;
import org.yes.cart.bulkcommon.service.support.auth.impl.SystemAdminFederationFacade;
import org.yes.cart.bulkcommon.service.support.query.LookUpQuery;
import org.yes.cart.bulkcommon.service.support.query.LookUpQueryParameterStrategy;
import org.yes.cart.bulkcommon.service.support.xml.XmlLookUpQueryParameterStrategy;
import org.yes.cart.bulkcommon.xml.XmlImpExDescriptor;
import org.yes.cart.bulkcommon.xml.XmlImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.service.impl.AbstractExportService;
import org.yes.cart.bulkexport.xml.XmlEntityExportHandler;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.bulkexport.xml.XmlExportTuple;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.service.async.JobStatusListener;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: denispavlov
 * Date: 20/10/2018
 * Time: 13:13
 */
public class XmlFastBulkExportServiceImpl extends AbstractExportService<XmlExportDescriptor> implements ExportService {


    private GenericDAO<Object, Long> genericDAO;

    private XmlValueAdapter valueDataAdapter;

    private LookUpQueryParameterStrategy<XmlImpExDescriptor, XmlImpExTuple, XmlValueAdapter> columnLookUpQueryParameterStrategy;

    private Map<String, XmlEntityExportHandler> handlerMap = Collections.emptyMap();


    public XmlFastBulkExportServiceImpl() {
        super(new SystemAdminFederationFacade());
    }

    /**
     * Perform export for single file.
     *
     * @param statusListener            error report
     * @param xmlExportDescriptorName   file name of the descriptor
     * @param xmlExportDescriptor       export descriptor.
     * @param fileToExport              file to export
     */
    protected void doExport(final JobStatusListener statusListener,
                            final String xmlExportDescriptorName,
                            final XmlExportDescriptor xmlExportDescriptor,
                            final String fileToExport) throws Exception {

        statusListener.notifyMessage("export file : {}", fileToExport);

        final XmlEntityExportHandler<Object> handler = this.handlerMap.get(xmlExportDescriptor.getXmlHandler());

        if (handler == null) {
            statusListener.notifyError("no handler : {}", xmlExportDescriptor.getXmlHandler());
            return;
        }

        final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(fileToExport)), StandardCharsets.UTF_8);
        ResultsIterator<Object> results = null;
        try {

            writer.write(handler.startXml());

            Map<String, Integer> counts = new ConcurrentHashMap<>();

            results = getExistingEntities(xmlExportDescriptor, xmlExportDescriptor.getSelectCmd(), null, null);
            while (results.hasNext()) {
                final Object entity = results.next();
                final XmlExportTuple tuple = new XmlExportTupleImpl(entity);

                handler.handle(statusListener, xmlExportDescriptor, tuple, valueDataAdapter, fileToExport, writer, counts);

                releaseEntity(entity);
            }

            writer.write(handler.endXml());

            for (final Map.Entry<String, Integer> countEntry : counts.entrySet()) {
                statusListener.notifyMessage("total data objects : {} {}", countEntry.getKey(), countEntry.getValue());
            }

        } catch (UnsupportedEncodingException e) {
            statusListener.notifyError("wrong file encoding in xml descriptor : {} {}",
                    e,
                    xmlExportDescriptor.getExportFileDescriptor().getFileEncoding(),
                    e.getMessage());

        } catch (IOException e) {
            statusListener.notifyError("cannot write the xml file : {} {}",
                    e,
                    fileToExport,
                    e.getMessage());
        } finally {
            try {
                if (results != null) {
                    results.close();
                }
            } catch (Exception exp) {
                statusListener.notifyError("cannot close the xml resultset : {} {}",
                        exp,
                        fileToExport,
                        exp.getMessage());
            }
            try {
                writer.close();
            } catch (IOException ioe) {
                statusListener.notifyError("cannot close the xml file : {} {}",
                        ioe,
                        fileToExport,
                        ioe.getMessage());
            }
        }

    }

    /**
     * Try to get existing entity for update.
     *
     * @param exportDescriptor  descriptor
     * @param queryTemplate     template to use with tuple columns as parameter values
     * @param masterObject      in case of subexport will be not null, but will be used with flag only
     * @param tuple             data row to get the parameter value for lookup query.
     *
     * @return existing entity or null if not found
     */
    private ResultsIterator<Object> getExistingEntities(final XmlExportDescriptor exportDescriptor,
                                                        final String queryTemplate,
                                                        final Object masterObject,
                                                        final XmlExportTuple tuple) {

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
     * @param valueDataAdapter {@link XmlValueAdapter}  to use.
     */
    public void setValueDataAdapter(final XmlValueAdapter valueDataAdapter) {
        this.valueDataAdapter = valueDataAdapter;
    }

    /**
     * IoC.
     *
     * @param columnLookUpQueryParameterStrategy {@link XmlLookUpQueryParameterStrategy}  to use.
     */
    public void setColumnLookUpQueryParameterStrategy(final LookUpQueryParameterStrategy<XmlImpExDescriptor, XmlImpExTuple, XmlValueAdapter> columnLookUpQueryParameterStrategy) {
        this.columnLookUpQueryParameterStrategy = columnLookUpQueryParameterStrategy;
    }

    /**
     * IoC.
     *
     * @param handlerMap handler map
     */
    public void setHandlerMap(final Map<String, XmlEntityExportHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }
}

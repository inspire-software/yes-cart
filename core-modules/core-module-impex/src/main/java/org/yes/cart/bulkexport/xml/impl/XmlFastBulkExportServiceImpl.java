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
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

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

        final String msgInfoImp = MessageFormat.format("export file : {0}", fileToExport);
        statusListener.notifyMessage(msgInfoImp);

        final XmlEntityExportHandler<Object, String> handler = this.handlerMap.get(xmlExportDescriptor.getXmlHandler());

        if (handler == null) {
            final String msgNoHandler = MessageFormat.format("no handler : {0}", xmlExportDescriptor.getXmlHandler());
            statusListener.notifyError(msgNoHandler);
            return;
        }

        final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(fileToExport)), StandardCharsets.UTF_8);
        ResultsIterator<Object> results = null;
        try {

            writer.write(handler.startXml());

            final String filename = fileToExport;
            int count = 0;

            results = getExistingEntities(xmlExportDescriptor, xmlExportDescriptor.getSelectSql(), null, null);
            while (results.hasNext()) {
                final Object entity = results.next();
                final XmlExportTuple tuple = new XmlExportTupleImpl(entity);

                final String xml = handler.handle(statusListener, xmlExportDescriptor, tuple, valueDataAdapter, filename);

                if (xml != null) {
                    writer.write(xml);
                    count++;
                }
                releaseEntity(entity);
            }

            writer.write(handler.endXml());

            final String msgInfoLines = MessageFormat.format("total data objects : {0}", count);
            statusListener.notifyMessage(msgInfoLines);

        } catch (UnsupportedEncodingException e) {
            final String msgErr = MessageFormat.format(
                    "wrong file encoding in xml descriptor : {0} {1}",
                    xmlExportDescriptor.getExportFileDescriptor().getFileEncoding(),
                    e.getMessage());
            statusListener.notifyError(msgErr, e);

        } catch (IOException e) {
            final String msgErr = MessageFormat.format("cannot write the xml file : {0} {1}",
                    fileToExport,
                    e.getMessage());
            statusListener.notifyError(msgErr, e);
        } finally {
            try {
                if (results != null) {
                    results.close();
                }
            } catch (Exception exp) {
                final String msgErr = MessageFormat.format("cannot close the xml resultset : {0} {1}",
                        fileToExport,
                        exp.getMessage());
                statusListener.notifyError(msgErr, exp);
            }
            try {
                writer.close();
            } catch (IOException ioe) {
                final String msgErr = MessageFormat.format("cannot close the xml file : {0} {1}",
                        fileToExport,
                        ioe.getMessage());
                statusListener.notifyError(msgErr, ioe);
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

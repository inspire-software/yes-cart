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

package org.yes.cart.bulkimport.xml.impl;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.yes.cart.bulkcommon.service.ImportService;
import org.yes.cart.bulkcommon.service.support.auth.impl.SystemAdminFederationFacade;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkimport.service.impl.AbstractImportService;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.XmlImportDescriptor;
import org.yes.cart.service.async.JobStatusListener;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 04/11/2018
 * Time: 17:29
 */
public class XmlFastBulkImportServiceImpl extends AbstractImportService<XmlImportDescriptor> implements ImportService {

    private XmlValueAdapter valueDataAdapter;

    private Map<String, XmlEntityImportHandler> handlerMap = Collections.emptyMap();

    public XmlFastBulkImportServiceImpl() {
        super(new SystemAdminFederationFacade());
    }

    @Override
    protected void doSingleFileImportInternal(final JobStatusListener statusListener,
                                              final File fileToImport,
                                              final String importDescriptorName,
                                              final XmlImportDescriptor importDescriptor) throws Exception {


        statusListener.notifyMessage("import file : {}", fileToImport.getAbsolutePath());

        final XmlEntityImportHandler handler = this.handlerMap.get(importDescriptor.getXmlHandler());

        if (handler == null) {
            statusListener.notifyError("no handler : {}", importDescriptor.getXmlHandler());
            return;
        }

        // org.yes.cart.bulkexport.xml.internal
        final JAXBContext context = JAXBContext.newInstance(handler.getContextNamespace());

        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final XMLReader reader = factory.newSAXParser().getXMLReader();

        final ChunkFilter splitter = new ChunkFilter(
                context, handler.getElementName(),
                (tuple) -> {
                    handler.handle(statusListener, importDescriptor, tuple, valueDataAdapter, fileToImport.getName());
                },
                (line, exp) -> {
                    statusListener.notifyError("unable to process XML file: {}:{}, cause by: {}",
                            exp,
                            importDescriptor.getImportFileDescriptor().getFileEncoding(),
                            line,
                            exp.getMessage());
                });

        // connect two components
        reader.setContentHandler(splitter);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileToImport), StandardCharsets.UTF_8))) {

            reader.parse(new InputSource(in));

        }

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
     * @param handlerMap handler map
     */
    public void setHandlerMap(final Map<String, XmlEntityImportHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }
}

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

package org.yes.cart.bulkimport.xml.impl;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.helpers.XMLFilterImpl;
import org.yes.cart.bulkcommon.model.ImpExTuple;

import javax.xml.bind.*;
import java.util.Enumeration;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 21:34
 */
public class ChunkFilter extends XMLFilterImpl {

    private final JAXBContext context;
    private final String chunkElement;


    /**
     * Remembers the depth of the elements as we forward
     * SAX events to a JAXB unmarshaller.
     */
    private int depth;
    private String elementStart;

    // Element unmarshaller
    private UnmarshallerHandler unmarshallerHandler;

    private NamespaceSupport namespaces = new NamespaceSupport();

    private Locator locator;

    private final HandleCallback<Object> callback;
    private final HandleException exception;

    public ChunkFilter(final JAXBContext context,
                       final String chunkElement,
                       final HandleCallback<Object> callback,
                       final HandleException exception) {
        this.context = context;
        this.chunkElement = chunkElement;
        this.callback = callback;
        this.exception = exception;
    }

    /**
     * {@inheritDoc}
     */
    public void startElement(final String namespaceURI,
                             final String localName,
                             final String qName,
                             final Attributes atts) throws SAXException {

        if(depth != 1) {
            depth++;
            super.startElement(namespaceURI, localName, qName, atts);
            return;
        }

        initialiseForChunkElement(namespaceURI, localName, qName, atts);
    }

    private void initialiseForChunkElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {

        if ("".equals(namespaceURI) && localName.equals(this.chunkElement) ) {
            // start a new unmarshaller
            Unmarshaller unmarshaller;
            try {
                unmarshaller = context.createUnmarshaller();
            } catch( JAXBException e ) {
                // there's no way to recover from this error.
                // we will abort the processing.
                throw new SAXException(e);
            }
            unmarshallerHandler = unmarshaller.getUnmarshallerHandler();

            // set it as the content handler so that it will receive
            // SAX events from now on.
            setContentHandler(unmarshallerHandler);

            // fire SAX events to emulate the start of a new document.
            unmarshallerHandler.startDocument();
            unmarshallerHandler.setDocumentLocator(locator);

            elementStart = locator.getLineNumber() + "/" + locator.getColumnNumber();

            final Enumeration prefixes = namespaces.getPrefixes();
            while(prefixes.hasMoreElements()) {
                final String prefix = (String)prefixes.nextElement();
                final String uri = namespaces.getURI(prefix);
                unmarshallerHandler.startPrefixMapping(prefix,uri);
            }
            final String defaultURI = namespaces.getURI("");
            if(defaultURI != null) {
                unmarshallerHandler.startPrefixMapping("", defaultURI);
            }
            super.startElement(namespaceURI, localName, qName, atts);

            depth = 2;
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

        super.endElement(namespaceURI, localName, qName);

        if (depth != 0) {
            depth--;
            if(depth == 1) {
                // chunk completed

                // emulate the end of a document.
                final Enumeration prefixes = namespaces.getPrefixes();
                while (prefixes.hasMoreElements()) {
                    final String prefix = (String) prefixes.nextElement();
                    unmarshallerHandler.endPrefixMapping(prefix);
                }
                final String defaultURI = namespaces.getURI("");
                if (defaultURI != null) {
                    unmarshallerHandler.endPrefixMapping("");
                }
                unmarshallerHandler.endDocument();

                // stop forwarding events by setting a dummy handler.
                setContentHandler(new DefaultHandler());

                // then retrieve the fully unmarshalled object
                try {
                    final JAXBElement<Object> result =
                            (JAXBElement<Object>)unmarshallerHandler.getResult();
                    // process this new purchase order
                    callback.handle(new XmlImportTupleImpl(elementStart, result.getValue()));
                } catch( JAXBException je ) {

                    exception.handle(elementStart, je);
                    return;

                }

                unmarshallerHandler = null;
            }
        }
    }


    @Override
    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
        this.locator = locator;
    }


    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        namespaces.pushContext();
        namespaces.declarePrefix(prefix,uri);
        super.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        namespaces.popContext();
        super.endPrefixMapping(prefix);
    }

    interface HandleCallback<Object> {

        void handle(ImpExTuple<String, Object> tuple);

    }

    interface HandleException {

        void handle(String line, Exception exp);

    }

}

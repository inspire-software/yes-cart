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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.yes.cart.bulkcommon.service.DataDescriptorSampleGenerator;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.XmlImportDescriptor;
import org.yes.cart.domain.misc.Pair;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: inspiresoftware
 * Date: 16/01/2021
 * Time: 11:26
 */
public class XmlDataDescriptorSampleGeneratorImpl implements DataDescriptorSampleGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(XmlDataDescriptorSampleGeneratorImpl.class);

    private final Map<String, XmlEntityImportHandler> handlers;

    private byte[] schema;

    public XmlDataDescriptorSampleGeneratorImpl(final Map<String, XmlEntityImportHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean supports(final Object descriptor) {
        return descriptor instanceof XmlImportDescriptor &&
                this.handlers.containsKey(((XmlImportDescriptor) descriptor).getXmlHandler());
    }

    @Override
    public List<Pair<String, byte[]>> generateSample(final Object descriptor) {

        final List<Pair<String, byte[]>> templates = new ArrayList<>();

        final XmlImportDescriptor xml = (XmlImportDescriptor) descriptor;

        final String encoding = xml.getImportFileDescriptor().getFileEncoding();
        final Charset charset = encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;

        String fileName = xml.getImportFileDescriptor().getFileNameMask();
        if (fileName.endsWith("(.*)")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        fileName = fileName.replace("(.*)", "-XXXXXXX");

        templates.add(new Pair<>(fileName + "-readme.txt",
                ("Template: " + fileName + "\n" +
                "Encoding: " + encoding + "\n").getBytes(charset))
        );

        templates.add(new Pair<>("impex.xsd", loadSchema()));

        final XmlEntityImportHandler handler = this.handlers.get(xml.getXmlHandler());

        final String element = handler.getElementName();

        final String xmlSample = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<" + element + "s xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "        xsi:noNamespaceSchemaLocation=\"impex.xsd\">\n" +
                "<" + element + ">\n" +
                "    <!-- refer to impex.xsd -->\n" +
                "</" + element + ">\n" +
                "</" + element + "s>";

        templates.add(new Pair<>(fileName,
                xmlSample.getBytes(charset))
        );

        templates.add(new Pair<>(fileName + "-descriptor.xml",
                (xml.getSource() != null ? xml.getSource() : "<!-- no source -->").getBytes(charset))
        );

        return templates;
    }

    private byte[] loadSchema() {
        if (this.schema == null) {
            try {
                this.schema = StreamUtils.copyToString(
                        getClass().getResourceAsStream("/META-INF/schema/impex.xsd"),
                        StandardCharsets.UTF_8
                ).getBytes(StandardCharsets.UTF_8);
            } catch (Exception exp) {
                LOG.error("Unable to load XSD file /META-INF/schema/impex.xsd", exp);
                this.schema = new byte[0];
            }
        }
        return this.schema;
    }

}

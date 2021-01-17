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

package org.yes.cart.bulkexport.xml.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.yes.cart.bulkcommon.service.DataDescriptorSampleGenerator;
import org.yes.cart.bulkexport.xml.XmlEntityExportHandler;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.misc.Pair;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
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

    private final Map<String, XmlEntityExportHandler> handlers;

    private byte[] schema;

    public XmlDataDescriptorSampleGeneratorImpl(final Map<String, XmlEntityExportHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean supports(final Object descriptor) {
        return descriptor instanceof XmlExportDescriptor &&
                this.handlers.containsKey(((XmlExportDescriptor) descriptor).getXmlHandler());
    }

    @Override
    public List<Pair<String, byte[]>> generateSample(final Object descriptor) {

        final List<Pair<String, byte[]>> templates = new ArrayList<>();

        final XmlExportDescriptor xml = (XmlExportDescriptor) descriptor;

        final String encoding = xml.getExportFileDescriptor().getFileEncoding();
        final Charset charset = encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;

        String fileName = xml.getExportFileDescriptor().getFileName();
        fileName = fileName.replace("/", "_").replace("\\", "_");
        fileName = fileName.replace("{timestamp}", "yyyy-MM-dd_HH-mm-ss");

        templates.add(new Pair<>(fileName + "-readme.txt",
                ("Template: " + fileName + "\n" +
                "Encoding: " + encoding + "\n" +
                "Select command: " + xml.getSelectCmd() + "\n").getBytes(charset))
        );

        templates.add(new Pair<>("impex.xsd", loadSchema()));

        final XmlEntityExportHandler handler = this.handlers.get(xml.getXmlHandler());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter(baos, charset);
        try {
            handler.startXml(writer);
            handler.endXml(writer);
        } catch (Exception exp) {
            LOG.error("Unable to generate sample", exp);
        }

        templates.add(new Pair<>(fileName, baos.toByteArray()));

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

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

package org.yes.cart.bulkexport.xml;

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:13
 */
public interface XmlEntityExportHandler<T> {

    /**
     * Start XML output (header + root element).
     *
     * @param writer              writer stream to print tag(s) using {@link OutputStreamWriter#write(String)}
     *
     * @throws Exception in case of write errors
     */
    void startXml(OutputStreamWriter writer) throws Exception;


    /**
     * Handle single item
     *
     * @param statusListener      status listener
     * @param xmlExportDescriptor descriptor
     * @param tuple               tuple
     * @param xmlValueAdapter     adapter
     * @param fileToExport        file to export (if exporting to file)
     * @param writer              writer stream to print tag(s) using {@link OutputStreamWriter#write(String)}
     *
     * @throws Exception in case of write errors
     */
    void handle(JobStatusListener statusListener,
                XmlExportDescriptor xmlExportDescriptor,
                ImpExTuple<String, T> tuple,
                XmlValueAdapter xmlValueAdapter,
                String fileToExport,
                OutputStreamWriter writer) throws Exception;



    /**
     * End XML (close root).
     *
     * @param writer              writer stream to print tag(s) using {@link OutputStreamWriter#write(String)}
     *
     * @throws Exception in case of write errors
     */
    void endXml(OutputStreamWriter writer) throws Exception;

}

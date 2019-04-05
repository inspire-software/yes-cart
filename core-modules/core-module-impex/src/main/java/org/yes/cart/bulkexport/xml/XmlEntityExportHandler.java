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

package org.yes.cart.bulkexport.xml;

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.service.async.JobStatusListener;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:13
 */
public interface XmlEntityExportHandler<T, O> {

    /**
     * Start XML output (header + root element).
     *
     * @return start
     */
    String startXml();


    /**
     * Handle single item
     *
     * @param statusListener      status listener
     * @param xmlExportDescriptor descriptor
     * @param tuple               tuple
     * @param xmlValueAdapter     adapter
     * @param fileToExport        file to export
     *
     * @return XML fragment
     */
    O handle(JobStatusListener statusListener,
             XmlExportDescriptor xmlExportDescriptor,
             ImpExTuple<String, T> tuple,
             XmlValueAdapter xmlValueAdapter,
             String fileToExport);



    /**
     * End XML (close root).
     *
     * @return end
     */
    String endXml();

}

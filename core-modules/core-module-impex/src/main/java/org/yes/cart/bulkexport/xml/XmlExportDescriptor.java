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

import org.yes.cart.bulkcommon.xml.XmlImpExDescriptor;
import org.yes.cart.bulkexport.model.ExportDescriptor;

/**
 * Xml export descriptor.
 * <p/>
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 08:00
 */
public interface XmlExportDescriptor extends
        ExportDescriptor<XmlExportContext>, XmlImpExDescriptor<XmlExportContext> {

    /**
     * Get the export file description.
     *
     * @return {@link XmlExportFile}
     */
    @Override
    XmlExportFile getExportFileDescriptor();

    /**
     * XML handler implementation.
     *
     * @return bean name
     */
    String getXmlHandler();

}

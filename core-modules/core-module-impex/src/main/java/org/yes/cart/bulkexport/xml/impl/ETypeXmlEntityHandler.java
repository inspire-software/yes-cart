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

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.Etype;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.EtypeService;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class ETypeXmlEntityHandler extends AbstractXmlEntityHandler<Etype> {

    private EtypeService etypeService;

    public ETypeXmlEntityHandler() {
        super("e-types");
    }

    @Override
    public String handle(final JobStatusListener statusListener,
                         final XmlExportDescriptor xmlExportDescriptor,
                         final ImpExTuple<String, Etype> tuple,
                         final XmlValueAdapter xmlValueAdapter,
                         final String fileToExport) {

        return tagEType(null, tuple.getData()).toXml();
        
    }

    Tag tagEType(final Tag parent, final Etype type) {

        return tag(parent, "e-type")
                .attr("id", type.getEtypeId())
                .attr("guid", type.getGuid())
                .attr("java-type", type.getJavatype())
                .attr("business-type", type.getBusinesstype())
                    .tagTime(type)
                .end();

    }

    /**
     * Spring IoC.
     *
     * @param etypeService eType service
     */
    public void setEtypeService(final EtypeService etypeService) {
        this.etypeService = etypeService;
    }
}

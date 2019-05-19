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
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class AttributeXmlEntityHandler extends AbstractXmlEntityHandler<Attribute> {

    public AttributeXmlEntityHandler() {
        super("attributes");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Attribute> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer,
                       final Map<String, Integer> entityCount) throws Exception {

        handleInternal(tagAttribute(null, tuple.getData()), writer, entityCount);

    }

    Tag tagAttribute(final Tag parent, final Attribute attribute) {

        return tag(parent, "attribute")
                .attr("id", attribute.getAttributeId())
                .attr("guid", attribute.getGuid())
                .attr("code", attribute.getCode())
                .attr("group", attribute.getAttributeGroup().getCode())
                .attr("etype", attribute.getEtype().getBusinesstype())
                .attr("rank", attribute.getRank())
                    .tagBool("mandatory", attribute.isMandatory())
                    .tagBool("secure", attribute.isSecure())
                    .tagBool("allow-duplicate", attribute.isAllowduplicate())
                    .tagBool("allow-failover", attribute.isAllowfailover())
                    .tagBool("store", attribute.isStore())
                    .tagBool("search", attribute.isSearch())
                    .tagBool("primary", attribute.isPrimary())
                    .tagBool("navigation", attribute.isNavigation())
                    .tagChars("val", attribute.getVal())
                    .tagChars("regexp", attribute.getRegexp())
                    .tagI18n("validation-failed-message", attribute.getValidationFailedMessage())
                    .tagI18n("choice-data", attribute.getChoiceData())
                    .tagCdata("name", attribute.getName())
                    .tagI18n("display-name", attribute.getDisplayName())
                    .tagCdata("description", attribute.getDescription())
                    .tagTime(attribute)
                .end();

    }

}

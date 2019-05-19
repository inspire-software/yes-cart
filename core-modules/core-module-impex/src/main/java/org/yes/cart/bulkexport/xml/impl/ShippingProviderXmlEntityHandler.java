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
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class ShippingProviderXmlEntityHandler extends AbstractXmlEntityHandler<Carrier> {

    private final ShippingMethodXmlEntityHandler methodHandler = new ShippingMethodXmlEntityHandler();

    public ShippingProviderXmlEntityHandler() {
        super("shipping-providers");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Carrier> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer,
                       final Map<String, Integer> entityCount) throws Exception {

        handleInternal(tagShippingProvider(null, tuple.getData()), writer, entityCount);

    }


    Tag tagShippingProvider(final Tag parent, final Carrier provider) {

        final Tag tag = tag(parent, "shipping-provider")
                .attr("id", provider.getCarrierId())
                .attr("guid", provider.getGuid())
                .tagCdata("name", provider.getName())
                .tagI18n("display-name", provider.getDisplayName())
                .tagCdata("description", provider.getDescription())
                .tagI18n("display-description", provider.getDisplayDescription())
                .tag("configuration")
                    .attr("worldwide", provider.isWorldwide())
                    .attr("country", provider.isCountry())
                    .attr("state", provider.isState())
                    .attr("local", provider.isLocal())
                .end();

        final Tag shippingMethodTag = tag.tag("shipping-methods");

        for (final CarrierSla carrierSla : provider.getCarrierSla()) {

            this.methodHandler.tagShippingMethod(shippingMethodTag, carrierSla);
            
        }

        shippingMethodTag.end();

        return tag
                .tagTime(provider)
            .end();

    }

    /**
     * Spring IoC.
     *
     * @param prettyPrint set pretty print mode (new lines and indents)
     */
    @Override
    public void setPrettyPrint(final boolean prettyPrint) {
        super.setPrettyPrint(prettyPrint);
        this.methodHandler.setPrettyPrint(prettyPrint);
    }
}

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
import org.yes.cart.payment.persistence.entity.PaymentGatewayCallback;
import org.yes.cart.service.async.JobStatusListener;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class PaymentGatewayCallbackXmlEntityHandler extends AbstractXmlEntityHandler<PaymentGatewayCallback> {

    public PaymentGatewayCallbackXmlEntityHandler() {
        super("payment-gateway-callbacks");
    }

    @Override
    public String handle(final JobStatusListener statusListener,
                         final XmlExportDescriptor xmlExportDescriptor,
                         final ImpExTuple<String, PaymentGatewayCallback> tuple,
                         final XmlValueAdapter xmlValueAdapter,
                         final String fileToExport) {

        return tagCallback(null, tuple.getData()).toXml();

    }


    Tag tagCallback(final Tag parent, final PaymentGatewayCallback callback) {

        final Tag cbTag = tag(parent, "payment-gateway-callback")
                .attr("id", callback.getPaymentGatewayCallbackId())
                .attr("guid", callback.getGuid())
                .attr("shop-code", callback.getShopCode())
                    .tagChars("payment-gateway", callback.getLabel())
                    .tagChars("request-dump", callback.getRequestDump());

            final Tag rpTag = cbTag.tag("request-parameters");
            for (final Map.Entry<String, String[]> entry : ((Map<String, String[]>)callback.getParameterMap()).entrySet()) {
                final Tag rpiTag = rpTag.tag("request-parameter").attr("name", entry.getKey());
                for (final String value : entry.getValue()) {
                    rpiTag.tagChars("value", value);
                }
                rpiTag.end();
            }
            rpTag.end();


        return cbTag
                    .tagTime(callback)
                .end();

    }

}

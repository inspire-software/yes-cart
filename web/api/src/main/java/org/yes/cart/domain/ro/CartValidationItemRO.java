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

package org.yes.cart.domain.ro;

import org.yes.cart.domain.ro.xml.impl.StringMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 15/11/2017
 * Time: 19:50
 */
@XmlRootElement(name = "cart-validation-item")
public class CartValidationItemRO {

    private boolean checkoutBlocking;
    private String messageType;
    private String messageKey;
    private Map<String, String> parameters;

    @XmlAttribute(name = "checkout-blocking")
    public boolean isCheckoutBlocking() {
        return checkoutBlocking;
    }

    public void setCheckoutBlocking(final boolean checkoutBlocking) {
        this.checkoutBlocking = checkoutBlocking;
    }

    @XmlAttribute(name = "message-type")
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(final String messageType) {
        this.messageType = messageType;
    }

    @XmlAttribute(name = "message-key")
    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(final String messageKey) {
        this.messageKey = messageKey;
    }

    @XmlJavaTypeAdapter(StringMapAdapter.class)
    @XmlElement(name = "parameters")
    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }
}

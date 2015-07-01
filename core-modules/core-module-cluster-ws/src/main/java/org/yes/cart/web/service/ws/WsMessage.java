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

package org.yes.cart.web.service.ws;

import org.springframework.util.SerializationUtils;
import org.yes.cart.cluster.node.Message;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * User: denispavlov
 * Date: 17/06/2015
 * Time: 22:16
 */
@XmlRootElement(name = "message")
public class WsMessage {

    private String source;
    private ArrayList<String> targets;
    private String subject;
    private byte[] payload;

    public WsMessage() {
    }

    public WsMessage(final Message message) {
        this.source = message.getSource();
        this.subject = message.getSubject();
        if (message.getTargets() != null) {
            this.targets = new ArrayList<String>(message.getTargets());
        }
        this.payload = SerializationUtils.serialize(message.getPayload());
    }

    @XmlAttribute(name = "source")
    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    @XmlElementWrapper(name = "targets")
    @XmlElement(name = "target")
    public ArrayList<String> getTargets() {
        return targets;
    }

    public void setTargets(final ArrayList<String> targets) {
        this.targets = targets;
    }

    @XmlAttribute(name = "subject")
    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    @XmlElement(name = "payload")
    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(final byte[] payload) {
        this.payload = payload;
    }

    public <T extends Serializable> T getPayloadObject() {
        return (T) SerializationUtils.deserialize(this.payload);
    }

}

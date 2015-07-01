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

package org.yes.cart.cluster.node.impl;

import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.RspMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 17/06/2015
 * Time: 13:06
 */
public class RspMessageImpl extends BasicMessageImpl implements RspMessage {

    private final List<Message> responses = new ArrayList<Message>();

    public RspMessageImpl(final String source,
                          final String subject,
                          final Serializable payload) {
        super(source, subject, payload);
    }

    public RspMessageImpl(final String source,
                          final List<String> targets,
                          final String subject,
                          final Serializable payload) {
        super(source, targets, subject, payload);
    }

    /**
     * {@inheritDoc}
     */
    public void addResponse(final Message message) {
        this.responses.add(message);
    }

    /**
     * {@inheritDoc}
     */
    public List<Message> getResponses() {
        return Collections.unmodifiableList(this.responses);
    }
}

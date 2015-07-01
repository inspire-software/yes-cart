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

import java.io.Serializable;
import java.util.List;

/**
 * User: denispavlov
 * Date: 16/06/2015
 * Time: 12:13
 */
public class BasicMessageImpl implements Message {

    private final String source;
    private final List<String> targets;
    private final String subject;
    private final Serializable payload;

    public BasicMessageImpl(final String source,
                            final String subject,
                            final Serializable payload) {
        this(source, null, subject, payload);
    }

    public BasicMessageImpl(final String source,
                            final List<String> targets,
                            final String subject,
                            final Serializable payload) {
        this.source = source;
        this.targets = targets;
        this.subject = subject;
        this.payload = payload;
    }


    /**
     * {@inheritDoc}
     */
    public String getSource() {
        return source;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getTargets() {
        return targets;
    }

    /**
     * {@inheritDoc}
     */
    public String getSubject() {
        return subject;
    }

    /**
     * {@inheritDoc}
     */
    public Serializable getPayload() {
        return payload;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BasicMessageImpl{" +
                "source='" + source + '\'' +
                ", targets=" + targets +
                ", subject='" + subject + '\'' +
                ", payload=" + payload +
                '}';
    }
}

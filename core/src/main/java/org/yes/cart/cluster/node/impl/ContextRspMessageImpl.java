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

import org.yes.cart.cluster.node.ContextRspMessage;
import org.yes.cart.service.async.model.AsyncContext;

import java.io.Serializable;
import java.util.List;

/**
 * User: denispavlov
 * Date: 17/06/2015
 * Time: 13:08
 */
public class ContextRspMessageImpl extends RspMessageImpl implements ContextRspMessage {

    private final AsyncContext asyncContext;

    public ContextRspMessageImpl(final String source,
                                 final String subject,
                                 final Serializable payload,
                                 final AsyncContext asyncContext) {
        super(source, subject, payload);
        this.asyncContext = asyncContext;
    }

    public ContextRspMessageImpl(final String source,
                                 final List<String> targets,
                                 final String subject,
                                 final Serializable payload,
                                 final AsyncContext asyncContext) {
        super(source, targets, subject, payload);
        this.asyncContext = asyncContext;
    }

    /**
     * {@inheritDoc}
     */
    public AsyncContext getAsyncContext() {
        return asyncContext;
    }
}

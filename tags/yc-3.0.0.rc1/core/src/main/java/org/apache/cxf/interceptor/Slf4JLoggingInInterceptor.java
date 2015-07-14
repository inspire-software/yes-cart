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

package org.apache.cxf.interceptor;

import org.apache.cxf.common.logging.Slf4jLogger;
import org.apache.cxf.message.Message;
import org.yes.cart.cluster.node.NodeService;

import java.util.logging.Logger;

/**
 * User: denispavlov
 * Date: 22/06/2015
 * Time: 13:23
 */
public class Slf4JLoggingInInterceptor extends LoggingInInterceptor {

    private final Logger logger;

    public Slf4JLoggingInInterceptor(final NodeService nodeService) {
        logger = new Slf4jLogger("WS.IN." + nodeService.getCurrentNodeId(), null);
    }

    /**
     * {@inheritDoc}
     */
    protected Logger getLogger() {
        return logger;
    }

    /**
     * {@inheritDoc}
     */
    Logger getMessageLogger(final Message message) {
        return getLogger();
    }
}

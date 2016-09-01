/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.order.impl;

import org.yes.cart.service.order.OrderFlow;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 31/08/2016
 * Time: 20:06
 */
public class SimpleStaticOrderFlow implements OrderFlow {

    private final Map<String, List<String>> flow;

    public SimpleStaticOrderFlow(final Map<String, List<String>> flow) {
        this.flow = flow;
    }

    @Override
    public List<String> getNext(final String pgLabel, final String currentStatus) {
        final List<String> next = flow.get(currentStatus);
        if (next == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(next);
    }
}

/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.domain.vo;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 09/05/2020
 * Time: 17:35
 */
public class VoCustomerOrderTransition {

    private String transition;
    private Map<String, String> context;

    public String getTransition() {
        return transition;
    }

    public void setTransition(final String transition) {
        this.transition = transition;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(final Map<String, String> context) {
        this.context = context;
    }
}

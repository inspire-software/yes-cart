/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.remote.service.impl;

import flex.messaging.FlexContext;
import org.yes.cart.remote.service.RemoteFlexSupportService;

/**
 *
 * Utility to support flex session.
 *
 * User:  Igor Azarny
 * Date: 4/28/12
 * Time: 10:54 AM
 */
public class RemoteFlexSupportServiceImpl implements RemoteFlexSupportService {

    /** {@inheritDoc} */
    public void setSessionInfo(final String key, final String value) {
        FlexContext.getFlexSession().setAttribute(key, value);
    }
}

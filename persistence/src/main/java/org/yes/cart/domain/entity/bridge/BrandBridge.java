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

package org.yes.cart.domain.entity.bridge;

import org.hibernate.search.bridge.StringBridge;
import org.hibernate.search.bridge.TwoWayStringBridge;
import org.yes.cart.domain.entity.Brand;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 * */
public class BrandBridge implements StringBridge, TwoWayStringBridge {
    /** {@inheritDoc} */
    public String objectToString(final Object brandObject) {
        return ((Brand) brandObject).getName().toLowerCase();
    }

    /** {@inheritDoc} */
    public Object stringToObject(final String stringValue) {
        return stringValue; // This is used for projections
    }
}

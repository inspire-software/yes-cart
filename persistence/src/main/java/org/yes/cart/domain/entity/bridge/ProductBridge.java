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

package org.yes.cart.domain.entity.bridge;

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.search.bridge.TwoWayStringBridge;
import org.hibernate.search.bridge.builtin.impl.TwoWayString2FieldBridgeAdaptor;
import org.yes.cart.domain.entity.Product;

/**
 * User: denispavlov
 * Date: 27/11/2014
 * Time: 00:06
 */
public class ProductBridge extends TwoWayString2FieldBridgeAdaptor {

    public ProductBridge() {
        super(new TwoWayStringBridge() {
            @Override
            public Object stringToObject(final String stringValue) {
                return NumberUtils.toLong(stringValue);
            }

            @Override
            public String objectToString(final Object object) {
                return String.valueOf(((Product) object).getProductId());
            }
        });
    }

}

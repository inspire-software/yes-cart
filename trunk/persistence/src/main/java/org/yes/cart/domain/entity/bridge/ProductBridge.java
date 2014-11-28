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

import org.hibernate.search.bridge.StringBridge;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;

/**
 * User: denispavlov
 * Date: 27/11/2014
 * Time: 00:06
 */
public class ProductBridge implements StringBridge {
    /** {@inheritDoc} */
    public String objectToString(final Object productObject) {
        return String.valueOf(((Product) productObject).getProductId());
    }
}

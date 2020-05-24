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

package org.yes.cart.domain.dto.impl;

import org.yes.cart.domain.dto.ProductSearchResultNavItemDTO;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;

/**
 * User: denispavlov
 * Date: 03/11/2017
 * Time: 10:50
 */
public class ProductSearchResultNavItemDTOImpl implements ProductSearchResultNavItemDTO {

    private final Pair<Pair<String, I18NModel>, Integer> data;

    public ProductSearchResultNavItemDTOImpl(final Pair<Pair<String, I18NModel>, Integer> data) {
        this.data = data;
    }

    /** {@inheritDoc} */
    @Override
    public String getValue() {
        return data.getFirst().getFirst();
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayValue(final String locale) {
        final I18NModel model = data.getFirst().getSecond();
        if (model != null) {
            final String i18n = model.getValue(locale);
            if (i18n != null) {
                return i18n;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Integer getCount() {
        return data.getSecond();
    }
}

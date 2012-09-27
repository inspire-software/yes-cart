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

package org.yes.cart.web.page.component.header;

import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.Currency;
import org.yes.cart.web.page.component.Language;
import org.yes.cart.web.page.component.cart.SmallShoppingCartView;
import org.yes.cart.web.page.component.product.FeaturedProducts;
import org.yes.cart.web.page.component.search.SearchView;

/**
 * User: iazarny@yahoo.com
 * Date: 9/27/12
 * Time: 2:01 PM
 */
public class StandardHeader  extends BaseComponent {

    /**
     * {@inheritDoc}
     */
    protected void onBeforeRender() {

        addOrReplace(new SearchView("search"));
        addOrReplace(new SmallShoppingCartView("smallCart"));
        addOrReplace(new FeaturedProducts("featured"));
        addOrReplace(new Currency("currency"));
        addOrReplace(new Language("language"));
        super.onBeforeRender();
    }

    /**
     * Construct view.
     * @param id component id.
     */
    public StandardHeader(final String id) {
        super(id);
    }
}

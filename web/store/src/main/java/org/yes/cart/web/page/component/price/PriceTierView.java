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

package org.yes.cart.web.page.component.price;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.SkuPriceQuantityComparatorImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 17-Sep-2011
 * Time: 13:51:49
 */
public class PriceTierView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    /**
     * Single item price panel
     */
    private final static String PRICE_VIEW = "priceView";
    private final static String PRICE_TIERS_LIST = "skusPriceTiers";
    private final static String QUANTITY_LABEL = "quantity";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    private List<SkuPrice> skuPrices;

    /**
     * Construct price tiers view.
     *
     * @param id        component id
     * @param rawPrices list of prices
     */
    public PriceTierView(final String id, final Collection<SkuPrice> rawPrices) {
        super(id);
        skuPrices = new ArrayList<SkuPrice>(rawPrices);
        Collections.sort(skuPrices, new SkuPriceQuantityComparatorImpl());
    }

    /** {@inheritDoc} */
    @Override
    protected void onBeforeRender() {
        add(
            new ListView<SkuPrice>(PRICE_TIERS_LIST, skuPrices) {
                protected void populateItem(ListItem<SkuPrice> listItem) {
                    listItem.add(
                            new Label(QUANTITY_LABEL, String.valueOf(listItem.getModelObject().getQuantity().intValue()))
                    );
                    listItem.add(
                            new PriceView(PRICE_VIEW, listItem.getModel(), true, false)
                    );
                }
            }
        );
        super.onBeforeRender();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isVisible() {
        return super.isVisible() && skuPrices.size() > 1;
    }
}

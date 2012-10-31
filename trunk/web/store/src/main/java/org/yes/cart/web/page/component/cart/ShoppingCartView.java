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

package org.yes.cart.web.page.component.cart;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;

import java.math.BigDecimal;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/8/11
 * Time: 1:18 PM
 */
public class ShoppingCartView extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String SUB_TOTAL_VIEW = "subTotalView";
    private static final String ITEMS_LIST = "itemsList";
    private static final String CART_FORM = "cartForm";


    private static final String DELIVERY_COST_LABEL = "deliveryCost";
    private static final String TAX_LABEL = "tax";
    private static final String TOTAL_LABEL = "total";
    private static final String GRAND_TOTAL_LABEL = "grandTotal";
    private static final String DELIVERY_LIST = "deliveryList";
    private static final String NEXT_LINK = "nextLink";


    private static final String MULTIPLE_DELIVERY_CHECKBOX = "multipleDelivery";
    private static final String MULTIPLE_DELIVERY_LABEL = "multipleDeliveryLabel";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    /**
     * Construct shopping cart view.
     *
     * @param id component id
     */
    public ShoppingCartView(final String id) {
        super(id);

        addOrReplace(
                new Form(CART_FORM).addOrReplace(
                        new ShoppingCartItemsList(ITEMS_LIST, ApplicationDirector.getShoppingCart().getCartItemList())
                ).addOrReplace(
                        new FeedbackPanel(FEEDBACK)
                ).addOrReplace(
                        new PriceView(
                                SUB_TOTAL_VIEW,
                                ApplicationDirector.getShoppingCart().getCartSubTotal(),
                                ApplicationDirector.getShoppingCart().getCurrencyCode(),
                                true)
                )
        );

    }


}

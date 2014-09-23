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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.misc.PluralFormService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.ShoppingCartPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.util.WicketUtil;

import java.math.BigDecimal;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 18-Sep-2011
 * Time: 09:56:58
 */
public class SmallShoppingCartView extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String QTY_LABEL = "qtyLabel";
    private static final String EMPTY_LABEL = "emptyCart";
    private static final String SUB_TOTAL_VIEW = "subTotal";
    private static final String CART_LINK = "cartLink";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    private static final String [] pluralForms = new String [] {
            "itemForm0",
            "itemForm1",
            "itemForm2"
    };

    @SpringBean(name = ServiceSpringKeys.PRICE_SERVICE)
    private PriceService priceService;

    @SpringBean(name = ServiceSpringKeys.PLURAL_FORM_SERVICE)
    private PluralFormService pluralFormService;

    /**
     * Construct small cart view.
     *
     * @param id component id.
     */
    public SmallShoppingCartView(final String id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {
        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        final Integer itemsInCart = cart.getCartItemsCount();
        final SkuPrice skuPrice = priceService.getGenericDao().getEntityFactory().getByIface(SkuPrice.class);
        final String linkTarget = getLinkTarget();
        skuPrice.setRegularPrice(cart.getTotal().getSubTotal());
        skuPrice.setCurrency(cart.getCurrencyCode());
        skuPrice.setQuantity(BigDecimal.ONE);

        final String resourceKey = pluralFormService.getPluralForm(
                cart.getCurrentLocale(),
                itemsInCart,
                pluralForms);



        add(
                new Label(
                        EMPTY_LABEL,
                        new StringResourceModel("noItem", this, null, itemsInCart)
                ).setVisible(isCartEmpty())
        );

        add(
                new ExternalLink(
                        CART_LINK,
                        linkTarget
                )
                        .add(
                                new PriceView(
                                        SUB_TOTAL_VIEW,
                                        new Model<SkuPrice>(skuPrice),
                                        true, false
                                )
                        )
                        .add(
                                new Label(
                                        QTY_LABEL,
                                        isCartEmpty()?
                                                new StringResourceModel("noItem", this, null, itemsInCart):
                                                new StringResourceModel(resourceKey, this, null, itemsInCart)
                                )
                        )
                        .setVisible(!isCartEmpty())
        );

        super.onBeforeRender();
    }

    private String getLinkTarget() {

        if (getPage() instanceof ShoppingCartPage) {
            return WicketUtil.getHttpServletRequest().getContextPath() + "/checkout";
        }
        return WicketUtil.getHttpServletRequest().getContextPath() + "/cart";
    }

    /**
     *
     * @return true in case of empty cart
     */
    public boolean isCartEmpty() {
        return ApplicationDirector.getShoppingCart().getCartItemsCount() == 0;
    }

}

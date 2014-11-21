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

package org.yes.cart.web.page.component.product;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductQuantityModel;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ProductServiceFacade;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 23/10/2014
 * Time: 18:20
 */
public class QuantityPickerPanel extends BaseComponent {

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_SERVICE_FACADE)
    protected ProductServiceFacade productServiceFacade;

    private final Long productId;
    private final String sku;

    public QuantityPickerPanel(final String id, final Long productId, final String sku) {
        super(id);
        this.productId = productId;
        this.sku = sku;
    }

    protected void onBeforeRender() {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        final Product product = productServiceFacade.getProductById(productId);
        final BigDecimal cartQty = cart.getProductSkuQuantity(sku);
        final ProductQuantityModel pqm = productServiceFacade.getProductQuantity(cartQty, product);

        final String message;
        if (!pqm.canOrderMore()) {
            message = getLocalizer().getString("quantityPickerFullTooltip", this,
                    new Model<Object[]>(new Object[] {
                            pqm.getCartQty().toPlainString()
                    }));
        } else if (pqm.hasMax()) {
            message = getLocalizer().getString("quantityPickerTooltip", this,
                    new Model<Object[]>(new Object[] {
                            pqm.getMin().toPlainString(),
                            pqm.getStep().toPlainString(),
                            pqm.getMax().toPlainString(),
                            pqm.getCartQty().toPlainString()
                    }));
        } else {
            message = getLocalizer().getString("quantityPickerTooltipNoMax", this,
                    new Model<Object[]>(new Object[] {
                            pqm.getMin().toPlainString(),
                            pqm.getStep().toPlainString(),
                            pqm.getCartQty().toPlainString()
                    }));
        }

        add(new ExternalLink("removeOneLink", "#"));
        add(new Label("quantity")
                .add(new AttributeModifier("yc-data-min", pqm.getMinOrder().toPlainString()))
                .add(new AttributeModifier("yc-data-max", pqm.getMaxOrder().toPlainString()))
                .add(new AttributeModifier("yc-data-step", pqm.getStep().toPlainString()))
                .add(new AttributeModifier("value", pqm.getMinOrder().toPlainString()))
                .add(new AttributeModifier("title", message)));
        add(new ExternalLink("addOneLink", "#"));

        super.onBeforeRender();
    }
}

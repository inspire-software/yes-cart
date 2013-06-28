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

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 17-Sep-2011
 * Time: 11:07:59
 */
public class SkuInListView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String SKU_LINK = "skuLink";
    private final static String SKU_CODE_LABEL = "skuCode";
    private final static String SKU_NAME_LABEL = "skuName";
    // ------------------------------------- MARKUP IDs END ------------------------------------ //

    private final ProductSku sku;


    /**
     * Construct sku in list view.
     *
     * @param id  component id
     * @param sku given {@link ProductSku} to show
     */
    public SkuInListView(final String id, final ProductSku sku) {
        super(id);
        this.sku = sku;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final String selectedLocale = getLocale().getLanguage();

        final I18NModel nameModel = getI18NSupport().getFailoverModel(sku.getDisplayName(), sku.getName());

        final Class homePage = Application.get().getHomePage();

        add(
                getWicketSupportFacade().links().newProductSkuLink(SKU_LINK, sku.getId(), getPage().getPageParameters())
                        .add(new Label(SKU_CODE_LABEL, sku.getCode()))
                        .add(new Label(SKU_NAME_LABEL, nameModel.getValue(selectedLocale)))
        );


        super.onBeforeRender();

    }
}

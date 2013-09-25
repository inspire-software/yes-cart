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

package org.yes.cart.web.page.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 9:17 PM
 */
public class Currency extends BaseComponent {

     // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CURRENCY_LIST = "supportedCurrencies";
    private final static String CURRENCY_LINK = "switchCurrencyLink";
    private final static String CURRENCY_NAME = "currencyName";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.CURRENCY_SYMBOL_SERVICE)
    private CurrencySymbolService currencySymbolService;


    /**
     * Construct panel.
     *
     * @param id panel id
     */
    public Currency(final String id) {
        super(id);
    }

    /**
     * Construct panel.
     *
     * @param id    panel id
     * @param model model.
     */
    public Currency(final String id, final IModel<?> model) {
        super(id, model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        if (isVisible()) {

            final List<String> supportedCurrencies = ApplicationDirector.getCurrentShop().getSupportedCurrenciesAsList();

            final PageParameters basePageParameters = WicketUtil.getFilteredRequestParameters(getPage().getPageParameters());

            add( new ListView<String>(CURRENCY_LIST, supportedCurrencies) {

                protected void populateItem(ListItem<String> stringListItem) {

                    final String currencyCode = stringListItem.getModelObject();
                    final String currencySymbol = currencySymbolService.getCurrencySymbol(currencyCode);

                    final Link pageLink = getWicketSupportFacade().links().newChangeCurrencyLink(
                            CURRENCY_LINK,
                            currencyCode,
                            basePageParameters);

                    final Label currencyLabel = new Label(CURRENCY_NAME, currencySymbol);

                    currencyLabel.setEscapeModelStrings(false);

                    if (currencyCode.equals(ApplicationDirector.getShoppingCart().getCurrencyCode())) {

                        pageLink.add(new AttributeModifier(HTML_CLASS, "currency-active"));
                    }

                    pageLink.add(currencyLabel);

                    stringListItem.add(pageLink);
                }
            }
            );

        }
        super.onBeforeRender();
    }


}

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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.util.WicketUtil;

import java.util.Collections;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/11/11
 * Time: 11:15 PM
 */
public class Language extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String LANGUAGE_LIST = "languageList";
    private final static String LANGUAGE_LINK = "languageLink";
    private final static String LANGUAGE_NAME = "languageName";
    private final static String ACTIVE_LANGUAGE_NAME = "activeLanguageName";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.LANGUAGE_SERVICE)
    private LanguageService languageService;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    /**
     * Construct panel.
     *
     * @param id panel id
     */
    public Language(final String id) {
        super(id);
    }

    /**
     * Construct panel.
     *
     * @param id    panel id
     * @param model model.
     */
    public Language(final String id, final IModel<?> model) {
        super(id, model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        if (isVisible()) {

            if (StringUtils.isBlank(ApplicationDirector.getShoppingCart().getCurrentLocale())) {
                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_CHANGELOCALE, ApplicationDirector.getShoppingCart(),
                        (Map) Collections.singletonMap(ShoppingCartCommand.CMD_CHANGELOCALE, getSession().getLocale().getLanguage()));
            }

            final PageParameters basePageParameters = WicketUtil.getFilteredRequestParameters(getPage().getPageParameters());

            final String activeLocal = ApplicationDirector.getShoppingCart().getCurrentLocale();
            final String activeLanguageName = languageService.resolveLanguageName(activeLocal);

            add(new Label(ACTIVE_LANGUAGE_NAME, activeLanguageName));
            add(new ListView<String>(LANGUAGE_LIST, languageService.getSupportedLanguages(ShopCodeContext.getShopCode())) {

                @Override
                protected void populateItem(final ListItem<String> stringListItem) {

                    final String languageCode = stringListItem.getModelObject();

                    final String languageName = languageService.resolveLanguageName(languageCode);

                    final Link pageLink = getWicketSupportFacade().links().newChangeLocaleLink(
                            LANGUAGE_LINK,
                            languageCode,
                            getPage().getPageClass(),
                            basePageParameters);

                    final boolean isActiveLng = languageCode.equals(activeLocal);

                    final Label languageLabel = new Label(LANGUAGE_NAME, languageName);

                    languageLabel.setEscapeModelStrings(false);

                    pageLink.add(languageLabel);

                    stringListItem.add(pageLink).setVisible(!isActiveLng);
                }
            }
            );

        }

        super.onBeforeRender();
    }

}

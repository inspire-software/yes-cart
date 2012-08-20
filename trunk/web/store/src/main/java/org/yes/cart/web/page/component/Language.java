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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.shoppingcart.impl.ChangeLocaleCartCommandImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.LanguageService;
import org.yes.cart.web.util.WicketUtil;

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
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.LANGUAGE_SERVICE)
    private LanguageService languageService;

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

            final String selectedLocale = getLocale().getLanguage();

            if (StringUtils.isBlank(ApplicationDirector.getShoppingCart().getCurrentLocale())) {
                       //todo set locale into cart
            }

            final PageParameters basePageParameters = WicketUtil.getFilteredRequestParameters(getPage().getPageParameters());

            add(new ListView<String>(LANGUAGE_LIST, languageService.getSupportedLanguages()) {

                @Override
                protected void populateItem(final ListItem<String> stringListItem) {

                    final String languageCode = stringListItem.getModelObject();

                    final String languageName = languageService.resolveLanguageName(languageCode);

                    final PageParameters itemPageParameters =  new PageParameters(basePageParameters);

                    itemPageParameters.add(ChangeLocaleCartCommandImpl.CMD_KEY, languageCode);

                    final BookmarkablePageLink<HomePage> pageLink = new BookmarkablePageLink<HomePage>(
                            LANGUAGE_LINK,
                            getPage().getClass(),
                            itemPageParameters);

                    final Label languageLabel = new Label(LANGUAGE_NAME, languageName);

                    languageLabel.setEscapeModelStrings(false);

                    if (languageCode.equals(ApplicationDirector.getShoppingCart().getCurrentLocale())) {

                        pageLink.add(new AttributeModifier(HTML_CLASS, "language-active"));

                    }

                    pageLink.add(languageLabel);

                    stringListItem.add(pageLink);
                }
            }
            );

        }

        super.onBeforeRender();
    }

}

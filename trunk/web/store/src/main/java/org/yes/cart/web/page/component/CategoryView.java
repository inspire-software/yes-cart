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
import org.apache.wicket.ThreadContext;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 2:01 PM
 */
public class CategoryView extends  BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CATEGORY_IMAGE_LINK = "categoryLinkImage";
    private final static String CATEGORY_IMAGE = "categoryImage";
    private final static String CATEGORY_NAME_LINK = "categoryLinkName";
    private final static String CATEGORY_NAME = "categoryName";
    private final static String CATEGORY_DESCR_LINK = "categoryLinkDescription";
    private final static String CATEGORY_DESCR = "categoryDescription";
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //


    /**
     * Construct category view.
     * @param id component id
     */
    public CategoryView(final String id) {
        super(id);
    }

    @Override
    protected void onBeforeRender() {

        final String selectedLocale = getLocale().getLanguage();

        final CategoryDecorator category = (CategoryDecorator) this.getParent().getDefaultModel().getObject();

        final PageParameters pageParameters = new PageParameters().add(WebParametersKeys.CATEGORY_ID, category.getCategoryId());

        final String [] defSize = category.getDefaultImageSize(category);

        final String width = defSize[0];
        final String height = defSize[1];


        add(
            new BookmarkablePageLink<HomePage>(CATEGORY_IMAGE_LINK, HomePage.class, pageParameters).add(
                    new ContextImage(CATEGORY_IMAGE, category.getDefaultImage(width, height))
                            .add(new AttributeModifier(HTML_WIDTH, width))
                            .add(new AttributeModifier(HTML_HEIGHT, height))
            )
        ).add(
            new BookmarkablePageLink<HomePage>(CATEGORY_NAME_LINK, HomePage.class, pageParameters).add(
                    new Label(CATEGORY_NAME, category.getName(selectedLocale)).setEscapeModelStrings(false)
            )
        ).add(
                new BookmarkablePageLink<HomePage>(CATEGORY_DESCR_LINK, HomePage.class, pageParameters).add(
                        new Label( CATEGORY_DESCR,  getDescription(category)  ).setEscapeModelStrings(false)
                )
        );


        super.onBeforeRender();
    }

    /**
     * Get category description for UI.
     * @param category   category
     * @return  category description
     */
    private String getDescription(final CategoryDecorator category) {
        final String desc = category.getDescription(getLocale().getLanguage());
        if(StringUtils.isBlank(desc)) {
            return "&nbsp;";
        }
        return desc;
    }
}

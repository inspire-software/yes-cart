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

import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.util.CollectionUtils;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CategoryServiceFacade;
import org.yes.cart.web.support.service.ContentServiceFacade;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 8:43 PM
 */
public class TopCategories extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CATEGORY_LIST = "categoryList";
    private final static String CATEGORY_NAME_LINK = "categoryLinkName";
    private final static String CATEGORY_NAME = "categoryName";
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CATEGORY_SERVICE_FACADE)
    private CategoryServiceFacade categoryServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    /**
     * Construct top categories view.
     *
     * @param id component id
     */
    public TopCategories(final String id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final String selectedLocale = getLocale().getLanguage();

        final long categoryId = NumberUtils.toLong(getPage().getPageParameters().get(WebParametersKeys.CATEGORY_ID).toString());
        final long contentId = NumberUtils.toLong(getPage().getPageParameters().get(WebParametersKeys.CONTENT_ID).toString());
        final long shopId = ShopCodeContext.getShopId();
        final List<Category> categories;
        final long currentCategoryId;
        final boolean viewingCategory;
        if (contentId > 0L) { // content menu
            final List<Category> contentMenu = contentServiceFacade.getCurrentContentMenu(contentId, shopId);
            if (CollectionUtils.isEmpty(contentMenu)) {
                // if this content does not have sub content, display parent's menu
                final Category content = contentServiceFacade.getContent(contentId, shopId);
                if (content != null) {
                    categories = contentServiceFacade.getCurrentContentMenu(content.getParentId(), shopId);
                } else {
                    categories = contentMenu;
                }
            } else {
                categories = contentMenu;
            }
            currentCategoryId = contentId;
            viewingCategory = false;
        } else { // sub categories or top categories
            final List<Category> categoryMenu = categoryServiceFacade.getCurrentCategoryMenu(categoryId, shopId);
            if (CollectionUtils.isEmpty(categoryMenu)) {
                // if this content does not have sub content, display parent's menu
                final Category category = categoryServiceFacade.getCategory(categoryId, shopId);
                if (category != null) {
                    categories = categoryServiceFacade.getCurrentCategoryMenu(category.getParentId(), shopId);
                } else {
                    categories = categoryMenu;
                }
            } else {
                categories = categoryMenu;
            }
            currentCategoryId = categoryId;
            viewingCategory = true;
        }


        add(
                new ListView<Category>(CATEGORY_LIST, categories) {

                    @Override
                    protected void populateItem(final ListItem<Category> categoryListItem) {

                        final Category category = categoryListItem.getModelObject();
                        final I18NModel nameModel = getI18NSupport().getFailoverModel(category.getDisplayName(), category.getName());

                        final Link link;
                        if (viewingCategory) {
                            link = getWicketSupportFacade().links().newCategoryLink(CATEGORY_NAME_LINK, category.getCategoryId());
                        } else {
                            link = getWicketSupportFacade().links().newContentLink(CATEGORY_NAME_LINK, category.getCategoryId());
                        }

                        link
                                .add(new Label(CATEGORY_NAME, nameModel.getValue(selectedLocale)).setEscapeModelStrings(false))
                                .add(new AttributeModifier("class",
                                        category.getCategoryId() == currentCategoryId ? "active-category" : ""
                                ));

                        categoryListItem.add(link);

                        categoryListItem.add(new AttributeModifier("class",
                                category.getCategoryId() == currentCategoryId ? "list-group-item active-item" : "list-group-item"
                        ));

                    }
                }

        );

        super.onBeforeRender();


    }


}

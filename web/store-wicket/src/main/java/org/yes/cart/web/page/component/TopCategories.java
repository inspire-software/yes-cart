/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.domain.entity.Content;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.i18n.I18NModel;
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

        final long categoryId = NumberUtils.toLong(getPage().getPageParameters().get(WebParametersKeys.CATEGORY_ID).toString());
        final long contentId = NumberUtils.toLong(getPage().getPageParameters().get(WebParametersKeys.CONTENT_ID).toString());
        final long contentShopId = getCurrentShopId();
        final long browsingShopId = getCurrentCustomerShopId();
        final String lang = getLocale().getLanguage();
        final List<Identifiable> categories;
        final long currentCategoryId;
        final boolean viewingCategory;
        if (contentId > 0L) { // content menu
            final List<Identifiable> contentMenu = (List) contentServiceFacade.getCurrentContentMenu(contentId, contentShopId, lang);
            if (CollectionUtils.isEmpty(contentMenu)) {
                // if this content does not have sub content, display parent's menu
                final Content content = contentServiceFacade.getContent(contentId, contentShopId);
                if (content != null) {
                    categories = (List) contentServiceFacade.getCurrentContentMenu(content.getParentId(), contentShopId, lang);
                } else {
                    categories = contentMenu;
                }
            } else {
                categories = contentMenu;
            }
            currentCategoryId = contentId;
            viewingCategory = false;
        } else { // sub categories or top categories
            final List<Identifiable> categoryMenu = (List) categoryServiceFacade.getCurrentCategoryMenu(categoryId, browsingShopId, lang);
            if (CollectionUtils.isEmpty(categoryMenu)) {
                // if this content does not have sub content, display parent's menu
                final Category category = categoryServiceFacade.getCategory(categoryId, browsingShopId);
                if (category != null) {
                    final Long parentId = categoryServiceFacade.getCategoryParentId(categoryId, browsingShopId);
                    if (parentId != null) {
                        categories = (List) categoryServiceFacade.getCurrentCategoryMenu(parentId, browsingShopId, lang);
                    } else {
                        categories = (List) categoryServiceFacade.getCurrentCategoryMenu(0L, browsingShopId, lang);
                    }
                } else {
                    categories = (List) categoryServiceFacade.getCurrentCategoryMenu(0L, browsingShopId, lang);
                }
            } else {
                categories = categoryMenu;
            }
            currentCategoryId = categoryId;
            viewingCategory = true;
        }


        add(
                new ListView<Identifiable>(CATEGORY_LIST, categories) {

                    @Override
                    protected void populateItem(final ListItem<Identifiable> categoryListItem) {

                        final String selectedLocale = getLocale().getLanguage();
                        final Identifiable category = categoryListItem.getModelObject();
                        final I18NModel displayName = category instanceof Category ? ((Category) category).getDisplayName() : ((Content) category).getDisplayName();
                        final String name = category instanceof Category ? ((Category) category).getName() : ((Content) category).getName();
                        final I18NModel nameModel = getI18NSupport().getFailoverModel(displayName, name);

                        final Link link;
                        if (viewingCategory) {
                            link = getWicketSupportFacade().links().newCategoryLink(CATEGORY_NAME_LINK, category.getId());
                        } else {
                            link = getWicketSupportFacade().links().newContentLink(CATEGORY_NAME_LINK, category.getId());
                        }

                        link
                                .add(new Label(CATEGORY_NAME, nameModel.getValue(selectedLocale)).setEscapeModelStrings(false))
                                .add(new AttributeModifier("class",
                                        category.getId() == currentCategoryId ? "active-category" : ""
                                ));

                        categoryListItem.add(link);

                        categoryListItem.add(new AttributeModifier("class",
                                category.getId() == currentCategoryId ? "list-group-item active-item" : "list-group-item"
                        ));

                    }
                }

        );

        super.onBeforeRender();


    }


}

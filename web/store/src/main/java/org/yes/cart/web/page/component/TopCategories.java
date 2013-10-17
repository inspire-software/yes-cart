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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.constants.WebParametersKeys;

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

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;

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

        final List<Category> categories = categoryService.getTopLevelCategories(ShopCodeContext.getShopId());

        final long categoryId = NumberUtils.toLong(getPage().getPageParameters().get(WebParametersKeys.CATEGORY_ID).toString());

        add(
                new ListView<Category>(CATEGORY_LIST, categories) {

                    @Override
                    protected void populateItem(final ListItem<Category> categoryListItem) {

                        final Category category = categoryListItem.getModelObject();
                        final I18NModel nameModel = getI18NSupport().getFailoverModel(category.getDisplayName(), category.getName());

                        categoryListItem.add(

                                getWicketSupportFacade().links().newCategoryLink(CATEGORY_NAME_LINK, category.getCategoryId())
                                .add(

                                        new Label(CATEGORY_NAME, nameModel.getValue(selectedLocale)).setEscapeModelStrings(false)

                                ).add(

                                        new AttributeModifier(
                                                "class",
                                                /*categoryService.isCategoryHasSubcategory(category.getCategoryId(), categoryId)*/ category.getCategoryId() == categoryId ?
                                                "active-category" : ""
                                        )

                                )

                        );

                    }
                }

        );

        super.onBeforeRender();


    }


}

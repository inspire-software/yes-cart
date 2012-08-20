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

import org.apache.lucene.search.BooleanQuery;
import org.apache.wicket.markup.html.basic.Label;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;
import org.yes.cart.web.util.WicketUtil;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:04 AM
 */
public class EmptyCentralView extends AbstractCentralView {

    private static final String DESCRIPTION = "description";

    private CategoryDecorator category;

    /**
      * Construct panel.
     *
     * @param id panel id
     * @param categoryId  current category id.
     * @param booleanQuery     boolean query.
     */
    public EmptyCentralView(String id, long categoryId, BooleanQuery booleanQuery) {
        super(id, categoryId, booleanQuery);


    }

    private void configureContext() {

        String catId = getPage().getPageParameters().get(WebParametersKeys.CATEGORY_ID).toString();
        if (catId != null) {
            Category category = getCategory();
            if (category != null) {
                this.category = getDecoratorFacade().decorate(
                        category,
                        WicketUtil.getHttpServletRequest().getContextPath(),
                        getI18NSupport());
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        configureContext();

        String description = null;
        if (category != null) {
            description = category.getDescription(getLocale().getLanguage());
        }

        add(new Label(DESCRIPTION, description != null ? description : ""));

        super.onBeforeRender();

    }



}

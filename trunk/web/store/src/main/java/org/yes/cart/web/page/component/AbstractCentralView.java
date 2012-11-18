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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.service.domain.CategoryService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/12/11
 * Time: 10:11 PM
 */
public abstract class AbstractCentralView extends BaseComponent {

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;

    private final long categoryId;

    private final BooleanQuery booleanQuery;

    private Category category;



    /**
      * Construct panel.
     *
     * @param id panel id
     * @param categoryId  current category id.
     * @param booleanQuery     boolean query.
     */
    public AbstractCentralView(final String id, final long categoryId, final BooleanQuery booleanQuery) {
        super(id);
        this.categoryId = categoryId;
        this.booleanQuery = booleanQuery;
    }

    /**
     * Get category id.
     * @return current category id.
     */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * Get current query.
     * @return  query.
     */
    public BooleanQuery getBooleanQuery() {
        return booleanQuery;
    }

    /**
     * Get category.
     * @return {@link Category}
     */
    public Category getCategory() {
        if (category == null) {
            category = categoryService.getById(categoryId);
        }
        return category;
    }

    /**
     * Get category service
     * @return    category id.
     */
    public CategoryService getCategoryService() {
        return categoryService;
    }


    /**
     * Get page title.
     * @return page title
     */
    public IModel<String> getPageTitle() {
        if (getCategory() != null) {
            Seo seo = getCategory().getSeo();
            if (seo != null) {
                return new Model<String>(seo.getTitle());
            }
        }
        return null;
    }

    /**
     * Get opage description
     * @return description
     */
    public IModel<String> getDescription() {
        if (getCategory() != null) {
            Seo seo = getCategory().getSeo();
            if (seo != null) {
                return new Model<String>(seo.getMetadescription());
            }
        }
        return null;

    }

    /**
     * Get keywords.
     * @return keywords
     */
    public IModel<String> getKeywords() {
        if (getCategory() != null) {
            Seo seo = getCategory().getSeo();
            if (seo != null) {
                return new Model<String>(seo.getMetakeywords());
            }
        }
        return null;

    }


}

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

package org.yes.cart.web.page.component.data;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;

import java.util.Iterator;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:51 AM
 */
public class SortableCategoryDataProvider extends SortableDataProvider<CategoryDecorator> {


    private final List<CategoryDecorator> categories;

    /**
     * Construct sortable category list provider.
     *
     * @param categories given categories
     */
    public SortableCategoryDataProvider(final List<CategoryDecorator> categories) {
        this.categories = categories;
    }

    /**
     * {@inheritDoc
     */
    public Iterator<? extends CategoryDecorator> iterator(final int i, final int i1) {
        return categories.iterator();
    }

    /**
     * {@inheritDoc
     */
    public int size() {
        return categories.size();
    }

    /**
     * {@inheritDoc
     */
    public IModel<CategoryDecorator> model(final CategoryDecorator category) {
        IModel<CategoryDecorator> model = new IModel<CategoryDecorator>() {

            private CategoryDecorator category;

            public CategoryDecorator getObject() {
                return category;
            }

            public void setObject(final CategoryDecorator product) {
                this.category = product;
            }

            public void detach() {
                //Nothing to do
            }
        };

        model.setObject(category);

        return model;
    }

}

package org.yes.cart.web.page.component.data;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.yes.cart.domain.entity.Category;
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

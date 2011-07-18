package org.yes.cart.web.page.component;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.search.BooleanQuery;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.web.page.component.data.SortableCategoryDataProvider;
import org.yes.cart.web.support.constants.WebServiceSpringKey;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;
import org.yes.cart.web.support.entity.decorator.impl.CategoryDecoratorImpl;
import org.yes.cart.web.support.service.CategoryImageService;
import org.yes.cart.web.util.WicketUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:32 AM
 */
public class SubCategoriesCentralView extends AbstractCentralView {

    @SpringBean(name = WebServiceSpringKey.CATEGORY_IMAGE_SERVICE)
    protected CategoryImageService categoryImageService;

    /**
     * Default quantity of columns to show subcategorues in list
     */
    private static final String DEFAULT_SUBCATEGORIES_COLUMNS = "2";

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    /**
     * Name of category list view.
     */
    private static final String CATEGORY_LIST = "rows";
    /**
     * Name of category list view.
     */
    private static final String CATEGORY_VIEW = "categoryView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    /**
     * Construct panel.
     *
     * @param id           panel id
     * @param categoryId   current category id.
     * @param booleanQuery boolean query.
     */
    public SubCategoriesCentralView(String id, long categoryId, BooleanQuery booleanQuery) {
        super(id, categoryId, booleanQuery);
    }

    private List<CategoryDecorator> decorate(final List<Category> categories) {
        final List<CategoryDecorator> rez = new ArrayList<CategoryDecorator>();
        for (Category cat : categories) {
            rez.add(
                    new CategoryDecoratorImpl(
                            categoryImageService,
                            getCategoryService(),
                            cat,
                            WicketUtil.getHttpServletRequest().getContextPath()
                    )
            );
        }
        return rez;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final List<CategoryDecorator> categories = decorate(getCategoryService().getChildCategories(getCategoryId()));

        final SortableDataProvider<CategoryDecorator> dataProvider = new SortableCategoryDataProvider(categories);

        final String val = getCategoryService().getCategoryAttributeRecursive(getCategory(),
                AttributeNamesKeys.Category.CATEGORY_SUBCATEGORIES_COLUMNS,
                DEFAULT_SUBCATEGORIES_COLUMNS);

        int subCatsQty = NumberUtils.createInteger(val);

        add(
                new GridView<CategoryDecorator>(CATEGORY_LIST, dataProvider) {

                    /** {@inheritDoc} */
                    protected void populateItem(Item<CategoryDecorator> categoryItem) {

                        categoryItem.add(

                                new CategoryView(CATEGORY_VIEW)

                        );

                    }

                    protected void populateEmptyItem(Item<CategoryDecorator> categoryItem) {

                        categoryItem.add(

                                new Label(CATEGORY_VIEW, StringUtils.EMPTY).setVisible(false)

                        );

                    }

                }
                        .setColumns(subCatsQty)
                        .setRows(1 + categories.size() / subCatsQty)
        );

        super.onBeforeRender();
    }
}

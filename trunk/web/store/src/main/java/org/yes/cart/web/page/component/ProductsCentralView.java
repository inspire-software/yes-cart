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
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.search.BooleanQuery;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.page.component.data.SortableProductDataProvider;
import org.yes.cart.web.page.component.navigation.ProductPerPageListView;
import org.yes.cart.web.page.component.navigation.ProductSorter;
import org.yes.cart.web.page.component.navigation.URLPagingNavigator;
import org.yes.cart.web.page.component.product.ProductInListView;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 10:29 PM
 */
public class ProductsCentralView extends AbstractCentralView {

    final static String[] defaultSize =
            new String[]{
                    AttributeNamesKeys.Category.PRODUCT_IMAGE_WIDTH,
                    AttributeNamesKeys.Category.PRODUCT_IMAGE_HEIGHT
            };


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    /**
     * Named place for  per page links
     */
    private static final String ITEMS_PER_PAGE_LIST = "itemsPerPageList";
    /**
     * Name of product view.
     */
    private static final String PAGINATOR = "paginator";
    /**
     * Name of product view.
     */
    private static final String SORTER = "sorter";
    /**
     * Name of product view.
     */
    private static final String PRODUCT = "product";
    /**
     * Name of products list view.
     */
    private static final String PRODUCT_LIST = "rows";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    protected CategoryService categoryService;

    /**
     * Construct panel.
     *
     * @param id           panel id
     * @param categoryId   current category id.
     * @param booleanQuery boolean query.
     */
    public ProductsCentralView(final String id, long categoryId, final BooleanQuery booleanQuery) {
        super(id, categoryId, booleanQuery);

    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final List<String> itemsPerPageValues =
                getCategoryService().getItemsPerPage(getCategory());


        final String[] widthHeight = getCategoryService().getCategoryAttributeRecursive(
                null, getCategory(),
                defaultSize
        );

        final int selectedItemPerPage = WicketUtil.getSelectedItemsPerPage(
                getPage().getPageParameters(), itemsPerPageValues);

        final SortableProductDataProvider dataProvider = new  SortableProductDataProvider(
                productService,
                getBooleanQuery(),
                getI18NSupport(),
                getDecoratorFacade()
        );

        applySortFieldAndOrder(dataProvider);

        final GridView<ProductSearchResultDTO> productDataView = new GridView<ProductSearchResultDTO>(PRODUCT_LIST, dataProvider) {

            protected void populateItem(Item<ProductSearchResultDTO> productItem) {
                productItem.add(
                        new ProductInListView(PRODUCT, productItem.getModelObject(), getCategory(), widthHeight)
                );
            }

            protected void populateEmptyItem(Item<ProductSearchResultDTO> productItem) {
                productItem.add(
                        new Label(PRODUCT, StringUtils.EMPTY).setVisible(false)
                );

            }

        };

        final int columns = NumberUtils.toInt(
                getCategoryService().getCategoryAttributeRecursive(
                        null, getCategory(),
                        AttributeNamesKeys.Category.CATEGORY_PRODUCTS_COLUMNS,
                        "2")
        );

        productDataView.setColumns(columns);
        productDataView.setRows(selectedItemPerPage / columns);

        int currentPageIdx = getWicketSupportFacade().pagination().getCurrentPage(getPage().getPageParameters());
        if (currentPageIdx < productDataView.getPageCount()) {
            productDataView.setCurrentPage(currentPageIdx);
        } else {
            productDataView.setCurrentPage(0);
        }

        add(new ProductSorter(SORTER, "sort-order-active"));
        add(new URLPagingNavigator(PAGINATOR, productDataView, getPage().getPageParameters(), "page-active"));
        add(new ProductPerPageListView(ITEMS_PER_PAGE_LIST, itemsPerPageValues, getPage().getPageParameters()));
        add(productDataView);

        super.onBeforeRender();
    }


    private void applySortFieldAndOrder(SortableProductDataProvider dataProvider) {
        final Pair<String, Boolean> sortResult = getSortField();

        if (sortResult != null) {
            dataProvider.setSortFieldName(sortResult.getFirst());
            dataProvider.setReverse(sortResult.getSecond());
        }
    }

    /**
     * Get the sort field and sort direction.
     *
     * @return {@link Pair} of sort filed and sort direction (true - asc order)
     */
    private Pair<String, Boolean> getSortField() {
        String sort = getPage().getPageParameters().get(WebParametersKeys.SORT).toString();
        if (sort != null) {
            return new Pair<String, Boolean>(sort, false);
        }
        sort = getPage().getPageParameters().get(WebParametersKeys.SORT_REVERSE).toString();
        if (sort != null) {
            return new Pair<String, Boolean>(sort, true);
        }
        return null;
    }


}

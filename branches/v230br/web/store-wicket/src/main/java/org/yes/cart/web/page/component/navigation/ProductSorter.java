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

package org.yes.cart.web.page.component.navigation;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.service.wicketsupport.PaginationSupport;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.util.ProductSortingUtils;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/14/11
 * Time: 8:55 PM
 */
public class ProductSorter extends BaseComponent {

    /**
     * Product sorting
     */
    private final static String PAGE_SORT = "pageSort";
    private final static String SORT_LABEL = "sortLabel";
    private final static String SORT_A = "sortA";
    private final static String SORT_D = "sortD";

    /**
     * Construct product sorter.
     * @param id component id.
     * @param items sort options.
     */
    public ProductSorter(final String id,
                         final List<String> items) {
        super(id);

        add(new ListView<String>(PAGE_SORT, items) {
            /**
             * {@inheritDoc}
             */
            @Override
            protected void populateItem(final ListItem<String> stringSortOption) {

                final ShoppingCart cart = ApplicationDirector.getShoppingCart();

                ProductSortingUtils.SupportedSorting sort = ProductSortingUtils.getConfiguration(stringSortOption.getModelObject());
                final boolean supported = sort != null;
                if (!supported) {
                    sort = ProductSortingUtils.NULL_SORT;
                }

                stringSortOption.setVisible(supported);

                final String labelKey = sort.resolveLabelKey(cart.getShoppingContext().getShopId(), cart.getCurrentLocale(), cart.getCurrencyCode());
                stringSortOption.add(new Label(SORT_LABEL, new StringResourceModel(labelKey, this, null)));

                final String sortField = sort.resolveSortField(cart.getShoppingContext().getShopId(), cart.getCurrentLocale(), cart.getCurrencyCode());

                stringSortOption.add(getSortLink(SORT_A, WebParametersKeys.SORT, sortField));
                stringSortOption.add(getSortLink(SORT_D, WebParametersKeys.SORT_REVERSE, sortField));

            }
        });

    }


    /**
     * Get the product sort link by given sort filed and order.
     *
     * @param sortOrder sort order see {@link org.yes.cart.web.support.constants.WebParametersKeys#SORT}
     *                  and {@link org.yes.cart.web.support.constants.WebParametersKeys#SORT_REVERSE}
     * @param sortField sort by filed see {@link org.yes.cart.domain.query.ProductSearchQueryBuilder}
     * @param id        link id
     * @return product sort link
     */
    private Link getSortLink(final String id, final String sortOrder, final String sortField) {

        final AbstractWebPage page = ((AbstractWebPage) getPage());
        final LinksSupport links = page.getWicketSupportFacade().links();
        final PaginationSupport pagination = page.getWicketSupportFacade().pagination();

        final PageParameters pageParameters = page.getPageParameters();
        final PageParameters params = links.getFilteredCurrentParameters(pageParameters);
        params.remove(WebParametersKeys.SORT);
        params.remove(WebParametersKeys.SORT_REVERSE);
        params.set(WebParametersKeys.PAGE, "0");
        params.add(sortOrder, sortField);

        final Link rez = links.newLink(id, params);
        pagination.markSelectedSortLink(rez, pageParameters, sortOrder, sortField);
        return rez;
    }
}

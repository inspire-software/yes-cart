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

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.service.wicketsupport.PaginationSupport;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/14/11
 * Time: 8:55 PM
 */
public class ProductSorter extends BaseComponent {

    /**
     * Product sorting
     */
    private static final String PRODUCT_SORT_BY_NAME_ASC = "orderByNameA";
    private static final String PRODUCT_SORT_BY_NAME_DESC = "orderByNameD";
    private static final String PRODUCT_SORT_BY_PRICE_ASC = "orderByPriceA";
    private static final String PRODUCT_SORT_BY_PRICE_DESC = "orderByPriceD";

    private final String sortOrderActiveClass;

    /**
     * Construct product sorter.
     * @param id component id.
     * @param sortOrderActiveLinkHtmlClass html class for active links
     */
    public ProductSorter(final String id, final String sortOrderActiveLinkHtmlClass) {
        super(id);
        sortOrderActiveClass = sortOrderActiveLinkHtmlClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        add(getSortLink(PRODUCT_SORT_BY_NAME_ASC, WebParametersKeys.SORT, ProductSearchQueryBuilder.PRODUCT_NAME_SORT_FIELD));
        add(getSortLink(PRODUCT_SORT_BY_NAME_DESC, WebParametersKeys.SORT_REVERSE, ProductSearchQueryBuilder.PRODUCT_NAME_SORT_FIELD));
        add(getSortLink(PRODUCT_SORT_BY_PRICE_ASC, WebParametersKeys.SORT, ProductSearchQueryBuilder.PRODUCT_PRICE_AMOUNT));
        add(getSortLink(PRODUCT_SORT_BY_PRICE_DESC, WebParametersKeys.SORT_REVERSE, ProductSearchQueryBuilder.PRODUCT_PRICE_AMOUNT));


        super.onBeforeRender();
    }

    /**
     * Get the product sort link by given sort filed and order.
     *
     * @param sortOrder sort order see {@link org.yes.cart.web.support.constants.WebParametersKeys#SORT}
     *                  and {@link org.yes.cart.web.support.constants.WebParametersKeys#SORT_REVERSE}
     * @param sortField sort by filed see {@link org.yes.cart.domain.query.ProductSearchQueryBuilder#PRODUCT_NAME_FIELD} and
     *                  {@link org.yes.cart.domain.query.ProductSearchQueryBuilder#PRODUCT_PRICE_AMOUNT}
     * @param id        link id
     * @return product sort link
     */
    private Link getSortLink(final String id, final String sortOrder, final String sortField) {

        final LinksSupport links = getWicketSupportFacade().links();
        final PaginationSupport pagination = getWicketSupportFacade().pagination();

        final PageParameters params = links.getFilteredCurrentParameters(getPage().getPageParameters());
        params.remove(WebParametersKeys.SORT);
        params.remove(WebParametersKeys.SORT_REVERSE);
        params.set(WebParametersKeys.PAGE, "0");
        params.add(sortOrder, sortField);

        final Link rez = links.newLink(id, params);
        pagination.markSelectedSortLink(rez, sortOrderActiveClass, getPage().getPageParameters(), sortOrder, sortField);
        return rez;
    }
}

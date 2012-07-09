package org.yes.cart.web.page.component.navigation;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.page.component.BaseComponent;
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

    /**
     * Construct product sorter.
     * @param id component id.
     */
    public ProductSorter(final String id) {
        super(id);
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
        final PageParameters params = WicketUtil.getFilteredRequestParameters(getPage().getPageParameters());
        params.remove(WebParametersKeys.SORT);
        params.remove(WebParametersKeys.SORT_REVERSE);
        params.set(WebParametersKeys.PAGE, "0");
        params.add(sortOrder, sortField);
        return new BookmarkablePageLink<Link>(id, HomePage.class, params);
    }
}

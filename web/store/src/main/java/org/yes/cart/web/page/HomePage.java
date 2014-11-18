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

package org.yes.cart.web.page;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.search.Query;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.AbstractCentralView;
import org.yes.cart.web.page.component.TopCategories;
import org.yes.cart.web.page.component.breadcrumbs.BreadCrumbsView;
import org.yes.cart.web.page.component.filterednavigation.AttributeProductFilter;
import org.yes.cart.web.page.component.filterednavigation.BrandProductFilter;
import org.yes.cart.web.page.component.filterednavigation.PriceProductFilter;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.page.component.js.ServerSideJs;
import org.yes.cart.web.page.component.product.FeaturedProducts;
import org.yes.cart.web.page.component.product.NewArrivalProducts;
import org.yes.cart.web.page.component.product.RecentlyViewedProducts;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.constants.WicketServiceSpringKeys;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.util.HttpUtil;
import org.yes.cart.web.theme.WicketCentralViewProvider;
import org.yes.cart.web.util.WicketUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 10:27 AM
 */
public class HomePage extends AbstractWebPage {


    @SpringBean(name = StorefrontServiceSpringKeys.CENTRAL_VIEW_RESOLVER)
    private CentralViewResolver centralViewResolver;

    @SpringBean(name = WicketServiceSpringKeys.WICKET_CENTRAL_VIEW_PROVIDER)
    private WicketCentralViewProvider wicketCentralViewProvider;

    @SpringBean(name = ServiceSpringKeys.LUCENE_QUERY_FACTORY)
    private LuceneQueryFactory luceneQueryFactory;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.SHOP_SERVICE)
    private ShopService shopService;

    private AbstractCentralView centralPanel;

    /**
     * Construct home page.
     *
     * @param params page parameters
     */
    public HomePage(final PageParameters params) {
        super(params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        executeHttpPostedCommands();

        final Map<String, List<String>> mapParams = WicketUtil.pageParametesAsMultiMap(getPageParameters());

        final String centralViewLabel = centralViewResolver.resolveMainPanelRendererLabel(mapParams);

        final long categoryId;
        if (mapParams.containsKey(WebParametersKeys.CATEGORY_ID)) {
            categoryId = NumberUtils.toLong(HttpUtil.getSingleValue(mapParams.get(WebParametersKeys.CATEGORY_ID)));
        } else if (mapParams.containsKey(WebParametersKeys.CONTENT_ID)) {
            categoryId = NumberUtils.toLong(HttpUtil.getSingleValue(mapParams.get(WebParametersKeys.CONTENT_ID)));
        } else {
            categoryId = 0l;
        }

        final Shop shop = ApplicationDirector.getCurrentShop();


        final boolean categoryProductsRecursive = categoryService.isSearchInSubcategory(categoryId, shop.getShopId());
        final List<Long> currentCategoriesIds = getCategories(categoryId, categoryProductsRecursive);


        final Query query = luceneQueryFactory.getFilteredNavigationQueryChain(
                shop.getShopId(),
                currentCategoriesIds,
                (Map) mapParams
        );


        add(new TopCategories("topCategories"));

        if (CentralViewLabel.PRODUCT.equals(centralViewLabel) || CentralViewLabel.SKU.equals(centralViewLabel)) {
            add(new Label("brandFilter"));
            add(new Label("attributeFilter"));
            add(new Label("priceFilter"));
        } else {
            add(new BrandProductFilter("brandFilter", query, categoryId));
            add(new AttributeProductFilter("attributeFilter", query, categoryId));
            add(new PriceProductFilter("priceFilter", query, categoryId));
        }

        add(new BreadCrumbsView("breadCrumbs", categoryId, shopService.getShopAllCategoriesIds(shop.getShopId())));


        add(new RecentlyViewedProducts("recentlyViewed"));
        add(new NewArrivalProducts("newArrival"));

        //add(new Carousel("featured"));

        centralPanel = getCentralPanel(centralViewLabel, "centralView", categoryId, query);

        add(centralPanel);

        add(
                new StandardHeader(HEADER)
        );

        add(
                new StandardFooter(FOOTER)
        );

        add(
                new ServerSideJs("serverSideJs")
        );

        addOrReplace(
                new FeaturedProducts("featured")
        );




        super.onBeforeRender();

        persistCartIfNecessary();
    }

    /**
     * Get product id or product sku id.
     *
     * @param mapParams page parameters
     *
     * @return product id or product sku id.
     */
    private String getItemId(final Map<String, List<String>> mapParams) {
        // Sku has priority over product
        String itemId = HttpUtil.getSingleValue(mapParams.get(WebParametersKeys.SKU_ID));
        if (itemId == null) {
            itemId = HttpUtil.getSingleValue(mapParams.get(WebParametersKeys.PRODUCT_ID));
        }
        return itemId;
    }


    /**
     * Get category sub tree as list, that starts from given category id.
     *
     *
     * @param categoryId root of tree.
     * @param includeSubCategories allow subcategories
     * @return list of categories, that belong to sub tree.
     */
    private List<Long> getCategories(final Long categoryId, final boolean includeSubCategories) {
        if (categoryId != null && categoryId > 0) {
            if (includeSubCategories) {
                // this will include the categoryId as well
                return new ArrayList<Long>(categoryService.getChildCategoriesRecursiveIds(categoryId));
            }
            return Collections.singletonList(categoryId);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Get the main panel renderer by given renderer label.
     *
     * @param rendererLabel renderer label
     * @param id            component id
     * @param categoryId    category id
     * @param booleanQuery  optional lucene query
     * @return concrete instance of {@link AbstractCentralView} if renderer found, otherwise
     *         instance of EmptyCentralView
     */
    private AbstractCentralView getCentralPanel(
            final String rendererLabel,
            final String id,
            final long categoryId,
            final Query booleanQuery) {

        return wicketCentralViewProvider.getCentralPanel(rendererLabel, id, categoryId, booleanQuery);

    }

    /**
     * Get page title.
     *
     * @return page title
     */
    public IModel<String> getPageTitle() {
        if (centralPanel != null) {
            final IModel<String> rez = centralPanel.getPageTitle();
            if (rez == null) {
                return super.getPageTitle();
            }
            return rez;
        }
        return super.getPageTitle();
    }


    /**
     * Get opage description
     *
     * @return description
     */
    public IModel<String> getDescription() {
        if (centralPanel != null) {
            final IModel<String> rez = centralPanel.getDescription();
            if (rez == null) {
                return super.getDescription();
            }
            return rez;
        }
        return super.getDescription();
    }

    /**
     * Get keywords.
     *
     * @return keywords
     */
    public IModel<String> getKeywords() {
        if (centralPanel != null) {
            final IModel<String> rez = centralPanel.getKeywords();
            if (rez == null) {
                return super.getKeywords();
            }
            return rez;
        }
        return super.getKeywords();
    }


}

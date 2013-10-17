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
import org.apache.lucene.search.BooleanQuery;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.*;
import org.yes.cart.web.page.component.breadcrumbs.BreadCrumbsView;
import org.yes.cart.web.page.component.filterednavigation.AttributeProductFilter;
import org.yes.cart.web.page.component.filterednavigation.BrandProductFilter;
import org.yes.cart.web.page.component.filterednavigation.PriceProductFilter;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.page.component.product.NewArrivalProducts;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.util.HttpUtil;
import org.yes.cart.web.util.WicketUtil;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 10:27 AM
 */
public class HomePage extends AbstractWebPage {



    final Map<String, String> mapParams;


    @SpringBean(name = StorefrontServiceSpringKeys.CENTRAL_VIEW_RESOLVER)
    private CentralViewResolver centralViewResolver;

    @SpringBean(name = ServiceSpringKeys.LUCENE_QUERY_FACTORY)
    private LuceneQueryFactory luceneQueryFactory;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.SHOP_SERVICE)
    private ShopService shopService;

    private AbstractCentralView centralPanel;

    /**
     * to show all products in subcategories rather than category view
     * change this to true.
     */
    private final boolean categoryProductsRecursive = false;

    /**
     * Construct home page.
     *
     * @param params page parameters
     */
    public HomePage(final PageParameters params) {

        super(params);

        mapParams = WicketUtil.pageParametesAsMap(params);
        mapParams.put(WebParametersKeys.CATEGORY_PRODUCTS_RECURSIVE, String.valueOf(categoryProductsRecursive));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        processCommands();

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

        final List<Long> shopSubCategoriesIds = getCategories(categoryId);


        final List<BooleanQuery> queriesChain = luceneQueryFactory.getFilteredNavigationQueryChain(
                shop.getShopId(),
                shopSubCategoriesIds,
                mapParams
        );

        final BooleanQuery query = centralViewResolver.getBooleanQuery(
                queriesChain,
                null,
                categoryId,
                categoryProductsRecursive ? shopSubCategoriesIds : null,
                centralViewLabel,
                getItemId()
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

        final List<Long> shopAllCategoriesIds = new ArrayList<Long>(shopService.getShopCategoriesIds(shop.getShopId()));
        add(new BreadCrumbsView("breadCrumbs", categoryId, shopAllCategoriesIds));


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


        super.onBeforeRender();


    }

    /**
     * Get product id or product sku id.
     *
     * @return product id or product sku id.
     */
    private String getItemId() {
        String itemId = HttpUtil.getSingleValue(mapParams.get(WebParametersKeys.PRODUCT_ID));
        if (itemId == null) {
            itemId = HttpUtil.getSingleValue(mapParams.get(WebParametersKeys.SKU_ID));
        }
        return itemId;
    }


    /**
     * Get category sub tree as list, that starts from given category id.
     *
     * @param categoryId root of tree.
     * @return list of categories, that belong to sub tree.
     */
    private List<Long> getCategories(final Long categoryId) {
        if (categoryId > 0) {
            return new ArrayList<Long>(categoryService.getChildCategoriesRecursiveIds(categoryId));
        } else {
            return Arrays.asList(0L);
        }
    }

    /**
     * Registered main panel renderers
     */
    private static final Map<String, Class<? extends AbstractCentralView>> rendererPanelMap =
            new HashMap<String, Class<? extends AbstractCentralView>>() {{
                put(CentralViewLabel.SUBCATEGORIES_LIST, SubCategoriesCentralView.class);
                put(CentralViewLabel.PRODUCTS_LIST, ProductsCentralView.class);
                put(CentralViewLabel.PRODUCT, SkuCentralView.class);
                put(CentralViewLabel.SKU, SkuCentralView.class);
                put(CentralViewLabel.SEARCH_LIST, ProductsCentralView.class);
                put(CentralViewLabel.CONTENT, ContentCentralView.class);
                put(CentralViewLabel.CATEGORY, EmptyCentralView.class);
                put(CentralViewLabel.DEFAULT, EmptyCentralView.class);
            }};

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
            final Long categoryId,
            final BooleanQuery booleanQuery) {

        Class<? extends AbstractCentralView> clz = rendererPanelMap.get(rendererLabel);
        try {
            if (categoryId != 0 && clz != ContentCentralView.class) { //check is this category allowed to open in this shop

                final Shop shop = ApplicationDirector.getCurrentShop();
                if (!shopService.getShopCategoriesIds(shop.getShopId()).contains(categoryId)) {
                    final Logger log = ShopCodeContext.getLog(this);
                    if (log.isWarnEnabled()) {
                        log.warn("Can not access to category  {} from shop {}", categoryId, shop.getShopId());
                    }
                    return new EmptyCentralView(id, booleanQuery);
                }
            }

            Constructor<? extends AbstractCentralView> constructor = clz.getConstructor(String.class,
                    long.class,
                    BooleanQuery.class);
            return constructor.newInstance(id, categoryId, booleanQuery);

        } catch (Exception e) {
            ShopCodeContext.getLog(this).error(MessageFormat.format("Can not create instance of panel for label {0}", rendererLabel), e);
            //e.printStackTrace();
            return new EmptyCentralView(id, booleanQuery);

        }

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

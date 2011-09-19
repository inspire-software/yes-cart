package org.yes.cart.web.page;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.search.BooleanQuery;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.*;
import org.yes.cart.web.page.component.breadcrumbs.BreadCrumbsView;
import org.yes.cart.web.page.component.filterednavigation.AttributeProductFilter;
import org.yes.cart.web.page.component.filterednavigation.BrandProductFilter;
import org.yes.cart.web.page.component.filterednavigation.PriceProductFilter;
import org.yes.cart.web.page.component.product.NewArrivalProducts;
import org.yes.cart.web.page.component.search.SearchView;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.constants.WebServiceSpringKey;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.util.HttpUtil;
import org.yes.cart.web.util.WicketUtil;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 10:27 AM
 */
public class HomePage extends AbstractWebPage {


    final Map<String, Object> mapParams;


    @SpringBean(name = WebServiceSpringKey.CENTRAL_VIEW_RESOLVER)
    private CentralViewResolver centralViewResolver;

    @SpringBean(name = ServiceSpringKeys.LUCENE_QUERY_FACTORY)
    private LuceneQueryFactory luceneQueryFactory;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.SHOP_SERVICE)
    private ShopService shopService;

    /**
     * Construct home page.
     *
     * @param params page parameters
     */
    public HomePage(final PageParameters params) {

        super(params);

        mapParams = WicketUtil.pageParametesAsMap(params);

    }

    /** {@inheritDoc} */
    @Override
    protected void onBeforeRender() {

        final String centralViewLabel = centralViewResolver.resolveMainPanelRendererLabel(mapParams);

        final long categoryId = NumberUtils.toLong(HttpUtil.getSingleValue(mapParams.get(WebParametersKeys.CATEGORY_ID)));

        final Shop shop =  ApplicationDirector.getCurrentShop();

        final List<Long> shopCategories = getCategories(categoryId);

        final List<BooleanQuery> queriesChain = luceneQueryFactory.getFilteredNavigationQueryChain(
                shop.getShopId(),
                shopCategories,
                mapParams,
                categoryService.transform(shopService.getShopCategories(shop))
        );

        final BooleanQuery query = centralViewResolver.getBooleanQuery(
                queriesChain,
                null,
                categoryId,
                centralViewLabel,
                getItemId()
        );



        add(new Label("version",centralViewLabel));
        add(new Currency("currency"));
        add(new Language("language"));
        add(new TopCategories("topCategories"));
        add(new BrandProductFilter("brandFilter", query, categoryId));
        add(new AttributeProductFilter("attributeFilter", query, categoryId));
        add(new PriceProductFilter("priceFilter", query, categoryId));
        add(new BreadCrumbsView("breadCrumbs", categoryId, shopCategories));
        add(new SearchView("search"));
        add(new NewArrivalProducts("newArrival"));

        add(getCentralPanel(centralViewLabel, "centralView", categoryId, query));


        super.onBeforeRender();


    }

    /**
     * Get product id or product sku id.
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
            return categoryService.transform(
                    categoryService.getChildCategoriesRecursive(categoryId));
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

        Class<? extends AbstractCentralView>  clz = rendererPanelMap.get(rendererLabel);
        try {
            Constructor<? extends AbstractCentralView> constructor = clz.getConstructor(String.class,
                    long.class,
                    BooleanQuery.class);
            return constructor.newInstance(id, categoryId, booleanQuery);
        } catch (Exception e) {
            /*if (LOG.isErrorEnabled()) {
                LOG.error(MessageFormat.format("Can not create instance of panel for label {0}", rendererLabel), e);
            }*/
            return new EmptyCentralView(id, categoryId, booleanQuery);

        }

    }

}

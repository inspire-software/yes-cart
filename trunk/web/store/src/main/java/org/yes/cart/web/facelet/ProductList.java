package org.yes.cart.web.facelet;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.search.BooleanQuery;
import org.primefaces.model.LazyDataModel;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.constants.ManagedBeanELNames;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.ShopUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/6/11
 * Time: 9:25 AM
 */
@ManagedBean(name = WebParametersKeys.REQUEST_PRODUCT_LIST)
@RequestScoped
public class ProductList extends BaseFacelet {

    @ManagedProperty(ManagedBeanELNames.EL_VIEW_PARAMETERS)
    private transient RequestParametersHolder requestParametersHolder;

    private LazyDataModel<Product> productList;

    //private String rowsPerPageTemplate = "4,8,16"; //todo CATEGORY_ITEMS_PER_PAGE
    //rowsPerPageTemplate="#{empty subCategories.rowsPerPageTemplate ? '9,12,15' : subCategories.rowsPerPageTemplate}"
    //paginatorTemplate="{CurrentPageReport}  {FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}"



    @PostConstruct
    public void init() {
        initProductList();
    }

    /**
     * Get the products list.
     *
     * @return products list.
     */
    public LazyDataModel<Product> getProductList() {
        return productList;
    }

    private void initProductList() {

        final Shop shop = ShopUtil.getShop();

        final Long categoryId = getRequestParametersHolder().getCategoryId();

        final List<BooleanQuery> queryList = getLuceneQueryFactory().getFilteredNavigationQueryChain(
                shop.getShopId(),
                getCategories(categoryId),
                FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap(),
                getCategoryService().transform(getShopService().getShopCategories(shop))
        );

        final BooleanQuery booleanQuery = getCentralViewResolver().getBooleanQuery(
                queryList,
                null,
                categoryId,
                getRequestParametersHolder().getViewLabel(),
                null);

        productList = new LazyDataModel<Product>() {

            @Override
            public List<Product> load(final int first,
                                      final int pageSize,
                                      final String sortField,
                                      final boolean sortOrder,
                                      final Map<String, String> filters) {

                return getProductService().getProductByQuery(
                        booleanQuery,
                        first,
                        first + pageSize,
                        sortField, //TODO convert filed to lucene field,
                        false);
            }

        };

        productList.setPageSize(10); //TODO primefaces bug

        productList.setRowCount(getProductService().getProductQty(booleanQuery));
    }

    /**
     * Get category sub tree as list, that starts from given category id.
     *
     * @param categoryId root of tree.
     * @return list of categories, that belong to sub tree.
     */
    private List<Long> getCategories(final Long categoryId) {
        if (categoryId > 0) {
            return getCategoryService().transform(
                    getCategoryService().getChildCategoriesRecursive(categoryId));
        } else {
            return Arrays.asList(0L);
        }
    }

    /**
     * Get view parameters;
     * @return  view parameters.
     */
    public RequestParametersHolder getRequestParametersHolder() {
        if (requestParametersHolder == null) {
            final FacesContext facesContext = FacesContext.getCurrentInstance();
            final Application application = facesContext.getApplication();
            requestParametersHolder = (RequestParametersHolder) application.getVariableResolver().resolveVariable(
                    facesContext, WebParametersKeys.VIEW_PARAMETERS);
        }
        return requestParametersHolder;
    }

    /**
     * Set request parameter holder.
     * @param requestParametersHolder  request parameter holder.
     */
    public void setRequestParametersHolder(final RequestParametersHolder requestParametersHolder) {
        this.requestParametersHolder = requestParametersHolder;
    }
}

package org.yes.cart.web.page.component.data;

import org.apache.lucene.search.Query;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.support.entity.decorator.ProductDecorator;
import org.yes.cart.web.support.entity.decorator.impl.ProductDecoratorImpl;
import org.yes.cart.web.support.service.AttributableImageService;
import org.yes.cart.web.util.WicketUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:38 PM
 */


public class SortableProductDataProvider extends SortableDataProvider<ProductDecorator> {

    private final ProductService productService;
    private final AttributableImageService attributableImageService;
    private final CategoryService categoryService;
    private final ImageService imageService;
    private final Query query;
    private String sortFieldName = ProductSearchQueryBuilder.PRODUCT_CATEGORY_RANK_FIELD;
    private boolean reverse = false;
    private List<ProductDecorator> products;

    /**
     * Construct product data provider.
     *
     * @param productService product service to get the products.
     * @param attributableImageService image service
     * @param categoryService category service
     * @param query          lucene query.
     * @param imageService image service
     */
    public SortableProductDataProvider(final ImageService imageService,
                                       final ProductService productService,
                                       final AttributableImageService attributableImageService,
                                       final CategoryService categoryService,
                                       final Query query) {
        this.productService = productService;
        this.attributableImageService = attributableImageService;
        this.categoryService = categoryService;
        this.query = query;
        this.imageService = imageService;
    }

    public Iterator<? extends ProductDecorator> iterator(int first, int count) {
        if (query == null || size() == 0) {
            products = Collections.EMPTY_LIST;
        } else {
            products = decorate(productService.getProductByQuery(
                    query,
                    first,
                    count,
                    sortFieldName,
                    reverse));
        }

        return products.iterator();
    }


    private List<ProductDecorator> decorate(final List<Product> productsToDecorate) {
        final List<ProductDecorator> rez = new ArrayList<ProductDecorator>(productsToDecorate.size());
        for (Product product : productsToDecorate) {
            rez.add(
                    new ProductDecoratorImpl(
                            imageService,
                            attributableImageService,
                            categoryService,
                            product,
                            WicketUtil.getHttpServletRequest().getContextPath()
                    )
            );
        }
        return rez;
    }


    public int size() {
        if (query != null) {
            return productService.getProductQty(query);
        }
        return 0;
    }

    public String getSortFieldName() {
        return sortFieldName;
    }

    public void setSortFieldName(final String sortFieldName) {
        this.sortFieldName = sortFieldName;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(final boolean reverse) {
        this.reverse = reverse;
    }

    public IModel<ProductDecorator> model(ProductDecorator productDecorator) {

        IModel<ProductDecorator> model = new IModel<ProductDecorator>() {

            private ProductDecorator product;

            public ProductDecorator getObject() {
                return product;
            }

            public void setObject(final ProductDecorator product) {
                this.product = product;
            }

            public void detach() {
                //Nothing to do
            }
        };

        model.setObject(productDecorator);

        return model;


    }
}

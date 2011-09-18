package org.yes.cart.web.page.component.product;

import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.service.domain.ProductAssociationService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.ProductAssociation;
import org.yes.cart.domain.entity.Product;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.support.constants.WebServiceSpringKey;
import org.yes.cart.web.support.service.ProductImageService;
import org.yes.cart.web.util.WicketUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Product association panel, that can show configured from markup
 * associations like cross/up/accessories/sell/etc
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 16-Sep-2011
 * Time: 15:36:10
 */
public class ProductAssociationsView extends AbstractProductList {

    private List<Product> associatedProductList = null;

    /**
     * Construct product association view.
     *
     * @param id              component id
     * @param associationType type of association
     */
    public ProductAssociationsView(final String id, final String associationType) {
        super(id);
        final List<ProductAssociation> associatedProducts = productAssociationService.getProductAssociations(
                getProductId(),
                associationType
        );
        final List<Long> productIds = new ArrayList<Long>(associatedProducts.size());
        for (ProductAssociation pass : associatedProducts) {
            productIds.add(pass.getProductAssociated().getId());
        }
        associatedProductList = productService.getProductByIdList(productIds);
    }

    /**
     * Get product id from page parameters or from sku.
     *
     * @return product id
     */
    protected long getProductId() {
        final long productId;
        String productIdStr = getPage().getPageParameters().get(WebParametersKeys.PRODUCT_ID).toString();
        if (productIdStr == null) {
            long skuId = getPage().getPageParameters().get(WebParametersKeys.SKU_ID).toLong();
            productId = productSkuService.getById(skuId).getProduct().getProductId();
        } else {
            productId = Long.valueOf(productIdStr);
        }
        return productId;
    }

    /**
     * {@inheritDoc
     */
    public List<Product> getProductListToShow() {
        return associatedProductList;
    }

}

package org.yes.cart.web.page.component.product;

import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductAssociation;
import org.yes.cart.web.support.constants.WebParametersKeys;

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
    private final String associationType;

    /**
     * Construct product association view.
     *
     * @param id              component id
     * @param associationType type of association. See
     */
    public ProductAssociationsView(final String id, final String associationType) {
        super(id, true);
        this.associationType = associationType;
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
        if (associatedProductList == null) {
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
        return associatedProductList;
    }

}

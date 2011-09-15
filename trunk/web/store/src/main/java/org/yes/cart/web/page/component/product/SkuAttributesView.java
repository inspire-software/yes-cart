package org.yes.cart.web.page.component.product;

import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.ProductService;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 *
 * View to show attributes of particular sku.
 * Attributes - value pairs from product and sku will be merged.
 * Sku attributes have higher priority.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 17-Sep-2011
 * Time: 13:34:05
 */
public class SkuAttributesView extends BaseComponent {


    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;

    private final List<Pair<String, List<AttrValue>>> attributesToShow;

    /**
     * Construct attribute view.
     * @param id component id.
     * @param sku product sku
     * @param productOnly true in case if need to show product attributes only
     */
    public SkuAttributesView (final String id, final ProductSku sku, final boolean productOnly) {
        super(id);

        final long productTypeId = sku.getProduct().getProducttype().getId();

        List<Pair<String, List<AttrValue>>> productAttributes = productService.getProductAttributes(sku.getProduct(), productTypeId);
        if (productOnly) {
            attributesToShow = productAttributes;
        } else {
            List<Pair<String, List<AttrValue>>> skuAttributes = productService.getProductAttributes(sku, productTypeId);
            attributesToShow = null;
        }

    }


}

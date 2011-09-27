package org.yes.cart.web.support.entity.decorator.impl;

import org.springframework.beans.BeanUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.impl.ProductSkuEntity;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.support.entity.decorator.ProductSkuDecorator;
import org.yes.cart.web.support.service.AttributableImageService;

/**
 *
 * Product sku decorator.
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 1/10/11
 * Time: 14:42
 */
public class ProductSkuDecoratorImpl  extends ProductSkuEntity implements ProductSkuDecorator {


    private final AttributableImageService attributableImageService;
    private final CategoryService categoryService;
    private final String httpServletContextPath;
    private String productImageUrl;

    /**
     * Construct product sku decorator.
     * @param attributableImageService category image service to get the image.
     * @param categoryService          to get image width and height
     * @param httpServletContextPath   servlet context path
     * @param productSkuEntity sku to decorate
     */
    public ProductSkuDecoratorImpl(final AttributableImageService attributableImageService,
            final CategoryService categoryService,
            final ProductSku productSkuEntity,
            final String httpServletContextPath) {

        BeanUtils.copyProperties(productSkuEntity, this);
        this.httpServletContextPath = httpServletContextPath;
        this.attributableImageService = attributableImageService;
        this.categoryService = categoryService;
        this.productImageUrl = null;
    }


/**
     * {@inheritDoc}
     */
    public String getDefaultImage(final String width, final String height) {
        if (productImageUrl == null) {
            productImageUrl = attributableImageService.getImage(
                    this,
                    httpServletContextPath,
                    width,
                    height, getDefaultImageAttributeName());
        }
        return productImageUrl;
    }

    /**
     * {@inheritDoc}
     */
    public String getImageWidth(final Category category) {
        return categoryService.getCategoryAttributeRecursive(category,
                AttributeNamesKeys.Category.PRODUCT_IMAGE_WIDTH,
                "120");
    }

    /**
     * {@inheritDoc}
     */
    public String getImageHeight(final Category category) {
        return categoryService.getCategoryAttributeRecursive(category,
                AttributeNamesKeys.Category.PRODUCT_IMAGE_HEIGHT,
                "120");
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultImageAttributeName() {
        return Constants.PRODUCT_SKU_DEFAULT_IMAGE_ATTR_NAME;
    }


}

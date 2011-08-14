package org.yes.cart.web.support.entity.decorator.impl;

import org.springframework.beans.BeanUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.impl.ProductEntity;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.support.entity.decorator.ProductDecorator;
import org.yes.cart.web.support.service.ProductImageService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:39 PM
 */
public class ProductDecoratorImpl extends ProductEntity implements ProductDecorator {

    private final ProductImageService productImageService;
    private final CategoryService categoryService;
    private final String httpServletContextPath;
    private String productImageUrl;

    /**
     * Construct entity decorator.
     * @param productImageService  category image service to get the image.
     * @param categoryService  to get image width and height
     * @param httpServletContextPath servlet context path
     *
     * @param productEntity original product to decorate.
     */
    public ProductDecoratorImpl(
            final ProductImageService productImageService,
            final CategoryService categoryService,
            final Product productEntity,
            final String httpServletContextPath ) {

        BeanUtils.copyProperties(productEntity, this);
        this.httpServletContextPath = httpServletContextPath;
        this.productImageService = productImageService;
        this.categoryService = categoryService;
        this.productImageUrl = null;

    }

    /** {@inheritDoc} */
    public String getProductImage() {
        if (productImageUrl == null) {
            productImageUrl = productImageService.getProductImage(this, httpServletContextPath);
        }
        return productImageUrl;
    }

    /** {@inheritDoc} */
    public String getProductImage(final String width, final String height) {
        if (productImageUrl == null) {
            productImageUrl = productImageService.getProductImage(this, httpServletContextPath, width, height);
        }
        return productImageUrl;
    }

    /** {@inheritDoc} */
    public String getProductImageWidth(final Category category) {
        return categoryService.getCategoryAttributeRecursive(category,
                AttributeNamesKeys.Category.PRODUCT_IMAGE_WIDTH,
                "120");
    }

    /** {@inheritDoc} */
    public String getProductImageHeight(final Category category) {
        return categoryService.getCategoryAttributeRecursive(category,
                AttributeNamesKeys.Category.PRODUCT_IMAGE_HEIGHT,
                "120");
    }
}

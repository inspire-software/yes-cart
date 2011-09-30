package org.yes.cart.web.support.entity.decorator.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.impl.ProductSkuEntity;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.support.entity.decorator.ProductSkuDecorator;
import org.yes.cart.web.support.service.AttributableImageService;

import java.util.ArrayList;
import java.util.List;

/**
 * Product sku decorator.
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 1/10/11
 * Time: 14:42
 */
public class ProductSkuDecoratorImpl extends ProductSkuEntity implements ProductSkuDecorator {

    private final static List<String> attrNames = new ArrayList<String>() {{
        add(Constants.PRODUCT_SKU_IMAGE_ATTR_NAME_PREFIX + "0");
        add(Constants.PRODUCT_SKU_IMAGE_ATTR_NAME_PREFIX + "1");
        add(Constants.PRODUCT_SKU_IMAGE_ATTR_NAME_PREFIX + "2");
        add(Constants.PRODUCT_SKU_IMAGE_ATTR_NAME_PREFIX + "3");
        add(Constants.PRODUCT_SKU_IMAGE_ATTR_NAME_PREFIX + "4");
        add(Constants.PRODUCT_SKU_IMAGE_ATTR_NAME_PREFIX + "5");
    }};




    private final AttributableImageService attributableImageService;
    private final CategoryService categoryService;
    private final String httpServletContextPath;
    private String productImageUrl;

    /**
     * Construct product sku decorator.
     *
     * @param attributableImageService category image service to get the image.
     * @param categoryService          to get image width and height
     * @param httpServletContextPath   servlet context path
     * @param productSkuEntity         sku to decorate
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
    public List<String> getImageAttributeNames(final boolean filled) {
        if (filled) {
            final List<String> rez = new ArrayList<String>();
            for(String attrName : attrNames) {
                if(this.getAttributeByCode(attrName) != null) {
                    rez.add(attrName);
                }
            }
            return rez;
        }
        return attrNames;
    }


    /**
     * {@inheritDoc}
     */
    public String getImage(final String width, final String height, final String imageAttributeName) {
        return attributableImageService.getImage(
                this,
                httpServletContextPath,
                width,
                height,
                imageAttributeName);
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
    public String getDefaultImageWidth(final Category category) {
        return categoryService.getCategoryAttributeRecursive(category,
                AttributeNamesKeys.Category.PRODUCT_IMAGE_WIDTH,
                PRODUCT_DEFAULT_IMAGE_WIDTH);
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultImageHeight(final Category category) {
        return categoryService.getCategoryAttributeRecursive(category,
                AttributeNamesKeys.Category.PRODUCT_IMAGE_HEIGHT,
                PRODUCT_DEFAULT_IMAGE_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    public String getThumbnailImageWidth(final Category category) {
        return categoryService.getCategoryAttributeRecursive(category,
                AttributeNamesKeys.Category.PRODUCT_IMAGE_WIDTH,
                PRODUCT_THUMBNAIL_IMAGE_WIDTH);
    }

    /**
     * {@inheritDoc}
     */
    public String getThumbnailImageHeight(final Category category) {
        return categoryService.getCategoryAttributeRecursive(category,
                AttributeNamesKeys.Category.PRODUCT_IMAGE_HEIGHT,
                PRODUCT_THUMBNAIL_IMAGE_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultImageAttributeName() {
        return Constants.PRODUCT_SKU_DEFAULT_IMAGE_ATTR_NAME;
    }


}

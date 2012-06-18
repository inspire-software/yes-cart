package org.yes.cart.web.support.entity.decorator.impl;

import com.google.common.collect.MapMaker;
import org.springframework.beans.BeanUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.ProductEntity;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.web.support.entity.decorator.ProductDecorator;
import org.yes.cart.web.support.service.AttributableImageService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:39 PM
 */
public class ProductDecoratorImpl extends ProductEntity implements ProductDecorator {

    private final static List<String> attrNames = new ArrayList<String>() {{
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "0");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "1");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "2");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "3");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "4");
        add(Constants.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "5");
    }};

    final static  String [] defaultSize =
            new String [] {
                        AttributeNamesKeys.Category.PRODUCT_IMAGE_WIDTH,
                        AttributeNamesKeys.Category.PRODUCT_IMAGE_HEIGHT
            };

    final static  String [] thumbnailSize =
            new String [] {
                        AttributeNamesKeys.Category.PRODUCT_IMAGE_TUMB_WIDTH,
                        AttributeNamesKeys.Category.PRODUCT_IMAGE_TUMB_HEIGHT
            };


    private final AttributableImageService attributableImageService;
    private final CategoryService categoryService;
    private final String httpServletContextPath;
    private String productImageUrl;
    private final ImageService imageService;
    private final Map<String, AttrValue> attrValueMap;

    /**
     * Construct entity decorator.
     *
     * @param attributableImageService category image service to get the image.
     * @param categoryService          to get image width and height
     * @param httpServletContextPath   servlet context path
     * @param productEntity            original product to decorate.
     * @param imageService image serice to get the image seo info
     */
    private ProductDecoratorImpl(
            final ImageService imageService,
            final AttributableImageService attributableImageService,
            final CategoryService categoryService,
            final Product productEntity,
            final String httpServletContextPath,
            final boolean withAttributes) {

        BeanUtils.copyProperties(productEntity, this);
        this.httpServletContextPath = httpServletContextPath;
        this.attributableImageService = attributableImageService;
        this.categoryService = categoryService;
        this.productImageUrl = null;
        this.imageService = imageService;
        if (withAttributes) {
            this.attrValueMap = getAllAttibutesAsMap();
        } else {
            this.attrValueMap = Collections.emptyMap();

        }

    }


    private static final ConcurrentMap<String, ProductDecoratorImpl> productDecoratorCache = new MapMaker()
            .concurrencyLevel(16).softValues()
            .expiration(Constants.DEFAULT_EXPIRATION_TIMEOUT, TimeUnit.MINUTES).makeMap();


    public static ProductDecoratorImpl createProductDecoratorImpl(
            final ImageService imageService,
            final AttributableImageService attributableImageService,
            final CategoryService categoryService,
            final Product productEntity,
            final String httpServletContextPath,
            final boolean withAttributes) {

        final String key = httpServletContextPath + productEntity.getProductId() + withAttributes;

        ProductDecoratorImpl rez = productDecoratorCache.get(key);
        if (rez == null) {
            rez = new ProductDecoratorImpl(imageService, attributableImageService, categoryService, productEntity, httpServletContextPath, withAttributes);
            productDecoratorCache.put(key, rez);
        }
        return rez;
    }


    /**
     * {@inheritDoc}
     */
    public List<String> getImageAttributeNames() {
        return attrNames;
    }


    /**
     * {@inheritDoc}
     */
    public List<Pair<String, String>> getImageAttributeFileNames() {
        final List<Pair<String, String>> rez = new ArrayList<Pair<String, String>>();
        for (String attrName : attrNames) {
            if (this.getAttributeByCode(attrName) != null) {
                rez.add(new Pair<String, String>(attrName, this.getAttributeByCode(attrName).getVal()));
            }
        }
        return rez;
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
                    height,
                    getDefaultImageAttributeName()
            );
        }
        return productImageUrl;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueProduct getAttributeByCode(final String attributeCode) {
        return (AttrValueProduct) attrValueMap.get(attributeCode);
    }


    /**
     * {@inheritDoc}
     */
    public String [] getDefaultImageSize(final Category category) {
        return categoryService.getCategoryAttributeRecursive(
                category,
                defaultSize
        );
    }


    /**
     * {@inheritDoc}
     */
    public String [] getThumbnailImageSize(final Category category) {
        return categoryService.getCategoryAttributeRecursive(
                category,
                ProductDecoratorImpl.thumbnailSize
        );
    }




    /**
     * {@inheritDoc}
     */
    public String getDefaultImageAttributeName() {
        return Constants.PRODUCT_DEFAULT_IMAGE_ATTR_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public SeoImage getSeoImage(final String fileName) {
        return imageService.getSeoImage(fileName);
    }

}

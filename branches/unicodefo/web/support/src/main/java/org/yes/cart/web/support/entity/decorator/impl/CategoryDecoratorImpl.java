package org.yes.cart.web.support.entity.decorator.impl;

import org.springframework.beans.BeanUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.domain.entity.impl.CategoryEntity;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;
import org.yes.cart.web.support.service.AttributableImageService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 8:19 PM
 */
public class CategoryDecoratorImpl extends CategoryEntity implements CategoryDecorator {

    private static final List<String> attrNames = Collections.singletonList(AttributeNamesKeys.Category.CATEGORY_IMAGE);

    private final static String[] defaultSize =
            new String[]{
                    AttributeNamesKeys.Category.CATEGORY_IMAGE_WIDTH,
                    AttributeNamesKeys.Category.CATEGORY_IMAGE_HEIGHT
            };

    private final static String[] thumbnailSize =
            new String[]{
                    AttributeNamesKeys.Category.CATEGORY_IMAGE_WIDTH,
                    AttributeNamesKeys.Category.CATEGORY_IMAGE_HEIGHT
            };

    private final AttributableImageService categoryImageService;
    private final CategoryService categoryService;
    private final String httpServletContextPath;
    private String categoryImageUrl;
    private final ImageService imageService;

    /**
     * Construct entity decorator.
     *
     * @param categoryImageService   category image service to get the image.
     * @param categoryEntity         entity to decorate.
     * @param httpServletContextPath servlet context path
     * @param categoryService        category service to get the images width and height
     * @param imageService           image service to get the image seo info
     */
    public CategoryDecoratorImpl(
            final ImageService imageService,
            final AttributableImageService categoryImageService,
            final CategoryService categoryService,
            final Category categoryEntity,
            final String httpServletContextPath) {
        this.categoryService = categoryService;
        this.categoryImageService = categoryImageService;
        this.httpServletContextPath = httpServletContextPath;
        this.imageService = imageService;
        BeanUtils.copyProperties(categoryEntity, this);
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
        return categoryImageService.getImage(
                this,
                httpServletContextPath,
                width,
                height,
                imageAttributeName,
                null);
    }


    /**
     * {@inheritDoc}
     */
    public String getDefaultImage(final String width, final String height) {
        if (categoryImageUrl == null) {
            categoryImageUrl = categoryImageService.getImage(this, httpServletContextPath, width, height, null, null);
        }
        return categoryImageUrl;
    }


    /**
     * {@inheritDoc}
     */
    public String[] getDefaultImageSize(final Category category) {
        return categoryService.getCategoryAttributeRecursive(
                category,
                defaultSize
        );
    }


    /**
     * {@inheritDoc}
     */
    public String[] getThumbnailImageSize(final Category category) {
        return categoryService.getCategoryAttributeRecursive(
                category,
                thumbnailSize
        );
    }


    public String getThumbnailImageWidth(final Category category) {
        return categoryService.getCategoryAttributeRecursive(category,
                AttributeNamesKeys.Category.CATEGORY_IMAGE_WIDTH,
                "80");
    }

    public String getThumbnailImageHeight(final Category category) {
        return categoryService.getCategoryAttributeRecursive(this,
                AttributeNamesKeys.Category.CATEGORY_IMAGE_HEIGHT,
                "80");
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultImageAttributeName() {
        return AttributeNamesKeys.Category.CATEGORY_IMAGE;
    }


    /**
     * {@inheritDoc}
     */
    public SeoImage getSeoImage(final String fileName) {
        return imageService.getSeoImage(fileName);
    }

}

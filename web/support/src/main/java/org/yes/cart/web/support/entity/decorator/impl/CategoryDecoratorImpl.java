package org.yes.cart.web.support.entity.decorator.impl;

import org.springframework.beans.BeanUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.impl.CategoryEntity;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;
import org.yes.cart.web.support.service.AttributableImageService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 8:19 PM
 */
public class CategoryDecoratorImpl extends CategoryEntity implements CategoryDecorator {

    private final AttributableImageService categoryImageService;
    private final CategoryService categoryService;
    private final String httpServletContextPath;
    private String categoryImageUrl;

    /**
     * Construct entity decorator.
     *
     * @param categoryImageService   category image service to get the image.
     * @param categoryEntity         entity to decorate.
     * @param httpServletContextPath servlet context path
     * @param categoryService        category service to get the images width and height
     */
    public CategoryDecoratorImpl(final AttributableImageService categoryImageService,
                                 final CategoryService categoryService,
                                 final Category categoryEntity,
                                 final String httpServletContextPath) {
        this.categoryService = categoryService;
        this.categoryImageService = categoryImageService;
        this.httpServletContextPath = httpServletContextPath;
        BeanUtils.copyProperties(categoryEntity, this);
    }


    public String getDefaultImage(String width, String height) {
        if (categoryImageUrl == null) {
            categoryImageUrl = categoryImageService.getImage(this, httpServletContextPath, width, height, null);
        }
        return categoryImageUrl;
    }

    public String getImageWidth(final Category category) {
        return categoryService.getCategoryAttributeRecursive(category,
                AttributeNamesKeys.Category.CATEGORY_IMAGE_WIDTH,
                "80");
    }

    public String getImageHeight(final Category category) {
        return categoryService.getCategoryAttributeRecursive(this,
                AttributeNamesKeys.Category.CATEGORY_IMAGE_HEIGHT,
                "80");
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultImageAttributeName() {
        return Constants.PRODUCT_DEFAULT_IMAGE_ATTR_NAME;
    }

}

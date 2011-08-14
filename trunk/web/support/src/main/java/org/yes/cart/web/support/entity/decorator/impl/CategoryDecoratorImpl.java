package org.yes.cart.web.support.entity.decorator.impl;

import org.springframework.beans.BeanUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.impl.CategoryEntity;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;
import org.yes.cart.web.support.service.CategoryImageService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 8:19 PM
 */
public class CategoryDecoratorImpl extends CategoryEntity implements CategoryDecorator {

    private final CategoryImageService categoryImageService;
    private final CategoryService categoryService;
    private final String httpServletContextPath;
    private String categoryImageUrl;

    /**
     * Construct entity decorator.
     * @param categoryImageService  category image service to get the image.
     * @param categoryEntity  entity to decorate.
     * @param httpServletContextPath servlet context path
     * @param categoryService category service to get the images width and height
     */
    public CategoryDecoratorImpl(final CategoryImageService categoryImageService,
                                 final CategoryService categoryService,
                                 final Category categoryEntity,
                                 final String httpServletContextPath) {
        this.categoryService = categoryService;
        this.categoryImageService = categoryImageService;
        this.httpServletContextPath = httpServletContextPath;
        BeanUtils.copyProperties(categoryEntity, this);
    }

    /** {@inheritDoc} */
    public String getCategoryImage() {
        if (categoryImageUrl == null) {
            categoryImageUrl = categoryImageService.getCategoryImage(this, httpServletContextPath);
        }
        return categoryImageUrl;
    }

    /** {@inheritDoc} */
    public String getCategoryImage(final String width, final  String height) {
        if (categoryImageUrl == null) {
            categoryImageUrl = categoryImageService.getCategoryImage(this, httpServletContextPath, width, height);
        }
        return categoryImageUrl;
    }

    /**
     * Get category image width.
     * @return  image width.
     */
    public String getCategoryImageWidth() {
        return categoryService.getCategoryAttributeRecursive(this,
                AttributeNamesKeys.Category.CATEGORY_IMAGE_WIDTH,
                "80");
    }

    /**
     * Get category image height.
     * @return     image height.
     */
    public String getCategoryImageHeight() {
        return categoryService.getCategoryAttributeRecursive(this,
                AttributeNamesKeys.Category.CATEGORY_IMAGE_HEIGHT,
                "80");
    }
}

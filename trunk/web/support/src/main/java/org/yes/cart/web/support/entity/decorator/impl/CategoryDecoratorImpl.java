package org.yes.cart.web.support.entity.decorator.impl;

import org.springframework.beans.BeanUtils;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.impl.CategoryEntity;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;
import org.yes.cart.web.support.service.CategoryImageService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 8:19 PM
 */
public class CategoryDecoratorImpl extends CategoryEntity implements CategoryDecorator {

    private final CategoryImageService categoryImageService;
    private final String httpServletContextPath;
    private String categoryImageUrl;

    /**
     * Construct entity decorator.
     * @param categoryImageService  category image service to get the image.
     * @param categoryEntity  entity to decorate.
     * @param httpServletContextPath servlet context path
     */
    public CategoryDecoratorImpl(final CategoryImageService categoryImageService,
                                 final Category categoryEntity,
                                 final String httpServletContextPath) {
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
}

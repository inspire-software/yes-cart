package org.yes.cart.web.support.service.impl;

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.support.service.CategoryImageRetrieveStrategy;
import org.yes.cart.web.support.service.CategoryImageService;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 6:42 PM
 */
public class CategoryImageServiceImpl  extends AbstractImageServiceImpl implements CategoryImageService {

    private final Map<String, CategoryImageRetrieveStrategy> strategies;
    private final CategoryService categoryService;
    private final String defaultStrategy;

    /**
     * Construct category image service.
     *
     * @param strategies      map of strategy lable - strategy.
     * @param categoryService category service to use.
     * @param defaultStrategy default strategy if strategy can not be found for given category
     */
    public CategoryImageServiceImpl(final Map<String, CategoryImageRetrieveStrategy> strategies,
                                    final CategoryService categoryService,
                                    final String defaultStrategy) {
        this.strategies = strategies;
        this.categoryService = categoryService;
        this.defaultStrategy = defaultStrategy;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getImageRepositoryUrlPattern(final Object object) {
        String label = (String) object;

        final CategoryImageRetrieveStrategy strategy = strategies.get(label);

        return strategy.getImageRepositoryUrlPattern();

    }

    /** {@inheritDoc} */
    public String getCategoryImage(final Category category,
                            final String httpServletContextPath) {

        final String strategyLabel = getImageRetreiveStrategy(category);

        final CategoryImageRetrieveStrategy strategy = strategies.get(strategyLabel);

        final String imageName = strategy.getImageName(category);

        final String url = getImageURI(imageName, httpServletContextPath, strategyLabel);

        return url;
    }

    /**
     * Get the image retreive strategy label for given category.
     *
     * @param category given category
     * @return strategy label if found, otherwise default strategy label will be returned.
     */
    String getImageRetreiveStrategy(Category category) {

        return categoryService.getCategoryAttributeRecursive(
                category,
                AttributeNamesKeys.CATEGORY_IMAGE_RETREIVE_STRATEGY,
                defaultStrategy);
    }



}

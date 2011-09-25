package org.yes.cart.web.support.service.impl;

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Attributable;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.support.service.AttributableImageService;
import org.yes.cart.web.support.service.CategoryImageRetrieveStrategy;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/4/11
 * Time: 6:42 PM
 */
public class CategoryImageServiceImpl extends AbstractImageServiceImpl implements AttributableImageService {

    private final Map<String, CategoryImageRetrieveStrategy> strategies;
    private final CategoryService categoryService;
    private final String defaultStrategy;

    /**
     * Construct category image service.
     *
     * @param strategies      map of strategy label - strategy.
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
    public String getImageRepositoryUrlPattern(final Object object) {     //TODO
        //final AttrValue attrValue = ((Category) object).getAttributeByCode("CATEGORY_IMAGE_RETREIVE_STRATEGY"); //TODO consta
        /*String val = "RANDOM_PRODUCT";//TODO const
        if (attrValue != null) {
            val = attrValue.getVal();
        }  */
        final CategoryImageRetrieveStrategy strategy = strategies.get(object);
        if (strategy == null) {
            return "RANDOM_PRODUCT";
        }
        return strategy.getImageRepositoryUrlPattern();
    }




    /**
     * {@inheritDoc}
     */
    public String getImage(final Attributable category,
                           final String httpServletContextPath,
                           final String width,
                           final String height,
                           final String attrName) {

        //final String strategyLabel = getImageRetreiveStrategy(category);
        final String strategyLabel = getImageAttributeValue(category, AttributeNamesKeys.Category.CATEGORY_IMAGE_RETREIVE_STRATEGY);
        final CategoryImageRetrieveStrategy strategy = strategies.get(strategyLabel);
        final String imageName = strategy.getImageName(category);
        return getImageURI(imageName, width, height, httpServletContextPath, strategyLabel);

    }


}

/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.support.service.impl;

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Attributable;
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
    public String getImageRepositoryUrlPattern(final Object strategyLabel) {
        final String strategyKey;
        if (strategies.containsKey(strategyLabel)) {
            strategyKey = String.valueOf(strategyLabel);
        } else {
            strategyKey = defaultStrategy;
        }
        final CategoryImageRetrieveStrategy strategy = strategies.get(strategyKey);
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
        final String strategyLabel = getImageAttributeValue(category, AttributeNamesKeys.Category.CATEGORY_IMAGE_RETRIEVE_STRATEGY, defaultStrategy);
        final CategoryImageRetrieveStrategy strategy = strategies.get(strategyLabel);
        final String imageName = strategy.getImageName(category);
        return getImageURI(imageName, width, height, httpServletContextPath, strategyLabel);

    }


}

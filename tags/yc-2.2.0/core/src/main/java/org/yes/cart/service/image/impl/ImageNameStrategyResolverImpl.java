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

package org.yes.cart.service.image.impl;

import org.yes.cart.service.image.ImageNameStrategy;
import org.yes.cart.service.image.ImageNameStrategyResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible to get the particular {@link ImageNameStrategy} by given image url.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ImageNameStrategyResolverImpl implements ImageNameStrategyResolver {

    private final ImageNameStrategy defaultStrategy;
    private final Map<String, ImageNameStrategy> urlToStrategyMap;
    private final String[] urls;

    /**
     * Construct image name strategy resolver.
     *
     * @param defaultStrategy  default strategy
     * @param urlToStrategies map of strategies for given url paths
     */
    public ImageNameStrategyResolverImpl(final ImageNameStrategy defaultStrategy,
                                         final List<ImageNameStrategy> urlToStrategies) {

        this.defaultStrategy = defaultStrategy;
        final Map<String, ImageNameStrategy> urlToStrategyMap = new HashMap<String, ImageNameStrategy>();
        for (final ImageNameStrategy strategy : urlToStrategies) {
            urlToStrategyMap.put(strategy.getUrlPath(), strategy);
        }
        this.urlToStrategyMap = urlToStrategyMap;
        this.urls = new ArrayList<String>(urlToStrategyMap.keySet()).toArray(new String[urlToStrategyMap.keySet().size()]);
    }


    /**
     * {@inheritDoc}
     */
    public ImageNameStrategy getImageNameStrategy(final String imageUrl) {

        for (final String url : urls) {

            if (imageUrl.contains(url)) {
                return urlToStrategyMap.get(url);
            }

        }

        return defaultStrategy;

    }
}

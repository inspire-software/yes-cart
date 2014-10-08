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

package org.yes.cart.service.misc.impl;

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.misc.LanguageService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/25/11
 * Time: 10:19 PM
 */
public class LanguageServiceImpl implements LanguageService {

    private final Map<String, String> languageName;

    private final List<String> supportedLanguages;

    private final Map<String, List<String>> shopToLanguageMap;

    private final ShopService shopService;

    /**
     * Construct language service.
     * @param languageName  map lang code - lang name
     * @param shopService shop service
     */
    public LanguageServiceImpl(final Map<String, String> languageName,
                               final Map<String, List<String>> shopToLanguageMap,
                               final ShopService shopService) {
        this.shopService = shopService;
        this.languageName = new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        this.languageName.putAll(languageName);
        this.supportedLanguages = shopToLanguageMap.get("DEFAULT");
        this.shopToLanguageMap = shopToLanguageMap;
    }

    /** {@inheritDoc} */
    @Override
    public String resolveLanguageName(final String language) {
        return languageName.get(language);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getLanguageName() {
        return languageName;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getSupportedLanguages(final String shopCode) {

        final Shop shop = shopService.getShopByCode(shopCode);
        if (shop != null) {
            final List<String> languageCodes = shop.getSupportedLanguagesAsList();
            if (!languageCodes.isEmpty()) {
                return languageCodes;
            }
        }

        if (shopToLanguageMap.containsKey(shopCode)) {
            return shopToLanguageMap.get(shopCode);
        }
        return getSupportedLanguages();
    }
}

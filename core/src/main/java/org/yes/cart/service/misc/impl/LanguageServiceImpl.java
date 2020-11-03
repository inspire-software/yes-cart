/*
 * Copyright 2009 Inspire-Software.com
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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.utils.RuntimeConstants;

import java.io.IOException;
import java.util.*;

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
     * @param config  property file with i18n configurations
     * @param shopService shop service
     */
    public LanguageServiceImpl(final RuntimeConstants config,
                               final ShopService shopService) throws IOException {

        this.shopService = shopService;
        this.languageName = new TreeMap<>((o1, o2) -> o1.compareToIgnoreCase(o2));
        this.languageName.putAll(getLanguageNameFromConfig(config));
        this.shopToLanguageMap = getShopToLanguageMapFromConfig(config);
        this.supportedLanguages = this.shopToLanguageMap.get("DEFAULT");
    }

    private Map<String, String> getLanguageNameFromConfig(final RuntimeConstants config) {

        final String langs = config.getConstantNonBlankOrDefault("webapp.i18n.supported.locales", "en,de,ru,uk");

        final Map<String, String> all = new LinkedHashMap<>();

        for (final String lang : StringUtils.split(langs, ',')) {
            all.put(lang, config.getConstantNonBlankOrDefault("webapp.i18n.supported.locales." + lang, lang));
        }

        return all;
    }

    private Map<String, List<String>> getShopToLanguageMapFromConfig(final RuntimeConstants config) {

        final String shops = config.getConstantNonBlankOrDefault("webapp.i18n.supported.locales.specific", "DEFAULT");

        final Map<String, List<String>> all = new LinkedHashMap<>();

        for (final String shop : StringUtils.split(shops, ',')) {
            all.put(shop, Arrays.asList(
                    StringUtils.split(config.getConstantNonBlankOrDefault("webapp.i18n.supported.locales.specific." + shop, "en,de,ru,uk"), ',')
            ));
        }

        return all;

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

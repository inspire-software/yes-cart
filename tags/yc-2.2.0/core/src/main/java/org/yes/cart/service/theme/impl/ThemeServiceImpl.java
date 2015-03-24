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

package org.yes.cart.service.theme.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.theme.ThemeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 02/09/2014
 * Time: 18:37
 */
public class ThemeServiceImpl implements ThemeService {

    private static final String DEFAULT_THEME = "default";
    private static final List<String> DEFAULT_CHAIN = Arrays.asList(DEFAULT_THEME);

    private static final String MARKUP_ROOT         = "/markup/";
    private static final String MAILTEMPLATE_ROOT   = "/mail/";

    private final ShopService shopService;

    public ThemeServiceImpl(final ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "themeService-themeChainByShopId")
    public List<String> getThemeChainByShopId(final Long shopId) {
        if (shopId != null) {
            final Shop shop = shopService.getById(shopId);
            if (shop != null) {

                final String themeCfg = shop.getFspointer();

                if (StringUtils.isNotBlank(themeCfg)) {

                    if (themeCfg.indexOf(';') == -1) {
                        final List<String> tmpChain = new ArrayList<String>(Arrays.asList(themeCfg));
                        if (!tmpChain.contains(DEFAULT_THEME)) {
                            tmpChain.add(DEFAULT_THEME);
                        }
                        return Collections.unmodifiableList(tmpChain);
                    }
                    final List<String> tmpChain = new ArrayList<String>(Arrays.asList(StringUtils.split(shop.getFspointer(), ';')));
                    if (!tmpChain.contains(DEFAULT_THEME)) {
                        tmpChain.add(DEFAULT_THEME);
                    }
                    return Collections.unmodifiableList(tmpChain);
                }
            }
        }
        return DEFAULT_CHAIN;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "themeService-markupChainByShopId")
    public List<String> getMarkupChainByShopId(final Long shopId) {
        final List<String> themes = getThemeChainByShopId(shopId);
        final List<String> markups = new ArrayList<String>(themes.size());
        for (final String theme : themes) {
            markups.add(theme + MARKUP_ROOT);
        }
        return Collections.unmodifiableList(markups);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "themeService-mailTemplateChainByShopId")
    public List<String> getMailTemplateChainByShopId(final Long shopId) {
        final List<String> themes = getThemeChainByShopId(shopId);
        final List<String> mailtemplates = new ArrayList<String>(themes.size());
        for (final String theme : themes) {
            mailtemplates.add(theme + MAILTEMPLATE_ROOT);
        }
        return Collections.unmodifiableList(mailtemplates);
    }
}

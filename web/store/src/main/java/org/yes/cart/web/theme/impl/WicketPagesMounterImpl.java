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

package org.yes.cart.web.theme.impl;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.MountedMapper;
import org.apache.wicket.util.ClassProvider;
import org.slf4j.Logger;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.theme.WicketPagesMounter;
import org.yes.cart.web.util.SeoBookmarkablePageParametersEncoder;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-03-22
 * Time: 4:18 PM
 */
public class WicketPagesMounterImpl implements WicketPagesMounter {

    private final SeoBookmarkablePageParametersEncoder encoder;
    private final String loginUrl;
    private final Map<String, Map<String, Class<IRequestablePage>>> pageMapping;
    private final List<String> encoderEnabledUrls;

    private ClassProvider<IRequestablePage> loginPage;
    private ClassProvider<IRequestablePage> homePage;

    public WicketPagesMounterImpl(final SeoBookmarkablePageParametersEncoder encoder,
                                  final Map<String, Map<String, Class<IRequestablePage>>> pageMapping,
                                  final String loginUrl,
                                  final List<String> encoderEnabledUrls) {
        this.encoder = encoder;
        this.loginUrl = loginUrl;
        this.pageMapping = pageMapping;
        this.encoderEnabledUrls = encoderEnabledUrls;
    }

    /** {@inheritDoc} */
    public void mountPages(final WebApplication webApplication) {

        final Logger log = ShopCodeContext.getLog();

        for (Map.Entry<String, Map<String, Class<IRequestablePage>>> pageMappingEntry : pageMapping.entrySet()) {
            final String url = pageMappingEntry.getKey();
            final Map<String, Class<IRequestablePage>> pages = pageMappingEntry.getValue();
            final ClassProvider classProvider;
            if (pages.size() == 1) {
                // there is only default mapping for this url
                classProvider = ClassProvider.of(pages.entrySet().iterator().next().getValue());
                if (log.isInfoEnabled()) {
                    log.info("Mounting url '{}' to page '{}'", url, classProvider.get().getCanonicalName());
                }
            } else {
                // more than one mapping - need a theme dependent class provider
                classProvider = new ThemePageProvider(pages);
                if (log.isInfoEnabled()) {
                    log.info("Mounting url '{}' to pages:", url);
                    for (final Map.Entry<String, Class<IRequestablePage>> entry : pages.entrySet()) {
                        log.info("theme: '{}', page: '{}'", entry.getKey(), entry.getValue());
                    }
                }
            }
            if (encoderEnabledUrls.contains(url)) {
                webApplication.mount(new MountedMapper(url, classProvider, encoder));
            } else {
                webApplication.mount(new MountedMapper(url, classProvider));
            }
            if (loginUrl.equals(url)) {
                loginPage = classProvider;
                if (log.isInfoEnabled()) {
                    log.info("This is a login url");
                }
            } else if ("/".equals(url)) {
                homePage = classProvider;
                if (log.isInfoEnabled()) {
                    log.info("This is a home url");
                }
            }
        }
    }

    /** {@inheritDoc} */
    public ClassProvider<IRequestablePage> getHomePageProvider() {
        return homePage;
    }

    /** {@inheritDoc} */
    public ClassProvider<IRequestablePage> getLoginPageProvider() {
        return loginPage;
    }
}

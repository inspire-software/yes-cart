/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.util.reference.ClassReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.web.theme.WicketPagesMounter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-03-22
 * Time: 4:18 PM
 */
public class WicketPagesMounterImpl implements WicketPagesMounter {

    private static final Logger LOG = LoggerFactory.getLogger(WicketPagesMounterImpl.class);

    private final IPageParametersEncoder encoder;
    private final String loginUrl;
    private final Map<String, Map<String, Class<IRequestablePage>>> pageMapping;
    private final List<String> encoderEnabledUrls;

    private ClassReference<IRequestablePage> loginPage;
    private ClassReference<IRequestablePage> homePage;
    private Map<String, ClassReference<IRequestablePage>> pageByUri = new HashMap<String, ClassReference<IRequestablePage>>();

    public WicketPagesMounterImpl(final IPageParametersEncoder encoder,
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

        for (Map.Entry<String, Map<String, Class<IRequestablePage>>> pageMappingEntry : pageMapping.entrySet()) {
            final String url = pageMappingEntry.getKey();
            final Map<String, Class<IRequestablePage>> pages = pageMappingEntry.getValue();
            final ClassReference classProvider;
            if (pages.size() == 1) {
                // there is only default mapping for this url
                classProvider = ClassReference.of(pages.entrySet().iterator().next().getValue());
                LOG.info("Mounting url '{}' to page '{}'", url, classProvider.get().getCanonicalName());
            } else {
                // more than one mapping - need a theme dependent class provider
                classProvider = new ThemePageProvider(pages);
                if (LOG.isInfoEnabled()) {
                    LOG.info("Mounting url '{}' to pages:", url);
                    for (final Map.Entry<String, Class<IRequestablePage>> entry : pages.entrySet()) {
                        LOG.info("theme: '{}', page: '{}'", entry.getKey(), entry.getValue());
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
                LOG.info("Login url [{}], class {}", loginUrl, loginPage);
            } else if ("/".equals(url)) {
                homePage = classProvider;
                LOG.info("Home url [/], class {}", homePage);
            }
            pageByUri.put(url, classProvider);
        }
    }

    /** {@inheritDoc} */
    public ClassReference<IRequestablePage> getHomePageProvider() {
        return homePage;
    }

    /** {@inheritDoc} */
    public ClassReference<IRequestablePage> getLoginPageProvider() {
        return loginPage;
    }

    /** {@inheritDoc} */
    public ClassReference<IRequestablePage> getPageProviderByUri(final String uri) {
        return pageByUri.get(uri);
    }
}

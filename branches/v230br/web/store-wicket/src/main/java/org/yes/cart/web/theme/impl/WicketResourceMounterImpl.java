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

import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.yes.cart.web.theme.WicketResourceMounter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13-08-28
 * Time: 3:26 PM
 */
public class WicketResourceMounterImpl implements WicketResourceMounter {

    private final List<String> enabledPatterns = new ArrayList<String>();

    private IResource sitemapXml;

    /**
     * Wicket resource via Spring IoC.
     *
     * @param sitemapXml sitemap resource
     */
    public void setSitemapXml(final IResource sitemapXml) {
        this.sitemapXml = sitemapXml;
    }

    /**
     * Patterns to enable for the application.
     *
     * @param enabledPatterns patterns
     */
    public void setEnabledPatterns(final List<String> enabledPatterns) {
        this.enabledPatterns.addAll(enabledPatterns);
    }

    /** {@inheritDoc} */
    @Override
    public void enableResourceAccess(final WebApplication webApplication) {

        IPackageResourceGuard packageResourceGuard = webApplication.getResourceSettings().getPackageResourceGuard();
        if (packageResourceGuard instanceof SecurePackageResourceGuard)
        {
            SecurePackageResourceGuard guard = (SecurePackageResourceGuard) packageResourceGuard;

            for (final String pattern : enabledPatterns) {
                guard.addPattern(pattern);
            }
        }

    }

    /** {@inheritDoc} */
    @Override
    public void mountResources(final WebApplication webApplication) {

        webApplication.mountResource("/sitemap.xml", new ResourceReference("sitemap.xml"){
            @Override
            public IResource getResource() {
                return sitemapXml;
            }
        });

    }
}

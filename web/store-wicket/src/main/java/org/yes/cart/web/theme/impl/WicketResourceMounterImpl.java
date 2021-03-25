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

package org.yes.cart.web.theme.impl;

import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.utils.spring.ArrayListBean;
import org.yes.cart.utils.spring.LinkedHashMapBean;
import org.yes.cart.web.theme.WicketResourceMounter;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-08-28
 * Time: 3:26 PM
 */
public class WicketResourceMounterImpl implements WicketResourceMounter {

    private static final Logger LOG = LoggerFactory.getLogger(WicketResourceMounterImpl.class);

    private List<String> enabledPatterns;

    private Map<String, IResource> resources;

    /**
     * Wicket resource via Spring IoC.
     *
     * @param resources resources
     */
    public void setResources(final LinkedHashMapBean<String, IResource> resources) {
        this.resources = resources;
    }

    /**
     * Patterns to enable for the application.
     *
     * @param enabledPatterns patterns
     */
    public void setEnabledPatterns(final ArrayListBean<String> enabledPatterns) {
        this.enabledPatterns = enabledPatterns;
    }

    /** {@inheritDoc} */
    @Override
    public void enableResourceAccess(final WebApplication webApplication) {

        IPackageResourceGuard packageResourceGuard = webApplication.getResourceSettings().getPackageResourceGuard();
        if (packageResourceGuard instanceof SecurePackageResourceGuard)
        {
            SecurePackageResourceGuard guard = (SecurePackageResourceGuard) packageResourceGuard;

            if (enabledPatterns != null) {
                for (final String pattern : enabledPatterns) {
                    LOG.info("Enabling resource pattern '{}'", pattern);
                    guard.addPattern(pattern);
                }
            }
        }

    }

    /** {@inheritDoc} */
    @Override
    public void mountResources(final WebApplication webApplication) {

        if (resources != null) {
            for (final Map.Entry<String, IResource> resource : resources.entrySet()) {
                final String key = resource.getKey();
                final IResource source = resource.getValue();
                LOG.info("Mounting url '/{}' to resource '{}'", key, source.getClass().getCanonicalName());
                webApplication.mountResource("/" + key, new ResourceReference(key){
                    @Override
                    public IResource getResource() {
                        return source;
                    }
                });
            }
        }

    }
}

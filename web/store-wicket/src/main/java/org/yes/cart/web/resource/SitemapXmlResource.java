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

package org.yes.cart.web.resource;

import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.yes.cart.utils.ShopCodeContext;
import org.yes.cart.web.support.seo.SitemapXmlService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: denispavlov
 * Date: 13-08-28
 * Time: 3:49 PM
 */
public class SitemapXmlResource extends AbstractResource {

    private final SitemapXmlService sitemapXmlService;

    public SitemapXmlResource(final SitemapXmlService sitemapXmlService) {
        this.sitemapXmlService = sitemapXmlService;
    }

    /** {@inheritDoc} */
    @Override
    protected ResourceResponse newResourceResponse(final Attributes attributes) {

        final SitemapResource rsr = new SitemapResource(getStream(attributes));
        return rsr.getResponse(attributes);
        
    }

    /**
     * Hook to get sitemap stream
     *
     * @param attributes attributes
     *
     * @return sitemap
     */
    protected InputStream getStream(final Attributes attributes) {

        final String shopCode = ShopCodeContext.getShopCode();

        if (shopCode != null) {

            return sitemapXmlService.generateSitemapXmlStream(shopCode);

        }

        return new ByteArrayInputStream(new byte[0]);
    }

    private static class SitemapResource extends ResourceStreamResource {

        SitemapResource(final InputStream stream) {
            super(new AbstractResourceStream() {

                /** {@inheritDoc} */
                @Override
                public String getContentType() {
                    return "text/xml";
                }

                /** {@inheritDoc} */
                @Override
                public InputStream getInputStream() throws ResourceStreamNotFoundException {
                    return stream;
                }

                /** {@inheritDoc} */
                @Override
                public void close() throws IOException {
                    stream.close();
                }
            });
        }

        ResourceResponse getResponse(final Attributes attributes) {
            return newResourceResponse(attributes);
        }

    }

}

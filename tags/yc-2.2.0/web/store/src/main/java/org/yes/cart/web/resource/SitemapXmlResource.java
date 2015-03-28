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

package org.yes.cart.web.resource;

import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.seo.SitemapXmlService;

import java.io.UnsupportedEncodingException;

/**
 * User: denispavlov
 * Date: 13-08-28
 * Time: 3:49 PM
 */
public class SitemapXmlResource extends AbstractDynamicResource {

    private final SitemapXmlService sitemapXmlService;

    public SitemapXmlResource(final SitemapXmlService sitemapXmlService) {
        super("text/xml");
        this.sitemapXmlService = sitemapXmlService;
    }

    /** {@inheritDoc} */
    @Override
    protected byte[] getData(final Attributes attributes) {

        final String shopCode = ShopCodeContext.getShopCode();

        if (shopCode != null) {

            final String sitemap = sitemapXmlService.generateSitemapXml(shopCode);

            if (sitemap != null) {
                try {
                    return sitemap.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    ShopCodeContext.getLog(this).error(e.getMessage(), e);
                }
            }

        }

        return new byte[0];
    }

}

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

package org.yes.cart.web.resource;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.yes.cart.utils.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.service.ContentServiceFacade;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * User: denispavlov
 * Date: 22/05/2018
 * Time: 07:46
 */
public class RobotsTxtResource extends AbstractResource {

    private final String DEFAULT =
            "User-Agent: *\n" +
            "Disallow: \n" +
            "Allow: /\n" +
            "Disallow: /profile\n" +
            "Disallow: /order\n" +
            "Disallow: /orders\n" +
            "Disallow: /wishlist\n" +
            "Disallow: /login\n" +
            "Disallow: /reset\n" +
            "Disallow: /wishlistadd\n" +
            "Disallow: /cart\n" +
            "Disallow: /registration\n" +
            "Disallow: /address\n" +
            "Disallow: /checkout\n" +
            "Disallow: /payment\n" +
            "Disallow: /paymentresult\n" +
            "Disallow: /anetsimresult\n";

    private final ContentServiceFacade contentServiceFacade;

    public RobotsTxtResource(final ContentServiceFacade contentServiceFacade) {
        this.contentServiceFacade = contentServiceFacade;
    }


    /**
     * Hook to get robots.txt stream
     *
     * @param attributes attributes
     *
     * @return robots.txt
     */
    protected InputStream getStream(final Attributes attributes) {

        final Long shopId = ShopCodeContext.getShopId();

        if (shopId != null) {

            final String lang = ApplicationDirector.getShoppingCart().getCurrentLocale();

            String robotsTxt = this.contentServiceFacade.getContentBody("robots.txt", shopId, lang);

            if (StringUtils.isBlank(robotsTxt)) {

                robotsTxt = DEFAULT;

            }

            return new ByteArrayInputStream(robotsTxt.getBytes(StandardCharsets.UTF_8));

        }

        return new ByteArrayInputStream(new byte[0]);
    }


    @Override
    protected ResourceResponse newResourceResponse(final Attributes attributes) {

        final RobotsResource robotsResource = new RobotsResource(getStream(attributes));
        return robotsResource.getResponse(attributes);

    }

    private static class RobotsResource extends ResourceStreamResource {

        RobotsResource(final InputStream stream) {
            super(new AbstractResourceStream() {

                /** {@inheritDoc} */
                @Override
                public String getContentType() {
                    return "text/plain";
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

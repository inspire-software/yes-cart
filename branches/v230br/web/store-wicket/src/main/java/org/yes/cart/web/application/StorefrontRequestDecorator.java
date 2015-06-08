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

package org.yes.cart.web.application;

import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

/**
 * User: denispavlov
 * Date: 13-08-29
 * Time: 8:21 AM
 */
public class StorefrontRequestDecorator extends Request {

    private final Request request;
    private final List<String> supportedLanguages;
    private final Locale locale;

    public StorefrontRequestDecorator(final Request request,
                                      final List<String> supportedLanguages) {
        this.request = request;
        this.supportedLanguages = supportedLanguages;
        if (supportedLanguages.contains(request.getLocale().getLanguage())) {
            locale = request.getLocale();
        } else {
            // if unsupported language need to ensure that we select default
            locale = new Locale(supportedLanguages.get(0));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Url getUrl() {
        return request.getUrl();
    }

    /** {@inheritDoc} */
    @Override
    public Url getClientUrl() {
        return request.getClientUrl();
    }

    /** {@inheritDoc} */
    @Override
    public Url getOriginalUrl() {
        return request.getOriginalUrl();
    }

    /** {@inheritDoc} */
    @Override
    public IRequestParameters getPostParameters() {
        return request.getPostParameters();
    }

    /** {@inheritDoc} */
    @Override
    public IRequestParameters getQueryParameters() {
        return request.getQueryParameters();
    }

    /** {@inheritDoc} */
    @Override
    public IRequestParameters getRequestParameters() {
        return request.getRequestParameters();
    }

    /** {@inheritDoc} */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /** {@inheritDoc} */
    @Override
    public Request cloneWithUrl(final Url url) {
        return request.cloneWithUrl(url);
    }

    /** {@inheritDoc} */
    @Override
    public String getPrefixToContextPath() {
        return request.getPrefixToContextPath();
    }

    /** {@inheritDoc} */
    @Override
    public String getContextPath() {
        return request.getContextPath();
    }

    /** {@inheritDoc} */
    @Override
    public String getFilterPath() {
        return request.getFilterPath();
    }

    /** {@inheritDoc} */
    @Override
    public Charset getCharset() {
        return request.getCharset();
    }

    /** {@inheritDoc} */
    @Override
    public Object getContainerRequest() {
        return request.getContainerRequest();
    }

}

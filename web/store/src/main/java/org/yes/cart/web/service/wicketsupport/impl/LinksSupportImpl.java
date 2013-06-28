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

package org.yes.cart.web.service.wicketsupport.impl;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.shoppingcart.impl.AddSkuToCartEventCommandImpl;
import org.yes.cart.shoppingcart.impl.ChangeCurrencyEventCommandImpl;
import org.yes.cart.shoppingcart.impl.ChangeLocaleCartCommandImpl;
import org.yes.cart.shoppingcart.impl.LogoutCommandImpl;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 13-06-28
 * Time: 8:43 AM
 */
public class LinksSupportImpl implements LinksSupport {

    private final ThreadLocal<Class<Page>> currentHomePage = new ThreadLocal<Class<Page>>();

    /** {@inheritDoc} */
    @Override
    public PageParameters getFilteredCurrentParameters(final PageParameters pageParameters) {
        return WicketUtil.getFilteredRequestParameters(pageParameters);
    }

    /** {@inheritDoc} */
    @Override
    public Link newLink(final String linkId, final PageParameters pageParameters) {
        return new BookmarkablePageLink(linkId, getHomePage(), pageParameters);
    }

    /** {@inheritDoc} */
    @Override
    public Link newCategoryLink(final String linkId, final Object categoryRef) {
        return newBookmarkableLink(linkId, WebParametersKeys.CATEGORY_ID, categoryRef);
    }

    /** {@inheritDoc} */
    @Override
    public Link newCategoryLink(final String linkId, final long categoryRef, final PageParameters pageParameters) {
        return newBookmarkableLink(linkId, WebParametersKeys.CATEGORY_ID, categoryRef,
                getFilteredCurrentParameters(pageParameters));
    }

    /** {@inheritDoc} */
    @Override
    public Link newContentLink(final String linkId, final Object contentRef) {
        return newBookmarkableLink(linkId, WebParametersKeys.CONTENT_ID, contentRef);
    }

    /** {@inheritDoc} */
    @Override
    public Link newContentLink(final String linkId, final Object contentRef, final PageParameters pageParameters) {
        return newBookmarkableLink(linkId, WebParametersKeys.CONTENT_ID, contentRef,
                getFilteredCurrentParameters(pageParameters));
    }

    /** {@inheritDoc} */
    @Override
    public Link newProductLink(final String linkId, final Object productRef) {
        return newBookmarkableLink(linkId, WebParametersKeys.PRODUCT_ID, productRef);
    }

    /** {@inheritDoc} */
    @Override
    public Link newProductLink(final String linkId, final Object productRef, final PageParameters pageParameters) {
        return newBookmarkableLink(linkId, WebParametersKeys.PRODUCT_ID, productRef,
                getFilteredCurrentParameters(pageParameters));
    }

    /** {@inheritDoc} */
    @Override
    public Link newProductSkuLink(final String linkId, final Object productRef) {
        return newBookmarkableLink(linkId, WebParametersKeys.SKU_ID, productRef);
    }

    /** {@inheritDoc} */
    @Override
    public Link newProductSkuLink(final String linkId, final Object productRef, final PageParameters pageParameters) {
        return newBookmarkableLink(linkId, WebParametersKeys.SKU_ID, productRef,
                getFilteredCurrentParameters(pageParameters));
    }

    /** {@inheritDoc} */
    @Override
    public Link newAddToCartLink(final String linkId, final String skuCode, final String quantity, final PageParameters pageParameters) {
        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(AddSkuToCartEventCommandImpl.CMD_KEY, skuCode);
        params.set(AddSkuToCartEventCommandImpl.CMD_QTY_KEY, quantity != null ? quantity : "1");
        return new BookmarkablePageLink(linkId, getHomePage(), params);
    }

    /** {@inheritDoc} */
    @Override
    public Link newLogOffLink(final String linkId, final PageParameters pageParameters) {
        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(LogoutCommandImpl.CMD_KEY, LogoutCommandImpl.CMD_KEY);
        return new BookmarkablePageLink(linkId, getHomePage(), params);
    }

    /** {@inheritDoc} */
    @Override
    public Link newChangeLocaleLink(final String linkId, final String language, final PageParameters pageParameters) {
        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ChangeLocaleCartCommandImpl.CMD_KEY, language);
        return new BookmarkablePageLink(linkId, getHomePage(), params);
    }

    /** {@inheritDoc} */
    @Override
    public Link newChangeCurrencyLink(final String linkId, final String currency, final PageParameters pageParameters) {
        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ChangeCurrencyEventCommandImpl.CMD_KEY, currency);
        return new BookmarkablePageLink(linkId, getHomePage(), params);
    }

    private Class<Page> getHomePage() {
        if (currentHomePage.get() == null) {
            currentHomePage.set((Class<Page>) Application.get().getHomePage());
        }
        return currentHomePage.get();
    }

    /*
     * Generate bookmarkable link for current shop home page. This covers all pa
     */
    private Link newBookmarkableLink(final String linkId, final String uriContext, final Object uri) {
        final PageParameters params = new PageParameters().add(uriContext, uri);
        return new BookmarkablePageLink(linkId, getHomePage(), params);
    }

    /*
     * Generate bookmarkable link for current shop home page. This covers all pa
     */
    private Link newBookmarkableLink(final String linkId, final String uriContext, final Object uri, final PageParameters carried) {
        carried.set(uriContext, uri);
        return new BookmarkablePageLink(linkId, getHomePage(), carried);
    }

}

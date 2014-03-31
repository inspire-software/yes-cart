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
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

/**
 * User: denispavlov
 * Date: 13-06-28
 * Time: 8:43 AM
 */
public class LinksSupportImpl implements LinksSupport {

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
    @SuppressWarnings("unchecked")
    public Link newAddToCartLink(final String linkId, final String skuCode, final String quantity, final PageParameters pageParameters) {
        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_ADDTOCART, skuCode);
        params.set(ShoppingCartCommand.CMD_ADDTOCART_P_QTY, quantity != null ? quantity : "1");
        return new BookmarkablePageLink(linkId, getHomePage(), params);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Link newLogOffLink(final String linkId, final PageParameters pageParameters) {
        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_LOGOUT, ShoppingCartCommand.CMD_LOGOUT);
        return new BookmarkablePageLink(linkId, getHomePage(), params);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Link newChangeLocaleLink(final String linkId, final String language, final Class<? extends Page> pageClass, final PageParameters pageParameters) {
        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_CHANGELOCALE, language);
        return new BookmarkablePageLink(
                linkId,
                pageClass==null?getHomePage():pageClass,
                params);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Link newChangeCurrencyLink(final String linkId, final String currency, final Class<? extends Page> pageClass, final PageParameters pageParameters) {
        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_CHANGECURRENCY, currency);
        return new BookmarkablePageLink(
                linkId,
                pageClass==null?getHomePage():pageClass,
                params);
    }

    @SuppressWarnings("unchecked")
    private Class<Page> getHomePage() {
        return (Class<Page>) Application.get().getHomePage();
    }

    /*
     * Generate bookmarkable link for current shop home page. This covers all pa
     */
    @SuppressWarnings("unchecked")
    private Link newBookmarkableLink(final String linkId, final String uriContext, final Object uri) {
        final PageParameters params = new PageParameters().add(uriContext, uri);
        return new BookmarkablePageLink(linkId, getHomePage(), params);
    }

    /*
     * Generate bookmarkable link for current shop home page. This covers all pa
     */
    @SuppressWarnings("unchecked")
    private Link newBookmarkableLink(final String linkId, final String uriContext, final Object uri, final PageParameters carried) {
        carried.set(uriContext, uri);
        return new BookmarkablePageLink(linkId, getHomePage(), carried);
    }

}

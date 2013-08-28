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

package org.yes.cart.web.page;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.ChangeLocaleCartCommandImpl;
import org.yes.cart.shoppingcart.impl.LogoutCommandImpl;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.service.wicketsupport.WicketSupportFacade;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.entity.decorator.DecoratorFacade;
import org.yes.cart.web.support.i18n.I18NWebSupport;
import org.yes.cart.web.support.util.cookie.ShoppingCartPersister;
import org.yes.cart.web.util.WicketUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Locale;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 10:22 AM
 */
public class AbstractWebPage extends WebPage {

    public static final String FEEDBACK = "feedback";
    public static final String FOOTER = "footer";
    public static final String HEADER = "header";

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @SpringBean(name = StorefrontServiceSpringKeys.CART_PERSISTER)
    private ShoppingCartPersister shoppingCartPersister;

    @SpringBean(name = StorefrontServiceSpringKeys.LANGUAGE_SERVICE)
    private LanguageService languageService;

    @SpringBean(name = StorefrontServiceSpringKeys.I18N_SUPPORT)
    private I18NWebSupport i18NWebSupport;

    @SpringBean(name = StorefrontServiceSpringKeys.DECORATOR_FACADE)
    private DecoratorFacade decoratorFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.WICKET_SUPPORT_FACADE)
    private WicketSupportFacade wicketSupportFacade;


    /** Page title. */
    public static final String PAGE_TITLE="pageTitle";
    /** Meta created. */
    public static final String CREATED="created";
    /** Meta description. */
    public static final String DESCRIPTION="description";
    /** Meta keywords. */
    public static final String KEYWORDS="keywords";





    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public AbstractWebPage(final PageParameters params) {
        super(params);

        final ShoppingCart shoppingCart = ApplicationDirector.getShoppingCart();

        if (getSession().getLocale() == null) {
            getSession().setLocale(new Locale(languageService.getSupportedLanguages(ShopCodeContext.getShopCode()).get(0)));
        }

        if(StringUtils.isBlank(shoppingCart.getCurrentLocale())) {
            new ChangeLocaleCartCommandImpl(
                    null,
                    Collections.singletonMap(ChangeLocaleCartCommandImpl.CMD_KEY, getSession().getLocale().getLanguage()))
                    .execute(shoppingCart);
        }

        getSession().setLocale(new Locale(shoppingCart.getCurrentLocale()));

        setStatelessHint(true);

    }


    protected void onBeforeRender() {

        super.onBeforeRender();
        // need to call super first because components need to perform before render to initialise first
        addOrReplace(new Label(
                PAGE_TITLE,
                getPageTitle()));

        Label desc = new Label(DESCRIPTION,"");
        desc.add( new AttributeAppender("content", getDescription(), " "));
        addOrReplace(desc);

        Label keywords = new Label(KEYWORDS,"");
        keywords.add( new AttributeAppender("content", getKeywords(), " "));
        addOrReplace(keywords);

        /*Label created = new Label(CREATED,"");
        created.add( new AttributeAppender("content", getCreated(), " "));
        addOrReplace(created);*/ //TODO: V2

    }

    /**
     * {@inheritDoc}
     */
    public void processCommands() {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();

        final ShoppingCartCommand command = getShoppingCartCommandFactory().create(WicketUtil.pageParametesAsMap(getPageParameters()));

        if (cart.accept(command) || needToPersists(cart)) {
            //Not sure what the best way to apply changed locale
            if (cart.getCurrentLocale() != null && !getSession().getLocale().getLanguage().equals(cart.getCurrentLocale())) {
                getSession().setLocale(new Locale(cart.getCurrentLocale()));
            }
            getShoppingCartPersister().persistShoppingCart(
                    (HttpServletRequest) getRequest().getContainerRequest(),
                    (HttpServletResponse) getResponse().getContainerResponse(),
                    cart
            );

            if (command != null && LogoutCommandImpl.CMD_KEY.equals(command.getCmdKey())) {
                // Need to remove user from wicket auth
                final IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
                        .getAuthenticationStrategy();
                strategy.remove();
                AuthenticatedWebSession.get().signOut();
            }
        }

        //wft ??? super.onBeforeRender();

    }

    private boolean needToPersists(final ShoppingCart shoppingCart) {
       return (shoppingCart.getProcessingStartDate().getTime() <= shoppingCart.getModifiedDate().getTime());
    }

    /**
     * {@inheritDoc}
     */
    public ShoppingCartCommandFactory getShoppingCartCommandFactory() {
        return shoppingCartCommandFactory;
    }

    /**
     * {@inheritDoc}
     */
    public ShoppingCartPersister getShoppingCartPersister() {
        return shoppingCartPersister;
    }

    /**
     * @return I18n support object
     */
    public I18NWebSupport getI18NSupport() {
        return i18NWebSupport;
    }

    /**
     * @return decorator facade
     */
    public DecoratorFacade getDecoratorFacade() {
        return decoratorFacade;
    }

    /**
     * @return wicket support facade
     */
    public WicketSupportFacade getWicketSupportFacade() {
        return wicketSupportFacade;
    }

    /**
     * Get page title.
     * @return page title
     */
    public IModel<String> getPageTitle() {
        if (ApplicationDirector.getCurrentShop().getSeo() != null
                && StringUtils.isNotBlank(ApplicationDirector.getCurrentShop().getSeo().getTitle())) {
            return new Model<String>(ApplicationDirector.getCurrentShop().getSeo().getTitle());
        }
        return new Model<String>(ApplicationDirector.getCurrentShop().getName());
    }


    /**
     * Get opage description
     * @return description
     */
    public IModel<String> getDescription() {
        if (ApplicationDirector.getCurrentShop().getSeo() != null
                && StringUtils.isNotBlank(ApplicationDirector.getCurrentShop().getSeo().getMetadescription())) {
            return new Model<String>(ApplicationDirector.getCurrentShop().getSeo().getMetadescription());
        }
        return null;
    }

    /**
     * Get keywords.
     * @return keywords
     */
    public IModel<String> getKeywords() {
        if (ApplicationDirector.getCurrentShop().getSeo() != null
                && StringUtils.isNotBlank(ApplicationDirector.getCurrentShop().getSeo().getMetakeywords())) {
            return new Model<String>(ApplicationDirector.getCurrentShop().getSeo().getMetakeywords());
        }
        return null;
    }


}

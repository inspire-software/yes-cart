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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.IRequestCycleProvider;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.https.HttpsConfig;
import org.apache.wicket.protocol.https.HttpsMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;
import org.apache.wicket.request.mapper.MountedMapper;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.file.IResourceFinder;
import org.apache.wicket.util.resource.IResourceStream;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.service.payment.PaymentModulesManager;
import org.yes.cart.web.page.*;
import org.yes.cart.web.page.component.customer.address.CreateEditAddressPage;
import org.yes.cart.web.page.payment.callback.AuthorizeNetSimPaymentOkPage;
import org.yes.cart.web.page.payment.callback.LiqPayReturnUrlPage;
import org.yes.cart.web.page.payment.callback.PayPalReturnUrlPage;
import org.yes.cart.web.page.payment.callback.ResultPage;
import org.yes.cart.web.util.SeoBookmarkablePageParametersEncoder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Main web application.
 *
 * In case if we yes-cart running without apache http server
 *
 *  1. Tomcat is responsible to offload ssl certificate
 *
 *  Main approach to work with https behind proxy is following:
 *
 *  1. Apache http server responsible to offload ssl
 *  2. Tomcat accept only ajp unsecured protocol.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 9:02 AM
 */
public class StorefrontApplication
        extends AuthenticatedWebApplication
        implements
        IResourceFinder,
        IRequestCycleProvider {

    /**
     * Home page mount path.
     */
    public static final String HOME_PAGE_MOUNT_PATH = "/shop";

    private static Map<String, MultiWebApplicationPath> resourceResolvers =
            new ConcurrentHashMap<String, MultiWebApplicationPath>();

    private ThreadLocal<MultiWebApplicationPath> resolver = new ThreadLocal<MultiWebApplicationPath>();

    private SpringComponentInjector springComponentInjector;




    /**
     * Lazy getter of spring injector.
     *
     * @return
     */
    public SpringComponentInjector getSpringComponentInjector() {
        if (springComponentInjector == null) {
            this.springComponentInjector = new SpringComponentInjector(this);
        }
        return springComponentInjector;
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    public Class<HomePage> getHomePage() {
        return HomePage.class;
    }



    /**
     * {@inheritDoc}
     */
    protected void init() {

        super.init();

        // dynamic shop markup support via specific refource finder
        getResourceSettings().setResourceFinder(this);

        setRequestCycleProvider(this);

        // wicket-groovy dynamic pages support
        //getApplicationSettings().setClassResolver(new GroovyClassResolver(this));

        getMarkupSettings().setMarkupFactory(new MultiMarkupFactory());
        getMarkupSettings().setCompressWhitespace(true);
        getMarkupSettings().setStripWicketTags(true); // true remove wicket:tags in development mode
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        //getMarkupSettings().setDefaultAfterDisabledLink("");
        //getMarkupSettings().setDefaultBeforeDisabledLink("");
        //getMarkupSettings().setAutomaticLinking(false);

        getComponentInstantiationListeners().add(getSpringComponentInjector());

        getRequestCycleListeners().add(new StorefrontRequestCycleListener());

        mountPages();

        if ("true".equalsIgnoreCase(getInitParameter("secureMode"))) {

            final HttpsConfig httpsConfig = new HttpsConfig(
                    Integer.valueOf((String) ObjectUtils.defaultIfNull(getInitParameter("unsecurePort"), "8080")),
                    Integer.valueOf((String) ObjectUtils.defaultIfNull(getInitParameter("securePort"), "8443"))
            );

            final HttpsMapper httpsMapper = new HttpsMapper(getRootRequestMapper(), httpsConfig);

            setRootRequestMapper(httpsMapper);

        }

    }

    @Override
    public Session newSession(Request request, Response response) {
        return super.newSession(request, response);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * {@inheritDoc}
     */
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return StorefrontWebSession.class;
    }

    /**
     * {@inheritDoc}
     */
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    /**
     * Mount pages to particular pathes.
     */
    private void mountPages() {
        final ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        final SeoBookmarkablePageParametersEncoder encoder = ctx.getBean(
                "seoBookmarkablePageParametersEncoder",
                SeoBookmarkablePageParametersEncoder.class);
        final PaymentModulesManager paymentModulesManager = ctx.getBean(
                "paymentModulesManager",
                PaymentModulesManager.class);
        mount(
                new MountedMapper(
                        "/",
                        HomePage.class,
                        encoder
                )
        );

        mount(
                new MountedMapper(
                        "/cart",
                        ShoppingCartPage.class,
                        encoder
                )
        );

        mount(
                new MountedMapper(
                        "/selfcare",
                        CustomerSelfCarePage.class
                )
        );

        mount(
                new MountedMapper(
                        "/faq",
                        FaqPage.class
                )
        );

        mount(
                new MountedMapper(
                        "/contact",
                        ContactPage.class
                )
        );

        mount(
                new MountedMapper(
                        "/login",
                        LoginPage.class
                )
        );

        mount(
                new MountedMapper(
                        "/registration",
                        RegistrationPage.class
                )
        );

        mount(
                new MountedMapper(
                        "/address",
                        CreateEditAddressPage.class
                )
        );

        mount(
                new MountedMapper(
                        "/checkout",
                        CheckoutPage.class
                )
        );

        mount(
                new MountedMapper(
                        "/payment",
                        PaymentPage.class
                )
        );


        mountPaymentGatewayCallBackPages(paymentModulesManager);

    }


    /**
     * Mount pages for gateways, that works via redirect to
     * gateway page and back.
     * @param paymentModulesManager to determinate what module is active
     */
    private void mountPaymentGatewayCallBackPages(final PaymentModulesManager paymentModulesManager) {

        mount(
                new MountedMapper(
                        "/paymentresult",
                        ResultPage.class
                )
        );

        final List<PaymentGatewayDescriptor> allowedPaymentGateways = paymentModulesManager.getPaymentGatewaysDescriptors(false);




        if (isPaymentGatewayAllowed(allowedPaymentGateways, "authorizeNetSimPaymentGatewayLabel")) {
            mount(
                    new MountedMapper(
                            "/anetsim",
                            AuthorizeNetSimPaymentOkPage.class
                    )
            );
        }


        if (isPaymentGatewayAllowed(allowedPaymentGateways, "payPalExpressPaymentGatewayLabel")) {
            mount(
                    new MountedMapper(
                            "/paypallreturn",
                            PayPalReturnUrlPage.class
                    )
            );
        }

        if (isPaymentGatewayAllowed(allowedPaymentGateways, "liqPayPaymentGatewayLabel")) {
            mount(
                    new MountedMapper(
                            "/liqpayreturn",
                            LiqPayReturnUrlPage.class
                    )
            );

        }



    }

    private boolean isPaymentGatewayAllowed(final List<PaymentGatewayDescriptor> allowedPaymentGateways, final String gatewayLabel) {
        return CollectionUtils.exists(allowedPaymentGateways, new Predicate() {
            public boolean evaluate(Object o) {
                return ((PaymentGatewayDescriptor) o).getLabel().equals(gatewayLabel);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public IResourceStream find(final Class<?> aClass, final String s) {
        return resolver.get().find(aClass, s);
    }

    /**
     * {@inheritDoc}
     */
    public RequestCycle get(final RequestCycleContext context) {
        resolver.set(getMultiWebApplicationPath());
        return new RequestCycle(context);
    }


    /**
     * Get exising or create new {@link MultiWebApplicationPath} for new request
     *
     * @return instance of {@link MultiWebApplicationPath}
     */
    private MultiWebApplicationPath getMultiWebApplicationPath() {
        MultiWebApplicationPath MultiWebApplicationPath = resourceResolvers.get(ApplicationDirector.getCurrentShop().getCode());
        if (MultiWebApplicationPath == null) { //first request to this shop, lets create a resolver
            MultiWebApplicationPath = new MultiWebApplicationPath(getServletContext());

            MultiWebApplicationPath.add(ApplicationDirector.getCurrentShop().getFspointer() + "/markup");  // shop specific markup folder
            MultiWebApplicationPath.add("default/markup"); // default place to search resource

            resourceResolvers.put(ApplicationDirector.getCurrentShop().getCode(), MultiWebApplicationPath);
        }
        return MultiWebApplicationPath;
    }

}

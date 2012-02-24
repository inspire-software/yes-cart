package org.yes.cart.web.application;

import org.apache.wicket.IRequestCycleProvider;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
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
import org.yes.cart.web.page.*;
import org.yes.cart.web.page.component.customer.address.CreateEditAddressPage;
import org.yes.cart.web.page.payment.callback.AuthorizeNetSimPaymentOkPage;
import org.yes.cart.web.page.payment.callback.LiqPayReturnUrlPage;
import org.yes.cart.web.page.payment.callback.PayPalReturnUrlPage;
import org.yes.cart.web.page.payment.callback.ResultPage;
import org.yes.cart.web.util.SeoBookmarkablePageParametersEncoder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
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
     * Create and set resource locators.
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

        mountPages();

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


        mountPaymentGatewayCallBackPages();

    }


    /**
     * Mount pages for gateways, that works via redirect to
     * gateway page and back.
     */
    private void mountPaymentGatewayCallBackPages() {

        mount(
                new MountedMapper(
                        "/paymentresult",
                        ResultPage.class
                )
        );


        //TODO mount only available payment gateways
        mount(
                new MountedMapper(
                        "/anetsim",
                        AuthorizeNetSimPaymentOkPage.class
                )
        );

        mount(
                new MountedMapper(
                        "/paypallreturn",
                        PayPalReturnUrlPage.class
                )
        );

        mount(
                new MountedMapper(
                        "/liqpayreturn",
                        LiqPayReturnUrlPage.class
                )
        );


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

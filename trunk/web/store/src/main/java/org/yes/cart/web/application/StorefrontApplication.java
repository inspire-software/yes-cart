package org.yes.cart.web.application;

import org.apache.wicket.IRequestCycleProvider;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
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
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.util.SeoBookmarkablePageParametersEncoder;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 9:02 AM
 */
public class StorefrontApplication extends WebApplication implements IResourceFinder, IRequestCycleProvider {

    /**
     * Home page mount path.
     */
    public static final String HOME_PAGE_MOUNT_PATH = "/shop";

    private static Map<String, MultiWebApplicationPath> resourceResolvers =
            new ConcurrentHashMap<String, MultiWebApplicationPath>();

    private ThreadLocal<MultiWebApplicationPath> resolver = new ThreadLocal<MultiWebApplicationPath>();

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

        getComponentInstantiationListeners().add(new SpringComponentInjector(this));

        mountPages();



    }

    /**
     * Mount pages to particular pathes.
     */
    private void mountPages() {
        final ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        final CategoryService categoryService = ctx.getBean("categoryService", CategoryService.class);
        final ProductService productService = ctx.getBean("productService", ProductService.class);

        mount(
                new MountedMapper(
                        "/",
                        HomePage.class,
                        new SeoBookmarkablePageParametersEncoder(
                                categoryService,
                                productService
                        )
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

            MultiWebApplicationPath.add(ApplicationDirector.getCurrentShop().getFspointer()+"/markup");  // shop specific markup folder
            MultiWebApplicationPath.add("default/markup"); // default place to search resource


            resourceResolvers.put(ApplicationDirector.getCurrentShop().getCode(), MultiWebApplicationPath);
        }
        return MultiWebApplicationPath;
    }

}

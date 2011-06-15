package org.yes.cart.web.filter;


import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.support.constants.WebParametersKeys;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//import javax.el.ELContext;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 11:56:24 AM
 */
public abstract class AbstractFilter {

    private FilterConfig filterConfig = null;


    /**
     */
    public AbstractFilter() {
    }

    /**
     * {@inheritDoc}
     */
    public final void doFilter(final ServletRequest servletRequest,
                               final ServletResponse servletResponse,
                               final FilterChain filterChain) throws IOException, ServletException {
        final ServletRequest passRequest = doBefore(servletRequest, servletResponse);
        if (passRequest == null) {
            return;
        }
        filterChain.doFilter(passRequest, servletResponse);
        doAfter(passRequest, servletResponse);
    }

    /**
     * Do all before next item in chain.
     *
     * @param servletRequest  the request
     * @param servletResponse the response
     * @return request to pass onto chain (return null to terminate chain).
     * @throws IOException      as with filter
     * @throws ServletException as with filter
     */
    public abstract ServletRequest doBefore(final ServletRequest servletRequest,
                                            final ServletResponse servletResponse) throws IOException, ServletException;

    /**
     * Do all before next item in chain.
     *
     * @param servletRequest  the request
     * @param servletResponse the response
     * @throws IOException      as with filter
     * @throws ServletException as with filter
     */
    public abstract void doAfter(final ServletRequest servletRequest,
                                 final ServletResponse servletResponse) throws IOException, ServletException;

    /**
     * {@inheritDoc}
     */
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        filterConfig = null;
    }

    /**
     * @return filter config
     */
    protected FilterConfig getFilterConfig() {
        return filterConfig;
    }


    /**
     * Get  FacesContext for usage in filters.
     *
     * @param servletRequest  http request
     * @param servletResponse http responce
     * @return FacesContext
     */
    protected FacesContext getFacesContext(ServletRequest servletRequest, ServletResponse servletResponse) {
        return getFacesContext((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
    }

    /**
     * Get  FacesContext for usage in filters.
     *
     * @param request  http request
     * @param response http responce
     * @return FacesContext
     */
    protected FacesContext getFacesContext(HttpServletRequest request, HttpServletResponse response) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {

            FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

            facesContext = contextFactory.getFacesContext(request.getSession().getServletContext(), request, response, lifecycle);

            // Set using our inner class
            InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);

            // set a new viewRoot, otherwise context.getViewRoot returns null
            UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "");
            facesContext.setViewRoot(view);
        }
        return facesContext;
    }


    protected Application getApplication(FacesContext facesContext) {
        return facesContext.getApplication();
    }

    /**
     * Get managed bean.
     *
     * @param facesContext faces context
     * @param beanName     bean name
     * @return managed bean
     */
    protected Object getManagedBean(final FacesContext facesContext, final String beanName) {
        /*ELContext elContext = FacesContext.getCurrentInstance().getELContext();
NeededBean neededBean = (NeededBean) FacesContext.getCurrentInstance().getApplication()
    .getELResolver().getValue(elContext, null, "neededBean"); */
        return getApplication(facesContext).getVariableResolver().resolveVariable(facesContext, beanName);
    }

    // You need an inner class to be able to call FacesContext.setCurrentInstance
    // since it's a protected method
    private abstract static class InnerFacesContext extends FacesContext {
        protected static void setFacesContextAsCurrentInstance(FacesContext facesContext) {
            FacesContext.setCurrentInstance(facesContext);
        }
    }


    protected ShoppingCart getShoppingCart(ServletRequest servletRequest, ServletResponse servletResponse) {
        final FacesContext facesContext = getFacesContext(servletRequest, servletResponse);
        return (ShoppingCart)
                getManagedBean(facesContext, WebParametersKeys.SESSION_SHOPPING_CART);
    }


}

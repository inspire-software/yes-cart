package org.yes.cart.web.application.view;

import com.sun.faces.application.view.MultiViewHandler;
import com.sun.faces.config.WebConfiguration;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.WebParametersKeys;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import java.net.MalformedURLException;

/**
 * Responsible to resolve view location, depends from server name.
 *
 * @see org.yes.cart.web.filter.ShopResolverFilter#getModifiedRequest(javax.servlet.ServletRequest, org.yes.cart.domain.entity.Shop) that
 *      do the same for resources and jsp.
 *      <p/>
 *      User: Igor Azarny iazarny@yahoo.com
 *      Date: 6/18/11
 *      Time: 1:25 PM
 */
public class MultiStoreViewHandler extends MultiViewHandler {

    private String[] msConfiguredExtensions;
    private boolean msextensionsSet;

    public MultiStoreViewHandler() {
        super();
        final WebConfiguration config = WebConfiguration.getInstance();
        msConfiguredExtensions = config.getOptionValue(WebConfiguration.WebContextInitParameter.DefaultSuffix, " ");
        msextensionsSet = config.isSet(WebConfiguration.WebContextInitParameter.DefaultSuffix);
    }

    /**
     * <p>Adjust the viewID per the requirements of {@link #renderView}.</p>
     *
     * @param context current {@link javax.faces.context.FacesContext}
     * @param viewId  incoming view ID
     * @return the view ID with an altered suffix mapping (if necessary)
     */
    protected String convertViewId(final FacesContext context, final String viewId) {
        /*if (PhaseId.RENDER_RESPONSE.equals(context.getCurrentPhaseId())) {
            System.out.println(">>>>>>> pass  viewId = " + viewId + " to super ");
            return super.convertViewId(context, viewId);
        } else {
            if (context != null && context.getCurrentPhaseId() != null) {
                System.out.println(">>>>>>>>>>>>>>>>  " + viewId + " on phase " + context.getCurrentPhaseId().toString());
            }
            return msconvertViewId(context, viewId);
        }  */
        //return super.convertViewId(context, viewId);
        return msconvertViewId(context, viewId);
    }


    /**
     * <p>Adjust the viewID per the requirements of {@link #renderView}.</p>
     *
     * @param context current {@link javax.faces.context.FacesContext}
     * @param viewId  incoming view ID
     * @return the view ID with an altered suffix mapping (if necessary)
     */
    protected String msconvertViewId(FacesContext context, String viewId) {

        // if the viewId doesn't already use the above suffix,
        // replace or append.
        int extIdx = viewId.lastIndexOf('.');
        int length = viewId.length();
        StringBuilder buffer = new StringBuilder(length);

        for (String ext : msConfiguredExtensions) {
            if (viewId.endsWith(ext)) {
                return viewId;
            }

            appendOrReplaceExtension(viewId, ext, length, extIdx, buffer);

            String convertedViewId = buffer.toString();
            try {
                final Shop shop = getShop(context);
                final String newViewId;
                if (shop == null || StringUtils.isBlank(viewId) || viewId.startsWith(shop.getMarkupFolder())) {
                    newViewId = convertedViewId;
                } else {
                    newViewId = shop.getMarkupFolder() + convertedViewId;
                }
                System.out.println(">>>>> newViewId = " + newViewId + " will return " + convertedViewId);
                if (context.getExternalContext().getResource(newViewId) != null) {
                    System.out.println(">>> ok ");
                    // RELEASE_PENDING (rlubke,driscoll) cache the lookup
                    return convertedViewId;
                }
            } catch (MalformedURLException e) {
                /*if (logger.isLoggable(Level.SEVERE)) {
                    logger.log(Level.SEVERE,
                               e.toString(),
                               e);
                } */
                //TODO
                e.printStackTrace();
            }
        }

        // unable to find any resource match that the default ViewHandler
        // can deal with.  Fall back to legacy (JSF 1.2) id conversion.
        return legacyConvertViewId(viewId, length, extIdx, buffer);
    }

    // Utility method used by viewId conversion.  Appends the extension
    // if no extension is present.  Otherwise, replaces the extension.
    private void appendOrReplaceExtension(String viewId,
                                          String ext,
                                          int length,
                                          int extIdx,
                                          StringBuilder buffer) {

        buffer.setLength(0);
        buffer.append(viewId);

        if (extIdx != -1) {
            buffer.replace(extIdx, length, ext);
        } else {
            // no extension in the provided viewId, append the suffix
            buffer.append(ext);
        }
    }

    private String legacyConvertViewId(String viewId,
                                       int length,
                                       int extIdx,
                                       StringBuilder buffer) {

        // In 1.2, the viewId was converted by replacing the extension
        // with the single extension specified by javax.faces.DEFAULT_SUFFIX,
        // which defaulted to ".jsp".  In 2.0, javax.faces.DEFAULT_SUFFIX
        // may specify multiple extensions.  If javax.faces.DEFAULT_SUFFIX is
        // explicitly set, we honor it and pick off the first specified
        // extension.  If javax.faces.DEFAULT_SUFFIX is not explicitly set,
        // we honor the default 1.2 behavior and use ".jsp" as the suffix.

        String ext = (msextensionsSet &&
                !(msConfiguredExtensions.length == 0)) ?
                msConfiguredExtensions[0] : ".jsp";

        if (viewId.endsWith(ext)) {
            return viewId;
        }

        appendOrReplaceExtension(viewId, ext, length, extIdx, buffer);

        return buffer.toString();
    }

    /**
     * Get the shop.
     *
     * @param facesContext current {@link javax.faces.context.FacesContext}
     * @return {@link Shop}
     */
    public Shop getShop(final FacesContext facesContext) {
        final Application application = facesContext.getApplication();
        final ShoppingCart shoppingCart = (ShoppingCart) application.getVariableResolver().resolveVariable(
                facesContext, WebParametersKeys.SESSION_SHOPPING_CART);
        final ApplicationDirector applicationDirector = (ApplicationDirector) application.getVariableResolver().resolveVariable(
                facesContext, WebParametersKeys.APPLICATION_DYNAMYC_CACHE);
        return applicationDirector.getShopById(shoppingCart.getShoppingContext().getShopId());
    }

}

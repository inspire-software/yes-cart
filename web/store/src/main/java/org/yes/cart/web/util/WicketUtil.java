package org.yes.cart.web.util;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.yes.cart.web.support.constants.WebParametersKeys;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 9:42 PM
 */
public class WicketUtil {


    private static final int DEFAULT_ITEMS = 10;

    /**
     * Temporally fields will be removed from parameter maps
     */
    private static List<String> cmdKeys = new ArrayList<String>();

    private static final PageParametersEncoder pageParametersEncoder = new PageParametersEncoder();

    public void setCmdKeys(final List<String> cmdKeys) {
        WicketUtil.cmdKeys = cmdKeys;
    }

    /**
     * Get  {@link HttpServletRequest} .
     *
     * @return {@link HttpServletRequest}
     */
    public static HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) ((WebRequest) RequestCycle.get().getRequest()).getContainerRequest();
    }

    public static PageParameters getPageParametes() {
        return pageParametersEncoder.decodePageParameters(RequestCycle.get().getRequest());
    }

    /**
     * Transform wicket 1.5 page parameter to more traditional map.  Expensive operation.
     *
     * @param pageParameters given parameters to transform.
     * @return parameters transformed to map
     */
    public static Map<String, Object> pageParametesAsMap(final PageParameters pageParameters) {

        final Map<String, Object> map = new LinkedHashMap<String, Object>(pageParameters.getNamedKeys().size());
        for (String key : pageParameters.getNamedKeys()) {
            map.put(
                    key,
                    pageParameters.getValues(key)
            );
        }

        return map;

    }


    /**
     * Get the filtered, from commands, request parameters,
     * that not contains given set of request parameter names
     *
     * @param parameters original request parameters
     * @return new filtered {@link LinkedHashMap}
     */
    public static PageParameters getFilteredRequestParameters(final PageParameters parameters) {

        if (parameters == null) {
            return new PageParameters();
        } else {
            final PageParameters rez = new PageParameters(parameters);
            for (String paramName : cmdKeys) {
                rez.remove(paramName);
            }
            return rez;
        }
    }

    /**
     * Get the filtered request parameters, that not contains given set of request parameter names
     *
     * @param pageParameters original request parameters
     * @param nameFilter     set of parameter name to remove
     * @return new filtered {@link PageParameters}
     */
    public static PageParameters getFilteredRequestParameters(
            final PageParameters pageParameters,
            final Collection<String> nameFilter) {

        final PageParameters rez = new PageParameters(pageParameters);
        for (String paramName : cmdKeys) {
            rez.remove(paramName);
        }
        for (String paramName : nameFilter) {
            rez.remove(paramName);
        }
        return rez;
    }

    /**
     * Get the retained request parameters, that not contains given set of request parameter names
     *
     * @param pageParameters original request parameters
     * @param nameFilter     set of parameter name to retaine
     * @return new filtered {@link PageParameters}
     */
    public static PageParameters getRetainedRequestParameters(
            final PageParameters pageParameters,
            final Collection<String> nameFilter) {
        final PageParameters rez = new PageParameters(pageParameters);
        for (String paramName : cmdKeys) {
            rez.remove(paramName);
        }
        for (String keyName : rez.getNamedKeys()) {
            if (!nameFilter.contains(keyName)) {
                rez.remove(keyName);

            }
        }
        return rez;
    }


    /**
     * Get selected items on page.
     *
     * @param pageParameters     page parameters from http request
     * @param itemsPerPageValues allowed values
     * @return selected items per page value if it in allowed list otherwise {@see NavigationUtil.DEFAULT_ITEMS} vaule
     */
    public static int getSelectedItemsPerPage(final PageParameters pageParameters, final List<String> itemsPerPageValues) {
        int result = DEFAULT_ITEMS;
        if (itemsPerPageValues != null && !itemsPerPageValues.isEmpty()) {
            String selectedItemPerPage = pageParameters.get(WebParametersKeys.QUANTITY).toString();
            if (!itemsPerPageValues.contains(selectedItemPerPage)) {
                selectedItemPerPage = itemsPerPageValues.get(0);
            }
            result = NumberUtils.toInt(selectedItemPerPage);
        }
        return result;
    }


}

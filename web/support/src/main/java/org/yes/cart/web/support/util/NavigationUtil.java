package org.yes.cart.web.support.util;

import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 9:17:56 AM
 * <p/>
 * FilteredNavigationUtil responsible for:
 * get the lucene queries for filtered navigation from cookies;
 * add filtered navigation cookies.
 */
public class NavigationUtil {


    private static final int DEFAULT_ITEMS = 10;

    /**
     * Temporally fields will be removed from parameter maps
     */
    private static List<String> cmdKeys = new ArrayList<String>();

    public void setCmdKeys(final List<String> cmdKeys) {
        NavigationUtil.cmdKeys = cmdKeys;
    }

    /**
     * Get the filtered request parameters, that not contains given set of request parameter names
     *
     * @param parameters original request parameters
     * @return new filtered {@link LinkedHashMap}
     * @see NavigationUtil#getRetainedRequestParameters(Map, java.util.Collection) oposite opetation
     */
    public static LinkedHashMap getFilteredRequestParameters(final Map<String, ?> parameters) {

        final LinkedHashMap params;

        if (parameters == null) {
            params = new LinkedHashMap();
        } else {
            params = new LinkedHashMap(parameters);
            params.keySet().removeAll(cmdKeys);
        }

        return params;

    }


    /**
     * Get the filtered request parameters, that not contains given set of request parameter names
     *
     * @param pageParameters original request parameters
     * @param nameFilter     set of parameter name to remove
     * @return new filtered {@link LinkedHashMap}
     * @see NavigationUtil#getRetainedRequestParameters(Map, java.util.Collection) oposite opetation
     */
    public static LinkedHashMap getFilteredRequestParameters(
            final Map pageParameters,
            final Collection<String> nameFilter) {
        final LinkedHashMap params = new LinkedHashMap(pageParameters);
        params.keySet().removeAll(nameFilter);
        params.keySet().removeAll(cmdKeys);
        return params;
    }

    /**
     * Get the retained request parameters, that not contains given set of request parameter names
     *
     * @param pageParameters original request parameters
     * @param nameFilter     set of parameter name to retaine
     * @return new filtered {@link LinkedHashMap}
     * @see NavigationUtil#getFilteredRequestParameters(Map, java.util.Collection) oposite opetation
     */
    public static LinkedHashMap getRetainedRequestParameters(
            final Map pageParameters,
            final Collection<String> nameFilter) {
        final LinkedHashMap params = new LinkedHashMap(pageParameters);
        params.keySet().retainAll(nameFilter);
        params.keySet().removeAll(cmdKeys);
        return params;
    }

    /**
     * Is cmd present in given string.
     *
     * @param str given string
     * @return true if one of the configured cmd present in given string.
     */
    public static boolean isCmdPresent(final String str) {
        for (String cmd : cmdKeys) {
            if (str.indexOf(cmd) > -1) {
                return true;
            }
        }
        return false;
    }


    /**
     * Get selected items on page.
     *
     * @param pageParameters     page parameters from http request
     * @param itemsPerPageValues allowed values
     * @return selected items per page value if it in allowed list otherwise {@see NavigationUtil.DEFAULT_ITEMS} vaule
     */
    public static int getSelectedItemsPerPage(final Map pageParameters, final List<String> itemsPerPageValues) {
        int result = DEFAULT_ITEMS;
        if (itemsPerPageValues != null && !itemsPerPageValues.isEmpty()) {
            String selectedItemPerPage = (String) pageParameters.get(WebParametersKeys.QUANTITY);
            if (!itemsPerPageValues.contains(selectedItemPerPage)) {
                selectedItemPerPage = itemsPerPageValues.get(0);
            }
            result = NumberUtils.toInt(selectedItemPerPage);
        }
        return result;
    }


}

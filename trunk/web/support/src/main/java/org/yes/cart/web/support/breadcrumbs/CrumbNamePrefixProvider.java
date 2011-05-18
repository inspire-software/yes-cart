package org.yes.cart.web.support.breadcrumbs;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 8:38:04 AM
 */
public interface CrumbNamePrefixProvider {

    /**
     * Get the localized name of breadcrumb for given key.
     *
     * @param key given key
     * @return localized prefix if localization found, otherwise key.
     */
    String getLinkNamePrefix(String key);

    /**
     * Get adapted value for link label
     *
     * @param key   given key
     * @param value to adapt
     * @return localized adapted value if localization found, otherwise key.
     */
    String getLinkName(String key, String value);

}

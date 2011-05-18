package org.yes.cart.web.support.breadcrumbs;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Represents a Breadcrumb navigation object.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 9:33:59
 */
public class Crumb implements Serializable {

    private final String name;

    private final LinkedHashMap<java.lang.String, ?> crumbLinkParameters;

    private LinkedHashMap<java.lang.String, ?> removeCrumbLinkParameters;

    public String getName() {
        return name;
    }

    public LinkedHashMap<java.lang.String, ?> getCrumbLinkParameters() {
        return crumbLinkParameters;
    }

    public LinkedHashMap<java.lang.String, ?> getRemoveCrumbLinkParameters() {
        return removeCrumbLinkParameters;
    }

    public void setRemoveCrumbLinkParameters(LinkedHashMap<String, ?> removeCrumbLinkParameters) {
        this.removeCrumbLinkParameters = removeCrumbLinkParameters;
    }

    public Crumb(final String name, final LinkedHashMap<String, ?> crumbLinkParameters) {
        this.name = name;
        this.crumbLinkParameters = crumbLinkParameters;
    }

    public Crumb(final String name,
                 final LinkedHashMap<java.lang.String, ?> crumbLinkParameters,
                 final LinkedHashMap<java.lang.String, ?> removeCrumbLinkParameters) {
        this.name = name;
        this.crumbLinkParameters = crumbLinkParameters;
        this.removeCrumbLinkParameters = removeCrumbLinkParameters;
    }

}

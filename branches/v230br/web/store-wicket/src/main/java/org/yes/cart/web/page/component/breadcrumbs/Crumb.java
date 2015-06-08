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

package org.yes.cart.web.page.component.breadcrumbs;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.Serializable;

/**
 * Represents a Breadcrumb navigation object.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 9:33:59
 */
public class Crumb implements Serializable {

    private final String key;

    private final String name;

    private final Object displayName;

    private final PageParameters crumbLinkParameters;

    private PageParameters removeCrumbLinkParameters;

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Object getDisplayName() {
        return displayName;
    }

    public PageParameters getCrumbLinkParameters() {
        return crumbLinkParameters;
    }

    public PageParameters getRemoveCrumbLinkParameters() {
        return removeCrumbLinkParameters;
    }

    public void setRemoveCrumbLinkParameters(PageParameters removeCrumbLinkParameters) {
        this.removeCrumbLinkParameters = removeCrumbLinkParameters;
    }

    public Crumb(final String key,
                 final String name,
                 final Object displayName,
                 final PageParameters crumbLinkParameters,
                 final PageParameters removeCrumbLinkParameters) {
        this.key = key;
        this.name = name;
        this.displayName = displayName;
        this.crumbLinkParameters = crumbLinkParameters;
        this.removeCrumbLinkParameters = removeCrumbLinkParameters;
    }

}

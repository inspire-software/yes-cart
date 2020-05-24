/*
 * Copyright 2009 Inspire-Software.com
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

import org.apache.wicket.Component;
import org.yes.cart.web.support.i18n.I18NWebSupport;

import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 02/08/2018
 * Time: 22:02
 */
public class CrumbNameFormatter implements Serializable {

    private boolean useKeyOnly;
    private boolean useKeyAsPrefix;
    private boolean useNameAsKey;
    private boolean useKeyAndValueAsKey;
    private boolean useValueAsKey;

    public void setUseKeyOnly(final boolean useKeyOnly) {
        this.useKeyOnly = useKeyOnly;
    }

    public void setUseKeyAsPrefix(final boolean useKeyAsPrefix) {
        this.useKeyAsPrefix = useKeyAsPrefix;
    }

    public void setUseNameAsKey(final boolean useNameAsKey) {
        this.useNameAsKey = useNameAsKey;
    }

    public void setUseKeyAndValueAsKey(final boolean useKeyAndValueAsKey) {
        this.useKeyAndValueAsKey = useKeyAndValueAsKey;
    }

    public void setUseValueAsKey(final boolean useValueAsKey) {
        this.useValueAsKey = useValueAsKey;
    }

    String format(final Component component, final Crumb crumb, final I18NWebSupport i18NWebSupport, final String lang) {

        final StringBuilder name = new StringBuilder();
        if (this.useKeyOnly) {
            try {
                name.append(component.getString(crumb.getKey()));
            } catch (Exception exp) {
                // catch for wicket unknown resource key exception
            }

        } else {
            if (this.useKeyAsPrefix) {
                try {
                    name.append(component.getString(crumb.getKey())).append("::");
                } catch (Exception exp) {
                    // catch for wicket unknown resource key exception
                }
            }

            if (this.useNameAsKey) {
                try {
                    name.append(component.getString(crumb.getName()));
                } catch (Exception exp) {
                    // catch for wicket unknown resource key exception
                    name.append(crumb.getName());
                }
            } else if (this.useKeyAndValueAsKey) {
                try {
                    name.append(component.getString(crumb.getKey() + crumb.getValue()));
                } catch (Exception exp) {
                    // catch for wicket unknown resource key exception
                    name.append(crumb.getKey() + crumb.getName());
                }
            } else if (this.useValueAsKey) {
                try {
                    name.append(component.getString(crumb.getValue()));
                } catch (Exception exp) {
                    // catch for wicket unknown resource key exception
                    name.append(crumb.getValue());
                }
            } else {

                name.append(i18NWebSupport.getFailoverModel(crumb.getDisplayName(), crumb.getName()).getValue(lang));

            }
        }

        return name.toString();
    }

}

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

package org.yes.cart.web.theme.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.util.ClassProvider;
import org.apache.wicket.util.IProvider;
import org.yes.cart.web.application.ApplicationDirector;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-03-21
 * Time: 10:28 AM
 */
public class ThemePageProvider extends ClassProvider<IRequestablePage> {

    private final Map<String, IProvider<Class<IRequestablePage>>> pages = new HashMap<String, IProvider<Class<IRequestablePage>>>();

    public ThemePageProvider(final Map<String, Class<IRequestablePage>> pages) {
        super(null);
        for (final Map.Entry<String, Class<IRequestablePage>> entry : pages.entrySet()) {
            this.pages.put(entry.getKey(), ClassProvider.of(entry.getValue()));
        }
    }

    /** {@inheritDoc} */
    public Class<IRequestablePage> get() {
        String theme = ApplicationDirector.getCurrentTheme();
        if (StringUtils.isEmpty(theme) || !this.pages.containsKey(theme)) {
            theme = "default";
        }
        return this.pages.get(theme).get();
    }
}

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

package org.yes.cart.service.theme.templates.impl;

import java.io.InputStream;

/**
 * User: inspiresoftware
 * Date: 25/10/2020
 * Time: 09:29
 */
public class TestThemeRepositoryServiceServletContextImpl extends ThemeRepositoryServiceServletContextImpl {

    @Override
    public InputStream getSource(final String path) {

        if (path.contains("/mail/")) {
            return super.getSource("../../../../../theme/mail/src/main/resources/" + path);
        } else if (path.contains("/reports/")) {
            return super.getSource("../../../../../theme/reports/src/main/resources/" + path);
        }
        return super.getSource(path);
        
    }
}

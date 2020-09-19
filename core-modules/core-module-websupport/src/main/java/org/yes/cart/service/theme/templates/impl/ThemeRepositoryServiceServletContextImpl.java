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

import org.springframework.web.context.ServletContextAware;
import org.yes.cart.service.theme.templates.ThemeRepositoryService;

import javax.servlet.ServletContext;
import java.io.InputStream;

/**
 * Date: 19/09/2020
 * Time: 14:18
 */
public class ThemeRepositoryServiceServletContextImpl implements ThemeRepositoryService, ServletContextAware {

    private ServletContext servletContext;

    /** {@inheritDoc} */
    @Override
    public InputStream getSource(final String path) {
        return servletContext.getResourceAsStream(path);
    }

    /** {@inheritDoc} */
    @Override
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}

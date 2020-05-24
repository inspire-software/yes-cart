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

package org.yes.cart.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.config.ConfigurationRegistry;
import org.yes.cart.service.domain.SecurityAccessControlService;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: denispavlov
 * Date: 21/10/2019
 * Time: 11:15
 */
public class SecurityAccessControlFilter extends AbstractFilter implements ConfigurationRegistry<String, SecurityAccessControlService<ServletRequest>> {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityAccessControlFilter.class);

    private SecurityAccessControlService<ServletRequest> securityAccessControlService = null;

    private String allowKey = "httpSecurityAccessControlService";

    @Override
    public ServletRequest doBefore(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {

        if (securityAccessControlService != null) {

            if (securityAccessControlService.restrict(servletRequest)) {

                if (servletResponse instanceof HttpServletResponse) {

                    ((HttpServletResponse) servletResponse).setStatus(429);

                }

                return null;
            }


        }

        return servletRequest;
    }

    @Override
    public void doAfter(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IOException, ServletException {

    }

    @Override
    public boolean supports(final String cfgProperty, final Object configuration) {
        return (configuration instanceof SecurityAccessControlService ||
                (configuration instanceof Class && SecurityAccessControlService.class.isAssignableFrom((Class<?>) configuration)))
                && this.allowKey.equals(cfgProperty);
    }

    @Override
    public void register(final String key, final SecurityAccessControlService<ServletRequest> configuration) {

        if (configuration != null) {
            LOG.debug("Custom SecurityAccessControlFilter settings registering service {}", configuration.getClass());
            this.securityAccessControlService = configuration;
        } else {
            LOG.debug("Custom SecurityAccessControlFilter settings registering NO PROTECTION");
            this.securityAccessControlService = null;
        }

    }

    public void setAllowKey(final String allowKey) {
        this.allowKey = allowKey;
    }
}

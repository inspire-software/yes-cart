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

package org.yes.cart.web.swagger2;

import com.google.common.base.Optional;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: denispavlov
 * Date: 15/05/2020
 * Time: 22:02
 */
@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class SecurityRolesReader implements springfox.documentation.spi.service.OperationBuilderPlugin {

    private static final String SECURE = "Authorization required:\n";

    private final DescriptionResolver descriptions;

    final static Logger LOG = LoggerFactory.getLogger(SecurityRolesReader.class);

    @Autowired
    public SecurityRolesReader(final DescriptionResolver descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public void apply(OperationContext context) {
        try {
            Optional<ApiOperation> methodAnnotation = context.findAnnotation(ApiOperation.class);

            if (!methodAnnotation.isPresent()) {
                return;
            }

            final List<String> roles = new ArrayList<>();
            Optional<Secured> securedAnnotation = context.findAnnotation(Secured.class);
            if (securedAnnotation.isPresent()) {
                roles.addAll(Arrays.asList(securedAnnotation.get().value()));
            }
            Optional<PreAuthorize> preAnnotation = context.findAnnotation(PreAuthorize.class);
            if (preAnnotation.isPresent()) {
                roles.add(preAnnotation.get().value());
            }

            if (!roles.isEmpty()) {
                final StringBuilder apiRoleAccessNoteText = new StringBuilder(SECURE);
                for (final String role : roles) {
                    apiRoleAccessNoteText.append("\n * ").append(role);
                }
                // add the note text to the Swagger UI
                context.operationBuilder().notes(descriptions.resolve(apiRoleAccessNoteText.toString()));
            }

        } catch (Exception e) {
            LOG.error("Error when creating swagger documentation for security roles: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }
}

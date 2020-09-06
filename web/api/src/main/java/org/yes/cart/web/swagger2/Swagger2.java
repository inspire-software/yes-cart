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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 20/10/2019
 * Time: 21:04
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

    @Bean
    public Docket api() {

        final ParameterBuilder globalParamsBuilder = new ParameterBuilder();
        final List<Parameter> globalParams = new ArrayList<>();

        // Global Headers
        globalParams.add(globalParamsBuilder.name("X-CW-TOKEN")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .description("Request token")
                .required(false)
                .build());
        globalParams.add(globalParamsBuilder.name("X-SALES-CHANNEL")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .description("Sales channel domain name")
                .required(true)
                .build());

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("(?!/error).+"))
                .paths(PathSelectors.regex("(?!/connector).+"))
                .build()
                .apiInfo(apiInfo()).globalOperationParameters(globalParams);
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "REST API",
                "REST API.",
                "3.7.0",
                "www.yes-cart.org",
                new Contact("YC", "www.yes-cart.org", "admin@yes-cart.org"),
                "API License",
                "www.yes-cart.org",
                Collections.singleton(new StringVendorExtension("YC", "3.7.0"))
        );
        return apiInfo;
    }

}

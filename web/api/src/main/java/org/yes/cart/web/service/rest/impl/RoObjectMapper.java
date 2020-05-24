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
package org.yes.cart.web.service.rest.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.yes.cart.utils.impl.JsonAdapterUtils;

/**
 * Customized object mapper to support creation of particular dto classes from json.
 * Created by igor on 30.12.2015.
 */
public class RoObjectMapper extends ObjectMapper {

    public RoObjectMapper() {
        super();

        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final SimpleModule module = new SimpleModule("ro", JsonAdapterUtils.VERSION);

        JsonAdapterUtils.configureModuleWithJSFriendlyJavaTime(module);

        this.registerModule(module);

    }
}

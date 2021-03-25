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

package org.yes.cart.domain.dto.impl;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.yes.cart.domain.dto.factory.impl.DtoFactoryImpl;
import org.yes.cart.utils.spring.LinkedHashMapBean;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * DtoFactoryImpl test.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 11:51:21 PM
 */
public class DtoFactoryImplTest {

    private static final String KEY = "java.util.List";
    private DtoFactoryImpl factory;

    @Test(expected = InstantiationError.class)
    public void testGetInstanceUnmapped() throws Exception {
        factory = new DtoFactoryImpl(new LinkedHashMapBean<>(Collections.emptyMap()));
        factory.getByIface(List.class);
    }

    @Test(expected = InstantiationError.class)
    public void testGetInstanceUnableToInstantiate() throws Exception {
        factory = new DtoFactoryImpl(new LinkedHashMapBean<>(Collections.singletonMap(KEY, "invalid.class.Name")));
        factory.getByIface(List.class);
    }

    @Test
    public void testGetInstance() throws Exception {
        factory = new DtoFactoryImpl(new LinkedHashMapBean<>(Collections.singletonMap(KEY, "java.util.ArrayList")));
        MatcherAssert.assertThat(factory.getByIface(List.class), is(notNullValue()));
    }
}

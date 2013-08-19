/*
 * Copyright 2013 Igor Azarnyi, Denys Pavlov
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
package org.yes.cart.web.service.ws.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.service.ws.CacheDirector;

import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: igora
 * Date: 8/19/13
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:cache-config.xml"})
public class CacheDirectorImplTest {

    @Autowired
    ApplicationContext context;

    CacheDirectorImpl cacheDirector;
    @Before
    public void setUp() {
        cacheDirector = new CacheDirectorImpl();
        cacheDirector.setEntityOperationCache((Map<String, Map<String, Set<Pair<String,String>>>>) context.getBean("evictionConfig"));
    }

    @Test
    public void testResolveCacheNames() throws Exception {

        Set<Pair<String,String>> caches = cacheDirector.resolveCacheNames(CacheDirector.EntityOperation.CREATE, CacheDirector.EntityName.ATTRIBUTE);

        assertEquals(5, caches.size());



        assertTrue(caches.contains(new Pair("attributeService-availableAttributesByProductTypeId", "all")));
        assertTrue(caches.contains(new Pair("attributeService-availableImageAttributesByGroupCode", "all")));
        assertTrue(caches.contains(new Pair("attributeService-allAttributeCodes", "all")));
        assertTrue(caches.contains(new Pair("attributeService-allNavigatableAttributeCodes", "all")));
        assertTrue(caches.contains(new Pair("attributeService-attributeNamesByCodes", "all")));

    }

}

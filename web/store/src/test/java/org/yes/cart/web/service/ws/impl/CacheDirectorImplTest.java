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
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yes.cart.domain.dto.impl.CacheInfoDTOImpl;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.service.ws.CacheDirector;

import javax.naming.NamingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.*;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/19/13
 * Time: 4:16 PM
 */
public class CacheDirectorImplTest {

    private GenericXmlApplicationContext context;
    private CacheDirectorImpl cacheDirector;

    @BeforeClass
    public static void initInitialContext() {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("test-ds.xml");
        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.bind("java:comp/env/jdbc/yesjndi", applicationContext.getBean("dataSource"));
        builder.bind("java:comp/env/jdbc/yespayjndi", applicationContext.getBean("payDataSource"));
        try {
            builder.activate();
        } catch (NamingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }



    @Before
    public void setUp() throws NamingException {

        context = new GenericXmlApplicationContext();
        context.load("file:src/test/resources/testApplicationContext.xml");
        context.refresh();


        cacheDirector = new CacheDirectorImpl();
        cacheDirector.setEntityOperationCache((Map<String, Map<String, Set<Pair<String,String>>>>) context.getBean("evictionConfig"));
        cacheDirector.setCacheManager((CacheManager) context.getBean("cacheManager"));

    }

    @Test
    public void testResolveCacheNames() throws Exception {

        Set<Pair<String,String>> caches = cacheDirector.resolveCacheNames(CacheDirector.EntityOperation.CREATE, CacheDirector.EntityName.ATTRIBUTE);

        assertEquals(7, caches.size());

        assertTrue(caches.contains(new Pair("attributeService-availableAttributesByProductTypeId", "all")));
        assertTrue(caches.contains(new Pair("attributeService-availableImageAttributesByGroupCode", "all")));
        assertTrue(caches.contains(new Pair("attributeService-allAttributeCodes", "all")));
        assertTrue(caches.contains(new Pair("attributeService-allNavigatableAttributeCodes", "all")));
        assertTrue(caches.contains(new Pair("attributeService-attributeNamesByCodes", "all")));
        assertTrue(caches.contains(new Pair("breadCrumbBuilder-breadCrumbs", "all")));
        assertTrue(caches.contains(new Pair("filteredNavigationSupport-attributeFilteredNavigationRecords", "all")));

        caches = cacheDirector.resolveCacheNames(CacheDirector.EntityOperation.CREATE, "unknownEntity");
        assertNull(caches);

        caches = cacheDirector.resolveCacheNames("unkbnownOperation", CacheDirector.EntityName.PRODUCT);
        assertNull(caches);


    }

    @Test
    public void testGetCacheInfo() {
        cacheDirector.getCacheManager().getCache("attributeService-availableAttributesByProductTypeId").put("hi", "there");
        List<CacheInfoDTOImpl> rez = cacheDirector.getCacheInfo();
        for (CacheInfoDTOImpl cacheInfoDTO : rez) {
            if (cacheInfoDTO.getCacheName().equals("attributeService-availableAttributesByProductTypeId")){
                assertEquals(1, cacheInfoDTO.getInMemorySize());
            }
        }
        cacheDirector.getCacheManager().getCache("attributeService-availableAttributesByProductTypeId").clear();
    }


    @Test
    public void testEvictCache() {

        List<CacheInfoDTOImpl> rez = cacheDirector.getCacheInfo();
        for (CacheInfoDTOImpl cacheInfoDTO : rez) {
            cacheDirector.getCacheManager().getCache(cacheInfoDTO.getCacheName()).put("hi", "there");
        }
        cacheDirector.evictCache();
        for (CacheInfoDTOImpl cacheInfoDTO : rez) {
            assertEquals(0, cacheInfoDTO.getCacheSize());
        }

    }

    @Test
    public void testOnCacheableChange() {
        cacheDirector.getCacheManager().getCache("attributeService-availableAttributesByProductTypeId").put("hi", "there");
        cacheDirector.getCacheManager().getCache("categoryService-categoryHasSubcategory").put("hi", "there");
        cacheDirector.onCacheableChange(CacheDirector.EntityOperation.UPDATE, CacheDirector.EntityName.ATTRIBUTE, 123L);
        assertNull(cacheDirector.getCacheManager().getCache("attributeService-availableAttributesByProductTypeId").get("hi"));
        assertNotNull(cacheDirector.getCacheManager().getCache("categoryService-categoryHasSubcategory").get("hi"));
    }


}

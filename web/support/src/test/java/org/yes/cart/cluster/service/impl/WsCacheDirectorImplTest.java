/*
 * Copyright 2013 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.cluster.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.yes.cart.cluster.service.CacheDirector;
import org.yes.cart.domain.dto.impl.CacheInfoDTO;
import org.yes.cart.domain.misc.Pair;

import javax.naming.NamingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/19/13
 * Time: 4:16 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:cacheDirectorApplicationContext.xml")
public class WsCacheDirectorImplTest {

    @Autowired
    private ApplicationContext context;
    private WsCacheDirectorImpl cacheDirector;

    @Before
    public void setUp() throws NamingException {

        cacheDirector = new WsCacheDirectorImpl();
        cacheDirector.setEntityOperationCache((Map<String, Map<String, Set<Pair<String,String>>>>) context.getBean("evictionConfig"));
        cacheDirector.setCacheManager((CacheManager) context.getBean("cacheManager"));

    }

    @Test
    public void testResolveCacheNames() throws Exception {

        Set<Pair<String,String>> caches = cacheDirector.resolveCacheNames(CacheDirector.EntityOperation.CREATE, "AttributeEntity");

        assertEquals(15, caches.size());

        assertTrue(caches.contains(new Pair("attributeService-availableAttributesByProductTypeId", "all")));
        assertTrue(caches.contains(new Pair("attributeService-availableImageAttributesByGroupCode", "all")));
        assertTrue(caches.contains(new Pair("attributeService-availableAttributesByGroupCodeStartsWith", "all")));
        assertTrue(caches.contains(new Pair("attributeService-byAttributeCode", "all")));
        assertTrue(caches.contains(new Pair("attributeService-allAttributeCodes", "all")));
        assertTrue(caches.contains(new Pair("attributeService-allNavigatableAttributeCodes", "all")));
        assertTrue(caches.contains(new Pair("attributeService-allSearchableAttributeCodes", "all")));
        assertTrue(caches.contains(new Pair("attributeService-allSearchablePrimaryAttributeCodes", "all")));
        assertTrue(caches.contains(new Pair("attributeService-allStorableAttributeCodes", "all")));
        assertTrue(caches.contains(new Pair("attributeService-singleNavigatableAttributeCodesByProductType", "all")));
        assertTrue(caches.contains(new Pair("attributeService-navigatableAttributeDisplayValue", "all")));
        assertTrue(caches.contains(new Pair("attributeService-allAttributeNames", "all")));
        assertTrue(caches.contains(new Pair("attributeService-attributeNamesByCodes", "all")));
        assertTrue(caches.contains(new Pair("breadCrumbBuilder-breadCrumbs", "all")));
        assertTrue(caches.contains(new Pair("filteredNavigationSupport-attributeFilteredNavigationRecords", "all")));

        caches = cacheDirector.resolveCacheNames(CacheDirector.EntityOperation.CREATE, "unknownEntity");
        assertNull(caches);

        caches = cacheDirector.resolveCacheNames("unkbnownOperation", "ProductEntity");
        assertNull(caches);


    }

    @Test
    public void testGetCacheInfo() {
        cacheDirector.getCacheManager().getCache("attributeService-availableAttributesByProductTypeId").put("hi", "there");
        List<CacheInfoDTO> rez = cacheDirector.getCacheInfo();
        for (CacheInfoDTO cacheInfoDTO : rez) {
            if (cacheInfoDTO.getCacheName().equals("attributeService-availableAttributesByProductTypeId")){
                assertEquals(1, cacheInfoDTO.getInMemorySize());
            }
        }
        cacheDirector.getCacheManager().getCache("attributeService-availableAttributesByProductTypeId").clear();
    }


    @Test
    public void testAllEvictCache() {

        List<CacheInfoDTO> rez;

        rez = cacheDirector.getCacheInfo();
        for (CacheInfoDTO cacheInfoDTO : rez) {
            cacheDirector.getCacheManager().getCache(cacheInfoDTO.getCacheName()).put("hi", "there");
        }
        rez = cacheDirector.getCacheInfo();
        for (CacheInfoDTO cacheInfoDTO : rez) {
            assertTrue(cacheInfoDTO.getCacheSize() > 0);
        }
        cacheDirector.evictAllCache(false);
        rez = cacheDirector.getCacheInfo();
        for (CacheInfoDTO cacheInfoDTO : rez) {
            if (cacheDirector.getSkipEvictAll().contains(cacheInfoDTO.getCacheName())) {
                assertTrue(cacheInfoDTO.getCacheSize() > 0);
            } else {
                assertEquals(0, cacheInfoDTO.getCacheSize());
            }
        }
        cacheDirector.evictAllCache(true);
        rez = cacheDirector.getCacheInfo();
        for (CacheInfoDTO cacheInfoDTO : rez) {
            assertEquals(0, cacheInfoDTO.getCacheSize());
        }

    }

    @Test
    public void testEvictCache() {

        List<CacheInfoDTO> rez;
        rez = cacheDirector.getCacheInfo();
        final String first = rez.get(0).getCacheName();
        cacheDirector.getCacheManager().getCache(first).put("hi", "there");

        rez = cacheDirector.getCacheInfo();
        for (CacheInfoDTO cacheInfoDTO : rez) {
            if (cacheInfoDTO.getCacheName().equals(first)) {
                assertTrue(cacheInfoDTO.getCacheSize() > 0);
            }
        }

        cacheDirector.evictCache(first);
        rez = cacheDirector.getCacheInfo();
        for (CacheInfoDTO cacheInfoDTO : rez) {
            if (cacheInfoDTO.getCacheName().equals(first)) {
                assertEquals(0, cacheInfoDTO.getCacheSize());
            }
        }

    }

    @Test
    public void testOnCacheableChange() {
        cacheDirector.getCacheManager().getCache("attributeService-availableAttributesByProductTypeId").put("hi", "there");
        cacheDirector.getCacheManager().getCache("categoryService-categoryHasSubcategory").put("hi", "there");
        cacheDirector.onCacheableChange(CacheDirector.EntityOperation.UPDATE, "AttributeEntity", 123L);
        assertNull(cacheDirector.getCacheManager().getCache("attributeService-availableAttributesByProductTypeId").get("hi"));
        assertNotNull(cacheDirector.getCacheManager().getCache("categoryService-categoryHasSubcategory").get("hi"));
    }

    @Test
    public void testOnCacheableBulkChange() {
        cacheDirector.getCacheManager().getCache("attributeService-availableAttributesByProductTypeId").put("hi", "there");
        cacheDirector.getCacheManager().getCache("categoryService-categoryHasSubcategory").put("hi", "there");
        cacheDirector.onCacheableBulkChange(CacheDirector.EntityOperation.UPDATE, "AttributeEntity", new Long[] { 123L });
        assertNull(cacheDirector.getCacheManager().getCache("attributeService-availableAttributesByProductTypeId").get("hi"));
        assertNotNull(cacheDirector.getCacheManager().getCache("categoryService-categoryHasSubcategory").get("hi"));
    }


}

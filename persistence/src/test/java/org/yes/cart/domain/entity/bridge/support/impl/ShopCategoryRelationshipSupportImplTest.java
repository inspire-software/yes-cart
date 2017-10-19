/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.domain.entity.bridge.support.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.search.dao.support.ShopCategoryRelationshipSupport;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 17/10/2017
 * Time: 12:19
 */
public class ShopCategoryRelationshipSupportImplTest {
    
    private final Mockery context = new JUnit4Mockery();

    final List<Object[]> dbCategories = Arrays.asList(
            new Object[] { 100L, 100L, null }, // root
            new Object[] { 101L, 100L, null },
            new Object[] { 1010L, 101L, null },
            new Object[] { 1011L, 101L, null },
            new Object[] { 1012L, 101L, null },
            new Object[] { 102L, 100L, null },
            new Object[] { 1020L, 102L, null },
            new Object[] { 1021L, 102L, null },
            new Object[] { 103L, 100L, null },
            new Object[] { 104L, 100L, null },
            new Object[] { 1040L, 104L, null },
            new Object[] { 10400L, 1040L, null },
            new Object[] { 10401L, 1040L, 101L },
            new Object[] { 10402L, 1040L, 1021L }
    );


    @Test
    public void loadCategoryMapping() throws Exception {


        final GenericDAO<Shop, Long> shopDao = this.context.mock(GenericDAO.class, "shopDao");
        final GenericDAO<Category, Long> categoryDao = this.context.mock(GenericDAO.class, "categoryDao");

        this.context.checking(new Expectations() {{
            allowing(categoryDao).findQueryObjectByNamedQuery("CATEGORY.PARENT.LINK.ALL"); will(returnValue(dbCategories));
        }});

        final ShopCategoryRelationshipSupportImpl support = new ShopCategoryRelationshipSupportImpl(shopDao, categoryDao) {
            @Override
            public ShopCategoryRelationshipSupport getSelf() {
                return this;
            }
        };

        final Map<Long, Set<Long>> mapping = support.loadCategoryMapping();

        assertEquals(6, mapping.size());

        // root has 4 sub cats
        final Set<Long> _100 = mapping.get(100L);
        assertEquals(4, _100.size());
        assertTrue(_100.containsAll(Arrays.asList(101L, 102L, 103L, 104L)));

        // 101 has 3 sub cats
        final Set<Long> _101 = mapping.get(101L);
        assertEquals(3, _101.size());
        assertTrue(_101.containsAll(Arrays.asList(1010L, 1011L, 1012L)));

        // 1010, 1011, 1012 do not have sub cats so not in the map

        // 102 has 2 sub cats
        final Set<Long> _102 = mapping.get(102L);
        assertEquals(2, _102.size());
        assertTrue(_102.containsAll(Arrays.asList(1020L, 1021L)));

        // 1020L, 1021L do not have sub cats so not in the map

        // 103 does not have sub cats, so not in the map

        // 104 has 1 sub cat
        final Set<Long> _104 = mapping.get(104L);
        assertEquals(1, _104.size());
        assertTrue(_104.containsAll(Arrays.asList(1040L)));

        // 1040 has 3 own sub cats, + 2 linked
        final Set<Long> _1040 = mapping.get(1040L);
        assertEquals(5, _1040.size());
        assertTrue(_1040.containsAll(Arrays.asList(10400L, 10401L, 101L, 10402L, 1021L)));

        // 10400 does not have sub cats so not in the map

        // 10401 links to 101 and inherits 1010, 1011, 1012
        final Set<Long> _10401 = mapping.get(10401L);
        assertEquals(3, _10401.size());
        assertTrue(_10401.containsAll(Arrays.asList(1010L, 1011L, 1012L)));

        // 10402 links to 1021 which does not have any sub cats so not in the map

        this.context.assertIsSatisfied();

    }

    @Test
    public void getShopCategoriesIds() throws Exception {

        final GenericDAO<Shop, Long> shopDao = this.context.mock(GenericDAO.class, "shopDao");
        final GenericDAO<Category, Long> categoryDao = this.context.mock(GenericDAO.class, "categoryDao");

        final Shop shop10 = this.context.mock(Shop.class, "shop10");
        final ShopCategory shop10sc1 = this.context.mock(ShopCategory.class, "shop10sc1");
        final ShopCategory shop10sc2 = this.context.mock(ShopCategory.class, "shop10sc2");
        final ShopCategory shop10sc3 = this.context.mock(ShopCategory.class, "shop10sc3");
        final Shop shop20 = this.context.mock(Shop.class, "shop20");
        final ShopCategory shop20sc1 = this.context.mock(ShopCategory.class, "shop20sc1");
        final ShopCategory shop20sc2 = this.context.mock(ShopCategory.class, "shop20sc2");
        final Category c101 = this.context.mock(Category.class, "c101");
        final Category c102 = this.context.mock(Category.class, "c102");
        final Category c103 = this.context.mock(Category.class, "c103");
        final Category c104 = this.context.mock(Category.class, "c104");

        this.context.checking(new Expectations() {{
            allowing(categoryDao).findQueryObjectByNamedQuery("CATEGORY.PARENT.LINK.ALL"); will(returnValue(dbCategories));
            allowing(shopDao).findById(10L); will(returnValue(shop10));
            allowing(shop10).getShopCategory(); will(returnValue(Arrays.asList(shop10sc1, shop10sc2, shop10sc3)));
            allowing(shop10sc1).getCategory(); will(returnValue(c101));
            allowing(shop10sc2).getCategory(); will(returnValue(c102));
            allowing(shop10sc3).getCategory(); will(returnValue(c103));
            allowing(shopDao).findById(20L); will(returnValue(shop20));
            allowing(shop20).getShopCategory(); will(returnValue(Arrays.asList(shop20sc1, shop20sc2)));
            allowing(shop20sc1).getCategory(); will(returnValue(c102));
            allowing(shop20sc2).getCategory(); will(returnValue(c104));
            allowing(c101).getCategoryId(); will(returnValue(101L));
            allowing(c102).getCategoryId(); will(returnValue(102L));
            allowing(c103).getCategoryId(); will(returnValue(103L));
            allowing(c104).getCategoryId(); will(returnValue(104L));
        }});

        final ShopCategoryRelationshipSupportImpl support = new ShopCategoryRelationshipSupportImpl(shopDao, categoryDao) {
            @Override
            public ShopCategoryRelationshipSupport getSelf() {
                return this;
            }
        };

        final Set<Long> shop10cats = support.getShopCategoriesIds(10);

        assertEquals(8, shop10cats.size());
        assertTrue(shop10cats.containsAll(Arrays.asList(101L, 1010L, 1011L, 1012L, 102L, 1020L, 1021L, 103L)));

        final Set<Long> shop20cats = support.getShopCategoriesIds(20);
        assertEquals(12, shop20cats.size());
        assertTrue(shop20cats.containsAll(Arrays.asList(102L, 1020L, 1021L, 104L, 1040L, 10400L, 10401L, 101L, 1010L, 1011L, 1012L, 10402L, 1021L)));

        this.context.assertIsSatisfied();

    }

}
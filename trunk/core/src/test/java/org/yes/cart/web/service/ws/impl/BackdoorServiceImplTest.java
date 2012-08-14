/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.web.service.ws.BackdoorService;

import java.math.BigInteger;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Test
 * User: iga
 * Date: 29-jan-12
 * Time: 2:36 PM
 */
public class BackdoorServiceImplTest extends BaseCoreDBTestCase {


    @Test
    public void testSqlQuery() throws Exception {
        BackdoorService backdoorService = ctx().getBean("backDoorBean", BackdoorService.class);
        List<Object[]> rez = backdoorService.sqlQuery("select *  from  TETYPE where ETYPE_ID < 1009 order by 1 asc");
        assertEquals(9, rez.size());
        assertEquals(new BigInteger("1000"), rez.get(0)[0]);
        assertEquals("java.lang.String", rez.get(0)[1]);
        assertEquals("String", rez.get(0)[2]);


    }

    @Test
    public void testHsqlQuery() throws Exception {
        BackdoorService backdoorService = ctx().getBean("backDoorBean", BackdoorService.class);
        List<Object[]> rez = backdoorService.hsqlQuery("select t  from  Etype t fetch all properties where t.etypeId = 1000");
        assertEquals(1, rez.size());
        assertEquals(""+new BigInteger("1000"), ""+rez.get(0)[7]);
        assertEquals("java.lang.String", rez.get(0)[0]);
        assertEquals("String", rez.get(0)[1]);

    }

   /*@Test
   public void testLuceneQuery() throws Exception {

       GenericDAO<Product, Long>productDao = (GenericDAO<Product, Long>) ctx().getBean(DaoServiceBeanKeys.PRODUCT_DAO);

       //productDao.fullTextSearchReindex();

       BackdoorService backdoorService = ctx().getBean("backDoorBean", BackdoorService.class);

       List<Object[]> rez = backdoorService.luceneQuery("productCategory.category:101 productCategory.category:200 +(attribute.attribute:MATERIAL sku.attribute.attribute:MATERIAL) +(attribute.val:metal sku.attribute.val:metal)");

       //assertEquals(1, rez.size());

   }      */

}

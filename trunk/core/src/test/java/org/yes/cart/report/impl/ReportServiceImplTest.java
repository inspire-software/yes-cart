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

package org.yes.cart.report.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.misc.Pair;

import java.util.Collections;

import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/1/12
 * Time: 11:59 AM
 */
public class ReportServiceImplTest   extends BaseCoreDBTestCase {

    private GenericDAO<Object, Long> genericDAO;



    @Before
    public void setUp() throws Exception {
        genericDAO = (GenericDAO) ctx().getBean("genericDao");

    }



    @Test
    public void testGetReport() throws Exception {

        final ReportDescriptor reportDescriptor = new ReportDescriptor();
        reportDescriptor.setHsqlQuery("select s from ShopEntity s");
        reportDescriptor.getLangXslfo().add(new ReportPair("en", "src/test/resources/xslfo/shop.xslfo"));
        reportDescriptor.setReportId("testReport");

        ReportServiceImpl reportService = new ReportServiceImpl(genericDAO, Collections.singletonList(reportDescriptor), null);

        assertTrue(reportService.createReport("en", "testReport", "shop.pdf"));

    }

}

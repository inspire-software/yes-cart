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

package org.yes.cart.report.impl;

import org.junit.Test;
import org.yes.cart.report.ReportDescriptorXLSX;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 10/10/2019
 * Time: 13:00
 */
public class AbstractPoiXlsxReportGeneratorTest {

    @Test
    public void testGenerateXlsxReport() throws Exception {

        final AbstractPoiXlsxReportGenerator generator = new AbstractPoiXlsxReportGenerator() {

        };

        final ReportDescriptorXLSX availableStock = new ReportDescriptorXLSX();
        availableStock.setReportId("available-stock");
        availableStock.setVisible(true);

        final ByteArrayOutputStream baosAvailableStock = new ByteArrayOutputStream();

        generator.generateReport(availableStock, new HashMap<String, Object>() {{
            put("p1", "v1");
        }}, generateData(), "en", baosAvailableStock);

        assertTrue(baosAvailableStock.toByteArray().length > 0);

        try(FileOutputStream fos = new FileOutputStream("target/test-xlsx-report.xlsx")) {

            fos.write(baosAvailableStock.toByteArray());

        }

    }

    private Object generateData() {
        return Arrays.asList(
                new Object[]{"col 1", "col 2", "col 3"},
                new Object[]{"A", 1, new BigDecimal("0.99")}
        );
    }

}
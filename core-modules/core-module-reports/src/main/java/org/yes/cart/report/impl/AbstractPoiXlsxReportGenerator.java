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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.report.ReportDescriptor;
import org.yes.cart.report.ReportGenerator;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 10/10/2019
 * Time: 12:36
 */
public class AbstractPoiXlsxReportGenerator implements ReportGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractFopReportGenerator.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final ReportDescriptor reportDescriptor) {
        return "xlsx".equalsIgnoreCase(reportDescriptor.getReportType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateReport(final ReportDescriptor descriptor,
                               final Map<String, Object> parameters,
                               final Object data,
                               final String lang,
                               final OutputStream outputStream) {

        if (data == null || data instanceof Collection && ((Collection) data).isEmpty()) {
            LOG.debug("No data, no report will be generated");
            return;

        }

        try {

            final XSSFWorkbook workbook = new XSSFWorkbook();
            final XSSFSheet sheet = workbook.createSheet(descriptor.getReportId());

            final XSSFCellStyle headerStyle = workbook.createCellStyle();
            final XSSFFont font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            int rowNum = 0;

            for (Object dataLine : (Collection) data) {

                final Object[] line = (Object[]) dataLine;

                final Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (Object field : line) {
                    final Cell cell = row.createCell(colNum++);
                    if (rowNum == 1) {
                        cell.setCellStyle(headerStyle);
                    }
                    if (field instanceof String) {
                        cell.setCellValue((String) field);
                    } else if (field instanceof Number) {
                        cell.setCellValue(((Number) field).doubleValue());
                    } else {
                        cell.setCellValue("");
                    }
                }
            }

            workbook.write(outputStream);
            workbook.close();

        } catch (Exception exp) {
            LOG.error("Unable to generate report for " + descriptor + " in " + lang, exp);
        }

    }

    /**
     * Get required MIME type for report.
     *
     * Uses parameters['MIME'], defaults to "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
     * if not provided
     *
     * @param descriptor descriptor
     * @param parameters passed in parameter values
     * @param data data object for report
     * @param lang language
     *
     * @return mime type
     */
    protected String getOutputMimeType(final ReportDescriptor descriptor,
                                       final Map<String, Object> parameters,
                                       final Object data,
                                       final String lang) {
        if (parameters.containsKey("MIME")) {
            return String.valueOf(parameters.get("MIME"));
        }
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

}

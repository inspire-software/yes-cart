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

package org.yes.cart.bulkexport.csv.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.service.DataDescriptorSampleGenerator;
import org.yes.cart.bulkexport.csv.CsvExportColumn;
import org.yes.cart.bulkexport.csv.CsvExportDescriptor;
import org.yes.cart.domain.misc.Pair;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: inspiresoftware
 * Date: 16/01/2021
 * Time: 11:26
 */
public class CsvDataDescriptorSampleGeneratorImpl implements DataDescriptorSampleGenerator {

    @Override
    public boolean supports(final Object descriptor) {
        return descriptor instanceof CsvExportDescriptor &&
                !"IMAGE".equalsIgnoreCase(((CsvExportDescriptor) descriptor).getEntityType());
    }

    @Override
    public List<Pair<String, byte[]>> generateSample(final Object descriptor) {

        final List<Pair<String, byte[]>> templates = new ArrayList<>();

        final CsvExportDescriptor csv = (CsvExportDescriptor) descriptor;

        final String encoding = csv.getExportFileDescriptor().getFileEncoding();
        final Charset charset = encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;

        String fileName = csv.getExportFileDescriptor().getFileName();
        fileName = fileName.replace("/", "_").replace("\\", "_");
        fileName = fileName.replace("{timestamp}", "yyyy-MM-dd_HH-mm-ss");

        templates.add(new Pair<>(fileName + "-readme.txt",
                ("Template: " + fileName + "\n" +
                "Encoding: " + encoding + "\n" +
                "Select command: " + csv.getSelectCmd() + "\n").getBytes(charset))
        );

        final Pair<String, String> template = getTemplatePair(csv);

        templates.add(new Pair<>(fileName,
                ((template.getFirst() != null ? template.getFirst() + "\n" : "") + template.getSecond() + "\n")
                        .getBytes(charset))
        );

        templates.add(new Pair<>(fileName + "-descriptor.xml",
                (csv.getSource() != null ? csv.getSource() : "<!-- no source -->").getBytes(charset))
        );

        return templates;
    }

    private Pair<String, String> getTemplatePair(final CsvExportDescriptor csv) {

        final boolean header = csv.getExportFileDescriptor().isPrintHeader();
        final char delimiter = csv.getExportFileDescriptor().getColumnDelimiter();
        final char textQualifier = csv.getExportFileDescriptor().getTextQualifier();

        final List<String> headers = new ArrayList<>();
        final List<String> values = new ArrayList<>();

        for (final Pair<CsvExportColumn, CsvExportColumn> columnAndParent : sortByIndex(csv.getColumns())) {

            if (columnAndParent == null) {

                headers.add("");
                values.add("");

            } else {

                final CsvExportColumn column = columnAndParent.getFirst();
                final CsvExportColumn parent = columnAndParent.getSecond();

                final String headerName =
                        (parent != null ? parent.getColumnHeader() + " > " : "") +
                                column.getColumnHeader() + (column.getLanguage() != null ? " (" + column.getLanguage() + ")" : "");

                if (CsvImpExColumn.FK_FIELD.equals(column.getFieldType())) {

                    if (column.getName().toLowerCase().contains("guid")) {

                        headers.add(wrapText(headerName + " GUID", textQualifier));

                    } else if (column.getName().toLowerCase().contains("code")) {

                        headers.add(wrapText(headerName + " CODE", textQualifier));

                    } else if (column.getName().toLowerCase().contains("id")) {

                        headers.add(wrapText(headerName + " ID", textQualifier));

                    } else {

                        headers.add(wrapText(headerName, textQualifier));

                    }

                } else {

                    headers.add(wrapText(headerName, textQualifier));

                }

                final String columnType = column.getDataType() != null ? column.getDataType() : "STRING";
                switch (columnType.toUpperCase()) {
                    case "DATE":
                        values.add(wrapText("yyyy-MM-dd", textQualifier));
                        break;
                    case "DATETIME":
                        values.add(wrapText("yyyy-MM-dd HH:mm:ss", textQualifier));
                        break;
                    case "PRICE":
                    case "DECIMAL":
                        values.add("99.99");
                        break;
                    case "INTEGER":
                    case "LONG":
                        values.add("1");
                        break;
                    case "BOOLEAN":
                        values.add("true");
                        break;
                    default:
                        values.add(wrapText("", textQualifier));
                }

            }

        }

        final Pair<String, String> template;
        if (header) {
            template = new Pair<>(StringUtils.join(headers, delimiter), StringUtils.join(values, delimiter));
        } else {
            template = new Pair<>(null, StringUtils.join(values, delimiter));
        }
        return template;
    }

    private List<Pair<CsvExportColumn, CsvExportColumn>> sortByIndex(final Collection<CsvExportColumn> columns) {

        final List<Pair<CsvExportColumn, CsvExportColumn>> full = new ArrayList<>();
        columns.forEach(column -> {
            if (column.getDescriptor() != null &&
                    (CsvImpExColumn.SLAVE_INLINE_FIELD.equals(column.getFieldType()) ||
                            CsvImpExColumn.SLAVE_TUPLE_FIELD.equals(column.getFieldType()))) {
                column.getDescriptor().getColumns().forEach(inline -> {
                    full.add(new Pair<>(inline, column));
                });
            } else {
                full.add(new Pair<>(column, null));
            }
        });

        return full;
    }

    private String wrapText(final String value, final char textQualifier) {
        return textQualifier > 0 ? textQualifier + value + textQualifier : value;
    }
}

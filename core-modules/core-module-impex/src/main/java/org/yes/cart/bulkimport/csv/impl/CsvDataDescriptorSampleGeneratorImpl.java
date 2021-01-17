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

package org.yes.cart.bulkimport.csv.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.service.DataDescriptorSampleGenerator;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.domain.misc.Pair;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * User: inspiresoftware
 * Date: 16/01/2021
 * Time: 11:26
 */
public class CsvDataDescriptorSampleGeneratorImpl implements DataDescriptorSampleGenerator {

    @Override
    public boolean supports(final Object descriptor) {
        return descriptor instanceof CsvImportDescriptor &&
                !"IMAGE".equalsIgnoreCase(((CsvImportDescriptor) descriptor).getEntityType());
    }

    @Override
    public List<Pair<String, byte[]>> generateSample(final Object descriptor) {

        final List<Pair<String, byte[]>> templates = new ArrayList<>();

        final CsvImportDescriptor csv = (CsvImportDescriptor) descriptor;

        final String encoding = csv.getImportFileDescriptor().getFileEncoding();
        final Charset charset = encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;

        String fileName = csv.getImportFileDescriptor().getFileNameMask();
        if (fileName.endsWith("(.*)")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        fileName = fileName.replace("(.*)", "-XXXXXXX");

        templates.add(new Pair<>(fileName + "-readme.txt",
                ("Template: " + fileName + "\n" +
                "Mode: " + csv.getModeName() + "\n" +
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

    private Pair<String, String> getTemplatePair(final CsvImportDescriptor csv) {

        final boolean header = csv.getImportFileDescriptor().isIgnoreFirstLine();
        final char delimiter = csv.getImportFileDescriptor().getColumnDelimiter();
        final char textQualifier = csv.getImportFileDescriptor().getTextQualifier();

        final List<String> headers = new ArrayList<>();
        final List<String> values = new ArrayList<>();

        for (final Pair<CsvImportColumn, CsvImportColumn> columnAndParent : sortByIndex(csv.getColumns())) {

            if (columnAndParent == null) {

                headers.add("");
                values.add("");

            } else {

                final CsvImportColumn column = columnAndParent.getFirst();
                final CsvImportColumn parent = columnAndParent.getSecond();

                final String headerName =
                        (parent != null ? parent.getName() + " > " : "") +
                                column.getName() + (column.getLanguage() != null ? " (" + column.getLanguage() + ")" : "");

                if (column.getDescriptor() != null && !CsvImpExColumn.SLAVE_INLINE_FIELD.equals(column.getFieldType())) {

                    final Pair<String, String> sub = getTemplatePair(column.getDescriptor());

                    headers.add(wrapText(sub.getFirst() != null ? headerName + " > " + sub.getFirst() : headerName, textQualifier));

                    values.add(wrapText(sub.getSecond(), textQualifier));

                } else {

                    if (CsvImpExColumn.FK_FIELD.equals(column.getFieldType())) {

                        if (column.getLookupQuery().toLowerCase().contains("guid")) {

                            headers.add(wrapText(headerName + " GUID", textQualifier));

                        } else if (column.getLookupQuery().toLowerCase().contains("code")) {

                            headers.add(wrapText(headerName + " CODE", textQualifier));

                        } else {

                            headers.add(wrapText(headerName + " ID", textQualifier));

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

        }

        final Pair<String, String> template;
        if (header) {
            template = new Pair<>(StringUtils.join(headers, delimiter), StringUtils.join(values, delimiter));
        } else {
            template = new Pair<>(null, StringUtils.join(values, delimiter));
        }
        return template;
    }

    private List<Pair<CsvImportColumn, CsvImportColumn>> sortByIndex(final Collection<CsvImportColumn> columns) {

        final SortedSet<Integer> indexes = new TreeSet<>();
        final Map<Integer, Pair<CsvImportColumn, CsvImportColumn>> map = new HashMap<>();
        columns.forEach(column -> {
            if (column.getColumnIndex() >= 0) {
                if (column.getDescriptor() != null && CsvImpExColumn.SLAVE_INLINE_FIELD.equals(column.getFieldType())) {
                    column.getDescriptor().getColumns().forEach(inline -> {
                        indexes.add(inline.getColumnIndex());
                        map.put(inline.getColumnIndex(), new Pair<>(inline, column));
                    });
                } else {
                    indexes.add(column.getColumnIndex());
                    map.put(column.getColumnIndex(), new Pair<>(column, null));
                }
            }
        });

        final List<Pair<CsvImportColumn, CsvImportColumn>> full = new ArrayList<>();
        for (int i = 0; i <= indexes.last(); i++) {
            full.add(map.get(i));
        }

        return full;
    }

    private String wrapText(final String value, final char textQualifier) {
        return textQualifier > 0 ? textQualifier + value + textQualifier : value;
    }
}

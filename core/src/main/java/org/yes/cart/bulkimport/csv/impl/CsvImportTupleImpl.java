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

package org.yes.cart.bulkimport.csv.impl;

import org.yes.cart.bulkimport.csv.CsvImportTuple;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.model.ImportColumn;
import org.yes.cart.bulkimport.model.ImportTuple;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 12-07-31
 * Time: 8:06 AM
 */
public class CsvImportTupleImpl implements CsvImportTuple {

    private final String filename;
    private final long lineNumber;
    private final String[] line;

    public CsvImportTupleImpl(final String filename, final long lineNumber, final String[] line) {
        this.filename = filename;
        this.lineNumber = lineNumber;
        this.line = line;
    }

    /** {@inheritDoc} */
    public String getSourceId() {
        return filename + ":" + lineNumber;
    }

    /** {@inheritDoc} */
    public String[] getData() {
        return line;
    }

    /** {@inheritDoc} */
    public Object getColumnValue(final ImportColumn column) {
        if (line == null || column.getColumnIndex() > line.length - 1) {
            return null;
        }
        final String rawValue = line[column.getColumnIndex()];
        if (column.getGroupCount(rawValue) > 1) {
            return column.getValues(rawValue);
        }
        return column.getValue(rawValue);
    }

    /** {@inheritDoc} */
    public List<ImportTuple<String, String[]>> getSubTuples(final CsvImportDescriptor importDescriptor, final ImportColumn column) {
        if (line == null || column.getColumnIndex() > line.length - 1) {
            return null;
        }
        final String rawValue = line[column.getColumnIndex()];
        final String[] rows = rawValue.split(",");
        final List<ImportTuple<String, String[]>> subTuples = new ArrayList<ImportTuple<String, String[]>>(rows.length);
        int subLine = 0;
        for (String row : rows) {
            subTuples.add(new CsvImportTupleImpl(filename + ":" + lineNumber + ":" + column.getName(), subLine++,
                    row.split(String.valueOf(importDescriptor.getImportFileDescriptor().getColumnDelimiter()))));
        }
        return subTuples;
    }

    @Override
    public String toString() {
        return "CsvImportTupleImpl{" +
                "sid=" + getSourceId() +
                ", line=" + line +
                '}';
    }
}

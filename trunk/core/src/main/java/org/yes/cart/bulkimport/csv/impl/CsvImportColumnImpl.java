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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.model.DataTypeEnum;
import org.yes.cart.bulkimport.model.FieldTypeEnum;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.model.ValueAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 12:03 PM
 */
public class CsvImportColumnImpl implements CsvImportColumn, Serializable {

    private boolean table;

    private int columnIndex;

    private int forceGroupCount;

    private FieldTypeEnum fieldType;

    private DataTypeEnum dataType;

    private String entityType;

    private String name;

    private String valueRegEx;
    private Integer valueRegExGroup;

    private String valueConstant;

    private String lookupQuery; //for locate fk and pk

    private boolean useMasterObject;

    private String language;

    private ImportDescriptor importDescriptor; //complex fields.

    private transient Pattern pattern = null;


    public CsvImportColumnImpl() {
        super();
    }

    /**
     * Construct import column.
     *
     * @param columnIndex 0 based index
     * @param fieldType   {@link FieldTypeEnum} filed type
     * @param name        name
     * @param valueRegEx      regular expression to extract data
     * @param lookupQuery lookup query to determinate duplication, in this case the update
     *                    or insert strategy will be selected
     */
    public CsvImportColumnImpl(final int columnIndex, final FieldTypeEnum fieldType,
                               final String name, final String valueRegEx, final String lookupQuery) {
        super();
        this.columnIndex = columnIndex;
        this.fieldType = fieldType;
        this.name = name;
        this.valueRegEx = valueRegEx;
        this.lookupQuery = lookupQuery;
    }

    private Pattern getPattern() {
        if (pattern == null && StringUtils.isNotBlank(valueRegEx)) {
            pattern = Pattern.compile(valueRegEx, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        }
        return pattern;
    }


    /**
     * In case if column has reg exp.
     *
     * @return matched groups or 0 if column has not reg exp.
     */
    public int getGroupCount(final String rawValue) {
        if (getPattern() != null) {
            if (forceGroupCount == 0) {
                Matcher matcher = getPattern().matcher(rawValue);
                if (matcher.find()) {
                    if (valueRegExGroup != null) {
                        return 1;
                    }
                    return matcher.groupCount();
                }
            }
            return forceGroupCount;
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTable() {
        return table;
    }

    /**
     * {@inheritDoc}
     */
    public void setTable(boolean table) {
        this.table = table;
    }


    /**
     * {@inheritDoc}
     */
    public List getValues(final String rawValue, final ValueAdapter adapter) {
        List result = new ArrayList();
        if (getPattern() != null) {
            Matcher matcher = getPattern().matcher(rawValue);
            if (matcher.find()) {
                int groupCount = getGroupCount(rawValue);
                for (int i = 0; i < groupCount; i++) {
                    result.add(adapter.fromRaw(matcher.group(i + 1).trim(), getDataType()));
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Object getValue(final String rawValue, final ValueAdapter adapter) {
        if (getValueConstant() != null) {
            return adapter.fromRaw(getValueConstant(), getDataType());
        } else if (rawValue != null) {
            if (getPattern() != null) {
                Matcher matcher = getPattern().matcher(rawValue);
                if (matcher.find()) {
                    return adapter.fromRaw(matcher.group(getValueRegExGroup()).trim(), getDataType());
                } else {
                    return null;
                }
            }
            return adapter.fromRaw(rawValue, getDataType());
        }
        return null;

    }

    /**
     * {@inheritDoc}
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * {@inheritDoc}
     */
    public void setColumnIndex(final int columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * {@inheritDoc}
     */
    public FieldTypeEnum getFieldType() {
        return fieldType;
    }

    /**
     * {@inheritDoc}
     */
    public void setFieldType(final FieldTypeEnum fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * {@inheritDoc}
     */
    public DataTypeEnum getDataType() {
        return dataType;
    }

    /**
     * {@inheritDoc}
     */
    public void setDataType(final DataTypeEnum dataType) {
        this.dataType = dataType;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public String getValueRegEx() {
        return valueRegEx;
    }

    /**
     * {@inheritDoc}
     */
    public void setValueRegEx(final String valueRegEx) {
        this.valueRegEx = valueRegEx;
        this.pattern = null;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getValueRegExGroup() {
        if (valueRegExGroup == null) {
            return 1;
        }
        return valueRegExGroup;
    }

    /**
     * {@inheritDoc}
     */
    public void setValueRegExGroup(final Integer valueRegExGroup) {
        this.valueRegExGroup = valueRegExGroup;
    }

    /**
     * {@inheritDoc}
     */
    public String getLookupQuery() {
        return lookupQuery;
    }

    /**
     * {@inheritDoc}
     */
    public void setLookupQuery(final String lookupQuery) {
        this.lookupQuery = lookupQuery;
    }

    /**
     * {@inheritDoc}
     */
    public ImportDescriptor getImportDescriptor() {
        return importDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public void setImportDescriptor(ImportDescriptor importDescriptor) {
        this.importDescriptor = importDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUseMasterObject() {
        return useMasterObject;
    }

    /**
     * {@inheritDoc}
     */
    public void setUseMasterObject(final boolean useMasterObject) {
        this.useMasterObject = useMasterObject;
    }


    /**
     * {@inheritDoc}
     */
    public String getValueConstant() {
        return valueConstant;
    }

    /**
     * {@inheritDoc}
     */
    public void setValueConstant(final String valueConstant) {
        this.valueConstant = valueConstant;
    }

    /**
     * {@inheritDoc}
     */
    public int getForceGroupCount() {
        return forceGroupCount;
    }

    /**
     * {@inheritDoc}
     */
    public void setForceGroupCount(final int forceGroupCount) {
        this.forceGroupCount = forceGroupCount;
    }


    /**
     * {@inheritDoc}
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * {@inheritDoc}
     */
    public void setEntityType(final String entityType) {
        this.entityType = entityType;
    }

    /**
     * {@inheritDoc}
     */
    public String getLanguage() {
        return language;
    }

    /**
     * {@inheritDoc}
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "CsvImportColumnImpl{" +
                "columnIndex=" + columnIndex +
                ", fieldType=" + fieldType +
                ", name='" + name + '\'' +
                ", valueRegEx='" + valueRegEx + '\'' +
                ", valueRegExGroup=" + valueRegExGroup +
                ", lookupQuery='" + lookupQuery + '\'' +
                ", useMasterObject=" + useMasterObject +
                ", importDescriptor=" + importDescriptor +
                ", pattern=" + pattern +
                ", valueConstant='" + valueConstant + '\'' +
                ", forceGroupCount='" + forceGroupCount + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
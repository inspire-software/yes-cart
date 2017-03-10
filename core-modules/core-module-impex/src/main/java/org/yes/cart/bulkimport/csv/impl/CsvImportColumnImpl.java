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

package org.yes.cart.bulkimport.csv.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkcommon.model.ValueAdapter;
import org.yes.cart.bulkexport.model.ExportTuple;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkimport.model.ImportTuple;

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

    private int columnIndex;

    private String fieldType;

    private String dataType;

    private String entityType;

    private String name;

    private String valueRegEx;
    private Integer valueRegExGroup;
    private String valueRegExTemplate;

    private String valueConstant;

    private String lookupQuery; //for locate fk and pk

    private boolean useMasterObject;
    private boolean insertOnly;
    private boolean updateOnly;
    private boolean skipUpdateForUnresolved;

    private String language;

    private String context;

    private CsvImportDescriptor descriptor; //complex fields.
    private CsvImportDescriptor parentDescriptor;

    private transient Pattern pattern = null;


    public CsvImportColumnImpl() {
        super();
    }

    /**
     * Construct import column.
     *
     * @param columnIndex 0 based index
     * @param fieldType   field type
     * @param name        name
     * @param valueRegEx      regular expression to extract data
     * @param lookupQuery lookup query to determinate duplication, in this case the update
     *                    or insert strategy will be selected
     */
    public CsvImportColumnImpl(final int columnIndex, final String fieldType,
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
            Matcher matcher = getPattern().matcher(rawValue);
            if (matcher.find()) {
                if (StringUtils.isNotBlank(valueRegExTemplate) || valueRegExGroup != null) {
                    return 1;
                }
                return matcher.groupCount();
            }
        }
        return 0;
    }


    /**
     * {@inheritDoc}
     */
    public List getValues(final String rawValue, final ValueAdapter adapter, final ImportTuple tuple) {
        List result = new ArrayList();
        if (getPattern() != null) {
            Matcher matcher = getPattern().matcher(rawValue);
            if (matcher.find()) {
                int groupCount = getGroupCount(rawValue);
                for (int i = 0; i < groupCount; i++) {
                    result.add(adapter.fromRaw(matcher.group(i + 1).trim(), getDataType(), this, tuple));
                }
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Object getValue(final Object rawValue, final ValueAdapter adapter, final ExportTuple tuple) {
        final String value;
        if (rawValue == null) {
            value = null;
        } else {
            value = String.valueOf(rawValue);
        }
        return getValue(value, adapter, tuple);
    }

    /**
     * {@inheritDoc}
     */
    public Object getValue(final String rawValue, final ValueAdapter adapter, final ImportTuple tuple) {
        if (getValueConstant() != null) {
            return adapter.fromRaw(getValueConstant(), getDataType(), this, tuple);
        } else if (rawValue != null) {
            if (getPattern() != null) {
                Matcher matcher = getPattern().matcher(rawValue);
                if (StringUtils.isBlank(getValueRegExTemplate())) {
                    if (matcher.find()) {
                        return adapter.fromRaw(matcher.group(getValueRegExGroup()).trim(), getDataType(), this, tuple);
                    } else {
                        return null;
                    }
                } else {
                    if (matcher.matches()) {
                        return adapter.fromRaw(matcher.replaceFirst(getValueRegExTemplate()).trim(), getDataType(), this, tuple);
                    } else {
                        return null;
                    }
                }
            }
            return adapter.fromRaw(rawValue, getDataType(), this, tuple);
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
     * Set column index.
     *
     * @param columnIndex column index
     */
    public void setColumnIndex(final int columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * {@inheritDoc}
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * Set the field type
     *
     * @param fieldType to set.
     */
    public void setFieldType(final String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * {@inheritDoc}
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Set the data type
     *
     * @param dataType to set.
     */
    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * Set the field name.
     *
     * @param name field name.
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
     * Set optional regexp to get the value from csv field.
     *
     * @param valueRegEx regular expression.
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
     * @param valueRegExGroup group that defines the value in regex specified
     */
    public void setValueRegExGroup(final Integer valueRegExGroup) {
        this.valueRegExGroup = valueRegExGroup;
    }

    /**
     * {@inheritDoc}
     */
    public String getValueRegExTemplate() {
        return valueRegExTemplate;
    }

    /**
     * @param valueRegExTemplate reg ex template
     */
    public void setValueRegExTemplate(final String valueRegExTemplate) {
        this.valueRegExTemplate = valueRegExTemplate;
    }

    /**
     * {@inheritDoc}
     */
    public String getLookupQuery() {
        return lookupQuery;
    }

    /**
     * Set the HQL query.
     *
     * @param lookupQuery HQL query.
     */
    public void setLookupQuery(final String lookupQuery) {
        this.lookupQuery = lookupQuery;
    }

    /**
     * {@inheritDoc}
     */
    public CsvImportDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Set included import descriptor for complex fields.
     *
     * @param descriptor {@link ImportDescriptor}
     */
    public void setDescriptor(CsvImportDescriptor descriptor) {
        this.descriptor = descriptor;
    }


    /**
     * {@inheritDoc}
     */
    public CsvImportDescriptor getParentDescriptor() {
        return parentDescriptor;
    }

    /**
     * Set parent descriptor.
     *
     * @param parentDescriptor parent
     */
    public void setParentDescriptor(final CsvImportDescriptor parentDescriptor) {
        this.parentDescriptor = parentDescriptor;
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
    public boolean isInsertOnly() {
        return insertOnly;
    }

    /**
     * {@inheritDoc}
     */
    public void setInsertOnly(final boolean insertOnly) {
        this.insertOnly = insertOnly;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUpdateOnly() {
        return updateOnly;
    }

    /**
     * {@inheritDoc}
     */
    public void setUpdateOnly(final boolean updateOnly) {
        this.updateOnly = updateOnly;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSkipUpdateForUnresolved() {
        return skipUpdateForUnresolved;
    }

    /**
     * {@inheritDoc}
     */
    public void setSkipUpdateForUnresolved(final boolean skipUpdateForUnresolved) {
        this.skipUpdateForUnresolved = skipUpdateForUnresolved;
    }

    /**
     * {@inheritDoc}
     */
    public String getValueConstant() {
        return valueConstant;
    }

    /**
     * Set the constants
     *
     * @param valueConstant string constant
     */
    public void setValueConstant(final String valueConstant) {
        this.valueConstant = valueConstant;
    }


    /**
     * {@inheritDoc}
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @param entityType entity type for FK's
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
     * @param language language of the localisable value (or null if this is not localisable)
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * {@inheritDoc}
     */
    public String getContext() {
        return context;
    }

    /**
     * @param context additional column context
     */
    public void setContext(final String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "CsvImportColumnImpl{" +
                "columnIndex=" + columnIndex +
                ", fieldType=" + fieldType +
                ", name='" + name + '\'' +
                ", valueRegEx='" + valueRegEx + '\'' +
                ", valueRegExGroup=" + valueRegExGroup +
                ", valueRegExTemplate='" + valueRegExTemplate + '\'' +
                ", lookupQuery='" + lookupQuery + '\'' +
                ", useMasterObject=" + useMasterObject +
                ", importDescriptor=" + descriptor +
                ", pattern=" + pattern +
                ", valueConstant='" + valueConstant + '\'' +
                ", language='" + language + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}
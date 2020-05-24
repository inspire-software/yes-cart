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
import org.yes.cart.bulkcommon.csv.CsvImpExTuple;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.bulkcommon.model.ImpExValues;
import org.yes.cart.bulkexport.csv.CsvExportColumn;
import org.yes.cart.bulkexport.csv.CsvExportDescriptor;
import org.yes.cart.bulkexport.csv.CsvExportTuple;
import org.yes.cart.bulkexport.model.ExportDescriptor;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 11:24
 */
public class CsvExportColumnImpl implements CsvExportColumn, Serializable {

    private String columnHeader;

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

    private String language;
    private String context;

    private CsvExportDescriptor descriptor; //complex fields.
    private CsvExportDescriptor parentDescriptor;

    private transient Pattern pattern = null;


    public CsvExportColumnImpl() {
        super();
    }

    /**
     * Construct import column.
     *
     * @param columnHeader header
     * @param fieldType   field type
     * @param name        name
     * @param valueRegEx      regular expression to extract data
     * @param lookupQuery lookup query to determinate duplication, in this case the update
     *                    or insert strategy will be selected
     */
    public CsvExportColumnImpl(final String columnHeader, final String fieldType,
                               final String name, final String valueRegEx, final String lookupQuery) {
        super();
        this.columnHeader = columnHeader;
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
    @Override
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
    @Override
    public String getValue(final Object rawValue, final CsvValueAdapter adapter, final CsvExportTuple tuple) {
        if (getValueConstant() != null) {
            return getValueConstant();
        } else if (rawValue != null) {

            final String strValue = (String) adapter.fromRaw(rawValue, ImpExValues.STRING, this, tuple);

            if (strValue != null) {
                if (getPattern() != null) {

                    Matcher matcher = getPattern().matcher(strValue);
                    if (StringUtils.isBlank(getValueRegExTemplate())) {
                        if (matcher.find()) {
                            return matcher.group(getValueRegExGroup()).trim();
                        } else {
                            return null;
                        }
                    } else {
                        if (matcher.matches()) {
                            return matcher.replaceFirst(getValueRegExTemplate()).trim();
                        } else {
                            return null;
                        }
                    }
                }
                return strValue;
            }
        }
        return null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(final Object rawValue, final CsvValueAdapter adapter, final CsvImpExTuple tuple) {
        return getValue(rawValue, adapter, (CsvExportTuple) tuple);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnHeader() {
        return columnHeader;
    }

    /**
     * Set column header.
     *
     * @param columnHeader column header
     */
    public void setColumnHeader(final String columnHeader) {
        this.columnHeader = columnHeader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public CsvExportDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Set included import descriptor for complex fields.
     *
     * @param descriptor {@link ExportDescriptor}
     */
    public void setDescriptor(CsvExportDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CsvExportDescriptor getParentDescriptor() {
        return parentDescriptor;
    }

    /**
     * Set parent descriptor.
     *
     * @param parentDescriptor parent
     */
    @Override
    public void setParentDescriptor(final CsvExportDescriptor parentDescriptor) {
        this.parentDescriptor = parentDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUseMasterObject() {
        return useMasterObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUseMasterObject(final boolean useMasterObject) {
        this.useMasterObject = useMasterObject;
    }


    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
                "columnHeader=" + columnHeader +
                ", fieldType=" + fieldType +
                ", name='" + name + '\'' +
                ", valueRegEx='" + valueRegEx + '\'' +
                ", valueRegExGroup=" + valueRegExGroup +
                ", valueRegExTemplate='" + valueRegExTemplate + '\'' +
                ", lookupQuery='" + lookupQuery + '\'' +
                ", useMasterObject=" + useMasterObject +
                ", exportDescriptor=" + descriptor +
                ", pattern=" + pattern +
                ", valueConstant='" + valueConstant + '\'' +
                ", language='" + language + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}
package org.yes.cart.bulkimport.csv.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.model.FieldTypeEnum;
import org.yes.cart.bulkimport.model.ImportDescriptor;

import java.io.Serializable;
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

    private String name;

    private String regExp;

    private String lookupQuery; //for locate fk and pk

    private boolean useMasterObject;

    private ImportDescriptor importDescriptor; //comple fileds.

    private transient Pattern pattern = null;

    private String constant;


    public CsvImportColumnImpl() {
        super();
    }

    /**
     * Construct import column.
     *
     * @param columnIndex 0 based index
     * @param fieldType   {@link FieldTypeEnum} filed type
     * @param name        name
     * @param regExp      regular expression to extract data
     * @param lookupQuery lookup query to determinate duplication, in this case the update
     *                    or insert strategy will be selected
     */
    public CsvImportColumnImpl(final int columnIndex, final FieldTypeEnum fieldType,
                               final String name, final String regExp, final String lookupQuery) {
        super();
        this.columnIndex = columnIndex;
        this.fieldType = fieldType;
        this.name = name;
        this.regExp = regExp;
        this.lookupQuery = lookupQuery;
    }

    private Pattern getPattern() {
        if (pattern == null && StringUtils.isNotBlank(regExp)) {
            pattern = Pattern.compile(regExp, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        }
        return pattern;
    }


    /**
     * In case if column has reg exp.
     *
     * @return mathced groups or 0 if column has not reg exp.
     */
    public int getGroupCount(final String rawValue) {
        if (getPattern() != null) {
            if (forceGroupCount == 0) {
                Matcher matcher = getPattern().matcher(rawValue);
                if (matcher.find()) {
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
    public String[] getValues(final String rawValue) {
        if (getPattern() != null) {
            Matcher matcher = getPattern().matcher(rawValue);
            if (matcher.find()) {
                int groupCount = getGroupCount(rawValue);
                String[] result = new String[groupCount];
                for (int i = 0; i < groupCount; i++) {
                    result[i] = matcher.group(i + 1).trim();
                }
                return result;
            }
        }
        return new String[0];
    }

    /**
     * {@inheritDoc}
     */
    public String getValue(final String rawValue) {
        if (getPattern() != null) {
            Matcher matcher = getPattern().matcher(rawValue);
            if (matcher.find()) {
                return matcher.group(1).trim();
            } else {
                return null;
            }
        }
        return rawValue;

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
    public String getRegExp() {
        return regExp;
    }

    /**
     * {@inheritDoc}
     */
    public void setRegExp(final String regExp) {
        this.regExp = regExp;
        this.pattern = null;
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
    public String getConstant() {
        return constant;
    }

    /**
     * {@inheritDoc}
     */
    public void setConstant(final String constant) {
        this.constant = constant;
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

    @Override
    public String toString() {
        return "CsvImportColumnImpl{" +
                "columnIndex=" + columnIndex +
                ", fieldType=" + fieldType +
                ", name='" + name + '\'' +
                ", regExp='" + regExp + '\'' +
                ", lookupQuery='" + lookupQuery + '\'' +
                ", useMasterObject=" + useMasterObject +
                ", importDescriptor=" + importDescriptor +
                ", pattern=" + pattern +
                ", constant='" + constant + '\'' +
                ", forceGroupCount='" + forceGroupCount + '\'' +
                '}';
    }
}
package org.yes.cart.bulkimport.model;

/**
 * Single import line description.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public interface ImportColumn {

    /**
     * Get the field value. Regular expression will be used for obtain value if reg exp is set.
     *
     * @param rawValue the whole value from cell
     * @return value
     */
    String getValue(String rawValue);


    /**
     * Get the field value as string array via reg exp.
     *
     * @param rawValue the whole value from cell
     * @return field value as string array
     */
    String[] getValues(String rawValue);

    /**
     * In case if column has reg exp.
     *
     * @param rawValue the whole value from cell
     * @return mathced groups or 0 if column has not reg exp.
     */
    int getGroupCount(String rawValue);

    /**
     * Column index from csv file, 1 based
     *
     * @return column index
     */
    int getColumnIndex();

    /**
     * Set column index.
     *
     * @param columnIndex column index
     */
    void setColumnIndex(int columnIndex);

    /**
     * Get the {@link FieldTypeEnum}.
     *
     * @return filed type
     */
    FieldTypeEnum getFieldType();

    /**
     * Set the {@link FieldTypeEnum}
     *
     * @param fieldType to set.
     */
    void setFieldType(FieldTypeEnum fieldType);

    /**
     * Filed name in object in java beans notation.
     *
     * @return filed name.
     */
    String getName();

    /**
     * Set the filed name.
     *
     * @param name fileld name.
     */
    void setName(String name);

    /**
     * Regular expression for get value for specified filed name
     * in case if csv field contains more than one object field.
     * Example:  gold,855
     *
     * @return regula expression to get the value from csv field
     */
    String getRegExp();

    /**
     * Set optional regexp to get the value from csv field.
     *
     * @param regExp regular expression.
     */
    void setRegExp(String regExp);

    /**
     * Get the HQL lookup query to get the foreign key object within csv filed value
     * or value from csv gathered via regexp.
     *
     * @return HQL lookup query.
     */
    String getLookupQuery();

    /**
     * Set the HQL query.
     *
     * @param lookupQuery HQL query.
     */
    void setLookupQuery(String lookupQuery);


    /**
     * Get included import descriptor for complex fields.
     *
     * @return {@link ImportDescriptor}
     */
    ImportDescriptor getImportDescriptor();


    /**
     * Set included import descriptor for complex fields.
     *
     * @param importDescriptor {@link ImportDescriptor}
     */
    void setImportDescriptor(ImportDescriptor importDescriptor);

    /**
     * Boolean flag need to use master object for fk in case of subimport.
     *
     * @return true if need to use master object.
     */
    boolean isUseMasterObject();

    /**
     * Set use master object in case of fk subimport.
     *
     * @param useMasterObject use master object flag.
     */
    void setUseMasterObject(boolean useMasterObject);

    /**
     * Get the constant for field. Some fields can be filedd with constants
     *
     * @return filed constant
     */
    String getConstant();

    /**
     * Set the constants
     *
     * @param constant string constant
     */
    void setConstant(String constant);


}

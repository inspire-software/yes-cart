package org.yes.cart.bulkimport.model;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public enum FieldTypeEnum {

    FIELD("field"),
    FK_FIELD("fk_field"),
    SIMPLE_SLAVE_FIELD("simple_slave_field");

    private final String fieldType;

    FieldTypeEnum(final String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Return the enum constant from the String representation
     *
     * @param fieldType string representation of fieldType
     * @return FieldTypeImpl instance if found, otherwise IllegalArgumentException will be thrown.
     */
    public static FieldTypeEnum fromValue(final String fieldType) {
        if (fieldType != null) {
            for (FieldTypeEnum myFieldType : FieldTypeEnum.values()) {
                if (myFieldType.getFieldType().equals(fieldType)) {
                    return myFieldType;
                }
            }
        }
        throw new IllegalArgumentException(fieldType);
    }

    /**
     * Get string representation of FieldTypeImpl.
     *
     * @return string representation.
     */
    public String getFieldType() {
        return fieldType;
    }


}

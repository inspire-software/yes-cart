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

package org.yes.cart.bulkimport.model;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public enum FieldTypeEnum {

    FIELD("field"),
    FK_FIELD("fk_field"),
    SIMPLE_SLAVE_FIELD("simple_slave_field"),
    KEYVALUE_SLAVE_FIELD("keyvalue_slave_field");

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

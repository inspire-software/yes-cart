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
public enum ImportStrategyTypeEnum {

    INSERT("insert"),
    UPDATE("update");

    private final String importStrategyType;

    ImportStrategyTypeEnum(final String importStrategyType) {
        this.importStrategyType = importStrategyType;
    }

    /**
     * Return the enum constant from the String representation
     *
     * @param importStrategyType string representation of ImportStrategyTypeImpl
     * @return FieldTypeImpl instance if found, otherwise IllegalArgumentException will be thrown.
     */
    public static ImportStrategyTypeEnum fromValue(final String importStrategyType) {
        if (importStrategyType != null) {
            for (ImportStrategyTypeEnum myImportStrategy : ImportStrategyTypeEnum.values()) {
                if (myImportStrategy.getImportStrategyType().equals(importStrategyType)) {
                    return myImportStrategy;
                }
            }
        }
        throw new IllegalArgumentException(importStrategyType);
    }

    /**
     * Get string representation of ImportStrategyTypeImpl.
     *
     * @return string representation.
     */
    public String getImportStrategyType() {
        return importStrategyType;
    }

}

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

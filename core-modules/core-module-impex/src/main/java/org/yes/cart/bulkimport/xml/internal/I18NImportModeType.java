
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for i18nImportModeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="i18nImportModeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MERGE"/>
 *     &lt;enumeration value="REPLACE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "i18nImportModeType")
@XmlEnum
public enum I18NImportModeType {

    MERGE,
    REPLACE;

    public String value() {
        return name();
    }

    public static I18NImportModeType fromValue(String v) {
        return valueOf(v);
    }

}

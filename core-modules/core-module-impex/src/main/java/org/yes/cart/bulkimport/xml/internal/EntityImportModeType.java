
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for entityImportModeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="entityImportModeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MERGE"/>
 *     &lt;enumeration value="DELETE"/>
 *     &lt;enumeration value="INSERT_ONLY"/>
 *     &lt;enumeration value="UPDATE_ONLY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "entityImportModeType")
@XmlEnum
public enum EntityImportModeType {

    MERGE,
    DELETE,
    INSERT_ONLY,
    UPDATE_ONLY;

    public String value() {
        return name();
    }

    public static EntityImportModeType fromValue(String v) {
        return valueOf(v);
    }

}

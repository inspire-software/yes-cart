
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for collectionImportModeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="collectionImportModeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MERGE"/>
 *     &lt;enumeration value="DELETE"/>
 *     &lt;enumeration value="REPLACE"/>
 *     &lt;enumeration value="INSERT_ONLY"/>
 *     &lt;enumeration value="UPDATE_ONLY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "collectionImportModeType")
@XmlEnum
public enum CollectionImportModeType {

    MERGE,
    DELETE,
    REPLACE,
    INSERT_ONLY,
    UPDATE_ONLY;

    public String value() {
        return name();
    }

    public static CollectionImportModeType fromValue(String v) {
        return valueOf(v);
    }

}

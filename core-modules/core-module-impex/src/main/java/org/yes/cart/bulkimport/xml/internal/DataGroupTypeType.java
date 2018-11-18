
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for data-groupTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="data-groupTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="IMPORT"/>
 *     &lt;enumeration value="EXPORT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "data-groupTypeType")
@XmlEnum
public enum DataGroupTypeType {

    IMPORT,
    EXPORT;

    public String value() {
        return name();
    }

    public static DataGroupTypeType fromValue(String v) {
        return valueOf(v);
    }

}

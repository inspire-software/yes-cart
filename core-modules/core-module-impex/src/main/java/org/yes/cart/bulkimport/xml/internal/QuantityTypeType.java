
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for quantityTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="quantityTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="stock"/>
 *     &lt;enumeration value="reserved"/>
 *     &lt;enumeration value="ats"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "quantityTypeType")
@XmlEnum
public enum QuantityTypeType {

    @XmlEnumValue("stock")
    STOCK("stock"),
    @XmlEnumValue("reserved")
    RESERVED("reserved"),
    @XmlEnumValue("ats")
    ATS("ats");
    private final String value;

    QuantityTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static QuantityTypeType fromValue(String v) {
        for (QuantityTypeType c: QuantityTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

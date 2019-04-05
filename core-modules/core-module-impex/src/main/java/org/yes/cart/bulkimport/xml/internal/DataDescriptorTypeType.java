
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for data-descriptorTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="data-descriptorTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="WEBINF_XML/CSV"/>
 *     &lt;enumeration value="RAW_XML/CSV"/>
 *     &lt;enumeration value="WEBINF_XML/XML"/>
 *     &lt;enumeration value="RAW_XML/XML"/>
 *     &lt;enumeration value="WEBINF_XML/JSON"/>
 *     &lt;enumeration value="RAW_XML/JSON"/>
 *     &lt;enumeration value="WEBINF_XML"/>
 *     &lt;enumeration value="RAWINF_XML"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "data-descriptorTypeType")
@XmlEnum
public enum DataDescriptorTypeType {

    @XmlEnumValue("WEBINF_XML/CSV")
    WEBINF_XML_CSV("WEBINF_XML/CSV"),
    @XmlEnumValue("RAW_XML/CSV")
    RAW_XML_CSV("RAW_XML/CSV"),
    @XmlEnumValue("WEBINF_XML/XML")
    WEBINF_XML_XML("WEBINF_XML/XML"),
    @XmlEnumValue("RAW_XML/XML")
    RAW_XML_XML("RAW_XML/XML"),
    @XmlEnumValue("WEBINF_XML/JSON")
    WEBINF_XML_JSON("WEBINF_XML/JSON"),
    @XmlEnumValue("RAW_XML/JSON")
    RAW_XML_JSON("RAW_XML/JSON"),
    WEBINF_XML("WEBINF_XML"),
    RAWINF_XML("RAWINF_XML");
    private final String value;

    DataDescriptorTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DataDescriptorTypeType fromValue(String v) {
        for (DataDescriptorTypeType c: DataDescriptorTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

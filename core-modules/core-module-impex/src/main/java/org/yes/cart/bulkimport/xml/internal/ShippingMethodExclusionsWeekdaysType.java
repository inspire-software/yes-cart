
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shipping-method-exclusions-weekdaysType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shipping-method-exclusions-weekdaysType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="weekday" maxOccurs="unbounded">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="MONDAY"/>
 *               &lt;enumeration value="TUESDAY"/>
 *               &lt;enumeration value="WEDNESDAY"/>
 *               &lt;enumeration value="THURSDAY"/>
 *               &lt;enumeration value="FRIDAY"/>
 *               &lt;enumeration value="SATURDAY"/>
 *               &lt;enumeration value="SUNDAY"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shipping-method-exclusions-weekdaysType", propOrder = {
    "weekday"
})
public class ShippingMethodExclusionsWeekdaysType {

    @XmlElement(required = true)
    protected List<String> weekday;

    /**
     * Gets the value of the weekday property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the weekday property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWeekday().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getWeekday() {
        if (weekday == null) {
            weekday = new ArrayList<String>();
        }
        return this.weekday;
    }

}

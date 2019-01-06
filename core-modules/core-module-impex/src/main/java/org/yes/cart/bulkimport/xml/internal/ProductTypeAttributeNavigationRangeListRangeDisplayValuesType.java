
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for product-type-attribute-navigation-range-list-range-display-valuesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="product-type-attribute-navigation-range-list-range-display-valuesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="display" type="{}product-type-attribute-navigation-range-list-range-display-values-valueType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "product-type-attribute-navigation-range-list-range-display-valuesType", propOrder = {
    "display"
})
public class ProductTypeAttributeNavigationRangeListRangeDisplayValuesType {

    @XmlElement(required = true)
    protected List<ProductTypeAttributeNavigationRangeListRangeDisplayValuesValueType> display;

    /**
     * Gets the value of the display property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the display property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDisplay().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductTypeAttributeNavigationRangeListRangeDisplayValuesValueType }
     * 
     * 
     */
    public List<ProductTypeAttributeNavigationRangeListRangeDisplayValuesValueType> getDisplay() {
        if (display == null) {
            display = new ArrayList<ProductTypeAttributeNavigationRangeListRangeDisplayValuesValueType>();
        }
        return this.display;
    }

}

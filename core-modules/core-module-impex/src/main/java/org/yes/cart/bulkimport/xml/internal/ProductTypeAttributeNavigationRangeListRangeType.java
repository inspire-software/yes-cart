
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for product-type-attribute-navigation-range-list-rangeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="product-type-attribute-navigation-range-list-rangeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="from" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="to" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="i18n" type="{}product-type-attribute-navigation-range-list-range-display-valuesType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "product-type-attribute-navigation-range-list-rangeType", propOrder = {
    "from",
    "to",
    "i18N"
})
public class ProductTypeAttributeNavigationRangeListRangeType {

    @XmlElement(required = true)
    protected String from;
    @XmlElement(required = true)
    protected String to;
    @XmlElement(name = "i18n")
    protected ProductTypeAttributeNavigationRangeListRangeDisplayValuesType i18N;

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrom(String value) {
        this.from = value;
    }

    /**
     * Gets the value of the to property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTo(String value) {
        this.to = value;
    }

    /**
     * Gets the value of the i18N property.
     * 
     * @return
     *     possible object is
     *     {@link ProductTypeAttributeNavigationRangeListRangeDisplayValuesType }
     *     
     */
    public ProductTypeAttributeNavigationRangeListRangeDisplayValuesType getI18N() {
        return i18N;
    }

    /**
     * Sets the value of the i18N property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductTypeAttributeNavigationRangeListRangeDisplayValuesType }
     *     
     */
    public void setI18N(ProductTypeAttributeNavigationRangeListRangeDisplayValuesType value) {
        this.i18N = value;
    }

}

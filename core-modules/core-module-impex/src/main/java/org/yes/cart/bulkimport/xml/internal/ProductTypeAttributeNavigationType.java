
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for product-type-attribute-navigationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="product-type-attribute-navigationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="range-list" type="{}product-type-attribute-navigation-range-listType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="template" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "product-type-attribute-navigationType", propOrder = {
    "rangeList"
})
public class ProductTypeAttributeNavigationType {

    @XmlElement(name = "range-list")
    protected ProductTypeAttributeNavigationRangeListType rangeList;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "template")
    protected String template;

    /**
     * Gets the value of the rangeList property.
     * 
     * @return
     *     possible object is
     *     {@link ProductTypeAttributeNavigationRangeListType }
     *     
     */
    public ProductTypeAttributeNavigationRangeListType getRangeList() {
        return rangeList;
    }

    /**
     * Sets the value of the rangeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductTypeAttributeNavigationRangeListType }
     *     
     */
    public void setRangeList(ProductTypeAttributeNavigationRangeListType value) {
        this.rangeList = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTemplate(String value) {
        this.template = value;
    }

}

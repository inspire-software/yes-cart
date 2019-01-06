
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for product-type-attribute-navigation-range-listType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="product-type-attribute-navigation-range-listType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ranges">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="range" type="{}product-type-attribute-navigation-range-list-rangeType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
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
@XmlType(name = "product-type-attribute-navigation-range-listType", propOrder = {
    "ranges"
})
public class ProductTypeAttributeNavigationRangeListType {

    @XmlElement(required = true)
    protected ProductTypeAttributeNavigationRangeListType.Ranges ranges;

    /**
     * Gets the value of the ranges property.
     * 
     * @return
     *     possible object is
     *     {@link ProductTypeAttributeNavigationRangeListType.Ranges }
     *     
     */
    public ProductTypeAttributeNavigationRangeListType.Ranges getRanges() {
        return ranges;
    }

    /**
     * Sets the value of the ranges property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductTypeAttributeNavigationRangeListType.Ranges }
     *     
     */
    public void setRanges(ProductTypeAttributeNavigationRangeListType.Ranges value) {
        this.ranges = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="range" type="{}product-type-attribute-navigation-range-list-rangeType" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "range"
    })
    public static class Ranges {

        protected List<ProductTypeAttributeNavigationRangeListRangeType> range;

        /**
         * Gets the value of the range property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the range property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRange().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ProductTypeAttributeNavigationRangeListRangeType }
         * 
         * 
         */
        public List<ProductTypeAttributeNavigationRangeListRangeType> getRange() {
            if (range == null) {
                range = new ArrayList<ProductTypeAttributeNavigationRangeListRangeType>();
            }
            return this.range;
        }

    }

}

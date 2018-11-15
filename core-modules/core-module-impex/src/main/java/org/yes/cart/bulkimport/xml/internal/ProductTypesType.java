
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for product-typesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="product-typesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="product-type" type="{}product-typeTypeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "product-typesType", propOrder = {
    "productType"
})
public class ProductTypesType {

    @XmlElement(name = "product-type")
    protected List<ProductTypeTypeType> productType;

    /**
     * Gets the value of the productType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductTypeTypeType }
     * 
     * 
     */
    public List<ProductTypeTypeType> getProductType() {
        if (productType == null) {
            productType = new ArrayList<ProductTypeTypeType>();
        }
        return this.productType;
    }

}

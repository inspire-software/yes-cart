
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for products-linksCodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="products-linksCodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="product-links" type="{}product-linksCodeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "products-linksCodeType", propOrder = {
    "productLinks"
})
public class ProductsLinksCodeType {

    @XmlElement(name = "product-links")
    protected List<ProductLinksCodeType> productLinks;

    /**
     * Gets the value of the productLinks property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productLinks property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductLinks().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProductLinksCodeType }
     * 
     * 
     */
    public List<ProductLinksCodeType> getProductLinks() {
        if (productLinks == null) {
            productLinks = new ArrayList<ProductLinksCodeType>();
        }
        return this.productLinks;
    }

}

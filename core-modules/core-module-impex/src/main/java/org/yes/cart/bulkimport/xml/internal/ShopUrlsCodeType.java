
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shop-urlsCodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shop-urlsCodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shop-urls" type="{}shop-urlCodeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shop-urlsCodeType", propOrder = {
    "shopUrls"
})
public class ShopUrlsCodeType {

    @XmlElement(name = "shop-urls")
    protected List<ShopUrlCodeType> shopUrls;

    /**
     * Gets the value of the shopUrls property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shopUrls property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShopUrls().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShopUrlCodeType }
     * 
     * 
     */
    public List<ShopUrlCodeType> getShopUrls() {
        if (shopUrls == null) {
            shopUrls = new ArrayList<ShopUrlCodeType>();
        }
        return this.shopUrls;
    }

}


package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shops-carriersCodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shops-carriersCodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shop-carriers" type="{}shop-carriersCodeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shops-carriersCodeType", propOrder = {
    "shopCarriers"
})
public class ShopsCarriersCodeType {

    @XmlElement(name = "shop-carriers")
    protected List<ShopCarriersCodeType> shopCarriers;

    /**
     * Gets the value of the shopCarriers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shopCarriers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShopCarriers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShopCarriersCodeType }
     * 
     * 
     */
    public List<ShopCarriersCodeType> getShopCarriers() {
        if (shopCarriers == null) {
            shopCarriers = new ArrayList<ShopCarriersCodeType>();
        }
        return this.shopCarriers;
    }

}

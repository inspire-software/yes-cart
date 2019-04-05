
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shops-fulfilment-centresCodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shops-fulfilment-centresCodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shop-fulfilment-centres" type="{}shop-fulfilment-centresCodeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shops-fulfilment-centresCodeType", propOrder = {
    "shopFulfilmentCentres"
})
public class ShopsFulfilmentCentresCodeType {

    @XmlElement(name = "shop-fulfilment-centres")
    protected List<ShopFulfilmentCentresCodeType> shopFulfilmentCentres;

    /**
     * Gets the value of the shopFulfilmentCentres property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shopFulfilmentCentres property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShopFulfilmentCentres().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShopFulfilmentCentresCodeType }
     * 
     * 
     */
    public List<ShopFulfilmentCentresCodeType> getShopFulfilmentCentres() {
        if (shopFulfilmentCentres == null) {
            shopFulfilmentCentres = new ArrayList<ShopFulfilmentCentresCodeType>();
        }
        return this.shopFulfilmentCentres;
    }

}

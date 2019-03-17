
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shops-categoriesCodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shops-categoriesCodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shop-categories" type="{}shop-categoriesCodeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shops-categoriesCodeType", propOrder = {
    "shopCategories"
})
public class ShopsCategoriesCodeType {

    @XmlElement(name = "shop-categories")
    protected List<ShopCategoriesCodeType> shopCategories;

    /**
     * Gets the value of the shopCategories property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shopCategories property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShopCategories().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShopCategoriesCodeType }
     * 
     * 
     */
    public List<ShopCategoriesCodeType> getShopCategories() {
        if (shopCategories == null) {
            shopCategories = new ArrayList<ShopCategoriesCodeType>();
        }
        return this.shopCategories;
    }

}

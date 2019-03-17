
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shop-aliasesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shop-aliasesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shop-alias" type="{}shop-aliasType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="import-mode" type="{}collectionImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shop-aliasesType", propOrder = {
    "shopAlias"
})
public class ShopAliasesType {

    @XmlElement(name = "shop-alias")
    protected List<ShopAliasType> shopAlias;
    @XmlAttribute(name = "import-mode")
    protected CollectionImportModeType importMode;

    /**
     * Gets the value of the shopAlias property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shopAlias property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShopAlias().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShopAliasType }
     * 
     * 
     */
    public List<ShopAliasType> getShopAlias() {
        if (shopAlias == null) {
            shopAlias = new ArrayList<ShopAliasType>();
        }
        return this.shopAlias;
    }

    /**
     * Gets the value of the importMode property.
     * 
     * @return
     *     possible object is
     *     {@link CollectionImportModeType }
     *     
     */
    public CollectionImportModeType getImportMode() {
        return importMode;
    }

    /**
     * Sets the value of the importMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link CollectionImportModeType }
     *     
     */
    public void setImportMode(CollectionImportModeType value) {
        this.importMode = value;
    }

}


package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shop-urlCodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shop-urlCodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shop-url" type="{}shop-urlType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="shop-code" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="import-mode" type="{}collectionImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shop-urlCodeType", propOrder = {
    "shopUrl"
})
public class ShopUrlCodeType {

    @XmlElement(name = "shop-url")
    protected List<ShopUrlType> shopUrl;
    @XmlAttribute(name = "shop-code")
    protected String shopCode;
    @XmlAttribute(name = "import-mode")
    protected CollectionImportModeType importMode;

    /**
     * Gets the value of the shopUrl property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shopUrl property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShopUrl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShopUrlType }
     * 
     * 
     */
    public List<ShopUrlType> getShopUrl() {
        if (shopUrl == null) {
            shopUrl = new ArrayList<ShopUrlType>();
        }
        return this.shopUrl;
    }

    /**
     * Gets the value of the shopCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShopCode() {
        return shopCode;
    }

    /**
     * Sets the value of the shopCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShopCode(String value) {
        this.shopCode = value;
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


package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shop-fulfilment-centresCodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shop-fulfilment-centresCodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shop-fulfilment-centre" type="{}shop-fulfilment-centreType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "shop-fulfilment-centresCodeType", propOrder = {
    "shopFulfilmentCentre"
})
public class ShopFulfilmentCentresCodeType {

    @XmlElement(name = "shop-fulfilment-centre")
    protected List<ShopFulfilmentCentreType> shopFulfilmentCentre;
    @XmlAttribute(name = "shop-code")
    protected String shopCode;
    @XmlAttribute(name = "import-mode")
    protected CollectionImportModeType importMode;

    /**
     * Gets the value of the shopFulfilmentCentre property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shopFulfilmentCentre property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShopFulfilmentCentre().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShopFulfilmentCentreType }
     * 
     * 
     */
    public List<ShopFulfilmentCentreType> getShopFulfilmentCentre() {
        if (shopFulfilmentCentre == null) {
            shopFulfilmentCentre = new ArrayList<ShopFulfilmentCentreType>();
        }
        return this.shopFulfilmentCentre;
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


package org.yes.cart.bulkimport.xml.internal;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for customer-wishlist-itemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="customer-wishlist-itemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tags" type="{}tagsType"/>
 *         &lt;element name="quantity" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="price" type="{}customer-wishlist-item-priceType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="wishlist-type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="visibility" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sku-code" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fulfilment-centre-code" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="import-mode" type="{}entityImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "customer-wishlist-itemType", propOrder = {
    "tags",
    "quantity",
    "price"
})
public class CustomerWishlistItemType {

    @XmlElement(required = true)
    protected TagsType tags;
    @XmlElement(required = true)
    protected BigDecimal quantity;
    @XmlElement(required = true)
    protected CustomerWishlistItemPriceType price;
    @XmlAttribute(name = "id")
    protected Long id;
    @XmlAttribute(name = "guid")
    protected String guid;
    @XmlAttribute(name = "wishlist-type", required = true)
    protected String wishlistType;
    @XmlAttribute(name = "visibility", required = true)
    protected String visibility;
    @XmlAttribute(name = "sku-code", required = true)
    protected String skuCode;
    @XmlAttribute(name = "fulfilment-centre-code", required = true)
    protected String fulfilmentCentreCode;
    @XmlAttribute(name = "import-mode")
    protected EntityImportModeType importMode;

    /**
     * Gets the value of the tags property.
     * 
     * @return
     *     possible object is
     *     {@link TagsType }
     *     
     */
    public TagsType getTags() {
        return tags;
    }

    /**
     * Sets the value of the tags property.
     * 
     * @param value
     *     allowed object is
     *     {@link TagsType }
     *     
     */
    public void setTags(TagsType value) {
        this.tags = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setQuantity(BigDecimal value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the price property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerWishlistItemPriceType }
     *     
     */
    public CustomerWishlistItemPriceType getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerWishlistItemPriceType }
     *     
     */
    public void setPrice(CustomerWishlistItemPriceType value) {
        this.price = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the guid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuid(String value) {
        this.guid = value;
    }

    /**
     * Gets the value of the wishlistType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWishlistType() {
        return wishlistType;
    }

    /**
     * Sets the value of the wishlistType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWishlistType(String value) {
        this.wishlistType = value;
    }

    /**
     * Gets the value of the visibility property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Sets the value of the visibility property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVisibility(String value) {
        this.visibility = value;
    }

    /**
     * Gets the value of the skuCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSkuCode() {
        return skuCode;
    }

    /**
     * Sets the value of the skuCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSkuCode(String value) {
        this.skuCode = value;
    }

    /**
     * Gets the value of the fulfilmentCentreCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFulfilmentCentreCode() {
        return fulfilmentCentreCode;
    }

    /**
     * Sets the value of the fulfilmentCentreCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFulfilmentCentreCode(String value) {
        this.fulfilmentCentreCode = value;
    }

    /**
     * Gets the value of the importMode property.
     * 
     * @return
     *     possible object is
     *     {@link EntityImportModeType }
     *     
     */
    public EntityImportModeType getImportMode() {
        return importMode;
    }

    /**
     * Sets the value of the importMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityImportModeType }
     *     
     */
    public void setImportMode(EntityImportModeType value) {
        this.importMode = value;
    }

}


package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for shopType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shopType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="presentation" type="{}shop-presentationType" minOccurs="0"/>
 *         &lt;element name="availability" type="{}shop-availabilityType" minOccurs="0"/>
 *         &lt;element name="shop-urls" type="{}shop-urlsType" minOccurs="0"/>
 *         &lt;element name="shop-aliases" type="{}shop-aliasesType" minOccurs="0"/>
 *         &lt;element name="shop-categories" type="{}shop-categoriesType" minOccurs="0"/>
 *         &lt;element name="shop-carriers" type="{}shop-carriersType" minOccurs="0"/>
 *         &lt;element name="shop-fulfilment-centres" type="{}shop-fulfilment-centresType" minOccurs="0"/>
 *         &lt;element name="shop-addressbook" type="{}shop-addressbookType" minOccurs="0"/>
 *         &lt;element name="seo" type="{}seoType" minOccurs="0"/>
 *         &lt;element name="custom-attributes" type="{}custom-attributesType" minOccurs="0"/>
 *         &lt;element name="created-timestamp" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="created-by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated-timestamp" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="updated-by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="code" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="master-code" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="import-mode" type="{}entityImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shopType", propOrder = {
    "presentation",
    "availability",
    "shopUrls",
    "shopAliases",
    "shopCategories",
    "shopCarriers",
    "shopFulfilmentCentres",
    "shopAddressbook",
    "seo",
    "customAttributes",
    "createdTimestamp",
    "createdBy",
    "updatedTimestamp",
    "updatedBy"
})
public class ShopType {

    protected ShopPresentationType presentation;
    protected ShopAvailabilityType availability;
    @XmlElement(name = "shop-urls")
    protected ShopUrlsType shopUrls;
    @XmlElement(name = "shop-aliases")
    protected ShopAliasesType shopAliases;
    @XmlElement(name = "shop-categories")
    protected ShopCategoriesType shopCategories;
    @XmlElement(name = "shop-carriers")
    protected ShopCarriersType shopCarriers;
    @XmlElement(name = "shop-fulfilment-centres")
    protected ShopFulfilmentCentresType shopFulfilmentCentres;
    @XmlElement(name = "shop-addressbook")
    protected ShopAddressbookType shopAddressbook;
    protected SeoType seo;
    @XmlElement(name = "custom-attributes")
    protected CustomAttributesType customAttributes;
    @XmlElement(name = "created-timestamp")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String createdTimestamp;
    @XmlElement(name = "created-by")
    protected String createdBy;
    @XmlElement(name = "updated-timestamp")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String updatedTimestamp;
    @XmlElement(name = "updated-by")
    protected String updatedBy;
    @XmlAttribute(name = "id")
    protected Long id;
    @XmlAttribute(name = "guid")
    protected String guid;
    @XmlAttribute(name = "code", required = true)
    protected String code;
    @XmlAttribute(name = "master-code")
    protected String masterCode;
    @XmlAttribute(name = "import-mode")
    protected EntityImportModeType importMode;

    /**
     * Gets the value of the presentation property.
     * 
     * @return
     *     possible object is
     *     {@link ShopPresentationType }
     *     
     */
    public ShopPresentationType getPresentation() {
        return presentation;
    }

    /**
     * Sets the value of the presentation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShopPresentationType }
     *     
     */
    public void setPresentation(ShopPresentationType value) {
        this.presentation = value;
    }

    /**
     * Gets the value of the availability property.
     * 
     * @return
     *     possible object is
     *     {@link ShopAvailabilityType }
     *     
     */
    public ShopAvailabilityType getAvailability() {
        return availability;
    }

    /**
     * Sets the value of the availability property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShopAvailabilityType }
     *     
     */
    public void setAvailability(ShopAvailabilityType value) {
        this.availability = value;
    }

    /**
     * Gets the value of the shopUrls property.
     * 
     * @return
     *     possible object is
     *     {@link ShopUrlsType }
     *     
     */
    public ShopUrlsType getShopUrls() {
        return shopUrls;
    }

    /**
     * Sets the value of the shopUrls property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShopUrlsType }
     *     
     */
    public void setShopUrls(ShopUrlsType value) {
        this.shopUrls = value;
    }

    /**
     * Gets the value of the shopAliases property.
     * 
     * @return
     *     possible object is
     *     {@link ShopAliasesType }
     *     
     */
    public ShopAliasesType getShopAliases() {
        return shopAliases;
    }

    /**
     * Sets the value of the shopAliases property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShopAliasesType }
     *     
     */
    public void setShopAliases(ShopAliasesType value) {
        this.shopAliases = value;
    }

    /**
     * Gets the value of the shopCategories property.
     * 
     * @return
     *     possible object is
     *     {@link ShopCategoriesType }
     *     
     */
    public ShopCategoriesType getShopCategories() {
        return shopCategories;
    }

    /**
     * Sets the value of the shopCategories property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShopCategoriesType }
     *     
     */
    public void setShopCategories(ShopCategoriesType value) {
        this.shopCategories = value;
    }

    /**
     * Gets the value of the shopCarriers property.
     * 
     * @return
     *     possible object is
     *     {@link ShopCarriersType }
     *     
     */
    public ShopCarriersType getShopCarriers() {
        return shopCarriers;
    }

    /**
     * Sets the value of the shopCarriers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShopCarriersType }
     *     
     */
    public void setShopCarriers(ShopCarriersType value) {
        this.shopCarriers = value;
    }

    /**
     * Gets the value of the shopFulfilmentCentres property.
     * 
     * @return
     *     possible object is
     *     {@link ShopFulfilmentCentresType }
     *     
     */
    public ShopFulfilmentCentresType getShopFulfilmentCentres() {
        return shopFulfilmentCentres;
    }

    /**
     * Sets the value of the shopFulfilmentCentres property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShopFulfilmentCentresType }
     *     
     */
    public void setShopFulfilmentCentres(ShopFulfilmentCentresType value) {
        this.shopFulfilmentCentres = value;
    }

    /**
     * Gets the value of the shopAddressbook property.
     * 
     * @return
     *     possible object is
     *     {@link ShopAddressbookType }
     *     
     */
    public ShopAddressbookType getShopAddressbook() {
        return shopAddressbook;
    }

    /**
     * Sets the value of the shopAddressbook property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShopAddressbookType }
     *     
     */
    public void setShopAddressbook(ShopAddressbookType value) {
        this.shopAddressbook = value;
    }

    /**
     * Gets the value of the seo property.
     * 
     * @return
     *     possible object is
     *     {@link SeoType }
     *     
     */
    public SeoType getSeo() {
        return seo;
    }

    /**
     * Sets the value of the seo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SeoType }
     *     
     */
    public void setSeo(SeoType value) {
        this.seo = value;
    }

    /**
     * Gets the value of the customAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link CustomAttributesType }
     *     
     */
    public CustomAttributesType getCustomAttributes() {
        return customAttributes;
    }

    /**
     * Sets the value of the customAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomAttributesType }
     *     
     */
    public void setCustomAttributes(CustomAttributesType value) {
        this.customAttributes = value;
    }

    /**
     * Gets the value of the createdTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Sets the value of the createdTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedTimestamp(String value) {
        this.createdTimestamp = value;
    }

    /**
     * Gets the value of the createdBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the value of the createdBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedBy(String value) {
        this.createdBy = value;
    }

    /**
     * Gets the value of the updatedTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /**
     * Sets the value of the updatedTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedTimestamp(String value) {
        this.updatedTimestamp = value;
    }

    /**
     * Gets the value of the updatedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the value of the updatedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedBy(String value) {
        this.updatedBy = value;
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
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the masterCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMasterCode() {
        return masterCode;
    }

    /**
     * Sets the value of the masterCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMasterCode(String value) {
        this.masterCode = value;
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

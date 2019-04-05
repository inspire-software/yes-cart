
package org.yes.cart.bulkimport.xml.internal;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for productType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="productType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="brand" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="product-type" type="{}product-typeType"/>
 *         &lt;element name="manufacturer">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="manufacturer-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="manufacturer-part-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="supplier">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="supplier-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="supplier-catalog-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="pim">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="pim-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="pim-outdated" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="pim-updated" type="{}dateTimeType" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="disabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="availability">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="available-from" type="{}dateTimeType" minOccurs="0"/>
 *                   &lt;element name="available-to" type="{}dateTimeType" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="disabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="inventory-config" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="min" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                 &lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                 &lt;attribute name="step" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="display-name" type="{}i18nsType" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tags" type="{}tagsType" minOccurs="0"/>
 *         &lt;element name="seo" type="{}seoType" minOccurs="0"/>
 *         &lt;element name="custom-attributes" type="{}custom-attributesType" minOccurs="0"/>
 *         &lt;element name="product-categories" type="{}product-categoriesType" minOccurs="0"/>
 *         &lt;element name="product-sku" type="{}product-skuType" minOccurs="0"/>
 *         &lt;element name="product-links" type="{}product-linksType" minOccurs="0"/>
 *         &lt;element name="created-timestamp" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="created-by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated-timestamp" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="updated-by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="code" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="featured" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="import-mode" type="{}entityImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "productType", propOrder = {
    "brand",
    "productType",
    "manufacturer",
    "supplier",
    "pim",
    "availability",
    "inventoryConfig",
    "name",
    "displayName",
    "description",
    "tags",
    "seo",
    "customAttributes",
    "productCategories",
    "productSku",
    "productLinks",
    "createdTimestamp",
    "createdBy",
    "updatedTimestamp",
    "updatedBy"
})
public class ProductType {

    @XmlElement(required = true)
    protected String brand;
    @XmlElement(name = "product-type", required = true)
    protected ProductTypeType productType;
    @XmlElement(required = true)
    protected ProductType.Manufacturer manufacturer;
    @XmlElement(required = true)
    protected ProductType.Supplier supplier;
    @XmlElement(required = true)
    protected ProductType.Pim pim;
    @XmlElement(required = true)
    protected ProductType.Availability availability;
    @XmlElement(name = "inventory-config")
    protected ProductType.InventoryConfig inventoryConfig;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(name = "display-name")
    protected I18NsType displayName;
    protected String description;
    protected TagsType tags;
    protected SeoType seo;
    @XmlElement(name = "custom-attributes")
    protected CustomAttributesType customAttributes;
    @XmlElement(name = "product-categories")
    protected ProductCategoriesType productCategories;
    @XmlElement(name = "product-sku")
    protected ProductSkuType productSku;
    @XmlElement(name = "product-links")
    protected ProductLinksType productLinks;
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
    @XmlAttribute(name = "featured")
    protected Boolean featured;
    @XmlAttribute(name = "import-mode")
    protected EntityImportModeType importMode;

    /**
     * Gets the value of the brand property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Sets the value of the brand property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrand(String value) {
        this.brand = value;
    }

    /**
     * Gets the value of the productType property.
     * 
     * @return
     *     possible object is
     *     {@link ProductTypeType }
     *     
     */
    public ProductTypeType getProductType() {
        return productType;
    }

    /**
     * Sets the value of the productType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductTypeType }
     *     
     */
    public void setProductType(ProductTypeType value) {
        this.productType = value;
    }

    /**
     * Gets the value of the manufacturer property.
     * 
     * @return
     *     possible object is
     *     {@link ProductType.Manufacturer }
     *     
     */
    public ProductType.Manufacturer getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the value of the manufacturer property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductType.Manufacturer }
     *     
     */
    public void setManufacturer(ProductType.Manufacturer value) {
        this.manufacturer = value;
    }

    /**
     * Gets the value of the supplier property.
     * 
     * @return
     *     possible object is
     *     {@link ProductType.Supplier }
     *     
     */
    public ProductType.Supplier getSupplier() {
        return supplier;
    }

    /**
     * Sets the value of the supplier property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductType.Supplier }
     *     
     */
    public void setSupplier(ProductType.Supplier value) {
        this.supplier = value;
    }

    /**
     * Gets the value of the pim property.
     * 
     * @return
     *     possible object is
     *     {@link ProductType.Pim }
     *     
     */
    public ProductType.Pim getPim() {
        return pim;
    }

    /**
     * Sets the value of the pim property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductType.Pim }
     *     
     */
    public void setPim(ProductType.Pim value) {
        this.pim = value;
    }

    /**
     * Gets the value of the availability property.
     * 
     * @return
     *     possible object is
     *     {@link ProductType.Availability }
     *     
     */
    public ProductType.Availability getAvailability() {
        return availability;
    }

    /**
     * Sets the value of the availability property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductType.Availability }
     *     
     */
    public void setAvailability(ProductType.Availability value) {
        this.availability = value;
    }

    /**
     * Gets the value of the inventoryConfig property.
     * 
     * @return
     *     possible object is
     *     {@link ProductType.InventoryConfig }
     *     
     */
    public ProductType.InventoryConfig getInventoryConfig() {
        return inventoryConfig;
    }

    /**
     * Sets the value of the inventoryConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductType.InventoryConfig }
     *     
     */
    public void setInventoryConfig(ProductType.InventoryConfig value) {
        this.inventoryConfig = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the displayName property.
     * 
     * @return
     *     possible object is
     *     {@link I18NsType }
     *     
     */
    public I18NsType getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link I18NsType }
     *     
     */
    public void setDisplayName(I18NsType value) {
        this.displayName = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

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
     * Gets the value of the productCategories property.
     * 
     * @return
     *     possible object is
     *     {@link ProductCategoriesType }
     *     
     */
    public ProductCategoriesType getProductCategories() {
        return productCategories;
    }

    /**
     * Sets the value of the productCategories property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductCategoriesType }
     *     
     */
    public void setProductCategories(ProductCategoriesType value) {
        this.productCategories = value;
    }

    /**
     * Gets the value of the productSku property.
     * 
     * @return
     *     possible object is
     *     {@link ProductSkuType }
     *     
     */
    public ProductSkuType getProductSku() {
        return productSku;
    }

    /**
     * Sets the value of the productSku property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductSkuType }
     *     
     */
    public void setProductSku(ProductSkuType value) {
        this.productSku = value;
    }

    /**
     * Gets the value of the productLinks property.
     * 
     * @return
     *     possible object is
     *     {@link ProductLinksType }
     *     
     */
    public ProductLinksType getProductLinks() {
        return productLinks;
    }

    /**
     * Sets the value of the productLinks property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductLinksType }
     *     
     */
    public void setProductLinks(ProductLinksType value) {
        this.productLinks = value;
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
     * Gets the value of the featured property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFeatured() {
        return featured;
    }

    /**
     * Sets the value of the featured property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFeatured(Boolean value) {
        this.featured = value;
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


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="available-from" type="{}dateTimeType" minOccurs="0"/>
     *         &lt;element name="available-to" type="{}dateTimeType" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="disabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "availableFrom",
        "availableTo"
    })
    public static class Availability {

        @XmlElement(name = "available-from")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String availableFrom;
        @XmlElement(name = "available-to")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String availableTo;
        @XmlAttribute(name = "disabled", required = true)
        protected boolean disabled;

        /**
         * Gets the value of the availableFrom property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAvailableFrom() {
            return availableFrom;
        }

        /**
         * Sets the value of the availableFrom property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAvailableFrom(String value) {
            this.availableFrom = value;
        }

        /**
         * Gets the value of the availableTo property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAvailableTo() {
            return availableTo;
        }

        /**
         * Sets the value of the availableTo property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAvailableTo(String value) {
            this.availableTo = value;
        }

        /**
         * Gets the value of the disabled property.
         * 
         */
        public boolean isDisabled() {
            return disabled;
        }

        /**
         * Sets the value of the disabled property.
         * 
         */
        public void setDisabled(boolean value) {
            this.disabled = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="min" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *       &lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *       &lt;attribute name="step" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class InventoryConfig {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "type", required = true)
        protected int type;
        @XmlAttribute(name = "min")
        protected BigDecimal min;
        @XmlAttribute(name = "max")
        protected BigDecimal max;
        @XmlAttribute(name = "step")
        protected BigDecimal step;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the type property.
         * 
         */
        public int getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         */
        public void setType(int value) {
            this.type = value;
        }

        /**
         * Gets the value of the min property.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getMin() {
            return min;
        }

        /**
         * Sets the value of the min property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setMin(BigDecimal value) {
            this.min = value;
        }

        /**
         * Gets the value of the max property.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getMax() {
            return max;
        }

        /**
         * Sets the value of the max property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setMax(BigDecimal value) {
            this.max = value;
        }

        /**
         * Gets the value of the step property.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getStep() {
            return step;
        }

        /**
         * Sets the value of the step property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setStep(BigDecimal value) {
            this.step = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="manufacturer-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="manufacturer-part-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "manufacturerCode",
        "manufacturerPartCode"
    })
    public static class Manufacturer {

        @XmlElement(name = "manufacturer-code")
        protected String manufacturerCode;
        @XmlElement(name = "manufacturer-part-code")
        protected String manufacturerPartCode;

        /**
         * Gets the value of the manufacturerCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getManufacturerCode() {
            return manufacturerCode;
        }

        /**
         * Sets the value of the manufacturerCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setManufacturerCode(String value) {
            this.manufacturerCode = value;
        }

        /**
         * Gets the value of the manufacturerPartCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getManufacturerPartCode() {
            return manufacturerPartCode;
        }

        /**
         * Sets the value of the manufacturerPartCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setManufacturerPartCode(String value) {
            this.manufacturerPartCode = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="pim-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="pim-outdated" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="pim-updated" type="{}dateTimeType" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="disabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "pimCode",
        "pimOutdated",
        "pimUpdated"
    })
    public static class Pim {

        @XmlElement(name = "pim-code")
        protected String pimCode;
        @XmlElement(name = "pim-outdated")
        protected Boolean pimOutdated;
        @XmlElement(name = "pim-updated")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String pimUpdated;
        @XmlAttribute(name = "disabled", required = true)
        protected boolean disabled;

        /**
         * Gets the value of the pimCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPimCode() {
            return pimCode;
        }

        /**
         * Sets the value of the pimCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPimCode(String value) {
            this.pimCode = value;
        }

        /**
         * Gets the value of the pimOutdated property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isPimOutdated() {
            return pimOutdated;
        }

        /**
         * Sets the value of the pimOutdated property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setPimOutdated(Boolean value) {
            this.pimOutdated = value;
        }

        /**
         * Gets the value of the pimUpdated property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPimUpdated() {
            return pimUpdated;
        }

        /**
         * Sets the value of the pimUpdated property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPimUpdated(String value) {
            this.pimUpdated = value;
        }

        /**
         * Gets the value of the disabled property.
         * 
         */
        public boolean isDisabled() {
            return disabled;
        }

        /**
         * Sets the value of the disabled property.
         * 
         */
        public void setDisabled(boolean value) {
            this.disabled = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="supplier-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="supplier-catalog-code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "supplierCode",
        "supplierCatalogCode"
    })
    public static class Supplier {

        @XmlElement(name = "supplier-code")
        protected String supplierCode;
        @XmlElement(name = "supplier-catalog-code")
        protected String supplierCatalogCode;

        /**
         * Gets the value of the supplierCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSupplierCode() {
            return supplierCode;
        }

        /**
         * Sets the value of the supplierCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSupplierCode(String value) {
            this.supplierCode = value;
        }

        /**
         * Gets the value of the supplierCatalogCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSupplierCatalogCode() {
            return supplierCatalogCode;
        }

        /**
         * Sets the value of the supplierCatalogCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSupplierCatalogCode(String value) {
            this.supplierCatalogCode = value;
        }

    }

}

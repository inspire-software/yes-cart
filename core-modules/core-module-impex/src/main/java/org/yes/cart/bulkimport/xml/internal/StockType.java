
package org.yes.cart.bulkimport.xml.internal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
 * <p>Java class for stockType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="stockType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="availability" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="available-from" type="{}dateTimeType" minOccurs="0"/>
 *                   &lt;element name="available-to" type="{}dateTimeType" minOccurs="0"/>
 *                   &lt;element name="release-date" type="{}dateTimeType" minOccurs="0"/>
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
 *                 &lt;attribute name="featured" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="quantity" type="{}quantityType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="created-timestamp" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="created-by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated-timestamp" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="updated-by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sku" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="warehouse" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="import-mode" type="{}entityImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stockType", propOrder = {
    "availability",
    "inventoryConfig",
    "quantity",
    "createdTimestamp",
    "createdBy",
    "updatedTimestamp",
    "updatedBy"
})
public class StockType {

    protected StockType.Availability availability;
    @XmlElement(name = "inventory-config")
    protected StockType.InventoryConfig inventoryConfig;
    protected List<QuantityType> quantity;
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
    @XmlAttribute(name = "sku", required = true)
    protected String sku;
    @XmlAttribute(name = "warehouse", required = true)
    protected String warehouse;
    @XmlAttribute(name = "import-mode")
    protected EntityImportModeType importMode;

    /**
     * Gets the value of the availability property.
     * 
     * @return
     *     possible object is
     *     {@link StockType.Availability }
     *     
     */
    public StockType.Availability getAvailability() {
        return availability;
    }

    /**
     * Sets the value of the availability property.
     * 
     * @param value
     *     allowed object is
     *     {@link StockType.Availability }
     *     
     */
    public void setAvailability(StockType.Availability value) {
        this.availability = value;
    }

    /**
     * Gets the value of the inventoryConfig property.
     * 
     * @return
     *     possible object is
     *     {@link StockType.InventoryConfig }
     *     
     */
    public StockType.InventoryConfig getInventoryConfig() {
        return inventoryConfig;
    }

    /**
     * Sets the value of the inventoryConfig property.
     * 
     * @param value
     *     allowed object is
     *     {@link StockType.InventoryConfig }
     *     
     */
    public void setInventoryConfig(StockType.InventoryConfig value) {
        this.inventoryConfig = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the quantity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuantity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QuantityType }
     * 
     * 
     */
    public List<QuantityType> getQuantity() {
        if (quantity == null) {
            quantity = new ArrayList<QuantityType>();
        }
        return this.quantity;
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
     * Gets the value of the sku property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSku() {
        return sku;
    }

    /**
     * Sets the value of the sku property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSku(String value) {
        this.sku = value;
    }

    /**
     * Gets the value of the warehouse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWarehouse() {
        return warehouse;
    }

    /**
     * Sets the value of the warehouse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWarehouse(String value) {
        this.warehouse = value;
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
     *         &lt;element name="release-date" type="{}dateTimeType" minOccurs="0"/>
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
        "availableTo",
        "releaseDate"
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
        @XmlElement(name = "release-date")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String releaseDate;
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
         * Gets the value of the releaseDate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getReleaseDate() {
            return releaseDate;
        }

        /**
         * Sets the value of the releaseDate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setReleaseDate(String value) {
            this.releaseDate = value;
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
     *       &lt;attribute name="featured" type="{http://www.w3.org/2001/XMLSchema}boolean" />
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
        @XmlAttribute(name = "featured")
        protected Boolean featured;

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

    }

}

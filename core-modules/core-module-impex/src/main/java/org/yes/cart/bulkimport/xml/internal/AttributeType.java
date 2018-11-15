
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
 * <p>Java class for attributeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="attributeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mandatory" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="secure" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="allow-duplicate" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="allow-failover" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="store" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="search" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="primary" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="navigation" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="val" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regexp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="validation-failed-message" type="{}i18nsType" minOccurs="0"/>
 *         &lt;element name="choice-data" type="{}i18nsType" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="display-name" type="{}i18nsType" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="created-timestamp" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="created-by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated-timestamp" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="updated-by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="code" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="group" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="etype" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rank" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="import-mode" type="{}entityImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attributeType", propOrder = {
    "mandatory",
    "secure",
    "allowDuplicate",
    "allowFailover",
    "store",
    "search",
    "primary",
    "navigation",
    "val",
    "regexp",
    "validationFailedMessage",
    "choiceData",
    "name",
    "displayName",
    "description",
    "createdTimestamp",
    "createdBy",
    "updatedTimestamp",
    "updatedBy"
})
public class AttributeType {

    protected boolean mandatory;
    protected boolean secure;
    @XmlElement(name = "allow-duplicate")
    protected boolean allowDuplicate;
    @XmlElement(name = "allow-failover")
    protected boolean allowFailover;
    protected boolean store;
    protected boolean search;
    protected boolean primary;
    protected boolean navigation;
    protected String val;
    protected String regexp;
    @XmlElement(name = "validation-failed-message")
    protected I18NsType validationFailedMessage;
    @XmlElement(name = "choice-data")
    protected I18NsType choiceData;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(name = "display-name")
    protected I18NsType displayName;
    protected String description;
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
    @XmlAttribute(name = "group", required = true)
    protected String group;
    @XmlAttribute(name = "etype", required = true)
    protected String etype;
    @XmlAttribute(name = "rank")
    protected Integer rank;
    @XmlAttribute(name = "import-mode")
    protected EntityImportModeType importMode;

    /**
     * Gets the value of the mandatory property.
     * 
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Sets the value of the mandatory property.
     * 
     */
    public void setMandatory(boolean value) {
        this.mandatory = value;
    }

    /**
     * Gets the value of the secure property.
     * 
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Sets the value of the secure property.
     * 
     */
    public void setSecure(boolean value) {
        this.secure = value;
    }

    /**
     * Gets the value of the allowDuplicate property.
     * 
     */
    public boolean isAllowDuplicate() {
        return allowDuplicate;
    }

    /**
     * Sets the value of the allowDuplicate property.
     * 
     */
    public void setAllowDuplicate(boolean value) {
        this.allowDuplicate = value;
    }

    /**
     * Gets the value of the allowFailover property.
     * 
     */
    public boolean isAllowFailover() {
        return allowFailover;
    }

    /**
     * Sets the value of the allowFailover property.
     * 
     */
    public void setAllowFailover(boolean value) {
        this.allowFailover = value;
    }

    /**
     * Gets the value of the store property.
     * 
     */
    public boolean isStore() {
        return store;
    }

    /**
     * Sets the value of the store property.
     * 
     */
    public void setStore(boolean value) {
        this.store = value;
    }

    /**
     * Gets the value of the search property.
     * 
     */
    public boolean isSearch() {
        return search;
    }

    /**
     * Sets the value of the search property.
     * 
     */
    public void setSearch(boolean value) {
        this.search = value;
    }

    /**
     * Gets the value of the primary property.
     * 
     */
    public boolean isPrimary() {
        return primary;
    }

    /**
     * Sets the value of the primary property.
     * 
     */
    public void setPrimary(boolean value) {
        this.primary = value;
    }

    /**
     * Gets the value of the navigation property.
     * 
     */
    public boolean isNavigation() {
        return navigation;
    }

    /**
     * Sets the value of the navigation property.
     * 
     */
    public void setNavigation(boolean value) {
        this.navigation = value;
    }

    /**
     * Gets the value of the val property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVal() {
        return val;
    }

    /**
     * Sets the value of the val property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVal(String value) {
        this.val = value;
    }

    /**
     * Gets the value of the regexp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegexp() {
        return regexp;
    }

    /**
     * Sets the value of the regexp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegexp(String value) {
        this.regexp = value;
    }

    /**
     * Gets the value of the validationFailedMessage property.
     * 
     * @return
     *     possible object is
     *     {@link I18NsType }
     *     
     */
    public I18NsType getValidationFailedMessage() {
        return validationFailedMessage;
    }

    /**
     * Sets the value of the validationFailedMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link I18NsType }
     *     
     */
    public void setValidationFailedMessage(I18NsType value) {
        this.validationFailedMessage = value;
    }

    /**
     * Gets the value of the choiceData property.
     * 
     * @return
     *     possible object is
     *     {@link I18NsType }
     *     
     */
    public I18NsType getChoiceData() {
        return choiceData;
    }

    /**
     * Sets the value of the choiceData property.
     * 
     * @param value
     *     allowed object is
     *     {@link I18NsType }
     *     
     */
    public void setChoiceData(I18NsType value) {
        this.choiceData = value;
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
     * Gets the value of the group property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the value of the group property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroup(String value) {
        this.group = value;
    }

    /**
     * Gets the value of the etype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEtype() {
        return etype;
    }

    /**
     * Sets the value of the etype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEtype(String value) {
        this.etype = value;
    }

    /**
     * Gets the value of the rank property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRank() {
        return rank;
    }

    /**
     * Sets the value of the rank property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRank(Integer value) {
        this.rank = value;
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

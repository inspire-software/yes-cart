
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shop-urlType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shop-urlType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="primary" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="domain" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="theme-chain" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="import-mode" type="{}entityImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shop-urlType")
public class ShopUrlType {

    @XmlAttribute(name = "primary", required = true)
    protected boolean primary;
    @XmlAttribute(name = "domain", required = true)
    protected String domain;
    @XmlAttribute(name = "theme-chain")
    protected String themeChain;
    @XmlAttribute(name = "import-mode")
    protected EntityImportModeType importMode;

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
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the themeChain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThemeChain() {
        return themeChain;
    }

    /**
     * Sets the value of the themeChain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThemeChain(String value) {
        this.themeChain = value;
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

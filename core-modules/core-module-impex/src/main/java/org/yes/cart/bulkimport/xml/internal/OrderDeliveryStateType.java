
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for order-delivery-stateType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="order-delivery-stateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eligible-for-export" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="block-export" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="last-export-date" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="last-export-status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="last-export-delivery-status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "order-delivery-stateType", propOrder = {
    "status",
    "eligibleForExport",
    "blockExport",
    "lastExportDate",
    "lastExportStatus",
    "lastExportDeliveryStatus"
})
public class OrderDeliveryStateType {

    protected String status;
    @XmlElement(name = "eligible-for-export")
    protected String eligibleForExport;
    @XmlElement(name = "block-export")
    protected Boolean blockExport;
    @XmlElement(name = "last-export-date")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String lastExportDate;
    @XmlElement(name = "last-export-status")
    protected String lastExportStatus;
    @XmlElement(name = "last-export-delivery-status")
    protected String lastExportDeliveryStatus;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the eligibleForExport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEligibleForExport() {
        return eligibleForExport;
    }

    /**
     * Sets the value of the eligibleForExport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEligibleForExport(String value) {
        this.eligibleForExport = value;
    }

    /**
     * Gets the value of the blockExport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBlockExport() {
        return blockExport;
    }

    /**
     * Sets the value of the blockExport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBlockExport(Boolean value) {
        this.blockExport = value;
    }

    /**
     * Gets the value of the lastExportDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastExportDate() {
        return lastExportDate;
    }

    /**
     * Sets the value of the lastExportDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastExportDate(String value) {
        this.lastExportDate = value;
    }

    /**
     * Gets the value of the lastExportStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastExportStatus() {
        return lastExportStatus;
    }

    /**
     * Sets the value of the lastExportStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastExportStatus(String value) {
        this.lastExportStatus = value;
    }

    /**
     * Gets the value of the lastExportDeliveryStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastExportDeliveryStatus() {
        return lastExportDeliveryStatus;
    }

    /**
     * Sets the value of the lastExportDeliveryStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastExportDeliveryStatus(String value) {
        this.lastExportDeliveryStatus = value;
    }

}

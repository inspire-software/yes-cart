
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for shipping-providersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shipping-providersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shipping-provider" type="{}shipping-providerType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shipping-providersType", propOrder = {
    "shippingProvider"
})
public class ShippingProvidersType {

    @XmlElement(name = "shipping-provider")
    protected List<ShippingProviderType> shippingProvider;

    /**
     * Gets the value of the shippingProvider property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shippingProvider property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShippingProvider().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShippingProviderType }
     * 
     * 
     */
    public List<ShippingProviderType> getShippingProvider() {
        if (shippingProvider == null) {
            shippingProvider = new ArrayList<ShippingProviderType>();
        }
        return this.shippingProvider;
    }

}

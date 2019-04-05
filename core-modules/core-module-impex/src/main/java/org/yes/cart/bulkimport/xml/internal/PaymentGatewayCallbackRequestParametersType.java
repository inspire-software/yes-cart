
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for payment-gateway-callback-request-parametersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="payment-gateway-callback-request-parametersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="request-parameter" type="{}payment-gateway-callback-request-parameterType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "payment-gateway-callback-request-parametersType", propOrder = {
    "requestParameter"
})
public class PaymentGatewayCallbackRequestParametersType {

    @XmlElement(name = "request-parameter")
    protected List<PaymentGatewayCallbackRequestParameterType> requestParameter;

    /**
     * Gets the value of the requestParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requestParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequestParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentGatewayCallbackRequestParameterType }
     * 
     * 
     */
    public List<PaymentGatewayCallbackRequestParameterType> getRequestParameter() {
        if (requestParameter == null) {
            requestParameter = new ArrayList<PaymentGatewayCallbackRequestParameterType>();
        }
        return this.requestParameter;
    }

}

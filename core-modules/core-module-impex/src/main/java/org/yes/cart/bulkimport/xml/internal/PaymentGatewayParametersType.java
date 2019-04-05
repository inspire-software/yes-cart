
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for payment-gateway-parametersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="payment-gateway-parametersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="payment-gateway-parameter" type="{}payment-gateway-parameterType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "payment-gateway-parametersType", propOrder = {
    "paymentGatewayParameter"
})
public class PaymentGatewayParametersType {

    @XmlElement(name = "payment-gateway-parameter")
    protected List<PaymentGatewayParameterType> paymentGatewayParameter;

    /**
     * Gets the value of the paymentGatewayParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paymentGatewayParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentGatewayParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentGatewayParameterType }
     * 
     * 
     */
    public List<PaymentGatewayParameterType> getPaymentGatewayParameter() {
        if (paymentGatewayParameter == null) {
            paymentGatewayParameter = new ArrayList<PaymentGatewayParameterType>();
        }
        return this.paymentGatewayParameter;
    }

}

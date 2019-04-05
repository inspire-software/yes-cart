
package org.yes.cart.bulkimport.xml.internal;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for payment-gateway-callbacksType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="payment-gateway-callbacksType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="payment-gateway-callback" type="{}payment-gateway-callbackType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "payment-gateway-callbacksType", propOrder = {
    "paymentGatewayCallback"
})
public class PaymentGatewayCallbacksType {

    @XmlElement(name = "payment-gateway-callback")
    protected List<PaymentGatewayCallbackType> paymentGatewayCallback;

    /**
     * Gets the value of the paymentGatewayCallback property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paymentGatewayCallback property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentGatewayCallback().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentGatewayCallbackType }
     * 
     * 
     */
    public List<PaymentGatewayCallbackType> getPaymentGatewayCallback() {
        if (paymentGatewayCallback == null) {
            paymentGatewayCallback = new ArrayList<PaymentGatewayCallbackType>();
        }
        return this.paymentGatewayCallback;
    }

}

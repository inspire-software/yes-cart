package org.yes.cart.payment.service.impl;

import org.junit.Test;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayParameterEntity;
import org.yes.cart.payment.service.PaymentGatewayParameterService;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:45:45
 */
public class PaymentGatewayParametersServiceTest extends BasePaymentModuleDBTestCase {

    @Test
    public void testCrud() {
        PaymentGatewayParameterService service = (PaymentGatewayParameterService) ctx().getBean("paymentGatewayParameterService");
        PaymentGatewayParameter parameter = new PaymentGatewayParameterEntity();
        parameter.setDescription("desc");
        parameter.setPgLabel("pgLabel");
        parameter.setLabel("label");
        parameter.setName("name");
        parameter.setValue("red");
        parameter = service.create(parameter);
        assertTrue(parameter.getPaymentGatewayParameterId() > 0);
        long pkValue = parameter.getPaymentGatewayParameterId();
        parameter.setValue("black");
        service.update(parameter);
        parameter = service.getById(pkValue);
        assertEquals("black", parameter.getValue());
        service.delete(parameter);
        parameter = service.getById(pkValue);
        assertNull(parameter);
    }

    @Test
    public void testDeleteByLabel() {
        PaymentGatewayParameterService service = (PaymentGatewayParameterService) ctx().getBean("paymentGatewayParameterService");
        PaymentGatewayParameter parameter = new PaymentGatewayParameterEntity();
        parameter.setDescription("desc");
        parameter.setPgLabel("pgLabel");
        parameter.setLabel("label");
        parameter.setName("name");
        parameter.setValue("red");
        parameter = service.create(parameter);
        assertTrue(parameter.getPaymentGatewayParameterId() > 0);
        long pkValue = parameter.getPaymentGatewayParameterId();
        service.deleteByLabel("pgLabel", "label");
        parameter = service.getById(pkValue);
        assertNull(parameter);
    }
}

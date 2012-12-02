/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

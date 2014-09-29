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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:45:45
 */
public class PaymentGatewayParametersServiceTest extends BasePaymentModuleDBTestCase {


    private PaymentGatewayParameter createParameter(final String pg, final String label, final String value) {
        PaymentGatewayParameter parameter = new PaymentGatewayParameterEntity();
        parameter.setDescription("desc");
        parameter.setPgLabel(pg);
        parameter.setLabel(label);
        parameter.setName("name");
        parameter.setValue(value);
        return parameter;
    }

    @Test
    public void testCrud() {
        PaymentGatewayParameterService service = (PaymentGatewayParameterService) ctx().getBean("paymentGatewayParameterService");
        PaymentGatewayParameter parameter = createParameter("pgLabel", "label", "red");
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
        PaymentGatewayParameter parameter = createParameter("pgLabel", "label", "red");
        parameter = service.create(parameter);
        assertTrue(parameter.getPaymentGatewayParameterId() > 0);
        long pkValue = parameter.getPaymentGatewayParameterId();
        service.deleteByLabel("pgLabel", "label");
        parameter = service.getById(pkValue);
        assertNull(parameter);
    }

    @Test
    public void testFindAllByLabel() {

        PaymentGatewayParameterService service = (PaymentGatewayParameterService) ctx().getBean("paymentGatewayParameterService");

        service.create(createParameter("pgLabel", "label1", "red1"));
        service.create(createParameter("pgLabel", "label2", "red2"));
        service.create(createParameter("pgLabel", "#SHOP10_label1", "shop10red1"));
        service.create(createParameter("pgLabel", "#SHOP10_label2", "shop10red2"));
        service.create(createParameter("pgLabel", "#SHOP20_label1", "shop20red1"));
        service.create(createParameter("pgLabel", "#SHOP20_label2", "shop20red2"));

        final Collection<PaymentGatewayParameter> defParamsNull = service.findAll("pgLabel", null);
        final Set<String> defParamsNullLabels = new HashSet<String>();
        for (final PaymentGatewayParameter param : defParamsNull) {
            defParamsNullLabels.add(param.getLabel());
        }
        assertEquals(2, defParamsNullLabels.size());
        assertTrue(defParamsNullLabels.contains("label1"));
        assertTrue(defParamsNullLabels.contains("label2"));

        final Collection<PaymentGatewayParameter> defParamsDEFAULT = service.findAll("pgLabel", "DEFAULT");
        final Set<String> defParamsDEFAULTLabels = new HashSet<String>();
        for (final PaymentGatewayParameter param : defParamsDEFAULT) {
            defParamsDEFAULTLabels.add(param.getLabel());
        }
        assertEquals(2, defParamsDEFAULTLabels.size());
        assertTrue(defParamsDEFAULTLabels.contains("label1"));
        assertTrue(defParamsDEFAULTLabels.contains("label2"));

        final Collection<PaymentGatewayParameter> defParamsSHOP10 = service.findAll("pgLabel", "SHOP10");
        final Set<String> defParamsSHOP10Labels = new HashSet<String>();
        for (final PaymentGatewayParameter param : defParamsSHOP10) {
            defParamsSHOP10Labels.add(param.getLabel());
        }
        assertEquals(4, defParamsSHOP10Labels.size());
        assertTrue(defParamsSHOP10Labels.contains("label1"));
        assertTrue(defParamsSHOP10Labels.contains("label2"));
        assertTrue(defParamsSHOP10Labels.contains("#SHOP10_label1"));
        assertTrue(defParamsSHOP10Labels.contains("#SHOP10_label2"));

        final Collection<PaymentGatewayParameter> defParamsSHOP20 = service.findAll("pgLabel", "SHOP20");
        final Set<String> defParamsSHOP20Labels = new HashSet<String>();
        for (final PaymentGatewayParameter param : defParamsSHOP20) {
            defParamsSHOP20Labels.add(param.getLabel());
        }
        assertEquals(4, defParamsSHOP20Labels.size());
        assertTrue(defParamsSHOP20Labels.contains("label1"));
        assertTrue(defParamsSHOP20Labels.contains("label2"));
        assertTrue(defParamsSHOP20Labels.contains("#SHOP20_label1"));
        assertTrue(defParamsSHOP20Labels.contains("#SHOP20_label2"));

    }
}

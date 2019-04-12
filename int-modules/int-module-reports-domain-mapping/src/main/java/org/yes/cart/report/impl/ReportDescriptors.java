/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.report.impl;

import org.yes.cart.report.ReportDescriptor;
import org.yes.cart.report.ReportParameter;

import java.util.Collections;

/**
 * User: denispavlov
 * Date: 12/04/2019
 * Time: 20:33
 */
public class ReportDescriptors {

    public static ReportDescriptor reportDelivery() {
        final ReportDescriptor receipt = new ReportDescriptor();
        receipt.setReportId("reportDelivery");
        receipt.setXslfoBase("client/order/delivery");
        final ReportParameter param1 = new ReportParameter();
        param1.setParameterId("orderNumber");
        param1.setBusinesstype("String");
        param1.setMandatory(true);
        receipt.setParameters(Collections.singletonList(param1));
        return receipt;
    }


}

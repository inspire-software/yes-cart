/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo.impl;

import org.hibernate.criterion.Restrictions;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.domain.vo.VoManagerRole;
import org.yes.cart.payment.persistence.entity.PaymentGatewayCallback;
import org.yes.cart.payment.service.PaymentModuleGenericService;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;
import org.yes.cart.service.vo.VoDashboardWidgetService;

import java.util.*;

/**
 * User: denispavlov
 * Date: 13/02/2017
 * Time: 07:19
 */
public class VoDashboardWidgetPluginUnprocessedPgCallbacks implements VoDashboardWidgetPlugin {

    private final List<String> roles = Arrays.asList("ROLE_SMADMIN", "ROLE_SMSHOPADMIN");

    private PaymentModuleGenericService<PaymentGatewayCallback> paymentModuleGenericService;

    public VoDashboardWidgetPluginUnprocessedPgCallbacks(final PaymentModuleGenericService<PaymentGatewayCallback> paymentModuleGenericService) {
        this.paymentModuleGenericService = paymentModuleGenericService;
    }

    @Override
    public boolean applicable(final VoManager manager) {
        if (manager.getManagerShops().size() > 0) {
            for (final VoManagerRole role : manager.getManagerRoles()) {
                if (roles.contains(role.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public VoDashboardWidget getWidget(final VoManager manager) {

        final VoDashboardWidget widget = new VoDashboardWidget();
        widget.setWidgetId("UnprocessedPgCallbacks");

        final Map<String, Integer> counts = new HashMap<>();
        final List<PaymentGatewayCallback> callbacks = this.paymentModuleGenericService.findByCriteria(Restrictions.eq("processed", Boolean.FALSE));
        for (final PaymentGatewayCallback callback : callbacks) {

            final Integer count = counts.get(callback.getLabel());
            if (count == null) {
                counts.put(callback.getLabel(), 1);
            } else {
                counts.put(callback.getLabel(), count + 1);
            }

        }

        final List<MutablePair<String, Integer>> data = new ArrayList<>();
        if (counts.isEmpty()) {
            data.add(new MutablePair<String, Integer>("-", 0));
        } else {
            for (final Map.Entry<String, Integer> entry : counts.entrySet()) {
                data.add(new MutablePair<String, Integer>(entry.getKey(), entry.getValue()));
            }
        }

        widget.setData(data);

        return widget;
    }

    /**
     * Spring IoC.
     *
     * @param dashboardWidgetService dashboard service
     */
    public void setDashboardWidgetService(VoDashboardWidgetService dashboardWidgetService) {
        dashboardWidgetService.registerWidgetPlugin(this);
    }

    public void setProductService(final PaymentModuleGenericService<PaymentGatewayCallback> paymentModuleGenericService) {
        this.paymentModuleGenericService = paymentModuleGenericService;
    }

}

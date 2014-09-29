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

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;
import org.yes.cart.payment.service.PaymentGatewayParameterService;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public class PaymentGatewayParameterServiceImpl
        extends PaymentModuleGenericServiceImpl<PaymentGatewayParameter>
        implements PaymentGatewayParameterService {

    /**
     * Construct service to work with pg parameters.
     *
     * @param genericDao dao to use.
     */
    public PaymentGatewayParameterServiceImpl(final PaymentModuleGenericDAO<PaymentGatewayParameter, Long> genericDao) {
        super(genericDao);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteByLabel(final String paymentGatewayLabel, final String parameterLabel) {
        PaymentGatewayParameter toDelete = getGenericDao().findSingleByCriteria(
                Restrictions.eq("pgLabel", paymentGatewayLabel),
                Restrictions.eq("label", parameterLabel)

        );
        if (toDelete != null) {
            delete(toDelete);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<PaymentGatewayParameter> findAll(final String label, final String shopCode) {
        if (shopCode != null && !"DEFAULT".equals(shopCode)) {
            return getGenericDao().findByCriteria(
                    Restrictions.and(
                            Restrictions.eq("pgLabel", label),
                            Restrictions.or(
                                    Restrictions.not(Restrictions.like("label", "#", MatchMode.START)),
                                    Restrictions.like("label", "#" + shopCode + "_", MatchMode.START)
                            )
                        )
            );
        }
        return getGenericDao().findByCriteria(
                Restrictions.and(
                        Restrictions.eq("pgLabel", label),
                        Restrictions.not(Restrictions.like("label", "#", MatchMode.START))
                )
        );
    }
}

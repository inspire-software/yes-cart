/*
 * Copyright 2009 Inspire-Software.com
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
package org.yes.cart.cluster.service.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.cluster.service.QueryDirectorPlugin;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;
import org.yes.cart.payment.service.PaymentModuleGenericService;
import org.yes.cart.utils.impl.ObjectUtil;

import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 16:31
 */
public class QueryDirectorPluginPayHQLImpl implements QueryDirectorPlugin {

    private static final List<String> SUPPORTS = Collections.singletonList("hql-pay");

    private PaymentModuleGenericService paymentModuleService;

    /** {@inheritDoc} */
    @Override
    public List<String> supports() {
        return SUPPORTS;
    }

    /** {@inheritDoc} */
    @Override
    public List<Object[]> runQuery(final String query) {

        if (StringUtils.isNotBlank(query)) {

            if (query.toLowerCase().contains("select ")) {

                final List queryRez = getGenericDao().findByQuery(query);
                return ObjectUtil.transformTypedResultListToArrayList(queryRez);

            } else {
                throw new UnsupportedOperationException("Updates on Payment DB are not supported");
            }
        }
        return Collections.emptyList();

    }

    @SuppressWarnings("unchecked")
    private PaymentModuleGenericDAO<Object, Long> getGenericDao() {
        return (PaymentModuleGenericDAO) paymentModuleService.getGenericDao();
    }


    /**
     * IoC. Set payment service.
     *
     * @param paymentModuleService payment service to use.
     */
    public void setPaymentModuleGenericService(final PaymentModuleGenericService paymentModuleService) {
        this.paymentModuleService = paymentModuleService;
    }


}

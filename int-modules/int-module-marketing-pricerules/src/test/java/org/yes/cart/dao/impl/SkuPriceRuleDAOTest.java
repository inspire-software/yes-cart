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

package org.yes.cart.dao.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.SkuPriceRule;
import org.yes.cart.domain.entity.impl.SkuPriceRuleEntity;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: denispavlov
 * Date: 01/04/2018
 * Time: 21:55
 */
public class SkuPriceRuleDAOTest extends AbstractTestDAO {

    private GenericDAO<SkuPriceRule, Long> priceruleDAO;

    @Override
    @Before
    public void setUp() {
        priceruleDAO = (GenericDAO<SkuPriceRule, Long>) ctx().getBean("skuPriceRuleDao");
        super.setUp();

    }


    @Test
    public void testAddEmptyRule() {
        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {

                // create simple rule
                SkuPriceRule entity = new SkuPriceRuleEntity();
                entity.setName("simple rule");
                entity.setCode("SIMPLE-001");
                entity.setShopCode("SHOP10");
                entity.setCurrency("USD");
                entity.setEligibilityCondition("true");
                entity.setRuleAction(SkuPriceRule.ACTION_SKIP);
                long pk = priceruleDAO.create(entity).getSkuPriceRuleId();
                assertTrue(pk != 0);
                SkuPriceRule rule = priceruleDAO.findById(pk);
                assertNotNull(rule);

                status.setRollbackOnly();

            }
        });

    }
}

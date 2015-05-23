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

package org.yes.cart.bulkjob.promotion;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.PromotionService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: denispavlov
 * Date: 07/11/2013
 * Time: 10:11
 */
public class BulkCustomerTagProcessorImplTest extends BaseCoreDBTestCase {
    @Test
    public void testRun() throws Exception {

        final CustomerService customerService = ctx().getBean("customerService", CustomerService.class);
        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final Runnable bulkCustomerTagProcessor = ctx().getBean("bulkCustomerTagProcessor", Runnable.class);

        final Customer customer1 = createCustomer(" cust1 ");
        final Customer customer2 = createCustomer(" cust2 ");
        final Customer customer3 = createCustomer(" cust3 ");

        // create three promotions to see cumulative effect
        final Promotion cust1Tag = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        cust1Tag.setCode("TAG1");
        cust1Tag.setRank(10);
        cust1Tag.setShopCode("SHOIP1");
        cust1Tag.setCurrency("EUR");
        cust1Tag.setName("tag all cust1");
        cust1Tag.setPromoType(Promotion.TYPE_CUSTOMER_TAG);
        cust1Tag.setPromoAction(Promotion.ACTION_TAG);
        cust1Tag.setEligibilityCondition("customer.firstname.indexOf(' cust1 ') != -1");
        cust1Tag.setPromoActionContext("tag1");
        cust1Tag.setCanBeCombined(true);
        cust1Tag.setEnabled(true);

        promotionService.create(cust1Tag);

        final Promotion cust3Tag = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        cust3Tag.setCode("TAG3");
        cust3Tag.setRank(10);
        cust3Tag.setShopCode("SHOIP1");
        cust3Tag.setCurrency("EUR");
        cust3Tag.setName("tag all cust3");
        cust3Tag.setPromoType(Promotion.TYPE_CUSTOMER_TAG);
        cust3Tag.setPromoAction(Promotion.ACTION_TAG);
        cust3Tag.setEligibilityCondition("customer.firstname.indexOf(' cust3 ') != -1");
        cust3Tag.setPromoActionContext("tag3");
        cust3Tag.setCanBeCombined(true);
        cust3Tag.setEnabled(true);

        promotionService.create(cust3Tag);

        final Promotion cust1or3Tag = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        cust1or3Tag.setCode("TAG1OR3");
        cust1or3Tag.setRank(10);
        cust1or3Tag.setShopCode("SHOIP1");
        cust1or3Tag.setCurrency("EUR");
        cust1or3Tag.setName("tag all cust1 and cust3");
        cust1or3Tag.setPromoType(Promotion.TYPE_CUSTOMER_TAG);
        cust1or3Tag.setPromoAction(Promotion.ACTION_TAG);
        cust1or3Tag.setEligibilityCondition("customer.firstname.indexOf(' cust1 ') != -1 || customer.firstname.indexOf(' cust3 ') != -1");
        cust1or3Tag.setPromoActionContext("tag1or3");
        cust1or3Tag.setCanBeCombined(true);
        cust1or3Tag.setEnabled(true);

        promotionService.create(cust1or3Tag);

        final Promotion cust3FirstTimeTag = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        cust3FirstTimeTag.setCode("TAG3FIRST");
        cust3FirstTimeTag.setRank(10);
        cust3FirstTimeTag.setShopCode("SHOIP1");
        cust3FirstTimeTag.setCurrency("EUR");
        cust3FirstTimeTag.setName("tag all cust3 with no orders"); // test lazy walking
        cust3FirstTimeTag.setPromoType(Promotion.TYPE_CUSTOMER_TAG);
        cust3FirstTimeTag.setPromoAction(Promotion.ACTION_TAG);
        cust3FirstTimeTag.setEligibilityCondition("customer.firstname.indexOf(' cust3 ') != -1 && customer.orders.isEmpty()");
        cust3FirstTimeTag.setPromoActionContext("first3");
        cust3FirstTimeTag.setCanBeCombined(true);
        cust3FirstTimeTag.setEnabled(true);

        promotionService.create(cust3FirstTimeTag);

        // run the job
        bulkCustomerTagProcessor.run();

        final List<Customer> customer1tagged = customerService.findCustomer(customer1.getEmail(), null, null, null, null);

        assertEquals("tag1 tag1or3", customer1tagged.get(0).getTag());

        final List<Customer> customer2tagged = customerService.findCustomer(customer2.getEmail(), null, null, null, null);

        assertNull(customer2tagged.get(0).getTag());

        final List<Customer> customer3tagged = customerService.findCustomer(customer3.getEmail(), null, null, null, null);

        assertEquals("first3 tag1or3 tag3", customer3tagged.get(0).getTag());


    }
}

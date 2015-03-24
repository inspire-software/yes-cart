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

package org.yes.cart.promotion.impl.action;

import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.promotion.PromotionContext;
import org.yes.cart.promotion.PromotionContextFactory;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.PromotionService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 06/11/2013
 * Time: 18:17
 */
public class CustomerTagPromotionActionTest extends BaseCoreDBTestCase {


    @Test
    public void testSingleTagPromotion() throws Exception {


        final CustomerService customerService = ctx().getBean("customerService", CustomerService.class);
        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final PromotionContextFactory promotionContextFactory = ctx().getBean("promotionContextFactory", PromotionContextFactory.class);


        // create promotion
        final Promotion firstTimeBuyer = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        firstTimeBuyer.setCode("FTB_TAG");
        firstTimeBuyer.setShopCode("SHOIP1");
        firstTimeBuyer.setCurrency("EUR");
        firstTimeBuyer.setName("tag first time buyers");
        firstTimeBuyer.setPromoType(Promotion.TYPE_CUSTOMER_TAG);
        firstTimeBuyer.setPromoAction(Promotion.ACTION_TAG);
        firstTimeBuyer.setEligibilityCondition("customer.orders.isEmpty()");
        firstTimeBuyer.setPromoActionContext("first");
        firstTimeBuyer.setEnabled(true);

        promotionService.create(firstTimeBuyer);


        final Customer newCustomer = createCustomer();
        final PromotionContext promotionContext = promotionContextFactory.getInstance("SHOIP1", "EUR");

        // Promotion context is not transactional and therefore we need surrounding
        // transaction and fresh from db customer object to be able deeply navigate the object graph
        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                final List<Customer> fromDb = customerService.findCustomer(newCustomer.getEmail(), null, null, null, null);
                assertFalse(fromDb.isEmpty());
                promotionContext.applyCustomerPromo(fromDb.get(0), null);
                assertEquals("first", fromDb.get(0).getTag());
                status.setRollbackOnly();

            }
        });

        // clean test
        promotionService.delete(firstTimeBuyer);


    }

    @Test
    public void testSingleTagPromotionWithNonEligible() throws Exception {


        final CustomerService customerService = ctx().getBean("customerService", CustomerService.class);
        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final PromotionContextFactory promotionContextFactory = ctx().getBean("promotionContextFactory", PromotionContextFactory.class);


        // create promotion
        final Promotion activeShopper = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        activeShopper.setCode("AS_TAG");
        activeShopper.setShopCode("SHOIP1");
        activeShopper.setCurrency("EUR");
        activeShopper.setName("tag active shoppers");
        activeShopper.setPromoType(Promotion.TYPE_CUSTOMER_TAG);
        activeShopper.setPromoAction(Promotion.ACTION_TAG);
        activeShopper.setEligibilityCondition("!customer.orders.isEmpty()");
        activeShopper.setPromoActionContext("active");
        activeShopper.setEnabled(true);

        promotionService.create(activeShopper);


        final Customer newCustomer = createCustomer();
        final PromotionContext promotionContext = promotionContextFactory.getInstance("SHOIP1", "EUR");

        // Promotion context is not transactional and therefore we need surrounding
        // transaction and fresh from db customer object to be able deeply navigate the object graph
        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                final List<Customer> fromDb = customerService.findCustomer(newCustomer.getEmail(), null, null, null, null);
                assertFalse(fromDb.isEmpty());
                promotionContext.applyCustomerPromo(fromDb.get(0), null);
                assertNull(fromDb.get(0).getTag());
                status.setRollbackOnly();

            }
        });

        // clean test
        promotionService.delete(activeShopper);


    }

    @Test
    public void testMultiTagPromotion() throws Exception {


        final CustomerService customerService = ctx().getBean("customerService", CustomerService.class);
        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final PromotionContextFactory promotionContextFactory = ctx().getBean("promotionContextFactory", PromotionContextFactory.class);


        // create promotion
        final Promotion nameJohn = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        nameJohn.setCode("JOHN_TAG");
        nameJohn.setShopCode("SHOIP1");
        nameJohn.setCurrency("EUR");
        nameJohn.setName("tag all John's");
        nameJohn.setPromoType(Promotion.TYPE_CUSTOMER_TAG);
        nameJohn.setPromoAction(Promotion.ACTION_TAG);
        nameJohn.setEligibilityCondition("customer.firstname.indexOf('John') != -1");
        nameJohn.setPromoActionContext("john");
        nameJohn.setCanBeCombined(true);
        nameJohn.setEnabled(true);

        promotionService.create(nameJohn);

        final Promotion firstTimeBuyer = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        firstTimeBuyer.setCode("FTB_TAG");
        firstTimeBuyer.setShopCode("SHOIP1");
        firstTimeBuyer.setCurrency("EUR");
        firstTimeBuyer.setName("tag first time buyers");
        firstTimeBuyer.setPromoType(Promotion.TYPE_CUSTOMER_TAG);
        firstTimeBuyer.setPromoAction(Promotion.ACTION_TAG);
        firstTimeBuyer.setEligibilityCondition("customer.orders.isEmpty()");
        firstTimeBuyer.setPromoActionContext("first");
        firstTimeBuyer.setCanBeCombined(true);
        firstTimeBuyer.setEnabled(true);

        promotionService.create(firstTimeBuyer);


        final Customer newCustomer = createCustomer();
        final PromotionContext promotionContext = promotionContextFactory.getInstance("SHOIP1", "EUR");

        // Promotion context is not transactional and therefore we need surrounding
        // transaction and fresh from db customer object to be able deeply navigate the object graph
        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                final List<Customer> fromDb = customerService.findCustomer(newCustomer.getEmail(), null, null, null, null);
                assertFalse(fromDb.isEmpty());
                promotionContext.applyCustomerPromo(fromDb.get(0), null);
                assertEquals("first john", fromDb.get(0).getTag());
                status.setRollbackOnly();

            }
        });

        // clean test
        promotionService.delete(nameJohn);
        promotionService.delete(firstTimeBuyer);


    }

    @Test
    public void testBestDealPromotion() throws Exception {



        final CustomerService customerService = ctx().getBean("customerService", CustomerService.class);
        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final PromotionContextFactory promotionContextFactory = ctx().getBean("promotionContextFactory", PromotionContextFactory.class);


        // create promotion
        final Promotion nameJohn = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        nameJohn.setCode("JOHN_TAG");
        nameJohn.setRank(10);  // 10th ranking will give discount max - 10
        nameJohn.setShopCode("SHOIP1");
        nameJohn.setCurrency("EUR");
        nameJohn.setName("tag all John's");
        nameJohn.setPromoType(Promotion.TYPE_CUSTOMER_TAG);
        nameJohn.setPromoAction(Promotion.ACTION_TAG);
        nameJohn.setEligibilityCondition("customer.firstname.indexOf('John') != -1");
        nameJohn.setPromoActionContext("john");
        nameJohn.setCanBeCombined(false);
        nameJohn.setEnabled(true);

        promotionService.create(nameJohn);

        final Promotion firstTimeBuyer = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        firstTimeBuyer.setCode("FTB_TAG");
        firstTimeBuyer.setRank(1); // 1st ranking will give discount max - 1
        firstTimeBuyer.setShopCode("SHOIP1");
        firstTimeBuyer.setCurrency("EUR");
        firstTimeBuyer.setName("tag first time buyers");
        firstTimeBuyer.setPromoType(Promotion.TYPE_CUSTOMER_TAG);
        firstTimeBuyer.setPromoAction(Promotion.ACTION_TAG);
        firstTimeBuyer.setEligibilityCondition("customer.orders.isEmpty()");
        firstTimeBuyer.setPromoActionContext("first");
        firstTimeBuyer.setCanBeCombined(false);
        firstTimeBuyer.setEnabled(true);

        promotionService.create(firstTimeBuyer);


        final Customer newCustomer = createCustomer();
        final PromotionContext promotionContext = promotionContextFactory.getInstance("SHOIP1", "EUR");

        // Promotion context is not transactional and therefore we need surrounding
        // transaction and fresh from db customer object to be able deeply navigate the object graph
        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                final List<Customer> fromDb = customerService.findCustomer(newCustomer.getEmail(), null, null, null, null);
                assertFalse(fromDb.isEmpty());
                promotionContext.applyCustomerPromo(fromDb.get(0), null);
                assertEquals("first", fromDb.get(0).getTag());
                status.setRollbackOnly();

            }
        });

        // clean test
        promotionService.delete(nameJohn);
        promotionService.delete(firstTimeBuyer);



    }


}

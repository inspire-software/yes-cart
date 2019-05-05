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
import org.yes.cart.dao.constants.DaoServiceBeanKeys;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.domain.entity.Shop;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 27/04/2019
 * Time: 13:28
 */
public class ContentDAOTest extends AbstractTestDAO {

    private GenericDAO<Content, Long> contentDao;
    private GenericDAO<Shop, Long> shopDao;

    @Override
    @Before
    public void setUp()  {
        shopDao = (GenericDAO<Shop, Long>) ctx().getBean(DaoServiceBeanKeys.SHOP_DAO);
        contentDao = (GenericDAO<Content, Long>) ctx().getBean(DaoServiceBeanKeys.CONTENT_DAO);
        super.setUp();
    }


    @Test
    public void resolveContentByShop() {

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {

                Shop shop = shopDao.findSingleByNamedQuery("SHOP.BY.URL", "gadget.yescart.org");
                assertNotNull("Shop must be resolved by URL", shop);
                List<Content> assignedCmsRoot =
                        (List) contentDao.findQueryObjectByNamedQuery("CMS3.ROOTCONTENT.BY.SHOP.ID", shop.getShopId());
                assertNotNull("Test shop must have assigned categories", assignedCmsRoot);
                assertFalse("Assigned CMS not empty", assignedCmsRoot.isEmpty());

                final List<String> mailTemplate =
                        (List) contentDao.findQueryObjectByNamedQuery("CMS3.CONTENTBODY.BY.CONTENTID",
                                10113L, "CONTENT_BODY_en_%", LocalDateTime.now(), Boolean.FALSE);
                assertEquals("$firstName registered", mailTemplate.get(0));

                status.setRollbackOnly();

            }
        });


    }
}

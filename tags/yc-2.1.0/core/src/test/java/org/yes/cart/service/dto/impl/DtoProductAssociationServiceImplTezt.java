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

package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ProductAssociationDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.service.dto.DtoAssociationService;
import org.yes.cart.service.dto.DtoProductAssociationService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductAssociationServiceImplTezt extends BaseCoreDBTestCase {

    private DtoFactory dtoFactory;
    private DtoProductAssociationService dtoProductAssociationService;
    private DtoAssociationService dtoAssociationService;

    @Before
    public void setUp() {
        dtoProductAssociationService = (DtoProductAssociationService) ctx().getBean(ServiceSpringKeys.DTO_PRODUCT_ASSOCIATION_SERVICE);
        dtoAssociationService = (DtoAssociationService) ctx().getBean(ServiceSpringKeys.DTO_ASSOCIATION_SERVICE);
        dtoFactory = (DtoFactory) ctx().getBean(ServiceSpringKeys.DTO_FACTORY);
        super.setUp();
    }

    @Test
    public void testUpdate() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {

                try {
                    ProductAssociationDTO dto = getDto();
                    dto = dtoProductAssociationService.create(dto);
                    assertTrue(dto.getProductassociationId() > 0);
                    assertEquals(0, dto.getRank());
                    dto.setRank(11324);
                    dto = dtoProductAssociationService.update(dto);
                    assertEquals(11324, dto.getRank());
                    long pk = dto.getProductassociationId();
                    dtoProductAssociationService.remove(pk);
                    dto = dtoProductAssociationService.getById(pk);
                    assertNull(dto);
                }   catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();



            }
        });


    }

    @Test
    public void testGetProductAssociations() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try{
                    ProductAssociationDTO dto = getDto();
                    dto = dtoProductAssociationService.create(dto);
                    assertTrue(dto.getProductassociationId() > 0);
                    List<ProductAssociationDTO> list = dtoProductAssociationService.getProductAssociations(11002L);
                    assertEquals(1, list.size());
                    list = dtoProductAssociationService.getProductAssociations(11004L);
                    assertEquals(5, list.size());
                    dtoProductAssociationService.remove(dto.getProductassociationId());
                }   catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();
            }
        });


    }


    @Test
    public void testInvalidAssociationCreate() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    ProductAssociationDTO dto = getDto();
                    dto.setProductId(11002L);
                    dto.setAssociatedProductId(11002L);
                    dtoProductAssociationService.create(dto);
                }   catch (UnableToCreateInstanceException e) {
                    assertTrue(e.getMessage(), true);
                }   catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();
            }
        });


    }


    @Test
    public void testGetProductAssociationsByProductAssociationType() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    ProductAssociationDTO dto = getDto();
                    dto = dtoProductAssociationService.create(dto);
                    assertTrue(dto.getProductassociationId() > 0);
                    long pk = dto.getProductassociationId();
                    List<ProductAssociationDTO> list = dtoProductAssociationService.getProductAssociationsByProductAssociationType(11002L, "accessories");
                    assertEquals(0, list.size());
                    list = dtoProductAssociationService.getProductAssociationsByProductAssociationType(11002L, "up");
                    assertEquals(0, list.size());
                    list = dtoProductAssociationService.getProductAssociationsByProductAssociationType(11002L, "cross");
                    assertEquals(1, list.size());
                    list = dtoProductAssociationService.getProductAssociationsByProductAssociationType(11002L, "buywiththis");
                    assertEquals(0, list.size());
                }   catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();

            }
        });


    }

    private ProductAssociationDTO getDto() throws Exception {
        ProductAssociationDTO dto = dtoFactory.getByIface(ProductAssociationDTO.class);
        dto.setAssociationId(dtoAssociationService.getById(3L).getAssociationId());
        dto.setProductId(11002L);
        dto.setAssociatedProductId(11003L);
        return dto;
    }
}

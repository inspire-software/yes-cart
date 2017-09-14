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

package org.yes.cart.domain.dto.impl;

import org.junit.Test;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * User: denispavlov
 * Date: 20/03/2015
 * Time: 10:28
 */
public class ProductSkuSearchResultDTOImplTest {
    @Test
    public void testCopy() throws Exception {

        final ProductSkuSearchResultDTOImpl first = new ProductSkuSearchResultDTOImpl();
        first.setId(1);
        first.setCode("First");
        first.setManufacturerCode("ManFirst");
        first.setName("FirstName");
        first.setDisplayName("FirstDisplayName");
        first.setDefaultImage("FirstDefaultImage");

        final ProductSkuSearchResultDTO copy = first.copy();

        assertEquals(first.getId(), copy.getId());
        assertEquals(first.getCode(), copy.getCode());
        assertEquals(first.getManufacturerCode(), copy.getManufacturerCode());
        assertEquals(first.getName(), copy.getName());
        assertEquals(first.getDisplayName(), copy.getDisplayName());
        assertEquals(first.getDefaultImage(), copy.getDefaultImage());


        // Do not override equals and hash code for ProductSearchResultDTO because we can have
        // multiple copies in memory used by hash maps and hash sets
        assertFalse(first.equals(copy));
        assertFalse(first.hashCode() == copy.hashCode());


    }
}

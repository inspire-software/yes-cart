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
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.Assert.*;

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
        first.setFulfilmentCentreCode("Main");
        first.setName("FirstName");
        first.setDisplayName("FirstDisplayName");
        first.setDefaultImage("FirstDefaultImage");
        first.setAvailablefrom(LocalDateTime.now());
        first.setAvailableto(LocalDateTime.now());
        first.setReleaseDate(LocalDateTime.now());
        first.setRestockDate(LocalDateTime.now());
        first.setRestockNotes(new StringI18NModel("EN#~#Some text"));
        first.setCreatedTimestamp(Instant.now());
        first.setUpdatedTimestamp(Instant.now());
        first.setAvailability(1);
        first.setMinOrderQuantity(BigDecimal.ONE);
        first.setMaxOrderQuantity(BigDecimal.TEN);
        first.setStepOrderQuantity(BigDecimal.ONE);
        first.setQtyOnWarehouse(new HashMap<Long, BigDecimal>() {{
            put(10L, BigDecimal.ONE);
        }});
        first.setFeatured(true);
        first.setDefaultImage("FirstDefaultImageSKU.jpg");
        first.setTag("tag1 tag2");

        final ProductSkuSearchResultDTO copy = first.copy();

        assertEquals(first.getId(), copy.getId());
        assertEquals(first.getCode(), copy.getCode());
        assertEquals(first.getManufacturerCode(), copy.getManufacturerCode());
        assertEquals(first.getFulfilmentCentreCode(), copy.getFulfilmentCentreCode());
        assertEquals(first.getName(), copy.getName());
        assertEquals(first.getDisplayName(), copy.getDisplayName());
        assertEquals(first.getDefaultImage(), copy.getDefaultImage());
        assertEquals(first.getDisplayName(), copy.getDisplayName());
        assertEquals(first.getDescription(), copy.getDescription());
        assertEquals(first.getDisplayDescription(), copy.getDisplayDescription());
        assertEquals(first.getAvailablefrom(), copy.getAvailablefrom());
        assertEquals(first.getAvailableto(), copy.getAvailableto());
        assertEquals(first.getReleaseDate(), copy.getReleaseDate());
        assertEquals(first.getRestockDate(), copy.getRestockDate());
        assertEquals("Some text", first.getRestockNote("EN"));
        assertEquals(first.getRestockNote("EN"), copy.getRestockNote("EN"));
        assertEquals(first.getCreatedTimestamp(), copy.getCreatedTimestamp());
        assertEquals(first.getUpdatedTimestamp(), copy.getUpdatedTimestamp());
        assertEquals(first.getAvailability(), copy.getAvailability());
        assertEquals(first.getMinOrderQuantity(), copy.getMinOrderQuantity());
        assertEquals(first.getMaxOrderQuantity(), copy.getMaxOrderQuantity());
        assertEquals(first.getStepOrderQuantity(), copy.getStepOrderQuantity());
        assertNotNull(first.getQtyOnWarehouse(10L));
        assertEquals(BigDecimal.ONE, first.getQtyOnWarehouse(10L));
        assertEquals(BigDecimal.ONE, copy.getQtyOnWarehouse(10L));
        assertEquals(first.getDefaultImage(), copy.getDefaultImage());
        assertEquals(first.isFeatured(), copy.isFeatured());
        assertEquals(first.getTag(), copy.getTag());

        // Do not override equals and hash code for ProductSearchResultDTO because we can have
        // multiple copies in memory used by hash maps and hash sets
        assertFalse(first.equals(copy));
        assertFalse(first.hashCode() == copy.hashCode());


    }
}

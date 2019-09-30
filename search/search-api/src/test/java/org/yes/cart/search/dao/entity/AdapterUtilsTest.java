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

package org.yes.cart.search.dao.entity;

import org.junit.Test;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.dto.impl.ProductSearchResultDTOImpl;
import org.yes.cart.domain.dto.impl.ProductSkuSearchResultDTOImpl;
import org.yes.cart.domain.dto.impl.StoredAttributesDTOImpl;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 20/03/2018
 * Time: 09:48
 */
public class AdapterUtilsTest {
    @Test
    public void readObjectFieldValue() throws Exception {

        final ProductSkuSearchResultDTOImpl dtoSKU = new ProductSkuSearchResultDTOImpl();
        dtoSKU.setAttributes(new StoredAttributesDTOImpl());
        dtoSKU.setDisplayName(new StringI18NModel("EN#~#DisplayName SKU"));
        dtoSKU.setCode("CODE");
        dtoSKU.setAvailability(2);
        dtoSKU.setAvailablefrom(LocalDateTime.now());
        dtoSKU.setMaxOrderQuantity(BigDecimal.ONE);
        dtoSKU.setQtyOnWarehouse(new HashMap<Long, BigDecimal>() {{
            put(123L, BigDecimal.TEN);
        }});
        dtoSKU.getAttributes().putValue("attr1", "val1", "en#~#name1");

        final ProductSearchResultDTOImpl dto = new ProductSearchResultDTOImpl();
        dto.setAttributes(new StoredAttributesDTOImpl());
        dto.setDisplayName(new StringI18NModel("EN#~#DisplayName"));
        dto.setCode("CODE");
        dto.setShippable(true);
        dto.setEnsemble(true);
        dto.getAttributes().putValue("attr1", "val1", "en#~#name1");
        dto.setBaseSkus(new HashMap<Long, ProductSkuSearchResultDTO>() {{
            put(dtoSKU.getId(), dtoSKU);
        }});

        final String json = AdapterUtils.writeObjectFieldValue(dto);

        final ProductSearchResultDTO dtoCopy = AdapterUtils.readObjectFieldValue(json, dto.getClass());

        assertNotNull(dtoCopy);
        assertEquals("CODE", dtoCopy.getCode());
        assertEquals(1, dtoCopy.getAvailability());
        assertNull(dtoCopy.getAvailablefrom());
        assertNull(dtoCopy.getMaxOrderQuantity());
        assertNotNull(dtoCopy.getQtyOnWarehouse(123L));
        assertTrue(dtoCopy.getQtyOnWarehouse(123L).isEmpty());
        assertNotNull(dtoCopy.getAttributes());
        assertNotNull(dtoCopy.getAttributes().getValue("attr1"));
        assertEquals("val1", dtoCopy.getAttributes().getValue("attr1").getFirst());
        assertNotNull(dtoCopy.getAttributes().getValue("attr1").getSecond());
        assertEquals("name1", dtoCopy.getAttributes().getValue("attr1").getSecond().getValue("en"));
        assertFalse(dtoCopy.isService());
        assertTrue(dtoCopy.isShippable());
        assertFalse(dtoCopy.isDigital());
        assertFalse(dtoCopy.isDownloadable());
        assertTrue(dtoCopy.isEnsemble());

        final ProductSkuSearchResultDTO dtoSkuCopy = dtoCopy.getBaseSku(dtoSKU.getId());
        assertNotNull(dtoSkuCopy);
        assertNotSame(dtoSKU, dtoSkuCopy);

        assertNotNull(dtoSkuCopy.getAvailablefrom());
        assertEquals(BigDecimal.ONE, dtoSkuCopy.getMaxOrderQuantity());
        assertNotNull(dtoCopy.getQtyOnWarehouse(123L));
        assertNull(dtoCopy.getQtyOnWarehouse(123L).get("ABC"));

        dtoCopy.setSearchSkus(Collections.singletonList(dtoSkuCopy));

        assertNotNull(dtoCopy.getAvailablefrom());
        assertEquals(BigDecimal.ONE, dtoCopy.getMaxOrderQuantity());
        assertNotNull(dtoCopy.getQtyOnWarehouse(123L));
        assertEquals(BigDecimal.TEN, dtoCopy.getQtyOnWarehouse(123L).get("CODE"));


    }

}
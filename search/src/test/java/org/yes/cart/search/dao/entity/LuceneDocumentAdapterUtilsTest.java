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
import org.yes.cart.domain.dto.impl.ProductSearchResultDTOImpl;
import org.yes.cart.domain.dto.impl.StoredAttributesDTOImpl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 14/09/2017
 * Time: 20:56
 */
public class LuceneDocumentAdapterUtilsTest {
    @Test
    public void readObjectFieldValue() throws Exception {

        final ProductSearchResultDTOImpl dto = new ProductSearchResultDTOImpl();
        dto.setAttributes(new StoredAttributesDTOImpl());
        dto.setCode("CODE");
        dto.setAvailability(1);
        dto.setAvailablefrom(new Date());
        dto.setShippable(true);
        dto.setEnsemble(true);
        dto.setMaxOrderQuantity(BigDecimal.ONE);
        dto.setQtyOnWarehouse(new HashMap<Long, Map<String, BigDecimal>>() {{
            put(123L, new HashMap<String, BigDecimal>() {{
                put("ABC", BigDecimal.TEN);
            }});
        }});
        dto.getAttributes().putValue("attr1", "val1", "en#~#name1");

        final String json = LuceneDocumentAdapterUtils.writeObjectFieldValue(dto);

        final ProductSearchResultDTO dtoCopy = LuceneDocumentAdapterUtils.readObjectFieldValue(json, dto.getClass());

        assertNotNull(dtoCopy);
        assertEquals("CODE", dtoCopy.getCode());
        assertEquals(1, dtoCopy.getAvailability());
        assertNotNull(dtoCopy.getAvailablefrom());
        assertEquals(BigDecimal.ONE, dtoCopy.getMaxOrderQuantity());
        assertNotNull(((ProductSearchResultDTOImpl) dtoCopy).getQtyOnWarehouse());
        assertNotNull(dtoCopy.getQtyOnWarehouse(123L));
        assertEquals(BigDecimal.TEN, dtoCopy.getQtyOnWarehouse(123L).get("ABC"));
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

    }

}
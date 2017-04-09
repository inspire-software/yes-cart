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

import org.apache.lucene.document.Document;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.LuceneDocumentAdapter;

/**
 * User: denispavlov
 * Date: 07/04/2017
 * Time: 14:22
 */
public class ProductSkuLuceneDocumentAdapter implements LuceneDocumentAdapter<ProductSku, Long> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Long, Document[]> toDocument(final ProductSku entity) {
        return null;
    }
}

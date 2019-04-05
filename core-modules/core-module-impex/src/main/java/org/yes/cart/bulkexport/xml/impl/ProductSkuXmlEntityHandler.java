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

package org.yes.cart.bulkexport.xml.impl;

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.async.JobStatusListener;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class ProductSkuXmlEntityHandler extends AbstractXmlEntityHandler<ProductSku> {

    public ProductSkuXmlEntityHandler() {
        super("sku");
    }

    @Override
    public String handle(final JobStatusListener statusListener,
                         final XmlExportDescriptor xmlExportDescriptor,
                         final ImpExTuple<String, ProductSku> tuple,
                         final XmlValueAdapter xmlValueAdapter,
                         final String fileToExport) {

        return tagSku(null, tuple.getData()).toXml();

    }

    Tag tagSku(final Tag parent, final ProductSku sku) {

        return tag(parent, "sku")
                .attr("id", sku.getSkuId())
                .attr("product-id", sku.getProduct().getProductId())
                .attr("guid", sku.getGuid())
                .attr("code", sku.getCode())
                .attr("product-code", sku.getProduct().getCode())
                .attr("rank", sku.getRank())
                    .tag("manufacturer")
                        .tagChars("manufacturer-code", sku.getManufacturerCode())
                        .tagChars("manufacturer-part-code", sku.getManufacturerPartCode())
                        .tagChars("barcode", sku.getBarCode())
                    .end()
                    .tag("supplier")
                        .tagChars("supplier-code", sku.getSupplierCode())
                        .tagChars("supplier-catalog-code", sku.getSupplierCatalogCode())
                    .end()
                    .tagCdata("name", sku.getName())
                    .tagI18n("display-name", sku.getDisplayName())
                    .tagCdata("description", sku.getDescription())
                    .tagSeo(sku)
                    .tagExt(sku)
                    .tagTime(sku)
                .end();
    }
    
}

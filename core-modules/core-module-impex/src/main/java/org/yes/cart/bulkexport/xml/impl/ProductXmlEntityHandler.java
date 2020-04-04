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

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class ProductXmlEntityHandler extends AbstractXmlEntityHandler<Product> {

    private final ProductSkuXmlEntityHandler skuHandler = new ProductSkuXmlEntityHandler();

    public ProductXmlEntityHandler() {
        super("products");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Product> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer,
                       final Map<String, Integer> entityCount) throws Exception {

        handleInternal(tagProduct(null, tuple.getData()), writer, entityCount);

    }


    Tag tagProduct(final Tag parent, final Product product) {

        final Tag tag = tag(parent, "product")
                .attr("id", product.getProductId())
                .attr("guid", product.getGuid())
                .attr("code", product.getCode())
                .tagChars("brand", product.getBrand().getName())
                .tag("product-type")
                    .attr("id", product.getProducttype().getProducttypeId())
                    .attr("guid", product.getProducttype().getGuid())
                .end()
                .tag("manufacturer")
                    .tagChars("manufacturer-code", product.getManufacturerCode())
                    .tagChars("manufacturer-part-code", product.getManufacturerPartCode())
                .end()
                .tag("supplier")
                    .tagChars("supplier-code", product.getSupplierCode())
                    .tagChars("supplier-catalog-code", product.getSupplierCatalogCode())
                .end()
                .tag("pim")
                    .attr("disabled", product.getPimDisabled())
                    .tagChars("pim-code", product.getPimCode())
                    .tagBool("pim-outdated", product.getPimOutdated())
                    .tagTime("pim-updated", product.getPimUpdated())
                .end()
                .tagCdata("name", product.getName())
                .tagI18n("display-name", product.getDisplayName())
                .tagCdata("description", product.getDescription())
                .tagList("tags", "tag", product.getTag(), ' ')
                .tagSeo(product)
                .tagExt(product);

        final Tag catTag = tag.tag("product-categories");

        for (final ProductCategory pc : product.getProductCategory()) {

            catTag.tag("product-category")
                    .attr("id", pc.getCategory().getCategoryId())
                    .attr("guid", pc.getCategory().getGuid())
                    .attr("rank", pc.getRank()).end();

        }

        catTag.end();

        final Tag skuTag = tag.tag("product-sku");

        for (final ProductSku sku : product.getSku()) {

            this.skuHandler.tagSku(skuTag, sku);
            
        }

        skuTag.end();

        final Tag assocTag = tag.tag("product-links");

        for (final ProductAssociation pa : product.getProductAssociations()) {

            catTag.tag("product-link")
                    .attr("association", pa.getAssociation().getCode())
                    .attr("sku", pa.getAssociatedSku())
                    .attr("rank", pa.getRank()).end();

        }

        assocTag.end();

        final Tag optionsTag = tag.tag("product-options").attr("configurable", product.getOptions().isConfigurable());

        for (final ProductOption po : product.getOptions().getConfigurationOption()) {

            if (CollectionUtils.isNotEmpty(po.getOptionSkuCodes())) {

                final Tag spo = optionsTag.tag("product-option")
                        .attr("mandatory", po.isMandatory())
                        .attr("quantity", po.getQuantity())
                        .attr("rank", po.getRank())
                        .attr("sku", po.getSkuCode())
                        .attr("attribute", po.getAttributeCode());

                final Tag spov = spo.tag("product-option-values");

                for (final String pov : po.getOptionSkuCodes()) {
                    spov.tag("product-option-value").attr("sku", pov).end();
                }

                spov.end();
                spo.end();
            }

        }

        optionsTag.end();

        return tag
                .tagTime(product)
            .end();

    }

    /**
     * Spring IoC.
     *
     * @param prettyPrint set pretty print mode (new lines and indents)
     */
    @Override
    public void setPrettyPrint(final boolean prettyPrint) {
        super.setPrettyPrint(prettyPrint);
        this.skuHandler.setPrettyPrint(prettyPrint);
    }
}

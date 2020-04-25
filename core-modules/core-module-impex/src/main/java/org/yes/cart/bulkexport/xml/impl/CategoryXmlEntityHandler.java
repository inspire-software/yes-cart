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
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.CategoryService;

import java.io.OutputStreamWriter;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class CategoryXmlEntityHandler extends AbstractXmlEntityHandler<Category> {

    private CategoryService categoryService;

    public CategoryXmlEntityHandler() {
        super("categories");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Category> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer) throws Exception {

        handleInternal(tagCategory(null, tuple.getData()), writer, statusListener);

    }


    Tag tagCategory(final Tag parent, final Category category) {

        final Tag tag = tag(parent, "category")
                .attr("id", category.getCategoryId())
                .attr("guid", category.getGuid())
                .attr("rank", category.getRank());

        Long productTypeId = null;
        String productTypeGuid = null;
        if (category.getProductType() != null) {
            productTypeId = category.getProductType().getProducttypeId();
            productTypeGuid = category.getProductType().getGuid();
        }

            tag
                .tagCdata("name", category.getName())
                .tagI18n("display-name", category.getDisplayName())
                .tagCdata("description", category.getDescription());


        if (category.getParentId() > 0L) {
            final Category parentCat = this.categoryService.findById(category.getParentId());
            if (parentCat != null) {
                tag
                    .tag("parent")
                        .attr("id", parentCat.getCategoryId())
                        .attr("guid", parentCat.getGuid())
                    .end();
            }
        }


        if (category.getLinkToId() != null) {
            final Category linkCat = this.categoryService.findById(category.getLinkToId());
            if (linkCat != null) {
                tag
                    .tag("link")
                        .attr("id", linkCat.getParentId())
                        .attr("guid", linkCat.getGuid())
                    .end();
            }
        }


            tag
                .tag("availability")
                    .attr("disabled", category.isDisabled())
                    .tagTime("available-from", category.getAvailablefrom())
                    .tagTime("available-to", category.getAvailableto())
                .end();

            final Tag nav = tag.tag("navigation");

                final Tag attrs = nav
                    .tag("attributes")
                        .attr("enabled", category.getNavigationByAttributes());

                if (productTypeGuid != null) {
                        attrs.tag("product-type")
                            .attr("id", productTypeId)
                            .attr("guid", productTypeGuid)
                        .end();
                }

                attrs.end();

                final Tag price = nav
                    .tag("price")
                        .attr("enabled", category.getNavigationByPrice());

                final PriceTierTree catPriceTiers = category.getNavigationByPriceTree();
                if (catPriceTiers != null && CollectionUtils.isNotEmpty(catPriceTiers.getSupportedCurrencies())) {
                    final Tag tiers = price.tag("price-navigation");

                    final Tag tierCurrencies = tiers.tag("currencies");

                    for (final String currency : catPriceTiers.getSupportedCurrencies()) {

                        final Tag tierCurrency = tierCurrencies.tag("currency");

                            tierCurrency.tagChars("name", currency);
                            final Tag tierCurrencyItems = tierCurrency.tag("price-tiers");

                            for (final PriceTierNode node : catPriceTiers.getPriceTierNodes(currency)) {

                                tierCurrencyItems.tag("price-tier")
                                        .tagNum("from", node.getFrom())
                                        .tagNum("to", node.getTo())
                                    .end();

                            }

                            tierCurrencyItems.end();

                        tierCurrency.end();

                    }

                    tierCurrencies.end();
                    tiers.end();
                }
                    price.end();

                nav.end();

            tag
                .tag("templates")
                    .tagChars("category", category.getUitemplate())
                .end()
                .tagSeo(category)
                .tagExt(category);

        return tag
                .tagTime(category)
            .end();

    }

    /**
     * Spring IoC.
     *
     * @param categoryService category service
     */
    public void setCategoryService(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }
}

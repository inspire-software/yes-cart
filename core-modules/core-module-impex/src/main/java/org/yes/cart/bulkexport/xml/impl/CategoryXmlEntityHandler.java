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
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.CategoryService;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class CategoryXmlEntityHandler extends AbstractXmlEntityHandler<Category> {

    private final CategoryService categoryService;

    public CategoryXmlEntityHandler(final CategoryService categoryService) {
        super("categories");
        this.categoryService = categoryService;
    }

    @Override
    public String handle(final JobStatusListener statusListener,
                         final XmlExportDescriptor xmlExportDescriptor,
                         final ImpExTuple<String, Category> tuple,
                         final XmlValueAdapter xmlValueAdapter,
                         final String fileToExport) {

        return tagCategory(null, tuple.getData()).toXml();

    }


    Tag tagCategory(final Tag parent, final Category category) {

        final Tag tag = tag(parent, "category")
                .attr("id", category.getCategoryId())
                .attr("guid", category.getGuid())
                .attr("rank", category.getRank());

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

        Long productTypeId = null;
        String productTypeGuid = null;
        if (category.getProductType() != null) {
            productTypeId = category.getProductType().getProducttypeId();
            productTypeGuid = category.getProductType().getGuid();
        }

            tag
                .tag("availability")
                    .attr("disabled", category.isDisabled())
                    .tagTime("available-from", category.getAvailablefrom())
                    .tagTime("available-to", category.getAvailableto())
                .end()
                .tagCdata("name", category.getName())
                .tagI18n("display-name", category.getDisplayName())
                .tagCdata("description", category.getDescription())
                .tag("navigation")
                    .tag("navigation-by-attributes")
                        .attr("enabled", category.getNavigationByAttributes())
                        .tag("navigation-product-type")
                            .attr("id", productTypeId)
                            .attr("guid", productTypeGuid)
                        .end()
                    .end()
                    .tag("navigation-by-price")
                        .attr("enabled", category.getNavigationByPrice())
                        .tag("price-tiers")
                            .cdata(category.getNavigationByPriceTiers())
                        .end()
                    .end()
                .end()
                .tag("templates")
                    .tagChars("category", category.getUitemplate())
                .end()
                .tagSeo(category)
                .tagExt(category);

        return tag
                .tagTime(category)
            .end();

    }

}

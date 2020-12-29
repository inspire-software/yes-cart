/*
 * Copyright 2009 Inspire-Software.com
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
import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.ProdTypeAttributeViewGroup;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.entity.ProductTypeAttr;
import org.yes.cart.domain.misc.navigation.range.DisplayValue;
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.domain.misc.navigation.range.RangeNode;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.AttributeService;

import java.io.OutputStreamWriter;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class ProductTypeXmlEntityHandler extends AbstractXmlEntityHandler<ProductType> {

    private AttributeService attributeService;

    public ProductTypeXmlEntityHandler() {
        super("product-types");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, ProductType> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer) throws Exception {

        handleInternal(tagProductType(null, tuple.getData()), writer, statusListener);

    }

    Tag tagProductType(final Tag parent, final ProductType type) {

        final Tag tag = tag(parent, "product-type")
                .attr("id", type.getProducttypeId())
                .attr("guid", type.getGuid())
                    .tagChars("name", type.getName())
                    .tagI18n("display-name", type.getDisplayName())
                    .tagCdata("description", type.getDescription())
                    .tag("templates")
                        .tagChars("product", type.getUitemplate())
                        .tagChars("category", type.getUisearchtemplate())
                    .end()
                    .tag("configuration")
                        .tagBool("service", type.isService())
                        .tagBool("shippable", type.isShippable())
                        .tagBool("digital", type.isDigital())
                        .tagBool("downloadable", type.isDownloadable())
                    .end();

        if (CollectionUtils.isNotEmpty(type.getAttributeViewGroup())) {
            final Tag group = tag(tag, "groups");
            for (final ProdTypeAttributeViewGroup vg : type.getAttributeViewGroup()) {
                final Tag vgTag = group.tag("group")
                        .attr("id", vg.getProdTypeAttributeViewGroupId())
                        .attr("guid", vg.getGuid())
                        .attr("rank", vg.getRank())
                        .tagChars("name", vg.getName())
                        .tagI18n("display-name", vg.getDisplayName());
                final String[] attrs = StringUtils.split(vg.getAttrCodeList(), ',');
                if (attrs != null) {
                    final Tag attsTag = vgTag.tag("group-attributes");
                    for (final String attr : attrs) {
                        attsTag.tagChars("group-attribute", attr);
                    }
                    attsTag.end();
                }
                vgTag.end();
            }
            group.end();
        }

        if (CollectionUtils.isNotEmpty(type.getAttributes())) {
            final Tag group = tag(tag, "attributes");
            for (final ProductTypeAttr pta : type.getAttributes()) {
                final Tag ptaTag = group.tag("attribute")
                        .attr("id", pta.getProductTypeAttrId())
                        .attr("guid", pta.getGuid())
                        .attr("attribute", pta.getAttributeCode())
                        .attr("rank", pta.getRank())
                        .attr("visible", pta.isVisible())
                        .attr("similarity", pta.isSimilarity())
                        .attr("numeric", pta.isNumeric());

                final Attribute attribute = this.attributeService.getByAttributeCode(pta.getAttributeCode());

                if (attribute != null && attribute.isNavigation()) {
                    final Tag nav = tag(ptaTag, "navigation")
                        .attr("type", pta.getNavigationType())
                        .attr("template", pta.getNavigationTemplate());

                    final RangeList rangeList = pta.getRangeList();
                    if (rangeList != null && CollectionUtils.isNotEmpty(rangeList.getRanges())) {

                        final Tag ranges = nav.tag("range-list").tag("ranges");

                        for (final RangeNode node : rangeList.getRanges()) {

                            final Tag range = ranges.tag("range")
                                    .tagChars("from", node.getFrom())
                                    .tagChars("to", node.getTo());

                            if (CollectionUtils.isNotEmpty(node.getI18n())) {

                                final Tag displays = range.tag("i18n");

                                for (final DisplayValue dv : node.getI18n()) {

                                    displays.tag("display")
                                            .tagChars("lang", dv.getLang())
                                            .tagCdata("value", dv.getValue())
                                        .end();
                                    
                                }

                                displays.end();

                            }

                            range.end();

                        }

                        ranges.end().end();
                    }
                        nav.end();
                }
                ptaTag.end();
            }
            group.end();
        }

        return tag
                .tagTime(type)
                .end();

    }

    /**
     * Spring IoC.
     *
     * @param attributeService attribute service
     */
    public void setAttributeService(final AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}

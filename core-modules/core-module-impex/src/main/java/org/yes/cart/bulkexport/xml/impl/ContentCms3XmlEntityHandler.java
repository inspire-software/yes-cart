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

import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Attributable;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.ContentService;

import java.io.OutputStreamWriter;
import java.util.*;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class ContentCms3XmlEntityHandler extends AbstractXmlEntityHandler<Content> {

    private static final String CONTENT_ATTR_PREFIX = "CONTENT_BODY_";

    private ContentService contentService;

    public ContentCms3XmlEntityHandler() {
        super("cms");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Content> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer) throws Exception {

        handleInternal(tagContent(null, tuple.getData()), writer, statusListener);

    }


    Tag tagContent(final Tag parent, final Content content) {

        final Tag tag = tag(parent, "content")
                .attr("id", content.getContentId())
                .attr("guid", content.getGuid())
                .attr("rank", content.getRank());

        Content root = content;
        while (root != null && !root.isRoot()) {
            root = this.contentService.getById(root.getParentId());
        }
        if (root != null) {
            tag.attr("shop", root.getGuid()); // Root content has same GUID as shop code
        }

        final Attributable attributable = new FilteredAttributable(content);

            tag
                .tagCdata("name", content.getName())
                .tagI18n("display-name", content.getDisplayName())
                .tagCdata("description", content.getDescription());


        if (content.getParentId() > 0L) {
            final Content parentCat = this.contentService.findById(content.getParentId());
            if (parentCat != null) {
                tag
                        .tag("parent")
                        .attr("id", parentCat.getContentId())
                        .attr("guid", parentCat.getGuid())
                        .end();
            }
        }

            tag
                .tag("availability")
                    .attr("disabled", content.isDisabled())
                    .tagTime("available-from", content.getAvailablefrom())
                    .tagTime("available-to", content.getAvailableto())
                .end()
                .tag("templates")
                    .tagChars("content", content.getUitemplate())
                .end()
                .tagSeo(content)
                .tagExt(attributable);

        final List<String> langs = new ArrayList<>();
        int maxChunks = 0;
        for (final AttrValue av : content.getAttributes()) {
            if (av.getAttributeCode().startsWith(CONTENT_ATTR_PREFIX)) {
                langs.add(av.getAttributeCode().substring(CONTENT_ATTR_PREFIX.length(), CONTENT_ATTR_PREFIX.length() + 2));
                final int chunkIndex = NumberUtils.toInt(av.getAttributeCode().substring(av.getAttributeCode().lastIndexOf('_') + 1));
                maxChunks = Math.max(maxChunks, chunkIndex);
            }
        }

        final Tag body = tag.tag("body");
        final Map<String, AttrValue> attrMap = content.getAllAttributesAsMap();
        for (final String lang : langs) {
            final Map<String, String> sortedMap = new TreeMap<>();
            for (int i = 1; i <= maxChunks; i++) {
                final String key = CONTENT_ATTR_PREFIX + lang + '_' + i;
                final AttrValue chunk = attrMap.get(key);
                if (chunk != null) {
                    sortedMap.put(key, chunk.getVal());
                }
            }
            final StringBuilder fullContent = new StringBuilder();
            for (final Map.Entry<String, String> entry : sortedMap.entrySet()) {
                fullContent.append(entry.getValue());
            }
            body.tag("body-content").attr("lang", lang).cdata(fullContent.toString()).end();

        }
        body.end();

        return tag
                .tagTime(content)
            .end();

    }

    private static class FilteredAttributable implements Attributable {

        private final Content content;


        private FilteredAttributable(final Content content) {
            this.content = content;
        }

        @Override
        public long getId() {
            return content.getId();
        }

        @Override
        public Collection<AttrValue> getAllAttributes() {
            final List<AttrValue> filtered = new ArrayList<>(content.getAllAttributes());
            filtered.removeIf(av -> av.getAttributeCode().startsWith(CONTENT_ATTR_PREFIX));
            return filtered;
        }

        @Override
        public Map<String, AttrValue> getAllAttributesAsMap() {
            return content.getAllAttributesAsMap();
        }

        @Override
        public Collection getAttributesByCode(final String attributeCode) {
            return content.getAttributesByCode(attributeCode);
        }

        @Override
        public AttrValue getAttributeByCode(final String attributeCode) {
            return content.getAttributeByCode(attributeCode);
        }

        @Override
        public String getAttributeValueByCode(final String attributeCode) {
            return content.getAttributeValueByCode(attributeCode);
        }

        @Override
        public boolean isAttributeValueByCodeTrue(final String attributeCode) {
            return content.isAttributeValueByCodeTrue(attributeCode);
        }

        @Override
        public String getName() {
            return content.getName();
        }

        @Override
        public void setName(final String name) {

        }

        @Override
        public String getDescription() {
            return content.getDescription();
        }

        @Override
        public void setDescription(final String description) {

        }
    }

    /**
     * Spring IoC.
     *
     * @param contentService content service
     */
    public void setContentService(final ContentService contentService) {
        this.contentService = contentService;
    }
}

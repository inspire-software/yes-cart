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

package org.yes.cart.domain.entity.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 20/04/2019
 * Time: 18:46
 */
public class ContentCategoryAdapter implements org.yes.cart.domain.entity.Content, java.io.Serializable {

    private static final Pattern CONTENT_BODY_PART = Pattern.compile("CONTENT_BODY_([a-z]{2})_(\\d+)$");
    // This is the limit on AV.val field - do not change unless changing schema
    private static final int CHUNK_SIZE = 4000;

    private final Category category;

    private Map<String, String> contentCache;


    public ContentCategoryAdapter(final Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public long getContentId() {
        return category.getCategoryId();
    }

    @Override
    public void setContentId(final long contentId) {
        category.setCategoryId(contentId);
    }

    @Override
    public long getParentId() {
        return category.getParentId();
    }

    @Override
    public void setParentId(final long parentId) {
        category.setParentId(parentId);
    }

    @Override
    public I18NModel getDisplayName() {
        return category.getDisplayName();
    }

    @Override
    public void setDisplayName(final I18NModel name) {
        category.setDisplayName(name);
    }

    @Override
    public String getUitemplate() {
        return category.getUitemplate();
    }

    @Override
    public void setUitemplate(final String uitemplate) {
        category.setUitemplate(uitemplate);
    }

    @Override
    public boolean isDisabled() {
        return category.isDisabled();
    }

    @Override
    public void setDisabled(final boolean disabled) {
        category.setDisabled(disabled);
    }

    @Override
    public LocalDateTime getAvailablefrom() {
        return category.getAvailablefrom();
    }

    @Override
    public void setAvailablefrom(final LocalDateTime availablefrom) {
        category.setAvailablefrom(availablefrom);
    }

    @Override
    public LocalDateTime getAvailableto() {
        return category.getAvailableto();
    }

    @Override
    public void setAvailableto(final LocalDateTime availableto) {
        category.setAvailableto(availableto);
    }

    @Override
    public boolean isAvailable(final LocalDateTime now) {
        return category.isAvailable(now);
    }

    @Override
    public Map<String, String> getBodies() {

        if (contentCache != null) {
            return contentCache;
        }

        return new HashMap<>(reloadContentBodies());

    }

    Map<String, String> reloadContentBodies() {

        final Map<String, Map<String, String>> contentParts = new HashMap<>();

        for (AttrValueCategory av : category.getAttributes()) {
            final Matcher matcher = CONTENT_BODY_PART.matcher(av.getAttributeCode());
            if (matcher.find()) {
                final String locale = matcher.group(1);
                final String part = matcher.group(2);
                if (StringUtils.isNotBlank(av.getVal())) {
                    if (!contentParts.containsKey(locale)) {
                        contentParts.put(locale, new HashMap<>());
                    }
                    final Map<String, String> parts = contentParts.get(locale);
                    parts.put(part, av.getVal());
                }
            }
        }

        final Map<String, String> content = new HashMap<>();
        for (final Map.Entry<String, Map<String, String>> langParts : contentParts.entrySet()) {
            final String lang = langParts.getKey();
            final StringBuilder parts = new StringBuilder();
            for (final String partNo : new TreeSet<>(langParts.getValue().keySet())) {
                parts.append(langParts.getValue().get(partNo));
            }
            content.put(lang, parts.toString());
        }

        this.contentCache = content;
        return content;
    }

    @Override
    public void setBodies(final Map<String, String> bodies) {

        final Map<String, String> content = getBodies();

        for (final String existingLang : content.keySet()) {

            if (bodies == null || StringUtils.isBlank(bodies.get(existingLang))) {
                putBody(existingLang, null);
            }

        }

        if (bodies != null) {
            for (final Map.Entry<String, String> newBody : bodies.entrySet()) {

                if (StringUtils.isNotBlank(newBody.getValue())) {

                    putBody(newBody.getKey(), newBody.getValue());

                }

            }
        }

    }

    @Override
    public String getBody(final String language) {

        final Map<String, String> content = getBodies();

        return content.get(language);

    }

    @Override
    public void putBody(final String language, final String body) {

        final String keyStart = "CONTENT_BODY_" + language;

        category.getAttributes().removeIf(av -> av.getAttributeCode().startsWith(keyStart));

        if (StringUtils.isNotBlank(body)) {
            int pos = 0;
            int chunkCount = 1;
            String part;
            do {
                part = pos + CHUNK_SIZE > body.length() ? body.substring(pos) : body.substring(pos, pos + CHUNK_SIZE);
                AttrValueCategory valueEntityCategory = new AttrValueEntityCategory();
                valueEntityCategory.setAttributeCode(keyStart + '_' + chunkCount);
                valueEntityCategory.setCategory(category);
                valueEntityCategory.setVal(part);
                category.getAttributes().add(valueEntityCategory);
                chunkCount++;
                pos += CHUNK_SIZE;
            } while (pos < body.length());
        }

        reloadContentBodies();

    }

    @Override
    public Collection<AttrValueContent> getAttributes() {
        final Collection<AttrValueContent> avc = new ArrayList<>();
        for (final AttrValueCategory avt : category.getAttributes()) {
            avc.add(new AttrValueEntityContentCategoryAdapter(avt));
        }
        return avc;
    }

    @Override
    public void setAttributes(final Collection<AttrValueContent> attribute) {
        throw new UnsupportedOperationException("Use original category object to set attributes");
    }

    @Override
    public boolean isRoot() {
        return getParentId() == 0L;
    }

    @Override
    public Collection<AttrValue> getAllAttributes() {
        return new ArrayList<>(getAttributes());
    }

    @Override
    public Map<String, AttrValue> getAllAttributesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<>();
        for (AttrValue attrValue : getAllAttributes()) {
            if (attrValue != null && attrValue.getAttributeCode() != null) {
                rez.put(attrValue.getAttributeCode(), attrValue);
            }
        }
        return rez;
    }

    @Override
    public Set<AttrValueContent> getAttributesByCode(final String attributeCode) {
        final Collection<AttrValueCategory> avcs = this.category.getAttributesByCode(attributeCode);
        if (avcs != null) {
            final Set<AttrValueContent> result = new HashSet<>();
            for (final AttrValueCategory avt : category.getAttributes()) {
                result.add(new AttrValueEntityContentCategoryAdapter(avt));
            }
            return result;
        }
        return null;
    }

    @Override
    public AttrValue getAttributeByCode(final String attributeCode) {
        final AttrValue avc = this.category.getAttributeByCode(attributeCode);
        if (avc != null) {
            return new AttrValueEntityContentCategoryAdapter((AttrValueCategory) avc);
        }
        return null;
    }

    @Override
    public String getAttributeValueByCode(final String attributeCode) {
        return this.category.getAttributeValueByCode(attributeCode);
    }

    @Override
    public boolean isAttributeValueByCodeTrue(final String attributeCode) {
        return this.category.isAttributeValueByCodeTrue(attributeCode);
    }

    @Override
    public String getName() {
        return this.category.getName();
    }

    @Override
    public void setName(final String name) {
        this.category.setName(name);
    }

    @Override
    public String getDescription() {
        return this.category.getDescription();
    }

    @Override
    public void setDescription(final String description) {
        this.category.setDescription(description);
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.category.getCreatedTimestamp();
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.category.setCreatedTimestamp(createdTimestamp);
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.category.getUpdatedTimestamp();
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.category.setUpdatedTimestamp(updatedTimestamp);
    }

    @Override
    public String getCreatedBy() {
        return this.category.getCreatedBy();
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.category.setCreatedBy(createdBy);
    }

    @Override
    public String getUpdatedBy() {
        return this.category.getUpdatedBy();
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.category.setUpdatedBy(updatedBy);
    }

    @Override
    public String getGuid() {
        return this.category.getGuid();
    }

    @Override
    public void setGuid(final String guid) {
        this.category.setGuid(guid);
    }

    @Override
    public long getId() {
        return this.category.getId();
    }

    @Override
    public int getRank() {
        return this.category.getRank();
    }

    @Override
    public void setRank(final int rank) {
        this.category.setRank(rank);
    }

    @Override
    public Seo getSeo() {
        return this.category.getSeo();
    }

    @Override
    public void setSeo(final Seo seo) {
        this.category.setSeo(seo);
    }

    @Override
    public long getVersion() {
        return this.category.getVersion();
    }
}

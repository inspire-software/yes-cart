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
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueContent;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.entity.Seo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 27/04/2019
 * Time: 12:36
 */
public class ContentEntity implements org.yes.cart.domain.entity.Content, java.io.Serializable {

    private static final Pattern CONTENT_BODY_PART = Pattern.compile("CONTENT_BODY_([a-z]{2})_(\\d+)$");
    // This is the limit on AV.val field - do not change unless changing schema
    private static final int CHUNK_SIZE = 4000;

    private long contentId;
    private long version;

    private long parentId;
    private Long linkToId;
    private int rank;
    private ProductType productType;
    private String name;
    private String displayName;
    private String description;
    private String uitemplate;
    private boolean disabled;
    private LocalDateTime availablefrom;
    private LocalDateTime availableto;
    private Collection<AttrValueContent> attributes = new ArrayList<>(0);
    private SeoEntity seoInternal;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    private Map<String, String> contentCache;

    public ContentEntity() {
    }



    @Override
    public long getParentId() {
        return this.parentId;
    }

    @Override
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override
    public int getRank() {
        return this.rank;
    }

    @Override
    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getUitemplate() {
        return this.uitemplate;
    }

    @Override
    public void setUitemplate(String uitemplate) {
        this.uitemplate = uitemplate;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public LocalDateTime getAvailablefrom() {
        return this.availablefrom;
    }

    @Override
    public void setAvailablefrom(LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    @Override
    public LocalDateTime getAvailableto() {
        return this.availableto;
    }

    @Override
    public void setAvailableto(LocalDateTime availableto) {
        this.availableto = availableto;
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

        for (AttrValueContent av : this.attributes) {
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

        this.attributes.removeIf(av -> av.getAttributeCode().startsWith(keyStart));

        if (StringUtils.isNotBlank(body)) {
            int pos = 0;
            int chunkCount = 1;
            String part;
            do {
                part = pos + CHUNK_SIZE > body.length() ? body.substring(pos) : body.substring(pos, pos + CHUNK_SIZE);
                AttrValueContent attrValueEntityContent = new AttrValueEntityContent();
                attrValueEntityContent.setAttributeCode(keyStart + '_' + chunkCount);
                attrValueEntityContent.setContent(this);
                attrValueEntityContent.setVal(part);
                this.attributes.add(attrValueEntityContent);
                chunkCount++;
                pos += CHUNK_SIZE;
            } while (pos < body.length());
        }

        reloadContentBodies();

    }

    @Override
    public Collection<AttrValueContent> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(Collection<AttrValueContent> attributes) {
        this.attributes = attributes;
    }

    public SeoEntity getSeoInternal() {
        return this.seoInternal;
    }

    public void setSeoInternal(SeoEntity seo) {
        this.seoInternal = seo;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public long getContentId() {
        return this.contentId;
    }

    @Override
    public long getId() {
        return this.contentId;
    }

    @Override
    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentEntity that = (ContentEntity) o;
        if (contentId != that.contentId) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return (int) (contentId ^ (contentId >>> 32));
    }

    @Override
    public boolean isRoot() {
        return (getParentId() == 0L || getParentId() == getContentId());
    }

    public String toString() {
        return this.getClass().getName() + this.getContentId();
    }

    @Override
    public Set<AttrValueContent> getAttributesByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        final Set<AttrValueContent> result = new HashSet<>();
        if (this.attributes != null) {
            for (AttrValueContent attrValue : this.attributes) {
                if (attributeCode.equals(attrValue.getAttributeCode())) {
                    result.add(attrValue);
                }
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    @Override
    public AttrValueContent getAttributeByCode(String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        if (this.attributes != null) {
            for (AttrValueContent attrValue : this.attributes) {
                if (attributeCode.equals(attrValue.getAttributeCode())) {
                    return attrValue;
                }
            }
        }
        return null;
    }



    @Override
    public String getAttributeValueByCode(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null ? val.getVal() : null;
    }


    @Override
    public boolean isAttributeValueByCodeTrue(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null && Boolean.valueOf(val.getVal());
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
    public Collection<AttrValue> getAllAttributes() {
        return new ArrayList<>(attributes);
    }


    /** {@inheritDoc} */
    @Override
    public Seo getSeo() {
        SeoEntity seo = getSeoInternal();
        if (seo == null) {
            seo = new SeoEntity();
            this.setSeoInternal(seo);
        }
        return seo;
    }

    /** {@inheritDoc} */
    @Override
    public void setSeo(final Seo seo) {
        this.setSeoInternal((SeoEntity) seo);
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}



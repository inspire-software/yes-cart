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

package org.yes.cart.bulkimport.xml.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.BodyContentType;
import org.yes.cart.bulkimport.xml.internal.ContentType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.domain.entity.AttrValueCategory;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ShopService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ContentXmlEntityHandler extends AbstractAttributableXmlEntityHandler<ContentType, Category, Category, AttrValueCategory> implements XmlEntityImportHandler<ContentType, Category> {

    // This is the limit on AV.val field - do not change unless changing schema
    private static final int CHUNK_SIZE = 4000;

    private ContentService contentService;
    private ShopService shopService;

    public ContentXmlEntityHandler() {
        super("content");
    }

    @Override
    protected void delete(final Category category) {
        this.contentService.delete(category);
        this.contentService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final Category domain, final ContentType xmlType, final EntityImportModeType mode) {

        if (xmlType.getParent() != null) {
            final Category parent = this.contentService.findSingleByCriteria(" where e.guid = ?1", xmlType.getParent().getGuid());
            if (parent != null) {
                if (domain.getParentId() != parent.getCategoryId()) {
                    domain.setParentId(parent.getCategoryId());
                }
            } /* else {

                final Shop shop = this.shopService.getShopByCode(xmlType.getGuid());
                if (shop == null) {
                    throw new IllegalArgumentException("content must have a shop GUID or a parent")
                }

            } */
        }

        if (xmlType.getBody() != null) {
            for (final BodyContentType body : xmlType.getBody().getBodyContent()) {
                final String lang = body.getLang();
                final String value = body.getValue();

                final Map<Integer, AttrValueCategory> chunks = new HashMap<>();
                for (final AttrValueCategory av : domain.getAttributes()) {
                    if (av.getAttributeCode().startsWith("CONTENT_BODY_" + lang + "_")) {
                        av.setVal(null);
                        final int chunkIndex = NumberUtils.toInt(av.getAttributeCode().substring(av.getAttributeCode().lastIndexOf('_') + 1));
                        chunks.put(chunkIndex, av);
                    }
                }

                if (StringUtils.isNotBlank(value)) {
                    final int max = value.length() % CHUNK_SIZE > 0 ? value.length() / CHUNK_SIZE + 1 : value.length() / CHUNK_SIZE;
                    for (int chunkCount = 0; chunkCount < max; chunkCount++) {
                        final Integer chunkIndex = chunkCount + 1;
                        final int start = chunkCount * CHUNK_SIZE;
                        final int end = start + CHUNK_SIZE > value.length() ? value.length() : start + CHUNK_SIZE;
                        final String chunkValue = value.substring(start, end);
                        if (chunks.containsKey(chunkIndex)) {
                            chunks.get(chunkIndex).setVal(chunkValue);
                        } else {
                            final AttrValueCategory avc = this.contentService.getGenericDao().getEntityFactory().getByIface(AttrValueCategory.class);
                            avc.setCategory(domain);
                            avc.setAttributeCode("CONTENT_BODY_" + lang + "_" + chunkIndex);
                            avc.setVal(chunkValue);
                            domain.getAttributes().add(avc);
                        }
                    }
                }
            }
        }

        if (xmlType.getAvailability() != null) {
            domain.setDisabled(xmlType.getAvailability().isDisabled());
            domain.setAvailablefrom(processLDT(xmlType.getAvailability().getAvailableFrom()));
            domain.setAvailableto(processLDT(xmlType.getAvailability().getAvailableTo()));
        }

        if (xmlType.getTemplates() != null) {
            domain.setUitemplate(xmlType.getTemplates().getContent());
        }

        updateSeo(xmlType.getSeo(), domain.getSeo());
        updateExt(xmlType.getCustomAttributes(), domain, domain.getAttributes());

        if (xmlType.getRank() != null) {
            domain.setRank(xmlType.getRank());
        }
        domain.setName(xmlType.getName());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        domain.setDescription(xmlType.getDescription());
        if (domain.getCategoryId() == 0L) {
            this.contentService.create(domain);
        } else {
            this.contentService.update(domain);
        }
        this.contentService.getGenericDao().flush();
        this.contentService.getGenericDao().evict(domain);
    }

    @Override
    protected Category getOrCreate(final ContentType xmlType) {
        Category category = this.contentService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (category != null) {
            return category;
        }
        category = this.contentService.getGenericDao().getEntityFactory().getByIface(Category.class);
        category.setGuid(xmlType.getGuid());
        return category;
    }

    @Override
    protected EntityImportModeType determineImportMode(final ContentType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Category domain) {
        return domain.getCategoryId() == 0L;
    }

    @Override
    protected void setMaster(final Category master, final AttrValueCategory av) {
        av.setCategory(master);
    }

    @Override
    protected Class<AttrValueCategory> getAvInterface() {
        return AttrValueCategory.class;
    }

    /**
     * Spring IoC.
     *
     * @param contentService category service
     */
    public void setContentService(final ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * Spring IoC.
     *
     * @param shopService shop service
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }
}

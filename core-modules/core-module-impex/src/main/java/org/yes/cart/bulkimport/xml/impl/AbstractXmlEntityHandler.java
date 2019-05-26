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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.XmlImportDescriptor;
import org.yes.cart.bulkimport.xml.internal.*;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public abstract class AbstractXmlEntityHandler<T, E> implements XmlEntityImportHandler<T, E> {

    protected static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final String contextNamespace;
    private final String elementName;

    protected AbstractXmlEntityHandler(final String elementName) {
        this.elementName = elementName;
        this.contextNamespace = "org.yes.cart.bulkimport.xml.internal";
    }

    protected AbstractXmlEntityHandler(final String elementName, final String contextNamespace) {
        this.elementName = elementName;
        this.contextNamespace = contextNamespace;
    }

    @Override
    public String getContextNamespace() {
        return contextNamespace;
    }

    @Override
    public String getElementName() {
        return elementName;
    }

    @Override
    public E handle(final JobStatusListener statusListener,
                    final XmlImportDescriptor xmlImportDescriptor,
                    final ImpExTuple<String, T> tuple,
                    final XmlValueAdapter xmlValueAdapter,
                    final String fileToExport,
                    final Map<String, Integer> entityCount) {

        final T xmlType = tuple.getData();

        final E domain = getOrCreate(statusListener, xmlType, entityCount);

        if (domain == null) {
            statusListener.notifyWarning("Unable to resolve domain object for {}:{}", xmlType.getClass().getSimpleName(), tuple.getSourceId());
            return null;
        }

        final boolean isNew = isNew(domain);

        final EntityImportModeType mode = determineImportMode(xmlType);

        switch (mode) {
            case DELETE:
                if (!isNew) {
                    delete(statusListener, domain, entityCount);
                    count(entityCount, mode.name(), this.elementName);
                }
                return null; // delete mode should not resolve domain object
            case INSERT_ONLY:
                if (!isNew) {
                    statusListener.notifyPing("Skipping tuple (insert restricted): " + tuple);
                    count(entityCount, "SKIP", this.elementName);
                    return domain; // no insert, return existing
                }
            case UPDATE_ONLY:
                if (isNew) {
                    statusListener.notifyPing("Skipping tuple (update restricted): " + tuple);
                    count(entityCount, "SKIP", this.elementName);
                    return null; // no update, return nothing
                }
            case MERGE:
            default:
                saveOrUpdate(statusListener, domain, xmlType, mode, entityCount);
                count(entityCount, isNew ? "INSERT" : "UPDATE", this.elementName);
                return domain; // return updated

        }

    }

    /**
     * Determine import mode for give XML
     *
     * @param xmlType XML object
     *
     * @return import mode
     */
    protected abstract EntityImportModeType determineImportMode(final T xmlType);

    /**
     * Perform delete operation.
     *
     * @param statusListener status listener
     * @param domain         domain object
     * @param entityCount    count of entities that have been imported
     */
    protected abstract void delete(final JobStatusListener statusListener,
                                   final E domain,
                                   final Map<String, Integer> entityCount);

    /**
     * Perform save or update operation.
     *
     * @param statusListener status listener
     * @param domain         domain object
     * @param xmlType        XML object
     * @param mode           desired mode
     * @param entityCount    count of entities that have been imported
     */
    protected abstract void saveOrUpdate(final JobStatusListener statusListener,
                                         final E domain,
                                         final T xmlType,
                                         final EntityImportModeType mode,
                                         final Map<String, Integer> entityCount);

    /**
     * Process I18n XML chunk.
     *
     * @param i18ns    i18n block
     * @param existing existing value in domain object
     *
     * @return updated i18n for domain object
     */
    protected String processI18n(final I18NsType i18ns, final String existing) {
        final I18NImportModeType mode = i18ns != null && i18ns.getImportMode() != null ? i18ns.getImportMode() : I18NImportModeType.MERGE;
        return processI18nInternal(i18ns, existing, mode);
    }

    private String processI18nInternal(final I18NsType i18ns, final String existing, final I18NImportModeType mode) {
        final I18NModel model = mode == I18NImportModeType.REPLACE ? new StringI18NModel() : new StringI18NModel(existing);
        if (i18ns != null) {
            for (final I18NType i18n : i18ns.getI18N()) {
                model.putValue(i18n.getLang(), i18n.getValue());
            }
        }
        if (model.getAllValues().isEmpty()) {
            return null;
        }
        return model.toString();
    }

    /**
     * Process tags.
     *
     * @param tags      tags block
     * @param existing  existing tags
     *
     * @return updated tags
     */
    protected String processTags(final TagsType tags, final String existing) {
        final CollectionImportModeType mode = tags != null && tags.getImportMode() != null ? tags.getImportMode() : CollectionImportModeType.REPLACE;
        return processTagsInternal(tags, existing, mode);

    }


    private String processTagsInternal(final TagsType tags, final String existing, final CollectionImportModeType mode) {

        if (tags == null) {
            return existing;
        }

        final List<String> initial = new ArrayList<>();
        if ((mode == CollectionImportModeType.MERGE || mode == CollectionImportModeType.DELETE) && StringUtils.isNotBlank(existing)) {
            final String[] items = StringUtils.split(existing, ' ');
            for (final String item : items) {
                initial.add(item.trim());
            }
        }

        if (mode == CollectionImportModeType.DELETE) {
            initial.removeAll(tags.getTag());
        } else {
            for (final String tag : tags.getTag()) {
                if (!initial.contains(tag)) {
                    initial.add(tag);
                }
            }
        }

        if (initial.isEmpty()) {
            return null;
        }
        return StringUtils.join(initial, ' ');
    }

    /**
     * Process codes as CSV
     *
     * @param codes     codes
     * @param delimiter delimiter char
     *
     * @return CSV
     */
    protected String processCodesCsv(final List<String> codes, final char delimiter) {
        if (CollectionUtils.isEmpty(codes)) {
            return null;
        }
        final List<String> list = new ArrayList<>(codes.size());
        for (final String item : codes) {
            list.add(item.trim());
        }
        return StringUtils.join(list, delimiter);
    }


    /**
     * Process Local date/time from given XML string.
     *
     * @param ldt local date time
     *
     * @return date time
     */
    protected LocalDateTime processLDT(final String ldt) {
        if (StringUtils.isBlank(ldt)) {
            return null;
        }
        return DateUtils.ldtParse(ldt, TIMESTAMP_FORMAT);
    }

    /**
     * Update SEO element.
     *
     * @param seo       SEO XML
     * @param existing  existing SEO
     */
    protected void updateSeo(final SeoType seo, final Seo existing) {
        if (seo != null) {
            final I18NImportModeType mode = seo.getImportMode() != null ? seo.getImportMode() : I18NImportModeType.MERGE;
            if (mode == I18NImportModeType.MERGE) {
                if (StringUtils.isNotBlank(seo.getUri())) {
                    existing.setUri(seo.getUri());
                }
                if (StringUtils.isNotBlank(seo.getMetaTitle())) {
                    existing.setTitle(seo.getMetaTitle());
                }
                existing.setDisplayTitle(processI18nInternal(seo.getMetaTitleDisplay(), existing.getDisplayTitle(), mode));
                if (StringUtils.isNotBlank(seo.getMetaKeywords())) {
                    existing.setMetakeywords(seo.getMetaKeywords());
                }
                existing.setDisplayMetakeywords(processI18nInternal(seo.getMetaKeywordsDisplay(), existing.getDisplayMetakeywords(), mode));
                if (StringUtils.isNotBlank(seo.getMetaDescription())) {
                    existing.setMetadescription(seo.getMetaDescription());
                }
                existing.setDisplayMetadescription(processI18nInternal(seo.getMetaDescriptionDisplay(), existing.getDisplayMetadescription(), mode));
            } else { // replace
                existing.setUri(seo.getUri());
                existing.setTitle(seo.getMetaTitle());
                existing.setDisplayTitle(processI18nInternal(seo.getMetaTitleDisplay(), existing.getDisplayTitle(), mode));
                existing.setMetakeywords(seo.getMetaKeywords());
                existing.setDisplayMetakeywords(processI18nInternal(seo.getMetaKeywordsDisplay(), existing.getDisplayMetakeywords(), mode));
                existing.setMetadescription(seo.getMetaDescription());
                existing.setDisplayMetadescription(processI18nInternal(seo.getMetaDescriptionDisplay(), existing.getDisplayMetadescription(), mode));
            }
        }
    }

    /**
     * Retrieve domain object for given XML type
     *
     * @param statusListener status listener
     * @param xmlType        XML object
     * @param entityCount    count of entities that have been imported
     *
     * @return domain object
     */
    protected abstract E getOrCreate(final JobStatusListener statusListener,
                                     final T xmlType,
                                     final Map<String, Integer> entityCount);

    /**
     * Determine if domain object is new
     *
     * @param domain domain object
     *
     * @return new flag
     */
    protected abstract boolean isNew(final E domain);



    /**
     * Convenience method to count entities.
     *
     * @param entityCount count map
     * @param entity      entity to count
     */
    protected void count(final Map<String, Integer> entityCount, final String mode, final String entity) {
        final String key = entity + " " + mode;
        if (entityCount.containsKey(key)) {
            entityCount.put(key, entityCount.get(key) + 1);
        } else {
            entityCount.put(key, 1);
        }
    }

}

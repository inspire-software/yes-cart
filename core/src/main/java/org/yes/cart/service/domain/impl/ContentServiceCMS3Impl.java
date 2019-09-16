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

package org.yes.cart.service.domain.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.theme.templates.TemplateProcessor;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.TimeContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * User: denispavlov
 * Date: 20/04/2019
 * Time: 18:41
 */
public class ContentServiceCMS3Impl extends BaseGenericServiceImpl<Content> implements ContentService, Configuration {

    private static final Logger LOG = LoggerFactory.getLogger(ContentServiceCMS3Impl.class);

    private final GenericDAO<Content, Long> contentDao;

    private final TemplateProcessor templateSupport;

    private ConfigurationContext cfgContext;

    /**
     * Construct service to manage content
     *
     * @param contentDao      content dao to use
     * @param templateSupport template support
     */
    public ContentServiceCMS3Impl(final GenericDAO<Content, Long> contentDao,
                                  final TemplateProcessor templateSupport) {
        super(contentDao);
        this.contentDao = contentDao;
        this.templateSupport = templateSupport;

        this.templateSupport.registerFunction("include", params -> {

            if (params != null && params.length == 3) {

                final String uri = String.valueOf(params[0]);

                final Long contentId = proxy().findContentIdBySeoUri(uri);

                if (contentId != null) {
                    final String locale = String.valueOf(params[1]);
                    final Map<String, Object> context = (Map<String, Object>) params[2];


                    return proxy().getDynamicContentBody(contentId, locale, context);

                }

            }

            return "";
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Content getRootContent(final long shopId) {
        if (shopId <= 0) {
            throw new IllegalArgumentException("Shop must not be null or transient");
        }
        return contentDao.findSingleByNamedQuery("CMS3.ROOTCONTENT.BY.SHOP.ID", shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Content createRootContent(final long shopId) {
        final List<Object> shops = contentDao.findQueryObjectByNamedQuery("SHOPCODE.BY.SHOP.ID", shopId);
        if (shops != null && shops.size() == 1) {
            return createContentRootForShop((String) shops.get(0));
        }
        throw new IllegalArgumentException("Unidentified shop id");
    }

    private Content createContentRootForShop(final String shopcode) {
        final LocalDateTime now = now();
        final Content root = contentDao.getEntityFactory().getByIface(Content.class);
        root.setGuid(shopcode);
        root.setName(shopcode);
        root.setParentId(0L);
        root.setUitemplate("content");
        root.setDisabled(false);
        root.setAvailablefrom(now.truncatedTo(ChronoUnit.DAYS));
        root.setAvailableto(now.plusYears(100).truncatedTo(ChronoUnit.DAYS));
        contentDao.saveOrUpdate(root);
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> getShopContentIds(final long shopId) {

        final Set<Long> result = new HashSet<>();

        final Content content = contentDao.findSingleByNamedQuery("CMS3.ROOTCONTENT.BY.SHOP.ID", shopId);

        if (content != null) {

            final List<Object[]> idParentList = (List) contentDao.findByNamedQuery("CMS3.CONTENT.PARENT.ALL");

            final Map<Long, Set<Long>> all = new HashMap<>(idParentList.size() + 100);

            for (final Object[] idParent : idParentList) {

                final Long id = (Long) idParent[0];
                final Long parent = (Long) idParent[1];

                if (parent > 0L && !id.equals(parent)) {
                    final Set<Long> children = all.computeIfAbsent(parent, k -> new HashSet<>());
                    children.add(id);
                }
            }


            appendChildren(result, content.getContentId(), all);
        }

        return Collections.unmodifiableSet(result);

    }


    private void appendChildren(final Set<Long> result, final long currentId, final Map<Long, Set<Long>> map) {

        result.add(currentId);

        final Set<Long> immediateChildren = map.get(currentId);
        if (CollectionUtils.isNotEmpty(immediateChildren)) {
            for (final Long child : immediateChildren) {
                appendChildren(result, child, map);
            }
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentTemplate(final long contentId) {
        final Content content = proxy().findById(contentId);
        if (content != null && !content.isRoot()) {
            if (StringUtils.isBlank(content.getUitemplate())) {
                return proxy().getContentTemplate(content.getParentId());
            } else {
                return content.getUitemplate();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentBody(final long contentId, final String locale) {
        final String attributeKey = "CONTENT_BODY_" + locale + "_%";
        final List<Object> bodyList = contentDao.findQueryObjectByNamedQuery("CMS3.CONTENTBODY.BY.CONTENTID", contentId, attributeKey, now(), Boolean.FALSE);
        if (bodyList != null && bodyList.size() > 0) {
            final StringBuilder content = new StringBuilder();
            for (final Object bodyPart : bodyList) {
                if (StringUtils.isNotBlank((String) bodyPart)) {
                    content.append(bodyPart);
                }
            }
            return content.toString();
        }
        return "";
    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentBody(final String contentUri, final String locale) {
        final Long id = findContentIdBySeoUri(contentUri);
        if (id != null) {
            return proxy().getContentBody(id, locale);
        }
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDynamicContentBody(final long contentId, final String locale, final Map<String, Object> context) {

        final String rawContent = proxy().getContentBody(contentId, locale);

        if (StringUtils.isNotBlank(rawContent)) {

            return this.templateSupport.processTemplate(rawContent, locale, context);

        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDynamicContentBody(final String contentUri, final String locale, final Map<String, Object> context) {

        final String rawContent = proxy().getContentBody(contentUri, locale);

        if (StringUtils.isNotBlank(rawContent)) {

            return this.templateSupport.processTemplate(rawContent, locale, context);

        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentAttributeRecursive(final String locale, final long contentId, final String attributeName, final String defaultValue) {

        final Content content = proxy().getById(contentId);

        if (content == null || attributeName == null || content.isRoot()) {
            return defaultValue;
        }

        final AttrValue attrValue = content.getAttributeByCode(attributeName);
        if (attrValue != null) {
            final String val;
            if (locale == null) {
                val = attrValue.getVal();
            } else {
                val = new FailoverStringI18NModel(attrValue.getDisplayVal(), attrValue.getVal()).getValue(locale);
            }
            if (!StringUtils.isBlank(val)) {
                return val;
            }
        }

        return proxy().getContentAttributeRecursive(locale, content.getParentId(), attributeName, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getContentAttributeRecursive(final String locale, final long contentId, final String[] attributeNames) {

        final Content content;

        if (contentId > 0L && attributeNames != null && attributeNames.length > 0) {
            content = proxy().getById(contentId);
        } else {
            return null;
        }

        if (content == null) {
            return null;
        }

        final String[] rez = new String[attributeNames.length];
        boolean hasValue = false;
        for (int i = 0; i < attributeNames.length; i++) {
            final String attributeName = attributeNames[i];
            final String val = proxy().getContentAttributeRecursive(locale, contentId, attributeName, null);
            if (val != null) {
                hasValue = true;
            }
            rez[i] = val;
        }

        if (hasValue) {
            return rez;
        }
        return null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Content> getChildContent(final long contentId) {
        return findChildContentWithAvailability(contentId, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Content> findChildContentWithAvailability(final long contentId, final boolean withAvailability) {

        final List<Content> cns = new ArrayList<>(contentDao.findByNamedQuery(
                "CMS3.CONTENT.BY.PARENTID.WITHOUT.DATE.FILTERING",
                contentId
        ));
        if (withAvailability) {

            final LocalDateTime now = now();
            cns.removeIf(cat -> !cat.isAvailable(now));

        }
        return cns;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Content> getChildContentRecursive(final long contentId) {
        final Content thisCon = proxy().getById(contentId);
        if (thisCon != null) {
            final Set<Content> all = new HashSet<>();
            all.add(thisCon);
            loadChildContentRecursiveInternal(all, thisCon);
            return all;
        }
        return Collections.emptySet();
    }


    private void loadChildContentRecursiveInternal(final Set<Content> result, final Content category) {
        List<Content> categories = proxy().getChildContent(category.getContentId());
        result.addAll(categories);
        for (Content subCategory : categories) {
            loadChildContentRecursiveInternal(result, subCategory);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Content> findBy(final long shopId, final String code, final String name, final String uri, final int page, final int pageSize) {

        final String codeP = HQLUtils.criteriaIlikeAnywhere(code);
        final String nameP = HQLUtils.criteriaIlikeAnywhere(name);
        final String uriP = HQLUtils.criteriaIlikeAnywhere(uri);

        final Content root = proxy().getRootContent(shopId);
        List<Content> cns;
        if ((codeP != null || nameP != null) && uriP != null) {
            cns = contentDao.findRangeByNamedQuery("CMS3.CONTENT.BY.CODE.NAME.URI", page * pageSize, pageSize, codeP, nameP, uriP);
        } else if (codeP == null && nameP == null && uriP != null) {
            cns = contentDao.findRangeByNamedQuery("CMS3.CONTENT.BY.URI", page * pageSize, pageSize, uriP);
        } else {
            cns = findChildContentWithAvailability(root.getContentId(), false);
        }

        final Iterator<Content> catsIt = cns.iterator();
        while (catsIt.hasNext()) {
            Content content = catsIt.next();
            if (content.isRoot()) {
                catsIt.remove();
            } else {
                final long currentCatId = content.getContentId();
                while (content.getParentId() != root.getContentId()) {
                    if (content.isRoot()) {
                        // if this is root and not shop root matches then this is not this shop's content
                        catsIt.remove();
                        break;
                    }
                    content = proxy().findById(content.getParentId());
                    if (content == null) {
                        // could have happened if import created some reassignments and we loose path to root
                        catsIt.remove();
                        LOG.warn("Found orphan content {}", currentCatId);
                        break;
                    }
                }
            }
        }
        return cns;
    }



    /**
     * {@inheritDoc} Just to cache
     */
    @Override
    public Content getById(final long pk) {
        return contentDao.findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findContentIdBySeoUri(final String seoUri) {
        List<Object> list = contentDao.findQueryObjectByNamedQuery("CMS3.CONTENT.ID.BY.SEO.URI", seoUri);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findContentIdByGUID(final String guid) {
        List<Object> list = contentDao.findQueryObjectByNamedQuery("CMS3.CONTENT.ID.BY.GUID", guid);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findSeoUriByContentId(final Long contentId) {
        List<Object> list = contentDao.findQueryObjectByNamedQuery("CMS3.SEO.URI.BY.CONTENT.ID", contentId);
        if (list != null && !list.isEmpty()) {
            final Object[] uriAndId = (Object[]) list.get(0);
            if (uriAndId[0] instanceof String) {
                return (String) uriAndId[0];
            }
            return String.valueOf(uriAndId[1]);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Content findContentIdBySeoUriOrGuid(final String seoUriOrGuid) {

        final Content content = contentDao.findSingleByNamedQuery("CMS3.CONTENT.BY.SEO.URI", seoUriOrGuid);
        if (content == null) {
            return contentDao.findSingleByNamedQuery("CMS3.CONTENT.BY.GUID", seoUriOrGuid);
        }
        return content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isContentHasSubcontent(final long topContentId, final long subContentId) {
        final Content start = proxy().getById(subContentId);
        if (start != null) {
            if (subContentId == topContentId) {
                return true;
            } else {
                final List<Content> list = new ArrayList<>();
                list.add(start);
                addParent(list, topContentId);
                return list.get(list.size() - 1).getContentId() == topContentId;
            }
        }
        return false;
    }

    private void addParent(final List<Content> contentChain, final long contentIdStopAt) {
        final Content cat = contentChain.get(contentChain.size() - 1);
        if (!cat.isRoot()) {
            final Content parent = proxy().getById(cat.getParentId());
            if (parent != null) {
                contentChain.add(parent);
                if (parent.getContentId() != contentIdStopAt) {
                    addParent(contentChain, contentIdStopAt);
                }
            }
        }
    }

    private ContentService proxy;

    private ContentService proxy() {
        if (proxy == null) {
            proxy = getSelf();
        }
        return proxy;
    }

    /**
     * @return self proxy to reuse AOP caching
     */
    public ContentService getSelf() {
        // Spring lookup method to get self proxy
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public ConfigurationContext getCfgContext() {
        return cfgContext;
    }

    public void setCfgContext(final ConfigurationContext cfgContext) {
        this.cfgContext = cfgContext;
    }

}

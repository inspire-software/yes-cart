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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.AttrValueContentDTO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.ContentDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttrValueContentDTOImpl;
import org.yes.cart.domain.dto.impl.ContentDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.AttrValueEntityCategory;
import org.yes.cart.domain.entity.impl.AttrValueEntityContentCategoryAdapter;
import org.yes.cart.domain.entity.impl.ContentCategoryAdapter;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoContentService;
import org.yes.cart.service.dto.AttrValueDTOComparator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Denis Pavlov
 * Date: 15-June-2013
 */
public class DtoContentCMS1ServiceImpl
        extends AbstractDtoServiceImpl<ContentDTO, ContentDTOImpl, Content>
        implements DtoContentService, Configuration {

    private static final ContentRankNameComparator CONTENT_RANK_NAME_COMPARATOR = new ContentRankNameComparator();
    private static final AttrValueDTOComparator ATTR_VALUE_DTO_COMPARATOR = new AttrValueDTOComparator();

    private final GenericService<Attribute> attributeService;
    private final DtoAttributeService dtoAttributeService;
    private final GenericDAO<AttrValueEntityCategory, Long> attrValueEntityCategoryDao;

    private final Assembler attrValueAssembler;

    private final ImageService imageService;
    private final FileService fileService;
    private final SystemService systemService;

    private ConfigurationContext cfgContext;

    /**
     * Construct base remote service.
     *
     * @param dtoFactory             {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param contentGenericService  content     {@link org.yes.cart.service.domain.GenericService}
     * @param imageService           {@link org.yes.cart.service.domain.ImageService} to manipulate  related images.
     * @param fileService {@link FileService} to manipulate related files
     * @param systemService          system service
     */
    public DtoContentCMS1ServiceImpl(final DtoFactory dtoFactory,
                                     final GenericService<Content> contentGenericService,
                                     final DtoAttributeService dtoAttributeService,
                                     final GenericDAO<AttrValueEntityCategory, Long> attrValueEntityCategoryDao,
                                     final ImageService imageService,
                                     final FileService fileService,
                                     final AdaptersRepository adaptersRepository,
                                     final SystemService systemService) {
        super(dtoFactory, contentGenericService, adaptersRepository);


        this.attrValueEntityCategoryDao = attrValueEntityCategoryDao;
        this.dtoAttributeService = dtoAttributeService;
        this.systemService = systemService;

        this.attributeService = dtoAttributeService.getService();


        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueContentDTO.class),
                AttrValueEntityContentCategoryAdapter.class);

        this.imageService = imageService;
        this.fileService = fileService;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentDTO> getAll() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnsupportedOperationException("Use getAllFromRoot()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createContentRoot(final long shopId) {
        ((ContentService) service).createRootContent(shopId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentDTO> getAllWithAvailabilityFilter(final long shopId, final boolean withAvailabilityFiltering)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ContentService contentService = (ContentService) service;
        Content root = contentService.getRootContent(shopId);
        if (root != null) {
            ContentDTO rootDTO = getById(root.getContentId());
            loadBranch(rootDTO, withAvailabilityFiltering, Integer.MAX_VALUE, Collections.emptyList());
            return Collections.singletonList(rootDTO);
        }
        return null;
    }


    private List<ContentDTO> loadBranch(final ContentDTO rootDTO,
                                        final boolean withAvailabilityFiltering,
                                        final int expandLevel,
                                        final List<Long> expandNodes)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (rootDTO != null) {
            final ContentService contentService = (ContentService) service;
            final List<Content> childContent = new ArrayList<>(contentService.findChildContentWithAvailability(
                    rootDTO.getContentId(),
                    withAvailabilityFiltering));
            childContent.sort(CONTENT_RANK_NAME_COMPARATOR);
            final List<ContentDTO> childCategoriesDTO = new ArrayList<>(childContent.size());
            fillDTOs(childContent, childCategoriesDTO);
            rootDTO.setChildren(childCategoriesDTO);
            if (expandLevel > 1 || !expandNodes.isEmpty()) {
                for (ContentDTO dto : childCategoriesDTO) {
                    if (expandLevel > 1 || expandNodes.contains(dto.getContentId())) {
                        dto.setChildren(loadBranch(dto, withAvailabilityFiltering, expandLevel - 1, expandNodes));
                    }
                }
            }
            return childCategoriesDTO;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentDTO> getBranchById(final long shopId, final long contentId, final List<Long> expand)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return getBranchByIdWithAvailabilityFilter(shopId, contentId, false, expand);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentDTO> getBranchByIdWithAvailabilityFilter(final long shopId, final long contentId, final boolean withAvailabilityFiltering, final List<Long> expand)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<ContentDTO> branch = new ArrayList<>();
        ContentService contentService = (ContentService) service;
        final Content branchRoot = contentId > 0L ? contentService.getById(contentId) : contentService.getRootContent(shopId);
        if (branchRoot != null) {
            ContentDTO rootDTO = getById(branchRoot.getContentId());
            if (rootDTO != null) {
                loadBranch(rootDTO, withAvailabilityFiltering, 1, expand != null ? expand : Collections.emptyList());
            }
            branch.add(rootDTO);
        }
        return branch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void assemblyPostProcess(final ContentDTO dto, final Content entity) {
        dto.setParentName(getParentName(entity));
        super.assemblyPostProcess(dto, entity);
    }

    protected String getParentName(final Content entity) {
        if (entity.getParentId() > 0L && entity.getParentId() != entity.getContentId()) {
            final Content parent = ((ContentService)getService()).getById(entity.getParentId());
            if (parent != null) {
                final String oneUp = getParentName(parent);
                if (oneUp != null) {
                    return oneUp + " > " + parent.getName();
                }
                return parent.getName();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createPostProcess(final ContentDTO dto, final Content entity) {
        bindDictionaryData(dto, entity);
        ensureBlankUriIsNull(entity);
        super.createPostProcess(dto, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updatePostProcess(final ContentDTO dto, final Content entity) {
        bindDictionaryData(dto, entity);
        ensureBlankUriIsNull(entity);
        super.updatePostProcess(dto, entity);
    }


    private void ensureBlankUriIsNull(final Seoable entity) {
        if (entity.getSeo() != null && entity.getSeo().getUri() != null && StringUtils.isBlank(entity.getSeo().getUri())) {
            entity.getSeo().setUri(null);
        }
    }

    /**
     * Bind data from dictionaries to content.
     *
     * @param instance content dto to collect data from
     * @param content content to set dictionary data to.
     */
    private void bindDictionaryData(final ContentDTO instance, final Content content) {
        // Nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentDTO> getAllByShopId(final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return getAllWithAvailabilityFilter(shopId, false);
    }


    private final static char[] PARENT_OR_URI = new char[] { '^', '@' };
    static {
        Arrays.sort(PARENT_OR_URI);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ContentDTO> findBy(final long shopId, final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        ContentService contentService = (ContentService) service;

        final List<ContentDTO> contentDTO = new ArrayList<>(pageSize);

        if (StringUtils.isNotBlank(filter)) {
            final Pair<String, String> parentOrUri = ComplexSearchUtils.checkSpecialSearch(filter, PARENT_OR_URI);

            if (parentOrUri == null) {

                fillDTOs(contentService.findBy(shopId, filter, filter, filter, page, pageSize), contentDTO);

            } else {

                if ("@".equals(parentOrUri.getFirst())) {

                    fillDTOs(contentService.findBy(shopId, null, null, parentOrUri.getSecond(), page, pageSize), contentDTO);

                } else if ("^".equals(parentOrUri.getFirst())) {

                    final List<Content> parents = contentService.findBy(shopId, parentOrUri.getSecond(), parentOrUri.getSecond(), parentOrUri.getSecond(), page, pageSize);

                    if (!parents.isEmpty()) {

                        final Set<Long> dedup = new HashSet<>();
                        final List<Content> parentsWithChildren = new ArrayList<>();
                        for (final Content parent : parents) {

                            if (!dedup.contains(parent.getContentId())) {
                                parentsWithChildren.add(parent);
                                dedup.add(parent.getContentId());
                            }
                            for (final Content child : contentService.findChildContentWithAvailability(parent.getContentId(), false)) {
                                if (!dedup.contains(child.getContentId())) {
                                    parentsWithChildren.add(child);
                                    dedup.add(child.getContentId());
                                }
                            }

                        }

                        fillDTOs(parentsWithChildren, contentDTO);

                    }

                }

            }

        } else {

            fillDTOs(contentService.findBy(shopId, null, null, null, page, pageSize), contentDTO);

        }

        return contentDTO;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUriAvailableForContent(final String seoUri, final Long contentId) {

        final Long conId = ((ContentService) service).findContentIdBySeoUri(seoUri);
        return conId == null || conId.equals(contentId);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGuidAvailableForContent(final String guid, final Long contentId) {

        final Long conId = ((ContentService) service).findContentIdByGUID(guid);
        return conId == null || conId.equals(contentId);

    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    @Override
    public Class<ContentDTO> getDtoIFace() {
        return ContentDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    @Override
    public Class<ContentDTOImpl> getDtoImpl() {
        return ContentDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    @Override
    public Class<Content> getEntityIFace() {
        return Content.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends AttrValueDTO> getEntityContentAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<AttrValueContentDTO> result = new ArrayList<>();
        final ContentDTO contentDTO = getById(entityPk);
        if (contentDTO != null) {
            final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                    AttributeGroupNames.CATEGORY, Collections.EMPTY_LIST);
            final Map<String, AttributeDTO> contentAttrsDTOs = new HashMap<>();
            for (final AttributeDTO attributeDTO : availableAttributeDTOs) {
                final Matcher matcher = CONTENT_BODY_PART.matcher(attributeDTO.getCode());
                if (matcher.find()) {
                    final String locale = matcher.group(1);
                    final String key = "CONTENT_BODY_" + locale;
                    if (!contentAttrsDTOs.containsKey(key)) {
                        final AttributeDTO global = dtoFactory.getByIface(AttributeDTO.class);
                        global.setCode(key);
                        global.setName(attributeDTO.getName());
                        global.setDisplayNames(attributeDTO.getDisplayNames());
                        global.setDescription(attributeDTO.getDescription());
                        global.setEtypeId(attributeDTO.getEtypeId());
                        global.setEtypeName(attributeDTO.getEtypeName());
                        contentAttrsDTOs.put(key, global);
                    }
                }
            }


            for (final AttributeDTO attributeDTO : contentAttrsDTOs.values()) {
                final Map<String, String> content = new HashMap<>();

                for (AttrValueContentDTO attributeValueDTO : contentDTO.getAttributes()) {
                    final Matcher matcher = CONTENT_BODY_PART.matcher(attributeValueDTO.getAttributeDTO().getCode());
                    if (matcher.find()) {
                        final String locale = matcher.group(1);
                        final String part = matcher.group(2);
                        final String key = "CONTENT_BODY_" + locale;
                        if (attributeDTO.getCode().equals(key)) {
                            if (StringUtils.isNotBlank(attributeValueDTO.getVal())) {
                                content.put(part, attributeValueDTO.getVal());
                            }
                        }
                    }
                }

                final StringBuilder parts = new StringBuilder();
                for (final String partNo : new TreeSet<>(content.keySet())) {
                    parts.append(content.get(partNo));
                }

                AttrValueContentDTO attrValueCategoryDTO = getAssemblerDtoFactory().getByIface(AttrValueContentDTO.class);
                attrValueCategoryDTO.setAttributeDTO(attributeDTO);
                attrValueCategoryDTO.setContentId(entityPk);
                attrValueCategoryDTO.setVal(parts.toString());

                result.add(attrValueCategoryDTO);


            }
            result.sort(ATTR_VALUE_DTO_COMPARATOR);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<AttrValueContentDTO> result = new ArrayList<>();
        final ContentDTO contentDTO = getById(entityPk);
        if (contentDTO != null) {
            result.addAll(contentDTO.getAttributes());
            final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                    AttributeGroupNames.CATEGORY,
                    getCodes(result));
            for (AttributeDTO attributeDTO : availableAttributeDTOs) {
                AttrValueContentDTO attrValueCategoryDTO = getAssemblerDtoFactory().getByIface(AttrValueContentDTO.class);
                attrValueCategoryDTO.setAttributeDTO(attributeDTO);
                attrValueCategoryDTO.setContentId(entityPk);
                result.add(attrValueCategoryDTO);
            }
            result.sort(ATTR_VALUE_DTO_COMPARATOR);
        }

        return result;
    }

    /*
     * Matcher that matches exact attribute such as CONTENT_BODY_en but not body parts
     * such as CONTENT_BODY_en_1, CONTENT_BODY_en_2 ... CONTENT_BODY_en_n.
     * This pattern allows to intercept virtual update for all content body parts.
     */
    private static final Pattern CONTENT_BODY = Pattern.compile("CONTENT_BODY_([a-z]{2})$");

    private static final Pattern CONTENT_BODY_PART = Pattern.compile("CONTENT_BODY_([a-z]{2})_(\\d+)$");

    // This is the limit on AV.val field - do not change unless changing schema
    private static final int CHUNK_SIZE = 4000;

    /**
     * Update attribute value.
     *
     * @param attrValueDTO value to update
     * @return updated value
     */
    @Override
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final Matcher matcher = CONTENT_BODY.matcher(attrValueDTO.getAttributeDTO().getCode());
        if (matcher.find()) {
            final String locale = matcher.group(1);
            final String keyStart = "CONTENT_BODY_" + locale;
            final String val = attrValueDTO.getVal();

            final Content content = service.findById(((AttrValueContentDTO) attrValueDTO).getContentId());
            final Category category = ((ContentCategoryAdapter) content).getCategory();
            final Iterator<AttrValueCategory> itOld = category.getAttributes().iterator();
            while (itOld.hasNext()) {
                final AttrValueCategory old = itOld.next();
                if (old.getAttributeCode().startsWith(keyStart)) {
                    itOld.remove();
                    attrValueEntityCategoryDao.delete(old);
                }
            }
            int pos = 0;
            int chunkCount = 1;
            String part;
            do {
                part = pos + CHUNK_SIZE > val.length() ? val.substring(pos) : val.substring(pos, pos + CHUNK_SIZE);
                AttrValueCategory valueEntityCategory = getPersistenceEntityFactory().getByIface(AttrValueCategory.class);
                valueEntityCategory.setAttributeCode(keyStart + '_' + chunkCount);
                valueEntityCategory.setCategory(category);
                valueEntityCategory.setVal(part);
                attrValueEntityCategoryDao.create((AttrValueEntityCategory) valueEntityCategory);
                chunkCount++;
                pos += CHUNK_SIZE;
            } while (pos < val.length());

        } else {
            final AttrValueEntityCategory valueEntityCategory = attrValueEntityCategoryDao.findById(attrValueDTO.getAttrvalueId());
            attrValueAssembler.assembleEntity(attrValueDTO, new AttrValueEntityContentCategoryAdapter(valueEntityCategory), getAdaptersRepository(), dtoFactory);
            attrValueEntityCategoryDao.update(valueEntityCategory);
        }
        return attrValueDTO;

    }


    /**
     * Delete attribute value by given pk value.
     *
     * @param attributeValuePk given pk value.
     */
    @Override
    public long deleteAttributeValue(final long attributeValuePk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException{
        final AttrValueEntityCategory valueEntityCategory = attrValueEntityCategoryDao.findById(attributeValuePk);
        final AttributeDTO attributeDTO = dtoAttributeService.findByAttributeCode(valueEntityCategory.getAttributeCode());
        if (Etype.IMAGE_BUSINESS_TYPE.equals(attributeDTO.getEtypeName())) {
            imageService.deleteImage(valueEntityCategory.getVal(),
                    Constants.CATEGORY_IMAGE_REPOSITORY_URL_PATTERN, systemService.getImageRepositoryDirectory());
        } else if (Etype.FILE_BUSINESS_TYPE.equals(attributeDTO.getEtypeName())) {
            fileService.deleteFile(valueEntityCategory.getVal(),
                    Constants.CATEGORY_FILE_REPOSITORY_URL_PATTERN, systemService.getFileRepositoryDirectory());
        }

        attrValueEntityCategoryDao.delete(valueEntityCategory);
        return valueEntityCategory.getCategory().getCategoryId();
    }

    /**
     * Create attribute value
     *
     * @param attrValueDTO value to persist
     * @return created value
     */
    @Override
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {

        final Attribute atr = attributeService.findById(attrValueDTO.getAttributeDTO().getAttributeId());
        final boolean multivalue = atr.isAllowduplicate();
        final Content content = service.findById(((AttrValueContentDTO) attrValueDTO).getContentId());
        if (!multivalue) {
            for (final AttrValueContent avp : content.getAttributes()) {
                if (avp.getAttributeCode().equals(atr.getCode())) {
                    // this is a duplicate, so need to update
                    attrValueDTO.setAttrvalueId(avp.getAttrvalueId());
                    return updateEntityAttributeValue(attrValueDTO);
                }
            }
        }

        AttrValueCategory valueEntityCategory = getPersistenceEntityFactory().getByIface(AttrValueCategory.class);
        attrValueAssembler.assembleEntity(attrValueDTO, new AttrValueEntityContentCategoryAdapter(valueEntityCategory), getAdaptersRepository(), dtoFactory);
        valueEntityCategory.setAttributeCode(atr.getCode());
        valueEntityCategory.setCategory(((ContentCategoryAdapter) content).getCategory());
        valueEntityCategory = attrValueEntityCategoryDao.create((AttrValueEntityCategory) valueEntityCategory);
        attrValueDTO.setAttrvalueId(valueEntityCategory.getAttrvalueId());
        return attrValueDTO;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO createAndBindAttrVal(final long entityPk, final String attrName, final String attrValue)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO getNewAttribute(final long entityPk) throws UnableToCreateInstanceException, UnmappedInterfaceException {
        final AttrValueContentDTO dto = new AttrValueContentDTOImpl();
        dto.setContentId(entityPk);
        return dto;
    }

    @Override
    public EntityFactory getPersistenceEntityFactory() {

        final EntityFactory base = super.getPersistenceEntityFactory();

        return new EntityFactory() {
            @Override
            public <T> T getByIface(final Class interfaceClass) {
                return (T) getByKey(interfaceClass.getName());
            }

            @Override
            public <T> T getByKey(final String entityBeanKey) {
                if (entityBeanKey.equals(Content.class.getCanonicalName())) {
                    return (T) new ContentCategoryAdapter(base.getByIface(Category.class));
                } else if (entityBeanKey.equals(AttrValueContent.class.getCanonicalName())) {
                    return (T) new AttrValueEntityContentCategoryAdapter(base.getByIface(AttrValueContent.class));
                }
                return base.getByKey(entityBeanKey);
            }

            @Override
            public Class getImplClass(final Class interfaceClass) {
                final String ifaceName = interfaceClass.getCanonicalName();
                return getImplClass(ifaceName);
            }

            @Override
            public Class getImplClass(final String entityBeanKey) {
                if (entityBeanKey.equals(Content.class.getCanonicalName())) {
                    return ContentCategoryAdapter.class;
                } else if (entityBeanKey.equals(AttrValueContent.class.getCanonicalName())) {
                    return AttrValueEntityContentCategoryAdapter.class;
                }
                return base.getImplClass(entityBeanKey);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getImageRepositoryUrlPattern() {
        return Constants.CATEGORY_IMAGE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileRepositoryUrlPattern() {
        return Constants.CATEGORY_FILE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSysFileRepositoryUrlPattern() {
        return Constants.CATEGORY_SYSFILE_REPOSITORY_URL_PATTERN;
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

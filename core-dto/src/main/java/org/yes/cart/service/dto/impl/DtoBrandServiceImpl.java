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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.AttrValueBrandDTO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.BrandDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AttrValueBrandDTOImpl;
import org.yes.cart.domain.dto.impl.BrandDTOImpl;
import org.yes.cart.domain.entity.AttrValueBrand;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.domain.entity.Etype;
import org.yes.cart.domain.entity.impl.AttrValueEntityBrand;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.dto.AttrValueDTOComparator;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoBrandService;

import java.util.*;

/**
 ** User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoBrandServiceImpl
        extends AbstractDtoServiceImpl<BrandDTO, BrandDTOImpl, Brand>
        implements DtoBrandService {

    private static final AttrValueDTOComparator ATTR_VALUE_DTO_COMPARATOR = new AttrValueDTOComparator();

    private final Assembler attrValueAssembler;
    private final DtoAttributeService dtoAttributeService;
    private final GenericDAO<AttrValueEntityBrand, Long> attrValueEntityBrandDao;
    private final ImageService imageService;
    private final FileService fileService;
    private final SystemService systemService;


    /**
     * Construct base remote service.
     *
     * @param brandGenericService       {@link GenericService}
     * @param dtoFactory    {@link DtoFactory}
     * @param dtoAttributeService dto attribute service
     * @param attrValueEntityBrandDao attribute value service
     * @param imageService {@link ImageService} to manipulate  related images.
     * @param fileService {@link FileService} to manipulate related files
     * @param systemService system service
     */
    public DtoBrandServiceImpl(final GenericService<Brand> brandGenericService,
                               final DtoFactory dtoFactory,
                               final DtoAttributeService dtoAttributeService,
                               final GenericDAO<AttrValueEntityBrand, Long> attrValueEntityBrandDao,
                               final ImageService imageService,
                               final FileService fileService,
                               final AdaptersRepository adaptersRepository,
                               final SystemService systemService) {
        super(dtoFactory, brandGenericService, adaptersRepository);

        this.systemService = systemService;
        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueBrandDTO.class),
                brandGenericService.getGenericDao().getEntityFactory().getImplClass(AttrValueBrand.class)
        );
        this.dtoAttributeService = dtoAttributeService;
        this.attrValueEntityBrandDao = attrValueEntityBrandDao;
        this.imageService = imageService;
        this.fileService = fileService;
    }



    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    @Override
    public Class<BrandDTO> getDtoIFace() {
        return BrandDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    @Override
    public Class<BrandDTOImpl> getDtoImpl() {
        return BrandDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    @Override
    public Class<Brand> getEntityIFace() {
        return Brand.class;
    }

    /** {@inheritDoc}*/
    @Override
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<AttrValueBrandDTO> result = new ArrayList<>(getById(entityPk).getAttributes());
        final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                AttributeGroupNames.BRAND,
                getCodes(result));
        for (AttributeDTO attributeDTO : availableAttributeDTOs) {
            AttrValueBrandDTO attrValueBarndDTO = getAssemblerDtoFactory().getByIface(AttrValueBrandDTO.class);
            attrValueBarndDTO.setAttributeDTO(attributeDTO);
            attrValueBarndDTO.setBrandId(entityPk);
            result.add(attrValueBarndDTO);
        }
        result.sort(ATTR_VALUE_DTO_COMPARATOR);
        return result;
    }

    /** {@inheritDoc}*/
    @Override
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final AttrValueEntityBrand valueEntityBrand = attrValueEntityBrandDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityBrand, getAdaptersRepository(), dtoFactory);
        attrValueEntityBrandDao.update(valueEntityBrand);
        return attrValueDTO;
        
    }

    /** {@inheritDoc}*/
    @Override
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {

        final Attribute atr = ((GenericService<Attribute>)dtoAttributeService.getService()).findById(attrValueDTO.getAttributeDTO().getAttributeId());
        final boolean multivalue = atr.isAllowduplicate();
        final Brand brand = service.findById(((AttrValueBrandDTO) attrValueDTO).getBrandId());
        if (!multivalue) {
            for (final AttrValueBrand avp : brand.getAttributes()) {
                if (avp.getAttributeCode().equals(atr.getCode())) {
                    // this is a duplicate, so need to update
                    attrValueDTO.setAttrvalueId(avp.getAttrvalueId());
                    return updateEntityAttributeValue(attrValueDTO);
                }
            }
        }

        AttrValueBrand valueEntityBrand = getPersistenceEntityFactory().getByIface(AttrValueBrand.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityBrand, getAdaptersRepository(), dtoFactory);
        valueEntityBrand.setAttributeCode(atr.getCode());
        valueEntityBrand.setBrand(brand);
        valueEntityBrand = attrValueEntityBrandDao.create((AttrValueEntityBrand) valueEntityBrand);
        attrValueDTO.setAttrvalueId(valueEntityBrand.getAttrvalueId());
        return attrValueDTO;
        

    }

    /** {@inheritDoc}*/
    @Override
    public long deleteAttributeValue(final long attributeValuePk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException{
        final AttrValueEntityBrand valueEntityBrand = attrValueEntityBrandDao.findById(attributeValuePk);
        final AttributeDTO attributeDTO = dtoAttributeService.findByAttributeCode(valueEntityBrand.getAttributeCode());
        if (attributeDTO != null) {
            if (Etype.IMAGE_BUSINESS_TYPE.equals(attributeDTO.getEtype())) {
                imageService.deleteImage(valueEntityBrand.getVal(),
                        Constants.BRAND_IMAGE_REPOSITORY_URL_PATTERN, systemService.getImageRepositoryDirectory());
            } else if (Etype.FILE_BUSINESS_TYPE.equals(attributeDTO.getEtype())) {
                fileService.deleteFile(valueEntityBrand.getVal(),
                        Constants.BRAND_FILE_REPOSITORY_URL_PATTERN, systemService.getFileRepositoryDirectory());
            }
        }
        attrValueEntityBrandDao.delete(valueEntityBrand);
        return valueEntityBrand.getBrand().getBrandId();
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
    public SearchResult<BrandDTO> findBrands(final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter");
        final String textFilter = FilterSearchUtils.getStringFilter(params.get("filter"));

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final Map<String, List> currentFilter = new HashMap<>();
        if (textFilter != null) {

            SearchContext.JoinMode.OR.setMode(currentFilter);
            currentFilter.put("guid", Collections.singletonList(textFilter));
            currentFilter.put("name", Collections.singletonList(textFilter));
            currentFilter.put("description", Collections.singletonList(textFilter));

        }


        final BrandService brandService = (BrandService) service;

        final int count = brandService.findBrandCount(currentFilter);
        if (count > startIndex) {

            final List<BrandDTO> entities = new ArrayList<>();
            final List<Brand> brands = brandService.findBrands(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), currentFilter);

            fillDTOs(brands, entities);

            return new SearchResult<>(filter, entities, count);

        }
        return new SearchResult<>(filter, Collections.emptyList(), count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttrValueDTO getNewAttribute(final long entityPk) throws UnableToCreateInstanceException, UnmappedInterfaceException {
        final AttrValueBrandDTO dto = new AttrValueBrandDTOImpl();
        dto.setBrandId(entityPk);
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getImageRepositoryUrlPattern() {
        return Constants.BRAND_IMAGE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileRepositoryUrlPattern() {
        return Constants.BRAND_FILE_REPOSITORY_URL_PATTERN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSysFileRepositoryUrlPattern() {
        return Constants.BRAND_SYSFILE_REPOSITORY_URL_PATTERN;
    }

}

/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
import org.apache.commons.collections.Predicate;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.adapter.impl.EntityFactoryToBeanFactoryAdaptor;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.AttrValueEntityProduct;
import org.yes.cart.exception.ObjectNotFoundException;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnableToWrapObjectException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductTypeAttrService;
import org.yes.cart.service.dto.*;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.utils.impl.AttrValueDTOComparatorImpl;

import java.util.*;

/**
 * Default implementation of {@link DtoProductService}. Uses
 * {@link org.yes.cart.service.domain.ProductService} to retrieve data and
 * {@link com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler} to perform deep
 * conversion of domain objects into DTO.
 * <p/>
 * User: dogma
 * Date: Jan 24, 2011
 * Time: 12:33:31 PM
 */
public class DtoProductServiceImpl
        extends AbstractDtoServiceImpl<ProductDTO, ProductDTOImpl, Product>
        implements DtoProductService {

    private final ProductService productService;
    private final DtoFactory dtoFactory;
    private final GenericService<Seo> seoGenericService;

    private final DtoAttributeService dtoAttributeService;
    private final DtoProductCategoryService dtoProductCategoryService;
    private final DtoProductSkuService dtoProductSkuService;
    private final GenericService<Attribute> attributeService;

    private final GenericDAO<AttrValueEntityProduct, Long> attrValueEntityProductDao;

    private final ProductTypeAttrService productTypeAttrService;
    private final DtoProductTypeAttrService dtoProductTypeAttrService;


    private final Assembler productSkuDTOAssembler;
    private final Assembler attrValueAssembler;
    private final ImageService imageService;

    private final LanguageService languageService;


    /**
     * IoC constructor.
     *
     * @param productService     domain objects product service
     * @param dtoFactory         factory for creating DTO object instances
     * @param adaptersRepository value converter repository
     * @param imageService       {@link ImageService} to manipulate  related images.
     */
    public DtoProductServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<Product> productService,
            final AdaptersRepository adaptersRepository,
            final GenericService<Seo> seoGenericService,
            final DtoAttributeService dtoAttributeService,
            final GenericDAO<AttrValueEntityProduct, Long> attrValueEntityProductDao,
            final ImageService imageService,
            final DtoProductTypeAttrService dtoProductTypeAttrService,
            final DtoProductCategoryService dtoProductCategoryService,
            final DtoProductSkuService dtoProductSkuService,
            final LanguageService languageService) {
        super(dtoFactory, productService, adaptersRepository);


        this.dtoProductSkuService = dtoProductSkuService;


        this.imageService = imageService;


        this.productService = (ProductService) productService;
        this.dtoFactory = dtoFactory;
        this.dtoProductCategoryService = dtoProductCategoryService;
/*
        this.AdaptersRepository = AdaptersRepository.getByKeysAsMap(
                "bigDecimalToFloat",
                "brandDTO2Brand",
                "availabilityDto2Availability",
                "productTypeDTO2ProductType");
*/
        this.seoGenericService = seoGenericService;
        this.dtoAttributeService = dtoAttributeService;


        this.attrValueEntityProductDao = attrValueEntityProductDao;
        this.attributeService = dtoAttributeService.getService();

        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueProductDTO.class),
                attributeService.getGenericDao().getEntityFactory().getImplClass(AttrValueProduct.class)
        );
        this.productSkuDTOAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(ProductSkuDTO.class),
                ProductSku.class);

        this.dtoProductTypeAttrService = dtoProductTypeAttrService;

        this.productTypeAttrService = (ProductTypeAttrService) dtoProductTypeAttrService.getService();

        this.languageService = languageService;

    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public ProductSkuDTO getProductSkuByCode(final String skuCode)
            throws ObjectNotFoundException, UnableToWrapObjectException {

        final ProductSku domainSku = productService.getProductSkuByCode(skuCode);
        if (domainSku == null) {
            return null;
        }
        try {
            final ProductSkuDTO dtoSku = dtoFactory.getByIface(ProductSkuDTO.class);
            productSkuDTOAssembler.assembleDto(dtoSku, domainSku, getAdaptersRepository(), dtoFactory);
            return dtoSku;
        } catch (Exception exp) {
            throw new UnableToWrapObjectException(ProductSku.class, ProductSkuDTO.class, exp);
        }
    }


    /**
     * {@inheritDoc}
     * Default product sku will be also created.
     */
    public ProductDTO create(final ProductDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Product product = getEntityFactory().getByIface(Product.class);
        assembler.assembleEntity(instance, product, getAdaptersRepository(),
                new EntityFactoryToBeanFactoryAdaptor(productService.getGenericDao().getEntityFactory()));
        product = service.create(product);
        return getById(product.getProductId());
    }

    /**
     * {@inheritDoc}
     */
    public ProductDTO update(final ProductDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Product product = service.findById(instance.getProductId());
        assembler.assembleEntity(
                instance,
                product,
                getAdaptersRepository(),
                new EntityFactoryToBeanFactoryAdaptor(service.getGenericDao().getEntityFactory()));
        product = service.update(product);
        return getById(product.getProductId());

    }


    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<ProductDTO> getDtoIFace() {
        return ProductDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<ProductDTOImpl> getDtoImpl() {
        return ProductDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<Product> getEntityIFace() {
        return Product.class;
    }


    /**
     * Get products, that assigned to given category id.
     *
     * @param categoryId given category id
     * @return List of assined product DTOs
     */
    public List<ProductDTO> getProductByCategory(final long categoryId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Product> products = ((ProductService) service).getProductByCategory(categoryId);
        final List<ProductDTO> dtos = new ArrayList<ProductDTO>(products.size());
        fillDTOs(products, dtos);
        return dtos;
    }


    /**
     * Get the all products in category without product availability dependancy
     *
     * @param categoryId  category id
     * @param firtsResult index of first result
     * @param maxResults  quantity results to return
     * @return list of products
     * @throws UnableToCreateInstanceException
     *                                    in case of reflection problem
     * @throws UnmappedInterfaceException in case of configuration problem
     */
    public List<ProductDTO> getProductByCategoryWithPaging(
            final long categoryId,
            final int firtsResult,
            final int maxResults) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Product> products = ((ProductService) service).getProductByCategory(categoryId, firtsResult, maxResults);
        final List<ProductDTO> dtos = new ArrayList<ProductDTO>(products.size());
        fillDTOs(products, dtos);
        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductDTO> getProductByCodeNameBrandType(
            final String code,
            final String name,
            final long brandId,
            final long productTypeId) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        Long brand = null;
        Long productType = null;
        if (brandId > 0) {
            brand = brandId;
        }
        if (productTypeId > 0) {
            productType = productTypeId;
        }
        final List<Product> products = ((ProductService) service).getProductByCodeNameBrandType(
                DEFAULT_SEARCH_CRITERIA_TUNER, code, name, brand, productType);

        final List<ProductDTO> dtos = new ArrayList<ProductDTO>(products.size());
        fillDTOs(products, dtos);
        return dtos;

    }


    /**
     * Default criteria tuner to serrch products
     */
    public static CriteriaTuner DEFAULT_SEARCH_CRITERIA_TUNER = new CriteriaTuner() {

        /** {@inheritDoc} */
        public void tune(final Criteria crit) {
            crit.setFetchMode("productCategory", FetchMode.SELECT);
        }

    };

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final ProductDTO productDTO = getById(entityPk);
        final List<AttrValueProductDTO> productAttrs = new ArrayList<AttrValueProductDTO>();
        productAttrs.addAll(productDTO.getAttributes());


        final List<AttributeDTO> ptList = dtoAttributeService.findAvailableAttributesByProductTypeId(
                productDTO.getProductTypeDTO().getProducttypeId()
        );

        final List<AttributeDTO> images = dtoAttributeService.findAvailableImageAttributesByGroupCode(
                AttributeGroupNames.PRODUCT
        );

        ptList.addAll(images);

        final List<AttrValueProductDTO> full = new ArrayList<AttrValueProductDTO>(ptList.size());
        for (int i = 0; i < ptList.size(); i++) {
            final AttributeDTO available = ptList.get(i);

            final Iterator<AttrValueProductDTO> valuesIt = productAttrs.iterator();
            boolean found = false;
            while (valuesIt.hasNext()) {
                final AttrValueProductDTO value = valuesIt.next();
                if (available.getCode().equals(value.getAttributeDTO().getCode())) {
                    full.add(value);
                    valuesIt.remove(); // remove from results
                    found = true;
                    break;
                }
            }

            if (!found) {
                AttrValueProductDTO attrValueDTO = getDtoFactory().getByIface(AttrValueProductDTO.class);
                attrValueDTO.setAttributeDTO(available);
                attrValueDTO.setProductId(entityPk);
                full.add(attrValueDTO);
            }
        }

        full.addAll(productAttrs); // add all the rest (probably not part of this product type)

        CollectionUtils.filter(
                full,
                new Predicate() {
                    public boolean evaluate(final Object object) {
                        return ((AttrValueDTO) object).getAttributeDTO() != null;
                    }
                }
        );

        Collections.sort(productAttrs, new AttrValueDTOComparatorImpl());
        return full;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final AttrValueEntityProduct attrValue = attrValueEntityProductDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, attrValue, getAdaptersRepository(), dtoFactory);
        attrValueEntityProductDao.update(attrValue);
        return attrValueDTO;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        AttrValueProduct valueEntity = getEntityFactory().getByIface(AttrValueProduct.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntity, getAdaptersRepository(), dtoFactory);
        Attribute atr = attributeService.findById(attrValueDTO.getAttributeDTO().getAttributeId());
        valueEntity.setAttribute(atr);
        valueEntity.setProduct(service.findById(((AttrValueProductDTO) attrValueDTO).getProductId()));
        valueEntity = attrValueEntityProductDao.create((AttrValueEntityProduct) valueEntity);
        attrValueDTO.setAttrvalueId(valueEntity.getAttrvalueId());
        return attrValueDTO;

    }


    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(final long entityPk, final String attrName, final String attrValue)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, String> displayNames = new TreeMap<String, String>();
        for (String lang : languageService.getSupportedLanguages()) {
            displayNames.put(lang, attrName);
        }


        AttributeDTO attrDto = dtoFactory.getByIface(AttributeDTO.class);
        attrDto.setName(attrName);
        attrDto.setCode(attrName.replaceAll(" ", "-"));
        attrDto.setDisplayNames(displayNames);
        attrDto.setAllowfailover(true);
        attrDto.setAttributegroupId(1003); //TODO fix me not constant but "PRODUCT" code
        attrDto.setEtypeId(1000); //and me

        attrDto = dtoAttributeService.create(attrDto);


        ProductType productType = productService.findById(entityPk).getProducttype();
        ProductTypeAttrDTO productTypeAttrDTO = dtoFactory.getByIface(ProductTypeAttrDTO.class);
        productTypeAttrDTO.setAttributeDTO(attrDto);
        productTypeAttrDTO.setProducttypeId(productType.getProducttypeId());
        productTypeAttrDTO.setVisible(true);
        productTypeAttrDTO.setNavigationType("S"); // TODO v2 "R" "S" Navigation type define as constant
        productTypeAttrDTO = dtoProductTypeAttrService.create(productTypeAttrDTO);


        AttrValueProductDTO attrValueDTO = getDtoFactory().getByIface(AttrValueProductDTO.class);
        attrValueDTO.setAttributeDTO(attrDto);
        attrValueDTO.setProductId(entityPk);
        attrValueDTO.setVal(attrValue);

        return createEntityAttributeValue(attrValueDTO);


    }


    /**
     * {@inheritDoc}
     */
    public void remove(long id) {
        dtoProductCategoryService.removeByProductIds(id);
        dtoProductSkuService.removeAllInventory(id);
        dtoProductSkuService.removeAllPrices(id);
        final Object obj = getService().findById(id);
        getService().getGenericDao().evict(obj);
        super.remove(id);
    }


    /**
     * {@inheritDoc}
     */
    public long deleteAttributeValue(final long attributeValuePk) {
        final AttrValueEntityProduct attrValue = attrValueEntityProductDao.findById(attributeValuePk);
        if (Etype.IMAGE_BUSINESS_TYPE.equals(attrValue.getAttribute().getEtype().getBusinesstype())) {
            imageService.deleteImage(attrValue.getVal());
        }
        attrValueEntityProductDao.delete(attrValue);
        return attrValue.getProduct().getProductId();
    }
}

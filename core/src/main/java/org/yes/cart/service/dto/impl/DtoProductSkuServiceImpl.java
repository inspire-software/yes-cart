package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.adapter.repository.ValueConverterRepository;
import dp.lib.dto.geda.assembler.DTOAssembler;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.adapter.impl.EntityFactoryToBeanFactoryAdaptor;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductSkuDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.AttrValueEntityProductSku;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoProductSkuService;
import org.yes.cart.utils.impl.AttrValueDTOComparatorImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductSkuServiceImpl
        extends AbstractDtoServiceImpl<ProductSkuDTO, ProductSkuDTOImpl, ProductSku>
        implements DtoProductSkuService {



    private final DtoAttributeService dtoAttributeService;
    private final GenericDAO<AttrValueEntityProductSku, Long> attrValueEntityProductSkuDao;
    private final DTOAssembler attrValueAssembler;
    private final DTOAssembler skuPriceAssembler;
    private final GenericService<Attribute> attributeService;
    private final PriceService priceService;
    private final GenericService<Seo> seoGenericService;
    private final ImageService imageService;


    /**
     * {@inheritDoc}
     */
    public List<ProductSkuDTO> getAllProductSkus(final long productId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final Collection<ProductSku> skus = ((ProductSkuService) getService()).getAllProductSkus(productId);
        final List<ProductSkuDTO> result = new ArrayList<ProductSkuDTO>(skus.size());
        fillDTOs(skus, result);
        return result;
    }

    /**
     * Construct product sku dto service.
     *
     * @param dtoFactory                   dto factory
     * @param productSkuGenericService     generic product service
     * @param dtoAttributeService          attr service to determinate allowed duplicates for attribute values.
     * @param attrValueEntityProductSkuDao sku attributes dao
     * @param valueConverterRepository     value converter
     * @param imageService {@link ImageService} to manipulate  related images. 
     */
    public DtoProductSkuServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<ProductSku> productSkuGenericService,
            final DtoAttributeService dtoAttributeService,
            final GenericDAO<AttrValueEntityProductSku, Long> attrValueEntityProductSkuDao,
            final ValueConverterRepository valueConverterRepository,
            final PriceService priceService,
            final GenericService<Seo> seoGenericService,
            final ImageService imageService) {
        super(dtoFactory, productSkuGenericService, valueConverterRepository);

        this.imageService = imageService;

        this.dtoAttributeService = dtoAttributeService;
        this.attrValueEntityProductSkuDao = attrValueEntityProductSkuDao;
        this.attributeService = dtoAttributeService.getService();
        this.priceService = priceService;
        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueProductSkuDTO.class),
                productSkuGenericService.getGenericDao().getEntityFactory().getImplClass(AttrValueProductSku.class)
        );
        this.skuPriceAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(SkuPriceDTO.class),
                productSkuGenericService.getGenericDao().getEntityFactory().getImplClass(SkuPrice.class)
        );
        this.seoGenericService = seoGenericService;
    }

    /**
     * {@inheritDoc}
     */
    public ProductSkuDTO create(final ProductSkuDTO instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductSku productSku = getEntityFactory().getByIface(ProductSku.class);
        assembler.assembleEntity(
                instance,
                productSku,
                getValueConverterRepository(),
                new EntityFactoryToBeanFactoryAdaptor(service.getGenericDao().getEntityFactory()));
        bindDictionaryData(instance, productSku);
        productSku = service.create(productSku);
        return getById(productSku.getSkuId(), getValueConverterRepository());
    }


    /**
     * {@inheritDoc}
     */
    public long updateSkuPrice(final SkuPriceDTO skuPriceDTO) {
        final SkuPrice skuPrice = priceService.getById(skuPriceDTO.getSkuPriceId());
        skuPriceAssembler.assembleEntity(skuPriceDTO, skuPrice,
                getValueConverterRepository(),
                new EntityFactoryToBeanFactoryAdaptor(service.getGenericDao().getEntityFactory()));
        priceService.update(skuPrice);
        return skuPrice.getSkuPriceId();
    }



    /**
     * {@inheritDoc}
     */
    public void removeSkuPrice(final long skuPriceId) {
        priceService.delete(
                priceService.getById(skuPriceId)
        );
    }

    /**
     * {@inheritDoc}
     */
    public long createSkuPrice(final SkuPriceDTO skuPriceDTO) {
        SkuPrice skuPrice = getService().getGenericDao().getEntityFactory().getByIface(SkuPrice.class);
        skuPriceAssembler.assembleEntity(skuPriceDTO, skuPrice,
                getValueConverterRepository(),
                new EntityFactoryToBeanFactoryAdaptor(service.getGenericDao().getEntityFactory()));
        skuPrice = priceService.create(skuPrice);
        return skuPrice.getSkuPriceId();
    }


    /**
     * {@inheritDoc}
     */
    public ProductSkuDTO update(final ProductSkuDTO instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductSku productSku = service.getById(instance.getSkuId());
        assembler.assembleEntity(
                instance,
                productSku,
                getValueConverterRepository(),
                new EntityFactoryToBeanFactoryAdaptor(service.getGenericDao().getEntityFactory()));
        bindDictionaryData(instance, productSku);
        productSku = service.update(productSku);
        return getById(productSku.getSkuId(), getValueConverterRepository());
    }

    private void bindDictionaryData(final ProductSkuDTO instance, final ProductSku productSku) {
        if (instance.getSeoDTO() != null && instance.getSeoDTO().getSeoId() > 0) {
            productSku.setSeo(seoGenericService.getById(instance.getSeoDTO().getSeoId()));
        } else {
            productSku.setSeo(null);
        }
    }


    /**
     * {@inheritDoc}
     */
    /*@Override
    public ProductSkuDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return super.getById(id, valueConverterRepository);
    } */

    /**
     * {@inheritDoc}
     */
    public Class<ProductSkuDTO> getDtoIFace() {
        return ProductSkuDTO.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<ProductSkuDTOImpl> getDtoImpl() {
        return ProductSkuDTOImpl.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<ProductSku> getEntityIFace() {
        return ProductSku.class;
    }


    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<AttrValueProductSkuDTO> result = new ArrayList<AttrValueProductSkuDTO>();
        result.addAll(getById(entityPk).getAttribute());
        final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                AttributeGroupNames.SKU,
                getCodes(result));
        for (AttributeDTO attributeDTO : availableAttributeDTOs) {
            AttrValueProductSkuDTO attrValueDTO = getDtoFactory().getByIface(AttrValueProductSkuDTO.class);
            attrValueDTO.setAttributeDTO(attributeDTO);
            attrValueDTO.setSkuId(entityPk);
            result.add(attrValueDTO);
        }
        Collections.sort(result, new AttrValueDTOComparatorImpl());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final AttrValueEntityProductSku attrValue = attrValueEntityProductSkuDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, attrValue, null, dtoFactory);
        attrValueEntityProductSkuDao.update(attrValue);
        return attrValueDTO;

    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        AttrValueProductSku valueEntity = getEntityFactory().getByIface(AttrValueProductSku.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntity, null, dtoFactory);
        Attribute atr = attributeService.getById(attrValueDTO.getAttributeDTO().getAttributeId());
        valueEntity.setAttribute(atr);
        valueEntity.setProductSku(service.getById(((AttrValueProductSkuDTO) attrValueDTO).getSkuId()));
        valueEntity = attrValueEntityProductSkuDao.create((AttrValueEntityProductSku) valueEntity);
        attrValueDTO.setAttrvalueId(valueEntity.getAttrvalueId());
        return attrValueDTO;

    }

    /**
     * {@inheritDoc}
     */
    public void deleteAttributeValue(final long attributeValuePk) {
        final AttrValueEntityProductSku attrValue = attrValueEntityProductSkuDao.findById(attributeValuePk);
        if (Etype.IMAGE_BUSINESS_TYPE.equals(attrValue.getAttribute().getEtype().getBusinesstype())) {
            imageService.deleteImage(attrValue.getVal());
        }
        attrValueEntityProductSkuDao.delete(attrValue);
    }
}

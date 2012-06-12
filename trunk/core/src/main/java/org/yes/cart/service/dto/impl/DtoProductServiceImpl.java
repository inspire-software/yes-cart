package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.adapter.repository.ValueConverterRepository;
import dp.lib.dto.geda.assembler.DTOAssembler;
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
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoProductService;
import org.yes.cart.utils.impl.AttrValueDTOComparatorImpl;

import java.util.*;

/**
 * Default implementation of {@link DtoProductService}. Uses
 * {@link org.yes.cart.service.domain.ProductService} to retrieve data and
 * {@link dp.lib.dto.geda.assembler.DTOAssembler} to perform deep
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
    private final Map<String, Object> valueConverterRepository;
    private final GenericService<Seo> seoGenericService;

    private final DtoAttributeService dtoAttributeService;
    private final GenericService<Attribute> attributeService;

    private final GenericDAO<AttrValueEntityProduct, Long> attrValueEntityProductDao;

    private final ProductTypeAttrService productTypeAttrService;


    private final DTOAssembler productSkuDTOAssembler;
    private final DTOAssembler attrValueAssembler;
    private final ImageService imageService;


    /**
     * IoC constructor.
     *
     * @param productService           domain objects product service
     * @param dtoFactory               factory for creating DTO object instances
     * @param valueConverterRepository value converter repository
     * @param imageService             {@link ImageService} to manipulate  related images.
     */
    public DtoProductServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<Product> productService,
            final ValueConverterRepository valueConverterRepository,
            final GenericService<Seo> seoGenericService,
            final DtoAttributeService dtoAttributeService,
            final GenericDAO<AttrValueEntityProduct, Long> attrValueEntityProductDao,
            final ImageService imageService,
            final ProductTypeAttrService productTypeAttrService
    ) {
        super(dtoFactory, productService, null);


        this.imageService = imageService;


        this.productService = (ProductService) productService;
        this.dtoFactory = dtoFactory;
        this.valueConverterRepository = valueConverterRepository.getAll();
/*
        this.valueConverterRepository = valueConverterRepository.getByKeysAsMap(
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

        this.productTypeAttrService = productTypeAttrService;

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
            productSkuDTOAssembler.assembleDto(dtoSku, domainSku, valueConverterRepository, dtoFactory);
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
        assembler.assembleEntity(instance, product, valueConverterRepository,
                new EntityFactoryToBeanFactoryAdaptor(productService.getGenericDao().getEntityFactory()));
        bindDictionaryData(instance, product);
        product = service.create(product);
        return getById(product.getProductId());
    }

    /**
     * {@inheritDoc}
     */
    public ProductDTO update(final ProductDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Product product = service.getById(instance.getProductId());
        assembler.assembleEntity(
                instance,
                product,
                valueConverterRepository,
                new EntityFactoryToBeanFactoryAdaptor(service.getGenericDao().getEntityFactory()));
        bindDictionaryData(instance, product);
        product = service.update(product);
        return getById(product.getProductId());

    }

    private void bindDictionaryData(final ProductDTO instance, final Product product) {
        if (instance.getSeoDTO() != null && instance.getSeoDTO().getSeoId() > 0) {
            product.setSeo(seoGenericService.getById(instance.getSeoDTO().getSeoId()));
        } else {
            product.setSeo(null);
        }
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
    public List<ProductDTO> getProductByConeNameBrandType(
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
        final List<Product> products = ((ProductService) service).getProductByConeNameBrandType(
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
        final List<AttrValueProductDTO> result = new ArrayList<AttrValueProductDTO>();
        final ProductDTO productDTO = getById(entityPk);
        result.addAll(productDTO.getAttribute());


        final List<ProductTypeAttr> ptaList = productTypeAttrService.getByProductTypeId(
                productDTO.getProductTypeDTO().getProducttypeId());

        Collections.sort(
                ptaList,
                new Comparator<ProductTypeAttr>() {
                    public int compare(ProductTypeAttr o1, ProductTypeAttr o2) {
                        return o1.getAttribute().getCode().compareTo(o2.getAttribute().getCode());
                    }
                }
        );

        final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                AttributeGroupNames.PRODUCT,
                getCodes(result));


        //Add which belong to product type only.
        for (AttributeDTO attributeDTO : availableAttributeDTOs) {
            if (isBelongToProductType(attributeDTO, ptaList) || Etype.IMAGE_BUSINESS_TYPE.equals(attributeDTO.getEtypeName())) {
                AttrValueProductDTO attrValueDTO = getDtoFactory().getByIface(AttrValueProductDTO.class);
                attrValueDTO.setAttributeDTO(attributeDTO);
                attrValueDTO.setProductId(entityPk);
                result.add(attrValueDTO);
            }
        }

        Collections.sort(result, new AttrValueDTOComparatorImpl());
        return result;
    }

    /**
     * Check is given attribute belong to particular product type.
     *
     * @param attributeDTO {@link AttributeDTO}
     * @param ptaList      list of attributes for particular product type
     * @return true in case if given attrtibute belong to product type.
     */
    public boolean isBelongToProductType(AttributeDTO attributeDTO, List<ProductTypeAttr> ptaList) {
        /* for (ProductTypeAttr pta : ptaList ) {
           if (pta.getAttribute().getCode().equals(attributeDTO.getCode())) {
               return true;
           }
       }
       return false;*/




        final int idx = Collections.binarySearch (
                ptaList,
                attributeDTO.getCode(),
                new Comparator<Object>() {
                    public int compare(final Object o1, final Object o2) {
                        if (o1 instanceof ProductTypeAttr && o2 instanceof String) {
                            return ((ProductTypeAttr) o1).getAttribute().getCode().compareTo((String) o2);
                        }
                        if (o2 instanceof ProductTypeAttr && o1 instanceof String) {
                            return ((ProductTypeAttr) o2).getAttribute().getCode().compareTo((String) o1);
                        }


                        return 0;
                    }
                }
        );

        return idx > 0;
    }


    /**
     * {@inheritDoc}
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final AttrValueEntityProduct attrValue = attrValueEntityProductDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, attrValue, null, dtoFactory);
        attrValueEntityProductDao.update(attrValue);
        return attrValueDTO;
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        AttrValueProduct valueEntity = getEntityFactory().getByIface(AttrValueProduct.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntity, null, dtoFactory);
        Attribute atr = attributeService.getById(attrValueDTO.getAttributeDTO().getAttributeId());
        valueEntity.setAttribute(atr);
        valueEntity.setProduct(service.getById(((AttrValueProductDTO) attrValueDTO).getProductId()));
        valueEntity = attrValueEntityProductDao.create((AttrValueEntityProduct) valueEntity);
        attrValueDTO.setAttrvalueId(valueEntity.getAttrvalueId());
        return attrValueDTO;

    }

    /**
     * {@inheritDoc}
     */
    public void deleteAttributeValue(final long attributeValuePk) {
        final AttrValueEntityProduct attrValue = attrValueEntityProductDao.findById(attributeValuePk);
        if (Etype.IMAGE_BUSINESS_TYPE.equals(attrValue.getAttribute().getEtype().getBusinesstype())) {
            imageService.deleteImage(attrValue.getVal());
        }
        attrValueEntityProductDao.delete(attrValue);
    }
}

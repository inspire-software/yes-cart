package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.adapter.repository.ValueConverterRepository;
import org.yes.cart.domain.dto.ProductTypeAttrDTO;
import org.yes.cart.domain.dto.adapter.impl.EntityFactoryToBeanFactoryAdaptor;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductTypeAttrDTOImpl;
import org.yes.cart.domain.entity.ProductTypeAttr;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ProductTypeAttrService;
import org.yes.cart.service.dto.DtoProductTypeAttrService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductTypeAttrServiceImpl
        extends AbstractDtoServiceImpl<ProductTypeAttrDTO, ProductTypeAttrDTOImpl, ProductTypeAttr>
        implements DtoProductTypeAttrService {
    /**
     * Construct base remote service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param productTypeAttrGenericService                  {@link org.yes.cart.service.domain.GenericService}
     * @param valueConverterRepository {@link dp.lib.dto.geda.adapter.repository.ValueConverterRepository}
     */
    public DtoProductTypeAttrServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<ProductTypeAttr> productTypeAttrGenericService,
            final ValueConverterRepository valueConverterRepository) {
        super(dtoFactory, productTypeAttrGenericService, valueConverterRepository);
    }


    /**
     * {@inheritDoc}
     */
    public ProductTypeAttrDTO create(final ProductTypeAttrDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductTypeAttr productTypeAttr = getEntityFactory().getByIface(ProductTypeAttr.class);
        assembler.assembleEntity(instance, productTypeAttr, getValueConverterRepository(),
                new EntityFactoryToBeanFactoryAdaptor(getEntityFactory()));
        productTypeAttr = service.create(productTypeAttr);
        return getById(productTypeAttr.getProductTypeAttrId());
    }

    /**
     * {@inheritDoc}
     */
    public ProductTypeAttrDTO update(final ProductTypeAttrDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductTypeAttr productTypeAttr = service.getById(instance.getProductTypeAttrId());
        assembler.assembleEntity(instance, productTypeAttr, getValueConverterRepository(),
                new EntityFactoryToBeanFactoryAdaptor(getEntityFactory()));
        productTypeAttr = service.update(productTypeAttr);
        return getById(productTypeAttr.getProductTypeAttrId());

    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<ProductTypeAttrDTO> getDtoIFace() {
        return ProductTypeAttrDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<ProductTypeAttrDTOImpl> getDtoImpl() {
        return ProductTypeAttrDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<ProductTypeAttr> getEntityIFace() {
        return ProductTypeAttr.class;
    }

    public List<ProductTypeAttrDTO> getByProductTypeId(final long productTypeId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<ProductTypeAttr> list = ((ProductTypeAttrService)service).getByProductTypeId(productTypeId);
        final List<ProductTypeAttrDTO> result = new ArrayList<ProductTypeAttrDTO>(list.size());
        fillDTOs(list, result);
        return result;
    }
}

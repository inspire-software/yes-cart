package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.adapter.repository.ValueConverterRepository;
import org.yes.cart.domain.dto.ProductAssociationDTO;
import org.yes.cart.domain.dto.adapter.impl.EntityFactoryToBeanFactoryAdaptor;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductAssociationDTOImpl;
import org.yes.cart.domain.entity.ProductAssociation;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ProductAssociationService;
import org.yes.cart.service.dto.DtoProductAssociationService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductAssociationServiceImpl
        extends AbstractDtoServiceImpl<ProductAssociationDTO, ProductAssociationDTOImpl, ProductAssociation>
        implements DtoProductAssociationService {

    private final ProductAssociationService productAssociationService;

    /**
     * Construct base remote service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param productAssociationGenericService                  {@link org.yes.cart.service.domain.GenericService}
     * @param valueConverterRepository {@link dp.lib.dto.geda.adapter.repository.ValueConverterRepository}
     */
    public DtoProductAssociationServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<ProductAssociation> productAssociationGenericService,
            final ValueConverterRepository valueConverterRepository) {
        super(dtoFactory, productAssociationGenericService, valueConverterRepository);
        productAssociationService = (ProductAssociationService) productAssociationGenericService;
    }

    /**
     * {@inheritDoc}
     */
    public ProductAssociationDTO create(final ProductAssociationDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductAssociation productAssociation = getEntityFactory().getByIface(ProductAssociation.class);
        assembler.assembleEntity(instance, productAssociation,  getValueConverterRepository(), new EntityFactoryToBeanFactoryAdaptor(getEntityFactory()));
        productAssociation = getService().create(productAssociation);
        return getById(productAssociation.getProductassociationId());
    }

    /**
     * {@inheritDoc}
     */
    public ProductAssociationDTO update(final ProductAssociationDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductAssociation productAssociation = service.getById(instance.getProductassociationId());
        assembler.assembleEntity(instance, productAssociation,  getValueConverterRepository(), new EntityFactoryToBeanFactoryAdaptor(getEntityFactory()));
        productAssociation = getService().update(productAssociation);
        return getById(productAssociation.getProductassociationId());

    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<ProductAssociationDTO> getDtoIFace() {
        return ProductAssociationDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<ProductAssociationDTOImpl> getDtoImpl() {
        return ProductAssociationDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<ProductAssociation> getEntityIFace() {
        return ProductAssociation.class;
    }

    /**
     * Get all product associations.
     *
     * @param productId product primary key
     * @return list of product assotiations
     */
    public List<ProductAssociationDTO> getProductAssociations(final long productId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        List<ProductAssociation> list = productAssociationService.getProductAssociations(productId);
        List<ProductAssociationDTO> result = new ArrayList<ProductAssociationDTO>(list.size());
        fillDTOs(list, result);
        return result;
    }

    /**
     * Get all product associations by association type.
     *
     * @param productId       product primary key
     * @param accosiationCode accosiation code [up, cross, etc]
     * @return list of product assotiations
     */
    public List<ProductAssociationDTO> getProductAssociationsByProductAssociationType(final long productId, final String accosiationCode) 
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        List<ProductAssociation> list = productAssociationService.getProductAssociations(productId, accosiationCode);
        List<ProductAssociationDTO> result = new ArrayList<ProductAssociationDTO>(list.size());
        fillDTOs(list, result);
        return result;

    }
}

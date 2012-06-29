package org.yes.cart.service.dto.impl;

import dp.lib.dto.geda.adapter.repository.ValueConverterRepository;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.domain.dto.ProdTypeAttributeViewGroupDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProdTypeAttributeViewGroupDTOImpl;
import org.yes.cart.domain.entity.ProdTypeAttributeViewGroup;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoProdTypeAttributeViewGroupService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/28/12
 * Time: 10:31 PM
 */
public class DtoProdTypeAttributeViewGroupServiceImpl
        extends AbstractDtoServiceImpl<ProdTypeAttributeViewGroupDTO, ProdTypeAttributeViewGroupDTOImpl, ProdTypeAttributeViewGroup>
        implements DtoProdTypeAttributeViewGroupService {
    
    private final GenericService<ProductType> productTypeService;


    /**
     * Construct base remote service.
     *
     * @param dtoFactory               {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param prodTypeAttributeViewGroupGenericService                  {@link org.yes.cart.service.domain.GenericService}
     * @param valueConverterRepository {@link dp.lib.dto.geda.adapter.repository.ValueConverterRepository}
     */
    public DtoProdTypeAttributeViewGroupServiceImpl(final DtoFactory dtoFactory,
                                                    final GenericService<ProdTypeAttributeViewGroup> prodTypeAttributeViewGroupGenericService,
                                                    final ValueConverterRepository valueConverterRepository,
                                                    final GenericService<ProductType> productTypeService) {
        super(dtoFactory, prodTypeAttributeViewGroupGenericService, valueConverterRepository);
        this.productTypeService = productTypeService;
    }

    /** {@inheritDoc} */
    public List<ProdTypeAttributeViewGroupDTO> getByProductTypeId(final long productTypeId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        //final List<ProdTypeAttributeViewGroup> list = getService().findByCriteria(Restrictions.eq("producttype.producttypeId", productTypeId)); hibernate plus derby fails
        ProductType prodType = productTypeService.getById(productTypeId);
        final List<ProdTypeAttributeViewGroupDTO> rez = new ArrayList<ProdTypeAttributeViewGroupDTO>(prodType.getAttributeViewGroup().size());
        fillDTOs(prodType.getAttributeViewGroup(), rez);
        return rez;


    }

    /** {@inheritDoc} */
    @Override
    public Class<ProdTypeAttributeViewGroupDTO> getDtoIFace() {
        return ProdTypeAttributeViewGroupDTO.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<ProdTypeAttributeViewGroupDTOImpl> getDtoImpl() {
        return ProdTypeAttributeViewGroupDTOImpl.class;
    }

    /** {@inheritDoc} */
    @Override
    public Class<ProdTypeAttributeViewGroup> getEntityIFace() {
        return ProdTypeAttributeViewGroup.class;
    }
}

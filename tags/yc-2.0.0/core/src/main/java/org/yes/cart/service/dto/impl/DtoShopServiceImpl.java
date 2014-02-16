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
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttrValueShopDTO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ShopDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.AttrValueEntityShop;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoShopService;
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
public class DtoShopServiceImpl
        extends AbstractDtoServiceImpl<ShopDTO, ShopDTOImpl, Shop>
        implements DtoShopService {

    private final CustomerService customerService;

    private final Assembler attrValueAssembler;
    private final DtoAttributeService dtoAttributeService;
    private final GenericDAO<AttrValueEntityShop, Long> attrValueEntityShopDao;
    private final ImageService imageService;



    public DtoShopServiceImpl(
            final ShopService shopService,
            final CustomerService customerService,
            final DtoFactory dtoFactory,
            final DtoAttributeService dtoAttributeService,
            final GenericDAO<AttrValueEntityShop, Long> attrValueEntityShopDao,
            final ImageService imageService,
            final AdaptersRepository adaptersRepository) {
        super(dtoFactory, shopService, adaptersRepository);

        this.customerService = customerService;

        this.attrValueAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(AttrValueShopDTO.class),
                shopService.getGenericDao().getEntityFactory().getImplClass(AttrValueShop.class)
        );
        this.dtoAttributeService = dtoAttributeService;
        this.attrValueEntityShopDao = attrValueEntityShopDao;
        this.imageService = imageService;


    }


    /** {@inheritDoc} */
    public String getSupportedCurrencies(final long shopId) {
        return service.findById(shopId).getSupportedCurrencies();
    }

    /** {@inheritDoc} */
    public Collection<String> getAllSupportedCurrenciesByShops() {
        return ((ShopService)service).findAllSupportedCurrenciesByShops();
    }

    /**
     * Set supported currencies by given shop.
     * @param shopId shop id
     * @param currencies comma separated list of supported currency codes. Example USD,EUR
     */
    public void updateSupportedCurrencies(final long shopId, final String currencies) {
        ((ShopService) service).updateAttributeValue(
                shopId,
                AttributeNamesKeys.SUPPORTED_CURRENCIES,
                currencies);
    }

    /** {@inheritDoc} */
    public ShopDTO getShopDtoByDomainName(final String serverDomainName) {
        final Shop shop =((ShopService)service).getShopByDomainName(serverDomainName);
        final ShopDTO dto = (ShopDTO) dtoFactory.getByIface(getDtoIFace());
        getAssembler().assembleDto(dto, shop, getAdaptersRepository(), getDtoFactory());
        return dto;
    }

    /** {@inheritDoc} */
    public List<ShopDTO> getAssignedShop(final long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        List<Shop> rez = new ArrayList<Shop>();
        for (CustomerShop customerShop : customerService.findById(customerId).getShops()) {
            rez.add(customerShop.getShop());
        }
        return getDTOs(rez);

    }

    /** {@inheritDoc} */
    public Class<ShopDTO> getDtoIFace() {
        return ShopDTO.class;
    }

    /** {@inheritDoc} */
    public Class<ShopDTOImpl> getDtoImpl() {
        return ShopDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<Shop> getEntityIFace() {
        return Shop.class;
    }

    /** {@inheritDoc}*/
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<AttrValueShopDTO> result = new ArrayList<AttrValueShopDTO>(getById(entityPk).getAttributes());
        final List<AttributeDTO> availableAttributeDTOs = dtoAttributeService.findAvailableAttributes(
                AttributeGroupNames.SHOP,
                getCodes(result));
        for (AttributeDTO attributeDTO : availableAttributeDTOs) {
            AttrValueShopDTO attrValueShopDTO = getDtoFactory().getByIface(AttrValueShopDTO.class);
            attrValueShopDTO.setAttributeDTO(attributeDTO);
            attrValueShopDTO.setShopId(entityPk);
            result.add(attrValueShopDTO);
        }
        Collections.sort(result, new AttrValueDTOComparatorImpl());
        return result;
    }

    /** {@inheritDoc}*/
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        final AttrValueEntityShop valueEntityShop = attrValueEntityShopDao.findById(attrValueDTO.getAttrvalueId());
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityShop, getAdaptersRepository(), dtoFactory);
        attrValueEntityShopDao.update(valueEntityShop);
        return attrValueDTO;

    }

    /** {@inheritDoc}*/
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        AttrValueShop valueEntityShop = getEntityFactory().getByIface(AttrValueShop.class);
        attrValueAssembler.assembleEntity(attrValueDTO, valueEntityShop, getAdaptersRepository(), dtoFactory);
        Attribute atr = ((GenericService<Attribute>)dtoAttributeService.getService()).findById(attrValueDTO.getAttributeDTO().getAttributeId());
        valueEntityShop.setAttribute(atr);
        valueEntityShop.setShop(service.findById(((AttrValueShopDTO) attrValueDTO).getShopId()));
        valueEntityShop = attrValueEntityShopDao.create((AttrValueEntityShop) valueEntityShop);
        attrValueDTO.setAttrvalueId(valueEntityShop.getAttrvalueId());
        return attrValueDTO;


    }

    /** {@inheritDoc}*/
    public long deleteAttributeValue(final long attributeValuePk) {
        final AttrValueEntityShop valueEntityShop = attrValueEntityShopDao.findById(attributeValuePk);
        if (Etype.IMAGE_BUSINESS_TYPE.equals(valueEntityShop.getAttribute().getEtype().getBusinesstype())) {
            imageService.deleteImage(valueEntityShop.getVal());
        }
        attrValueEntityShopDao.delete(valueEntityShop);
        return valueEntityShop.getShop().getShopId();
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(final long entityPk, final String attrName, final String attrValue)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }


}

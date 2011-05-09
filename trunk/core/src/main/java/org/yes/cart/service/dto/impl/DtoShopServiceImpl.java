package org.yes.cart.service.dto.impl;

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ShopDTOImpl;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.dto.DtoShopService;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoShopServiceImpl
        extends AbstractDtoServiceImpl<ShopDTO, ShopDTOImpl, Shop>
        implements DtoShopService {


    public DtoShopServiceImpl(
            final ShopService shopService,
            final DtoFactory dtoFactory) {
        super(dtoFactory, shopService, null);

    }

    /** {@inheritDoc} */
    public ShopDTO create(ShopDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Shop shop = getEntityFactory().getByIface(Shop.class);
        assembler.assembleEntity(instance, shop,  null, dtoFactory);
        shop = service.create(shop);
        return getById(shop.getShopId());
    }

    /** {@inheritDoc} */
    public ShopDTO update(ShopDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        Shop shop = service.getById(instance.getShopId());
        assembler.assembleEntity(instance, shop,  null, dtoFactory);
        shop = service.update(shop);
        return getById(shop.getShopId());
    }

    /** {@inheritDoc} */
    public String getSupportedCurrencies(final long shopId) {
        return service.getById(shopId).getSupportedCurrensies();        
    }

    /** {@inheritDoc} */
    public Collection<String> getAllSupportedCurrenciesByShops() {
        return ((ShopService)service).getAllSupportedCurrenciesByShops();
    }

    /**
     * Set supported currencies by given shop.
     * @param shopId shop id
     * @param currensies comma separated list of supported currency codes. Example USD,EUR
     */
    public void setSupportedCurrencies(final long shopId, final String currensies) {
        ((ShopService) service).updateAttributeValue(
                shopId,
                AttributeNamesKeys.SUPPORTED_CURRENSIES,
                currensies);
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


}

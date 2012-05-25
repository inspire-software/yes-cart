package org.yes.cart.service.dto.impl;

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ShopDTOImpl;
import org.yes.cart.domain.entity.CustomerShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.dto.DtoShopService;

import java.util.ArrayList;
import java.util.Collection;
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


    public DtoShopServiceImpl(
            final ShopService shopService,
            final CustomerService customerService,
            final DtoFactory dtoFactory) {
        super(dtoFactory, shopService, null);

        this.customerService = customerService;

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
    public ShopDTO getShopDtoByDomainName(final String serverDomainName) {
        final Shop shop =((ShopService)service).getShopByDomainName(serverDomainName);
        final ShopDTO dto = (ShopDTO) dtoFactory.getByIface(getDtoIFace());
        getAssembler().assembleDto(dto, shop, getValueConverterRepository(), getDtoFactory());
        return dto;
    }

    /** {@inheritDoc} */
    public List<ShopDTO> getAssignedShop(final long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        List<Shop> rez = new ArrayList<Shop>();
        for (CustomerShop customerShop : customerService.getById(customerId).getShops()) {
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


}

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import org.yes.cart.domain.dto.ShopExchangeRateDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ShopExchangeRateDTOImpl;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopExchangeRate;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.dto.DtoShopExchangeRateService;

import java.util.ArrayList;
import java.util.List;

/**
 * Exchange rate dto service implementation to work with
 * currency exchange rates.
 * User: iazarny@yahoo.com
 * Date: 9/22/12
 * Time: 1:46 PM
 */
public class DtoShopExchangeRateServiceImpl
        extends AbstractDtoServiceImpl<ShopExchangeRateDTO, ShopExchangeRateDTOImpl, ShopExchangeRate>
        implements DtoShopExchangeRateService {

    private final GenericService<Shop> shopService;
    private final PriceService priceService;


    public DtoShopExchangeRateServiceImpl(final DtoFactory dtoFactory,
                                          final GenericService<ShopExchangeRate> service,
                                          final AdaptersRepository adaptersRepository,
                                          final GenericService<Shop> shopService,
                                          final PriceService priceService) {
        super(dtoFactory, service, adaptersRepository);
        this.shopService = shopService;
        this.priceService = priceService;
    }

    /** {@inheritDoc} */
    public Class<ShopExchangeRateDTO> getDtoIFace() {
        return ShopExchangeRateDTO.class;
    }

    /** {@inheritDoc} */
    public Class<ShopExchangeRateDTOImpl> getDtoImpl() {
        return ShopExchangeRateDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<ShopExchangeRate> getEntityIFace() {
        return ShopExchangeRate.class;
    }

    /** {@inheritDoc} */
    public List<ShopExchangeRateDTO> getAllByShopId(long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Shop shop = shopService.getById(shopId);
        final List<ShopExchangeRateDTO> rez = new ArrayList<ShopExchangeRateDTO>();
        fillDTOs(shop.getExchangerates(), rez);
        return rez;
    }

    /** {@inheritDoc} */
    public int updateDerivedPrices(final long shopId) {

        int rez =0;

        final Shop shop = shopService.getById(shopId);

        final String defaultCurrency =  shop.getDefaultCurrency();

        final List<String> allCurencies = shop.getSupportedCurrenciesAsList();

        for (String curr : allCurencies) {
            if (!defaultCurrency.equals(curr)) {
                rez += priceService.updateDerivedPrices(shop, curr);
            }
        }

        return rez;

    }

}

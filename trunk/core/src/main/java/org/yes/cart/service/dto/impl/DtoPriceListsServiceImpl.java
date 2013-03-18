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
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.PriceListDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.DtoPriceListsService;
import org.yes.cart.service.dto.DtoProductSkuService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.dto.support.PriceListFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12-12-03
 * Time: 6:23 PM
 */
public class DtoPriceListsServiceImpl implements DtoPriceListsService {

    private final DtoShopService dtoShopService;
    private final DtoProductSkuService dtoProductSkuService;

    private final GenericDAO<SkuPrice, Long> skuPriceDAO;
    private final GenericDAO<ProductSku, Long> productSkuDAO;
    private final GenericDAO<Shop, Long> shopDAO;

    private final DtoFactory dtoFactory;
    private final AdaptersRepository adaptersRepository;

    private final Assembler skuPriceAsm;

    public DtoPriceListsServiceImpl(final DtoShopService dtoShopService,
                                    final DtoProductSkuService dtoProductSkuService,
                                    final GenericDAO<SkuPrice, Long> skuPriceDAO,
                                    final GenericDAO<ProductSku, Long> productSkuDAO,
                                    final GenericDAO<Shop, Long> shopDAO,
                                    final DtoFactory dtoFactory,
                                    final AdaptersRepository adaptersRepository) {
        this.dtoShopService = dtoShopService;
        this.dtoProductSkuService = dtoProductSkuService;
        this.skuPriceDAO = skuPriceDAO;
        this.productSkuDAO = productSkuDAO;
        this.shopDAO = shopDAO;
        this.dtoFactory = dtoFactory;
        this.adaptersRepository = adaptersRepository;

        this.skuPriceAsm = DTOAssembler.newAssembler(
                this.dtoFactory.getImplClass(PriceListDTO.class),
                this.skuPriceDAO.getEntityFactory().getImplClass(SkuPrice.class));

    }


    /** {@inheritDoc} */
    public List<ShopDTO> getShops() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoShopService.getAll();
    }

    /** {@inheritDoc} */
    public List<String> getShopCurrencies(final ShopDTO shop) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (shop == null) {
            return new ArrayList<String>();
        }
        final String currencies = dtoShopService.getSupportedCurrencies(shop.getShopId());
        if (currencies == null) {
            return new ArrayList<String>();
        }
        return new ArrayList<String>(Arrays.asList(currencies.split(",")));
    }

    /** {@inheritDoc} */
    public List<PriceListDTO> getPriceList(final PriceListFilter filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<PriceListDTO> priceList = new ArrayList<PriceListDTO>();

        if (filter.getShop() != null && StringUtils.hasLength(filter.getCurrencyCode())) {
            // only allow lists for shop+currency selection

            final List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(Restrictions.eq("shop.shopId", filter.getShop().getId()));
            criteria.add(Restrictions.eq("currency", filter.getCurrencyCode()));
            if (StringUtils.hasLength(filter.getProductCode())) {
                if (filter.getProductCodeExact()) {
                    criteria.add(
                            Restrictions.or(
                                    Restrictions.eq("prod.code", filter.getProductCode()),
                                    Restrictions.eq("sku.code", filter.getProductCode())
                            )
                    );
                } else {
                    criteria.add(
                            Restrictions.or(
                                    Restrictions.ilike("prod.code", filter.getProductCode(), MatchMode.ANYWHERE),
                                    Restrictions.ilike("sku.code", filter.getProductCode(), MatchMode.ANYWHERE)
                            )
                    );
                }
            }
            if (StringUtils.hasLength(filter.getTag())) {
                if (filter.getTagExact()) {
                    criteria.add(Restrictions.eq("tag", filter.getTag()));
                } else {
                    criteria.add(Restrictions.ilike("tag", filter.getTag(), MatchMode.ANYWHERE));
                }
            }
            if (filter.getFrom() != null) {
                criteria.add(
                        Restrictions.or(
                                Restrictions.ge("salefrom", filter.getFrom()),
                                Restrictions.isNull("salefrom")
                        )
                );
            }
            if (filter.getTo() != null) {
                criteria.add(
                        Restrictions.or(
                                Restrictions.le("saleto", filter.getTo()),
                                Restrictions.isNull("saleto")
                        )
                );
            }

            final List<SkuPrice> entities = skuPriceDAO.findByCriteria(new CriteriaTuner() {
                public void tune(final Criteria crit) {
                    crit.createAlias("sku", "sku");
                    crit.createAlias("shop", "shop");
                    crit.createAlias("sku.product", "prod");
                    crit.setFetchMode("shop", FetchMode.JOIN);
                    crit.setFetchMode("sku", FetchMode.JOIN);
                    crit.setFetchMode("prod", FetchMode.JOIN);
                }
            }, criteria.toArray(new Criterion[criteria.size()]));

            final Map<String, Object> adapters = adaptersRepository.getAll();
            for (final SkuPrice entity : entities) {
                final PriceListDTO dto = dtoFactory.getByIface(PriceListDTO.class);
                skuPriceAsm.assembleDto(dto, entity, adapters, dtoFactory);
                priceList.add(dto);
            }

        }

        return priceList;
    }

    /** {@inheritDoc} */
    public PriceListDTO createPrice(final PriceListDTO price) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return savePrice(price);
    }

    /** {@inheritDoc} */
    public PriceListDTO updatePrice(final PriceListDTO price) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return savePrice(price);
    }

    private PriceListDTO savePrice(final PriceListDTO price) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        SkuPrice entity = null;
        if (price.getSkuPriceId() > 0) {
            // check by id
            entity = skuPriceDAO.findById(price.getSkuPriceId());
        }

        if (entity == null) {
            final List<ProductSku> skus = productSkuDAO.findByCriteria(Restrictions.eq("code", price.getSkuCode()));
            if (skus == null || skus.size() != 1) {
                throw new UnableToCreateInstanceException("Invalid SKU: " + price.getSkuCode(), null);
            }
            final List<Shop> shops = shopDAO.findByCriteria(Restrictions.eq("code", price.getShopCode()));
            if (shops == null || shops.size() != 1) {
                throw new UnableToCreateInstanceException("Invalid warehouse: " + price.getShopCode(), null);
            }

            entity = skuPriceDAO.getEntityFactory().getByIface(SkuPrice.class);
            entity.setSku(skus.get(0));
            entity.setShop(shops.get(0));
            entity.setCurrency(price.getCurrency());
        }

        final Map<String, Object> adapters = adaptersRepository.getAll();

        skuPriceAsm.assembleEntity(price, entity, adapters, dtoFactory);

        skuPriceDAO.saveOrUpdate(entity);

        skuPriceAsm.assembleDto(price, entity, adapters, dtoFactory);

        return price;

    }

    /** {@inheritDoc} */
    public void removePrice(final long skuPriceId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        dtoProductSkuService.removeSkuPrice(skuPriceId);
    }
}

/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.PriceListDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.dto.DtoPriceListsService;
import org.yes.cart.service.dto.DtoProductSkuService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.utils.HQLUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 12-12-03
 * Time: 6:23 PM
 */
public class DtoPriceListsServiceImpl implements DtoPriceListsService {

    private final DtoShopService dtoShopService;
    private final DtoProductSkuService dtoProductSkuService;
    private final PriceService priceService;

    private final GenericDAO<SkuPrice, Long> skuPriceDAO;
    private final GenericDAO<ProductSku, Long> productSkuDAO;
    private final GenericDAO<CarrierSla, Long> carrierSlaDAO;
    private final GenericDAO<Shop, Long> shopDAO;

    private final DtoFactory dtoFactory;
    private final AdaptersRepository adaptersRepository;

    private final Assembler skuPriceAsm;

    public DtoPriceListsServiceImpl(final DtoShopService dtoShopService,
                                    final DtoProductSkuService dtoProductSkuService,
                                    final PriceService priceService,
                                    final GenericDAO<ProductSku, Long> productSkuDAO,
                                    final GenericDAO<CarrierSla, Long> carrierSlaDAO,
                                    final GenericDAO<Shop, Long> shopDAO,
                                    final DtoFactory dtoFactory,
                                    final AdaptersRepository adaptersRepository) {
        this.dtoShopService = dtoShopService;
        this.dtoProductSkuService = dtoProductSkuService;
        this.priceService = priceService;
        this.carrierSlaDAO = carrierSlaDAO;
        this.skuPriceDAO = priceService.getGenericDao();
        this.productSkuDAO = productSkuDAO;
        this.shopDAO = shopDAO;
        this.dtoFactory = dtoFactory;
        this.adaptersRepository = adaptersRepository;

        this.skuPriceAsm = DTOAssembler.newAssembler(
                this.dtoFactory.getImplClass(PriceListDTO.class),
                this.skuPriceDAO.getEntityFactory().getImplClass(SkuPrice.class));

    }


    /** {@inheritDoc} */
    @Override
    public List<ShopDTO> getShops() throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoShopService.getAll();
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getShopCurrencies(final ShopDTO shop) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        if (shop == null) {
            return new ArrayList<>();
        }
        final String currencies = dtoShopService.getSupportedCurrencies(shop.getShopId());
        if (currencies == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(currencies.split(",")));
    }

    private final static char[] CODE = new char[] { '!' };
    private final static char[] TAG_OR_POLICY = new char[] { '#' };

    /** {@inheritDoc} */
    @Override
    public List<PriceListDTO> findBy(final long shopId, final String currency, final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<PriceListDTO> priceList = new ArrayList<>();

        List<SkuPrice> entities;

        if (shopId > 0 && StringUtils.isNotBlank(currency)) {
            // only allow lists for shop+currency selection

            if (StringUtils.isNotBlank(filter)) {

                final Pair<String, String> tagSearch = ComplexSearchUtils.checkSpecialSearch(filter, TAG_OR_POLICY);
                final Pair<LocalDateTime, LocalDateTime> dateSearch = tagSearch == null ? ComplexSearchUtils.checkDateRangeSearch(filter) : null;

                if (tagSearch != null) {

                    // tag & policy search
                    final String tagOrPolicy = tagSearch.getSecond();

                    final List<String> codes  = new ArrayList<>();
                    if ("shipping".equalsIgnoreCase(tagOrPolicy)) {

                        final List<CarrierSla> carrierSlas = carrierSlaDAO.findByCriteria(" inner join fetch e.carrier c join fetch c.shops s where s.shop.shopId = ?1", shopId);
                        if (CollectionUtils.isNotEmpty(carrierSlas)) {
                            for (final CarrierSla carrierSla : carrierSlas) {
                                codes.add(carrierSla.getGuid());
                                if (CarrierSla.WEIGHT_VOLUME.equals(carrierSla.getSlaType())) {
                                    codes.add(carrierSla.getGuid().concat("_KG"));
                                    codes.add(carrierSla.getGuid().concat("_KGMAX"));
                                    codes.add(carrierSla.getGuid().concat("_M3"));
                                    codes.add(carrierSla.getGuid().concat("_M3MAX"));
                                }
                            }
                        }

                    }

                    if (!codes.isEmpty()) {

                        entities = skuPriceDAO.findRangeByCriteria(
                                " where e.shop.shopId = ?1 and e.currency = ?2 and (lower(e.tag) like ?3 or lower(e.pricingPolicy) = ?4 or lower(e.ref) = ?4 or e.skuCode in (?5)) order by e.skuCode, e.quantity",
                                page * pageSize, pageSize,
                                shopId,
                                currency,
                                HQLUtils.criteriaIlikeAnywhere(tagOrPolicy),
                                HQLUtils.criteriaIeq(tagOrPolicy),
                                codes
                        );

                    } else {

                        entities = skuPriceDAO.findRangeByCriteria(
                                " where e.shop.shopId = ?1 and e.currency = ?2 and (lower(e.tag) like ?3 or lower(e.pricingPolicy) = ?4 or lower(e.ref) = ?4) order by e.skuCode, e.quantity",
                                page * pageSize, pageSize,
                                shopId,
                                currency,
                                HQLUtils.criteriaIlikeAnywhere(tagOrPolicy),
                                HQLUtils.criteriaIeq(tagOrPolicy)
                        );

                    }

                } else if (dateSearch != null) {

                    final LocalDateTime from = dateSearch.getFirst();
                    final LocalDateTime to = dateSearch.getSecond();

                    // time search
                    entities = skuPriceDAO.findRangeByCriteria(
                            " where e.shop.shopId = ?1 and e.currency = ?2 and (?3 is null or e.salefrom >= ?3)  and (?4 is null or e.saleto <= ?4) order by e.skuCode, e.quantity",
                            page * pageSize, pageSize,
                            shopId,
                            currency,
                            from,
                            to
                    );

                } else {

                    final Pair<String, String> byCode = ComplexSearchUtils.checkSpecialSearch(filter, CODE);

                    if (byCode != null) {

                        final List<ProductSku> skus = productSkuDAO.findRangeByCriteria(
                                " where lower(e.product.code) = ?1 or lower(e.product.manufacturerCode) = ?1 or lower(e.product.pimCode) = ?1 or lower(e.barCode) = ?1 or lower(e.manufacturerCode) = ?1",
                                0, pageSize,
                                HQLUtils.criteriaIeq(byCode.getSecond())
                        );

                        final List<String> skuCodes = new ArrayList<>();
                        for (final ProductSku sku : skus) {
                            skuCodes.add(sku.getCode()); // sku codes from product match
                        }


                        if (skuCodes.isEmpty()) {

                            entities = skuPriceDAO.findRangeByCriteria(
                                    " where e.shop.shopId = ?1 and e.currency = ?2 and lower(e.skuCode) = ?3 order by e.skuCode",
                                    page * pageSize, pageSize,
                                    shopId,
                                    currency,
                                    HQLUtils.criteriaIeq(byCode.getSecond())
                            );

                        } else {

                            entities = skuPriceDAO.findRangeByCriteria(
                                    " where e.shop.shopId = ?1 and e.currency = ?2 and (e.skuCode in (?3) or lower(e.skuCode) = ?4) order by e.skuCode",
                                    page * pageSize, pageSize,
                                    shopId,
                                    currency,
                                    skuCodes,
                                    HQLUtils.criteriaIeq(byCode.getSecond())
                            );

                        }

                    } else {

                        final List<ProductSku> skus = productSkuDAO.findRangeByCriteria(
                                " where lower(e.product.code) like ?1 or lower(e.product.name) like ?1 or lower(e.name) like ?1",
                                0, pageSize,
                                HQLUtils.criteriaIlikeAnywhere(filter)
                        );

                        final List<String> skuCodes = new ArrayList<>();
                        for (final ProductSku sku : skus) {
                            skuCodes.add(sku.getCode()); // sku codes from product match
                        }


                        final List<CarrierSla> slas = carrierSlaDAO.findRangeByCriteria(
                                " where lower(e.name) like ?1",
                                0, pageSize,
                                HQLUtils.criteriaIlikeAnywhere(filter));
                        for (final CarrierSla sla : slas) {
                            skuCodes.add(sla.getGuid()); // codes from SLA match
                        }

                        if (skuCodes.isEmpty()) {

                            entities = skuPriceDAO.findRangeByCriteria(
                                    " where e.shop.shopId = ?1 and e.currency = ?2 and lower(e.skuCode) like ?3 order by e.skuCode",
                                    page * pageSize, pageSize,
                                    shopId,
                                    currency,
                                    HQLUtils.criteriaIlikeAnywhere(filter)
                            );

                        } else {

                            entities = skuPriceDAO.findRangeByCriteria(
                                    " where e.shop.shopId = ?1 and e.currency = ?2 and (e.skuCode in (?3) or lower(e.skuCode) like ?4) order by e.skuCode",
                                    page * pageSize, pageSize,
                                    shopId,
                                    currency,
                                    skuCodes,
                                    HQLUtils.criteriaIlikeAnywhere(filter)
                            );

                        }
                    }
                }
            } else {

                entities = skuPriceDAO.findRangeByCriteria(
                        " where e.shop.shopId = ?1 and e.currency = ?2 order by e.skuCode",
                        page * pageSize, pageSize,
                        shopId,
                        currency
                );

            }

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
    @Override
    public PriceListDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final SkuPrice entity = skuPriceDAO.findById(id);

        final Map<String, Object> adapters = adaptersRepository.getAll();

        final PriceListDTO price = dtoFactory.getByIface(PriceListDTO.class);

        skuPriceAsm.assembleDto(price, entity, adapters, dtoFactory);

        return price;

    }

    /** {@inheritDoc} */
    @Override
    public PriceListDTO createPrice(final PriceListDTO price) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return savePrice(price);
    }

    /** {@inheritDoc} */
    @Override
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
            final List<Shop> shops = shopDAO.findByCriteria(" where e.code = ?1", price.getShopCode());
            if (shops == null || shops.size() != 1) {
                throw new UnableToCreateInstanceException("Invalid shop: " + price.getShopCode(), null);
            }

            entity = skuPriceDAO.getEntityFactory().getByIface(SkuPrice.class);
            entity.setSkuCode(price.getSkuCode());
            entity.setShop(shops.get(0));
            entity.setCurrency(price.getCurrency());
        }

        final Map<String, Object> adapters = adaptersRepository.getAll();

        skuPriceAsm.assembleEntity(price, entity, adapters, dtoFactory);

        ensureNonZeroPrices(entity);

        // use service since we flush cache there
        if (entity.getSkuPriceId() > 0L) {
            priceService.update(entity);
        } else {
            priceService.create(entity);
        }

        skuPriceAsm.assembleDto(price, entity, adapters, dtoFactory);

        return price;

    }

    private void ensureNonZeroPrices(final SkuPrice entity) {
        if (!MoneyUtils.isPositive(entity.getSalePrice())) {
            entity.setSalePrice(null);
        }
        if (!MoneyUtils.isPositive(entity.getMinimalPrice())) {
            entity.setMinimalPrice(null);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removePrice(final long skuPriceId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        dtoProductSkuService.removeSkuPrice(skuPriceId);
    }
}

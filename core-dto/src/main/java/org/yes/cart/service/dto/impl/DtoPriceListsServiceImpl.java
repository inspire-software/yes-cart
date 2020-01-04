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
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.dto.DtoPriceListsService;
import org.yes.cart.service.dto.DtoProductSkuService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.MoneyUtils;

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

    private final static char[] CODE = new char[] { '!' };
    private final static char[] TAG_OR_POLICY = new char[] { '#' };

    @Override
    public SearchResult<PriceListDTO> findPrices(final long shopId, final String currency, final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter");
        final List filterParam = params.get("filter");

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final PriceService priceService = this.priceService;

        if (shopId > 0 && StringUtils.isNotBlank(currency)) {
            // only allow lists for shop+currency selection

            final Map<String, List> currentFilter = new HashMap<>();
            if (CollectionUtils.isNotEmpty(filterParam) && filterParam.get(0) instanceof String && StringUtils.isNotBlank((String) filterParam.get(0))) {

                final String textFilter = ((String) filterParam.get(0)).trim();

                final Pair<String, String> tagSearch = ComplexSearchUtils.checkSpecialSearch(textFilter, TAG_OR_POLICY);
                final Pair<LocalDateTime, LocalDateTime> dateSearch = tagSearch == null ? ComplexSearchUtils.checkDateRangeSearch(textFilter) : null;

                if (tagSearch != null) {

                    // tag & policy search
                    final String tagOrPolicy = tagSearch.getSecond();

                    if ("shipping".equalsIgnoreCase(tagOrPolicy)) {

                        final List<String> codes = new ArrayList<>();
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

                        SearchContext.JoinMode.OR.setMode(currentFilter);
                        currentFilter.put("skuCode", Collections.singletonList(SearchContext.MatchMode.ANY.toParam(codes)));
                        currentFilter.put("tag", Collections.singletonList(tagOrPolicy));

                    } else {

                        SearchContext.JoinMode.OR.setMode(currentFilter);
                        currentFilter.put("tag", Collections.singletonList(tagOrPolicy));
                        currentFilter.put("pricingPolicy", Collections.singletonList(tagOrPolicy));
                        currentFilter.put("ref", Collections.singletonList(tagOrPolicy));

                    }

                } else if (dateSearch != null) {

                    final LocalDateTime from = dateSearch.getFirst();
                    final LocalDateTime to = dateSearch.getSecond();

                    if (from != null) {
                        currentFilter.put("salefrom", Collections.singletonList(SearchContext.MatchMode.GE.toParam(from)));
                    }
                    if (to != null) {
                        currentFilter.put("saleto", Collections.singletonList(SearchContext.MatchMode.LE.toParam(to)));
                    }

                } else {

                    final Pair<String, String> byCode = ComplexSearchUtils.checkSpecialSearch(textFilter, CODE);

                    if (byCode != null) {

                        final List<ProductSku> skus = productSkuDAO.findRangeByCriteria(
                                " where lower(e.code) like ?1 or lower(e.product.code) = ?1 or lower(e.product.manufacturerCode) = ?1 or lower(e.product.pimCode) = ?1 or lower(e.barCode) = ?1 or lower(e.manufacturerCode) = ?1",
                                0, pageSize,
                                HQLUtils.criteriaIeq(byCode.getSecond())
                        );

                        final List<String> skuCodes = new ArrayList<>();
                        for (final ProductSku sku : skus) {
                            skuCodes.add(sku.getCode()); // sku codes from product match
                        }


                        if (skuCodes.isEmpty()) {

                            currentFilter.put("skuCode", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(byCode.getSecond())));

                        } else {

                            SearchContext.JoinMode.OR.setMode(currentFilter);
                            currentFilter.put("skuCode", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(byCode.getSecond())));
                            currentFilter.put("skuCode", Collections.singletonList(SearchContext.MatchMode.ANY.toParam(skuCodes)));

                        }

                    } else {

                        final List<ProductSku> skus = productSkuDAO.findRangeByCriteria(
                                " where lower(e.code) like ?1 or lower(e.product.code) like ?1 or lower(e.product.name) like ?1 or lower(e.name) like ?1",
                                0, pageSize,
                                HQLUtils.criteriaIlikeAnywhere(textFilter)
                        );

                        final List<String> skuCodes = new ArrayList<>();
                        for (final ProductSku sku : skus) {
                            skuCodes.add(sku.getCode()); // sku codes from product match
                        }


                        final List<CarrierSla> slas = carrierSlaDAO.findRangeByCriteria(
                                " where lower(e.name) like ?1",
                                0, pageSize,
                                HQLUtils.criteriaIlikeAnywhere(textFilter));
                        for (final CarrierSla sla : slas) {
                            skuCodes.add(sla.getGuid()); // codes from SLA match
                        }

                        if (skuCodes.isEmpty()) {

                            currentFilter.put("skuCode", Collections.singletonList(textFilter));

                        } else {

                            SearchContext.JoinMode.OR.setMode(currentFilter);
                            currentFilter.put("skuCode", Collections.singletonList(textFilter));
                            currentFilter.put("skuCode", Collections.singletonList(SearchContext.MatchMode.ANY.toParam(skuCodes)));

                        }

                    }

                }

            }

            currentFilter.put("shopIds", Collections.singletonList(shopId));
            currentFilter.put("currencies", Collections.singletonList(currency));

            final int count = priceService.findPriceCount(currentFilter);
            if (count > startIndex) {

                final List<PriceListDTO> entities = new ArrayList<>();
                final List<SkuPrice> prices = priceService.findPrices(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), currentFilter);

                final Map<String, Object> adapters = adaptersRepository.getAll();
                for (final SkuPrice entity : prices) {
                    final PriceListDTO dto = dtoFactory.getByIface(PriceListDTO.class);
                    skuPriceAsm.assembleDto(dto, entity, adapters, dtoFactory);
                    entities.add(dto);
                }

                return new SearchResult<>(filter, entities, count);

            }
        }
        return new SearchResult<>(filter, Collections.emptyList(), 0);
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

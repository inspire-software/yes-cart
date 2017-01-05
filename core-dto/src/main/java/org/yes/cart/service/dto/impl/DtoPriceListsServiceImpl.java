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
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;
import org.yes.cart.dao.CriteriaTuner;
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
import org.yes.cart.service.dto.support.PriceListFilter;
import org.yes.cart.util.MoneyUtils;

import java.math.BigDecimal;
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
            criteria.add(Restrictions.eq("shop.shopId", filter.getShop().getShopId()));
            criteria.add(Restrictions.eq("currency", filter.getCurrencyCode()));
            if (StringUtils.hasLength(filter.getProductCode())) {

                if (filter.getProductCodeExact()) {

                    final List<ProductSku> skus = productSkuDAO.findByCriteria(new CriteriaTuner() {
                        public void tune(final Criteria crit) {
                            crit.createAlias("product", "prod");
                            crit.setFetchMode("prod", FetchMode.JOIN);
                        }
                    }, Restrictions.or(
                            Restrictions.or(
                                    Restrictions.eq("prod.code", filter.getProductCode()),
                                    Restrictions.eq("code", filter.getProductCode())
                            ),
                            Restrictions.or(
                                    Restrictions.eq("prod.name", filter.getProductCode()),
                                    Restrictions.eq("name", filter.getProductCode())
                            )
                    ));

                    final List<String> skuCodes = new ArrayList<String>();
                    skuCodes.add(filter.getProductCode()); // original for standalone inventory
                    for (final ProductSku sku : skus) {
                        skuCodes.add(sku.getCode()); // sku codes from product match
                    }

                    criteria.add(Restrictions.in("skuCode", skuCodes));

                } else {

                    final List<ProductSku> skus = productSkuDAO.findByCriteria(new CriteriaTuner() {
                        public void tune(final Criteria crit) {
                            crit.createAlias("product", "prod");
                            crit.setFetchMode("prod", FetchMode.JOIN);
                        }
                    }, Restrictions.or(
                            Restrictions.or(
                                    Restrictions.ilike("prod.code", filter.getProductCode(), MatchMode.ANYWHERE),
                                    Restrictions.ilike("code", filter.getProductCode(), MatchMode.ANYWHERE)
                            ),
                            Restrictions.or(
                                    Restrictions.ilike("prod.name", filter.getProductCode(), MatchMode.ANYWHERE),
                                    Restrictions.ilike("name", filter.getProductCode(), MatchMode.ANYWHERE)
                            )
                    ));

                    final List<String> skuCodes = new ArrayList<String>();
                    for (final ProductSku sku : skus) {
                        skuCodes.add(sku.getCode()); // sku codes from product match
                    }

                    if (skuCodes.isEmpty()) {
                        criteria.add(Restrictions.ilike("skuCode", filter.getProductCode(), MatchMode.ANYWHERE));
                    } else {
                        criteria.add(
                                Restrictions.or(
                                        Restrictions.ilike("skuCode", filter.getProductCode(), MatchMode.ANYWHERE),
                                        Restrictions.in("skuCode", skuCodes)
                                )
                        );
                    }
                }
            }
            if (StringUtils.hasLength(filter.getTag())) {
                if (filter.getTagExact()) {
                    criteria.add(Restrictions.eq("tag", filter.getTag()));
                } else {
                    criteria.add(Restrictions.ilike("tag", filter.getTag(), MatchMode.ANYWHERE));
                }
            }
            if (StringUtils.hasLength(filter.getPricingPolicy())) {
                criteria.add(Restrictions.eq("pricingPolicy", filter.getPricingPolicy()));
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
                    crit.createAlias("shop", "shop");
                    crit.setFetchMode("shop", FetchMode.JOIN);
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

    private final static char[] CODE = new char[] { '!' };
    private final static char[] TAG_OR_POLICY = new char[] { '#' };

    private final static Order[] PRICE_ORDER = new Order[] { Order.asc("skuCode"), Order.asc("quantity") };

    /** {@inheritDoc} */
    public List<PriceListDTO> findBy(final long shopId, final String currency, final String filter, final int page, final int pageSize) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<PriceListDTO> priceList = new ArrayList<PriceListDTO>();

        if (shopId > 0 && StringUtils.hasLength(currency)) {
            // only allow lists for shop+currency selection

            final List<Criterion> criteria = new ArrayList<Criterion>();
            criteria.add(Restrictions.eq("shop.shopId", shopId));
            criteria.add(Restrictions.eq("currency", currency));
            if (StringUtils.hasLength(filter)) {

                final Pair<String, String> tagSearch = ComplexSearchUtils.checkSpecialSearch(filter, TAG_OR_POLICY);
                final Pair<Date, Date> dateSearch = tagSearch == null ? ComplexSearchUtils.checkDateRangeSearch(filter) : null;

                if (tagSearch != null) {

                    // tag & policy search
                    final String tagOrPolicy = tagSearch.getSecond();
                    criteria.add(Restrictions.or(
                            Restrictions.ilike("tag", tagOrPolicy, MatchMode.ANYWHERE),
                            Restrictions.eq("pricingPolicy", tagOrPolicy)
                    ));

                } else if (dateSearch != null) {

                    final Date from = dateSearch.getFirst();
                    final Date to = dateSearch.getSecond();

                    // time search
                    if (from != null) {
                        criteria.add(Restrictions.ge("salefrom", from));
                    }
                    if (to != null) {
                        criteria.add(Restrictions.le("saleto", to));
                    }

                } else {

                    final Pair<String, String> byCode = ComplexSearchUtils.checkSpecialSearch(filter, CODE);

                    if (byCode != null) {


                        final List<ProductSku> skus = productSkuDAO.findByCriteria(new CriteriaTuner() {
                            public void tune(final Criteria crit) {
                                crit.createAlias("product", "prod");
                                crit.setFetchMode("prod", FetchMode.JOIN);
                            }
                        }, Restrictions.or(
                                Restrictions.or(
                                        Restrictions.or(
                                                Restrictions.ilike("prod.code", byCode.getSecond(), MatchMode.EXACT),
                                                Restrictions.ilike("barCode", byCode.getSecond(), MatchMode.EXACT)
                                        ),
                                        Restrictions.or(
                                                Restrictions.ilike("code", byCode.getSecond(), MatchMode.EXACT),
                                                Restrictions.ilike("manufacturerCode", byCode.getSecond(), MatchMode.EXACT)
                                        )

                                ),
                                Restrictions.or(
                                        Restrictions.ilike("prod.manufacturerCode", byCode.getSecond(), MatchMode.EXACT),
                                        Restrictions.ilike("prod.pimCode", byCode.getSecond(), MatchMode.EXACT)
                                )
                        ));

                        final List<String> skuCodes = new ArrayList<String>();
                        for (final ProductSku sku : skus) {
                            skuCodes.add(sku.getCode()); // sku codes from product match
                        }

                        if (skuCodes.isEmpty()) {
                            criteria.add(Restrictions.ilike("skuCode", byCode.getSecond(), MatchMode.EXACT));
                        } else {
                            criteria.add(
                                    Restrictions.or(
                                            Restrictions.ilike("skuCode", byCode.getSecond(), MatchMode.EXACT),
                                            Restrictions.in("skuCode", skuCodes)
                                    )
                            );
                        }


                    } else {

                        final List<ProductSku> skus = productSkuDAO.findByCriteria(new CriteriaTuner() {
                            public void tune(final Criteria crit) {
                                crit.createAlias("product", "prod");
                                crit.setFetchMode("prod", FetchMode.JOIN);
                            }
                        }, Restrictions.or(
                                Restrictions.or(
                                        Restrictions.ilike("prod.code", filter, MatchMode.ANYWHERE),
                                        Restrictions.ilike("code", filter, MatchMode.ANYWHERE)
                                ),
                                Restrictions.or(
                                        Restrictions.ilike("prod.name", filter, MatchMode.ANYWHERE),
                                        Restrictions.ilike("name", filter, MatchMode.ANYWHERE)
                                )
                        ));

                        final List<String> skuCodes = new ArrayList<String>();
                        for (final ProductSku sku : skus) {
                            skuCodes.add(sku.getCode()); // sku codes from product match
                        }


                        final List<CarrierSla> slas = carrierSlaDAO.findByCriteria(
                                Restrictions.or(
                                        Restrictions.ilike("guid", filter, MatchMode.ANYWHERE),
                                        Restrictions.ilike("name", filter, MatchMode.ANYWHERE)
                                )
                        );
                        for (final CarrierSla sla : slas) {
                            skuCodes.add(sla.getGuid()); // codes from SLA match
                        }


                        if (skuCodes.isEmpty()) {
                            criteria.add(Restrictions.ilike("skuCode", filter, MatchMode.ANYWHERE));
                        } else {
                            criteria.add(
                                    Restrictions.or(
                                            Restrictions.ilike("skuCode", filter, MatchMode.ANYWHERE),
                                            Restrictions.in("skuCode", skuCodes)
                                    )
                            );
                        }
                    }
                }
            }

            final List<SkuPrice> entities = skuPriceDAO.findByCriteria(new CriteriaTuner() {
                public void tune(final Criteria crit) {
                    crit.createAlias("shop", "shop");
                    crit.setFetchMode("shop", FetchMode.JOIN);
                }
            }, page * pageSize, pageSize, criteria.toArray(new Criterion[criteria.size()]), PRICE_ORDER);

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
    public PriceListDTO getById(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final SkuPrice entity = skuPriceDAO.findById(id);

        final Map<String, Object> adapters = adaptersRepository.getAll();

        final PriceListDTO price = dtoFactory.getByIface(PriceListDTO.class);

        skuPriceAsm.assembleDto(price, entity, adapters, dtoFactory);

        return price;

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
            final List<Shop> shops = shopDAO.findByCriteria(Restrictions.eq("code", price.getShopCode()));
            if (shops == null || shops.size() != 1) {
                throw new UnableToCreateInstanceException("Invalid warehouse: " + price.getShopCode(), null);
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
        if (entity.getSalePrice() != null && MoneyUtils.isFirstEqualToSecond(entity.getSalePrice(), BigDecimal.ZERO)) {
            entity.setSalePrice(null);
        }
        if (entity.getMinimalPrice() != null && MoneyUtils.isFirstEqualToSecond(entity.getMinimalPrice(), BigDecimal.ZERO)) {
            entity.setMinimalPrice(null);
        }
    }

    /** {@inheritDoc} */
    public void removePrice(final long skuPriceId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        dtoProductSkuService.removeSkuPrice(skuPriceId);
    }
}

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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Hibernate;
import org.springframework.util.CollectionUtils;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.GenericFTSCapableDAO;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultNavDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.dto.impl.ProductSearchResultDTOImpl;
import org.yes.cart.domain.dto.impl.ProductSearchResultNavDTOImpl;
import org.yes.cart.domain.dto.impl.ProductSearchResultPageDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.IndexBuilder;
import org.yes.cart.search.dao.entity.LuceneDocumentAdapterUtils;
import org.yes.cart.search.dao.support.ShopCategoryRelationshipSupport;
import org.yes.cart.search.dto.FilteredNavigationRecordRequest;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.ProductTypeAttrService;
import org.yes.cart.util.TimeContext;
import org.yes.cart.utils.HQLUtils;

import java.lang.System;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductServiceImpl extends BaseGenericServiceImpl<Product> implements ProductService {

    private final GenericFTSCapableDAO<Product, Long, Object> productDao;
    private final GenericFTSCapableDAO<ProductSku, Long, Object> productSkuDao;
    private final ProductSkuService productSkuService;
    private final ProductTypeAttrService productTypeAttrService;
    private final AttributeService attributeService;
    private final GenericDAO<ProductCategory, Long> productCategoryDao;
    private final GenericDAO<ProductTypeAttr, Long> productTypeAttrDao;
    private final ShopCategoryRelationshipSupport shopCategoryRelationshipSupport;
    private final Random rand;

    /**
     * Construct product service.
     *
     * @param productDao         product dao
     * @param productSkuDao      product SKU dao
     * @param productSkuService  product service
     * @param productTypeAttrService     product type dao to deal with type information
     * @param attributeService   attribute service
     * @param productCategoryDao category dao to work with category information
     * @param productTypeAttrDao product type attributes need to work with range navigation
     * @param shopCategoryRelationshipSupport shop product category relationship support
     */
    public ProductServiceImpl(final GenericFTSCapableDAO<Product, Long, Object> productDao,
                              final GenericFTSCapableDAO<ProductSku, Long, Object> productSkuDao,
                              final ProductSkuService productSkuService,
                              final ProductTypeAttrService productTypeAttrService,
                              final AttributeService attributeService,
                              final GenericDAO<ProductCategory, Long> productCategoryDao,
                              final GenericDAO<ProductTypeAttr, Long> productTypeAttrDao,
                              final ShopCategoryRelationshipSupport shopCategoryRelationshipSupport) {
        super(productDao);
        this.productDao = productDao;
        this.productSkuDao = productSkuDao;
        this.productSkuService = productSkuService;
        this.productTypeAttrService = productTypeAttrService;
        this.attributeService = attributeService;
        this.productCategoryDao = productCategoryDao;
        this.productTypeAttrDao = productTypeAttrDao;
        this.shopCategoryRelationshipSupport = shopCategoryRelationshipSupport;
        rand = new Random();
        rand.setSeed(System.currentTimeMillis());
    }

    /** {@inheritDoc} */
    public ProductSku getSkuById(final Long skuId) {
        return proxy().getSkuById(skuId, false);
    }

    /** {@inheritDoc} */
    public ProductSku getSkuById(final Long skuId, final boolean withAttributes) {
        final ProductSku sku =  productSkuService.getGenericDao().findById(skuId);
        if (sku != null && withAttributes) {
            Hibernate.initialize(sku.getAttributes());
        }
        return sku;
    }


    /**
     * Get default image file name by given product.
     *
     * @param productId given id, which identify product
     * @return image file name if found.
     */
    public String getDefaultImage(final Long productId) {
        final Map<Long, String> images = proxy().getAllProductsAttributeValues(AttributeNamesKeys.Product.PRODUCT_DEFAULT_IMAGE_ATTR_NAME);
        return images.get(productId);
    }


    /**
     * {@inheritDoc}
     */
    public List<Product> getProductByCategory(final long categoryId) {
        return productDao.findByNamedQuery("PRODUCTS.BY.CATEGORYID", categoryId, now());
    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

    /**
     * {@inheritDoc}
     */
    public Product getRandomProductByCategory(final Category category) {
        final int qty = getProductQty(category.getCategoryId());
        if (qty > 0) {
            final int idx = rand.nextInt(qty);
            final List<ProductCategory> productCategory =
                    productCategoryDao.findRangeByNamedQuery("PRODUCT.IN.CATEGORY.ONE", idx, 1, category.getCategoryId());

            if (!CollectionUtils.isEmpty(productCategory)) {
                final Product product = productDao.findById(productCategory.get(0).getProduct().getProductId());
                product.getAttributes().size(); // initialise attributes
                return product;
            }
        }
        return null;
    }

    private static final Comparator<Pair> BY_SECOND = new Comparator<Pair>() {
        public int compare(final Pair pair1, final Pair pair2) {
            return ((String) pair1.getSecond()).compareTo((String) pair2.getSecond());
        }
    };


    /**
     * {@inheritDoc}
     */
    public Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> getProductAttributes(
            final String locale, final long productId, final long skuId, final long productTypeId) {

        final List<ProdTypeAttributeViewGroup> productTypeAttrGroups = productTypeAttrService.getViewGroupsByProductTypeId(productTypeId);
        final Map<String, List<Pair<String, String>>> attributeViewGroupMap =
                mapAttributeGroupsByAttributeCode(locale, productTypeAttrGroups);

        final ProductSku sku = skuId != 0L ? proxy().getSkuById(skuId, true) : null;
        final Product product = productId != 0L ? proxy().getProductById(productId, true) :
                (sku != null ? proxy().getProductById(sku.getProduct().getProductId(), true) : null);

        Collection<AttrValue> productAttrValues;
        Collection<AttrValue> skuAttrValues;
        if (sku != null) {
            productAttrValues = product.getAllAttributes();
            skuAttrValues = sku.getAllAttributes();
        } else if (product != null) {
            productAttrValues = product.getAllAttributes();
            skuAttrValues = Collections.emptyList();
        } else {
            return Collections.emptyMap();
        }

        final Map<String, Pair<String, String>> viewsGroupsI18n = new HashMap<String, Pair<String, String>>();
        final Map<String, Pair<String, String>> attrI18n = new HashMap<String, Pair<String, String>>();

        final Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> attributesToShow =
                new TreeMap<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>>(BY_SECOND);

        final Map<String, I18NModel> attrDisplayNames = attributeService.getAllAttributeNames();
        final List<Attribute> multivalue = attributeService.findAttributesWithMultipleValues(AttributeGroupNames.PRODUCT);
        final Set<String> multivalueCodes = new HashSet<String>();
        if (!CollectionUtils.isEmpty(multivalue)) {
            for (final Attribute multi : multivalue) {
                multivalueCodes.add(multi.getCode());
            }
        }

        for (final AttrValue attrValue : productAttrValues) {

            loadAttributeValueToAttributesToShowMap(locale, attributeViewGroupMap, viewsGroupsI18n, attrI18n, attributesToShow, attrValue, attrDisplayNames, multivalueCodes);

        }

        for (final AttrValue attrValue : skuAttrValues) {

            loadAttributeValueToAttributesToShowMap(locale, attributeViewGroupMap, viewsGroupsI18n, attrI18n, attributesToShow, attrValue, attrDisplayNames, multivalueCodes);

        }

        return attributesToShow;
    }



    private void loadAttributeValueToAttributesToShowMap(
            final String locale, final Map<String, List<Pair<String, String>>> attributeViewGroupMap,
            final Map<String, Pair<String, String>> viewsGroupsI18n, final Map<String, Pair<String, String>> attrI18n,
            final Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> attributesToShow,
            final AttrValue attrValue, final Map<String, I18NModel> attrDisplayNames, final Set<String> multivalueCodes) {

        if (attrValue.getAttributeCode() == null) {
            return;
        }
        final Pair<String, String> attr;
        if (attrI18n.containsKey(attrValue.getAttributeCode())) {
            attr = attrI18n.get(attrValue.getAttributeCode());
        } else {

            final I18NModel attrModel = attrDisplayNames.get(attrValue.getAttributeCode());
            final String name = attrModel != null ? attrModel.getValue(locale) : attrValue.getAttributeCode();

            attr = new Pair<String, String>(attrValue.getAttributeCode(), name);
            attrI18n.put(attrValue.getAttributeCode(), attr);
        }

        List<Pair<String, String>> groupsForAttr = attributeViewGroupMap.get(attr.getFirst());
        if (groupsForAttr == null) {
            // groupsForAttr = NO_GROUP;
            return; // no need to show un-grouped attributes
        }
        for (final Pair<String, String> groupForAttr : groupsForAttr) {

            final Pair<String, String> group;
            final Map<Pair<String, String>, List<Pair<String, String>>> attrValuesInGroup;
            if (viewsGroupsI18n.containsKey(groupForAttr.getFirst())) {
                group = viewsGroupsI18n.get(groupForAttr.getFirst());
                attrValuesInGroup = attributesToShow.get(group);
            } else {
                viewsGroupsI18n.put(groupForAttr.getFirst(), groupForAttr);
                attrValuesInGroup = new TreeMap<Pair<String, String>, List<Pair<String, String>>>(BY_SECOND);
                attributesToShow.put(groupForAttr, attrValuesInGroup);
            }

            final Pair<String, String> val = new Pair<String, String>(
                    attrValue.getVal(),
                    new FailoverStringI18NModel(
                            attrValue.getDisplayVal(),
                            attrValue.getVal()
                    ).getValue(locale)
            );

            final List<Pair<String, String>> attrValuesForAttr;
            if (attrValuesInGroup.containsKey(attr)) {
                attrValuesForAttr = attrValuesInGroup.get(attr);
                if (multivalueCodes.contains(attrValue.getAttributeCode())) {
                    attrValuesForAttr.add(val);
                } else {
                    attrValuesForAttr.set(0, val); // replace with latest (hopefully SKU)
                }
            } else {
                attrValuesForAttr = new ArrayList<Pair<String, String>>();
                attrValuesInGroup.put(attr, attrValuesForAttr);
                attrValuesForAttr.add(val);
            }

        }
    }

    /*
        Attribute Code => List<Groups>
     */
    private Map<String, List<Pair<String, String>>> mapAttributeGroupsByAttributeCode(
            final String locale, final Collection<ProdTypeAttributeViewGroup> attributeViewGroup) {
        if (CollectionUtils.isEmpty(attributeViewGroup)) {
            return Collections.emptyMap();
        }
        final Map<String, List<Pair<String, String>>> map = new HashMap<String, List<Pair<String, String>>>();
        for (final ProdTypeAttributeViewGroup group : attributeViewGroup) {
            if (group.getAttrCodeList() != null) {
                final String[] attributesCodes = group.getAttrCodeList().split(",");
                for (final String attrCode : attributesCodes) {
                    List<Pair<String, String>> groups = map.get(attrCode);
                    if (groups == null) {
                        groups = new ArrayList<Pair<String, String>>();
                        map.put(attrCode, groups);
                    }
                    groups.add(new Pair<String, String>(
                            String.valueOf(group.getProdTypeAttributeViewGroupId()),
                            new FailoverStringI18NModel(
                                    group.getDisplayName(),
                                    group.getName()
                            ).getValue(locale)
                    ));
                }
            }
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    public Map<Pair<String, String>, Map<Pair<String, String>, Map<String, List<Pair<String, String>>>>> getCompareAttributes(final String locale,
                                                                                                                              final List<Long> productId,
                                                                                                                              final List<Long> skuId) {

        final Map<Pair<String, String>, Map<Pair<String, String>, Map<String, List<Pair<String, String>>>>> view =
                new TreeMap<Pair<String, String>, Map<Pair<String, String>, Map<String, List<Pair<String, String>>>>>(BY_SECOND);

        for (final Long id : productId) {

            final Product product = proxy().getProductById(id, true);
            if (product != null) {

                final Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> productView =
                        proxy().getProductAttributes(locale, id, 0L, product.getProducttype().getProducttypeId());

                mergeCompareView(view, productView, "p_" + id);
            }

        }

        for (final Long id : skuId) {

            final ProductSku sku = proxy().getSkuById(id, true);
            if (sku != null) {

                final Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> productView =
                        proxy().getProductAttributes(locale, sku.getProduct().getProductId(), id, sku.getProduct().getProducttype().getProducttypeId());

                mergeCompareView(view, productView, "s_" + id);

            }

        }

        return view;

    }


    private void mergeCompareView(final Map<Pair<String, String>, Map<Pair<String, String>, Map<String, List<Pair<String, String>>>>> view,
                                  final Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> add,
                                  final String id) {

        for (final Map.Entry<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> group : add.entrySet()) {

            Map<Pair<String, String>, Map<String, List<Pair<String, String>>>> attributesInGroup = view.get(group.getKey());

            if (attributesInGroup == null) {

                attributesInGroup = new TreeMap<Pair<String, String>, Map<String, List<Pair<String, String>>>>(BY_SECOND);
                view.put(group.getKey(), attributesInGroup);

            }

            for (final Map.Entry<Pair<String, String>, List<Pair<String, String>>> attribute : group.getValue().entrySet()) {

                Map<String, List<Pair<String, String>>> valueByProduct = attributesInGroup.get(attribute.getKey());

                if (valueByProduct == null) {

                    valueByProduct = new HashMap<String, List<Pair<String, String>>>();
                    attributesInGroup.put(attribute.getKey(), valueByProduct);

                }

                valueByProduct.put(id, new ArrayList<Pair<String, String>>(attribute.getValue()));


            }


        }

    }

    /**
     * {@inheritDoc}
     */
    public Map<Long, String> getAllProductsAttributeValues(final String attributeCode) {
        final List<Object[]> values = (List) getGenericDao().findByNamedQuery("ALL.PRODUCT.ATTR.VALUE", attributeCode);
        if (values != null && !values.isEmpty()) {
            final Map<Long, String> map = new HashMap<Long, String>();
            for (final Object[] value : values) {
                map.put((Long) value[0], (String) value[1]);
            }
            return map;
        }
        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    public ProductSku getProductSkuByCode(final String skuCode) {
        return productSkuService.getProductSkuBySkuCode(skuCode);
    }

    /**
     * Get product by sku code.
     *
     * @param skuCode sku code
     * @return product sku for this sku code
     */
    public Product getProductBySkuCode(final String skuCode) {
        return (Product) productDao.getScalarResultByNamedQuery("PRODUCT.BY.SKU.CODE", skuCode);
    }


    /**
     * {@inheritDoc}
     */
    public Product getProductById(final Long productId) {
        // by default we use product with attributes, so true is better for caching
        return proxy().getProductById(productId, true);
    }

    /**
     * {@inheritDoc}
     */
    public Product getProductById(final Long productId, final boolean withAttribute) {
        final Product prod = productDao.findById(productId); // query with
        if (prod != null && withAttribute) {
            Hibernate.initialize(prod.getAttributes());
        }
        return prod;
    }

    /**
     * {@inheritDoc}
     */
    public ProductSearchResultPageDTO getProductSearchResultDTOByQuery(final NavigationContext navigationContext,
                                                                       final int firstResult,
                                                                       final int maxResults,
                                                                       final String sortFieldName,
                                                                       final boolean reverse) {

        final Pair<List<Object[]>, Integer> searchRez = productDao.fullTextSearch(
                navigationContext.getProductQuery(),
                firstResult,
                maxResults,
                sortFieldName,
                reverse,
                LuceneDocumentAdapterUtils.FIELD_PK,
                LuceneDocumentAdapterUtils.FIELD_CLASS,
                LuceneDocumentAdapterUtils.FIELD_OBJECT
        );

        final List<ProductSearchResultDTO> rez = new ArrayList<ProductSearchResultDTO>(searchRez.getFirst().size());
        for (Object[] obj : searchRez.getFirst()) {
            final ProductSearchResultDTO dto = LuceneDocumentAdapterUtils.readObjectFieldValue((String) obj[2], ProductSearchResultDTOImpl.class);
            rez.add(dto);
        }

        return new ProductSearchResultPageDTOImpl(rez, firstResult, maxResults, searchRez.getSecond(), sortFieldName, reverse);

    }

    /**
     * {@inheritDoc}
     */
    public ProductSearchResultNavDTO findFilteredNavigationRecords(final NavigationContext baseNavigationContext, final List<FilteredNavigationRecordRequest> request) {
        return new ProductSearchResultNavDTOImpl(productDao.fullTextSearchNavigation(baseNavigationContext.getProductQuery(), request));
    }

    /**
     * {@inheritDoc}
     */
    public int getProductQty(final NavigationContext navigationContext) {
        return productDao.fullTextSearchCount(navigationContext.getProductQuery());
    }


    /**
     * {@inheritDoc}
     */
    public Pair<Integer, Integer> getProductQtyAll() {

        final int total = getGenericDao().findCountByCriteria(null);
        final int active = getGenericDao().findCountByCriteria(
                " where (e.availablefrom is null or e.availability = ?1 or e.availablefrom <= ?2) and (e.availableto is null or e.availableto >= ?2)",
                Product.AVAILABILITY_PREORDER, now()
        );

        return new Pair<>(total, active);
    }

    /**
     * {@inheritDoc}
     */
    public List<Product> getProductByCategory(final long categoryId,
                                              final int firstResult,
                                              final int maxResults) {
        return productDao.findRangeByNamedQuery("PRODUCTS.BY.CATEGORYID",
                firstResult,
                maxResults,
                categoryId,
                now()
        );
    }


    /**
     * {@inheritDoc}
     */
    public List<Product> getProductByIdList(final List idList) {
        if (idList == null || idList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return productDao.findByNamedQuery("PRODUCTS.LIST.BY.IDS", idList);
    }


    /**
     * {@inheritDoc}
     */
    public Long findProductIdBySeoUri(final String seoUri) {
        List<Object> list = productDao.findQueryObjectByNamedQuery("PRODUCT.ID.BY.SEO.URI", seoUri);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Long findProductIdByGUID(final String guid) {
        List<Object> list = productDao.findQueryObjectByNamedQuery("PRODUCT.ID.BY.GUID", guid);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Long findProductIdByCode(final String code) {
        List<Object> list = productDao.findQueryObjectByNamedQuery("PRODUCT.ID.BY.CODE", code);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> findProductIdsByManufacturerCode(final String code) {
        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.IDS.BY.MANUFACTURER.CODE", code);
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> findProductIdsByBarCode(final String code) {
        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.IDS.BY.SKU.BARCODE", code);
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> findProductIdsByBarCodes(final Collection<String> codes) {
        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.IDS.BY.SKU.BARCODES", codes);
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> findProductIdsByPimCode(final String code) {
        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.IDS.BY.PIM.CODE", code);
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> findProductIdsByUnavailableBefore(final LocalDateTime before) {
        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.IDS.BY.AVAILABLETO", before);
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> findProductIdsByAttributeValue(final String attrCode, final String attrValue) {
        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.IDS.BY.ATTRIBUTE.CODE.AND.VALUE", attrCode, attrValue);
    }

    /**
     * {@inheritDoc}
     */
    public String findSeoUriByProductId(final Long productId) {
        List<Object> list = productDao.findQueryObjectByNamedQuery("SEO.URI.BY.PRODUCT.ID", productId);
        if (list != null && !list.isEmpty()) {
            final Object[] uriAndId = (Object[]) list.get(0);
            if (uriAndId[0] instanceof String) {
                return (String) uriAndId[0];
            }
            return String.valueOf(uriAndId[1]);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Long findProductSkuIdBySeoUri(final String seoUri) {
        List<Object> list = productSkuService.getGenericDao().findQueryObjectByNamedQuery("SKU.ID.BY.SEO.URI", seoUri);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Long findProductSkuIdByGUID(final String guid) {
        List<Object> list = productSkuService.getGenericDao().findQueryObjectByNamedQuery("SKU.ID.BY.GUID", guid);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Long findProductSkuIdByCode(final String code) {
        List<Object> list = productSkuService.getGenericDao().findQueryObjectByNamedQuery("SKU.ID.BY.CODE", code);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String findSeoUriByProductSkuId(final Long skuId) {
        List<Object> list = productSkuService.getGenericDao().findQueryObjectByNamedQuery("SEO.URI.BY.SKU.ID", skuId);
        if (list != null && !list.isEmpty()) {
            final Object[] uriAndId = (Object[]) list.get(0);
            if (uriAndId[0] instanceof String) {
                return (String) uriAndId[0];
            }
            return String.valueOf(uriAndId[1]);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public int getProductQty(final long categoryId) {
        return Integer.valueOf(
                String.valueOf(productDao.getScalarResultByNamedQuery("PRODUCTS.QTY.BY.CATEGORYID", categoryId, now())));
    }


    /**
     * {@inheritDoc}
     */
    public IndexBuilder.FTIndexState getProductsFullTextIndexState() {
        return productDao.getFullTextIndexState();
    }

    /**
     * {@inheritDoc}
     */
    public IndexBuilder.FTIndexState getProductsSkuFullTextIndexState() {
        return productSkuDao.getFullTextIndexState();
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProducts(final int batchSize) {
        productDao.fullTextSearchReindex(true, batchSize);
    }


    /**
     * {@inheritDoc}
     */
    public void reindexProducts(final int batchSize, final boolean async) {
        productDao.fullTextSearchReindex(async, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProductsSku(final int batchSize) {
        productSkuDao.fullTextSearchReindex(true, batchSize);
    }


    /**
     * {@inheritDoc}
     */
    public void reindexProductsSku(final int batchSize, final boolean async) {
        productSkuDao.fullTextSearchReindex(async, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProducts(final Long shopId, final int batchSize) {
        final Set<Long> categories = shopCategoryRelationshipSupport.getShopCategoriesIds(shopId);
        productDao.fullTextSearchReindex(true, batchSize);
    }


    /**
     * {@inheritDoc}
     */
    public void reindexProductsSku(final Long shopId, final int batchSize) {
        final Set<Long> categories = shopCategoryRelationshipSupport.getShopCategoriesIds(shopId);
        productSkuDao.fullTextSearchReindex(true, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProduct(final Long pk) {
        final Product product = findById(pk);
        if (product != null) {
            for (final ProductSku sku : product.getSku()) {
                productSkuDao.fullTextSearchReindex(sku.getSkuId());
            }
            productDao.fullTextSearchReindex(pk);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProductSku(final Long pk) {
        final ProductSku productSku = productSkuService.findById(pk);
        if (productSku != null) {
            productSkuDao.fullTextSearchReindex(productSku.getSkuId());
            productDao.fullTextSearchReindex(productSku.getProduct().getProductId());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProductSku(final String code) {
        final ProductSku productSku = productSkuService.findProductSkuBySkuCode(code);
        if (productSku != null) {
            productSkuDao.fullTextSearchReindex(productSku.getSkuId());
            productDao.fullTextSearchReindex(productSku.getProduct().getProductId());
        }
    }


    /**
     * {@inheritDoc}
     */
    public List<Product> getProductByCodeNameBrandType(final String code,
                                                       final String name,
                                                       final Long brandId,
                                                       final Long productTypeId) {

        return productDao.findByNamedQuery(
                "PRODUCT.BY.CODE.NAME.BRAND.TYPE",
                HQLUtils.criteriaIlikeAnywhere(code),
                NumberUtils.toLong(code),
                HQLUtils.criteriaIlikeAnywhere(name),
                brandId,
                productTypeId
        );

    }

    /**
     * Persist product. Default sku will be created.
     *
     * @param instance instance to persist
     * @return persisted instanse
     */
    public Product create(final Product instance) {

        ProductSku sku = productDao.getEntityFactory().getByIface(ProductSku.class);
        sku.setCode(instance.getCode());
        sku.setName(instance.getName());
        sku.setDisplayName(instance.getDisplayName());
        sku.setDescription(instance.getDescription());
        sku.setProduct(instance);
        sku.setRank(500);
        instance.getSku().add(sku);

        return getGenericDao().create(instance);
    }


    private ProductService proxy;

    private ProductService proxy() {
        if (proxy == null) {
            proxy = getSelf();
        }
        return proxy;
    }

    /**
     * @return self proxy to reuse AOP caching
     */
    public ProductService getSelf() {
        // Spring lookup method to get self proxy
        return null;
    }


}

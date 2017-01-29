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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.search.Query;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.CollectionUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.GenericFullTextSearchCapableDAO;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.dto.impl.ProductSearchResultDTOImpl;
import org.yes.cart.domain.dto.impl.ProductSearchResultPageDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.bridge.support.ShopCategoryRelationshipSupport;
import org.yes.cart.domain.entityindexer.IndexFilter;
import org.yes.cart.domain.entityindexer.impl.StoredAttributesImpl;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.i18n.impl.NonI18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.range.DisplayValue;
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.domain.misc.navigation.range.RangeNode;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.domain.queryobject.FilteredNavigationRecordRequest;
import org.yes.cart.domain.queryobject.impl.FilteredNavigationRecordImpl;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.ProductTypeAttrService;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductServiceImpl extends BaseGenericServiceImpl<Product> implements ProductService {

    private final GenericFullTextSearchCapableDAO<Product, Long> productDao;
    private final GenericFullTextSearchCapableDAO<ProductSku, Long> productSkuDao;
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
    public ProductServiceImpl(final GenericFullTextSearchCapableDAO<Product, Long> productDao,
                              final GenericFullTextSearchCapableDAO<ProductSku, Long> productSkuDao,
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
        rand.setSeed((new Date().getTime()));
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
        return productDao.findByNamedQuery("PRODUCTS.BY.CATEGORYID", categoryId, new Date());
    }

    /**
     * {@inheritDoc}
     */
    public Product getRandomProductByCategory(final Category category) {
        final int qty = getProductQty(category.getCategoryId());
        if (qty > 0) {
            final int idx = rand.nextInt(qty);
            final ProductCategory productCategory = productCategoryDao.findUniqueByCriteria(
                    idx,
                    Restrictions.eq("category.categoryId", category.getCategoryId())
            );

            if (productCategory != null) {
                final Product product = productDao.findById(productCategory.getProduct().getProductId());
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

        for (final AttrValue attrValue : productAttrValues) {

            loadAttributeValueToAttributesToShowMap(locale, attributeViewGroupMap, viewsGroupsI18n, attrI18n, attributesToShow, attrValue);

        }

        for (final AttrValue attrValue : skuAttrValues) {

            loadAttributeValueToAttributesToShowMap(locale, attributeViewGroupMap, viewsGroupsI18n, attrI18n, attributesToShow, attrValue);

        }

        return attributesToShow;
    }



    private void loadAttributeValueToAttributesToShowMap(
            final String locale, final Map<String, List<Pair<String, String>>> attributeViewGroupMap,
            final Map<String, Pair<String, String>> viewsGroupsI18n, final Map<String, Pair<String, String>> attrI18n,
            final Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> attributesToShow,
            final AttrValue attrValue) {

        if (attrValue.getAttribute() == null) {
            return;
        }
        final Pair<String, String> attr;
        if (attrI18n.containsKey(attrValue.getAttribute().getCode())) {
            attr = attrI18n.get(attrValue.getAttribute().getCode());
        } else {
            attr = new Pair<String, String>(
                    attrValue.getAttribute().getCode(),
                    new FailoverStringI18NModel(
                            attrValue.getAttribute().getDisplayName(),
                            attrValue.getAttribute().getName()
                    ).getValue(locale)
            );
            attrI18n.put(attrValue.getAttribute().getCode(), attr);
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
                if (attrValue.getAttribute().isAllowduplicate()) {
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
    public Pair<String, String> getProductAttribute(final String locale, final long productId, final long skuId, final String attributeCode) {
        if (skuId > 0L) {
            final List skuAvs =
                    getGenericDao().findByNamedQuery("PRODUCTSKU.ATTRIBUTE.VALUES.BY.CODE", skuId, attributeCode);
            if (skuAvs != null && skuAvs.size() > 0) {
                final Object[] av = (Object[]) skuAvs.get(0);
                return new Pair<String, String>(
                        (String) av[0],
                        new FailoverStringI18NModel((String) av[1], (String) av[0]).getValue(locale)
                );
            }
        }
        if (productId > 0L) {
            final List prodAvs =
                    getGenericDao().findByNamedQuery("PRODUCT.ATTRIBUTE.VALUES.BY.CODE", productId, attributeCode);
            if (prodAvs != null && prodAvs.size() > 0) {
                final Object[] av = (Object[]) prodAvs.get(0);
                return new Pair<String, String>(
                        (String) av[0],
                        new FailoverStringI18NModel((String) av[1], (String) av[0]).getValue(locale)
                );
            }
        }
        return null;
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
    public ProductSearchResultPageDTO getProductSearchResultDTOByQuery(final Query query,
                                                                       final int firstResult,
                                                                       final int maxResults,
                                                                       final String sortFieldName,
                                                                       final boolean reverse) {

        final Pair<List<Object[]>, Integer> searchRez = productDao.fullTextSearch(
                query,
                firstResult,
                maxResults,
                sortFieldName,
                reverse,
                ProductSearchQueryBuilder.PRODUCT_ID_FIELD,
                ProductSearchQueryBuilder.PRODUCT_CODE_FIELD,
                ProductSearchQueryBuilder.PRODUCT_DEFAULT_SKU_CODE_FIELD,
                ProductSearchQueryBuilder.PRODUCT_NAME_FIELD,
                ProductSearchQueryBuilder.PRODUCT_DESCRIPTION_FIELD,
                ProductSearchQueryBuilder.PRODUCT_AVAILABILITY_FIELD,
                ProductSearchQueryBuilder.PRODUCT_QTY_FIELD,
                ProductSearchQueryBuilder.PRODUCT_DEFAULTIMAGE_FIELD,
                ProductSearchQueryBuilder.PRODUCT_DISPLAYNAME_ASIS_FIELD,
                ProductSearchQueryBuilder.PRODUCT_DESCRIPTION_ASIS_FIELD,
                ProductSearchQueryBuilder.PRODUCT_FEATURED_FIELD,
                ProductSearchQueryBuilder.PRODUCT_AVAILABILITY_FROM_FIELD,
                ProductSearchQueryBuilder.PRODUCT_AVAILABILITY_TO_FIELD,
                ProductSearchQueryBuilder.PRODUCT_MIN_QTY_FIELD,
                ProductSearchQueryBuilder.PRODUCT_MAX_QTY_FIELD,
                ProductSearchQueryBuilder.PRODUCT_STEP_QTY_FIELD,
                ProductSearchQueryBuilder.PRODUCT_MULTISKU,
                ProductSearchQueryBuilder.PRODUCT_MANUFACTURER_CODE_FIELD,
                ProductSearchQueryBuilder.ATTRIBUTE_VALUE_STORE_FIELD,
                ProductSearchQueryBuilder.PRODUCT_CREATED_FIELD,
                ProductSearchQueryBuilder.PRODUCT_UPDATED_FIELD,
                ProductSearchQueryBuilder.PRODUCT_TAG_FIELD,
                ProductSearchQueryBuilder.BRAND_NAME_FIELD

                );

        final List<ProductSearchResultDTO> rez = new ArrayList<ProductSearchResultDTO>(searchRez.getFirst().size());
        for (Object[] obj : searchRez.getFirst()) {
            final ProductSearchResultDTO dto = new ProductSearchResultDTOImpl();
            dto.setId((Long) obj[0]);
            dto.setCode((String) obj[1]);
            dto.setDefaultSkuCode((String) obj[2]);
            dto.setName((String) obj[3]);
            dto.setDescription((String) obj[4]);
            dto.setAvailability(obj[5] == null ? Product.AVAILABILITY_STANDARD : (Integer) obj[5]);
            dto.setQtyOnWarehouse((Map) obj[6]);
            dto.setDefaultImage((String) obj[7]);
            dto.setDisplayName((String) obj[8]);
            dto.setDisplayDescription((String) obj[9]);
            dto.setFeatured(obj[10] != null && (Boolean) obj[10]);
            dto.setAvailablefrom((Date) obj[11]);
            dto.setAvailableto((Date) obj[12]);
            dto.setMinOrderQuantity((BigDecimal) obj[13]);
            dto.setMaxOrderQuantity((BigDecimal) obj[14]);
            dto.setStepOrderQuantity((BigDecimal) obj[15]);
            dto.setMultisku(obj[16] != null && Boolean.valueOf((String) obj[16]));
            dto.setManufacturerCode((String) obj[17]);
            dto.setAttributes(new StoredAttributesImpl((String) obj[18]));
            dto.setCreatedTimestamp((Date) obj[19]);
            dto.setUpdatedTimestamp((Date) obj[20]);
            dto.setTag((String) obj[21]);
            dto.setBrand((String) obj[22]);
            rez.add(dto);
        }

        return new ProductSearchResultPageDTOImpl(rez, firstResult, maxResults, searchRez.getSecond(), sortFieldName, reverse);

    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<Pair<String, Integer>>> findFilteredNavigationRecords(final Query baseQuery, final List<FilteredNavigationRecordRequest> request) {
        return productDao.fullTextSearchNavigation(baseQuery, request);
    }

    /**
     * {@inheritDoc}
     */
    public int getProductQty(final Query query) {
        return productDao.fullTextSearchCount(query);
    }


    /**
     * {@inheritDoc}
     */
    public Pair<Integer, Integer> getProductQtyAll() {

        final int total = getGenericDao().findCountByCriteria();
        final Date now = new Date();
        final int active = getGenericDao().findCountByCriteria(
                Restrictions.or(
                        Restrictions.isNull("availablefrom"),
                        Restrictions.eq("availability", Product.AVAILABILITY_PREORDER),
                        Restrictions.le("availablefrom", now)
                ),
                Restrictions.or(
                        Restrictions.isNull("availableto"),
                        Restrictions.ge("availableto", now)
                )
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
                new Date()   //TODO: V2 time machine
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
    public List<FilteredNavigationRecord> getDistinctBrands(final String locale) {
        List<Object[]> list = productDao.findQueryObjectsByNamedQuery("PRODUCTS.BRANDS.ALL");

        final List<FilteredNavigationRecord> records = constructBrandFilteredNavigationRecords(list);
        Collections.sort(
                records,
                new Comparator<FilteredNavigationRecord>() {
                    public int compare(final FilteredNavigationRecord record1, final FilteredNavigationRecord record2) {
                        int rez = record1.getName().compareTo(record2.getName());
                        if (rez == 0) {
                            rez = record1.getValue().compareTo(record2.getValue());
                        }
                        return rez;
                    }
                });
        return records;
    }


    /**
     * Get the ranked by ProductTypeAttr.rank list of unique product attribute values by given product type
     * and attribute code.
     *
     * @param locale        locale
     * @param productTypeId product type id
     * @return list of distinct attrib values
     */
    public List<FilteredNavigationRecord> getDistinctAttributeValues(final String locale, final long productTypeId) {
        final List<FilteredNavigationRecord> records = new ArrayList<FilteredNavigationRecord>();
        records.addAll(getSingleValueNavigationRecords(locale, productTypeId));
        records.addAll(getRangeValueNavigationRecords(locale, productTypeId));
        Collections.sort(
                records,
                new Comparator<FilteredNavigationRecord>() {
                    public int compare(final FilteredNavigationRecord record1, final FilteredNavigationRecord record2) {
                        int rez = record1.getRank() - record2.getRank();
                        if (rez == 0) {
                            rez = record1.getName().compareTo(record2.getName());
                            if (rez == 0 && !"R".equals(record1.getType())) {
                                rez = record1.getValue().compareTo(record2.getValue());
                            }
                        }
                        return rez;
                    }
                });
        return records;
    }

    /**
     * Collect the single attribute value navigation see ProductTypeAttr#navigationType
     *
     * @param locale        locale
     * @param productTypeId product type id
     * @return list of {@link org.yes.cart.domain.queryobject.FilteredNavigationRecord}
     */
    List<FilteredNavigationRecord> getSingleValueNavigationRecords(final String locale, final long productTypeId) {
        List<Object[]> list;
        final Map<FilteredNavigationRecord, FilteredNavigationRecord> records = new HashMap<FilteredNavigationRecord, FilteredNavigationRecord>();

        final Map<String, Integer> singleNavAttrCodes = attributeService.getSingleNavigatableAttributeCodesByProductType(productTypeId);
        if (!singleNavAttrCodes.isEmpty()) {
            final Map<String, I18NModel> attrNames = attributeService.getAllAttributeNames();

            /*
                Attribute values can be used by many different product types. However we cannot enforce usage of
                product types in determination of distinct values since we want to use product type definitions
                in polymorphic fashion.

                For example Category can define pseudo type PC which has attribute PROCESSOR. However we may want to
                refine PC into Notebook product type. Nootebooks may also reside in this category. Thefore when we
                access filtered navigation for Category PC we want distinct values of PROCESSOR for both
                PCs and Notebooks.

                Therefore distinct grouping must only be done on Attribute.CODE.

                However a causion must be taken here because this means that values for attribute must be consistent
                accross all product types, otherwise there is no guarantee on what displayable name will appear in
                filtered navigation.
             */

            list = productDao.findQueryObjectsByNamedQuery(
                    "PRODUCTS.ATTR.CODE.VALUES.BY.ATTRCODES", singleNavAttrCodes.keySet());
            appendFilteredNavigationRecords(records, locale, list, attrNames, singleNavAttrCodes);

            list = productDao.findQueryObjectsByNamedQuery(
                    "PRODUCTSKUS.ATTR.CODE.VALUES.BY.ATTRCODES", singleNavAttrCodes.keySet());
            appendFilteredNavigationRecords(records, locale, list, attrNames, singleNavAttrCodes);
        }
        return new ArrayList<FilteredNavigationRecord>(records.values());
    }


    /**
     * Get the navigation records for range values.
     *
     * @param locale        locale
     * @param productTypeId product type id
     * @return list of {@link org.yes.cart.domain.queryobject.FilteredNavigationRecord}
     */
    List<FilteredNavigationRecord> getRangeValueNavigationRecords(final String locale, final long productTypeId) {

        final List<ProductTypeAttr> rangeNavigationInType = productTypeAttrDao.findByNamedQuery(
                "PRODUCTS.RANGE.ATTR.CODE.VALUES.BY.PRODUCTTYPEID",
                productTypeId, Boolean.TRUE, Boolean.TRUE);


        final List<FilteredNavigationRecord> records = new ArrayList<FilteredNavigationRecord>();

        for (ProductTypeAttr entry : rangeNavigationInType) {
            RangeList rangeList = entry.getRangeList();
            if (rangeList != null && rangeList.getRanges() != null) {
                for (RangeNode node : rangeList.getRanges()) {

                    final Map<String, String> i18n = getRangeValueDisplayNames(node.getI18n());

                    records.add(
                            new FilteredNavigationRecordImpl(
                                    entry.getAttribute().getName(),
                                    getAttributeNameRepresentation(locale, entry),
                                    entry.getAttribute().getCode(),
                                    getRangeValueRepresentation(node.getFrom(), node.getTo()),
                                    getRangeDisplayValueRepresentation(locale, i18n, node.getFrom(), node.getTo()),
                                    0, // put zero initially as this this be populated by FT query
                                    entry.getRank(),
                                    "R"
                            )
                    );
                }
            }
        }
        return records;
    }

    private String getAttributeNameRepresentation(final String locale, final ProductTypeAttr entry) {
        return new FailoverStringI18NModel(entry.getAttribute().getDisplayName(), entry.getAttribute().getName()).getValue(locale);
    }

    private String getRangeValueRepresentation(final String from, final String to) {
        return from + Constants.RANGE_NAVIGATION_DELIMITER + to;
    }

    private String getRangeDisplayValueRepresentation(final String locale, final Map<String, String> display, final String from, final String to) {
        final I18NModel toI18n = new StringI18NModel(display);
        final String localName = toI18n.getValue(locale);
        if (StringUtils.isBlank(localName)) {
            return from + " - " + to;
        }
        return localName;
    }

    private Map<String, String> getRangeValueDisplayNames(final List<DisplayValue> displayValues) {

        final Map<String, String> display = new HashMap<String, String>();
        if (displayValues != null) {
            for (final DisplayValue dv :displayValues) {
                display.put(dv.getLang(), dv.getValue());
            }
        }
        return display;
    }

    private static final I18NModel BLANK = new NonI18NModel("-");

    private void appendFilteredNavigationRecords(final Map<FilteredNavigationRecord, FilteredNavigationRecord> toAppendTo,
                                                 final String locale,
                                                 final List<Object[]> list,
                                                 final Map<String, I18NModel> attrNames,
                                                 final Map<String, Integer> attrRanks) {
        for (Object[] objArray : list) {

            final String attrCode = (String) objArray[0];
            final I18NModel attrName = attrNames.containsKey(attrCode) ? attrNames.get(attrCode) : BLANK;
            final Integer attrRank = attrRanks.containsKey(attrCode) ? attrRanks.get(attrCode) : Integer.MAX_VALUE;

            final FilteredNavigationRecord fnr = new FilteredNavigationRecordImpl(
                    attrName.getValue("-"),
                    attrName.getValue(locale),
                    attrCode,
                    (String) objArray[1],
                    new StringI18NModel((String) objArray[2]).getValue(locale),
                    0, // put zero initially as this this be populated by FT query
                    attrRank,
                    "S"
            );

            final FilteredNavigationRecord oldFnr = toAppendTo.get(fnr);
            if (oldFnr == null) {
                toAppendTo.put(fnr, fnr);
            } else {
                final String displayValue = oldFnr.getDisplayValue();
                if (displayValue == null) {
                    toAppendTo.put(fnr, fnr);
                }
            }

        }
    }


    /**
     * Construct filtered navigation records.
     *
     * @param list of raw object arrays after, result of named query
     * @return constructed list of navigation records.
     */
    private List<FilteredNavigationRecord> constructBrandFilteredNavigationRecords(final List<Object[]> list) {
        List<FilteredNavigationRecord> result = new ArrayList<FilteredNavigationRecord>(list.size());
        for (Object[] objArray : list) {
            result.add(
                    new FilteredNavigationRecordImpl(
                            (String) objArray[0],
                            (String) objArray[1],
                            (String) objArray[2],
                            (Integer) objArray[3]
                    )
            );

        }
        return result;
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
    public List<Long> findProductIdsByPimCode(final String code) {
        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.IDS.BY.PIM.CODE", code);
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
                String.valueOf(productDao.getScalarResultByNamedQuery("PRODUCTS.QTY.BY.CATEGORYID", categoryId, new Date())));  //TODO: V2 time machine
    }


    /**
     * {@inheritDoc}
     */
    public GenericFullTextSearchCapableDAO.FTIndexState getProductsFullTextIndexState() {
        return productDao.getFullTextIndexState();
    }

    /**
     * {@inheritDoc}
     */
    public GenericFullTextSearchCapableDAO.FTIndexState getProductsSkuFullTextIndexState() {
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
        productDao.fullTextSearchReindex(true, batchSize, new IndexFilter<Product>() {
            @Override
            public boolean skipIndexing(final Product entity) {
                for (final ProductCategory pcat : entity.getProductCategory()) {
                    if (categories.contains(pcat.getCategory().getCategoryId())) {
                        return false;
                    }
                }
                return true;
            }
        });
    }


    /**
     * {@inheritDoc}
     */
    public void reindexProductsSku(final Long shopId, final int batchSize) {
        final Set<Long> categories = shopCategoryRelationshipSupport.getShopCategoriesIds(shopId);
        productSkuDao.fullTextSearchReindex(true, batchSize, new IndexFilter<ProductSku>() {
            @Override
            public boolean skipIndexing(final ProductSku entity) {
                for (final ProductCategory pcat : entity.getProduct().getProductCategory()) {
                    if (categories.contains(pcat.getCategory().getCategoryId())) {
                        return false;
                    }
                }
                return true;
            }
        });
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
    public List<Product> getProductByCodeNameBrandType(final CriteriaTuner criteriaTuner,
                                                       final String code,
                                                       final String name,
                                                       final Long brandId,
                                                       final Long productTypeId) {

        final List<Criterion> criterionList = new ArrayList<Criterion>();
        if (StringUtils.isNotBlank(code)) {
            criterionList.add(Restrictions.or(
                    Restrictions.like("code", code, MatchMode.ANYWHERE),
                    Restrictions.like("manufacturerCode", code, MatchMode.ANYWHERE),
                    Restrictions.eq("pimCode", code),
                    Restrictions.eq("guid", code),
                    Restrictions.eq("productId", NumberUtils.toLong(code))
            ));
        }
        if (StringUtils.isNotBlank(name)) {
            criterionList.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
        }
        if (brandId != null) {
            criterionList.add(Restrictions.eq("brand.brandId", brandId));
        }
        if (productTypeId != null) {
            criterionList.add(Restrictions.eq("producttype.producttypeId", productTypeId));
        }

        return productDao.findByCriteria(
                criteriaTuner,
                criterionList.toArray(new Criterion[criterionList.size()])
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

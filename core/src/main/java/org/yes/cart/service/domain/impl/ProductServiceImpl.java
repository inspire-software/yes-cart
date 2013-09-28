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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.CollectionUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.domain.misc.navigation.range.RangeNode;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.domain.queryobject.impl.FilteredNavigationRecordImpl;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductSkuService;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductServiceImpl extends BaseGenericServiceImpl<Product> implements ProductService {

    private static final List<Pair<String, String>> NO_GROUP = Arrays.asList(new Pair<String, String>("", ""));

    private final GenericDAO<Product, Long> productDao;
    private final ProductSkuService productSkuService;
    private final GenericDAO<ProductType, Long> productTypeDao;
    private final GenericDAO<ProductCategory, Long> productCategoryDao;
    private final GenericDAO<ProductTypeAttr, Long> productTypeAttrDao;
    private final Random rand;
    private final DtoFactory dtoFactory;

    /**
     * Construct product service.
     *
     * @param productDao         product dao
     * @param productSkuService  product service
     * @param productTypeDao     product type dao to deal with type information
     * @param productCategoryDao category dao to work with category nformation
     * @param productTypeAttrDao product type attributes need to work with range navigation
     */
    public ProductServiceImpl(final GenericDAO<Product, Long> productDao,
                              final ProductSkuService productSkuService,
                              final GenericDAO<ProductType, Long> productTypeDao,
                              final GenericDAO<ProductCategory, Long> productCategoryDao,
                              final GenericDAO<ProductTypeAttr, Long> productTypeAttrDao,
                              final DtoFactory dtoFactory) {
        super(productDao);
        this.productDao = productDao;
        this.productSkuService = productSkuService;
        this.productTypeDao = productTypeDao;
        this.productCategoryDao = productCategoryDao;
        this.productTypeAttrDao = productTypeAttrDao;
        rand = new Random();
        rand.setSeed((new Date().getTime()));
        this.dtoFactory = dtoFactory;
    }


    /** {@inheritDoc} */
    @Cacheable(value = {"productService-byId"})
    public Product getById(final Long productId) {
        return productDao.findById(productId);
    }

    /** {@inheritDoc} */
    @Cacheable(value = {"productService-skuById"})
    public ProductSku getSkuById(final Long skuId) {
        return  productSkuService.getGenericDao().findById(skuId);
    }

    /** {@inheritDoc} */
    @Cacheable(value = {"productService-skuById2"})
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
    @Cacheable(value = {"productService-defaultImage"})
    public String getDefaultImage(final Long productId) {
        final Object obj = productDao.findQueryObjectsByNamedQuery("PRODUCT.ATTR.VALUE", productId, Constants.PRODUCT_DEFAULT_IMAGE_ATTR_NAME);
        if (obj instanceof List) {
            final List<Object> rez = (List<Object>) obj;
            if (rez.isEmpty()) {
                return null;
            }
            return (String) rez.get(0);
        }
        return (String) obj;


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
    @Cacheable(value = {"productService-randomProductByCategory"}/*, key = "category.getCategoryId()"*/)
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
    @Cacheable(value = {"productService-productAttributes"})
    public Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> getProductAttributes(
            final String locale, final long productId, final long skuId, final long productTypeId) {

        final ProductType productType = productTypeDao.findById(productTypeId);
        final Map<String, List<Pair<String, String>>> attributeViewGroupMap =
                mapAttributeGroupsByAttributeCode(locale, productType.getAttributeViewGroup());

        final ProductSku sku = skuId != 0L ? getSkuById(skuId, true) : null;
        final Product product = productId != 0 ? getProductById(productId, true) :
                (sku != null ? getProductById(sku.getProduct().getId(), true) : null);

        Collection<AttrValue> productAttrValues;
        Collection<AttrValue> skuAttrValues;
        if (sku != null) {
            productAttrValues = product.getAllAttibutes();
            skuAttrValues = sku.getAllAttibutes();
        } else if (product != null) {
            productAttrValues = product.getAllAttibutes();
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
            return; // no need to show un-groupped attributes
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

    @Cacheable(value = "productService-productAttribute")
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
    public ProductSku getProductSkuByCode(final String skuCode) {
        return productSkuService.getProductSkuBySkuCode(skuCode);
    }

    /**
     * Get product by sku code.
     *
     * @param skuCode sku code
     * @return product sku for this sku code
     */
    @Cacheable(value = "productService-productBySkuCode")
    public Product getProductBySkuCode(final String skuCode) {
        return (Product) productDao.getScalarResultByNamedQuery("PRODUCT.BY.SKU.CODE", skuCode);
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productById")
    public Product getProductById(final Long productId) {
        return productDao.findById(productId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productById2")
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
    @Cacheable(value = "productService-featuredProducts")
    public List<Product> getFeaturedProducts(final Collection categories, final int limit) {
        if (categories == null || categories.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else {
            List<Product> list = productDao.findQueryObjectsByNamedQueryWithList(
                    "PRODUCT.FEATURED",
                    categories,
                    new Date(), //TODO: V2 time machine
                    true);
            Collections.shuffle(list);
            int toIndex = limit; //to index exclusive
            if (list.size() < limit) {
                toIndex = list.size();
            }
            if (toIndex < 0) {
                toIndex = 0;
            }
            return new ArrayList<Product>(list.subList(0, toIndex));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-newArrivalsProductInCategory")
    public List<Product> getNewArrivalsProductInCategory(
            final long categoryId,
            final int maxResults) {
        return productDao.findRangeByNamedQuery("NEW.PRODUCTS.IN.CATEGORYID",
                0,
                maxResults,
                categoryId,
                new Date()    //TODO: V2 time machine
        );
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productByQuery")
    public List<Product> getProductByQuery(
            final Query query,
            final int firstResult,
            final int maxResults,
            final String sortFieldName,
            final boolean reverse) {

        return productDao.fullTextSearch(query, firstResult, maxResults, sortFieldName, reverse);

    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productSearchResultDTOByQuery")
    public List<ProductSearchResultDTO> getProductSearchResultDTOByQuery(
            final Query query,
            final int firstResult,
            final int maxResults,
            final String sortFieldName,
            final boolean reverse) {

        final List<Object[]> searchRez = productDao.fullTextSearch(
                query,
                firstResult,
                maxResults,
                sortFieldName,
                reverse,
                ProductSearchQueryBuilder.PRODUCT_ID_FIELD,
                ProductSearchQueryBuilder.PRODUCT_CODE_FIELD,
                ProductSearchQueryBuilder.PRODUCT_NAME_FIELD,
                ProductSearchQueryBuilder.PRODUCT_DESCRIPTION_FIELD,
                ProductSearchQueryBuilder.PRODUCT_AVAILABILITY_FIELD,
                ProductSearchQueryBuilder.PRODUCT_QTY_FIELD,
                ProductSearchQueryBuilder.PRODUCT_AVAILABLESKUCODE_FIELD,
                ProductSearchQueryBuilder.PRODUCT_AVAILABLESKUCODEQTY_FIELD,
                ProductSearchQueryBuilder.PRODUCT_DEFAULTIMAGE_FIELD,
                ProductSearchQueryBuilder.PRODUCT_DISPLAYNAME_ASIS_FIELD,
                ProductSearchQueryBuilder.PRODUCT_DESCRIPTION_ASIS_FIELD,
                ProductSearchQueryBuilder.PRODUCT_FEATURED_FIELD);

        final List<ProductSearchResultDTO> rez = new ArrayList<ProductSearchResultDTO>(searchRez.size());
        for (Object[] obj : searchRez) {
            final ProductSearchResultDTO dto = dtoFactory.getByIface(ProductSearchResultDTO.class);
            dto.setId((Long) obj[0]);
            dto.setCode((String) obj[1]);
            dto.setName((String) obj[2]);
            dto.setDescription((String) obj[3]);
            dto.setAvailability(obj[4] == null ? Product.AVAILABILITY_STANDARD : (Integer) obj[4]);
            dto.setQtyOnWarehouse((BigDecimal) obj[5]);
            dto.setFirstAvailableSkuCode((String) obj[6]);
            dto.setFirstAvailableSkuQuantity((BigDecimal) obj[7]);
            dto.setDefaultImage((String) obj[8]);
            dto.setDisplayName((String) obj[9]);
            dto.setDisplayDescription((String) obj[10]);
            dto.setFeatured(obj[11] != null && (Boolean) obj[11]);
            rez.add(dto);
        }

        return rez;

    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productQty")
    public int getProductQty(final Query query) {
        return productDao.getResultCount(query);
    }


    /**
     * {@inheritDoc}
     */
    public List<Product> getProductByCategory(
            final long categoryId,
            final int firtsResult,
            final int maxResults) {
        return productDao.findRangeByNamedQuery("PRODUCTS.BY.CATEGORYID",
                firtsResult,
                maxResults,
                categoryId,
                new Date()   //TODO: V2 time machine
        );
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-distinctAttributeValues")
    public List<Object> getDistinctAttributeValues(final long productTypeId, final String code) {
        return productDao.findQueryObjectByNamedQuery(
                "PRODUCTS.ATTRIBUTE.VALUES.BY.CODE.PRODUCTTYPEID",
                productTypeId,
                code);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productByIdList")
    public List<Product> getProductByIdList(final List idList) {
        if (idList == null || idList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return productDao.findQueryObjectsByNamedQueryWithList("PRODUCTS.LIST.BY.IDS", idList, null);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-distinctBrands")
    public List<FilteredNavigationRecord> getDistinctBrands(final String locale, final List categories) {
        List<Object[]> list = productDao.findQueryObjectsByNamedQueryWithList(
                "PRODUCTS.ATTR.CODE.VALUES.BY.ASSIGNED.CATEGORIES",
                categories);
        return constructBrandFilteredNavigationRecords(list);
    }


    /**
     * Get the ranked by ProductTypeAttr.rank list of unique product attribute values by given product type
     * and attribute code.
     *
     * @param locale        locale
     * @param productTypeId product type id
     * @return list of distinct attib values
     */
    @Cacheable(value = "productService-distinctAttributeValues")
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
                            if (rez == 0) {
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
    public List<FilteredNavigationRecord> getSingleValueNavigationRecords(final String locale, final long productTypeId) {
        List<Object[]> list;
        List<FilteredNavigationRecord> records = new ArrayList<FilteredNavigationRecord>();

        list = productDao.findQueryObjectsByNamedQuery(
                "PRODUCTS.ATTR.CODE.VALUES.BY.PRODUCTTYPEID",
                productTypeId,
                true);
        records.addAll(constructFilteredNavigationRecords(locale, list));

        list = productDao.findQueryObjectsByNamedQuery(
                "PRODUCTSKUS.ATTR.CODE.VALUES.BY.PRODUCTTYPEID",
                productTypeId,
                true);
        records.addAll(constructFilteredNavigationRecords(locale, list));

        return records;
    }


    /**
     * Get the navigation records for range values.
     *
     * @param locale        locale
     * @param productTypeId product type id
     * @return list of {@link org.yes.cart.domain.queryobject.FilteredNavigationRecord}
     */
    public List<FilteredNavigationRecord> getRangeValueNavigationRecords(final String locale, final long productTypeId) {

        final List<ProductTypeAttr> rangeNavigationInType = productTypeAttrDao.findByNamedQuery(
                "PRODUCTS.RANGE.ATTR.CODE.VALUES.BY.PRODUCTTYPEID",
                productTypeId,
                true);


        final List<FilteredNavigationRecord> records = new ArrayList<FilteredNavigationRecord>();

        for (ProductTypeAttr entry : rangeNavigationInType) {
            RangeList rangeList = entry.getRangeList();
            if (rangeList != null && rangeList.getRanges() != null) {
                for (RangeNode node : rangeList.getRanges()) {
                    records.add(
                            new FilteredNavigationRecordImpl(
                                    entry.getAttribute().getName(),
                                    new FailoverStringI18NModel(entry.getAttribute().getDisplayName(), entry.getAttribute().getName()).getValue(locale),
                                    entry.getAttribute().getCode(),
                                    node.getFrom() + '-' + node.getTo(),
                                    node.getFrom() + '-' + node.getTo(),
                                    0,
                                    entry.getRank(),
                                    "R"
                            )
                    );
                }
            }
        }
        return records;
    }

    /**
     * Construct filtered navigation records.
     *
     * @param list of raw object arrays after, result of named query
     * @return constructed list of navigation records.
     */
    private List<FilteredNavigationRecord> constructFilteredNavigationRecords(final String locale, final List<Object[]> list) {
        List<FilteredNavigationRecord> result = new ArrayList<FilteredNavigationRecord>(list.size());
        for (Object[] objArray : list) {
            result.add(
                    new FilteredNavigationRecordImpl(
                            (String) objArray[0],
                            new FailoverStringI18NModel((String) objArray[1], (String) objArray[0]).getValue(locale),
                            (String) objArray[2],
                            (String) objArray[3],
                            new FailoverStringI18NModel((String) objArray[4], (String) objArray[3]).getValue(locale),
                            (Integer) objArray[5],
                            (Integer) objArray[6],
                            "S"
                    )
            );

        }
        return result;
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
    public Long getProductIdBySeoUri(final String seoUri) {
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
    public String getSeoUriByProductId(final Long productId) {
        List<Object> list = productDao.findQueryObjectByNamedQuery("SEO.URI.BY.PRODUCT.ID", productId);
        if (list != null && !list.isEmpty()) {
            final Object uri = list.get(0);
            if (uri instanceof String) {
                return (String) uri;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Long getProductSkuIdBySeoUri(final String seoUri) {
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
    public String getSeoUriByProductSkuId(final Long skuId) {
        List<Object> list = productSkuService.getGenericDao().findQueryObjectByNamedQuery("SEO.URI.BY.SKU.ID", skuId);
        if (list != null && !list.isEmpty()) {
            final Object uri = list.get(0);
            if (uri instanceof String) {
                return (String) uri;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productQty2")
    public int getProductQty(final long categoryId) {
        return Integer.valueOf(
                String.valueOf(productDao.getScalarResultByNamedQuery("PRODUCTS.QTY.BY.CATEGORYID", categoryId, new Date())));  //TODO: V2 time machine
    }


    /**
     * {@inheritDoc}
     */
    public int reindexProducts() {
        return productDao.fullTextSearchReindex(true);
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProduct(final Long pk) {
        return productDao.fullTextSearchReindex(pk);
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProductSku(final Long pk) {
        final ProductSku productSku = productSkuService.getById(pk);
        if (productSku != null) {
            return productDao.fullTextSearchReindex(productSku.getProduct().getProductId());
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProductSku(final String code) {
        final ProductSku productSku = productSkuService.getProductSkuBySkuCodeForIndexing(code);
        if (productSku != null) {
            return productDao.fullTextSearchReindex(productSku.getProduct().getProductId());
        }
        return 0;
    }

    /**
     * Get the total quantity of product skus on all warehouses.
     *
     * @param product product
     * @return quantity of product.
     */
    public BigDecimal getProductQuantity(final Product product) {
        return productDao.findSingleByNamedQuery("SKU.QTY.BY.PRODUCT", product);
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal getProductQuantity(final Product product, final Shop shop) {
        return productDao.findSingleByNamedQuery("SKU.QTY.BY.PRODUCT.SHOP", product, shop);
    }


    /**
     * {@inheritDoc}
     */
    public void clearEmptyAttributes() {
        //productDao.executeHsqlUpdate("delete from AttrValueEntityProduct a where a.val is null or a.val=''");
        //NativeUpdate("DELETE FROM TPRODUCTATTRVALUE WHERE VAL IS NULL OR VAL =''");
    }


    /**
     * {@inheritDoc}
     */
    public List<Product> getProductByCodeNameBrandType(
            final CriteriaTuner criteriaTuner,
            final String code,
            final String name,
            final Long brandId,
            final Long productTypeId) {

        final List<Criterion> criterionList = new ArrayList<Criterion>();
        if (StringUtils.isNotBlank(code)) {
            criterionList.add(Restrictions.like("code", code, MatchMode.ANYWHERE));
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
    @CacheEvict(value ={
            "productService-randomProductByCategory",
            "productService-featuredProducts",
            "productService-newArrivalsProductInCategory",
            "productService-productByQuery",
            "productService-productSearchResultDTOByQuery",
            "productService-productQty",
            "productService-productByIdList",
            "productService-distinctAttributeValues",
            "productService-distinctBrands",
            "productService-productQty2"

    }, allEntries = true)
    public Product create(final Product instance) {

        ProductSku sku = productDao.getEntityFactory().getByIface(ProductSku.class);
        sku.setCode(instance.getCode());
        sku.setName(instance.getName());
        sku.setDisplayName(instance.getDisplayName());
        sku.setDescription(instance.getDescription());
        sku.setProduct(instance);
        instance.getSku().add(sku);

        return getGenericDao().create(instance);
    }


    /** {@inheritDoc} */
    @CacheEvict(value ={
            "productService-byId",
            "productService-skuById",
            "productService-skuById2",
            "productService-defaultImage",
            "productService-randomProductByCategory",
            "productService-productAttributes",
            "productService-productAttribute",
            "productService-productBySkuCode",
            "productService-productById",
            "productService-productById2",
            "productService-featuredProducts",
            "productService-newArrivalsProductInCategory",
            "productService-productByQuery",
            "productService-productSearchResultDTOByQuery",
            "productService-productQty",
            "productService-distinctAttributeValues",
            "productService-distinctBrands",
            "productService-productByIdList",
            "productService-productQty2"

    }, allEntries = true)
    public Product update(Product instance) {
        return super.update(instance);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /** {@inheritDoc} */
    @CacheEvict(value ={
            "productService-byId",
            "productService-skuById",
            "productService-skuById2",
            "productService-defaultImage",
            "productService-randomProductByCategory",
            "productService-productAttributes",
            "productService-productAttribute",
            "productService-productBySkuCode",
            "productService-productById",
            "productService-productById2",
            "productService-featuredProducts",
            "productService-newArrivalsProductInCategory",
            "productService-productByQuery",
            "productService-productSearchResultDTOByQuery",
            "productService-productQty",
            "productService-distinctAttributeValues",
            "productService-distinctBrands",
            "productService-productByIdList",
            "productService-productQty2"

    }, allEntries = true)
    public void delete(Product instance) {
        super.delete(instance);    //To change body of overridden methods use File | Settings | File Templates.
    }
}

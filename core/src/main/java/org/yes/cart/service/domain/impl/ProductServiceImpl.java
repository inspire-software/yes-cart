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
import org.springframework.util.CollectionUtils;
import org.yes.cart.cache.Cacheable;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.domain.misc.navigation.range.RangeNode;
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

    private final static String PROD_SERV_METHOD_CACHE = "productServiceImplMethodCache";

    private final GenericDAO<Product, Long> productDao;
    //private final GenericDAO<ProductSku, Long> productSkuDao;
    private final ProductSkuService productSkuService;
    private final GenericDAO<ProductType, Long> productTypeDao;
    private final GenericDAO<ProductCategory, Long> productCategoryDao;
    private final GenericDAO<ProductTypeAttr, Long> productTypeAttrDao;
    private final Random rand;

    /**
     * Construct product service.
     *
     * @param productDao         product dao
     * @param productSkuService      product service
     * @param productTypeDao     product type dao to deal with type information
     * @param productCategoryDao category dao to work with category nformation
     * @param productTypeAttrDao product type attributes need to work with range navigation
     */
    public ProductServiceImpl(final GenericDAO<Product, Long> productDao,
                              final ProductSkuService productSkuService,
                              final GenericDAO<ProductType, Long> productTypeDao,
                              final GenericDAO<ProductCategory, Long> productCategoryDao,
                              final GenericDAO<ProductTypeAttr, Long> productTypeAttrDao) {
        super(productDao);
        this.productDao = productDao;
        this.productSkuService = productSkuService;
        this.productTypeDao = productTypeDao;
        this.productCategoryDao = productCategoryDao;
        this.productTypeAttrDao = productTypeAttrDao;
        rand = new Random();
        rand.setSeed((new Date().getTime()));
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
        sku.setDescription(instance.getDescription());
        sku.setProduct(instance);
        instance.getSku().add(sku);

        return getGenericDao().create(instance);
    }

    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public Product getById(final Long productId) {
        return productDao.findById(productId);
    }

    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public ProductSku getSkuById(final Long skuId) {
        return (ProductSku) productSkuService.getGenericDao().findById(skuId);
    }


    /**
     * Get default image file name by given product.
     *
     * @param productId given id, which identify product
     * @return image file name if found.
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
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
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public Product getRandomProductByCategory(final Category category) {
        final int qty = getProductQty(category.getCategoryId());
        final int idx = rand.nextInt(qty);
        final ProductCategory productCategory = productCategoryDao.findUniqueByCriteria(
                idx,
                Restrictions.eq("category", category)
        );

        if (productCategory != null) {
            return getById(productCategory.getProduct().getProductId());
        }
        return null;
    }


    /**
     * Get the grouped product attributes, with values.
     *
     * @param locale locale
     * @param attributable  product  or sku
     * @param productTypeId product type id
     * @return List of pair group names - list of attribute name and value.
     */
    public List<Pair<String, List<AttrValue>>> getProductAttributes(final String locale, final Attributable attributable, final long productTypeId) {
        final ProductType productType = productTypeDao.findById(productTypeId);
        final Collection<ProdTypeAttributeViewGroup> attributeViewGroup = productType.getAttributeViewGroup();
        final List<Pair<String, List<AttrValue>>> attributesToShow =
                new ArrayList<Pair<String, List<AttrValue>>>(attributeViewGroup.size());
        Collection<AttrValue> attrValues = attributable.getAllAttibutes();

        for (ProdTypeAttributeViewGroup viewGroup : attributeViewGroup) {
            final List<AttrValue> attrNameValues = getProductAttributeValues(attrValues, viewGroup);
            attributesToShow.add(
                    new Pair<String, List<AttrValue>>(
                            new FailoverStringI18NModel(viewGroup.getDisplayName(), viewGroup.getName()).getValue(locale),
                            attrNameValues)
            );
        }
        return attributesToShow;
    }


    private List<AttrValue> getProductAttributeValues(
            final Collection<AttrValue> attrValueCollection,
            final ProdTypeAttributeViewGroup viewGroup) {
        //todo need sorted collection and fast search

        final String[] attributesNames = viewGroup.getAttrCodeList().split(",");
        final List<AttrValue> attrNameValues = new ArrayList<AttrValue>(attributesNames.length);
        for (String attrName : attributesNames) {
            for (AttrValue attrValue : attrValueCollection) {
                if (attrValue != null && attrValue.getAttribute() != null) {
                    final String candidatCode = attrValue.getAttribute().getCode();
                    if (candidatCode.equals(attrName) || candidatCode.equals("SKU" + attrName)) {
                        attrNameValues.add(attrValue);
                    }
                }

            }
        }
        return attrNameValues;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public ProductSku getProductSkuByCode(final String skuCode) {
        final List<ProductSku> skus = productSkuService.getGenericDao().findByNamedQuery("PRODUCT.SKU.BY.CODE", skuCode);
        if (CollectionUtils.isEmpty(skus)) {
            return null;
            //throw new ObjectNotFoundException(ProductSku.class, "skuCode", skuCode);
        }
        return skus.get(0);
    }

    /**
     * Get product by sku code.
     *
     * @param skuCode sku code
     * @return product sku for this sku code
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public Product getProductBySkuCode(final String skuCode) {
        return (Product) productDao.getScalarResultByNamedQuery("PRODUCT.BY.SKU.CODE", skuCode);
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public Product getProductById(final Long productId) {
        return productDao.findById(productId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public Product getProductById(final Long productId, final boolean withAttribute) {
        final Product prod = productDao.findById(productId);
        if (prod != null && withAttribute) {
            Hibernate.initialize(prod.getAttribute());
        }
        return prod;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public List<Product> getProductByQuery(
            final Query query,
            final int firtsResult,
            final int maxResults) {

        return productDao.fullTextSearch(query, firtsResult, maxResults);

    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public List<Product> getFeaturedProducts(final Collection categories, final int limit) {
        if (categories == null || categories.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else {
            List<Product> list = productDao.findQueryObjectsByNamedQueryWithList(
                    "PRODUCT.FEATURED",
                    categories,
                    new Date(), //TODO v2 time machine
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
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public List<Product> getNewArrivalsProductInCategory(
            final long categoryId,
            final int maxResults) {
        return productDao.findRangeByNamedQuery("NEW.PRODUCTS.IN.CATEGORYID",
                0,
                maxResults,
                categoryId,
                new Date()    //TODO time machine
        );
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public List<Product> getProductByQuery(
            final Query query,
            final int firtsResult,
            final int maxResults,
            final String sortFieldName,
            final boolean reverse) {

        return productDao.fullTextSearch(query, firtsResult, maxResults, sortFieldName, reverse);

    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
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
                new Date()   //TODO time machine
        );
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public List<Object> getDistinctAttributeValues(final long productTypeId, final String code) {
        return productDao.findQueryObjectByNamedQuery(
                "PRODUCTS.ATTRIBUTE.VALUES.BY.CODE.PRODUCTTYPEID",
                productTypeId,
                code);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public List<Product> getProductByIdList(final List idList) {
        if (idList == null || idList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return productDao.findQueryObjectsByNamedQueryWithList("PRODUCTS.LIST.BY.IDS", idList, null);
    }

    /**
     * {@inheritDoc}
     */
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
     * @param locale locale
     * @param productTypeId product type id
     * @return list of distinct attib values
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
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
     * @param locale locale
     * @param productTypeId product type id
     * @return list of {@link org.yes.cart.domain.queryobject.FilteredNavigationRecord}
     */
    public List<FilteredNavigationRecord> getSingleValueNavigationRecords(final String locale, final long productTypeId) {
        List<Object[]> list = productDao.findQueryObjectsByNamedQuery(
                "PRODUCTS.ATTR.CODE.VALUES.BY.PRODUCTTYPEID",
                productTypeId,
                true);
        return constructFilteredNavigationRecords(locale, list);
    }


    /**
     * Get the navigation records for range values.
     *
     * @param locale locale
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
                            (String) objArray[1],
                            (String) objArray[2],
                            new FailoverStringI18NModel((String) objArray[3], (String) objArray[2]).getValue(locale),
                            (Integer) objArray[4],
                            (Integer) objArray[5],
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
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public Long getProductIdBySeoUri(final String seoUri) {
        List<Product> list = productDao.findByNamedQuery("PRODUCT.BY.SEO.URI", seoUri);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getProductId();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public Long getProductSkuIdBySeoUri(final String seoUri) {
        List<ProductSku> list = productSkuService.getGenericDao().findByNamedQuery("SKU.BY.SEO.URI", seoUri);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getSkuId();
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = PROD_SERV_METHOD_CACHE)
    public int getProductQty(final long categoryId) {
        return Integer.valueOf(
                String.valueOf(productDao.getScalarResultByNamedQuery("PRODUCTS.QTY.BY.CATEGORYID", categoryId, new Date())));  //todo time machine
    }


    /**
     * {@inheritDoc}
     */
    public int reindexProducts() {
        return productDao.fullTextSearchReindex();
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProduct(final Long pk) {
        return productDao.fullTextSearchReindex(pk);
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

}

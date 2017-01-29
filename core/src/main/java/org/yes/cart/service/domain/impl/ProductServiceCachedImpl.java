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

import org.apache.lucene.search.Query;
import org.hibernate.criterion.Criterion;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.GenericFullTextSearchCapableDAO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.domain.queryobject.FilteredNavigationRecordRequest;
import org.yes.cart.service.domain.ProductService;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 17:42
 */
public class ProductServiceCachedImpl implements ProductService {

    private final ProductService productService;

    public ProductServiceCachedImpl(final ProductService productService) {
        this.productService = productService;
    }

    /** {@inheritDoc} */
    public ProductSku getSkuById(final Long skuId) {
        return productService.getSkuById(skuId);
    }

    /** {@inheritDoc} */
    @Cacheable(value = "productService-skuById")
    public ProductSku getSkuById(final Long skuId, final boolean withAttributes) {
        return productService.getSkuById(skuId, withAttributes);
    }


    /**
     * Get default image file name by given product.
     *
     * @param productId given id, which identify product
     * @return image file name if found.
     */
    @Cacheable(value = "productService-defaultImage")
    public String getDefaultImage(final Long productId) {
        return productService.getDefaultImage(productId);
    }


    /**
     * {@inheritDoc}
     */
    public List<Product> getProductByCategory(final long categoryId) {
        return productService.getProductByCategory(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-randomProductByCategory"/*, key = "category.getCategoryId()"*/)
    public Product getRandomProductByCategory(final Category category) {
        return productService.getRandomProductByCategory(category);
    }



    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productAttributes")
    public Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> getProductAttributes(
            final String locale, final long productId, final long skuId, final long productTypeId) {
        return productService.getProductAttributes(locale, productId, skuId, productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    public Map<Pair<String, String>, Map<Pair<String, String>, Map<String, List<Pair<String, String>>>>> getCompareAttributes(final String locale,
                                                                                                                              final List<Long> productId,
                                                                                                                              final List<Long> skuId) {
        return productService.getCompareAttributes(locale, productId, skuId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productAttribute")
    public Pair<String, String> getProductAttribute(final String locale, final long productId, final long skuId, final String attributeCode) {
        return productService.getProductAttribute(locale, productId, skuId, attributeCode);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-allProductsAttributeValues")
    public Map<Long, String> getAllProductsAttributeValues(final String attributeCode) {
        return productService.getAllProductsAttributeValues(attributeCode);
    }

    /**
     * {@inheritDoc}
     */
    public ProductSku getProductSkuByCode(final String skuCode) {
        return productService.getProductSkuByCode(skuCode);
    }

    /**
     * Get product by sku code.
     *
     * @param skuCode sku code
     * @return product sku for this sku code
     */
    @Cacheable(value = "productService-productBySkuCode")
    public Product getProductBySkuCode(final String skuCode) {
        return productService.getProductBySkuCode(skuCode);
    }


    /**
     * {@inheritDoc}
     */
    public Product getProductById(final Long productId) {
        return productService.getProductById(productId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productById")
    public Product getProductById(final Long productId, final boolean withAttribute) {
        return productService.getProductById(productId, withAttribute);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productSearchResultDTOByQuery")
    public ProductSearchResultPageDTO getProductSearchResultDTOByQuery(final Query query, final int firstResult, final int maxResults, final String sortFieldName, final boolean reverse) {
        return productService.getProductSearchResultDTOByQuery(query, firstResult, maxResults, sortFieldName, reverse);
    }


    /**
     * {@inheritDoc}
     */
    public Map<String, List<Pair<String, Integer>>> findFilteredNavigationRecords(final Query baseQuery, final List<FilteredNavigationRecordRequest> request) {
        return productService.findFilteredNavigationRecords(baseQuery, request);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productQtyByQuery")
    public int getProductQty(final Query query) {
        return productService.getProductQty(query);
    }


    /**
     * {@inheritDoc}
     */
    public Pair<Integer, Integer> getProductQtyAll() {
        return productService.getProductQtyAll();
    }

    /**
     * {@inheritDoc}
     */
    public List<Product> getProductByCategory(final long categoryId,
                                              final int firstResult,
                                              final int maxResults) {
        return productService.getProductByCategory(categoryId, firstResult, maxResults);
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productByIdList")
    public List<Product> getProductByIdList(final List idList) {
        return productService.getProductByIdList(idList);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-distinctBrands")
    public List<FilteredNavigationRecord> getDistinctBrands(final String locale) {
        return productService.getDistinctBrands(locale);
    }


    /**
     * Get the ranked by ProductTypeAttr.rank list of unique product attribute values by given product type
     * and attribute code.
     *
     * @param locale        locale
     * @param productTypeId product type id
     * @return list of distinct attrib values
     */
    @Cacheable(value = "productService-distinctAttributeValues")
    public List<FilteredNavigationRecord> getDistinctAttributeValues(final String locale, final long productTypeId) {
        return productService.getDistinctAttributeValues(locale, productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    public Long findProductIdBySeoUri(final String seoUri) {
        return productService.findProductIdBySeoUri(seoUri);
    }

    /**
     * {@inheritDoc}
     */
    public Long findProductIdByGUID(final String guid) {
        return productService.findProductIdByGUID(guid);
    }


    /**
     * {@inheritDoc}
     */
    public Long findProductIdByCode(final String code) {
        return productService.findProductIdByCode(code);
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> findProductIdsByManufacturerCode(final String code) {
        return productService.findProductIdsByManufacturerCode(code);
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> findProductIdsByBarCode(final String code) {
        return productService.findProductIdsByBarCode(code);
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> findProductIdsByPimCode(final String code) {
        return productService.findProductIdsByPimCode(code);
    }

    /**
     * {@inheritDoc}
     */
    public List<Long> findProductIdsByAttributeValue(final String attrCode, final String attrValue) {
        return productService.findProductIdsByAttributeValue(attrCode, attrValue);
    }

    /**
     * {@inheritDoc}
     */
    public String findSeoUriByProductId(final Long productId) {
        return productService.findSeoUriByProductId(productId);
    }

    /**
     * {@inheritDoc}
     */
    public Long findProductSkuIdBySeoUri(final String seoUri) {
        return productService.findProductSkuIdBySeoUri(seoUri);
    }

    /**
     * {@inheritDoc}
     */
    public Long findProductSkuIdByGUID(final String guid) {
        return productService.findProductSkuIdByGUID(guid);
    }

    /**
     * {@inheritDoc}
     */
    public Long findProductSkuIdByCode(final String code) {
        return productService.findProductSkuIdByCode(code);
    }

    /**
     * {@inheritDoc}
     */
    public String findSeoUriByProductSkuId(final Long skuId) {
        return productService.findSeoUriByProductSkuId(skuId);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productService-productQtyByCategoryId")
    public int getProductQty(final long categoryId) {
        return productService.getProductQty(categoryId);
    }


    /**
     * {@inheritDoc}
     */
    public GenericFullTextSearchCapableDAO.FTIndexState getProductsFullTextIndexState() {
        return productService.getProductsFullTextIndexState();
    }

    /**
     * {@inheritDoc}
     */
    public GenericFullTextSearchCapableDAO.FTIndexState getProductsSkuFullTextIndexState() {
        return productService.getProductsSkuFullTextIndexState();
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProducts(final int batchSize) {
        productService.reindexProducts(batchSize);
    }


    /**
     * {@inheritDoc}
     */
    public void reindexProducts(final int batchSize, final boolean async) {
        productService.reindexProducts(batchSize, async);
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProductsSku(final int batchSize) {
        productService.reindexProductsSku(batchSize);
    }


    /**
     * {@inheritDoc}
     */
    public void reindexProductsSku(final int batchSize, final boolean async) {
        productService.reindexProductsSku(batchSize, async);
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProducts(final Long shopId, final int batchSize) {
        productService.reindexProducts(shopId, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProductsSku(final Long shopId, final int batchSize) {
        productService.reindexProductsSku(shopId, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProduct(final Long pk) {
        productService.reindexProduct(pk);
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProductSku(final Long pk) {
        productService.reindexProductSku(pk);
    }

    /**
     * {@inheritDoc}
     */
    public void reindexProductSku(final String code) {
        productService.reindexProductSku(code);
    }


    /**
     * {@inheritDoc}
     */
    public List<Product> getProductByCodeNameBrandType(final CriteriaTuner criteriaTuner,
                                                       final String code,
                                                       final String name,
                                                       final Long brandId,
                                                       final Long productTypeId) {
        return productService.getProductByCodeNameBrandType(criteriaTuner, code, name, brandId, productTypeId);
    }


    /**
     * {@inheritDoc}
     */
    public List<Product> findAll() {
        return productService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public Product findById(final long pk) {
        return productService.findById(pk);
    }

    /**
     * Persist product. Default sku will be created.
     *
     * @param instance instance to persist
     * @return persisted instanse
     */
    @CacheEvict(value ={
            "productService-randomProductByCategory",
            "productService-productByQuery",
            "productService-productSearchResultDTOByQuery",
            "productService-productQtyByQuery",
            "productService-productByIdList",
            "productService-distinctAttributeValues",
            "productService-distinctBrands",
            "productService-productQtyByCategoryId"

    }, allEntries = true)
    public Product create(final Product instance) {
        return productService.create(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value ={
            "productService-skuById",
            "productService-defaultImage",
            "productService-randomProductByCategory",
            "productService-productAttributes",
            "productService-productAttribute",
            "productService-allProductsAttributeValues",
            "productService-productAssociationsIds",
            "productService-featuredProducts",
            "productService-newProducts",
            "productService-taggedProducts",
            "productService-productBySkuCode",
            "productService-productById",
            "productService-productByQuery",
            "productService-productSearchResultDTOByQuery",
            "productService-productQtyByQuery",
            "productService-distinctAttributeValues",
            "productService-distinctBrands",
            "productService-productByIdList",
            "productService-productQtyByCategoryId"

    }, allEntries = true)
    public Product update(final Product instance) {
        return productService.update(instance);
    }

    /** {@inheritDoc} */
    @CacheEvict(value ={
            "productService-skuById",
            "productService-defaultImage",
            "productService-randomProductByCategory",
            "productService-productAttributes",
            "productService-productAttribute",
            "productService-allProductsAttributeValues",
            "productService-productAssociationsIds",
            "productService-featuredProducts",
            "productService-newProducts",
            "productService-taggedProducts",
            "productService-productBySkuCode",
            "productService-productById",
            "productService-productByQuery",
            "productService-productSearchResultDTOByQuery",
            "productService-productQtyByQuery",
            "productService-distinctAttributeValues",
            "productService-distinctBrands",
            "productService-productByIdList",
            "productService-productQtyByCategoryId"

    }, allEntries = true)
    public void delete(final Product instance) {
        productService.delete(instance);
    }

    /** {@inheritDoc} */
    public List<Product> findByCriteria(final Criterion... criterion) {
        return productService.findByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final Criterion... criterion) {
        return productService.findCountByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public int findCountByCriteria(final CriteriaTuner criteriaTuner, final Criterion... criterion) {
        return productService.findCountByCriteria(criteriaTuner, criterion);
    }

    /** {@inheritDoc} */
    public Product findSingleByCriteria(final Criterion... criterion) {
        return productService.findSingleByCriteria(criterion);
    }

    /** {@inheritDoc} */
    public GenericDAO<Product, Long> getGenericDao() {
        return productService.getGenericDao();
    }

}

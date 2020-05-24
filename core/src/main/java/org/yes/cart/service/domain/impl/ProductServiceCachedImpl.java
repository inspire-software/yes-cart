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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.dto.ProductSearchResultNavDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductAttributesModel;
import org.yes.cart.domain.entity.ProductCompareModel;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.IndexBuilder;
import org.yes.cart.search.dto.FilteredNavigationRecordRequest;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.service.domain.ProductService;

import java.util.Collection;
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
    @Override
    public ProductSku getSkuById(final Long skuId) {
        return productService.getSkuById(skuId);
    }

    /** {@inheritDoc} */
    @Override
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
    @Override
    @Cacheable(value = "productService-defaultImage")
    public String getDefaultImage(final Long productId) {
        return productService.getDefaultImage(productId);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productService-productAttributes")
    public ProductAttributesModel getProductAttributes(final long productId, final long skuId, final long productTypeId) {
        return productService.getProductAttributes(productId, skuId, productTypeId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductCompareModel getCompareAttributes(final List<Long> productId,
                                                    final List<Long> skuId) {
        return productService.getCompareAttributes(productId, skuId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productService-allProductsAttributeValues")
    public Map<Long, String> getAllProductsAttributeValues(final String attributeCode) {
        return productService.getAllProductsAttributeValues(attributeCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSku getProductSkuByCode(final String skuCode) {
        return productService.getProductSkuByCode(skuCode);
    }

    /**
     * Get product by sku code.
     *
     * @param skuCode sku code
     * @return product sku for this sku code
     */
    @Override
    @Cacheable(value = "productService-productBySkuCode")
    public Product getProductBySkuCode(final String skuCode) {
        return productService.getProductBySkuCode(skuCode);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Product getProductById(final Long productId) {
        return productService.getProductById(productId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productService-productById")
    public Product getProductById(final Long productId, final boolean withAttribute) {
        return productService.getProductById(productId, withAttribute);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productService-productSearchResultDTOByQuery")
    public ProductSearchResultPageDTO getProductSearchResultDTOByQuery(final NavigationContext navigationContext, final int firstResult, final int maxResults, final String sortFieldName, final boolean reverse) {
        return productService.getProductSearchResultDTOByQuery(navigationContext, firstResult, maxResults, sortFieldName, reverse);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSearchResultNavDTO findFilteredNavigationRecords(final NavigationContext baseNavigationContext, final List<FilteredNavigationRecordRequest> request) {
        return productService.findFilteredNavigationRecords(baseNavigationContext, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productService-productQtyByQuery")
    public int getProductQty(final NavigationContext navigationContext) {
        return productService.getProductQty(navigationContext);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Integer, Integer> findProductQtyAll() {
        return productService.findProductQtyAll();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "productService-productByIdList")
    public List<Product> getProductByIdList(final List idList) {
        return productService.getProductByIdList(idList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findProductIdBySeoUri(final String seoUri) {
        return productService.findProductIdBySeoUri(seoUri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findProductIdByGUID(final String guid) {
        return productService.findProductIdByGUID(guid);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Long findProductIdByCode(final String code) {
        return productService.findProductIdByCode(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findProductIdsByManufacturerCode(final String code) {
        return productService.findProductIdsByManufacturerCode(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findProductIdsByBarCode(final String code) {
        return productService.findProductIdsByBarCode(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findProductIdsByBarCodes(final Collection<String> codes) {
        return productService.findProductIdsByBarCodes(codes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findProductIdsByPimCode(final String code) {
        return productService.findProductIdsByPimCode(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findProductIdsByAttributeValue(final String attrCode, final String attrValue) {
        return productService.findProductIdsByAttributeValue(attrCode, attrValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findSeoUriByProductId(final Long productId) {
        return productService.findSeoUriByProductId(productId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findProductSkuIdBySeoUri(final String seoUri) {
        return productService.findProductSkuIdBySeoUri(seoUri);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findProductSkuIdByGUID(final String guid) {
        return productService.findProductSkuIdByGUID(guid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findProductSkuIdByCode(final String code) {
        return productService.findProductSkuIdByCode(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findSeoUriByProductSkuId(final Long skuId) {
        return productService.findSeoUriByProductSkuId(skuId);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public IndexBuilder.FTIndexState getProductsFullTextIndexState() {
        return productService.getProductsFullTextIndexState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexBuilder.FTIndexState getProductsSkuFullTextIndexState() {
        return productService.getProductsSkuFullTextIndexState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProducts(final int batchSize) {
        productService.reindexProducts(batchSize);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProducts(final int batchSize, final boolean async) {
        productService.reindexProducts(batchSize, async);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductsSku(final int batchSize) {
        productService.reindexProductsSku(batchSize);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductsSku(final int batchSize, final boolean async) {
        productService.reindexProductsSku(batchSize, async);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProducts(final Long shopId, final int batchSize) {
        productService.reindexProducts(shopId, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductsSku(final Long shopId, final int batchSize) {
        productService.reindexProductsSku(shopId, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProduct(final Long pk) {
        productService.reindexProduct(pk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductSku(final Long pk) {
        productService.reindexProductSku(pk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductSku(final String code) {
        productService.reindexProductSku(code);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findProductByCodeNameBrandType(final String code,
                                                        final String name,
                                                        final Long brandId,
                                                        final Long productTypeId) {
        return productService.findProductByCodeNameBrandType(code, name, brandId, productTypeId);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findProductSupplierCatalogCodes() {
        return productService.findProductSupplierCatalogCodes();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findProducts(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {
        return productService.findProducts(start, offset, sort, sortDescending, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findProductCount(final Map<String, List> filter) {
        return productService.findProductCount(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findAll() {
        return productService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<Product> callback) {
        productService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<Product> callback) {
        productService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product findById(final long pk) {
        return productService.findById(pk);
    }

    /**
     * Persist product. Default sku will be created.
     *
     * @param instance instance to persist
     * @return persisted instanse
     */
    @Override
    @CacheEvict(value ={
            "productService-productByQuery",
            "productService-productSearchResultDTOByQuery",
            "productService-productQtyByQuery",
            "productService-productByIdList"

    }, allEntries = true)
    public Product create(final Product instance) {
        return productService.create(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value ={
            "productService-skuById",
            "productService-defaultImage",
            "productService-productAttributes",
            "productService-allProductsAttributeValues",
            "productService-productCategoriesIds",
            "productService-productAssociationsIds",
            "productService-productAssociations",
            "productService-featuredProducts",
            "productService-newProducts",
            "productService-taggedProducts",
            "productService-productBySkuCode",
            "productService-productById",
            "productService-productByQuery",
            "productService-productSearchResultDTOByQuery",
            "productService-productQtyByQuery",
            "productService-productByIdList"

    }, allEntries = true)
    public Product update(final Product instance) {
        return productService.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value ={
            "productService-skuById",
            "productService-defaultImage",
            "productService-productAttributes",
            "productService-allProductsAttributeValues",
            "productService-productCategoriesIds",
            "productService-productAssociationsIds",
            "productService-productAssociations",
            "productService-featuredProducts",
            "productService-newProducts",
            "productService-taggedProducts",
            "productService-productBySkuCode",
            "productService-productById",
            "productService-productByQuery",
            "productService-productSearchResultDTOByQuery",
            "productService-productQtyByQuery",
            "productService-productByIdList"

    }, allEntries = true)
    public void delete(final Product instance) {
        productService.delete(instance);
    }

    /** {@inheritDoc} */
    @Override
    public List<Product> findByCriteria(final String eCriteria, final Object... parameters) {
        return productService.findByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return productService.findCountByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public Product findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return productService.findSingleByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public GenericDAO<Product, Long> getGenericDao() {
        return productService.getGenericDao();
    }

}

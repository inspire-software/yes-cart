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

package org.yes.cart.service.domain;

import org.apache.lucene.search.Query;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ProductService extends GenericService<Product> {

    /**
     * Get the all products in category
     *
     * @param categoryId category id
     * @return list of products
     */
    List<Product> getProductByCategory(long categoryId);

    /**
     * Get random product from category
     *
     * @param category category id
     * @return random product.
     */
    Product getRandomProductByCategory(Category category);


    /**
     * Get product sku by his id
     *
     * @param skuId given sku id
     * @return product sku
     */
    ProductSku getSkuById(Long skuId);

    /**
     * Get product sku by his id
     *
     * @param skuId given sku id
     * @param withAttributes with attributes
     * @return product sku
     */
    ProductSku getSkuById(Long skuId, boolean withAttributes);

    /**
     * Get the grouped product attributes, with values. The result can be represented in following form:
     * Shipment details:
     * weight: 17 Kg
     * length: 15 Cm
     * height: 20 Cm
     * width: 35 Cm
     * Power:
     * Charger: 200/110
     * Battery type: Lithium
     *
     * So the hierarchy returned for the above example will be:
     * Map
     *    Entry[1001, Shipment details] =>
     *      Map
     *          Entry [10010, weight] =>
     *              List
     *                  [100001, 17 Kg]
     *          Entry [10011, length] =>
     *              List
     *                  [100002, 15 cm]
     *  ... etc
     *
     *  If this is SKU then it should inherit the attributes of the product,
     *  If this is just product then we only display product attributes
     *
     * @param locale locale
     * @param productId  product ID
     * @param skuId sku ID
     * @param productTypeId product type id
     * @return hierarchy of attributes for this product or sku.
     */
    Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> getProductAttributes(
            String locale, long productId, long skuId, long productTypeId);

    /**
     *
     * @param locale locale
     * @param productId product id
     * @param skuId sku id
     * @param attributeCode code
     * @return raw and display value pair
     */
    Pair<String, String> getProductAttribute(
            String locale, long productId, long skuId, String attributeCode);

    /**
     * @param attributeCode code
     * @return raw and display value pair
     */
    Map<Long, String> getAllProductsAttributeValues(String attributeCode);

    /**
     * Get product by his primary key value
     *
     * @param productId product id
     * @return product if found, otherwise null
     */
    Product getProductById(Long productId);

    /**
     * Get product by his primary key value
     *
     * @param productId product id
     * @param withAttribute flag if need to load product with attributes
     * @return product if found, otherwise null
     */
    Product getProductById(Long productId, boolean withAttribute);

    /**
     * Get the all products in category.
     *
     * @param categoryId  category id
     * @param firtsResult index of first result
     * @param maxResults  quantity results to return
     * @return list of products
     */
    List<Product> getProductByCategory(
            long categoryId,
            int firtsResult,
            int maxResults);

    /**
     * Get the list of unique attribute values by given product type
     * and attribute code.
     *
     * @param productTypeId product type id
     * @param code          attribute code
     * @return list of distinct attib values
     */
    List<Object> getDistinctAttributeValues(long productTypeId, String code);

    /**
     * Get list of products by id list.
     * @param idList given list of id.
     * @return list of product, that satisfy given list of ids.
     */
    List<Product> getProductByIdList(List idList);

    /**
     * Get the ranked by ProductTypeAttr.rank list of unique product attribute values by given product type
     * and attribute code.
     *
     * @param locale locale
     * @param productTypeId product type id
     * @return list of distinct attrib values
     */
    List<FilteredNavigationRecord> getDistinctAttributeValues(String locale, long productTypeId);

    /**
     * Get all distinct brands in given categories list
     *
     * @param locale locale
     * @param categories categories id list
     * @return list of distinct brands
     */
    List<FilteredNavigationRecord> getDistinctBrands(String locale, List categories);


    /**
     * Get the quantity of products in particular category.
     *
     * @param categoryId category id
     * @return quantity of products
     */
    int getProductQty(long categoryId);

    /**
     * Get the all products , that match the given query
     *
     * @param query         lucene query
     * @param firstResult   index of first result
     * @param maxResults    quantity results to return
     * @param sortFieldName sort field name
     * @param reverse       reverse the search result if true
     * @return list of products
     */
    List<ProductSearchResultDTO> getProductSearchResultDTOByQuery(
            Query query,
            int firstResult,
            int maxResults,
            String sortFieldName,
            boolean reverse);

    /**
     * Get the quantity of products in particular category.
     *
     * @param query lucene query
     * @return quantity of products
     */
    int getProductQty(Query query);

    /**
     * Reindex the products.
     *
     * @return document quantity in index
     */
    int reindexProducts();

    /**
     * Reindex the products.
     *
     * @param pk the product primary key
     * @return document quantity in index
     */
    int reindexProduct(Long pk);

    /**
     * Reindex the products.
     *
     * @param pk the product primary key
     * @return document quantity in index
     */
    int reindexProductSku(Long pk);

    /**
     * Reindex the products.
     *
     * @param code the product SKU code
     * @return document quantity in index
     */
    int reindexProductSku(String code);


    /**
     * Get product sku by code.
     *
     * @param skuCode sku code
     * @return product sku for this sku code
     */
    ProductSku getProductSkuByCode(String skuCode);

    /**
     * Get product by sku code.
     *
     * @param skuCode sku code
     * @return product sku for this sku code
     */
    Product getProductBySkuCode(String skuCode);


    /**
     * Get product id id by given seo uri
     *
     * @param seoUri given seo uri
     * @return product id if found otherwise null
     */
    Long getProductIdBySeoUri(String seoUri);

    /**
     * Get product SEO uri id by given id
     *
     * @param productId given product id
     * @return product seo uri if found otherwise null
     */
    String getSeoUriByProductId(Long productId);

    /**
     * Get product sku id id by given seo uri
     *
     * @param seoUri given seo uri
     * @return product sku id if found otherwise null
     */
    Long getProductSkuIdBySeoUri(String seoUri);

    /**
     * Get product sku SEO uri by given id
     *
     * @param skuId given sku id
     * @return product sku uri if found otherwise null
     */
    String getSeoUriByProductSkuId(Long skuId);

    /**
     * Clear empty product attributes, that can appear after bulk import.
     */
    void clearEmptyAttributes();



    /**
     * Find product by given optional filtering criteria.
     *
     * @param criteriaTuner criteria tuner
     * @param code          product code.  use like %%
     * @param name          product name.  use like %%
     * @param brandId       brand id. use exact match
     * @param productTypeId product type id. use exact match
     * @return list of founded products
     */
     List<Product> getProductByCodeNameBrandType(
            CriteriaTuner criteriaTuner,
            String code,
            String name,
            Long brandId,
            Long productTypeId);


    /**
     * Get default image file name by given product.
     * @param productId   given id, which identify product
     * @return image file name if found.
     */
     String getDefaultImage( Long productId);

}

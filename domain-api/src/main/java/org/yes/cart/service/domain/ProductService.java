/*
 * Copyright 2009 Inspire-Software.com
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
     * Get product sku by his id
     *
     * @param skuId given sku id
     *
     * @return product sku
     */
    ProductSku getSkuById(Long skuId);

    /**
     * Get product sku by his id
     *
     * @param skuId given sku id
     * @param withAttributes with attributes
     *
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
     * @param productId  product ID
     * @param skuId sku ID
     * @param productTypeId product type id
     *
     * @return hierarchy of attributes for this product or sku.
     */
    ProductAttributesModel getProductAttributes(long productId,
                                                long skuId,
                                                long productTypeId);


    /**
     * Get the grouped product attributes, with values. The result can be represented in following form:
     *
     *                   Prod A    SKU B    Prod C
     * Shipment details:
     * weight:            17 Kg    15kg      14kg
     * length:            15 Cm    15 Cm     15 Cm
     * height:            20 Cm    20 Cm     20 Cm
     * width:             35 Cm    35 Cm     35 Cm
     * Power:
     * Charger:           200/110            200/115
     * Battery type:      Lithium  Lithium
     *
     * So the hierarchy returned for the above example will be:
     * Map
     *    Entry[1001, Shipment details] =>
     *      Map
     *          Entry [10010, weight] =>
     *               Map
     *                  Entry[ p_10001 =>
     *                      List
     *                         [100001, 17 Kg]
     *                  ]
     *                  Entry[ s_10001 =>
     *                      List
     *                         [100001, 15 Kg]
     *                  ]
     *                  Entry[ p_10002 =>
     *                      List
     *                         [100001, 14 Kg]
     *                  ]
     *  ... etc
     *
     *  If this is SKU then it should inherit the attributes of the product,
     *  If this is just product then we only display product attributes
     *
     * @param productId  product ID
     * @param skuId sku ID
     *
     * @return hierarchy of attributes for this product or sku.
     */
    ProductCompareModel getCompareAttributes(List<Long> productId,
                                             List<Long> skuId);


    /**
     * @param attributeCode code
     *
     * @return raw and display value pair
     */
    Map<Long, String> getAllProductsAttributeValues(String attributeCode);

    /**
     * Get product by his primary key value
     *
     * @param productId product id
     *
     * @return product if found, otherwise null
     */
    Product getProductById(Long productId);

    /**
     * Get product by his primary key value
     *
     * @param productId product id
     * @param withAttribute flag if need to load product with attributes
     *
     * @return product if found, otherwise null
     */
    Product getProductById(Long productId, boolean withAttribute);

    /**
     * Get list of products by id list.
     *
     * @param idList given list of id.
     *
     * @return list of product, that satisfy given list of ids.
     */
    List<Product> getProductByIdList(List idList);

    /**
     * Get the all products , that match the given query
     *
     * @param navigationContext navigation context
     * @param firstResult       index of first result
     * @param maxResults        quantity results to return
     * @param sortFieldName     sort field name
     * @param reverse           reverse the search result if true
     *
     * @return list of products
     */
    ProductSearchResultPageDTO getProductSearchResultDTOByQuery(NavigationContext navigationContext,
                                                                int firstResult,
                                                                int maxResults,
                                                                String sortFieldName,
                                                                boolean reverse);

    /**
     * Create filter navigation records counts.
     *
     * @param baseNavigationContext base navigation context
     * @param request               request for filtered navigation
     *
     * @return list of facets with values and their counts
     */
    ProductSearchResultNavDTO findFilteredNavigationRecords(NavigationContext baseNavigationContext,
                                                            List<FilteredNavigationRecordRequest> request);

    /**
     * Get the quantity of products in particular category.
     *
     * @param navigationContext navigation context
     *
     * @return quantity of products
     */
    int getProductQty(NavigationContext navigationContext);

    /**
     * Full count of products on the system.
     *
     * @return total and active
     */
    Pair<Integer, Integer> findProductQtyAll();


    /**
     * @return state of full text index.
     */
    IndexBuilder.FTIndexState getProductsFullTextIndexState();

    /**
     * @return state of full text index.
     */
    IndexBuilder.FTIndexState getProductsSkuFullTextIndexState();

    /**
     * Reindex the products.
     *
     * @param batchSize batch size for re-indexing
     */
    void reindexProducts(int batchSize);

    /**
     * Reindex the products.
     *
     * @param batchSize batch size for re-indexing
     * @param async asynchronous
     */
    void reindexProducts(int batchSize, boolean async);

    /**
     * Reindex the products.
     *
     * @param batchSize batch size for re-indexing
     */
    void reindexProductsSku(int batchSize);

    /**
     * Reindex the products.
     *
     * @param batchSize batch size for re-indexing
     * @param async asynchronous
     */
    void reindexProductsSku(int batchSize, boolean async);

    /**
     * Reindex the products.
     *
     * @param shopId shop for which to reindex products.
     * @param batchSize batch size for re-indexing
     */
    void reindexProducts(Long shopId, int batchSize);

    /**
     * Reindex the products.
     *
     * @param shopId shop for which to reindex products.
     * @param batchSize batch size for re-indexing
     */
    void reindexProductsSku(Long shopId, int batchSize);

    /**
     * Reindex the products.
     *
     * @param pk the product primary key
     */
    void reindexProduct(Long pk);

    /**
     * Reindex the products.
     *
     * @param pk the product primary key
     */
    void reindexProductSku(Long pk);

    /**
     * Reindex the products.
     *
     * @param code the product SKU code
     */
    void reindexProductSku(String code);


    /**
     * Get product sku by code.
     *
     * @param skuCode sku code
     *
     * @return product sku for this sku code
     */
    ProductSku getProductSkuByCode(String skuCode);

    /**
     * Get product by sku code.
     *
     * @param skuCode sku code
     *
     * @return product sku for this sku code
     */
    Product getProductBySkuCode(String skuCode);


    /**
     * Get product id by given seo uri
     *
     * @param seoUri given seo uri
     *
     * @return product id if found otherwise null
     */
    Long findProductIdBySeoUri(String seoUri);

    /**
     * Get product id by given GUID
     *
     * @param guid given GUID
     *
     * @return product id if found otherwise null
     */
    Long findProductIdByGUID(String guid);

    /**
     * Get product id by given code
     *
     * @param code given code
     *
     * @return product id if found otherwise null
     */
    Long findProductIdByCode(String code);

    /**
     * Get product id by given code
     *
     * @param code given manufacturer code
     *
     * @return product id if found otherwise null
     */
    List<Long> findProductIdsByManufacturerCode(String code);

    /**
     * Get product id by given code
     *
     * @param code given barcode (EAN/UPC)
     *
     * @return product id if found otherwise null
     */
    List<Long> findProductIdsByBarCode(String code);

    /**
     * Get product id by given code
     *
     * @param codes given barcode (EAN/UPC)
     *
     * @return product id if found otherwise null
     */
    List<Long> findProductIdsByBarCodes(Collection<String> codes);

    /**
     * Get product id by given code
     *
     * @param code given code in PIM
     *
     * @return product id if found otherwise null
     */
    List<Long> findProductIdsByPimCode(String code);


    /**
     * Get product id by given code
     *
     * @param attrCode attribute code
     * @param attrValue attribute value
     *
     * @return product id if found otherwise null
     */
    List<Long> findProductIdsByAttributeValue(String attrCode, String attrValue);

    /**
     * Get product SEO uri id by given id
     *
     * @param productId given product id
     *
     * @return product seo uri if found otherwise null
     */
    String findSeoUriByProductId(Long productId);

    /**
     * Get product sku id by given seo uri
     *
     * @param seoUri given seo uri
     *
     * @return product sku id if found otherwise null
     */
    Long findProductSkuIdBySeoUri(String seoUri);

    /**
     * Get product sku id by given GUID
     *
     * @param guid given GUID
     *
     * @return product sku id if found otherwise null
     */
    Long findProductSkuIdByGUID(String guid);

    /**
     * Get product sku id by given code
     *
     * @param code given code
     *
     * @return product sku id if found otherwise null
     */
    Long findProductSkuIdByCode(String code);

    /**
     * Get product sku SEO uri by given id
     *
     * @param skuId given sku id
     *
     * @return product sku uri if found otherwise null
     */
    String findSeoUriByProductSkuId(Long skuId);



    /**
     * Find product by given optional filtering criteria.
     *
     * @param code          product code.  use like %%
     * @param name          product name.  use like %%
     * @param brandId       brand id. use exact match
     * @param productTypeId product type id. use exact match
     *
     * @return list of founded products
     */
     List<Product> findProductByCodeNameBrandType(String code,
                                                  String name,
                                                  Long brandId,
                                                  Long productTypeId);

    /**
     * Get list of unique supplier catalog codes that exist.
     *
     * @return list of unique codes
     */
     List<String> findProductSupplierCatalogCodes();

    /**
     * Get default image file name by given product.
     *
     * @param productId   given id, which identify product
     *
     * @return image file name if found.
     */
     String getDefaultImage(Long productId);


    /**
     * Find products by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return list of products.
     */
    List<Product> findProducts(int start,
                               int offset,
                               String sort,
                               boolean sortDescending,
                               Map<String, List> filter);

    /**
     * Find products by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return count
     */
    int findProductCount(Map<String, List> filter);


}

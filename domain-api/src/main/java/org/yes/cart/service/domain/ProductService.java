package org.yes.cart.service.domain;

import org.apache.lucene.search.Query;
import org.yes.cart.dao.CriteriaTuner;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.queryobject.FiteredNavigationRecord;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

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
     * Get product by his id.
     *
     * @param productId product id
     * @return product
     */
    Product getById(Long productId);

    /**
     * Get product sku by his id
     *
     * @param skuId given sku id
     * @return product sku
     */
    ProductSku getSkuById(Long skuId);

    /**
     * Get the grouped product attributes, with values. The result can be represented in following form:
     * Shippment details:
     * weight: 17 Kg
     * lenght: 15 Cm
     * height: 20 Cm
     * width: 35 Cm
     * Power:
     * Charger: 200/110
     * Battery type: Litium
     *
     * @param attributable  product  or sku
     * @param productTypeId product type id
     * @return List of pair group names - list of attribute name and value.
     */
    List<Pair<String, List<AttrValue>>> getProductAttributes(Attributable attributable, long productTypeId);

    /**
     * Get product by his primary key value
     *
     * @param productId product id
     * @return product if found, otherwise null
     */
    Product getProductById(Long productId);

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
     * Get new arrivals in category.
     * @param categoryId  given category id
     * @param maxResults  max result
     * @return  list of new arrived products
     */
    List<Product> getNewArrivalsProductInCategory(
            long categoryId,
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
     * @param productTypeId product type id
     * @return list of distinct attib values
     */
    List<FiteredNavigationRecord> getDistinctAttributeValues(long productTypeId);

    /**
     * Get all distinct brands in given categories list
     *
     * @param categories categories id list
     * @return list of distinct brands
     */
    List<FiteredNavigationRecord> getDistinctBrands(List categories);


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
     * @param firtsResult   index of first result
     * @param maxResults    quantity results to return
     * @param sortFieldName sort field name
     * @param reverse       reverce the search result if true
     * @return list of products
     */
    List<Product> getProductByQuery(
            Query query,
            int firtsResult,
            int maxResults,
            String sortFieldName,
            boolean reverse);

    /**
     * Get the all products , that match the given query
     *
     * @param query       lucene query
     * @param firtsResult index of first result
     * @param maxResults  quantity results to return
     * @return list of products
     */
    List<Product> getProductByQuery(
            Query query,
            int firtsResult,
            int maxResults);

    /**
     * Get all available products, that marked as featured . Need to be carefull to mark product as featured and keep
     * quantity of featured products limited.
     *
     * @param categories current shop categories
     * @param limit limit of products to return.
     * @return shuffled list of featured products.
     */
    List<Product> getFeaturedProducts(Collection categories, int limit);

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
     * Get product sku id id by given seo uri
     *
     * @param seoUri given seo uri
     * @return product sku id if found otherwise null
     */
    Long getProductSkuIdBySeoUri(String seoUri);


    /**
     * Get the total quantity of product skus on all warehouses.
     *
     * @param product product
     * @return quantity of product.
     */
    BigDecimal getProductQuantity(Product product);

    /**
     * Get the total quantity of product skus on warehouses that belong to given shop.
     * In multishop enviroment some product has 0 quantity at one shop and non 0 quantity at another,
     * this is can be used when shop owner want to getByKey more money from this situation.
     *
     * @param shop    {@link Shop} given shop.
     * @param product product
     * @return quantity of product.
     */
    BigDecimal getProductQuantity(Product product, Shop shop);

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
    public List<Product> getProductByConeNameBrandType(
            CriteriaTuner criteriaTuner,
            String code,
            String name,
            Long brandId,
            Long productTypeId);

}

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

import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.search.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.dto.impl.ProductSkuSearchResultDTOImpl;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.ProductSkuService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductSkuServiceImpl extends BaseGenericServiceImpl<ProductSku> implements ProductSkuService {


    private final GenericDAO<Product, Long> productDao;
    private final GenericDAO<SkuPrice, Long> skuPriceDao;


    /**
     * Construct  service.
     * @param productSkuDao sku dao
     * @param productDao    product dao
     */
    public ProductSkuServiceImpl(final GenericDAO<ProductSku, Long> productSkuDao,
                                 final GenericDAO<Product, Long> productDao,
                                 final GenericDAO<SkuPrice, Long> skuPriceDao) {
        super(productSkuDao);
        this.productDao = productDao;
        this.skuPriceDao = skuPriceDao;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ProductSku> getAllProductSkus(final long productId) {
        final Product product = productDao.findById(productId);
        return product.getSku();
    }

    /**
     * {@inheritDoc}
     */
    public ProductSku findProductSkuBySkuCode(final String skuCode) {
        return getGenericDao().findSingleByCriteria(
                Restrictions.eq("code", skuCode)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productSkuService-productSkuBySkuCode")
    public ProductSku getProductSkuBySkuCode(final String skuCode) {
        return findProductSkuBySkuCode(skuCode);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "productSkuService-productSkuSearchResultDTOByQuery")
    public List<ProductSkuSearchResultDTO> getProductSkuSearchResultDTOByQuery(final Query query) {

        final Pair<List<Object[]>, Integer> searchRez = getGenericDao().fullTextSearch(
                query,
                0,
                -1, /* no limit */
                null,
                false,
                "skuId",
                ProductSearchQueryBuilder.SKU_PRODUCT_CODE_FIELD,
                ProductSearchQueryBuilder.PRODUCT_NAME_FIELD,
                ProductSearchQueryBuilder.PRODUCT_DEFAULTIMAGE_FIELD,
                ProductSearchQueryBuilder.PRODUCT_DISPLAYNAME_ASIS_FIELD,
                ProductSearchQueryBuilder.PRODUCT_ID_FIELD
        );

        final List<ProductSkuSearchResultDTO> rez = new ArrayList<ProductSkuSearchResultDTO>(searchRez.getFirst().size());
        for (Object[] obj : searchRez.getFirst()) {
            final ProductSkuSearchResultDTO dto = new ProductSkuSearchResultDTOImpl();
            dto.setId((Long) obj[0]);
            dto.setCode((String) obj[1]);
            dto.setName((String) obj[2]);
            dto.setDefaultImage((String) obj[3]);
            dto.setDisplayName((String) obj[4]);
            dto.setProductId((Long) obj[5]);
            rez.add(dto);
        }

        return rez;

    }

    /**
     * {@inheritDoc}
     */
    public List<Pair<String, SkuPrice>> getAllPrices(final long productId) {
        final List<Object[]> prices = (List) getGenericDao().findQueryObjectsByNamedQuery("SKUPRICE.BY.PRODUCT", productId);
        if (CollectionUtils.isNotEmpty(prices)) {
            final List<Pair<String, SkuPrice>> rez = new ArrayList<Pair<String, SkuPrice>>(prices.size());
            for (final Object[] price : prices) {
                rez.add(new Pair<String, SkuPrice>((String) price[1], (SkuPrice) price[0]));
            }
            return rez;
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<Pair<String, SkuWarehouse>> getAllInventory(final long productId) {
        final List<Object[]> inventory = (List) getGenericDao().findQueryObjectsByNamedQuery("SKUWAREHOUSE.BY.PRODUCT", productId);
        if (CollectionUtils.isNotEmpty(inventory)) {
            final List<Pair<String, SkuWarehouse>> rez = new ArrayList<Pair<String, SkuWarehouse>>(inventory.size());
            for (final Object[] price : inventory) {
                rez.add(new Pair<String, SkuWarehouse>((String) price[1], (SkuWarehouse) price[0]));
            }
            return rez;
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "productSkuService-productSkuBySkuCode",
            "productService-skuById"
    }, allEntries = true)
    public void removeAllPrices(final long productId) {
        final List<ProductSku> skus = getGenericDao().findByCriteria(Restrictions.eq("product.productId" , productId));
        for (ProductSku sku : skus) {
            removeAllPrices( sku);
        }
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "productSkuService-productSkuBySkuCode",
            "productService-skuById"
    }, allEntries = true)
    public void removeAllPrices(final ProductSku sku) {
         getGenericDao().executeUpdate("REMOVE.ALL.SKU.PRICES", sku);

     }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "productSkuService-productSkuBySkuCode",
            "productService-skuById"
    }, allEntries = true)
    public void removeAllInventory(final long productId) {
        final List<ProductSku> skus = getGenericDao().findByCriteria(Restrictions.eq("product.productId" , productId));
        for (ProductSku sku : skus) {
            removeAllInventory(sku);
        }
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
        "productSkuService-productSkuBySkuCode",
        "productService-skuById"
    }, allEntries = true)
    public void removeAllInventory(final ProductSku sku) {
            getGenericDao().executeUpdate("REMOVE.ALL.SKU.INVENTORY", sku);
    }

    /**
     * {@inheritDoc}
     */
    /*public void delete(ProductSku instance) {
        getGenericDao().executeUpdate("REMOVE.ALL.SKU.PRICES", instance);
        getGenericDao().executeUpdate("REMOVE.ALL.SKU.INVENTORY", instance);
        getGenericDao().evict(instance.getProduct());
        super.delete(instance);
        instance.getProduct().getSku().remove(instance);
    }*/
}

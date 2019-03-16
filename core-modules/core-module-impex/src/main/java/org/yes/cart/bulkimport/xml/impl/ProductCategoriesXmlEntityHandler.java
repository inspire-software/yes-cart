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

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.CollectionImportModeType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.ProductCategoryType;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductCategoryService;
import org.yes.cart.service.domain.ProductService;

import java.util.Iterator;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ProductCategoriesXmlEntityHandler extends AbstractXmlEntityHandler<org.yes.cart.bulkimport.xml.internal.ProductCategoriesCodeType, Product> implements XmlEntityImportHandler<org.yes.cart.bulkimport.xml.internal.ProductCategoriesCodeType> {

    private CategoryService categoryService;
    private ProductService productService;
    private ProductCategoryService productCategoryService;

    public ProductCategoriesXmlEntityHandler() {
        super("product-categories");
    }

    @Override
    protected void delete(final Product product) {
        throw new UnsupportedOperationException("Product delete mode is not supported");
    }

    @Override
    protected void saveOrUpdate(final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductCategoriesCodeType xmlType, final EntityImportModeType mode) {

        if (domain != null) {
            processCategories(domain, xmlType);

            if (domain.getProductId() == 0L) {
                this.productService.create(domain);
            } else {
                this.productService.update(domain);
            }
            this.productService.getGenericDao().flush();
            this.productService.getGenericDao().evict(domain);
        }

    }

    private void processCategories(final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductCategoriesCodeType xmlType) {

        final CollectionImportModeType collectionMode = xmlType.getImportMode() != null ? xmlType.getImportMode() : CollectionImportModeType.MERGE;
        if (collectionMode == CollectionImportModeType.REPLACE) {
            domain.getProductCategory().clear();
        }

        for (final ProductCategoryType cat : xmlType.getProductCategory()) {
            final EntityImportModeType itemMode = cat.getImportMode() != null ? cat.getImportMode() : EntityImportModeType.MERGE;
            if (itemMode == EntityImportModeType.DELETE) {
                if (cat.getGuid() != null) {
                    processCategoriesRemove(domain, cat);
                }
            } else {
                processCategoriesSave(domain, cat);
            }
        }

    }

    private void processCategoriesSave(final Product domain, final ProductCategoryType cat) {

        for (final ProductCategory pc : domain.getProductCategory()) {
            if (cat.getGuid().equals(pc.getCategory().getGuid())) {
                processCategoriesSaveBasic(cat, pc);
                return;
            }
        }
        final ProductCategory pc = this.productCategoryService.getGenericDao().getEntityFactory().getByIface(ProductCategory.class);
        pc.setProduct(domain);
        Category ct = this.categoryService.findSingleByCriteria(" where e.guid = ?1", cat.getGuid());
        if (ct == null) {
            final Category root = this.categoryService.getRootCategory();
            final Category pcCat = this.categoryService.getGenericDao().getEntityFactory().getByIface(Category.class);
            pcCat.setGuid(cat.getGuid());
            pcCat.setName(cat.getGuid());
            pcCat.setParentId(root.getCategoryId());
            this.categoryService.create(pcCat);
            ct = pcCat;
        }
        pc.setCategory(ct);
        processCategoriesSaveBasic(cat, pc);
        domain.getProductCategory().add(pc);


    }

    private void processCategoriesSaveBasic(final ProductCategoryType cat, final ProductCategory pc) {
        if (cat.getRank() != null) {
            pc.setRank(cat.getRank());
        }
    }

    private void processCategoriesRemove(final Product domain, final ProductCategoryType cat) {
        final Iterator<ProductCategory> it = domain.getProductCategory().iterator();
        while (it.hasNext()) {
            final ProductCategory next = it.next();
            if (cat.getGuid().equals(next.getCategory().getGuid())) {
                it.remove();
                return;
            }
        }
    }

    @Override
    protected Product getOrCreate(final org.yes.cart.bulkimport.xml.internal.ProductCategoriesCodeType xmlType) {
        Product product = this.productService.findSingleByCriteria(" where e.code = ?1", xmlType.getProductCode());
        if (product != null) {
            return product;
        }
        return null;
    }

    @Override
    protected EntityImportModeType determineImportMode(final org.yes.cart.bulkimport.xml.internal.ProductCategoriesCodeType xmlType) {
        return EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Product domain) {
        return domain.getProductId() == 0L;
    }


    /**
     * Spring IoC.
     *
     * @param categoryService category service
     */
    public void setCategoryService(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Spring IoC.
     *
     * @param productService product service
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    /**
     * Spring IoC.
     *
     * @param productCategoryService product category service
     */
    public void setProductCategoryService(final ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }
}

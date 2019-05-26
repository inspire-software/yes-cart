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
import org.yes.cart.bulkimport.xml.internal.ShopCategoriesCodeType;
import org.yes.cart.bulkimport.xml.internal.ShopCategoryType;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;

import java.util.Iterator;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ShopCategoriesXmlEntityHandler extends AbstractXmlEntityHandler<ShopCategoriesCodeType, Shop> implements XmlEntityImportHandler<ShopCategoriesCodeType, Shop> {

    private CategoryService categoryService;
    private ShopService shopService;

    public ShopCategoriesXmlEntityHandler() {
        super("shop-categories");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final Shop shop, final Map<String, Integer> entityCount) {
        throw new UnsupportedOperationException("Shop delete mode is not supported");
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final Shop domain, final ShopCategoriesCodeType xmlType, final EntityImportModeType mode, final Map<String, Integer> entityCount) {

        if (domain != null) {
            processCategories(domain, xmlType);

            if (domain.getShopId() == 0L) {
                this.shopService.create(domain);
            } else {
                this.shopService.update(domain);
            }
            this.shopService.getGenericDao().flush();
            this.shopService.getGenericDao().evict(domain);
        }

    }

    private void processCategories(final Shop domain, final ShopCategoriesCodeType xmlType) {

        final CollectionImportModeType collectionMode = xmlType.getImportMode() != null ? xmlType.getImportMode() : CollectionImportModeType.MERGE;
        if (collectionMode == CollectionImportModeType.REPLACE) {
            domain.getShopCategory().clear();
        }

        for (final ShopCategoryType cat : xmlType.getShopCategory()) {
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

    private void processCategoriesSave(final Shop domain, final ShopCategoryType cat) {

        for (final ShopCategory pc : domain.getShopCategory()) {
            if (cat.getGuid().equals(pc.getCategory().getGuid())) {
                processCategoriesSaveBasic(cat, pc);
                return;
            }
        }
        final ShopCategory pc = this.shopService.getGenericDao().getEntityFactory().getByIface(ShopCategory.class);
        pc.setShop(domain);
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
        domain.getShopCategory().add(pc);


    }

    private void processCategoriesSaveBasic(final ShopCategoryType cat, final ShopCategory pc) {
        if (cat.getRank() != null) {
            pc.setRank(cat.getRank());
        }
    }

    private void processCategoriesRemove(final Shop domain, final ShopCategoryType cat) {
        final Iterator<ShopCategory> it = domain.getShopCategory().iterator();
        while (it.hasNext()) {
            final ShopCategory next = it.next();
            if (cat.getGuid().equals(next.getCategory().getGuid())) {
                it.remove();
                return;
            }
        }
    }

    @Override
    protected Shop getOrCreate(final JobStatusListener statusListener, final ShopCategoriesCodeType xmlType, final Map<String, Integer> entityCount) {
        Shop shop = this.shopService.findSingleByCriteria(" where e.code = ?1", xmlType.getShopCode());
        if (shop != null) {
            return shop;
        }
        return null;
    }

    @Override
    protected EntityImportModeType determineImportMode(final ShopCategoriesCodeType xmlType) {
        return EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Shop domain) {
        return domain.getShopId() == 0L;
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
     * @param shopService shop service
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }

}

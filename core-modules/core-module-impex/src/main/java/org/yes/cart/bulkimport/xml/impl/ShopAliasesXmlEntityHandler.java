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

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.CollectionImportModeType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.ShopAliasType;
import org.yes.cart.bulkimport.xml.internal.ShopAliasesCodeType;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopAlias;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.ShopService;

import java.util.Iterator;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ShopAliasesXmlEntityHandler extends AbstractXmlEntityHandler<ShopAliasesCodeType, Shop> implements XmlEntityImportHandler<ShopAliasesCodeType, Shop> {

    private ShopService shopService;

    public ShopAliasesXmlEntityHandler() {
        super("shop-aliases");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final Shop shop) {
        throw new UnsupportedOperationException("Shop delete mode is not supported");
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final Shop domain, final ShopAliasesCodeType xmlType, final EntityImportModeType mode) {

        if (domain != null) {
            processAliases(domain, xmlType);

            if (domain.getShopId() == 0L) {
                this.shopService.create(domain);
            } else {
                this.shopService.update(domain);
            }
            this.shopService.getGenericDao().flush();
            this.shopService.getGenericDao().evict(domain);
        }

    }

    private void processAliases(final Shop domain, final ShopAliasesCodeType xmlType) {

        final CollectionImportModeType collectionMode = xmlType.getImportMode() != null ? xmlType.getImportMode() : CollectionImportModeType.MERGE;
        if (collectionMode == CollectionImportModeType.REPLACE) {
            domain.getShopUrl().clear();
        }

        for (final ShopAliasType aliasType : xmlType.getShopAlias()) {
            final EntityImportModeType itemMode = aliasType.getImportMode() != null ? aliasType.getImportMode() : EntityImportModeType.MERGE;
            if (itemMode == EntityImportModeType.DELETE) {
                if (aliasType.getName() != null) {
                    processAliasRemove(domain, aliasType);
                }
            } else {
                processAliasSave(domain, aliasType);
            }
        }

    }

    private void processAliasSave(final Shop domain, final ShopAliasType alias) {

        for (final ShopAlias shopAlias : domain.getShopAlias()) {
            if (alias.getName().equals(shopAlias.getAlias())) {
                processUrlsSaveBasic(alias, shopAlias);
                return;
            }
        }
        final ShopAlias shopAlias = this.shopService.getGenericDao().getEntityFactory().getByIface(ShopAlias.class);
        shopAlias.setShop(domain);
        processUrlsSaveBasic(alias, shopAlias);
        domain.getShopAlias().add(shopAlias);


    }

    private void processUrlsSaveBasic(final ShopAliasType alias, final ShopAlias sa) {
        sa.setAlias(alias.getName());
    }

    private void processAliasRemove(final Shop domain, final ShopAliasType shopAlias) {
        final Iterator<ShopAlias> it = domain.getShopAlias().iterator();
        while (it.hasNext()) {
            final ShopAlias next = it.next();
            if (shopAlias.getName().equals(next.getAlias())) {
                it.remove();
                return;
            }
        }
    }

    @Override
    protected Shop getOrCreate(final JobStatusListener statusListener, final ShopAliasesCodeType xmlType) {
        Shop shop = this.shopService.findSingleByCriteria(" where e.code = ?1", xmlType.getShopCode());
        if (shop != null) {
            return shop;
        }
        return null;
    }

    @Override
    protected EntityImportModeType determineImportMode(final ShopAliasesCodeType xmlType) {
        return EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Shop domain) {
        return domain.getShopId() == 0L;
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

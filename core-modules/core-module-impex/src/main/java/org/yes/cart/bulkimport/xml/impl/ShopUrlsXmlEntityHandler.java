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
import org.yes.cart.bulkimport.xml.internal.ShopUrlCodeType;
import org.yes.cart.bulkimport.xml.internal.ShopUrlType;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopUrl;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.ShopService;

import java.util.Iterator;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ShopUrlsXmlEntityHandler extends AbstractXmlEntityHandler<ShopUrlCodeType, Shop> implements XmlEntityImportHandler<ShopUrlCodeType, Shop> {

    private ShopService shopService;

    public ShopUrlsXmlEntityHandler() {
        super("shop-urls");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final Shop shop, final Map<String, Integer> entityCount) {
        throw new UnsupportedOperationException("Shop delete mode is not supported");
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final Shop domain, final ShopUrlCodeType xmlType, final EntityImportModeType mode, final Map<String, Integer> entityCount) {

        if (domain != null) {
            processUrls(domain, xmlType);

            if (domain.getShopId() == 0L) {
                this.shopService.create(domain);
            } else {
                this.shopService.update(domain);
            }
            this.shopService.getGenericDao().flush();
            this.shopService.getGenericDao().evict(domain);
        }

    }

    private void processUrls(final Shop domain, final ShopUrlCodeType xmlType) {

        final CollectionImportModeType collectionMode = xmlType.getImportMode() != null ? xmlType.getImportMode() : CollectionImportModeType.MERGE;
        if (collectionMode == CollectionImportModeType.REPLACE) {
            domain.getShopUrl().clear();
        }

        for (final ShopUrlType urlType : xmlType.getShopUrl()) {
            final EntityImportModeType itemMode = urlType.getImportMode() != null ? urlType.getImportMode() : EntityImportModeType.MERGE;
            if (itemMode == EntityImportModeType.DELETE) {
                if (urlType.getDomain() != null) {
                    processUrlsRemove(domain, urlType);
                }
            } else {
                processUrlsSave(domain, urlType);
            }
        }

    }

    private void processUrlsSave(final Shop domain, final ShopUrlType url) {

        for (final ShopUrl shopUrl : domain.getShopUrl()) {
            if (url.getDomain().equals(shopUrl.getUrl())) {
                processUrlsSaveBasic(url, shopUrl);
                return;
            }
        }
        final ShopUrl shopUrl = this.shopService.getGenericDao().getEntityFactory().getByIface(ShopUrl.class);
        shopUrl.setShop(domain);
        processUrlsSaveBasic(url, shopUrl);
        domain.getShopUrl().add(shopUrl);


    }

    private void processUrlsSaveBasic(final ShopUrlType url, final ShopUrl su) {
        su.setUrl(url.getDomain());
        su.setPrimary(url.isPrimary());
        su.setThemeChain(url.getThemeChain());
    }

    private void processUrlsRemove(final Shop domain, final ShopUrlType url) {
        final Iterator<ShopUrl> it = domain.getShopUrl().iterator();
        while (it.hasNext()) {
            final ShopUrl next = it.next();
            if (url.getDomain().equals(next.getUrl())) {
                it.remove();
                return;
            }
        }
    }

    @Override
    protected Shop getOrCreate(final JobStatusListener statusListener, final ShopUrlCodeType xmlType, final Map<String, Integer> entityCount) {
        Shop shop = this.shopService.findSingleByCriteria(" where e.code = ?1", xmlType.getShopCode());
        if (shop != null) {
            return shop;
        }
        return null;
    }

    @Override
    protected EntityImportModeType determineImportMode(final ShopUrlCodeType xmlType) {
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

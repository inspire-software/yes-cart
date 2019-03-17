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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.*;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;

import java.util.UUID;

/**
 * User: denispavlov
 * Date: 16/03/2019
 * Time: 20:10
 */
public class ShopXmlEntityHandler extends AbstractAttributableXmlEntityHandler<ShopType, Shop, Shop, AttrValueShop> implements XmlEntityImportHandler<ShopType> {

    private static final Logger LOG = LoggerFactory.getLogger(ShopXmlEntityHandler.class);

    private ShopService shopService;

    private XmlEntityImportHandler<ShopUrlCodeType> shopUrlXmlEntityImportHandler;
    private XmlEntityImportHandler<ShopAliasesCodeType> shopAliasesXmlEntityImportHandler;
    private XmlEntityImportHandler<ShopCategoriesCodeType> shopCategoriesXmlEntityImportHandler;
    private XmlEntityImportHandler<ShopCarriersCodeType> shopCarriersXmlEntityImportHandler;
    private XmlEntityImportHandler<ShopFulfilmentCentresCodeType> shopFulfilmentCentresXmlEntityImportHandler;
    private XmlEntityImportHandler<CustomerType> customerXmlEntityImportHandler;

    public ShopXmlEntityHandler() {
        super("shop");
    }

    @Override
    protected void delete(final Shop shop) {
        this.shopService.delete(shop);
        this.shopService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final Shop domain, final ShopType xmlType, final EntityImportModeType mode) {

        if (xmlType.getPresentation() != null) {
            domain.setName(xmlType.getPresentation().getName());
            domain.setDescription(xmlType.getPresentation().getDescription());
            domain.setFspointer(xmlType.getPresentation().getThemeChain());
        }

        if (xmlType.getAvailability() != null) {
            domain.setDisabled(xmlType.getAvailability().isDisabled());
        }

        updateSeo(xmlType.getSeo(), domain.getSeo());
        updateExt(xmlType.getCustomAttributes(), domain, domain.getAttributes());

        if (domain.getShopId() == 0L) {
            this.shopService.create(domain);
        } else {
            this.shopService.update(domain);
        }
        this.shopService.getGenericDao().flush();
        this.shopService.getGenericDao().evict(domain);


        processUrls(domain, xmlType);
        processAliases(domain, xmlType);
        processCategories(domain, xmlType);
        processCarriers(domain, xmlType);
        processCentres(domain, xmlType);

        if (xmlType.getShopAddressbook() != null) {

            final CustomerType customerType = xmlType.getShopAddressbook().getCustomer();
            customerType.setCredentials(new CustomerCredentialsType());
            customerType.getCredentials().setGuest(false);
            customerType.getCredentials().setEmail("#" + domain.getCode() + "#");
            customerType.getCredentials().setPassword(UUID.randomUUID().toString());
            customerType.setOrganisation(new CustomerOrganisationType());
            customerType.getOrganisation().setCompanyName1(domain.getName());
            customerType.getOrganisation().setShops(new CustomerShopsType());
            final CustomerShopType cst = new CustomerShopType();
            cst.setCode(domain.getCode());
            cst.setEnabled(true);
            customerType.getOrganisation().getShops().getShop().add(cst);
            customerType.setContactDetails(new CustomerContactDetailsType());
            customerType.getContactDetails().setFirstname(domain.getCode());
            customerType.getContactDetails().setLastname(domain.getName());

            customerXmlEntityImportHandler.handle(null, null, (ImpExTuple) new XmlImportTupleImpl(customerType.getGuid(), customerType), null, null);

        }

    }

    private void processUrls(final Shop domain, final ShopType xmlType) {

        if (xmlType.getShopUrls() != null) {

            final ShopUrlCodeType subXmlType = new ShopUrlCodeType();
            subXmlType.setShopCode(domain.getCode());
            subXmlType.setImportMode(xmlType.getShopUrls().getImportMode());
            subXmlType.getShopUrl().addAll(xmlType.getShopUrls().getShopUrl());

            shopUrlXmlEntityImportHandler.handle(null, null, (ImpExTuple) new XmlImportTupleImpl(subXmlType.getShopCode(), subXmlType), null, null);

        }

    }

    private void processAliases(final Shop domain, final ShopType xmlType) {

        if (xmlType.getShopAliases() != null) {

            final ShopAliasesCodeType subXmlType = new ShopAliasesCodeType();
            subXmlType.setShopCode(domain.getCode());
            subXmlType.setImportMode(xmlType.getShopAliases().getImportMode());
            subXmlType.getShopAlias().addAll(xmlType.getShopAliases().getShopAlias());

            shopAliasesXmlEntityImportHandler.handle(null, null, (ImpExTuple) new XmlImportTupleImpl(subXmlType.getShopCode(), subXmlType), null, null);

        }

    }


    private void processCategories(final Shop domain, final ShopType xmlType) {

        if (xmlType.getShopCategories() != null) {

            final ShopCategoriesCodeType subXmlType = new ShopCategoriesCodeType();
            subXmlType.setShopCode(domain.getCode());
            subXmlType.setImportMode(xmlType.getShopCategories().getImportMode());
            subXmlType.getShopCategory().addAll(xmlType.getShopCategories().getShopCategory());

            shopCategoriesXmlEntityImportHandler.handle(null, null, (ImpExTuple) new XmlImportTupleImpl(subXmlType.getShopCode(), subXmlType), null, null);

        }

    }


    private void processCarriers(final Shop domain, final ShopType xmlType) {

        if (xmlType.getShopCarriers() != null) {

            final ShopCarriersCodeType subXmlType = new ShopCarriersCodeType();
            subXmlType.setShopCode(domain.getCode());
            subXmlType.setImportMode(xmlType.getShopCarriers().getImportMode());
            subXmlType.getShopCarrier().addAll(xmlType.getShopCarriers().getShopCarrier());

            shopCarriersXmlEntityImportHandler.handle(null, null, (ImpExTuple) new XmlImportTupleImpl(subXmlType.getShopCode(), subXmlType), null, null);

        }

    }


    private void processCentres(final Shop domain, final ShopType xmlType) {

        if (xmlType.getShopFulfilmentCentres() != null) {

            final ShopFulfilmentCentresCodeType subXmlType = new ShopFulfilmentCentresCodeType();
            subXmlType.setShopCode(domain.getCode());
            subXmlType.setImportMode(xmlType.getShopFulfilmentCentres().getImportMode());
            subXmlType.getShopFulfilmentCentre().addAll(xmlType.getShopFulfilmentCentres().getShopFulfilmentCentre());

            shopFulfilmentCentresXmlEntityImportHandler.handle(null, null, (ImpExTuple) new XmlImportTupleImpl(subXmlType.getShopCode(), subXmlType), null, null);

        }

    }

    @Override
    protected Shop getOrCreate(final ShopType xmlType) {
        Shop shop = this.shopService.findSingleByCriteria(" where e.code = ?1", xmlType.getCode());
        if (shop != null) {
            return shop;
        }
        shop = this.shopService.getGenericDao().getEntityFactory().getByIface(Shop.class);
        shop.setCode(xmlType.getCode());
        shop.setGuid(xmlType.getCode());
        if (StringUtils.isNotBlank(xmlType.getMasterCode())) {
            final Shop master = this.shopService.findSingleByCriteria(" where e.code = ?1", xmlType.getMasterCode());
            if (master == null) {
                LOG.warn("Cannot resolve master shop {} when creating new shop {}", xmlType.getMasterCode(), xmlType.getCode());
            }
            shop.setMaster(master);
        }
        return shop;
    }

    @Override
    protected EntityImportModeType determineImportMode(final ShopType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Shop domain) {
        return domain.getShopId() == 0L;
    }

    @Override
    protected void setMaster(final Shop master, final AttrValueShop av) {
        av.setShop(master);
    }

    @Override
    protected Class<AttrValueShop> getAvInterface() {
        return AttrValueShop.class;
    }

    /**
     * Spring IoC.
     *
     * @param shopService shop service
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * Sprinf IoC.
     *
     * @param shopUrlXmlEntityImportHandler handler
     */
    public void setShopUrlXmlEntityImportHandler(final XmlEntityImportHandler<ShopUrlCodeType> shopUrlXmlEntityImportHandler) {
        this.shopUrlXmlEntityImportHandler = shopUrlXmlEntityImportHandler;
    }

    /**
     * Spring IoC.
     *
     * @param customerXmlEntityImportHandler handler
     */
    public void setCustomerXmlEntityImportHandler(final XmlEntityImportHandler<CustomerType> customerXmlEntityImportHandler) {
        this.customerXmlEntityImportHandler = customerXmlEntityImportHandler;
    }

    /**
     * Spring IoC.
     *
     * @param shopCategoriesXmlEntityImportHandler handler
     */
    public void setShopCategoriesXmlEntityImportHandler(final XmlEntityImportHandler<ShopCategoriesCodeType> shopCategoriesXmlEntityImportHandler) {
        this.shopCategoriesXmlEntityImportHandler = shopCategoriesXmlEntityImportHandler;
    }

    /**
     * Spring IoC.
     *
     * @param shopAliasesXmlEntityImportHandler handler
     */
    public void setShopAliasesXmlEntityImportHandler(final XmlEntityImportHandler<ShopAliasesCodeType> shopAliasesXmlEntityImportHandler) {
        this.shopAliasesXmlEntityImportHandler = shopAliasesXmlEntityImportHandler;
    }

    /**
     * Spring IoC.
     *
     * @param shopCarriersXmlEntityImportHandler handler
     */
    public void setShopCarriersXmlEntityImportHandler(final XmlEntityImportHandler<ShopCarriersCodeType> shopCarriersXmlEntityImportHandler) {
        this.shopCarriersXmlEntityImportHandler = shopCarriersXmlEntityImportHandler;
    }

    /**
     * Spring IoC.
     *
     * @param shopFulfilmentCentresXmlEntityImportHandler handler
     */
    public void setShopFulfilmentCentresXmlEntityImportHandler(final XmlEntityImportHandler<ShopFulfilmentCentresCodeType> shopFulfilmentCentresXmlEntityImportHandler) {
        this.shopFulfilmentCentresXmlEntityImportHandler = shopFulfilmentCentresXmlEntityImportHandler;
    }
}

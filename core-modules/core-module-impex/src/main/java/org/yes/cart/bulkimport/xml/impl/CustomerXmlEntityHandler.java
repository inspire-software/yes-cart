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
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.CustomerWishListService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.DateUtils;

import java.util.Iterator;
import java.util.Optional;

/**
 * User: denispavlov
 * Date: 16/03/2019
 * Time: 12:03
 */
public class CustomerXmlEntityHandler extends AbstractAttributableXmlEntityHandler<CustomerType, Customer, Customer, AttrValueCustomer> implements XmlEntityImportHandler<CustomerType, Customer> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerXmlEntityHandler.class);

    private CustomerService customerService;
    private CustomerWishListService customerWishListService;
    private ShopService shopService;
    private ProductSkuService productSkuService;

    private XmlEntityImportHandler<AddressType, Address> addressXmlEntityImportHandler;

    public CustomerXmlEntityHandler() {
        super("customer");
    }

    @Override
    protected void delete(final Customer customer) {
        this.customerService.delete(customer);
        this.customerService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final Customer domain, final CustomerType xmlType, final EntityImportModeType mode) {

        if (xmlType.getCredentials() != null) {

            if (StringUtils.isNotBlank(xmlType.getCredentials().getPassword())) {
                domain.setPassword(xmlType.getCredentials().getPassword());
            }
            if (StringUtils.isNotBlank(xmlType.getCredentials().getPasswordExpiry())) {
                domain.setPasswordExpiry(DateUtils.iParseSDT(xmlType.getCredentials().getPasswordExpiry()));
            }
            if (StringUtils.isNotBlank(xmlType.getCredentials().getAuthToken())) {
                domain.setAuthToken(xmlType.getCredentials().getAuthToken());
            }
            if (StringUtils.isNotBlank(xmlType.getCredentials().getAuthTokenExpiry())) {
                domain.setAuthTokenExpiry(DateUtils.iParseSDT(xmlType.getCredentials().getAuthTokenExpiry()));
            }
            if (StringUtils.isNotBlank(xmlType.getCredentials().getPublicKey())) {
                domain.setPublicKey(xmlType.getCredentials().getPublicKey());
            }
        }

        if (xmlType.getOrganisation() != null) {
            domain.setCompanyName1(xmlType.getOrganisation().getCompanyName1());
            domain.setCompanyName2(xmlType.getOrganisation().getCompanyName2());
            domain.setCompanyDepartment(xmlType.getOrganisation().getCompanyDepartment());
        }

        if (xmlType.getContactDetails() != null) {

            domain.setSalutation(xmlType.getContactDetails().getSalutation());
            domain.setFirstname(xmlType.getContactDetails().getFirstname());
            domain.setMiddlename(xmlType.getContactDetails().getMiddlename());
            domain.setLastname(xmlType.getContactDetails().getLastname());

        }

        if (xmlType.getPolicies() != null) {

            domain.setTag(processTags(xmlType.getPolicies().getTags(), domain.getTag()));
            domain.setCustomerType(xmlType.getPolicies().getType());
            domain.setPricingPolicy(xmlType.getPolicies().getPricingPolicy());

        }

        updateExt(xmlType.getCustomAttributes(), domain, domain.getAttributes());

        if (domain.getCustomerId() == 0L) {
            this.customerService.create(domain, null);
        } else {
            this.customerService.update(domain);
        }
        this.customerService.getGenericDao().flush();
        this.customerService.getGenericDao().evict(domain);

        processShops(domain, xmlType);

        processWishList(domain, xmlType.getCustomerWishlist());

        if (xmlType.getCustomerAddresses() != null) {
            for (final AddressType xmlAddressType : xmlType.getCustomerAddresses().getAddress()) {

                xmlAddressType.setCustomerCode(null);
                xmlAddressType.setCustomerEmail(null);
                xmlAddressType.setShopCode(null);
                if (domain.getEmail().startsWith("#") && domain.getEmail().endsWith("#")) {
                    final String shopCode = domain.getEmail().substring(1, domain.getEmail().length() - 2);
                    xmlAddressType.setShopCode(shopCode);
                } else {
                    xmlAddressType.setCustomerCode(domain.getGuid());
                    xmlAddressType.setCustomerEmail(domain.getEmail());
                }
                addressXmlEntityImportHandler.handle(null, null, (ImpExTuple) new XmlImportTupleImpl(xmlAddressType.getGuid(), xmlAddressType), null, null);

            }
        }

    }

    private void processWishList(final Customer domain, final CustomerWishlistType xmlType) {

        if (xmlType == null) {
            return;
        }

        final CollectionImportModeType collectionMode = xmlType.getImportMode() != null ? xmlType.getImportMode() : CollectionImportModeType.MERGE;
        if (collectionMode == CollectionImportModeType.REPLACE) {
            domain.getWishList().clear();
        }

        for (final CustomerWishlistItemType wli : xmlType.getWishlistItem()) {
            final EntityImportModeType itemMode = wli.getImportMode() != null ? wli.getImportMode() : EntityImportModeType.MERGE;
            if (itemMode == EntityImportModeType.DELETE) {
                if (wli.getGuid() != null) {
                    processWishListRemove(domain, wli);
                }
            } else {
                processWishListSave(domain, wli);
            }
        }

        this.customerService.update(domain);
        this.customerService.getGenericDao().flush();
        this.customerService.getGenericDao().evict(domain);

    }

    private void processWishListSave(final Customer domain, final CustomerWishlistItemType wli) {

        for (final CustomerWishList wlist : domain.getWishList()) {
            if (wli.getGuid() != null && wli.getGuid().equals(wlist.getGuid())) {
                processWishListSaveBasic(wli, wlist);
                return;
            }
        }
        final CustomerWishList wlist = this.customerWishListService.getGenericDao().getEntityFactory().getByIface(CustomerWishList.class);
        wlist.setCustomer(domain);
        wlist.setGuid(wli.getGuid());
        ProductSku sku = this.productSkuService.getProductSkuBySkuCode(wli.getSkuCode());
        if (sku == null) {
            LOG.warn("Wishlist skipped for SKU {} ... not found", wli.getSkuCode());
            return;
        }
        wlist.setSkus(sku);
        processWishListSaveBasic(wli, wlist);
        domain.getWishList().add(wlist);


    }

    private void processWishListSaveBasic(final CustomerWishlistItemType wli, final CustomerWishList wlist) {
        wlist.setWlType(wli.getWishlistType());
        wlist.setVisibility(wli.getVisibility());
        wlist.setQuantity(wli.getQuantity());
        wlist.setTag(processTags(wli.getTags(), wlist.getTag()));
        if (wli.getPrice() != null) {
            wlist.setRegularPriceWhenAdded(wli.getPrice().getListPrice());
            wlist.setRegularPriceCurrencyWhenAdded(wli.getPrice().getCurrency());
        }
    }

    private void processWishListRemove(final Customer domain, final CustomerWishlistItemType wli) {
        final Iterator<CustomerWishList> it = domain.getWishList().iterator();
        while (it.hasNext()) {
            final CustomerWishList next = it.next();
            if (wli.getGuid().equals(next.getGuid())) {
                it.remove();
                return;
            }
        }
    }


    private void processShops(final Customer domain, final CustomerType xmlType) {

        if (xmlType.getOrganisation() == null || xmlType.getOrganisation().getShops() == null) {
            return;
        }

        final CollectionImportModeType collectionMode = xmlType.getOrganisation().getShops().getImportMode() != null ? xmlType.getOrganisation().getShops().getImportMode() : CollectionImportModeType.MERGE;
        if (collectionMode == CollectionImportModeType.REPLACE) {
            domain.getShops().clear();
        }

        for (final CustomerShopType mShop : xmlType.getOrganisation().getShops().getShop()) {

            final Shop shop = shopService.getShopByCode(mShop.getCode());
            if (shop != null) {

                final Optional<CustomerShop> cs = domain.getShops().stream().filter(customerShop -> shop.getShopId() == customerShop.getShop().getShopId()).findFirst();
                if (!cs.isPresent()) {

                    final CustomerShop csNew = this.customerService.getGenericDao().getEntityFactory().getByIface(CustomerShop.class);

                    csNew.setCustomer(domain);
                    csNew.setShop(shop);
                    csNew.setDisabled(!mShop.isEnabled());

                    domain.getShops().add(csNew);

                }

            }

        }

        this.customerService.update(domain);
        this.customerService.getGenericDao().flush();

    }



    @Override
    protected Customer getOrCreate(final CustomerType xmlType) {
        Customer customer = this.customerService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (customer != null) {
            return customer;
        }
        customer = this.customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setGuid(xmlType.getGuid());
        customer.setGuest(xmlType.getCredentials().isGuest());
        customer.setEmail(xmlType.getCredentials().getEmail());
        customer.setGuestEmail(xmlType.getCredentials().getGuestEmail());
        customer.setPassword(xmlType.getCredentials().getPassword());
        customer.setPasswordExpiry(DateUtils.iParseSDT(xmlType.getCredentials().getPasswordExpiry()));
        customer.setAuthToken(xmlType.getCredentials().getAuthToken());
        customer.setAuthTokenExpiry(DateUtils.iParseSDT(xmlType.getCredentials().getAuthTokenExpiry()));
        customer.setPublicKey(xmlType.getCredentials().getPublicKey());
        return customer;
    }

    @Override
    protected EntityImportModeType determineImportMode(final CustomerType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Customer domain) {
        return domain.getCustomerId() == 0L;
    }

    @Override
    protected void setMaster(final Customer master, final AttrValueCustomer av) {
        av.setCustomer(master);
    }

    @Override
    protected Class<AttrValueCustomer> getAvInterface() {
        return AttrValueCustomer.class;
    }

    /**
     * Spring IoC.
     *
     * @param customerService customer service
     */
    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Spring IoC.
     *
     * @param customerWishListService wish list service
     */
    public void setCustomerWishListService(final CustomerWishListService customerWishListService) {
        this.customerWishListService = customerWishListService;
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
     * Spring IoC.
     *
     * @param productSkuService SKU service
     */
    public void setProductSkuService(final ProductSkuService productSkuService) {
        this.productSkuService = productSkuService;
    }

    /**
     * Spring IoC.
     *
     * @param addressXmlEntityImportHandler Address handler
     */
    public void setAddressXmlEntityImportHandler(final XmlEntityImportHandler<AddressType, Address> addressXmlEntityImportHandler) {
        this.addressXmlEntityImportHandler = addressXmlEntityImportHandler;
    }
}

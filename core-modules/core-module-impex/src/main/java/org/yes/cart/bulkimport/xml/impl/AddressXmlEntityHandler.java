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
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.AddressType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.service.domain.AddressService;
import org.yes.cart.service.domain.CustomerService;

/**
 * User: denispavlov
 * Date: 16/03/2019
 * Time: 11:20
 */
public class AddressXmlEntityHandler extends AbstractXmlEntityHandler<AddressType, Address> implements XmlEntityImportHandler<AddressType, Address> {

    private static final Logger LOG = LoggerFactory.getLogger(AddressXmlEntityHandler.class);

    private AddressService addressService;
    private CustomerService customerService;

    public AddressXmlEntityHandler() {
        super("address");
    }

    @Override
    protected void delete(final Address address) {
        this.addressService.delete(address);
        this.addressService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final Address domain, final AddressType xmlType, final EntityImportModeType mode) {

        domain.setName(xmlType.getAddressName());
        domain.setSalutation(xmlType.getSalutation());
        domain.setFirstname(xmlType.getFirstname());
        domain.setMiddlename(xmlType.getMiddlename());
        domain.setLastname(xmlType.getLastname());
        domain.setAddrline1(xmlType.getAddrline1());
        domain.setAddrline2(xmlType.getAddrline2());
        domain.setCity(xmlType.getCity());
        domain.setPostcode(xmlType.getPostcode());
        domain.setCountryCode(xmlType.getCountryCode());
        domain.setStateCode(xmlType.getStateCode());
        domain.setPhone1(xmlType.getPhone1());
        domain.setPhone2(xmlType.getPhone2());
        domain.setMobile1(xmlType.getMobile1());
        domain.setMobile2(xmlType.getMobile2());
        domain.setEmail1(xmlType.getEmail1());
        domain.setEmail2(xmlType.getEmail2());
        domain.setCompanyName1(xmlType.getCompanyName1());
        domain.setCompanyName2(xmlType.getCompanyName2());
        domain.setCompanyDepartment(xmlType.getCompanyDepartment());
        domain.setCustom0(xmlType.getCustom0());
        domain.setCustom1(xmlType.getCustom1());
        domain.setCustom2(xmlType.getCustom2());
        domain.setCustom3(xmlType.getCustom3());
        domain.setCustom4(xmlType.getCustom4());
        domain.setCustom5(xmlType.getCustom5());
        domain.setCustom6(xmlType.getCustom6());
        domain.setCustom7(xmlType.getCustom7());
        domain.setCustom8(xmlType.getCustom8());
        domain.setCustom9(xmlType.getCustom9());

        if (domain.getAddressId() == 0L) {
            this.addressService.create(domain);
        } else {
            this.addressService.update(domain);
        }
        this.addressService.getGenericDao().flush();
        this.addressService.getGenericDao().evict(domain);
    }

    @Override
    protected Address getOrCreate(final AddressType xmlType) {
        Address address = this.addressService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (address != null) {
            return address;
        }
        address = this.addressService.getGenericDao().getEntityFactory().getByIface(Address.class);
        address.setGuid(xmlType.getGuid());
        if (StringUtils.isNotBlank(xmlType.getCustomerCode())) {
            final Customer customer = this.customerService.findSingleByCriteria(" where e.guid = ?1", xmlType.getCustomerCode());
            if (customer == null) {
                LOG.warn("Cannot find customer for GUID {} ... address will not be associated with customer", xmlType.getCustomerCode());
            }
            address.setCustomer(customer);
        } else if (StringUtils.isNotBlank(xmlType.getShopCode())) {
            final Customer customer = this.customerService.findSingleByCriteria(" where e.email = ?1", "#" + xmlType.getCustomerCode() + "#");
            if (customer == null) {
                LOG.warn("Cannot find customer for shop {} ... address will not be associated with customer", xmlType.getShopCode());
            }
            address.setCustomer(customer);
        }
        address.setAddressType(xmlType.getAddressType());
        return address;
    }

    @Override
    protected EntityImportModeType determineImportMode(final AddressType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Address domain) {
        return domain.getAddressId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param addressService address service
     */
    public void setAddressService(final AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Spring IoC.
     *
     * @param customerService customer service
     */
    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
    }
}

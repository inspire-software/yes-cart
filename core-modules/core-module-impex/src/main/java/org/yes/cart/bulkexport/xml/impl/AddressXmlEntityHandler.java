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

package org.yes.cart.bulkexport.xml.impl;

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;

/**
 * User: denispavlov
 * Date: 16/03/2019
 * Time: 09:33
 */
public class AddressXmlEntityHandler extends AbstractXmlEntityHandler<Address>  {

    public AddressXmlEntityHandler() {
        super("addresses");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Address> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer) throws Exception {

        handleInternal(tagAddress(null, tuple.getData()), writer, statusListener);
        
    }

    Tag tagAddress(final Tag parent, final Address address) {

        final Tag aTag = tag(parent, "address")
                .attr("id", address.getAddressId())
                .attr("guid", address.getGuid())
                .attr("address-type", address.getAddressType())
                .attr("default-address", address.isDefaultAddress());

        if (address.getCustomer() != null) {
            if (address.getCustomer().isShop()) {

                final String shopCode = address.getCustomer().getLogin().substring(1, address.getCustomer().getLogin().length() - 2);
                aTag.attr("shop-code", shopCode);
            } else {
                aTag
                        .attr("customer-code", address.getCustomer().getGuid())
                        .attr("customer-email", address.getCustomer().getEmail());
            }
        }

        aTag
                .tagChars("address-name", address.getName())
                .tagChars("salutation", address.getSalutation())
                .tagChars("firstname", address.getFirstname())
                .tagChars("middlename", address.getMiddlename())
                .tagChars("lastname", address.getLastname())
                .tagChars("addrline-1", address.getAddrline1())
                .tagChars("addrline-2", address.getAddrline2())
                .tagChars("city", address.getCity())
                .tagChars("postcode", address.getPostcode())
                .tagChars("country-code", address.getCountryCode())
                .tagChars("state-code", address.getStateCode())
                .tagChars("phone-1", address.getPhone1())
                .tagChars("phone-2", address.getPhone2())
                .tagChars("mobile-1", address.getMobile1())
                .tagChars("mobile-2", address.getMobile2())
                .tagChars("email-1", address.getEmail1())
                .tagChars("email-2", address.getEmail2())
                .tagChars("company-name-1", address.getCompanyName1())
                .tagChars("company-name-2", address.getCompanyName2())
                .tagChars("company-department", address.getCompanyDepartment())
                .tagChars("custom-0", address.getCustom0())
                .tagChars("custom-1", address.getCustom1())
                .tagChars("custom-2", address.getCustom2())
                .tagChars("custom-3", address.getCustom3())
                .tagChars("custom-4", address.getCustom4())
                .tagChars("custom-5", address.getCustom5())
                .tagChars("custom-6", address.getCustom6())
                .tagChars("custom-7", address.getCustom7())
                .tagChars("custom-8", address.getCustom8())
                .tagChars("custom-9", address.getCustom9());

        return aTag.tagTime(address).end();

    }

}

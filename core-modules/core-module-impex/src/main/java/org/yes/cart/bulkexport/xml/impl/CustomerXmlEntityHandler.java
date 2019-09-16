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

package org.yes.cart.bulkexport.xml.impl;

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerShop;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 15/03/2019
 * Time: 09:00
 */
public class CustomerXmlEntityHandler extends AbstractXmlEntityHandler<Customer>  {

    private final AddressXmlEntityHandler addressHandler = new AddressXmlEntityHandler();

    public CustomerXmlEntityHandler() {
        super("customers");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Customer> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer,
                       final Map<String, Integer> entityCount) throws Exception {

        handleInternal(tagCustomer(null, tuple.getData()), writer, entityCount);

    }

    Tag tagCustomer(final Tag parent, final Customer customer) {

        final Tag cTag = tag(parent, "customer")
                .attr("id", customer.getCustomerId())
                .attr("guid", customer.getGuid());

        cTag.tag("credentials")
                .attr("guest", customer.isGuest())
                .tagChars("email", customer.getEmail())
                .tagChars("guest-email", customer.getGuestEmail())
                .tagChars("password", customer.getPassword())
                .tagTime("password-expiry", customer.getPasswordExpiry())
                .tagChars("auth-token", customer.getAuthToken())
                .tagTime("auth-token-expiry", customer.getAuthTokenExpiry())
                .tagChars("public-key", customer.getPublicKey())
                .end();

        final Tag orgTag = cTag.tag("organisation")
                .tagChars("company-name-1", customer.getCompanyName1())
                .tagChars("company-name-2", customer.getCompanyName2())
                .tagChars("company-department", customer.getCompanyDepartment());

        if (CollectionUtils.isNotEmpty(customer.getShops())) {
            final Tag shopsTag = orgTag.tag("shops");
            for (final CustomerShop ms : customer.getShops()) {
                shopsTag.tag("shop").attr("code", ms.getShop().getCode()).attr("enabled", !ms.isDisabled()).end();
            }
            shopsTag.end();
        }

        orgTag.end();

        cTag.tag("contact-details")
                .tagChars("salutation", customer.getSalutation())
                .tagChars("firstname", customer.getFirstname())
                .tagChars("middlename", customer.getMiddlename())
                .tagChars("lastname", customer.getLastname())
                .end();

        cTag.tag("policies")
                .tagList("tags", "tag", customer.getTag(), ' ')
                .tagChars("type", customer.getCustomerType())
                .tagChars("pricing-policy", customer.getPricingPolicy())
                .end();

        cTag.tagExt(customer);

        if (CollectionUtils.isNotEmpty(customer.getAddress())) {

           final Tag aTag = cTag.tag("customer-addresses");

           for (final Address address : customer.getAddress()) {

               this.addressHandler.tagAddress(aTag, address);

           }

           aTag.end();

        }

        if (CollectionUtils.isNotEmpty(customer.getWishList())) {

            final Tag wTag = cTag.tag("customer-wishlist");

            for (final CustomerWishList wl : customer.getWishList()) {

                wTag.tag("wishlist-item")
                        .attr("id", wl.getCustomerwishlistId())
                        .attr("guid", wl.getGuid())
                        .attr("wishlist-type", wl.getWlType())
                        .attr("visibility", wl.getVisibility())
                        .attr("sku-code", wl.getSkuCode())
                        .attr("fulfilment-centre-code", wl.getSupplierCode())
                        .tagList("tags", "tag", wl.getTag(), ' ')
                        .tagNum("quantity", wl.getQuantity())
                        .tag("price")
                            .attr("currency", wl.getRegularPriceCurrencyWhenAdded())
                            .attr("quantity", wl.getQuantity())
                            .tagNum("list-price", wl.getRegularPriceWhenAdded())
                        .end()
                    .end();

            }


            wTag.end();

        }

        return cTag.tagTime(customer).end();

    }

    /**
     * Spring IoC.
     *
     * @param prettyPrint set pretty print mode (new lines and indents)
     */
    @Override
    public void setPrettyPrint(final boolean prettyPrint) {
        super.setPrettyPrint(prettyPrint);
        this.addressHandler.setPrettyPrint(prettyPrint);
    }

}

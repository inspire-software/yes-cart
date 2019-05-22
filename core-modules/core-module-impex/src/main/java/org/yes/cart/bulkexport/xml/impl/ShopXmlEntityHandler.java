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
import org.yes.cart.domain.entity.*;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.CarrierService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.WarehouseService;

import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: denispavlov
 * Date: 16/03/2019
 * Time: 14:24
 */
public class ShopXmlEntityHandler extends AbstractXmlEntityHandler<Shop> {

    private CarrierService carrierService;
    private WarehouseService warehouseService;
    private CustomerService customerService;

    private final CustomerXmlEntityHandler customerXmlEntityHandler = new CustomerXmlEntityHandler();

    private boolean includeMaster = false;

    public ShopXmlEntityHandler() {
        super("shops");
    }

    public ShopXmlEntityHandler(final boolean includeMaster) {
        this();
        this.includeMaster = includeMaster;
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Shop> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer,
                       final Map<String, Integer> entityCount) throws Exception {

        if (this.includeMaster && tuple.getData().getMaster() != null) {
            handleInternal(tagShop(null, tuple.getData().getMaster()), writer, entityCount);
        }
        handleInternal(tagShop(null, tuple.getData()), writer, entityCount);

    }

    Tag tagShop(final Tag parent, final Shop shop) {

        final Tag sTag = tag(parent, "shop")
                .attr("id", shop.getShopId())
                .attr("guid", shop.getGuid())
                .attr("code", shop.getCode())
                .attr("master-code", shop.getMaster() != null ? shop.getMaster().getCode() : null);

        sTag
                .tag("presentation")
                    .tagChars("name", shop.getName())
                    .tagCdata("description", shop.getDescription())
                    .tagChars("theme-chain", shop.getFspointer())
                .end();

        sTag
                .tag("availability")
                    .attr("disabled", shop.isDisabled())
                .end();

        processUrls(shop, sTag);

        processAliases(shop, sTag);

        processCatalog(shop, sTag);

        processCarriers(shop, sTag);

        processFulfilmentCentres(shop, sTag);

        processAddressBook(shop, sTag);

        sTag.tagSeo(shop);

        sTag.tagExt(shop);

        return sTag.tagTime(shop)
                .end();

    }

    private void processAddressBook(final Shop shop, final Tag sTag) {

        final Customer addressBook = this.customerService.findSingleByCriteria(" where e.email = ?1", "#" + shop.getCode() + "#");

        if (addressBook != null) {

            final Tag aTag = sTag.tag("shop-addressbook");
            customerXmlEntityHandler.tagCustomer(aTag, addressBook);
            aTag.end();

        }

    }

    private void processFulfilmentCentres(final Shop shop, final Tag sTag) {

        final List<Warehouse> fcs = this.warehouseService.getByShopId(shop.getShopId(), true);

        if (CollectionUtils.isNotEmpty(fcs)) {

            final List<Warehouse> enabled = this.warehouseService.getByShopId(shop.getShopId(), false);
            final List<Long> enabledIds = enabled.stream().map(Warehouse::getWarehouseId).collect(Collectors.toList());

            final Tag cTag = sTag.tag("shop-fulfilment-centres");

            for (final Warehouse fc : fcs) {

                cTag.tag("shop-fulfilment-centre")
                        .attr("id", fc.getWarehouseId())
                        .attr("guid", fc.getGuid())
                        .attr("disabled", !enabledIds.contains(fc.getWarehouseId())).end();

            }

            cTag.end();

        }


    }

    private void processCarriers(final Shop shop, final Tag sTag) {

        final List<Carrier> carriers = this.carrierService.findCarriersByShopId(shop.getShopId(), true);

        if (CollectionUtils.isNotEmpty(carriers)) {

            final List<Carrier> enabled = this.carrierService.findCarriersByShopId(shop.getShopId(), false);
            final List<Long> enabledIds = enabled.stream().map(Carrier::getCarrierId).collect(Collectors.toList());

            final Tag cTag = sTag.tag("shop-carriers");

            for (final Carrier carrier : carriers) {

                cTag.tag("shop-carrier")
                        .attr("id", carrier.getCarrierId())
                        .attr("guid", carrier.getGuid())
                        .attr("disabled", !enabledIds.contains(carrier.getCarrierId())).end();

            }

            cTag.end();

        }

    }

    private void processCatalog(final Shop shop, final Tag sTag) {

        if (CollectionUtils.isNotEmpty(shop.getShopCategory())) {

            final Tag scTag = sTag.tag("shop-categories");

            for (final ShopCategory category : shop.getShopCategory()) {

                scTag
                        .tag("shop-category")
                        .attr("id", category.getCategory().getCategoryId())
                        .attr("guid", category.getCategory().getGuid())
                        .attr("rank", category.getRank())
                        .end();

            }

            scTag.end();
        }

    }

    private void processUrls(final Shop shop, final Tag sTag) {

        if (CollectionUtils.isNotEmpty(shop.getShopUrl())) {

            final Tag uTag = sTag.tag("shop-urls");

            for (final ShopUrl url : shop.getShopUrl()) {

                uTag
                        .tag("shop-url")
                            .attr("primary", url.isPrimary())
                            .attr("domain", url.getUrl())
                            .attr("theme-chain", url.getThemeChain())
                        .end();

            }

            uTag.end();
        }
    }

    private void processAliases(final Shop shop, final Tag sTag) {

        if (CollectionUtils.isNotEmpty(shop.getShopAlias())) {

            final Tag aTag = sTag.tag("shop-aliases");

            for (final ShopAlias alias : shop.getShopAlias()) {

                aTag
                        .tag("shop-alias")
                            .attr("name", alias.getAlias())
                        .end();

            }

            aTag.end();
        }
    }

    @Override
    public void setPrettyPrint(final boolean prettyPrint) {
        super.setPrettyPrint(prettyPrint);
        this.customerXmlEntityHandler.setPrettyPrint(prettyPrint);
    }

    /**
     * Inlclude master shop if available.
     *
     * @param includeMaster include master
     */
    public void setIncludeMaster(final boolean includeMaster) {
        this.includeMaster = includeMaster;
    }

    /**
     * Spring IoC.
     *
     * @param carrierService carrier service
     */
    public void setCarrierService(final CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    /**
     * Spring IoC.
     *
     * @param warehouseService warehouse service
     */
    public void setWarehouseService(final WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
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

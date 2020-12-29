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

import org.apache.commons.collections.CollectionUtils;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlEntityExportHandler;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.bulkexport.xml.XmlExportTuple;
import org.yes.cart.domain.entity.*;
import org.yes.cart.payment.persistence.entity.PaymentGatewayCallback;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.payment.service.PaymentGatewayParameterService;
import org.yes.cart.payment.service.PaymentModuleGenericService;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * User: denispavlov
 * Date: 19/05/2019
 * Time: 15:50
 */
public class SiteXmlHandler implements XmlEntityExportHandler<Shop> {

    private final ETypeXmlEntityHandler eTypeXmlEntityHandler = new ETypeXmlEntityHandler();
    private final AttributeGroupXmlEntityHandler attributeGroupXmlEntityHandler = new AttributeGroupXmlEntityHandler();
    private final AttributeXmlEntityHandler attributeXmlEntityHandler = new AttributeXmlEntityHandler();

    private final BrandXmlEntityHandler brandXmlEntityHandler = new BrandXmlEntityHandler();

    private final CountryXmlEntityHandler countryXmlEntityHandler = new CountryXmlEntityHandler();
    private final CountryStateXmlEntityHandler countryStateXmlEntityHandler = new CountryStateXmlEntityHandler();

    private final CategoryTreeXmlEntityHandler categoryTreeXmlEntityHandler = new CategoryTreeXmlEntityHandler(true);

    private final FulfilmentCentreXmlEntityHandler fulfilmentCentreXmlEntityHandler = new FulfilmentCentreXmlEntityHandler();

    private final ShippingProviderXmlEntityHandler shippingProviderXmlEntityHandler = new ShippingProviderXmlEntityHandler();

    private final OrganisationUserXmlEntityHandler organisationUserXmlEntityHandler = new OrganisationUserXmlEntityHandler();

    private final PromotionXmlEntityHandler promotionXmlEntityHandler = new PromotionXmlEntityHandler();

    private final SkuPriceXmlEntityHandler skuPriceXmlEntityHandler = new SkuPriceXmlEntityHandler();

    private final InventoryXmlEntityHandler inventoryXmlEntityHandler = new InventoryXmlEntityHandler();

    private final CustomerXmlEntityHandler customerXmlEntityHandler = new CustomerXmlEntityHandler();

    private final CustomerOrderXmlEntityHandler customerOrderXmlEntityHandler = new CustomerOrderXmlEntityHandler();

    private final CustomerOrderPaymentXmlEntityHandler customerOrderPaymentXmlEntityHandler = new CustomerOrderPaymentXmlEntityHandler();

    private final PaymentGatewayCallbackXmlEntityHandler paymentGatewayCallbackXmlEntityHandler = new PaymentGatewayCallbackXmlEntityHandler();
    private final PaymentGatewayParameterXmlEntityHandler paymentGatewayParameterXmlEntityHandler = new PaymentGatewayParameterXmlEntityHandler();

    private final TaxXmlEntityHandler taxXmlEntityHandler = new TaxXmlEntityHandler();
    private final TaxConfigXmlEntityHandler taxConfigXmlEntityHandler = new TaxConfigXmlEntityHandler();

    private final ShopXmlEntityHandler shopXmlEntityHandler = new ShopXmlEntityHandler(true);

    private EtypeService etypeService;
    private AttributeGroupService attributeGroupService;
    private AttributeService attributeService;

    private BrandService brandService;

    private CountryService countryService;
    private StateService stateService;

    private ShopService shopService;

    private CategoryService categoryService;

    private WarehouseService warehouseService;
    private SkuWarehouseService skuWarehouseService;

    private CarrierService carrierService;

    private ManagerService managerService;

    private PromotionService promotionService;
    private PriceService priceService;

    private CustomerService customerService;
    private CustomerOrderService customerOrderService;

    private CustomerOrderPaymentService customerOrderPaymentService;

    private TaxService taxService;
    private TaxConfigService taxConfigService;

    private PaymentModuleGenericService<PaymentGatewayCallback> paymentGatewayCallbackService;
    private PaymentGatewayParameterService paymentGatewayParameterService;


    @Override
    public void startXml(final OutputStreamWriter writer) throws Exception {
        // do nothing
    }

    @Override
    public void endXml(final OutputStreamWriter writer) throws Exception {
        // do nothing
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Shop> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer) throws Exception {

        final Shop shop = tuple.getData();

        statusListener.notifyMessage("exporting site {}", shop.getCode());

        if (!fileToExport.endsWith(".zip")) {
            statusListener.notifyError("wrong file name {}, file must end with '.zip'", fileToExport);
            return;
        }

        final File file = new File(fileToExport);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {

            final String prefix = shop.getCode() + "-";

            statusListener.notifyPing("exporting entity types");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "etypedata.xml",
                    eTypeXmlEntityHandler, etypeService,
                    "", null
            );

            statusListener.notifyPing("exporting attribute groups");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "attributegroupsdata.xml",
                    attributeGroupXmlEntityHandler, attributeGroupService,
                    "", null
            );

            statusListener.notifyPing("exporting attributes");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "attributedata.xml",
                    attributeXmlEntityHandler, attributeService,
                    "", null
            );

            statusListener.notifyPing("exporting brands");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "branddata.xml",
                    brandXmlEntityHandler, brandService,
                    "", null
            );

            statusListener.notifyPing("exporting countries");

            final List<String> assignedCountries = new ArrayList<>();
            assignedCountries.addAll(shop.getSupportedBillingCountriesAsList());
            assignedCountries.addAll(shop.getSupportedShippingCountriesAsList());

            if (!assignedCountries.isEmpty()) {
                addZipEntry(
                        statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                        zos, prefix + "countrydata.xml",
                        countryXmlEntityHandler, countryService,
                        " where e.countryCode in ?1", null, assignedCountries
                );

                addZipEntry(
                        statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                        zos, prefix + "countrystatedata.xml",
                        countryStateXmlEntityHandler, stateService,
                        " where e.countryCode in ?1", null, assignedCountries
                );
            }

            statusListener.notifyPing("exporting shop data");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "shopdata.xml",
                    shopXmlEntityHandler, shopService,
                    " where e.shopId = ?1", null, tuple.getData().getShopId()
            );

            statusListener.notifyPing("exporting shop catalog");

            final Collection<ShopCategory> assignedCategories = shop.getShopCategory();
            if (CollectionUtils.isNotEmpty(assignedCategories)) {

                final List<Long> ids = assignedCategories.stream().map(shopCategory -> shopCategory.getCategory().getCategoryId()).collect(Collectors.toList());

                addZipEntry(
                        statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                        zos, prefix + "catalogdata.xml",
                        categoryTreeXmlEntityHandler, categoryService,
                        " where e.categoryId in ?1", null, ids
                );

            }

            statusListener.notifyPing("exporting shop fulfilment centres and inventory");

            final List<Warehouse> assignedFulfilmentCentres = warehouseService.getByShopId(shop.getShopId(), true);
            if (CollectionUtils.isNotEmpty(assignedFulfilmentCentres)) {

                final List<Long> ids = assignedFulfilmentCentres.stream().map(centre -> centre.getWarehouseId()).collect(Collectors.toList());

                addZipEntry(
                        statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                        zos, prefix + "fulfilmentcentredata.xml",
                        fulfilmentCentreXmlEntityHandler, warehouseService,
                        " where e.warehouseId in ?1", null, ids
                );

                addZipEntry(
                        statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                        zos, prefix + "inventorydata.xml",
                        inventoryXmlEntityHandler, skuWarehouseService,
                        " where e.warehouse.warehouseId in ?1", null, ids
                );

            }

            statusListener.notifyPing("exporting shop shipping providers");

            final List<Carrier> assignedProviders = carrierService.findCarriersByShopId(shop.getShopId(), true);
            if (CollectionUtils.isNotEmpty(assignedProviders)) {

                final List<Long> ids = assignedProviders.stream().map(provider -> provider.getCarrierId()).collect(Collectors.toList());

                addZipEntry(
                        statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                        zos, prefix + "shippingprovidersdata.xml",
                        shippingProviderXmlEntityHandler, carrierService,
                        " where e.carrierId in ?1", null, ids
                );

            }

            statusListener.notifyPing("exporting promotions");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "promotionsdata.xml",
                    promotionXmlEntityHandler, promotionService,
                    " where e.shopCode = ?1", null, shop.getCode()
            );

            statusListener.notifyPing("exporting price lists");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "pricesdata.xml",
                    skuPriceXmlEntityHandler, priceService,
                    " where e.shop.shopId = ?1", null, shop.getShopId()
            );

            statusListener.notifyPing("exporting tax configurations");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "taxdata.xml",
                    taxXmlEntityHandler, taxService,
                    " where e.shopCode = ?1", null, shop.getCode()
            );

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "taxconfigdata.xml",
                    taxConfigXmlEntityHandler, taxConfigService,
                    " where e.tax.shopCode = ?1", null, shop.getCode()
            );

            statusListener.notifyPing("exporting customer profiles");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "customerdata.xml",
                    customerXmlEntityHandler, customerService,
                    " ", entity -> {
                        final Customer customer = (Customer) entity;
                        if (!customer.isShop()) {
                            for (CustomerShop cs : customer.getShops()) {
                                if (cs.getShop().getShopId() == shop.getShopId()) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
            );

            statusListener.notifyPing("exporting customer orders");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "customerorderdata.xml",
                    customerOrderXmlEntityHandler, customerOrderService,
                    " where e.shop.shopId = ?1", null, shop.getShopId()
            );

            statusListener.notifyPing("exporting customer payments");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "customerorderpaymentdata.xml",
                    customerOrderPaymentXmlEntityHandler, customerOrderPaymentService,
                    " where e.shopCode = ?1", null, shop.getCode()
            );

            statusListener.notifyPing("exporting payment gateway callbacks");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "paymentgatewaycallbackdata.xml",
                    paymentGatewayCallbackXmlEntityHandler, paymentGatewayCallbackService,
                    " where e.shopCode = ?1", null, shop.getCode()
            );

            statusListener.notifyPing("exporting payment gateway parameters");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "paymentgatewayparameterdata.xml",
                    paymentGatewayParameterXmlEntityHandler, paymentGatewayParameterService,
                    " ", entity -> {
                        final PaymentGatewayParameter parameter = (PaymentGatewayParameter) entity;
                        return parameter.getLabel() == null ||
                                (parameter.getLabel().startsWith("#") &&
                                !parameter.getLabel().startsWith("#" + shop.getCode()));
                    }
            );

            statusListener.notifyPing("exporting managers");

            addZipEntry(
                    statusListener, xmlExportDescriptor, xmlValueAdapter, fileToExport,
                    zos, prefix + "organisationuserdata.xml",
                    organisationUserXmlEntityHandler, managerService,
                    " ", entity -> {
                        final Manager manager = (Manager) entity;
                        for (ManagerShop ms : manager.getShops()) {
                            if (ms.getShop().getShopId() == shop.getShopId()) {
                                return false;
                            }
                        }
                        return true;
                    }
            );


        }

    }


    private void addZipEntry(final JobStatusListener statusListener,
                             final XmlExportDescriptor xmlExportDescriptor,
                             final XmlValueAdapter xmlValueAdapter,
                             final String fileToExport,
                             final ZipOutputStream zos,
                             final String entryName,
                             final XmlEntityExportHandler handler,
                             final GenericService genericService,
                             final String entitiesToExportCmd,
                             final ExclusionFilter exclusionFilter,
                             final Object... entitiesToExportCmdParams) throws Exception {

        final ZipEntry zosItem = new ZipEntry(entryName);
        zos.putNextEntry(zosItem);

        final OutputStreamWriter subWriter = new OutputStreamWriter(zos, StandardCharsets.UTF_8);

        handler.startXml(subWriter);
        genericService.findByCriteriaIterator(entitiesToExportCmd, entitiesToExportCmdParams, entity -> {

            if (exclusionFilter == null || !exclusionFilter.exclude(entity)) {
                final XmlExportTuple tuple = new XmlExportTupleImpl(entity);
                try {
                    handler.handle(statusListener, xmlExportDescriptor, tuple, xmlValueAdapter, fileToExport, subWriter);
                } catch (Exception e) {
                    statusListener.notifyError("Error during export of : {}, {}",
                            e,
                            tuple,
                            e.getMessage());
                }
            }
            return true;
        });
        handler.endXml(subWriter);

    }

    private void addZipEntry(final JobStatusListener statusListener,
                             final XmlExportDescriptor xmlExportDescriptor,
                             final XmlValueAdapter xmlValueAdapter,
                             final String fileToExport,
                             final ZipOutputStream zos,
                             final String entryName,
                             final XmlEntityExportHandler handler,
                             final PaymentModuleGenericService genericService,
                             final String entitiesToExportCmd,
                             final ExclusionFilter exclusionFilter,
                             final Object... entitiesToExportCmdParams) throws Exception {

        final ZipEntry zosItem = new ZipEntry(entryName);
        zos.putNextEntry(zosItem);

        final OutputStreamWriter subWriter = new OutputStreamWriter(zos, StandardCharsets.UTF_8);

        handler.startXml(subWriter);
        final List entities = genericService.findByCriteria(entitiesToExportCmd, entitiesToExportCmdParams);
        for (final Object entity : entities) {
            if (exclusionFilter == null || !exclusionFilter.exclude(entity)) {
                final XmlExportTuple tuple = new XmlExportTupleImpl(entity);
                try {
                    handler.handle(statusListener, xmlExportDescriptor, tuple, xmlValueAdapter, fileToExport, subWriter);
                } catch (Exception e) {
                    statusListener.notifyError("Error during export of : {}, {}",
                            e,
                            tuple,
                            e.getMessage());
                }
            }
        }
        handler.endXml(subWriter);

    }

    interface ExclusionFilter<T> {
        boolean exclude(T entity);
    }


    /**
     * Spring IoC.
     *
     * @param prettyPrint set pretty print mode (new lines and indents)
     */
    public void setPrettyPrint(final boolean prettyPrint) {

        this.eTypeXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.attributeGroupXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.attributeXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.brandXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.countryXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.countryStateXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.shopXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.categoryTreeXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.fulfilmentCentreXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.shippingProviderXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.organisationUserXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.promotionXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.skuPriceXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.customerXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.customerOrderXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.customerOrderPaymentXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.paymentGatewayCallbackXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.paymentGatewayParameterXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.inventoryXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.taxXmlEntityHandler.setPrettyPrint(prettyPrint);
        this.taxConfigXmlEntityHandler.setPrettyPrint(prettyPrint);

    }


    /**
     * Spring IoC.
     *
     * @param carrierService carrier service
     */
    public void setCarrierService(final CarrierService carrierService) {
        this.carrierService = carrierService;
        this.shopXmlEntityHandler.setCarrierService(carrierService);
    }

    /**
     * Spring IoC.
     *
     * @param warehouseService warehouse service
     */
    public void setWarehouseService(final WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
        this.shopXmlEntityHandler.setWarehouseService(warehouseService);
    }

    /**
     * Spring IoC.
     *
     * @param customerService customer service
     */
    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
        this.shopXmlEntityHandler.setCustomerService(customerService);
    }

    /**
     * Spring IoC.
     *
     * @param etypeService service
     */
    public void setEtypeService(final EtypeService etypeService) {
        this.etypeService = etypeService;
    }

    /**
     * Spring IoC.
     * @param attributeGroupService service
     */
    public void setAttributeGroupService(final AttributeGroupService attributeGroupService) {
        this.attributeGroupService = attributeGroupService;
    }

    /**
     * Spring IoC.
     *
     * @param attributeService service
     */
    public void setAttributeService(final AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    /**
     * Spring IoC.
     *
     * @param brandService service
     */
    public void setBrandService(final BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * Spring IoC.
     *
     * @param countryService service
     */
    public void setCountryService(final CountryService countryService) {
        this.countryService = countryService;
    }

    /**
     * Spring IoC.
     *
     * @param stateService service
     */
    public void setStateService(final StateService stateService) {
        this.stateService = stateService;
    }

    /**
     * Spring IoC.
     *
     * @param shopService service
     */
    public void setShopService(final ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * Spring IoC.
     *
     * @param categoryService service
     */
    public void setCategoryService(final CategoryService categoryService) {
        this.categoryService = categoryService;
        this.categoryTreeXmlEntityHandler.setCategoryService(categoryService);
    }

    /**
     * Spring IoC.
     *
     * @param managerService service
     */
    public void setManagerService(final ManagerService managerService) {
        this.managerService = managerService;
    }


    /**
     * Spring IoC.
     *
     * @param managerRoleService role service
     */
    public void setManagerRoleService(final GenericService<ManagerRole> managerRoleService) {
        this.organisationUserXmlEntityHandler.setManagerRoleService(managerRoleService);
    }

    /**
     * Spring IoC.
     *
     * @param promotionService service
     */
    public void setPromotionService(final PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    /**
     * Spring IoC.
     *
     * @param promotionCouponService service
     */
    public void setPromotionCouponService(final PromotionCouponService promotionCouponService) {
        this.promotionXmlEntityHandler.setPromotionCouponService(promotionCouponService);
        this.customerOrderXmlEntityHandler.setPromotionCouponService(promotionCouponService);
    }

    /**
     * Spring IoC.
     *
     * @param priceService service
     */
    public void setPriceService(final PriceService priceService) {
        this.priceService = priceService;
    }

    /**
     * Spring IoC.
     *
     * @param customerOrderService service
     */
    public void setCustomerOrderService(final CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    /**
     * Spring IoC.
     *
     * @param paymentGatewayCallbackService service
     */
    public void setPaymentGatewayCallbackService(final PaymentModuleGenericService<PaymentGatewayCallback> paymentGatewayCallbackService) {
        this.paymentGatewayCallbackService = paymentGatewayCallbackService;
    }

    /**
     * Spring IoC.
     *
     * @param paymentGatewayParameterService service
     */
    public void setPaymentGatewayParameterService(final PaymentGatewayParameterService paymentGatewayParameterService) {
        this.paymentGatewayParameterService = paymentGatewayParameterService;
    }

    /**
     * Spring IoC.
     *
     * @param customerOrderPaymentService service
     */
    public void setCustomerOrderPaymentService(final CustomerOrderPaymentService customerOrderPaymentService) {
        this.customerOrderPaymentService = customerOrderPaymentService;
    }

    /**
     * Spring IoC.
     *
     * @param skuWarehouseService service
     */
    public void setSkuWarehouseService(final SkuWarehouseService skuWarehouseService) {
        this.skuWarehouseService = skuWarehouseService;
    }

    /**
     * Spring IoC.
     *
     * @param taxService service
     */
    public void setTaxService(final TaxService taxService) {
        this.taxService = taxService;
    }

    /**
     * Spring IoC.
     *
     * @param taxConfigService service
     */
    public void setTaxConfigService(final TaxConfigService taxConfigService) {
        this.taxConfigService = taxConfigService;
    }
}

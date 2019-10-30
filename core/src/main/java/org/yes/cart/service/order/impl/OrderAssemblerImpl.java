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

package org.yes.cart.service.order.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.i18n.impl.NonI18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.order.*;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.TimeContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class OrderAssemblerImpl implements OrderAssembler {

    private final Logger LOG = LoggerFactory.getLogger(OrderAssemblerImpl.class);

    private final EntityFactory entityFactory;
    private final ShopService shopService;
    private final ProductService productService;
    private final ProductSkuService productSkuService;
    private final PriceResolver priceResolver;
    private final InventoryResolver inventoryResolver;
    private final OrderAddressFormatter addressFormatter;
    private final PromotionCouponService promotionCouponService;
    private final AddressService addressService;
    private final CustomerService customerService;
    private final AttributeService attributeService;
    private final WarehouseService warehouseService;

    /**
     * Create order assembler.
     *
     * @param genericDAO             generic DAO
     * @param customerService        customer service
     * @param shopService            shop service
     * @param productService         product service
     * @param productSkuService      product sku service
     * @param priceResolver           price service
     * @param addressFormatter       format string to create address in one string from {@link Address} entity.
     * @param promotionCouponService coupon service
     * @param addressService         address service
     * @param attributeService       attribute service
     */
    public OrderAssemblerImpl(final GenericDAO genericDAO,
                              final CustomerService customerService,
                              final ShopService shopService,
                              final ProductService productService,
                              final ProductSkuService productSkuService,
                              final PriceResolver priceResolver,
                              final InventoryResolver inventoryResolver,
                              final OrderAddressFormatter addressFormatter,
                              final PromotionCouponService promotionCouponService,
                              final AddressService addressService,
                              final AttributeService attributeService,
                              final WarehouseService warehouseService) {
        this.productService = productService;
        this.priceResolver = priceResolver;
        this.inventoryResolver = inventoryResolver;
        this.promotionCouponService = promotionCouponService;
        this.attributeService = attributeService;
        this.entityFactory = genericDAO.getEntityFactory();
        this.customerService = customerService;
        this.addressService = addressService;
        this.shopService = shopService;
        this.productSkuService = productSkuService;
        this.addressFormatter = addressFormatter;
        this.warehouseService = warehouseService;
    }

    /**
     * Create and fill {@link CustomerOrder} from   from {@link ShoppingCart}.
     *
     * @param shoppingCart given shopping cart
     * @param orderNumber order number
     *
     * @return not persisted but filled with data order
     */
    @Override
    public CustomerOrder assembleCustomerOrder(final ShoppingCart shoppingCart, final String orderNumber) throws OrderAssemblyException {
        return assembleCustomerOrder(shoppingCart, orderNumber, false);
    }

    /**
     * Create and fill {@link CustomerOrder} from   from {@link ShoppingCart}.
     *
     * @param shoppingCart given shopping cart
     * @param orderNumber order number
     * @param temp         if set to true then order number is not generated and coupon usage is not created
     *
     * @return not persisted but filled with data order
     */
    @Override
    public CustomerOrder assembleCustomerOrder(final ShoppingCart shoppingCart, final String orderNumber, final boolean temp) throws OrderAssemblyException {

        final Total cartTotal = shoppingCart.getTotal();

        if (cartTotal == null
                || cartTotal.getListSubTotal() == null
                || cartTotal.getSubTotal() == null
                || cartTotal.getSubTotalTax() == null
                || cartTotal.getSubTotalAmount() == null) {
            LOG.debug("Unable to create order for cart as one of the total is missing in cart {}", shoppingCart.getGuid());
            throw new OrderAssemblyException("No order total");
        }

        final CustomerOrder customerOrder = entityFactory.getByIface(CustomerOrder.class);

        // sets shop from cache
        customerOrder.setShop(shopService.getById(shoppingCart.getShoppingContext().getCustomerShopId()));

        // Basic data
        customerOrder.setLocale(shoppingCart.getCurrentLocale());
        customerOrder.setCurrency(shoppingCart.getCurrencyCode());
        customerOrder.setOrderStatus(CustomerOrder.ORDER_STATUS_NONE);
        customerOrder.setOrderTimestamp(now());
        customerOrder.setGuid(shoppingCart.getGuid());
        customerOrder.setCartGuid(shoppingCart.getGuid());
        customerOrder.setOrderIp(shoppingCart.getShoppingContext().getResolvedIp());

        customerOrder.setListPrice(cartTotal.getListSubTotal());
        customerOrder.setPrice(cartTotal.getSubTotal());
        customerOrder.setPromoApplied(cartTotal.isOrderPromoApplied());
        customerOrder.setAppliedPromo(cartTotal.getAppliedOrderPromo());
        customerOrder.setGrossPrice(cartTotal.getSubTotalAmount());
        customerOrder.setNetPrice(cartTotal.getSubTotalAmount().subtract(cartTotal.getSubTotalTax()));

        fillCustomerData(customerOrder, shoppingCart, temp);

        fillB2BData(customerOrder, shoppingCart, temp);

        fillOrderDetails(customerOrder, shoppingCart, temp);

        customerOrder.setOrdernum(orderNumber);

        fillCoupons(customerOrder, shoppingCart, temp);

        return customerOrder;
    }

    /**
     * Add B2B data from custom attributes.
     *
     * @param customerOrder order to fill
     * @param shoppingCart  cart
     * @param temp temporary flag
     */
    private void fillB2BData(final CustomerOrder customerOrder, final ShoppingCart shoppingCart, final boolean temp) throws OrderAssemblyException {

        customerOrder.setB2bRef(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_REF));
        customerOrder.setB2bEmployeeId(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_EMPLOYEE_ID));
        customerOrder.setB2bChargeId(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_CHARGE_ID));
        customerOrder.setB2bRequireApprove(Boolean.valueOf(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_APPROVE_ORDER)));
        customerOrder.setB2bApprovedBy(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_APPROVED_BY));
        final String approvedDate = shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_APPROVED_DATE);
        if (approvedDate != null) {
            final LocalDateTime approve = DateUtils.ldtParseSDT(approvedDate);
            if (approve == null) {
                LOG.debug("Unable to create order for cart as b2bApprovedDate has invalid format in cart {}, {} has to be \"yyyy-MM-dd HH:mm:ss\"", shoppingCart.getGuid(), approvedDate);
                throw new OrderAssemblyException("Order b2bApprovedDate has invalid format: " + approvedDate + ", has to be \"yyyy-MM-dd HH:mm:ss\"");
            }
            customerOrder.setB2bApprovedDate(approve);
        }

    }

    /**
     * Add coupons information.
     *
     * @param customerOrder order to fill
     * @param shoppingCart  cart
     * @param temp temporary flag
     */
    private void fillCoupons(final CustomerOrder customerOrder, final ShoppingCart shoppingCart, final boolean temp) throws OrderAssemblyException {

        if (!temp && !shoppingCart.getCoupons().isEmpty()) {

            final List<String> appliedCouponCodes = shoppingCart.getAppliedCoupons();

            if (!appliedCouponCodes.isEmpty()) {
                for (final String code : appliedCouponCodes) {

                    final PromotionCoupon coupon = promotionCouponService.findValidPromotionCoupon(code, shoppingCart.getCustomerEmail());
                    if (coupon == null) {
                        LOG.debug("Unable to create order for cart {}, coupon code is invalid {}", shoppingCart.getGuid(), code);
                        throw new CouponCodeInvalidException(code);
                    }

                    final PromotionCouponUsage usage = entityFactory.getByIface(PromotionCouponUsage.class);
                    usage.setCouponCode(coupon.getCode());
                    usage.setCustomerEmail(shoppingCart.getCustomerEmail());
                    usage.setCustomerOrder(customerOrder);

                    customerOrder.getCoupons().add(usage); // Usage is tracked by order state manager listener

                }
            }

        }
    }

    /**
     * Fill customer data
     *
     * @param customerOrder order to fill
     * @param shoppingCart  cart
     * @param temp temporary flag
     */
    private void fillCustomerData(final CustomerOrder customerOrder, final ShoppingCart shoppingCart, final boolean temp) throws OrderAssemblyException {

        final boolean guest = shoppingCart.getLogonState() != ShoppingCart.LOGGED_IN;

        final Shop customerShop = customerOrder.getShop();
        final Shop configShop = customerShop.getMaster() != null ? customerShop.getMaster() : customerShop;

        final Customer customer = customerService.getCustomerByEmail(
                guest ? shoppingCart.getGuid() : shoppingCart.getCustomerEmail(),
                customerOrder.getShop()
        );

        if (customer != null) {
            long selectedBillingAddressId = shoppingCart.getOrderInfo().getBillingAddressId() != null ? shoppingCart.getOrderInfo().getBillingAddressId() : 0L;
            long selectedShippingAddressId = shoppingCart.getOrderInfo().getDeliveryAddressId()!= null ? shoppingCart.getOrderInfo().getDeliveryAddressId() : 0L;


            boolean billingNotRequired = shoppingCart.getOrderInfo().isBillingAddressNotRequired();
            boolean shippingNotRequired = shoppingCart.getOrderInfo().isDeliveryAddressNotRequired();

            Address billingAddress = null;
            Address shippingAddress = null;

            for (final Address address : getAddressbook(customerShop, customer)) {
                if (address.getAddressId() == selectedBillingAddressId) {
                    billingAddress = address;
                }
                if (address.getAddressId() == selectedShippingAddressId) {
                    shippingAddress = address;
                }
                if (billingAddress != null && shippingAddress != null) {
                    break;
                }
            }

            if (billingAddress == null && !billingNotRequired) {
                billingAddress = getCustomerDefaultAddress(customerShop, customer, Address.ADDR_TYPE_BILLING);
            }
            if (shippingAddress == null && !shippingNotRequired) {
                shippingAddress = getCustomerDefaultAddress(customerShop, customer, Address.ADDR_TYPE_SHIPPING);
            }

            final boolean sameAddress = !shoppingCart.isSeparateBillingAddress() || billingAddress == null;

            if (shippingNotRequired) {
                customerOrder.setShippingAddress("");
            } else if (shippingAddress == null) {
                LOG.debug("Unable to create order for cart {}, missing shipping address", shoppingCart.getGuid());
                throw new OrderAssemblyException("Shipping address is required");
            } else {
                customerOrder.setShippingAddress(formatAddress(shippingAddress, configShop, customer, customerOrder.getLocale()));
            }

            if (sameAddress) {
                billingAddress = shippingAddress;
            }

            if (billingNotRequired) {
                customerOrder.setBillingAddress("");
            } else if (billingAddress == null) {
                LOG.debug("Unable to create order for cart {}, missing billing address", shoppingCart.getGuid());
                throw new OrderAssemblyException("Billing address is required");
            } else {
                customerOrder.setBillingAddress(formatAddress(billingAddress, configShop, customer, customerOrder.getLocale()));
            }

            if (!temp) {

                final Address orderShippingAddress = createCopy(shippingAddress);
                final Address orderBillingAddress;
                if (sameAddress) {
                    orderBillingAddress = orderShippingAddress;
                } else {
                    orderBillingAddress = createCopy(billingAddress);
                }

                if (!billingNotRequired) {
                    customerOrder.setBillingAddressDetails(orderBillingAddress);
                }
                if (!shippingNotRequired) {
                    customerOrder.setShippingAddressDetails(orderShippingAddress);
                }

            }

            if (!customer.isGuest()) {
                customerOrder.setCustomer(customer);
            }

            customerOrder.setEmail(customer.getContactEmail());
            customerOrder.setSalutation(customer.getSalutation());
            customerOrder.setFirstname(customer.getFirstname());
            customerOrder.setLastname(customer.getLastname());
            customerOrder.setMiddlename(customer.getMiddlename());
        }

        customerOrder.setOrderMessage(shoppingCart.getOrderMessage());
        customerOrder.setB2bRemarks(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_ORDER_REMARKS_ID));

        final LocalDateTime requestedDate =
                DateUtils.ldtFrom(
                        NumberUtils.toLong(shoppingCart.getOrderInfo().getDetailByKey(
                                AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID), 0)
                );
        if (requestedDate.isAfter(now())) {
            customerOrder.setRequestedDeliveryDate(requestedDate);
        }


        /*
            Copy cart order custom attributes.
            The details will be stored in "attributeOrder_" + attribute code, e.g. attributeOrder_GIFT_MESSAGE

            The values are stored by code ("attributeOrder:" + code) with value being value and display value being name
            of 'attribute + ":" + value'
         */
        if (!temp) {

            final Map<String, I18NModel> attrI18n = this.attributeService.getAllAttributeNames();

            final String attributeIdPrefix = AttributeNamesKeys.Cart.ORDER_INFO_ORDER_ATTRIBUTE_ID + "_";
            for (final Map.Entry<String, String> entry : shoppingCart.getOrderInfo().getDetails().entrySet()) {
                if (entry.getKey().startsWith(attributeIdPrefix)) {
                    final String avCode = entry.getKey().substring(attributeIdPrefix.length());
                    final I18NModel nameModel = attrI18n.get(avCode);
                    customerOrder.putValue(
                            AttributeNamesKeys.Cart.ORDER_INFO_ORDER_ATTRIBUTE_ID + ":" + avCode,
                            entry.getValue(),
                            convertToValueI18nModel(nameModel, new NonI18NModel(entry.getValue()), avCode)
                    );
                }
            }

            if (shoppingCart.getShoppingContext().isManagedCart()) {

                final String timestamp = DateUtils.formatSDT();
                final String auditKey = "MGR/ORD: " + timestamp;

                customerOrder.putValue(auditKey,
                        shoppingCart.getShoppingContext().getManagerName() + " / " + shoppingCart.getShoppingContext().getManagerEmail(),
                        newDefaultModel("AUDITEXPORT")
                );
            }
        }


    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

    /**
     * Get customer address book for this shop
     *
     * @param shop shop
     * @param customer customer
     *
     * @return addresses
     */
    protected Collection<Address> getAddressbook(final Shop shop, final Customer customer) {
        return customer.getAddress();
    }

    /**
     * Get default customer address for this shop.
     *
     * @param shop shop
     * @param customer customer
     * @param addrType type of address
     *
     * @return address to use
     */
    protected Address getCustomerDefaultAddress(final Shop shop, final Customer customer, final String addrType) {
        return customer.getDefaultAddress(addrType);
    }

    /**
     * Fill details records in order.
     *
     * @param customerOrder order to fill
     * @param shoppingCart  cart
     * @param temp temporary flag
     */
    private void fillOrderDetails(final CustomerOrder customerOrder, final ShoppingCart shoppingCart, final boolean temp) throws OrderAssemblyException {

        final Map<String, Warehouse> suppliersByCode = getSuppliersMap(shoppingCart);

        for (CartItem item : shoppingCart.getCartItemList()) {

            if (item.getPrice() == null
                    || item.getSalePrice() == null
                    || item.getListPrice() == null
                    || item.getNetPrice() == null
                    || item.getGrossPrice() == null
                    || item.getTaxRate() == null
                    || item.getTaxCode() == null
                    || item.getProductSkuCode() == null) {
                LOG.debug("Unable to create order for cart {}, one of the totals is missing for item {}", shoppingCart.getGuid(), item);
                throw new SkuUnavailableException(item.getProductSkuCode(), item.getProductName(), false);
            }

            final Warehouse supplier = suppliersByCode.get(item.getSupplierCode());

            if (supplier == null) {
                // Unknown supplier, must not allow order to proceed
                LOG.debug("Unable to create order for cart {}, item {} has invalid supplier {}", shoppingCart.getGuid(), item, item.getSupplierCode());
                throw new SkuUnavailableException(item.getProductSkuCode(), item.getProductName(), false);
            }

            CustomerOrderDet customerOrderDet = entityFactory.getByIface(CustomerOrderDet.class);
            customerOrderDet.setCustomerOrder(customerOrder);
            customerOrder.getOrderDetail().add(customerOrderDet);

            customerOrderDet.setPrice(item.getPrice());
            customerOrderDet.setSalePrice(item.getSalePrice());
            customerOrderDet.setListPrice(item.getListPrice());
            customerOrderDet.setQty(item.getQty());
            customerOrderDet.setGift(item.isGift());
            customerOrderDet.setPromoApplied(item.isPromoApplied());
            customerOrderDet.setFixedPrice(item.isFixedPrice());
            customerOrderDet.setAppliedPromo(item.getAppliedPromo());
            customerOrderDet.setNetPrice(item.getNetPrice());
            customerOrderDet.setGrossPrice(item.getGrossPrice());
            customerOrderDet.setTaxCode(item.getTaxCode());
            customerOrderDet.setTaxRate(item.getTaxRate());
            customerOrderDet.setTaxExclusiveOfPrice(item.isTaxExclusiveOfPrice());

            customerOrderDet.setProductSkuCode(item.getProductSkuCode());
            fillOrderDetail(customerOrder, shoppingCart, item, customerOrderDet, supplier, temp);

            customerOrderDet.setProductName(item.getProductName());
            customerOrderDet.setSupplierCode(item.getSupplierCode());

            fillOrderDetailPricing(customerOrder, shoppingCart, item, customerOrderDet, temp);

        }

    }

    private Map<String, Warehouse> getSuppliersMap(final ShoppingCart shoppingCart) {

        return warehouseService.getByShopIdMapped(shoppingCart.getShoppingContext().getCustomerShopId(), false);

    }

    /**
     * Fill specific product details for current item.
     *
     * @param customerOrder    order
     * @param shoppingCart     cart
     * @param item             item
     * @param customerOrderDet item to populate
     * @param supplier         supplier
     * @param temp             temporary
     *  @throws OrderAssemblyException error
     */
    protected void fillOrderDetail(final CustomerOrder customerOrder, final ShoppingCart shoppingCart, final CartItem item, final CustomerOrderDet customerOrderDet, final Warehouse supplier, final boolean temp) throws OrderAssemblyException {

        /*
            Check availability at given fulfilment centre
         */
        final SkuWarehouse inventory = inventoryResolver.findByWarehouseSku(supplier, item.getProductSkuCode());
        if (inventory == null) {
            // No inventory for this SKU provided by this supplier
            LOG.debug("Unable to create order for cart {}, item {} has no inventory for supplier {}", shoppingCart.getGuid(), item, item.getSupplierCode());
            throw new SkuUnavailableException(item.getProductSkuCode(), item.getProductName(), false);
        }
        final LocalDateTime now = now();
        final int availability = inventory.getAvailability();
        final boolean isAvailableNow = availability != SkuWarehouse.AVAILABILITY_SHOWROOM && inventory.isAvailable(now);
        if (!isAvailableNow) {
            LOG.debug("Unable to create order for cart {}, item {} has no available inventory for supplier {}", shoppingCart.getGuid(), item, item.getSupplierCode());
            throw new SkuUnavailableException(item.getProductSkuCode(), item.getProductName(), false);
        }
        if (!inventory.isAvailableToSell(item.getQty(), true)) {
            LOG.debug("Unable to create order for cart {}, item {} does not have enough inventory for supplier {}", shoppingCart.getGuid(), item, item.getSupplierCode());
            throw new SkuUnavailableException(item.getProductSkuCode(), item.getProductName(), true);
        }


        if (!item.isGift()) {
            // Copy line remark which will be stored in "b2bRemarksLine" + FF + SKU detail, e.g. b2bRemarksLineMAIN_ABC0001
            customerOrderDet.setB2bRemarks(shoppingCart.getOrderInfo().getDetailByKey(
                    AttributeNamesKeys.Cart.ORDER_INFO_B2B_ORDER_LINE_REMARKS_ID + item.getSupplierCode() + "_" + item.getProductSkuCode()));

            final String managed = shoppingCart.getOrderInfo().getDetailByKey(
                    AttributeNamesKeys.Cart.ORDER_INFO_ORDER_LINE_MANAGED_LIST + item.getSupplierCode() + "_" + item.getProductSkuCode());
            customerOrderDet.putValue(AttributeNamesKeys.Cart.ORDER_INFO_ORDER_LINE_MANAGED_LIST, managed, null);
        }

        /*
            Copy cart item custom attributes.
            The details will be stored in "attributeLine" + SKU + "_" + attribute code, e.g. attributeLineABC0001_GIFT_MESSAGE

            The values are stored by code ("attributeLine:" + code) with value being value and display value being name
            of 'attribute + ":" + value'
         */
        if (!temp) {

            final Map<String, I18NModel> attrI18n = this.attributeService.getAllAttributeNames();

            final String attributeIdPrefix = AttributeNamesKeys.Cart.ORDER_INFO_ORDER_LINE_ATTRIBUTE_ID + item.getSupplierCode() + "_" + item.getProductSkuCode() + "_";
            for (final Map.Entry<String, String> entry : shoppingCart.getOrderInfo().getDetails().entrySet()) {
                if (entry.getKey().startsWith(attributeIdPrefix)) {
                    final String avCode = entry.getKey().substring(attributeIdPrefix.length());
                    final I18NModel nameModel = attrI18n.get(avCode);
                    customerOrderDet.putValue(
                            AttributeNamesKeys.Cart.ORDER_INFO_ORDER_LINE_ATTRIBUTE_ID + ":" + avCode,
                            entry.getValue(),
                            convertToValueI18nModel(nameModel, new NonI18NModel(entry.getValue()), avCode)
                    );
                }
            }
        }

        /*
            Copy product specific attributes that are defined at shop level in SHOP_PRODUCT_STORED_ATTRIBUTES
            The value will be looked up at product level first and then SKU level so that SKU specific overrides work
            This is needed to preserve basic details about the product in the long run (say if product is deleted)

            The values are stored by code with value being av.val and display value being name of
            'attribute + ":" + displayable value'
         */
        final ProductSku sku = productSkuService.getProductSkuBySkuCode(item.getProductSkuCode());
        if (sku != null && !temp) {
            // stored attributes are configured per customer group
            final List<String> storedAttributes = customerOrder.getShop().getProductStoredAttributesAsList();
            if (CollectionUtils.isNotEmpty(storedAttributes)) {

                final Map<String, I18NModel> attrI18n = this.attributeService.getAllAttributeNames();

                // fill product first
                final Product product = productService.getProductById(sku.getProduct().getProductId(), true);
                for (final AttrValue av : product.getAttributes()) {
                    if (storedAttributes.contains(av.getAttributeCode())) {
                        final I18NModel nameModel = attrI18n.get(av.getAttributeCode());
                        final I18NModel displayValue = new FailoverStringI18NModel(av.getDisplayVal(), av.getVal());
                        customerOrderDet.putValue(
                                av.getAttributeCode(),
                                av.getVal(),
                                convertToValueI18nModel(nameModel, displayValue, av.getAttributeCode())
                        );
                    }
                }
                // fill SKU specific (will override product ones)
                for (final AttrValue av : sku.getAttributes()) {
                    if (storedAttributes.contains(av.getAttributeCode())) {
                        final I18NModel nameModel = attrI18n.get(av.getAttributeCode());
                        final I18NModel displayValue = new FailoverStringI18NModel(av.getDisplayVal(), av.getVal());
                        customerOrderDet.putValue(
                                av.getAttributeCode(),
                                av.getVal(),
                                convertToValueI18nModel(nameModel, displayValue, av.getAttributeCode())
                        );
                    }
                }
            }
        }
    }

    /**
     * Fill price specific product details for current item.
     *
     * @param customerOrder    order
     * @param shoppingCart     cart
     * @param item             item
     * @param customerOrderDet item to populate
     * @param temp             temporary
     *
     * @throws OrderAssemblyException error
     */
    private void fillOrderDetailPricing(final CustomerOrder customerOrder, final ShoppingCart shoppingCart, final CartItem item, final CustomerOrderDet customerOrderDet, final boolean temp) throws OrderAssemblyException {
        if (!temp) {

            final long customerShopId = shoppingCart.getShoppingContext().getCustomerShopId();
            final long masterShopId = shoppingCart.getShoppingContext().getShopId();
            // Fallback only if we have a B2B non-strict mode
            final Long fallbackShopId = masterShopId == customerShopId || shopService.getById(customerShopId).isB2BStrictPriceActive() ? null : masterShopId;
            final String currency = shoppingCart.getCurrencyCode();

            final String policyID = "COST" + (StringUtils.isBlank(item.getSupplierCode()) ? "" : "_" + item.getSupplierCode());

            final SkuPrice price = priceResolver.getMinimalPrice(
                    null,
                    item.getProductSkuCode(),
                    customerShopId,
                    fallbackShopId,
                    currency,
                    item.getQty(),
                    false,
                    policyID,
                    item.getSupplierCode()
            );
            
            if (price != null && price.getSkuPriceId() > 0L) {
                final BigDecimal cost = MoneyUtils.secondOrFirst(price.getSalePriceForCalculation());
                customerOrderDet.putValue(
                        "ItemCostPrice",
                        cost.toPlainString(),
                        new StringI18NModel("SUPPLIER")
                );
            }

            final String priceRef = AttributeNamesKeys.Cart.ORDER_INFO_ORDER_LINE_PRICE_REF_ID + item.getSupplierCode() + "_" + item.getProductSkuCode();
            final String priceRefVal = shoppingCart.getOrderInfo().getDetailByKey(priceRef);
            if (StringUtils.isNotBlank(priceRefVal)) {
                customerOrderDet.putValue(
                        "ItemPriceRef",
                        priceRefVal,
                        new StringI18NModel("SUPPLIER")
                );
            }

        }
    }

    private Address createCopy(final Address address) {
        if (address != null) {
            final Address copy = entityFactory.getByIface(Address.class);

            copy.setAddressType(address.getAddressType());

            copy.setAddrline1(address.getAddrline1());
            copy.setAddrline2(address.getAddrline2());
            copy.setCity(address.getCity());
            copy.setStateCode(address.getStateCode());
            copy.setPostcode(address.getPostcode());
            copy.setCountryCode(address.getCountryCode());

            copy.setSalutation(address.getSalutation());
            copy.setFirstname(address.getFirstname());
            copy.setLastname(address.getLastname());
            copy.setMiddlename(address.getMiddlename());
            copy.setPhone1(address.getPhone1());
            copy.setPhone2(address.getPhone2());
            copy.setMobile1(address.getMobile1());
            copy.setMobile2(address.getMobile2());
            copy.setEmail1(address.getEmail1());
            copy.setEmail2(address.getEmail2());
            copy.setCompanyName1(address.getCompanyName1());
            copy.setCompanyName2(address.getCompanyName2());
            copy.setCompanyDepartment(address.getCompanyDepartment());
            copy.setCustom0(address.getCustom0());
            copy.setCustom1(address.getCustom1());
            copy.setCustom2(address.getCustom2());
            copy.setCustom3(address.getCustom3());
            copy.setCustom4(address.getCustom4());
            copy.setCustom5(address.getCustom5());
            copy.setCustom6(address.getCustom6());
            copy.setCustom7(address.getCustom7());
            copy.setCustom8(address.getCustom8());
            copy.setCustom9(address.getCustom9());

            return copy;
        }
        return null;
    }

    /**
     * Format given address to string.
     *
     * @param address given address
     * @param shop shop for which to format it
     * @param lang language
     *
     * @return formatted address
     */
    private String formatAddress(final Address address, final Shop shop, final Customer customer, final String lang) {

        final String customerType = customer != null ? customer.getCustomerType() : null;
        final String addressType = address.getAddressType();
        final String format = shop.getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType(address.getCountryCode(), lang, customerType, addressType);
        return addressFormatter.formatAddress(address, format);

    }


    private I18NModel convertToValueI18nModel(final I18NModel nameModel, final I18NModel value, final String defaultPrefix) {
        final I18NModel display = new StringI18NModel();
        display.putValue(I18NModel.DEFAULT, defaultPrefix + ": " + value.getValue(I18NModel.DEFAULT));
        if (nameModel != null) {
            for (final Map.Entry<String, String> name : nameModel.getAllValues().entrySet()) {
                display.putValue(name.getKey(), name.getValue() + ": " + value.getValue(name.getKey()));
            }
        }
        return display;
    }

    private I18NModel newDefaultModel(final String value) {
        return new StringI18NModel(Collections.singletonMap(I18NModel.DEFAULT, value));
    }

}

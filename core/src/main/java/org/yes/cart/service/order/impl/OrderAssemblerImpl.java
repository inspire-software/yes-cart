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
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.order.*;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.MoneyUtils;

import java.lang.System;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class OrderAssemblerImpl implements OrderAssembler {

    private final OrderNumberGenerator orderNumberGenerator;
    private final EntityFactory entityFactory;
    private final ShopService shopService;
    private final ProductService productService;
    private final ProductSkuService productSkuService;
    private final PriceService priceService;
    private final OrderAddressFormatter addressFormatter;
    private final PromotionCouponService promotionCouponService;
    private final AddressService addressService;
    private final CustomerService customerService;

    /**
     * Create order assembler.
     *
     * @param orderNumberGenerator   order number generator
     * @param genericDAO             generic DAO
     * @param customerService        customer service
     * @param shopService            shop service
     * @param productService         product service
     * @param productSkuService      product sku service
     * @param priceService           price service
     * @param addressFormatter       format string to create address in one string from {@link Address} entity.
     * @param promotionCouponService coupon service
     * @param addressService         address service
     */
    public OrderAssemblerImpl(final OrderNumberGenerator orderNumberGenerator,
                              final GenericDAO genericDAO,
                              final CustomerService customerService,
                              final ShopService shopService,
                              final ProductService productService,
                              final ProductSkuService productSkuService,
                              final PriceService priceService,
                              final OrderAddressFormatter addressFormatter,
                              final PromotionCouponService promotionCouponService,
                              final AddressService addressService) {
        this.productService = productService;
        this.priceService = priceService;
        this.promotionCouponService = promotionCouponService;
        this.entityFactory = genericDAO.getEntityFactory();
        this.orderNumberGenerator = orderNumberGenerator;
        this.customerService = customerService;
        this.addressService = addressService;
        this.shopService = shopService;
        this.productSkuService = productSkuService;
        this.addressFormatter = addressFormatter;
    }

    /**
     * Create and fill {@link CustomerOrder} from   from {@link ShoppingCart}.
     *
     * @param shoppingCart given shopping cart
     * @return not persisted but filled with data order
     */
    public CustomerOrder assembleCustomerOrder(final ShoppingCart shoppingCart) throws OrderAssemblyException {
        return assembleCustomerOrder(shoppingCart, false);
    }

    /**
     * Create and fill {@link CustomerOrder} from   from {@link ShoppingCart}.
     *
     * @param shoppingCart given shopping cart
     * @param temp         if set to true then order number is not generated and coupon usage is not created
     *
     * @return not persisted but filled with data order
     */
    public CustomerOrder assembleCustomerOrder(final ShoppingCart shoppingCart, final boolean temp) throws OrderAssemblyException {

        final CustomerOrder customerOrder = entityFactory.getByIface(CustomerOrder.class);

        // sets shop from cache
        customerOrder.setShop(shopService.getById(shoppingCart.getShoppingContext().getCustomerShopId()));

        final Total cartTotal = shoppingCart.getTotal();

        if (cartTotal == null
                || cartTotal.getListSubTotal() == null
                || cartTotal.getSubTotal() == null
                || cartTotal.getSubTotalTax() == null
                || cartTotal.getSubTotalAmount() == null) {
            throw new OrderAssemblyException("No order total");
        }

        fillCustomerData(customerOrder, shoppingCart, temp);

        fillB2BData(customerOrder, shoppingCart, temp);

        fillOrderDetails(customerOrder, shoppingCart, temp);

        customerOrder.setLocale(shoppingCart.getCurrentLocale());
        customerOrder.setCurrency(shoppingCart.getCurrencyCode());
        customerOrder.setOrderStatus(CustomerOrder.ORDER_STATUS_NONE);
        customerOrder.setOrderTimestamp(new Date());
        customerOrder.setGuid(shoppingCart.getGuid());
        customerOrder.setCartGuid(shoppingCart.getGuid());
        customerOrder.setOrderIp(shoppingCart.getShoppingContext().getResolvedIp());

        customerOrder.setListPrice(cartTotal.getListSubTotal());
        customerOrder.setPrice(cartTotal.getSubTotal());
        customerOrder.setPromoApplied(cartTotal.isOrderPromoApplied());
        customerOrder.setAppliedPromo(cartTotal.getAppliedOrderPromo());
        customerOrder.setGrossPrice(cartTotal.getSubTotalAmount());
        customerOrder.setNetPrice(cartTotal.getSubTotalAmount().subtract(cartTotal.getSubTotalTax()));

        if (!temp) {
            customerOrder.setOrdernum(orderNumberGenerator.getNextOrderNumber());
            fillCoupons(customerOrder, shoppingCart);
        }

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
            try {
                customerOrder.setB2bApprovedDate(new SimpleDateFormat(Constants.DEFAULT_IMPORT_DATE_TIME_FORMAT).parse(approvedDate));
            } catch (Exception exp) {
                throw new OrderAssemblyException("Order b2bApprovedDate has invalid format: " + approvedDate + ", has to be " + Constants.DEFAULT_IMPORT_DATE_TIME_FORMAT);
            }
        }

    }

    /**
     * Add coupons information.
     *
     * @param customerOrder order to fill
     * @param shoppingCart  cart
     */
    private void fillCoupons(final CustomerOrder customerOrder, final ShoppingCart shoppingCart) throws OrderAssemblyException {

        if (!shoppingCart.getCoupons().isEmpty()) {

            final List<String> appliedCouponCodes = shoppingCart.getAppliedCoupons();

            if (!appliedCouponCodes.isEmpty()) {
                for (final String code : appliedCouponCodes) {

                    final PromotionCoupon coupon = promotionCouponService.findValidPromotionCoupon(code, shoppingCart.getCustomerEmail());
                    if (coupon == null) {
                        throw new CouponCodeInvalidException(code);
                    }

                    final PromotionCouponUsage usage = entityFactory.getByIface(PromotionCouponUsage.class);
                    usage.setCoupon(coupon);
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
    private void fillCustomerData(final CustomerOrder customerOrder, final ShoppingCart shoppingCart, final boolean temp) {

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

            if (!shippingNotRequired) {
                customerOrder.setShippingAddress(formatAddress(shippingAddress, configShop, customer, customerOrder.getLocale()));
            } else {
                customerOrder.setShippingAddress("");
            }

            if (sameAddress) {
                billingAddress = shippingAddress;
            }

            if (!billingNotRequired) {
                customerOrder.setBillingAddress(formatAddress(billingAddress, configShop, customer, customerOrder.getLocale()));
            } else {
                customerOrder.setBillingAddress("");
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

        final long requestedDate = NumberUtils.toLong(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID), 0);
        if (requestedDate > System.currentTimeMillis()) {
            customerOrder.setRequestedDeliveryDate(new Date(requestedDate));
        }

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

        for (CartItem item : shoppingCart.getCartItemList()) {

            if (item.getPrice() == null
                    || item.getSalePrice() == null
                    || item.getListPrice() == null
                    || item.getNetPrice() == null
                    || item.getGrossPrice() == null
                    || item.getTaxRate() == null
                    || item.getTaxCode() == null
                    || item.getProductSkuCode() == null) {
                throw new OrderAssemblyException("Order line has no prices and/or taxes: " + item);
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
            fillOrderDetail(customerOrder, shoppingCart, item, customerOrderDet, temp);

            customerOrderDet.setProductName(item.getProductName());
            customerOrderDet.setSupplierCode(item.getSupplierCode());

            fillOrderDetailCosts(customerOrder, shoppingCart, item, customerOrderDet, temp);

        }

    }

    /**
     * Fill specific product details for current item.
     *
     * @param customerOrder    order
     * @param shoppingCart     cart
     * @param item             item
     * @param customerOrderDet item to populate
     * @param temp             temporary
     *
     * @throws OrderAssemblyException
     */
    protected void fillOrderDetail(final CustomerOrder customerOrder, final ShoppingCart shoppingCart, final CartItem item, final CustomerOrderDet customerOrderDet, final boolean temp) throws OrderAssemblyException {

        if (!item.isGift()) {
            customerOrderDet.setB2bRemarks(shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_B2B_ORDER_LINE_REMARKS_ID + item.getProductSkuCode()));
        }

        final ProductSku sku = productSkuService.getProductSkuBySkuCode(item.getProductSkuCode());
        if (sku != null) {
            if (Product.AVAILABILITY_SHOWROOM == sku.getProduct().getAvailability()) {
                throw new OrderAssemblyException("Sku is for showroom only " + item.getProductSkuCode());
            }
            if (!temp) {
                // stored attributes are configured per customer group
                final List<String> storedAttributes = customerOrder.getShop().getProductStoredAttributesAsList();
                if (CollectionUtils.isNotEmpty(storedAttributes)) {
                    // fill product first
                    final Product product = productService.getProductById(sku.getProduct().getProductId(), true);
                    for (final AttrValue av : product.getAttributes()) {
                        if (storedAttributes.contains(av.getAttribute().getCode())) {
                            final String name =
                                    new FailoverStringI18NModel(av.getAttribute().getDisplayName(), av.getAttribute().getName()).getValue(customerOrder.getLocale());
                            final String displayValue =
                                    new FailoverStringI18NModel(av.getDisplayVal(), av.getVal()).getValue(customerOrder.getLocale());
                            customerOrderDet.putValue(av.getAttribute().getCode(), av.getVal(), name + ": " + displayValue);
                        }
                    }
                    // fill SKU specific (will override product ones)
                    for (final AttrValue av : sku.getAttributes()) {
                        if (storedAttributes.contains(av.getAttribute().getCode())) {
                            final String name =
                                    new FailoverStringI18NModel(av.getAttribute().getDisplayName(), av.getAttribute().getName()).getValue(customerOrder.getLocale());
                            final String displayValue =
                                    new FailoverStringI18NModel(av.getDisplayVal(), av.getVal()).getValue(customerOrder.getLocale());
                            customerOrderDet.putValue(av.getAttribute().getCode(), av.getVal(), name + ": " + displayValue);
                        }
                    }
                }
            }
        }
    }

    /**
     * Fill specific product details for current item.
     *
     * @param customerOrder    order
     * @param shoppingCart     cart
     * @param item             item
     * @param customerOrderDet item to populate
     * @param temp             temporary
     *
     * @throws OrderAssemblyException
     */
    private void fillOrderDetailCosts(final CustomerOrder customerOrder, final ShoppingCart shoppingCart, final CartItem item, final CustomerOrderDet customerOrderDet, final boolean temp) throws OrderAssemblyException {
        if (!temp) {

            final long customerShopId = shoppingCart.getShoppingContext().getCustomerShopId();
            final long masterShopId = shoppingCart.getShoppingContext().getShopId();
            // Fallback only if we have a B2B non-strict mode
            final Long fallbackShopId = masterShopId == customerShopId || shopService.getById(customerShopId).isB2BStrictPriceActive() ? null : masterShopId;
            final String currency = shoppingCart.getCurrencyCode();

            final String policyID = "COST" + (StringUtils.isBlank(item.getSupplierCode()) ? "" : "_" + item.getSupplierCode());

            final SkuPrice price = priceService.getMinimalPrice(null, item.getProductSkuCode(), customerShopId, fallbackShopId, currency, item.getQty(), false, policyID);
            if (price != null) {
                final BigDecimal sale = price.getSalePriceForCalculation();
                final BigDecimal list = price.getRegularPrice();
                final BigDecimal cost = sale != null && MoneyUtils.isFirstBiggerThanSecond(list, sale) ? sale : list;
                customerOrderDet.putValue("ItemCostPrice", cost.toPlainString(), "SUPPLIER");
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
}

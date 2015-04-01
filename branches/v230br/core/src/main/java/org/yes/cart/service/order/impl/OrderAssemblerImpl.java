/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.order.*;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;

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
    private final ProductSkuService productSkuService;
    private final OrderAddressFormatter addressFormatter;
    private final PromotionCouponService promotionCouponService;
    private final AddressService addressService;
    private final CustomerService customerService;

    /**
     * Create order assembler.
     *
     * @param orderNumberGenerator order number generator
     * @param genericDAO           generic DAO
     * @param customerService      customer service
     * @param shopService          shop service
     * @param productSkuService    product sku service
     * @param addressFormatter     format string to create address in one string from {@link org.yes.cart.domain.entity.Address} entity.
     * @param promotionCouponService coupon service
     * @param addressService       address service
     */
    public OrderAssemblerImpl(
            final OrderNumberGenerator orderNumberGenerator,
            final GenericDAO genericDAO,
            final CustomerService customerService,
            final ShopService shopService,
            final ProductSkuService productSkuService,
            final OrderAddressFormatter addressFormatter,
            final PromotionCouponService promotionCouponService,
            final AddressService addressService) {
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
        customerOrder.setShop(shopService.getById(shoppingCart.getShoppingContext().getShopId()));

        final Total cartTotal = shoppingCart.getTotal();

        if (cartTotal == null
                || cartTotal.getListSubTotal() == null
                || cartTotal.getSubTotal() == null
                || cartTotal.getSubTotalTax() == null
                || cartTotal.getSubTotalAmount() == null) {
            throw new OrderAssemblyException("No order total");
        }

        fillCustomerData(customerOrder, shoppingCart, temp);

        fillOrderDetails(customerOrder, shoppingCart, temp);

        customerOrder.setLocale(shoppingCart.getCurrentLocale());
        customerOrder.setCurrency(shoppingCart.getCurrencyCode());
        customerOrder.setOrderStatus(CustomerOrder.ORDER_STATUS_NONE);
        customerOrder.setOrderTimestamp(new Date());
        customerOrder.setGuid(shoppingCart.getGuid());
        customerOrder.setCartGuid(shoppingCart.getGuid());

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

                    customerOrder.getCoupons().add(usage);

                    promotionCouponService.updateUsage(coupon, 1);

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

        final Customer customer = customerService.getCustomerByEmail(shoppingCart.getCustomerEmail());

        if (customer != null) {
            long selectedBillingAddressId = shoppingCart.getOrderInfo().getBillingAddressId() != null ? shoppingCart.getOrderInfo().getBillingAddressId() : 0L;
            long selectedShippingAddressId = shoppingCart.getOrderInfo().getDeliveryAddressId()!= null ? shoppingCart.getOrderInfo().getDeliveryAddressId() : 0L;

            Address billingAddress = null;
            Address shippingAddress = null;

            for (final Address address : customer.getAddress()) {
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

            if (billingAddress == null) {
                billingAddress = customer.getDefaultAddress(Address.ADDR_TYPE_BILLING);
            }
            if (shippingAddress == null) {
                shippingAddress = customer.getDefaultAddress(Address.ADDR_TYPE_SHIPPING);
            }

            final boolean sameAddress = !shoppingCart.isSeparateBillingAddress() || billingAddress == null;

            customerOrder.setShippingAddress(formatAddress(shippingAddress, customerOrder.getShop()));

            if (sameAddress) {
                billingAddress = shippingAddress;
            }

            customerOrder.setBillingAddress(formatAddress(billingAddress, customerOrder.getShop()));

            if (!temp) {

                final Address orderShippingAddress = createCopy(shippingAddress);
                final Address orderBillingAddress;
                if (sameAddress) {
                    orderBillingAddress = orderShippingAddress;
                } else {
                    orderBillingAddress = createCopy(billingAddress);
                }

                customerOrder.setBillingAddressDetails(orderBillingAddress);
                customerOrder.setShippingAddressDetails(orderShippingAddress);

            }

            customerOrder.setCustomer(customer);
        }

        customerOrder.setOrderMessage(shoppingCart.getOrderMessage());

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
            customerOrderDet.setAppliedPromo(item.getAppliedPromo());
            customerOrderDet.setNetPrice(item.getNetPrice());
            customerOrderDet.setGrossPrice(item.getGrossPrice());
            customerOrderDet.setTaxCode(item.getTaxCode());
            customerOrderDet.setTaxRate(item.getTaxRate());
            customerOrderDet.setTaxExclusiveOfPrice(item.isTaxExclusiveOfPrice());

            customerOrderDet.setProductSkuCode(item.getProductSkuCode());

            // this is cached call so it should speed-up things
            final ProductSku sku = productSkuService.getProductSkuBySkuCode(item.getProductSkuCode());
            if (sku == null) {
                throw new OrderAssemblyException("No order line sku for " + item.getProductSkuCode());
            }
            if (Product.AVAILABILITY_SHOWROOM == sku.getProduct().getAvailability()) {
                throw new OrderAssemblyException("Sku is for showroom only " + item.getProductSkuCode());
            }
            customerOrderDet.setProductName(
                    new FailoverStringI18NModel(sku.getDisplayName(), sku.getName()).getValue(customerOrder.getLocale()));

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

            copy.setFirstname(address.getFirstname());
            copy.setLastname(address.getLastname());
            copy.setMiddlename(address.getMiddlename());
            copy.setPhoneList(address.getPhoneList());

            return copy;
        }
        return null;
    }

    /**
     * Format given address to string.
     *
     * @param address given address
     * @param shop shop for which to format it
     *
     * @return formatted address
     */
    private String formatAddress(final Address address, final Shop shop) {

        final AttrValue format = shop.getAttributeByCode(AttributeNamesKeys.Shop.ADDRESS_FORMATTER);
        if (format != null) {
            return addressFormatter.formatAddress(address, format.getVal());
        }
        return addressFormatter.formatAddress(address);

    }
}

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

import org.hibernate.criterion.Restrictions;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.service.domain.ShopService;
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
    private final GenericDAO<Customer, Long> customerDao;
    private final EntityFactory entityFactory;
    private final ShopService shopService;
    private final ProductSkuService productSkuService;
    private final OrderAddressFormatter addressFormatter;
    private final PromotionCouponService promotionCouponService;

    /**
     * Create order assembler.
     *
     * @param orderNumberGenerator order number generator
     * @param customerDao          customer dao to get customer from email
     * @param shopService          shop service
     * @param productSkuService    product sku service
     * @param addressFormatter        format string to create address in one string from {@link org.yes.cart.domain.entity.Address} entity.
     * @param promotionCouponService coupon service
     */
    public OrderAssemblerImpl(
            final OrderNumberGenerator orderNumberGenerator,
            final GenericDAO<Customer, Long> customerDao,
            final ShopService shopService,
            final ProductSkuService productSkuService,
            final OrderAddressFormatter addressFormatter,
            final PromotionCouponService promotionCouponService) {
        this.promotionCouponService = promotionCouponService;
        this.entityFactory = customerDao.getEntityFactory();
        this.orderNumberGenerator = orderNumberGenerator;
        this.customerDao = customerDao;
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

        final Total cartTotal = shoppingCart.getTotal();

        if (cartTotal == null
                || cartTotal.getListSubTotal() == null
                || cartTotal.getSubTotal() == null
                || cartTotal.getSubTotalTax() == null
                || cartTotal.getSubTotalAmount() == null) {
            throw new OrderAssemblyException("No order total");
        }

        fillCustomerData(customerOrder, shoppingCart);

        fillOrderDetails(customerOrder, shoppingCart);

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

        // sets shop from cache
        customerOrder.setShop(shopService.getById(shoppingCart.getShoppingContext().getShopId()));

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

                    final PromotionCouponUsage usage = customerDao.getEntityFactory().getByIface(PromotionCouponUsage.class);
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
     */
    private void fillCustomerData(final CustomerOrder customerOrder, final ShoppingCart shoppingCart) {

        final Customer customer = customerDao.findSingleByCriteria(
                Restrictions.eq("email", shoppingCart.getCustomerEmail()));

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
                shippingAddress = customer.getDefaultAddress(Address.ADDR_TYPE_SHIPING);
            }

            customerOrder.setShippingAddress(formatAddress(shippingAddress));

            if (!shoppingCart.isSeparateBillingAddress() || billingAddress == null) {
                billingAddress = shippingAddress;
            }

            customerOrder.setBillingAddress(formatAddress(billingAddress));

            customerOrder.setCustomer(customer);
        }

        customerOrder.setOrderMessage(shoppingCart.getOrderMessage());

    }

    /**
     * Fill details records in order.
     *
     * @param customerOrder order to fill
     * @param shoppingCart  cart
     */
    private void fillOrderDetails(final CustomerOrder customerOrder, final ShoppingCart shoppingCart) throws OrderAssemblyException {

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
            customerOrderDet.setProductName(
                    new FailoverStringI18NModel(sku.getDisplayName(), sku.getName()).getValue(customerOrder.getLocale()));

        }

    }

    /**
     * Format given address to string.
     *
     * @param defaultAddress given address
     * @return formatted address
     */
    private String formatAddress(final Address defaultAddress) {

        return addressFormatter.formatAddress(defaultAddress);

    }
}

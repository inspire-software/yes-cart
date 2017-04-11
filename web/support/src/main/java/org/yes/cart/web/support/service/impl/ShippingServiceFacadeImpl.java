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

package org.yes.cart.web.support.service.impl;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.ProductPriceModelImpl;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CarrierService;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.ShoppingCartShippingCostContainerImpl;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.web.support.service.ShippingServiceFacade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * User: denispavlov
 * Date: 11/01/2014
 * Time: 11:57
 */
public class ShippingServiceFacadeImpl implements ShippingServiceFacade {

    private final CarrierService carrierService;
    private final CarrierSlaService carrierSlaService;
    private final ShopService shopService;
    private final WarehouseService warehouseService;
    private final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy;

    public ShippingServiceFacadeImpl(final CarrierService carrierService,
                                     final CarrierSlaService carrierSlaService,
                                     final ShopService shopService,
                                     final WarehouseService warehouseService,
                                     final DeliveryCostCalculationStrategy deliveryCostCalculationStrategy) {
        this.carrierService = carrierService;
        this.carrierSlaService = carrierSlaService;
        this.shopService = shopService;
        this.warehouseService = warehouseService;
        this.deliveryCostCalculationStrategy = deliveryCostCalculationStrategy;
    }

    /** {@inheritDoc} */
    @Override
    public Pair<Boolean, Boolean> isAddressNotRequired(final Collection<Long> carrierSlaIds) {

        boolean billing = true;
        boolean delivery = true;

        for (final Long carrierSlaId : carrierSlaIds) {
            final CarrierSla sla = this.carrierSlaService.getById(carrierSlaId);
            if (sla != null) {
                if (!sla.isBillingAddressNotRequired()) {
                    billing = false;
                }
                if (!sla.isDeliveryAddressNotRequired()) {
                    delivery = false;
                }
            }
            if (!billing && !delivery) {
                break; // if both are required, no point in searching further
            }
        }

        return new Pair<Boolean, Boolean>(billing, delivery);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSkippableAddress(final ShoppingCart shoppingCart) {
        final List<String> suppliers = shoppingCart.getCartItemsSuppliers();
        if (suppliers.isEmpty()) {
            return false; // default is must select address
        }
        for (final String supplier : suppliers) {
            boolean atLeastOneNotRequiredAvailable = false;
            final List<Carrier> available = findCarriers(shoppingCart, supplier);
            for (final Carrier carrier : available) {
                for (final CarrierSla sla : carrier.getCarrierSla()) {
                    if (sla.isBillingAddressNotRequired() && sla.isDeliveryAddressNotRequired()) {
                        atLeastOneNotRequiredAvailable = true;
                        break;
                    }
                }
                if (atLeastOneNotRequiredAvailable) {
                    break;
                }
            }
            if (!atLeastOneNotRequiredAvailable) {
                return false;
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public List<Carrier> findCarriers(final ShoppingCart shoppingCart, final String supplier) {
        final List<Carrier> cached = carrierService.getCarriersByShopId(shoppingCart.getShoppingContext().getCustomerShopId());
        final List<Carrier> all = deepCopyCarriers(cached);
        filterCarriersForShoppingCart(all, shoppingCart, supplier);
        return all;
    }

    private List<Carrier> deepCopyCarriers(final List<Carrier> cached) {

        final List<Carrier> carriers = new ArrayList<Carrier>(cached.size());
        for (final Carrier cache : cached) {

            carriers.add((Carrier) SerializationUtils.clone(cache));

        }

        return carriers;
    }

    /*
        // CPOINT: shipping logic in most cases it is very business specific and should be put into this method
        // CPOINT: recommended approach is to create Carrier filter strategy bean and delegate filtering to it
     */
    private void filterCarriersForShoppingCart(final List<Carrier> all, final ShoppingCart shoppingCart, final String supplier) {

        final Iterator<Carrier> carrierIt = all.iterator();
        while (carrierIt.hasNext()) {
            final Carrier carrier = carrierIt.next();
            final Iterator<CarrierSla> slaIt = carrier.getCarrierSla().iterator();
            while (slaIt.hasNext()) {
                final CarrierSla carrierSla = slaIt.next();

                // Check if this SLA is available for given supplier (empty supplier could be for ALWAYS+Digital, so need to allow all SLA for those TODO: revise for YC-668)
                if (StringUtils.isNotBlank(supplier) && !carrierSla.getSupportedFulfilmentCentresAsList().contains(supplier)) {
                    slaIt.remove();
                    continue;
                }

                // Exclusions based on customer type
                final String customerType = shoppingCart.getOrderInfo().getDetailByKey(AttributeNamesKeys.Cart.ORDER_INFO_CUSTOMER_TYPE);
                if (StringUtils.isNotBlank(customerType) && carrierSla.getExcludeCustomerTypesAsList().contains(customerType)) {
                    slaIt.remove();
                    continue;
                }

                // We use the same logic to determine regional availability for for shipping. All CarrierSla must have
                // corresponding SkuPrice for given basket, if this is not the case then they are considered
                // unavailable for this country/region.
                // For R(Free) and (E)External just use price 1.00. The actual delivery cost are calculated by specific
                // DeliveryCostCalculationStrategy, so this is just availability in region marker.
                if (!isSlaAvailable(shoppingCart, supplier, carrierSla.getCarrierslaId())) {
                    slaIt.remove(); // No price defined, so must not be available for given cart state
                    continue;
                }
            }
            if (carrier.getCarrierSla().isEmpty()) {
                carrierIt.remove(); // if no SLA available then this carrier is not available
            }
        }

    }


    protected boolean isSlaAvailable(final ShoppingCart cart, final String supplier, final Long carrierSlaId) {

        final MutableShoppingCart changeShippingView = new ShoppingCartShippingCostContainerImpl(cart, supplier, carrierSlaId);

        final Total cost = deliveryCostCalculationStrategy.calculate(changeShippingView);

        return cost != null;

    }


    /** {@inheritDoc} */
    @Override
    public Pair<Carrier, CarrierSla> getCarrierSla(final ShoppingCart shoppingCart, final String supplier, final List<Carrier> carriersChoices) {

        final Long slaId = shoppingCart.getCarrierSlaId().get(supplier);

        if (slaId != null) {
            for (Carrier carrier : carriersChoices) {
                for (CarrierSla carrierSla : carrier.getCarrierSla()) {
                    if (slaId == carrierSla.getCarrierslaId()) {
                        return new Pair<Carrier, CarrierSla>(carrier, carrierSla);
                    }
                }
            }
        }

        return new Pair<Carrier, CarrierSla>(null, null);
    }

    static final String CART_SHIPPING_TOTAL_REF = "yc-cart-shipping-total";

    /**
     * {@inheritDoc}
     */
    public ProductPriceModel getCartShippingTotal(final ShoppingCart cart) {

        final String currency = cart.getCurrencyCode();

        final BigDecimal deliveriesCount = new BigDecimal(cart.getShippingList().size());
        final BigDecimal list = cart.getTotal().getDeliveryListCost();
        final BigDecimal sale = cart.getTotal().getDeliveryCost();

        final boolean showTax = cart.getShoppingContext().isTaxInfoEnabled();
        final boolean showTaxNet = showTax && cart.getShoppingContext().isTaxInfoUseNet();
        final boolean showTaxAmount = showTax && cart.getShoppingContext().isTaxInfoShowAmount();

        if (showTax) {

            final BigDecimal costInclTax = cart.getTotal().getDeliveryCostAmount();

            if (MoneyUtils.isFirstBiggerThanSecond(costInclTax, Total.ZERO)) {

                final BigDecimal totalTax = cart.getTotal().getDeliveryTax();
                final BigDecimal net = costInclTax.subtract(totalTax);
                final BigDecimal gross = costInclTax;

                final BigDecimal totalAdjusted = showTaxNet ? net : gross;

                final Set<String> taxes = new TreeSet<String>(); // sorts and de-dup's
                final Set<BigDecimal> rates = new TreeSet<BigDecimal>();
                for (final CartItem item : cart.getShippingList()) {
                    if (StringUtils.isNotBlank(item.getTaxCode())) {
                        taxes.add(item.getTaxCode());
                    }
                    rates.add(item.getTaxRate());
                }

                final BigDecimal taxRate;
                if (MoneyUtils.isFirstBiggerThanSecond(totalTax, Total.ZERO) && rates.size() > 1) {
                    // mixed rates in cart we use average with round up so that tax is not reduced by rounding
                    taxRate = totalTax.multiply(MoneyUtils.HUNDRED).divide(net, Constants.DEFAULT_SCALE, BigDecimal.ROUND_UP);
                } else {
                    // single rate for all items, use it to prevent rounding errors
                    taxRate = rates.iterator().next();
                }

                final String tax = StringUtils.join(taxes, ',');
                final boolean exclusiveTax = costInclTax.compareTo(sale) > 0;

                if (MoneyUtils.isFirstBiggerThanSecond(list, sale)) {
                    // if we have discounts

                    final MoneyUtils.Money listMoney = MoneyUtils.getMoney(list, taxRate, !exclusiveTax);
                    final BigDecimal listAdjusted = showTaxNet ? listMoney.getNet() : listMoney.getGross();

                    return new ProductPriceModelImpl(
                            CART_SHIPPING_TOTAL_REF,
                            currency,
                            deliveriesCount,
                            listAdjusted, totalAdjusted,
                            showTax, showTaxNet, showTaxAmount,
                            tax,
                            taxRate,
                            exclusiveTax,
                            totalTax
                    );

                }
                // no discounts
                return new ProductPriceModelImpl(
                        CART_SHIPPING_TOTAL_REF,
                        currency,
                        deliveriesCount,
                        totalAdjusted, null,
                        showTax, showTaxNet, showTaxAmount,
                        tax,
                        taxRate,
                        exclusiveTax,
                        totalTax
                );

            }

        }

        // standard "as is" prices

        if (MoneyUtils.isFirstBiggerThanSecond(list, sale)) {
            // if we have discounts
            return new ProductPriceModelImpl(
                    CART_SHIPPING_TOTAL_REF,
                    currency,
                    deliveriesCount,
                    list, sale
            );

        }
        // no discounts
        return new ProductPriceModelImpl(
                CART_SHIPPING_TOTAL_REF,
                currency,
                deliveriesCount,
                sale, null
        );


    }


    /**
     * {@inheritDoc}
     */
    public ProductPriceModel getCartShippingSupplierTotal(final ShoppingCart cart, final String supplier) {

        final String currency = cart.getCurrencyCode();

        BigDecimal deliveriesCount = BigDecimal.ZERO; new BigDecimal(cart.getShippingList().size());
        BigDecimal list = BigDecimal.ZERO;
        BigDecimal sale = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        final Set<String> taxes = new TreeSet<String>(); // sorts and de-dup's
        final Set<BigDecimal> rates = new TreeSet<BigDecimal>();

        for (final CartItem shipping : cart.getShippingList()) {
            if (supplier.equals(shipping.getSupplierCode())) {
                deliveriesCount = deliveriesCount.add(BigDecimal.ONE);
                list = list.add(shipping.getListPrice().multiply(shipping.getQty()).setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP));
                sale = sale.add(shipping.getPrice().multiply(shipping.getQty()).setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP));
                taxAmount = taxAmount.add(shipping.getGrossPrice().subtract(shipping.getNetPrice()).multiply(shipping.getQty()).setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP));
                if (StringUtils.isNotBlank(shipping.getTaxCode())) {
                    taxes.add(shipping.getTaxCode());
                }
                rates.add(shipping.getTaxRate());
            }
        }


        final boolean showTax = cart.getShoppingContext().isTaxInfoEnabled();
        final boolean showTaxNet = showTax && cart.getShoppingContext().isTaxInfoUseNet();
        final boolean showTaxAmount = showTax && cart.getShoppingContext().isTaxInfoShowAmount();

        if (showTax) {

            final BigDecimal costInclTax = sale;

            if (MoneyUtils.isFirstBiggerThanSecond(costInclTax, Total.ZERO)) {

                final BigDecimal totalTax = taxAmount;
                final BigDecimal net = costInclTax.subtract(totalTax);
                final BigDecimal gross = costInclTax;

                final BigDecimal totalAdjusted = showTaxNet ? net : gross;

                final BigDecimal taxRate;
                if (MoneyUtils.isFirstBiggerThanSecond(totalTax, Total.ZERO) && rates.size() > 1) {
                    // mixed rates in cart we use average with round up so that tax is not reduced by rounding
                    taxRate = totalTax.multiply(MoneyUtils.HUNDRED).divide(net, Constants.DEFAULT_SCALE, BigDecimal.ROUND_UP);
                } else {
                    // single rate for all items, use it to prevent rounding errors
                    taxRate = rates.iterator().next();
                }

                final String tax = StringUtils.join(taxes, ',');
                final boolean exclusiveTax = costInclTax.compareTo(sale) > 0;

                if (MoneyUtils.isFirstBiggerThanSecond(list, sale)) {
                    // if we have discounts

                    final MoneyUtils.Money listMoney = MoneyUtils.getMoney(list, taxRate, !exclusiveTax);
                    final BigDecimal listAdjusted = showTaxNet ? listMoney.getNet() : listMoney.getGross();

                    return new ProductPriceModelImpl(
                            CART_SHIPPING_TOTAL_REF,
                            currency,
                            deliveriesCount,
                            listAdjusted, totalAdjusted,
                            showTax, showTaxNet, showTaxAmount,
                            tax,
                            taxRate,
                            exclusiveTax,
                            totalTax
                    );

                }
                // no discounts
                return new ProductPriceModelImpl(
                        CART_SHIPPING_TOTAL_REF,
                        currency,
                        deliveriesCount,
                        totalAdjusted, null,
                        showTax, showTaxNet, showTaxAmount,
                        tax,
                        taxRate,
                        exclusiveTax,
                        totalTax
                );

            }

        }

        // standard "as is" prices

        if (MoneyUtils.isFirstBiggerThanSecond(list, sale)) {
            // if we have discounts
            return new ProductPriceModelImpl(
                    CART_SHIPPING_TOTAL_REF,
                    currency,
                    deliveriesCount,
                    list, sale
            );

        }
        // no discounts
        return new ProductPriceModelImpl(
                CART_SHIPPING_TOTAL_REF,
                currency,
                deliveriesCount,
                sale, null
        );


    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getCartItemsSuppliers(final ShoppingCart cart) {

        final String lang = cart.getCurrentLocale();
        final List<Warehouse> suppliers = warehouseService.getByShopId(cart.getShoppingContext().getCustomerShopId(), false);

        final Map<String, String> namesByCode = new HashMap<String, String>();
        for (final Warehouse supplier : suppliers) {
            namesByCode.put(supplier.getCode(), new FailoverStringI18NModel(
                    supplier.getDisplayName(),
                    supplier.getName()
            ).getValue(lang));
        }

        // Default supplier is the shop
        final Shop shop = shopService.getById(cart.getShoppingContext().getShopId());
        namesByCode.put("", shop.getName());

        return namesByCode;
    }
}

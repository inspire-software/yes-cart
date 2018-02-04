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

package org.yes.cart.web.page.component.cart;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.entity.decorator.ProductSkuDecorator;
import org.yes.cart.web.support.service.CategoryServiceFacade;
import org.yes.cart.web.support.service.CheckoutServiceFacade;
import org.yes.cart.web.support.service.ProductServiceFacade;
import org.yes.cart.web.util.WicketUtil;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.*;

/**
 * Class responsible to show cart with delivery amount and taxes for verification before payments.
 * Different  countries may require different models of representation.
 * CPOINT
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/3/11
 * Time: 8:42 PM
 */
public class ShoppingCartPaymentVerificationView extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String DELIVERY_LIST = "deliveryList";

    private static final String DELIVERY_CODE = "deliveryCode";
    private static final String DELIVERY_TIME = "deliveryTime";
    private static final String DELIVERY_REMARKS = "deliveryRemarks";
    private static final String DELIVERY_TIME_ITEM = "itemDeliveryTime";
    private static final String DELIVERY_REMARKS_ITEM = "itemDeliveryRemarks";

    private static final String DELIVERY_SUB_TOTAL = "deliverySubTotal";
    private static final String DELIVERY_SUB_TOTAL_TAX = "deliverySubTotalTax";
    private static final String DELIVERY_SUB_TOTAL_AMOUNT = "deliverySubTotalAmount";
    private static final String DELIVERY_METHOD = "deliveryMethod";
    private static final String DELIVERY_ADDRESS = "deliveryAddress";

    private static final String BILLING_ADDRESS = "billingAddress";

    public static final String DELIVERY_COST = "deliveryCost";
    public static final String DELIVERY_COST_GRATIS = "deliveryCostGratis";
    private static final String DELIVERY_COST_TAX = "deliveryCostTax";
    private static final String DELIVERY_COST_AMOUNT = "deliveryCostAmount";

    private static final String DELIVERY_GRAND_TOTAL = "grandTotal";
    private static final String DELIVERY_GRAND_AMOUNT = "grandTotalAmount";

    private static final String ITEM_LIST = "itemList";

    private static final String ITEM_NAME = "itemName";
    private static final String ITEM_NAME_LINK = "itemNameLink";
    private static final String ITEM_NAME_LINK_NAME = "itemNameLinkName";
    private static final String ITEM_CODE = "itemCode";
    private static final String ITEM_PRICE = "itemPrice";
    private static final String ITEM_QTY = "itemQty";
    private static final String ITEM_TOTAL = "itemTotal";
    private static final String DEFAULT_IMAGE = "defaultImage";
    // ------------------------------------- MARKUP IDs END ------------------------------------ //

    @SpringBean(name = StorefrontServiceSpringKeys.CHECKOUT_SERVICE_FACADE)
    private CheckoutServiceFacade checkoutServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CATEGORY_SERVICE_FACADE)
    private CategoryServiceFacade categoryServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_SERVICE_FACADE)
    private ProductServiceFacade productServiceFacade;

    /**
     * Construct payment form verification view, that
     * shows deliveries, items in deliveries and prices.
     *
     * @param id         component id
     * @param orderGuid  order guid
     */
    public ShoppingCartPaymentVerificationView(final String id,
                                               final String orderGuid,
                                               final boolean enableProductLinks) {
        super(id);

        final ShoppingCart cart = getCurrentCart();
        final CustomerOrder customerOrder = checkoutServiceFacade.findByReference(orderGuid);
        final ProductPriceModel subTotalPrice = checkoutServiceFacade.getOrderTotalSub(customerOrder, cart);
        final ProductPriceModel grandTotalPrice = checkoutServiceFacade.getOrderTotalAmount(customerOrder, cart);

        final String selectedLocale = getLocale().getLanguage();
        final Set<String> allPromos = checkoutServiceFacade.getOrderPromoCodes(customerOrder);
        for (final CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
            allPromos.addAll(checkoutServiceFacade.getOrderShippingPromoCodes(delivery));
        }
        for (final CustomerOrderDet orderDet : customerOrder.getOrderDetail()) {
            allPromos.addAll(checkoutServiceFacade.getOrderItemPromoCodes(orderDet));
        }

        final String deliveryAddress = customerOrder.getShippingAddress();
        final String billingAddress = customerOrder.getBillingAddress();

        final long configShopId = cart.getShoppingContext().getShopId();

        final Pair<String, String> imageSize = categoryServiceFacade.getThumbnailSizeConfig(0L, configShopId);

        add(
                new ListView<CustomerOrderDelivery>(DELIVERY_LIST, new ArrayList<CustomerOrderDelivery>(customerOrder.getDelivery()))
                {

                    @Override
                    protected void populateItem(ListItem<CustomerOrderDelivery> customerOrderDeliveryListItem)
                    {

                        final CustomerOrderDelivery delivery = customerOrderDeliveryListItem.getModelObject();
                        final CarrierSla sla = delivery.getCarrierSla();

                        final String slaName = getI18NSupport().getFailoverModel(sla.getDisplayName(), sla.getName()).getValue(selectedLocale);

                        final List<CustomerOrderDeliveryDet> deliveryDet = new ArrayList<CustomerOrderDeliveryDet>(delivery.getDetail());

                        final ProductPriceModel deliveryTotal = checkoutServiceFacade.getOrderDeliveryTotalSub(customerOrder, delivery, getCurrentCart());
                        final ProductPriceModel deliveryShipping = checkoutServiceFacade.getOrderDeliveryTotalShipping(customerOrder, delivery, getCurrentCart());
                        final boolean freeShipping = MoneyUtils.isFirstEqualToSecond(deliveryShipping.getRegularPrice(), BigDecimal.ZERO);

                        final IModel<String> deliveryTime;
                        final boolean showDeliveryTime;
                        if (delivery.getDeliveryConfirmed() != null) {
                            final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
                            deliveryTime = WicketUtil.createStringResourceModel(this, "deliveryConfirmed",
                                    Collections.<String, Object>singletonMap("date", df.format(delivery.getDeliveryConfirmed())));
                            showDeliveryTime = true;
                        } else if (delivery.getDeliveryGuaranteed() != null) {
                            final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
                            deliveryTime = WicketUtil.createStringResourceModel(this, "deliveryGuaranteed",
                                    Collections.<String, Object>singletonMap("date", df.format(delivery.getDeliveryGuaranteed())));
                            showDeliveryTime = true;
                        } else if (delivery.getDeliveryEstimatedMin() != null) {
                            if (delivery.getDeliveryEstimatedMax() != null) {
                                final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
                                deliveryTime = WicketUtil.createStringResourceModel(this, "deliveryEstimatedXY",
                                        new HashMap<String, Object>(4) {{
                                            put("from", df.format(delivery.getDeliveryEstimatedMin()));
                                            put("to", df.format(delivery.getDeliveryEstimatedMax()));
                                        }});
                                showDeliveryTime = true;
                            } else {
                                final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
                                deliveryTime = WicketUtil.createStringResourceModel(this, "deliveryEstimatedX",
                                        Collections.<String, Object>singletonMap("from", df.format(delivery.getDeliveryEstimatedMin())));
                                showDeliveryTime = true;
                            }
                        } else if (delivery.getRequestedDeliveryDate() != null) {
                            final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
                            deliveryTime = WicketUtil.createStringResourceModel(this, "deliveryRequested",
                                    Collections.<String, Object>singletonMap("date", df.format(delivery.getRequestedDeliveryDate())));
                            showDeliveryTime = true;
                        } else {
                            deliveryTime = null;
                            showDeliveryTime = false;
                        }

                        customerOrderDeliveryListItem
                                .add(
                                        new Label(DELIVERY_CODE, delivery.getDeliveryNum())
                                )
                                .add(
                                        new Label(DELIVERY_TIME, deliveryTime).setVisible(showDeliveryTime)
                                )
                                .add(
                                        new Label(DELIVERY_REMARKS, delivery.getDeliveryRemarks()).setVisible(StringUtils.isNotBlank(delivery.getDeliveryRemarks()))
                                )
                                .add(

                                        new ListView<CustomerOrderDeliveryDet>(ITEM_LIST, deliveryDet) {

                                            @Override
                                            protected void populateItem(ListItem<CustomerOrderDeliveryDet> customerOrderDeliveryDetListItem) {

                                                final CustomerOrderDeliveryDet det = customerOrderDeliveryDetListItem.getModelObject();

                                                final ProductSku productSku = productServiceFacade.getProductSkuBySkuCode(det.getProductSkuCode());


                                                final ProductSkuDecorator productSkuDecorator = getDecoratorFacade().decorate(
                                                        productSku != null ? productSku : det,
                                                        getWicketUtil().getHttpServletRequest().getContextPath(),
                                                        true);

                                                final String width = imageSize.getFirst();
                                                final String height = imageSize.getSecond();

                                                final String lang = getLocale().getLanguage();
                                                final String defaultImageRelativePath = productSkuDecorator.getDefaultImage(width, height, lang);

                                                final LinksSupport links = getWicketSupportFacade().links();

                                                final IModel<String> deliveryTime;
                                                final boolean showDeliveryTime;
                                                if (det.getDeliveryConfirmed() != null) {
                                                    final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
                                                    deliveryTime = WicketUtil.createStringResourceModel(this, "deliveryConfirmed",
                                                            Collections.<String, Object>singletonMap("date", df.format(det.getDeliveryConfirmed())));
                                                    showDeliveryTime = true;
                                                } else if (det.getDeliveryGuaranteed() != null) {
                                                    final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
                                                    deliveryTime = WicketUtil.createStringResourceModel(this, "deliveryGuaranteed",
                                                            Collections.<String, Object>singletonMap("date", df.format(det.getDeliveryGuaranteed())));
                                                    showDeliveryTime = true;
                                                } else if (det.getDeliveryEstimatedMin() != null) {
                                                    if (det.getDeliveryEstimatedMax() != null) {
                                                        final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
                                                        deliveryTime = WicketUtil.createStringResourceModel(this, "deliveryEstimatedXY",
                                                                new HashMap<String, Object>(4) {{
                                                                    put("from", df.format(det.getDeliveryEstimatedMin()));
                                                                    put("to", df.format(det.getDeliveryEstimatedMax()));
                                                                }});
                                                        showDeliveryTime = true;
                                                    } else {
                                                        final DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
                                                        deliveryTime = WicketUtil.createStringResourceModel(this, "deliveryEstimatedX",
                                                                Collections.<String, Object>singletonMap("from", df.format(det.getDeliveryEstimatedMin())));
                                                        showDeliveryTime = true;
                                                    }
                                                } else {
                                                    deliveryTime = null;
                                                    showDeliveryTime = false;
                                                }

                                                final boolean linkToProduct = enableProductLinks && productSku != null;
                                                final long linkId = productSkuDecorator.getId();
                                                final String name = productSkuDecorator.getName(selectedLocale);

                                                customerOrderDeliveryDetListItem
                                                        .add(
                                                                links.newProductSkuLink(ITEM_NAME_LINK, linkId)
                                                                        .add(new Label(ITEM_NAME_LINK_NAME, name))
                                                                        .setVisible(linkToProduct)
                                                        ).add(
                                                                new Label(ITEM_NAME, name).setVisible(!linkToProduct)
                                                        )
                                                        .add(
                                                                new Label(ITEM_CODE, det.getProductSkuCode())
                                                        )
                                                        .add(
                                                                new Label(DELIVERY_TIME_ITEM, deliveryTime).setVisible(showDeliveryTime)
                                                        )
                                                        .add(
                                                                new Label(DELIVERY_REMARKS_ITEM, det.getDeliveryRemarks()).setVisible(StringUtils.isNotBlank(det.getDeliveryRemarks()))
                                                        )
                                                        .add(
                                                                getPriceView(det)
                                                        )
                                                        .add(
                                                                new Label(ITEM_QTY, det.getQty().stripTrailingZeros().toPlainString())
                                                        )
                                                        .add(
                                                                getTotalView(det)
                                                        )
                                                        .add(
                                                                new ContextImage(DEFAULT_IMAGE, defaultImageRelativePath)
                                                                        .add(
                                                                                new AttributeModifier(BaseComponent.HTML_WIDTH, width),
                                                                                new AttributeModifier(BaseComponent.HTML_HEIGHT, height)
                                                                        )
                                                        );

                                            }
                                        }

                                )
                                .add(
                                        new PriceView(
                                                DELIVERY_SUB_TOTAL,
                                                deliveryTotal,
                                                null,
                                                false, false,
                                                deliveryTotal.isTaxInfoEnabled(), deliveryTotal.isTaxInfoShowAmount())
                                )
                                .add(
                                        new WebMarkupContainer(DELIVERY_COST_GRATIS).setVisible(freeShipping)
                                )
                                .add(
                                        new PriceView(
                                                DELIVERY_COST,
                                                deliveryShipping,
                                                delivery.getAppliedPromo(),
                                                false, true,
                                                deliveryShipping.isTaxInfoEnabled(), deliveryShipping.isTaxInfoShowAmount(), true)
                                )
                                .add(
                                        new Label(DELIVERY_METHOD, slaName)
                                )
                                .add(
                                        new Label(DELIVERY_ADDRESS, makeHtml(deliveryAddress))
                                                .setEscapeModelStrings(false)
                                                .setVisible(!billingAddress.equals(deliveryAddress))
                                );
                    }
                }
        );

        add(
                new PriceView(
                        DELIVERY_GRAND_TOTAL,
                        subTotalPrice,
                        null,
                        false, false,
                        subTotalPrice.isTaxInfoEnabled(), subTotalPrice.isTaxInfoShowAmount())
        );
        add(
            new PriceView(
                        DELIVERY_GRAND_AMOUNT,
                        grandTotalPrice,
                        StringUtils.join(allPromos, ','),
                        true, true,
                        false, false)
        );
        add(new Label(BILLING_ADDRESS, makeHtml(billingAddress)).setEscapeModelStrings(false));

    }

    private String makeHtml(final String address) {
        return address.trim().replace("\r\n", "<br/>").replace("\r", "<br/>").replace("\n", "<br/>");
    }


    private PriceView getPriceView(final CartItem cartItem) {

        final ShoppingCart cart = ((AbstractWebPage) getPage()).getCurrentCart();

        final ProductPriceModel model = productServiceFacade.getSkuPrice(cart, cartItem, false);

        final PriceView priceView = new PriceView(ITEM_PRICE, model, cartItem.getAppliedPromo(), false, true, model.isTaxInfoEnabled(), model.isTaxInfoShowAmount(), cartItem.isGift());

        return priceView;
    }


    private PriceView getTotalView(final CartItem cartItem) {

        final ShoppingCart cart = ((AbstractWebPage) getPage()).getCurrentCart();

        final ProductPriceModel model = productServiceFacade.getSkuPrice(cart, cartItem, true);

        final PriceView priceView = new PriceView(ITEM_TOTAL, model, null, false, false, model.isTaxInfoEnabled(), model.isTaxInfoShowAmount());

        return priceView;

    }

}

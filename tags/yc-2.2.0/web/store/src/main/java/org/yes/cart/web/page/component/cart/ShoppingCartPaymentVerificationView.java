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

package org.yes.cart.web.page.component.cart;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.entity.decorator.ProductSkuDecorator;
import org.yes.cart.web.support.service.CategoryServiceFacade;
import org.yes.cart.web.support.service.CheckoutServiceFacade;
import org.yes.cart.web.support.service.ProductServiceFacade;
import org.yes.cart.web.util.WicketUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private static final String DELIVERY_SUB_TOTAL = "deliverySubTotal";
    private static final String DELIVERY_SUB_TOTAL_TAX = "deliverySubTotalTax";
    private static final String DELIVERY_SUB_TOTAL_AMOUNT = "deliverySubTotalAmount";
    private static final String DELIVERY_METHOD = "deliveryMethod";
    private static final String DELIVERY_ADDRESS = "deliveryAddress";

    private static final String BILLING_ADDRESS = "billingAddress";

    public static final String DELIVERY_COST = "deliveryCost";
    private static final String DELIVERY_COST_TAX = "deliveryCostTax";
    private static final String DELIVERY_COST_AMOUNT = "deliveryCostAmount";

    private static final String DELIVERY_GRAND_TOTAL = "grandTotal";
    private static final String DELIVERY_GRAND_TAX = "grandTotalTax";
    private static final String DELIVERY_GRAND_AMOUNT = "grandTotalAmount";

    private static final String ITEM_LIST = "itemList";

    private static final String ITEM_NAME = "itemName";
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
                                               final String orderGuid) {
        super(id);

        final CustomerOrder customerOrder = checkoutServiceFacade.findByGuid(orderGuid);
        final Total grandTotal = checkoutServiceFacade.getOrderTotal(customerOrder);

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

        final Pair<String, String> imageSize = categoryServiceFacade.getThumbnailSizeConfig(0L, ShopCodeContext.getShopId());

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

                        final Total total = checkoutServiceFacade.getOrderDeliveryTotal(customerOrder, delivery);

                        customerOrderDeliveryListItem
                                .add(
                                        new Label(DELIVERY_CODE, delivery.getDeliveryNum())
                                )
                                .add(

                                        new ListView<CustomerOrderDeliveryDet>(ITEM_LIST, deliveryDet) {

                                            @Override
                                            protected void populateItem(ListItem<CustomerOrderDeliveryDet> customerOrderDeliveryDetListItem) {

                                                final CustomerOrderDeliveryDet det = customerOrderDeliveryDetListItem.getModelObject();

                                                final ProductSkuDecorator productSkuDecorator = getDecoratorFacade().decorate(
                                                        productServiceFacade.getProductSkuBySkuCode(det.getProductSkuCode()),
                                                        WicketUtil.getHttpServletRequest().getContextPath(),
                                                        true);

                                                final String width = imageSize.getFirst();
                                                final String height = imageSize.getSecond();

                                                final String lang = getLocale().getLanguage();
                                                final String defaultImageRelativePath = productSkuDecorator.getDefaultImage(width, height, lang);

                                                final BigDecimal itemTotal = det.getPrice()
                                                        .multiply(det.getQty())
                                                        .setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);

                                                customerOrderDeliveryDetListItem
                                                        .add(
                                                                new Label(ITEM_NAME, productSkuDecorator.getName(selectedLocale))
                                                        )
                                                        .add(
                                                                new Label(ITEM_CODE, det.getProductSkuCode())
                                                        )
                                                        .add(
                                                                new Label(ITEM_PRICE, det.getPrice().toString())
                                                        )
                                                        .add(
                                                                new Label(ITEM_QTY, det.getQty().toString())
                                                        )
                                                        .add(
                                                                new Label(ITEM_TOTAL, itemTotal.toString())
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
                                        new Label(DELIVERY_SUB_TOTAL, total.getSubTotal().toString())
                                )
                                .add(
                                        new Label(DELIVERY_SUB_TOTAL_TAX, total.getSubTotalTax().toString())
                                )
                                .add(
                                        new Label(DELIVERY_SUB_TOTAL_AMOUNT, total.getSubTotalAmount().toString())
                                )
                                .add(
                                        new Label(DELIVERY_COST, total.getDeliveryCost().toString())
                                )
                                .add(
                                        new Label(DELIVERY_COST_TAX, total.getDeliveryTax().toString())
                                )
                                .add(
                                        new Label(DELIVERY_COST_AMOUNT, total.getDeliveryCostAmount().toString())
                                )
                                .add(
                                        new Label(DELIVERY_METHOD, slaName)
                                )
                                .add(
                                        new Label(DELIVERY_ADDRESS, deliveryAddress)
                                );
                    }
                }
        )
                .add(
                        new Label(DELIVERY_GRAND_TOTAL, grandTotal.getTotal().toString())
                )
                .add(
                        new Label(BILLING_ADDRESS, billingAddress)
                )
                .add(
                        new Label(DELIVERY_GRAND_TAX, grandTotal.getTotalTax().toString())
                )
                .add(
                        new PriceView(
                                DELIVERY_GRAND_AMOUNT,
                                new Pair<BigDecimal, BigDecimal>(grandTotal.getListTotalAmount(), grandTotal.getTotalAmount()),
                                customerOrder.getCurrency(),
                                StringUtils.join(allPromos, ", "), true, true)
                );

    }
}

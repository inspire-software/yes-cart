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
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.*;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.entity.decorator.ProductSkuDecorator;
import org.yes.cart.web.support.service.AttributableImageService;
import org.yes.cart.web.util.WicketUtil;

import java.math.BigDecimal;
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

    private static final String DELIVERY_SUB_TOTAL = "deliverySubTotal";
    private static final String DELIVERY_SUB_TOTAL_TAX = "deliverySubTotalTax";
    private static final String DELIVERY_SUB_TOTAL_AMOUNT = "deliverySubTotalAmount";

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

    @SpringBean(name = StorefrontServiceSpringKeys.AMOUNT_CALCULATION_STRATEGY)
    private AmountCalculationStrategy amountCalculationStrategy;

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_ORDER_SERVICE)
    private CustomerOrderService customerOrderService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SKU_SERVICE)
    private ProductSkuService productSkuService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;

    @SpringBean(name = StorefrontServiceSpringKeys.ATTRIBUTABLE_IMAGE_SERVICE)
    protected AttributableImageService attributableImageService;


    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    protected CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.IMAGE_SERVICE)
    protected ImageService imageService;


    private final Category rootCategory;

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

        rootCategory = categoryService.getRootCategory();

        final CustomerOrder customerOrder = customerOrderService.findByGuid(orderGuid);
        final Total grandTotal = amountCalculationStrategy.calculate(customerOrder);

        final String selectedLocale = getLocale().getLanguage();
        final Set<String> allPromos = new HashSet<String>();

        if (StringUtils.isNotBlank(customerOrder.getAppliedPromo())) {
            allPromos.addAll(Arrays.asList(StringUtils.split(customerOrder.getAppliedPromo(), ',')));
        }

        add(
                new ListView<CustomerOrderDelivery>(DELIVERY_LIST, new ArrayList<CustomerOrderDelivery>(customerOrder.getDelivery()))
                {

                    @Override
                    protected void populateItem(ListItem<CustomerOrderDelivery> customerOrderDeliveryListItem)
                    {

                        final CustomerOrderDelivery delivery = customerOrderDeliveryListItem.getModelObject();

                        final List<CustomerOrderDeliveryDet> deliveryDet = new ArrayList<CustomerOrderDeliveryDet>(delivery.getDetail());

                        final Total total = amountCalculationStrategy.calculate(customerOrder, delivery);

                        if (StringUtils.isNotBlank(delivery.getAppliedPromo())) {
                            allPromos.addAll(Arrays.asList(StringUtils.split(delivery.getAppliedPromo(), ',')));
                        }

                        customerOrderDeliveryListItem
                                .add(
                                        new Label(DELIVERY_CODE, delivery.getDeliveryNum())
                                )
                                .add(

                                        new ListView<CustomerOrderDeliveryDet>(ITEM_LIST, deliveryDet)
                                        {

                                            @Override
                                            protected void populateItem(ListItem<CustomerOrderDeliveryDet> customerOrderDeliveryDetListItem)
                                            {

                                                final CustomerOrderDeliveryDet det = customerOrderDeliveryDetListItem.getModelObject();

                                                final ProductSkuDecorator productSkuDecorator = getDecoratorFacade().decorate(
                                                        productSkuService.getProductSkuBySkuCode(det.getProductSkuCode()),
                                                        WicketUtil.getHttpServletRequest().getContextPath(),
                                                        getI18NSupport());

                                                final String[] size = productSkuDecorator.getThumbnailImageSize(rootCategory);

                                                final String width = size[0];
                                                final String height = size[1];

                                                final String defaultImageRelativePath = productSkuDecorator.getImage(
                                                        width,
                                                        height,
                                                        productSkuDecorator.getDefaultImageAttributeName());

                                                final BigDecimal itemTotal = det.getPrice()
                                                        .multiply(det.getQty())
                                                        .setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);

                                                if (StringUtils.isNotBlank(det.getAppliedPromo())) {
                                                    allPromos.addAll(Arrays.asList(StringUtils.split(det.getAppliedPromo(), ',')));
                                                }

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
                                );
                    }
                }
        )
                .add(
                        new Label(DELIVERY_GRAND_TOTAL, grandTotal.getTotal().toString())
                )
                .add(
                        new Label(DELIVERY_GRAND_TAX, grandTotal.getTotalTax().toString())
                )
                .add(
                        new PriceView(
                                DELIVERY_GRAND_AMOUNT,
                                new Pair<BigDecimal, BigDecimal>(grandTotal.getListTotalAmount(), grandTotal.getTotalAmount()),
                                customerOrder.getCurrency(),
                                StringUtils.join(allPromos, ','), true, true)
                );

    }
}

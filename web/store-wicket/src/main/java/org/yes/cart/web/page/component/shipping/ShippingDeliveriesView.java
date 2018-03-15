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

package org.yes.cart.web.page.component.shipping;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.PriceModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.entity.decorator.ProductSkuDecorator;
import org.yes.cart.web.support.service.CategoryServiceFacade;
import org.yes.cart.web.support.service.CheckoutServiceFacade;
import org.yes.cart.web.support.service.ProductServiceFacade;
import org.yes.cart.web.support.service.ShippingServiceFacade;

import java.io.Serializable;
import java.util.*;

/**
 * Class responsible to show cart with delivery amount and taxes for verification before selecting shipping method.
 * Different  countries may require different models of representation.
 * CPOINT
 */
public class ShippingDeliveriesView extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String DELIVERY_LIST = "deliveryList";

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

    @SpringBean(name = StorefrontServiceSpringKeys.SHIPPING_SERVICE_FACADE)
    private ShippingServiceFacade shippingServiceFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    private boolean multipleDelivery;

    /**
     * Construct payment form verification view, that
     * shows deliveries, items in deliveries and prices.
     *
     * @param id         component id
     */
    public ShippingDeliveriesView(final String id,
                                  final boolean enableProductLinks) {
        super(id);

        final ShoppingCart shoppingCart = getCurrentCart();
        final String selectedLocale = getLocale().getLanguage();

        final Map<DeliveryBucket, List<CartItem>> bucketItemsMap = shoppingCart.getCartItemMap();
        final List<Pair<String, List<CartItem>>> supplierItems = new ArrayList<>(bucketItemsMap.size());
        final Map<String, List<CartItem>> supplierItemsMap = new HashMap<>();
        for (final Map.Entry<DeliveryBucket, List<CartItem>> bucketItem : bucketItemsMap.entrySet()) {
            List<CartItem> items = supplierItemsMap.get(bucketItem.getKey().getSupplier());
            if (items == null) {
                items = new ArrayList<>();
                supplierItems.add(new Pair<>(bucketItem.getKey().getSupplier(), items));
                supplierItemsMap.put(bucketItem.getKey().getSupplier(), items);
            }
            items.addAll(bucketItem.getValue());
        }

        this.multipleDelivery = shoppingCart.getOrderInfo().isMultipleDelivery();

        final Pair<String, String> imageSize = categoryServiceFacade.getThumbnailSizeConfig(0L, getCurrentCustomerShopId());
        final Map<String, String> supplierNames = shippingServiceFacade.getCartItemsSuppliers(shoppingCart);

        add(
                new ListView<Pair<String, List<CartItem>>>(DELIVERY_LIST, supplierItems)
                {

                    @Override
                    protected void populateItem(ListItem<Pair<String, List<CartItem>>> customerOrderDeliveryListItem)
                    {

                        final Pair<String, List<CartItem>> delivery = customerOrderDeliveryListItem.getModelObject();

                        final List<CartItem> deliveryDet = new ArrayList<>(delivery.getSecond());
                        String supplierName = supplierNames.get(delivery.getFirst());
                        if (supplierName == null) {
                            supplierName = delivery.getFirst();
                        }


                        customerOrderDeliveryListItem
                                .add(

                                        new ListView<CartItem>(ITEM_LIST, deliveryDet) {

                                            @Override
                                            protected void populateItem(ListItem<CartItem> customerOrderDeliveryDetListItem) {

                                                final CartItem det = customerOrderDeliveryDetListItem.getModelObject();

                                                final ProductSkuDecorator productSkuDecorator = getDecoratorFacade().decorate(
                                                        productServiceFacade.getProductSkuBySkuCode(det.getProductSkuCode()),
                                                        getWicketUtil().getHttpServletRequest().getContextPath(),
                                                        true);

                                                final String width = imageSize.getFirst();
                                                final String height = imageSize.getSecond();

                                                final String lang = getLocale().getLanguage();
                                                final String defaultImageRelativePath = productSkuDecorator.getDefaultImage(width, height, lang);

                                                final LinksSupport links = getWicketSupportFacade().links();

                                                customerOrderDeliveryDetListItem
                                                        .add(
                                                                links.newProductSkuLink(ITEM_NAME_LINK, productSkuDecorator.getId())
                                                                        .add(new Label(ITEM_NAME_LINK_NAME, productSkuDecorator.getName(selectedLocale)))
                                                                        .setVisible(enableProductLinks)
                                                        ).add(
                                                        new Label(ITEM_NAME, productSkuDecorator.getName(selectedLocale)).setVisible(!enableProductLinks)
                                                )
                                                        .add(
                                                                new Label(ITEM_CODE, det.getProductSkuCode())
                                                        )
                                                        .add(
                                                                getPriceView(det)
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

                                                final Label qty = new Label(ITEM_QTY, det.getQty().stripTrailingZeros().toPlainString());
                                                if (CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP.equals(det.getDeliveryGroup()) ||
                                                        CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP.equals(det.getDeliveryGroup())) {
                                                    qty.add(new AttributeModifier("class", "label label-warning"));
                                                } else if (CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP.equals(det.getDeliveryGroup()) ||
                                                        CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP.equals(det.getDeliveryGroup())) {
                                                    qty.add(new AttributeModifier("class", "label label-danger"));
                                                }
                                                customerOrderDeliveryDetListItem.add(qty);
                                            }
                                        }

                                )
                                .add(new Label("deliveryBucketCarrier", new StringResourceModel("deliveryBucketCarrier", this, new Model<Serializable>(new ValueMap(
                                        Collections.singletonMap("supplier", supplierName)
                                )))))
                                .add(
                                        new ShippingView("shippingView", delivery.getFirst())
                                );
                    }
                }
        );

        final Component multiDelivery = new CheckBox("multipleDelivery", new PropertyModel(this, "multipleDelivery")) {

            /** {@inheritDoc} */
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            public void onSelectionChanged() {
                setModelObject(!getModelObject());
                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_MULTIPLEDELIVERY,
                        getCurrentCart(),
                        (Map) Collections.singletonMap(ShoppingCartCommand.CMD_MULTIPLEDELIVERY, getModelObject().toString()));
                super.onSelectionChanged();
                ((AbstractWebPage) getPage()).persistCartIfNecessary();
            }

        };

        add(new Form("paymentOptionsForm") {
                    @Override
                    public boolean isVisible() {
                        return isMultipleDeliveryAvailableForAtLeastOneSupplier();
                    }
                }
                .add(multiDelivery)
                .add(
                        new Label("multipleDeliveryLabel",
                                new StringResourceModel("multipleDeliveryLabel", this)

                        )
                )
        );
    }


    private PriceView getPriceView(final CartItem cartItem) {

        final ShoppingCart cart = ((AbstractWebPage) getPage()).getCurrentCart();

        final PriceModel model = productServiceFacade.getSkuPrice(cart, cartItem, false);

        return new PriceView(ITEM_PRICE, model, cartItem.getAppliedPromo(), false, true, model.isTaxInfoEnabled(), model.isTaxInfoShowAmount(), cartItem.isGift());
    }


    private PriceView getTotalView(final CartItem cartItem) {

        final ShoppingCart cart = ((AbstractWebPage) getPage()).getCurrentCart();

        final PriceModel model = productServiceFacade.getSkuPrice(cart, cartItem, true);

        return new PriceView(ITEM_TOTAL, model, null, false, false, model.isTaxInfoEnabled(), model.isTaxInfoShowAmount());

    }


    @Override
    protected void onBeforeRender() {

        final ShoppingCart shoppingCart = getCurrentCart();
        if (shoppingCart.getCartItemsSuppliers().size() > 1) {
            info(getLocalizer().getString("carrierSelectMultiSupplier", this));
        }

        if (!shoppingCart.getOrderInfo().isMultipleDelivery() && isMultipleDeliveryAvailableForAtLeastOneSupplier()) {
            info(getLocalizer().getString("carrierSelectMultiDelivery", this));
        }

        super.onBeforeRender();
    }

    public boolean isMultipleDelivery() {
        return this.multipleDelivery;
    }

    public void setMultipleDelivery(final boolean multipleDelivery) {
        this.multipleDelivery = multipleDelivery;
    }

    private boolean isMultipleDeliveryAvailableForAtLeastOneSupplier() {
        for (final Boolean available : getCurrentCart().getOrderInfo().getMultipleDeliveryAvailable().values()) {
            if (available) {
                return true;
            }
        }
        return false;
    }
}

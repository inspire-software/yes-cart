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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;
import org.yes.cart.web.support.service.ShippingServiceFacade;

import java.io.Serializable;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 12:39 PM
 */
public class ShippingView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String SHIPPING_FORM = "shippingForm";
    private final static String CARRIER_LIST = "carrier";
    private final static String CARRIER_SLA_LIST = "carrierSla";
    private final static String PRICE_LABEL = "deliveryCost";
    private final static String PRICE_VIEW = "priceView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.SHIPPING_SERVICE_FACADE)
    private ShippingServiceFacade shippingServiceFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    protected ContentServiceFacade contentServiceFacade;

    private Carrier carrier;

    private CarrierSla carrierSla;

    private String supplier;

    /**
     * Construct shipping panel.
     *
     * @param id panel id
     */
    public ShippingView(final String id, final String supplier) {

        super(id);

        this.supplier = supplier;
        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        final Customer customer = customerServiceFacade.getCheckoutCustomer(ApplicationDirector.getCurrentShop(), cart);

        final Map<String, Object> contentParams = new HashMap<>();
        final List<Carrier> carriers = shippingServiceFacade.findCarriers(ApplicationDirector.getShoppingCart(), this.supplier);
        final List<CarrierSla> carrierSlas = new ArrayList<CarrierSla>();
        for (Carrier carrier : carriers) {
            carrierSlas.addAll(carrier.getCarrierSla());
        }

        // Restore carrier by sla from shopping cart into current model.
        final Pair<Carrier, CarrierSla> selection =
                shippingServiceFacade.getCarrierSla(ApplicationDirector.getShoppingCart(), this.supplier, carriers);

        setCarrier(selection.getFirst());
        setCarrierSla(selection.getSecond());

        final Form form = new StatelessForm(SHIPPING_FORM);

        final Component shippingSelector = new RadioGroup<CarrierSla>(
                CARRIER_SLA_LIST,
                new PropertyModel<CarrierSla>(this, "carrierSla")) {

            /** {@inheritDoc} */
            protected void onSelectionChanged(final Object descriptor) {
                super.onSelectionChanged(carrierSla);


                final ShoppingCart cart = ApplicationDirector.getShoppingCart();

                final Set<Long> slaSelection = new HashSet<Long>(cart.getCarrierSlaId().values());
                slaSelection.add(carrierSla.getCarrierslaId());

                final Pair<Boolean, Boolean> addressNotRequired = shippingServiceFacade.isAddressNotRequired(slaSelection);

                final Address billingAddress;
                final Address shippingAddress;
                if (customer != null &&
                        (!addressNotRequired.getFirst() || !addressNotRequired.getSecond())) {
                    final Address billingAddressTemp = customer.getDefaultAddress(Address.ADDR_TYPE_BILLING);
                    final Address shippingAddressTemp = customer.getDefaultAddress(Address.ADDR_TYPE_SHIPPING);

                    if (shippingAddressTemp != null) { //normal case when we entered shipping address

                        if (!cart.isSeparateBillingAddress() || billingAddressTemp == null) {

                            billingAddress = shippingAddressTemp;
                            shippingAddress = shippingAddressTemp;

                        } else {

                            billingAddress = billingAddressTemp;
                            shippingAddress = shippingAddressTemp;

                        }

                    } else if (billingAddressTemp != null) { // exception use case when we only have billing address

                        billingAddress = billingAddressTemp;
                        shippingAddress = billingAddressTemp;

                    } else {

                        billingAddress = null;
                        shippingAddress = null;

                    }

                } else {

                    billingAddress = null;
                    shippingAddress = null;

                }

                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SETCARRIERSLA, cart,
                        (Map) new HashMap() {{
                            put(ShoppingCartCommand.CMD_SETCARRIERSLA, String.valueOf(carrierSla.getCarrierslaId()) + '-' + supplier);
                            put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_BILLING_NOT_REQUIRED, addressNotRequired.getFirst());
                            put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_BILLING_ADDRESS, billingAddress);
                            put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_DELIVERY_NOT_REQUIRED, addressNotRequired.getSecond());
                            put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_DELIVERY_ADDRESS, shippingAddress);
                        }}
                );

                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_RECALCULATEPRICE,
                        cart,
                        (Map) Collections.singletonMap(ShoppingCartCommand.CMD_RECALCULATEPRICE, ShoppingCartCommand.CMD_RECALCULATEPRICE));

                ((AbstractWebPage) getPage()).persistCartIfNecessary();

                addPriceView(form);

                addShippingOptionFeedback();
            }

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }
        }.add(
                new ListView<CarrierSla>("shippingList", carrierSlas) {
                    protected void populateItem(final ListItem<CarrierSla> shippingItem) {
                        shippingItem.add(new Radio<CarrierSla>("shippingLabel", new Model<CarrierSla>(shippingItem.getModelObject())));
                        shippingItem.add(new Label("shippingName", shippingItem.getModelObject().getCarrier().getName() +
                                ", " + shippingItem.getModelObject().getName()));

                        final boolean infoVisible = shippingItem.getModelObject().equals(carrierSla);

                        contentParams.put("cart", cart);
                        contentParams.put("carrierSla", carrierSla);

                        shippingItem.add(new Label("shippingInfo", contentServiceFacade.getDynamicContentBody("checkout_shipping_"
                                        + shippingItem.getModelObject().getGuid(),
                                ShopCodeContext.getShopId(), getLocale().getLanguage(), contentParams)).setEscapeModelStrings(false).setVisible(infoVisible));

                    }
                });

        form.addOrReplace(shippingSelector);

        addPriceView(form);

        add(form);

        if (carriers.isEmpty()) {
            addNoShippingOptionFeedback();
        } else {
            addShippingOptionFeedback();
        }

    }

    private void addNoShippingOptionFeedback() {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();

        final Map<String, String> supplierNames = shippingServiceFacade.getCartItemsSuppliers(cart);
        String supplierName = supplierNames.get(this.supplier);
        if (supplierName == null) {
            supplierName = this.supplier;
        }

        warn(
                getLocalizer().getString("deliveryBucketCarrierSelectNone", this, new Model<Serializable>(new ValueMap(
                        Collections.singletonMap("supplier", supplierName)
                )))
        );
    }

    private void addShippingOptionFeedback() {
        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        if (cart.getCarrierSlaId().get(this.supplier) == null || cart.getCarrierSlaId().get(this.supplier) <= 0L) {

            final Map<String, String> supplierNames = shippingServiceFacade.getCartItemsSuppliers(cart);
            String supplierName = supplierNames.get(this.supplier);
            if (supplierName == null) {
                supplierName = this.supplier;
            }

            info(
                    getLocalizer().getString("deliveryBucketCarrierSelect", this, new Model<Serializable>(new ValueMap(
                            Collections.singletonMap("supplier", supplierName)
                    )))
            );
        }
    }


    /**
     * Add shipping price view to given form if shipping method is selected.
     *
     * CPOINT - this method just displays the fixed price for this SLA, potentially this
     *          value can be calculated based on order or promotions
     *
     * @param form given form.
     */
    private void addPriceView(final Form form) {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        final Total total = cart.getTotal();
        final Long slaId = cart.getCarrierSlaId().get(this.supplier);

        if (slaId == null) {
            form.addOrReplace(new Label(PRICE_LABEL));
            form.addOrReplace(new Label(PRICE_VIEW));
        } else {
            final ProductPriceModel model = shippingServiceFacade.getCartShippingSupplierTotal(cart, this.supplier);

            form.addOrReplace(new Label(PRICE_LABEL, getLocalizer().getString(PRICE_LABEL, this)));
            form.addOrReplace(
                    new PriceView(
                            PRICE_VIEW,
                            model,
                            total.getAppliedDeliveryPromo(), true, true,
                            model.isTaxInfoEnabled(), model.isTaxInfoUseNet(), model.isTaxInfoShowAmount()
                    ) {
                        @Override
                        public boolean isVisible() {
                            return true; // Always visible to show free shipping!
                        }
                    }
            );

        }

    }

    private List<CarrierSla> getCarrierSlas() {
        if (this.carrier == null) {
            return Collections.EMPTY_LIST;
        }
        return new ArrayList<CarrierSla>(carrier.getCarrierSla());
    }

    /**
     * Get selected carrier.
     *
     * @return selected carrier.
     */
    public Carrier getCarrier() {
        return carrier;
    }

    /**
     * Set selected carrier.
     *
     * @param carrier selected carrier.
     */
    public void setCarrier(final Carrier carrier) {
        this.carrier = carrier;
    }

    /**
     * Get selected carrier Sla.
     *
     * @return carrie sla
     */
    public CarrierSla getCarrierSla() {
        return carrierSla;
    }

    /**
     * Set selected carrier Sla.
     *
     * @param carrierSla carrier Sla.
     */
    public void setCarrierSla(final CarrierSla carrierSla) {
        this.carrierSla = carrierSla;
    }

}

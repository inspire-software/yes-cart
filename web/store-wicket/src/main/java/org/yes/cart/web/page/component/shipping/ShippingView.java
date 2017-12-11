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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.AddressBookFacade;
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

    @SpringBean(name = StorefrontServiceSpringKeys.ADDRESS_BOOK_FACADE)
    private AddressBookFacade addressBookFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.SHIPPING_SERVICE_FACADE)
    private ShippingServiceFacade shippingServiceFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    protected ContentServiceFacade contentServiceFacade;

    private Carrier carrier;

    private CarrierSla carrierSla;

    private String supplier;

    private Date requestedDate;

    /**
     * Construct shipping panel.
     *
     * @param id panel id
     */
    public ShippingView(final String id, final String supplier) {

        super(id);

        this.supplier = supplier;
        final ShoppingCart cart = getCurrentCart();
        final Customer customer = customerServiceFacade.getCheckoutCustomer(getCurrentShop(), cart);

        final Map<String, Object> contentParams = new HashMap<>();
        final List<Carrier> carriers = shippingServiceFacade.findCarriers(cart, this.supplier);
        final List<CarrierSla> carrierSlas = shippingServiceFacade.getSortedCarrierSla(cart, carriers);

        // Restore carrier by sla from shopping cart into current model.
        final Pair<Carrier, CarrierSla> selection =
                shippingServiceFacade.getCarrierSla(cart, this.supplier, carriers);

        setCarrier(selection.getFirst());
        setCarrierSla(selection.getSecond());

        if (selection.getSecond() != null && selection.getSecond().isNamedDay()) {
            final String suffix = selection.getSecond().getCarrierslaId() + supplier;
            final String requested = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + suffix;
            final long selectedDate = NumberUtils.toLong(getCurrentCart().getOrderInfo().getDetailByKey(requested));
            if (selectedDate > 0L) {
                setRequestedDate(new Date(selectedDate));
            }
        }

        final Form form = new StatelessForm(SHIPPING_FORM) {
            @Override
            protected void onSubmit() {

                final String suffix = getCarrierSla().getCarrierslaId() + supplier;
                final String requested = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + suffix;
                final String requestedDateLongStr = getRequestedDate() != null ? String.valueOf(getRequestedDate().getTime()) : "";

                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SETORDERDETAILS, getCurrentCart(),
                        (Map) Collections.singletonMap(ShoppingCartCommand.CMD_SETORDERDETAILS, requested + ":" + requestedDateLongStr)
                );

                ((AbstractWebPage) getPage()).persistCartIfNecessary();

                super.onSubmit();
            }
        };

        final Component shippingSelector = new RadioGroup<CarrierSla>(
                CARRIER_SLA_LIST,
                new PropertyModel<CarrierSla>(this, "carrierSla")) {

            /** {@inheritDoc} */
            protected void onSelectionChanged(final Object descriptor) {
                super.onSelectionChanged(carrierSla);


                final ShoppingCart cart = getCurrentCart();

                final Set<Long> slaSelection = new HashSet<Long>(cart.getCarrierSlaId().values());
                slaSelection.add(carrierSla.getCarrierslaId());

                final Pair<Boolean, Boolean> addressNotRequired = shippingServiceFacade.isAddressNotRequired(slaSelection);

                final Address billingAddress;
                final Address shippingAddress;
                if (customer != null &&
                        (!addressNotRequired.getFirst() || !addressNotRequired.getSecond())) {

                    final Shop customerShop = getCurrentCustomerShop();
                    final Address billingAddressTemp = addressBookFacade.getDefaultAddress(customer, customerShop, Address.ADDR_TYPE_BILLING);
                    final Address shippingAddressTemp = addressBookFacade.getDefaultAddress(customer, customerShop, Address.ADDR_TYPE_SHIPPING);

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

                        final String shippingName =
                                getI18NSupport().getFailoverModel(
                                        shippingItem.getModelObject().getDisplayName(),
                                        shippingItem.getModelObject().getName()
                                ).getValue(getLocale().getLanguage());

                        shippingItem.add(new Label("shippingName", shippingName));

                        final boolean infoVisible = shippingItem.getModelObject().equals(carrierSla);
                        final boolean showDateSelect = infoVisible && carrierSla.isNamedDay();

                        contentParams.put("cart", cart);
                        contentParams.put("carrierSla", carrierSla);

                        final long contentShopId = getCurrentShopId();
                        shippingItem.add(new Label("shippingInfo", contentServiceFacade.getDynamicContentBody("checkout_shipping_"
                                        + shippingItem.getModelObject().getGuid(),
                                contentShopId, getLocale().getLanguage(), contentParams)).setEscapeModelStrings(false).setVisible(infoVisible));

                        if (showDateSelect) {

                            final IModel<Date> model = new PropertyModel<Date>(ShippingView.this, "requestedDate");

                            final String namedDateSelectorId = "namedDaySelection" + getCarrierSla().getCarrierslaId() + supplier;
                            final DateTextField namedDateSelector = new DateTextField("namedDaySelection", model, "dd/MM/yyyy"); // TODO: format in settings?
                            namedDateSelector.add(new AttributeModifier("id", namedDateSelectorId));

                            String js = "";

                            final String suffix = selection.getSecond().getCarrierslaId() + supplier;
                            final String minKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + "Min" + suffix;
                            final String maxKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + "Max" + suffix;
                            final String excludedDatesKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + "DExcl" + suffix;
                            final String excludedWeekdaysKey = AttributeNamesKeys.Cart.ORDER_INFO_REQUESTED_DELIVERY_DATE_ID + "WExcl" + suffix;

                            final String min = getCurrentCart().getOrderInfo().getDetailByKey(minKey);
                            final String max = getCurrentCart().getOrderInfo().getDetailByKey(maxKey);
                            final String excludedD = getCurrentCart().getOrderInfo().getDetailByKey(excludedDatesKey);
                            final String[] excludedDays = StringUtils.split(excludedD, ',');
                            final String excludedW = getCurrentCart().getOrderInfo().getDetailByKey(excludedWeekdaysKey);
                            final String[] excludedWeekdays = StringUtils.split(excludedW, ',');

                            final StringBuilder jsOut = new StringBuilder();
                            jsOut.append("        <script type=\"text/javascript\">\n");
                            jsOut.append("            $(function () {\n");
                            jsOut.append("                var _input = $('#" + namedDateSelectorId + "')\n");
                            jsOut.append("                _input.parent().datetimepicker({\n");
                            jsOut.append("                    format: 'DD/MM/YYYY',\n");
                            jsOut.append("                    ignoreReadonly: true,\n");
                            if (getRequestedDate() != null) {
                                jsOut.append("                date: new Date(" + getRequestedDate().getTime() + "),\n");
                            }
                            jsOut.append("                    minDate: new Date(" + min + "),\n");
                            jsOut.append("                    maxDate: new Date(" + max + "),\n");
                            if (excludedDays != null) {
                                jsOut.append("                disabledDates: [\n");
                                for (final String excludedDay : excludedDays) {
                                    jsOut.append("                 new Date(" + excludedDay + "),\n");
                                }
                                jsOut.append("                ],\n");
                            }
                            if (excludedWeekdays != null) {
                                jsOut.append("                daysOfWeekDisabled: [\n");
                                for (final String excludedWeekday : excludedWeekdays) {
                                    jsOut.append("                " + (NumberUtils.toInt(excludedWeekday) - 1) + ",");
                                }
                                jsOut.append("                ],\n");
                            }
                            jsOut.append("                    locale: '" + getLocale().getLanguage() + "'\n");
                            jsOut.append("                }).on('dp.change', function() { setTimeout(function() { $('#" + namedDateSelectorId + "').closest('form').submit(); }, 200); });\n");
                            jsOut.append("            });\n");
                            jsOut.append("        </script>\n");

                            js = jsOut.toString();

                            final Component namedDaySelectionConfig = new Label("namedDaySelectionConfig", js).setEscapeModelStrings(false);

                            final Component namedDayEditor = new Fragment("namedDayEditor", "namedDayFragment", ShippingView.this)
                                    .add(namedDateSelector).add(namedDaySelectionConfig);

                            shippingItem.add(namedDayEditor);

                        } else {

                            final Component namedDayEditor = new Fragment("namedDayEditor", "namedDayFragmentEmpty", ShippingView.this);
                            shippingItem.add(namedDayEditor);

                        }


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

        final ShoppingCart cart = getCurrentCart();

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
        final ShoppingCart cart = getCurrentCart();
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

        final ShoppingCart cart = getCurrentCart();
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
                            model.isTaxInfoEnabled(), model.isTaxInfoShowAmount()
                    ) {
                        @Override
                        public boolean isVisible() {
                            return true; // Always visible to show free shipping!
                        }
                    }
            );

        }

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

    /**
     * Get requested date for named delivery.
     *
     * @return requested date
     */
    public Date getRequestedDate() {
        return requestedDate;
    }

    /**
     * Set requested date for named delivery.
     *
     * @param requestedDate requested date
     */
    public void setRequestedDate(final Date requestedDate) {
        this.requestedDate = requestedDate;
    }
}

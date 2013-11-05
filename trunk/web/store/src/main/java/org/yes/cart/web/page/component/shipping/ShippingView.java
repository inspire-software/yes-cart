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

package org.yes.cart.web.page.component.shipping;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CarrierService;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.page.component.util.CarrierRenderer;
import org.yes.cart.web.page.component.util.CarrierSlaRenderer;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.math.BigDecimal;
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
    private final static String PRICE_VIEW = "priceView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = ServiceSpringKeys.CARRIER_SERVICE)
    private CarrierService carrierService;

    @SpringBean(name = ServiceSpringKeys.CARRIER_SLA_SERVICE)
    private CarrierSlaService carrierSlaService;

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;


    private Carrier carrier;

    private CarrierSla carrierSla;


    /**
     * Restore carrier by sla from shopping cart into current model.
     *
     * @param carriers list of carriers
     */
    private void restoreCarrierSla(final List<Carrier> carriers) {

        final Long slaId = ApplicationDirector.getShoppingCart().getCarrierSlaId();

        setCarrier(null);
        setCarrierSla(null);

        if (slaId != null) {
            carrierSlaService.getById(slaId);
            for (Carrier carrier : carriers) {
                for (CarrierSla carrierSla : carrier.getCarrierSla()) {
                    if (slaId == carrierSla.getCarrierslaId()) {
                        this.carrierSla = carrierSla;
                        this.carrier = carrier;
                        break;
                    }
                }
            }
        }

    }


    /**
     * Construct shipping panel.
     *
     * @param id panel id
     */
    public ShippingView(final String id) {

        super(id);

        final List<Carrier> carriers = carrierService.findCarriers(
                null, //TODO: V2 get from default shipping addr . probably not to limit , but put it first
                null,
                null,
                ApplicationDirector.getShoppingCart().getCurrencyCode());

        restoreCarrierSla(carriers);

        final Form form = new Form(SHIPPING_FORM);

        final DropDownChoice<CarrierSla> carrierSlaChoice = new DropDownChoice<CarrierSla>(
                CARRIER_SLA_LIST,
                new PropertyModel<CarrierSla>(this, "carrierSla"),
                getCarrierSlas()) {

            @Override
            protected void onSelectionChanged(final CarrierSla carrierSla) {
                super.onSelectionChanged(carrierSla);

                final ShoppingCart cart = ApplicationDirector.getShoppingCart();
                final Customer customer = customerServiceFacade.findCustomer(cart.getShoppingContext().getCustomerEmail());
                final String shippingAddressId;
                final String billingAddressId;
                if (customer != null) {
                    Address billingAddress = customer.getDefaultAddress(Address.ADDR_TYPE_BILLING);
                    Address shippingAddress = customer.getDefaultAddress(Address.ADDR_TYPE_SHIPING);

                    shippingAddressId = String.valueOf(shippingAddress.getAddressId());

                    if (!cart.isSeparateBillingAddress() || billingAddress == null) {
                        billingAddress = shippingAddress;
                    }

                    billingAddressId = String.valueOf(billingAddress.getAddressId());
                } else {
                    shippingAddressId = "";
                    billingAddressId = "";
                }

                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SETCARRIERSLA, ApplicationDirector.getShoppingCart(),
                        (Map) new HashMap() {{
                            put(ShoppingCartCommand.CMD_SETCARRIERSLA, String.valueOf(carrierSla.getCarrierslaId()));
                            put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_BILLING_ADDRESS, billingAddressId);
                            put(ShoppingCartCommand.CMD_SETCARRIERSLA_P_DELIVERY_ADDRESS, shippingAddressId);
                        }}
                );

                addPriceView(form);

            }

            /** {@inheritDoc} */
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            /** {@inheritDoc} */
            protected CharSequence getDefaultChoice(final String selectedValue) {
                return super.getDefaultChoice(carrierSla == null ? "" : selectedValue);
            }

        };

        carrierSlaChoice.setChoiceRenderer(new CarrierSlaRenderer(this)).setRequired(true);

        form.addOrReplace(carrierSlaChoice);


        form.add(

                new DropDownChoice<Carrier>(CARRIER_LIST, new PropertyModel<Carrier>(this, "carrier"), carriers) {

                    @Override
                    protected void onSelectionChanged(final Carrier carrier) {

                        super.onSelectionChanged(carrier);

                        setCarrierSla(null);

                        carrierSlaChoice.setChoices((List) null);

                        carrierSlaChoice.setChoices(new ArrayList<CarrierSla>(carrier.getCarrierSla()));

                        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SETCARRIERSLA, ApplicationDirector.getShoppingCart(),
                                (Map) Collections.singletonMap(
                                        ShoppingCartCommand.CMD_SETCARRIERSLA,
                                        null)
                        );

                        addPriceView(form);

                    }

                    @Override
                    protected boolean wantOnSelectionChangedNotifications() {
                        return true;
                    }
                }.setChoiceRenderer(new CarrierRenderer(this)).setRequired(true)

        );


        addPriceView(form);

        add(form);


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
        final Long slaId = cart.getCarrierSlaId();

        if (slaId == null) {
            form.addOrReplace(
                    new Label(PRICE_VIEW)
            );
        } else {
            final CarrierSla carrierSla = carrierSlaService.getById(slaId);
            form.addOrReplace(
                    new PriceView(
                            PRICE_VIEW,
                            new Pair<BigDecimal, BigDecimal>(total.getDeliveryListCost(), total.getDeliveryCost()),
                            carrierSla.getCurrency(),
                            total.getAppliedDeliveryPromo(), true, true
                    )
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

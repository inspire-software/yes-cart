package org.yes.cart.web.page.component.shipping;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.domain.CarrierService;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.SetCarrierSlaCartCommandImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.util.CarrierRenderer;
import org.yes.cart.web.page.component.util.CarrierSlaRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = ServiceSpringKeys.CARRIER_SERVICE)
    private CarrierService carrierService;

    @SpringBean(name = ServiceSpringKeys.CARRIER_SLA_SERVICE)
    private CarrierSlaService carrierSlaService;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;




    private Carrier carrier;

    private CarrierSla carrierSla;


    /**
     * Restore carrier by sla from shoppint cart into current model.
     * @param carriers list of carriers
     */
    private void restoreCarrierSla(final List<Carrier> carriers) {

        final Integer slaId = ApplicationDirector.getShoppingCart().getCarrierSlaId();

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
                null, //todo get from default shipping addr
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

                shoppingCartCommandFactory.create(
                        Collections.singletonMap(
                                SetCarrierSlaCartCommandImpl.CMD_KEY,
                                String.valueOf(carrierSla.getCarrierslaId()))
                ).execute(ApplicationDirector.getShoppingCart());


                //todo ((AbstractWebPage) getPage()).setResponsePageExt(CheckoutPage.class, new PageParameters("step=address"));

            }

            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }
        };

        carrierSlaChoice.setChoiceRenderer(new CarrierSlaRenderer()).setRequired(true);


        form.add(

                new DropDownChoice<Carrier>(CARRIER_LIST, new PropertyModel<Carrier>(this, "carrier"), carriers) {

                    @Override
                    protected void onSelectionChanged(final Carrier carrier) {
                        super.onSelectionChanged(carrier);
                        carrierSlaChoice.setChoices(new ArrayList<CarrierSla>(carrier.getCarrierSla()));
                        setCarrierSla(null);
                        shoppingCartCommandFactory.create(
                                Collections.singletonMap(
                                SetCarrierSlaCartCommandImpl.CMD_KEY,
                                null)
                        ).execute(ApplicationDirector.getShoppingCart());

                    }

                    @Override
                    protected boolean wantOnSelectionChangedNotifications() {
                        return true;
                    }
                }.setChoiceRenderer(new CarrierRenderer()).setRequired(true)

        );

        form.add(carrierSlaChoice);

        add(form);


    }

    private List<CarrierSla> getCarrierSlas() {
        if (this.carrier == null) {
            return  Collections.EMPTY_LIST;
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

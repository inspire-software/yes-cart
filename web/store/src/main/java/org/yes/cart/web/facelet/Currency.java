package org.yes.cart.web.facelet;

import org.apache.commons.collections.map.SingletonMap;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.impl.ChangeCurrencyEventCommandImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.constants.ManagedBeanELNames;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/15/11
 * Time: 2:02 PM
 */
@ManagedBean(name = WebParametersKeys.REQUEST_CURRENCY)
@RequestScoped
public class Currency {

    @ManagedProperty(ManagedBeanELNames.EL_CURRENCY_SYMBOL_SERVICE)
    private CurrencySymbolService currencySymbolService;

    @ManagedProperty(ManagedBeanELNames.EL_APPLICATION_DYNAMYC_CACHE)
    private ApplicationDirector applicationDirector;

    @ManagedProperty(ManagedBeanELNames.EL_SESSION_SHOPPING_CART)
    private ShoppingCart shoppingCart;


    /**
     * Get selected currency.
     * @return selected currency.
     */
    public String getSelectedCurrency() {
        if (shoppingCart.getCurrencyCode() == null) {
            final Shop shop = applicationDirector.getShopById(shoppingCart.getShoppingContext().getShopId());
            new ChangeCurrencyEventCommandImpl(
                    applicationDirector.getApplicationContext(),
                    new SingletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, shop.getDefaultCurrency())
            ).execute(shoppingCart);
        }
        return shoppingCart.getCurrencyCode();
    }

    /**
     * Set selected currency.
     * @param selectedCurrency selected currency.
     */
    public void setSelectedCurrency(final String selectedCurrency) {
        new ChangeCurrencyEventCommandImpl(
                applicationDirector.getApplicationContext(),
                new SingletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, selectedCurrency)
        ).execute(shoppingCart);
    }

    /**
     * Change value event handler.
     * @param changeEvent event to handle.
     */
    public void selectedCurrencyChange(final ValueChangeEvent changeEvent) {
        setSelectedCurrency((String) changeEvent.getNewValue());
    }

    /**
     * Get items to select in combo box for shop in shopping context.
     * @return list of currency code - currency symbol
     */
    public Map<String, String> getSelectItems() {
        final Shop shop = applicationDirector.getShopById(shoppingCart.getShoppingContext().getShopId());
        return currencySymbolService.getCurrencyToDisplayAsMap(shop.getSupportedCurrensies());
    }

    /**
     * Set service to use.
     * @param currencySymbolService  service to use.
     */
    public void setCurrencySymbolService(final CurrencySymbolService currencySymbolService) {
        this.currencySymbolService = currencySymbolService;
    }

    /**
     * Set service to use.
     * @param applicationDirector  service to use.
     */
    public void setApplicationDirector(final ApplicationDirector applicationDirector) {
        this.applicationDirector = applicationDirector;
    }

    /**
     * Set service to use.
     * @param shoppingCart service to use.
     */
    public void setShoppingCart(final ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
}

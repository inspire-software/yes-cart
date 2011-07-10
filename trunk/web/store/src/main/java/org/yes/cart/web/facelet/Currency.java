package org.yes.cart.web.facelet;

import org.apache.commons.collections.map.SingletonMap;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.shoppingcart.impl.ChangeCurrencyEventCommandImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.constants.ManagedBeanELNames;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ValueChangeEvent;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/15/11
 * Time: 2:02 PM
 */
@ManagedBean(name = WebParametersKeys.REQUEST_CURRENCY)
@RequestScoped
public class Currency extends BaseFacelet {

    @ManagedProperty(ManagedBeanELNames.EL_CURRENCY_SYMBOL_SERVICE)
    private CurrencySymbolService currencySymbolService;

    /**
     * Get selected currency.
     * @return selected currency.
     */
    public String getSelectedCurrency() {
        if (getShoppingCart().getCurrencyCode() == null) {
            final Shop shop = getApplicationDirector().getShopById(getShoppingCart().getShoppingContext().getShopId());
            new ChangeCurrencyEventCommandImpl(
                    ApplicationDirector.getApplicationContext(),
                    new SingletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, shop.getDefaultCurrency())
            ).execute(getShoppingCart());
        }
        return getShoppingCart().getCurrencyCode();
    }

    /**
     * Set selected currency.
     * @param selectedCurrency selected currency.
     */
    public void setSelectedCurrency(final String selectedCurrency) {
        new ChangeCurrencyEventCommandImpl(
                ApplicationDirector.getApplicationContext(),
                new SingletonMap(ChangeCurrencyEventCommandImpl.CMD_KEY, selectedCurrency)
        ).execute(getShoppingCart());
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
        final Shop shop = getApplicationDirector().getShopById(getShoppingCart().getShoppingContext().getShopId());
        return currencySymbolService.getCurrencyToDisplayAsMap(shop.getSupportedCurrensies());
    }

    /**
     * Set service to use.
     * @param currencySymbolService  service to use.
     */
    public void setCurrencySymbolService(final CurrencySymbolService currencySymbolService) {
        this.currencySymbolService = currencySymbolService;
    }


}

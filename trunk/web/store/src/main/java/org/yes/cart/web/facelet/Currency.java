package org.yes.cart.web.facelet;

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;

import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/15/11
 * Time: 2:02 PM
 */
@ManagedBean(name = WebParametersKeys.REQUEST_CURRENCY)
@RequestScoped
public class Currency {

    @ManagedProperty("#{bikeDataProvider}")
    private CurrencySymbolService currencySymbolService;



    private String selectedCurrency;

    /**
     * Get selected currency.
     * @return selected currency.
     */
    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    /**
     * Set selected currency.
     * @param selectedCurrency selected currency.
     */
    public void setSelectedCurrency(final String selectedCurrency) {
        this.selectedCurrency = selectedCurrency;
    }

    public List<Pair<String, String>> getSelectItems() {
        return currencySymbolService.getCurrencyToDisplay("");
    }
}

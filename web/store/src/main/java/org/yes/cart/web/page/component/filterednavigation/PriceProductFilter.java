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

package org.yes.cart.web.page.component.filterednavigation;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.NavigationContext;
import org.yes.cart.domain.query.PriceNavigation;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;

import java.math.BigDecimal;

/**
 *
 * Component responsible to build  list view of available price ranges to filtering.
 * Currently included price ranges not supported.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/30/11
 * Time: 5:07 PM
 */
public class PriceProductFilter extends AbstractProductFilter {

    private Boolean visibilityRezult;

    @SpringBean(name = ServiceSpringKeys.PRICE_NAVIGATION)
    private PriceNavigation priceNavigation;

    @SpringBean(name = StorefrontServiceSpringKeys.CURRENCY_SYMBOL_SERVICE)
    private CurrencySymbolService currencySymbolService;

    @SpringBean(name = StorefrontServiceSpringKeys.FILTERNAV_SUPPORT_PRICE)
    private PriceFilteredNavigationSupport priceFilteredNavigationSupport;


    /**
     * Create price product filter component.
     * @param id         panel id
     * @param categoryId current category id
     * @param navigationContext navigation context.
     */
    public PriceProductFilter(final String id, final long categoryId, final NavigationContext navigationContext) {
        super(id, categoryId, navigationContext);
    }

    /**
     * {@inheritDoc}
     * Convert value from filtered navigation parameter to more human readable form. Example
     * USD-500-1000 to  $ 500...1000
     *
     * @param valueToAdapt - expected value in following format CUR-LOW-HIGH
     * @return currency symbol  low high
     */
    protected String adaptValueForLinkLabel(final String valueToAdapt, final String displayValue) {
        Pair<String, Pair<BigDecimal, BigDecimal>> pair = priceNavigation.decomposePriceRequestParams(valueToAdapt);
        return priceNavigation.composePriceRequestParams(
                currencySymbolService.getCurrencySymbol(pair.getFirst()),
                pair.getSecond().getFirst(),
                pair.getSecond().getSecond(),
                " ",
                "..."
        );

    }


    /**
     * {@inheritDoc}
     */
    public boolean isVisible() {

        if (getCategoryId() > 0) {

            if(visibilityRezult == null) {

                final ShoppingCart cart = ApplicationDirector.getShoppingCart();

                setNavigationRecords(
                        priceFilteredNavigationSupport.getFilteredNavigationRecords(
                                getNavigationContext(), getCategoryId(),
                                cart.getCurrencyCode(),
                                cart.getCurrentLocale(),
                                getLocalizer().getString("price", this)
                        )
                );

                visibilityRezult = super.isVisible()
                        && getNavigationRecords() != null
                        && !getNavigationRecords().isEmpty();

            }

            return visibilityRezult;

        }

        return false;

    }

}

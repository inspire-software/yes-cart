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

package org.yes.cart.web.page.component.price;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 10-Sep-2011
 * Time: 12:29:11
 */
public class PriceView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String CURRENCY_LABEL = "currency";
    private static final String WHOLE_LABEL = "whole";
    private static final String DOT_LABEL = "dot";
    private static final String DECIMAL_LABEL = "decimal";
    private static final String SAVE_LABEL = "save";
    private static final String PROMO_LABEL = "promo";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    

    private static final String CSS_SUFFIX_WHOLE = "-price-whole";
    private static final String CSS_SUFFIX_DOT = "-price-dot";
    private static final String CSS_SUFFIX_DECIMAL = "-price-decimal";
    private static final String CSS_SUFFIX_CURRENCY = "-price-currency";


    private final String promos;
    private final Pair<BigDecimal, BigDecimal> pricePair;
    private final String currencySymbol;
    private final boolean showCurrencySymbol;
    private final boolean showSavings;

    private static final String[] EMPTY_FORMATED_PRICE = new String[] { StringUtils.EMPTY, StringUtils.EMPTY };

    @SpringBean(name = StorefrontServiceSpringKeys.CURRENCY_SYMBOL_SERVICE)
    private CurrencySymbolService currencySymbolService;


    /**
     * Create price view.
     * @param id component id.
     * @param model model.
     * @param showCurrencySymbol currency symbol.
     * @param showSavings show user friendly percentage savings
     */
    public PriceView(final String id,
                     final IModel<SkuPrice> model,
                     final boolean showCurrencySymbol,
                     final boolean showSavings) {
        super(id, model);
        final SkuPrice skuPrice = model.getObject();
        this.pricePair = new Pair<BigDecimal, BigDecimal>(
                skuPrice == null ? null : skuPrice.getRegularPrice(),
                skuPrice == null ? null : skuPrice.getSalePriceForCalculation()
        );
        this.currencySymbol = skuPrice == null ? null : skuPrice.getCurrency();
        this.showCurrencySymbol = showCurrencySymbol;
        this.showSavings = showSavings;
        this.promos = null;

    }

    /**
     * Create price view.
     * @param id component id.
     * @param pricePair regular / sale price pair.
     * @param currencySymbol currency symbol
     * @param appliedPromos applied promotions
     * @param showCurrencySymbol currency symbol.
     * @param showSavings show user friendly percentage savings
     */
    public PriceView(final String id,
                     final Pair<BigDecimal, BigDecimal> pricePair,
                     final String currencySymbol,
                     final String appliedPromos,
                     final boolean showCurrencySymbol,
                     final boolean showSavings) {
        super(id);
        this.pricePair = pricePair;
        this.showCurrencySymbol = showCurrencySymbol;
        this.currencySymbol = currencySymbol;
        this.showSavings = showSavings;
        this.promos = appliedPromos;
    }

    /**
     * Create price view.
     * @param id component id.
     * @param price regular price.
     * @param showCurrencySymbol currency symbol.
     * @param currencySymbol currency symbol
     */
    public PriceView(final String id,
                     BigDecimal price,
                     final String currencySymbol,
                     final boolean showCurrencySymbol) {
        super(id);
        this.pricePair = new Pair<BigDecimal, BigDecimal> (price, null);
        this.showCurrencySymbol = showCurrencySymbol;
        this.currencySymbol = currencySymbol;
        this.showSavings = false;
        this.promos = null;
    }

    /**
     * Get whole and decimal part of money to render.
     * @param price price
     * @return array of string to render.
     */
    String[] getFormattedPrice(BigDecimal price) {
        final String[] formatted;
        if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(price, BigDecimal.ZERO)) { // show zeros!
            final String priceString = price.setScale(Constants.DEFAULT_SCALE, RoundingMode.HALF_UP).toPlainString();
            formatted = StringUtils.split(priceString, '.');
        } else { // if price was null
            formatted = EMPTY_FORMATED_PRICE;
        }
        return formatted;
    }

    @Override
    protected void onBeforeRender() {

        boolean showSave = false;
        String savePercent = "";

        BigDecimal priceToFormat = pricePair.getFirst();
        String cssModificator = "regular";
        if (pricePair.getSecond() != null && MoneyUtils.isFirstBiggerThanSecond(pricePair.getFirst(), pricePair.getSecond())) {
            priceToFormat = pricePair.getSecond();
            cssModificator = "sale";
            showSave = this.showSavings;
            if (showSave) {
                final BigDecimal save = MoneyUtils.getDiscountDisplayValue(pricePair.getFirst(), pricePair.getSecond());
                savePercent = save.toString();
            }
        }
        final String[] formatted = getFormattedPrice(priceToFormat);

        addOrReplace(
                new Label(WHOLE_LABEL, formatted[0])
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_WHOLE)))
        .addOrReplace(
                new Label(DOT_LABEL, ".")
                        .setVisible(StringUtils.isNotBlank(formatted[0]) || StringUtils.isNotBlank(formatted[1]))
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_DOT)))
        .addOrReplace(
                new Label(DECIMAL_LABEL, formatted[1])
                        .setVisible(StringUtils.isNotBlank(formatted[0]) || StringUtils.isNotBlank(formatted[1]))
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_DECIMAL)))
        .addOrReplace(
                new Label(CURRENCY_LABEL, currencySymbolService.getCurrencySymbol(currencySymbol))
                        .setVisible(showCurrencySymbol)
                        .setEscapeModelStrings(false)
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_CURRENCY))
        );


        addOrReplace(
                new Label(SAVE_LABEL, new StringResourceModel("savePercent", this, null, new Object[] { savePercent }))
                        .setVisible(showSave)
                        .add(new AttributeModifier(HTML_CLASS, "sale-price-save"))
        );

        addOrReplace(
                new Label(PROMO_LABEL, promos)
                        .setVisible(StringUtils.isNotBlank(promos))
                        .add(new AttributeModifier(HTML_CLASS, "sale-price-save"))
        );

        super.onBeforeRender();

    }

    /** {@inheritDoc}*/
    public boolean isVisible() {
        return super.isVisible()
                &&
                pricePair.getFirst() != null
                &&
                !MoneyUtils.isFirstEqualToSecond(
                        BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP),
                        MoneyUtils.notNull(pricePair.getFirst().setScale(Constants.DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP)),
                        2);
    }
}

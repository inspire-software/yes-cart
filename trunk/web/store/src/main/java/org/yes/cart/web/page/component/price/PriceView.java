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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;

import java.math.BigDecimal;
import java.text.DecimalFormat;

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
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    

    private static final String CSS_SUFFIX_WHOLE = "-price-whole";
    private static final String CSS_SUFFIX_DOT = "-price-dot";
    private static final String CSS_SUFFIX_DECIMAL = "-price-decimal";
    private static final String CSS_SUFFIX_CURRENCY = "-price-currency";



    private final Pair<BigDecimal, BigDecimal> pricePair;
    private final String currencySymbol;
    private final boolean showCurrencySymbol;
    private static final String decimalSeparator; // to perform split operation
    private static final String[] emptyFormatedPrice;

    @SpringBean(name = StorefrontServiceSpringKeys.CURRENCY_SYMBOL_SERVICE)
    private CurrencySymbolService currencySymbolService;


    static {
        emptyFormatedPrice  = new String[] {StringUtils.EMPTY, StringUtils.EMPTY};
        DecimalFormat decimalFormat = new DecimalFormat(Constants.MONEY_FORMAT);
        String separator =  String.valueOf(decimalFormat.getDecimalFormatSymbols().getDecimalSeparator());
        if (".".equals(separator)) {
            decimalSeparator = "\\.";
        } else {
            decimalSeparator = separator;
        }
    }


    /**
     * Create price view.
     * @param id component id.
     * @param model model.
     * @param showCurrencySymbol currency symbol.
     */
    public PriceView(final String id, final IModel<SkuPrice> model, final boolean showCurrencySymbol) {
        super(id, model);
        final SkuPrice skuPrice = model.getObject();
        this.pricePair = new Pair<BigDecimal, BigDecimal>(
                skuPrice == null ? null : skuPrice.getRegularPrice(),
                skuPrice == null ? null : skuPrice.getSalePriceForCalculation()
        );
        this.currencySymbol = skuPrice == null ? null : skuPrice.getCurrency();
        this.showCurrencySymbol = showCurrencySymbol;

    }

    /**
     * Create price view.
     * @param id component id.
     * @param pricePair regular / sale price pair.
     * @param showCurrencySymbol currency symbol.
     * @param currencySymbol currency symbol
     */
    public PriceView(final String id,
                     final Pair<BigDecimal, BigDecimal> pricePair,
                     final String currencySymbol,
                     final boolean showCurrencySymbol) {
        super(id);
        this.pricePair = pricePair;
        this.showCurrencySymbol = showCurrencySymbol;
        this.currencySymbol = currencySymbol;
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
    }

    /**
     * Get whole and decimal part of monay to render.
     * @param price price
     * @return array of string to render.
     */
    String[] getFormatedPrice(BigDecimal price) {
        final DecimalFormat decimalFormat = new DecimalFormat(Constants.MONEY_FORMAT);
        final String[] formated;
        if (MoneyUtils.isFirstBiggerThanSecond(price, BigDecimal.ZERO)) {
            formated = decimalFormat.format(
                    MoneyUtils.notNull(price, BigDecimal.ZERO)
            ).split(decimalSeparator);
        } else {
            formated = emptyFormatedPrice;
        }
        return formated;
    }

    @Override
    protected void onBeforeRender() {
        BigDecimal priceToFormat = pricePair.getFirst();
        String cssModificator = "regular";
        if (pricePair.getSecond() != null) {
            priceToFormat = pricePair.getSecond();
            cssModificator = "sale";
        }
        final String[] formated = getFormatedPrice(priceToFormat);

        addOrReplace(
                new Label(WHOLE_LABEL, formated[0])
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_WHOLE)))
        .addOrReplace(
                new Label(DOT_LABEL, ".")
                        .setVisible(StringUtils.isNotBlank(formated[0]) || StringUtils.isNotBlank(formated[1]))
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_DOT)))
        .addOrReplace(
                new Label(DECIMAL_LABEL, formated[1])
                        .setVisible(StringUtils.isNotBlank(formated[0]) || StringUtils.isNotBlank(formated[1]))
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_DECIMAL)))
        .addOrReplace(
                new Label(CURRENCY_LABEL, currencySymbolService.getCurrencySymbol(currencySymbol))
                        .setVisible(showCurrencySymbol)
                        .setEscapeModelStrings(false)
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_CURRENCY))
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

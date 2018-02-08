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

package org.yes.cart.web.page.component.price;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.ProductPriceModel;
import org.yes.cart.domain.entity.ProductPromotionModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;
import org.yes.cart.web.support.service.ProductServiceFacade;
import org.yes.cart.web.util.WicketUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 10-Sep-2011
 * Time: 12:29:11
 */
public class PriceView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String CURRENCY_LABEL = "currency";
    private static final String CURRENCY2_LABEL = "currency2";
    private static final String WHOLE_LABEL = "whole";
    private static final String DOT_LABEL = "dot";
    private static final String DECIMAL_LABEL = "decimal";
    private static final String TAX_LABEL = "tax";
    private static final String SAVE_LABEL = "save";
    private static final String PROMO_LABEL = "promo";
    private static final String LIST_PRICE_LABEL = "listPrice";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    

    private static final String CSS_SUFFIX_WHOLE = "-price-whole";
    private static final String CSS_SUFFIX_DOT = "-price-dot";
    private static final String CSS_SUFFIX_DECIMAL = "-price-decimal";
    private static final String CSS_SUFFIX_CURRENCY = "-price-currency";
    private static final String CSS_SUFFIX_TAX = "-price-tax";


    private final String promos;
    private final ProductPriceModel productPriceModel;
    private final boolean showCurrencySymbol;
    private final boolean showSavings;
    private final boolean showTax;
    private final boolean showTaxNet;
    private final boolean showTaxAmount;
    private final boolean showGratis;

    private static final Pair<BigDecimal, BigDecimal> NULL = new Pair<BigDecimal, BigDecimal>(null, null);
    private static final String[] EMPTY_FORMATED_PRICE = new String[] { StringUtils.EMPTY, StringUtils.EMPTY };

    @SpringBean(name = StorefrontServiceSpringKeys.CURRENCY_SYMBOL_SERVICE)
    private CurrencySymbolService currencySymbolService;

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_SERVICE_FACADE)
    protected ProductServiceFacade productServiceFacade;

    /**
     * Create price view.
     * @param id component id.
     * @param productPriceModel price model.
     * @param appliedPromos applied promotions
     * @param showCurrencySymbol currency symbol.
     * @param showSavings show user friendly percentage savings
     * @param showTax show tax information
     * @param showTaxAmount show amount of tax (rather than percent)
     */
    public PriceView(final String id,
                     final ProductPriceModel productPriceModel,
                     final String appliedPromos,
                     final boolean showCurrencySymbol,
                     final boolean showSavings,
                     final boolean showTax,
                     final boolean showTaxAmount
    ) {
        this(id, productPriceModel, appliedPromos, showCurrencySymbol, showSavings, showTax, showTaxAmount, false);
    }

    /**
     * Create price view.
     * @param id component id.
     * @param productPriceModel price model.
     * @param appliedPromos applied promotions
     * @param showCurrencySymbol currency symbol.
     * @param showSavings show user friendly percentage savings
     * @param showTax show tax information
     * @param showTaxAmount show amount of tax (rather than percent)
     * @param showGratis show gratis prices
     */
    public PriceView(final String id,
                     final ProductPriceModel productPriceModel,
                     final String appliedPromos,
                     final boolean showCurrencySymbol,
                     final boolean showSavings,
                     final boolean showTax,
                     final boolean showTaxAmount,
                     final boolean showGratis
    ) {
        super(id);
        this.productPriceModel = productPriceModel;
        this.showCurrencySymbol = showCurrencySymbol;
        this.showSavings = showSavings;
        this.promos = appliedPromos;
        this.showTax = showTax && productPriceModel != null && productPriceModel.getPriceTax() != null;
        this.showTaxNet = this.showTax && productPriceModel.isTaxInfoUseNet();
        this.showTaxAmount = showTaxAmount;
        this.showGratis = showGratis;
    }


    /**
     * Get whole and decimal part of money to render.
     * @param price price
     * @return array of string to render.
     */
    String[] getFormattedPrice(BigDecimal price) {
        final String[] formatted;
        if (MoneyUtils.isFirstBiggerThanOrEqualToSecond(price, BigDecimal.ZERO)) { // show zeros!
            final String priceString = price.setScale(Constants.MONEY_SCALE, RoundingMode.HALF_UP).toPlainString();
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
        String listPrice = "";
        final String lang = getLocale().getLanguage();

        BigDecimal priceToFormat = productPriceModel.getRegularPrice();
        String cssModificator = "regular";
        if (productPriceModel.getSalePrice() != null && MoneyUtils.isFirstBiggerThanSecond(productPriceModel.getRegularPrice(), productPriceModel.getSalePrice())) {
            priceToFormat = productPriceModel.getSalePrice();
            cssModificator = "sale";
            showSave = this.showSavings;
            if (showSave) {
                final BigDecimal save = MoneyUtils.getDiscountDisplayValue(productPriceModel.getRegularPrice(), productPriceModel.getSalePrice());
                savePercent = save.toString();
                listPrice = productPriceModel.getRegularPrice().setScale(Constants.MONEY_SCALE, RoundingMode.HALF_UP).toPlainString();
            }
        }
        final boolean nonZero = MoneyUtils.isPositive(priceToFormat);
        final String[] formatted = nonZero || !this.showGratis ?
                getFormattedPrice(priceToFormat) :
                new String[] { getString("gratis"), null };
        final boolean shopFractionalPart = StringUtils.isNotBlank(formatted[1]);

        addOrReplace(
                new Label(WHOLE_LABEL, formatted[0])
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_WHOLE)));
        addOrReplace(
                new Label(DOT_LABEL, ".")
                        .setVisible(StringUtils.isNotBlank(formatted[0]) || StringUtils.isNotBlank(formatted[1]))
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_DOT))
                .setVisible(shopFractionalPart)
        );
        addOrReplace(
                new Label(DECIMAL_LABEL, formatted[1])
                        .setVisible(StringUtils.isNotBlank(formatted[0]) || StringUtils.isNotBlank(formatted[1]))
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_DECIMAL))
                .setVisible(shopFractionalPart)
        );

        final Pair<String, Boolean> symbol = currencySymbolService.getCurrencySymbol(productPriceModel.getCurrency());

        final boolean showMainCurrencySymbol = showCurrencySymbol && (nonZero || !showGratis);

        addOrReplace(
                new Label(CURRENCY_LABEL, symbol.getFirst())
                        .setVisible(showMainCurrencySymbol && !symbol.getSecond())
                        .setEscapeModelStrings(false)
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_CURRENCY)));

        addOrReplace(
                new Label(CURRENCY2_LABEL, symbol.getFirst())
                        .setVisible(showMainCurrencySymbol && symbol.getSecond())
                        .setEscapeModelStrings(false)
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_CURRENCY)));


        final Map<String, Object> tax = new HashMap<String, Object>();
        if (this.showTaxAmount) {
            tax.put("tax", this.productPriceModel.getPriceTax() != null ? this.productPriceModel.getPriceTax().toPlainString() : Total.ZERO.toPlainString());
        } else {
            tax.put("tax", this.productPriceModel.getPriceTaxRate() != null ? this.productPriceModel.getPriceTaxRate().stripTrailingZeros().toPlainString() + "%" : "0%");
        }
        tax.put("code", this.productPriceModel.getPriceTaxCode());

        final String taxNote = this.showTaxNet ? "taxNoteNet" : "taxNoteGross";

        final WebMarkupContainer taxWrapper = new WebMarkupContainer("taxWrapper");
        taxWrapper.add(new Label(TAX_LABEL,
                WicketUtil.createStringResourceModel(this, taxNote, tax))
                .setVisible(this.showTax && nonZero)
                .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_TAX)));
        taxWrapper.setVisible(this.showTax && nonZero);

        addOrReplace(taxWrapper);

        final WebMarkupContainer listPriceWrapper = new WebMarkupContainer("listPriceWrapper");
        listPriceWrapper.add(new Label(LIST_PRICE_LABEL, listPrice)
                .setVisible(showSave)
                .add(new AttributeModifier(HTML_CLASS, "list-price")));
        listPriceWrapper.setVisible(showSave);

        addOrReplace(listPriceWrapper);

        final WebMarkupContainer saveWrapper = new WebMarkupContainer("saveWrapper");
        saveWrapper.setVisible(showSave);

        final Label discount = new Label(SAVE_LABEL,
                WicketUtil.createStringResourceModel(this, "savePercent",
                        Collections.<String, Object>singletonMap("discount", savePercent)));

        discount.setVisible(showSave);
        discount.setEscapeModelStrings(false);
        discount.add(new AttributeModifier(HTML_CLASS, "label label-success sale-price-save"));

        if (showSave && StringUtils.isNotBlank(promos)) {

            final Map<String, ProductPromotionModel> promoModels = productServiceFacade.getPromotionModel(promos);

            final StringBuilder details = new StringBuilder();
            for (final ProductPromotionModel model : promoModels.values()) {

                final String name = model.getName().getValue(lang);
                final String desc = model.getDescription().getValue(lang);

                details.append(name);
                if (model.getCouponCode() != null) {
                    details.append(" (").append(model.getCouponCode()).append(")");
                }

                if (StringUtils.isNotBlank(desc)) {
                    details.append(": ").append(desc);
                }
                details.append("\n");
            }

            discount.add(new AttributeModifier("title", details.toString()));
        } else {
            discount.add(new AttributeModifier("title", WicketUtil.createStringResourceModel(this, "savePercentTitle")));
        }

        saveWrapper.add(discount);

        addOrReplace(saveWrapper);

        final WebMarkupContainer promoWrapper = new WebMarkupContainer("promoWrapper");

        final boolean hasPromo = StringUtils.isNotBlank(promos);
        promoWrapper.add(new Label(PROMO_LABEL, promos)
                .setVisible(hasPromo)
                .add(new AttributeModifier(HTML_CLASS, "sale-price-save sale-price-save-details")));
        promoWrapper.setVisible(hasPromo);

        addOrReplace(promoWrapper);

        super.onBeforeRender();

        setVisible(
                !this.showGratis &&
                        MoneyUtils.isPositive(productPriceModel.getRegularPrice())
                || this.showGratis &&
                        MoneyUtils.isFirstBiggerThanOrEqualToSecond(productPriceModel.getRegularPrice(), BigDecimal.ZERO)
        );

    }

}

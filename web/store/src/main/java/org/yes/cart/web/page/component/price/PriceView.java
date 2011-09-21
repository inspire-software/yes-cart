package org.yes.cart.web.page.component.price;

import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.service.CurrencySymbolService;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.constants.Constants;
import org.apache.wicket.model.IModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.AttributeModifier;
import org.apache.commons.lang.StringUtils;

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



    private final SkuPrice skuPrice;
    private final boolean showCurrencySymbol;
    private static final String decimalSeparator; // to perform sptil operation
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
        this.skuPrice = model.getObject();
        this.showCurrencySymbol = showCurrencySymbol;
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
        BigDecimal priceToFormat = skuPrice.getRegularPrice();
        String cssModificator = "regular";
        if (skuPrice.getSalePrice() != null) {
            priceToFormat = skuPrice.getSalePrice();
            cssModificator = "sale";
        }
        final String[] formated = getFormatedPrice(priceToFormat);

        add(
                new Label(WHOLE_LABEL, formated[0])
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_WHOLE))
        );
        add(
                new Label(DOT_LABEL, ".")
                        .setVisible(StringUtils.isNotBlank(formated[0]) || StringUtils.isNotBlank(formated[1]))
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_DOT))
        );
        add(
                new Label(DECIMAL_LABEL, formated[1])
                        .setVisible(StringUtils.isNotBlank(formated[0]) || StringUtils.isNotBlank(formated[1]))
                        .add(new AttributeModifier(HTML_CLASS, cssModificator + CSS_SUFFIX_DECIMAL))
        );
        add(
                new Label(CURRENCY_LABEL, currencySymbolService.getCurrencySymbol(skuPrice.getCurrency()))
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
                skuPrice != null
                &&
                !MoneyUtils.isFirstEqualToSecond(
                        BigDecimal.ZERO,
                        MoneyUtils.notNull(skuPrice.getRegularPrice()),
                        2);
    }
}

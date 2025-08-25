package org.yes.cart.domain.entity.impl;

import org.yes.cart.domain.entity.PriceModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User: denispavlov
 * Date: 01/10/2015
 * Time: 23:36
 */
public class PriceModelImpl implements PriceModel {

    private final String ref;

    private final String currency;
    private final BigDecimal quantity;

    private final BigDecimal regularPrice;
    private final BigDecimal salePrice;

    private final boolean priceUponRequest;
    private final boolean priceOnOffer;

    private final boolean taxInfoEnabled;
    private final boolean taxInfoUseNet;
    private final boolean taxInfoShowAmount;

    private final String priceTaxCode;
    private final BigDecimal priceTaxRate;
    private final boolean priceTaxExclusive;
    private final BigDecimal priceTax;

    private final LocalDateTime validFrom;
    private final LocalDateTime validTo;

    /**
     * Basic setting for no tax information enabled.
     */
    public PriceModelImpl(final String ref,
                          final String currency,
                          final BigDecimal quantity,
                          final boolean priceUponRequest,
                          final boolean priceOnOffer,
                          final BigDecimal regularPrice,
                          final BigDecimal salePrice,
                          final LocalDateTime validFrom,
                          final LocalDateTime validTo) {
        this(ref, currency, quantity, priceUponRequest, priceOnOffer, regularPrice, salePrice, false, false, false, null, null, false, null, validFrom, validTo);
    }

    public PriceModelImpl(final String ref,
                          final String currency,
                          final BigDecimal quantity,
                          final boolean priceUponRequest,
                          final boolean priceOnOffer,
                          final BigDecimal regularPrice,
                          final BigDecimal salePrice,
                          final boolean taxInfoEnabled,
                          final boolean taxInfoUseNet,
                          final boolean taxInfoShowAmount,
                          final String priceTaxCode,
                          final BigDecimal priceTaxRate,
                          final boolean priceTaxExclusive,
                          final BigDecimal priceTax,
                          final LocalDateTime validFrom,
                          final LocalDateTime validTo) {
        this.ref = ref;
        this.currency = currency;
        this.quantity = quantity;
        this.priceUponRequest = priceUponRequest;
        this.priceOnOffer = priceOnOffer;
        this.regularPrice = regularPrice;
        this.salePrice = salePrice;
        this.taxInfoEnabled = taxInfoEnabled;
        this.taxInfoUseNet = taxInfoUseNet;
        this.taxInfoShowAmount = taxInfoShowAmount;
        this.priceTaxCode = priceTaxCode;
        this.priceTaxRate = priceTaxRate;
        this.priceTaxExclusive = priceTaxExclusive;
        this.priceTax = priceTax;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    /** {@inheritDoc} */
    @Override
    public String getRef() {
        return ref;
    }

    /** {@inheritDoc} */
    @Override
    public String getCurrency() {
        return currency;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getQuantity() {
        return quantity;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getRegularPrice() {
        return regularPrice;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPriceUponRequest() {
        return priceUponRequest;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPriceOnOffer() {
        return priceOnOffer;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTaxInfoEnabled() {
        return taxInfoEnabled;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTaxInfoUseNet() {
        return taxInfoUseNet;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTaxInfoShowAmount() {
        return taxInfoShowAmount;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getPriceTax() {
        return priceTax;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getPriceTaxRate() {
        return priceTaxRate;
    }

    /** {@inheritDoc} */
    @Override
    public String getPriceTaxCode() {
        return priceTaxCode;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPriceTaxExclusive() {
        return priceTaxExclusive;
    }


    /** {@inheritDoc} */
    @Override
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getValidTo() {
        return validTo;
    }

}

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

package org.yes.cart.shoppingcart.impl;

import org.yes.cart.service.domain.TaxConfigService;
import org.yes.cart.service.domain.TaxService;
import org.yes.cart.shoppingcart.TaxProvider;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 04/11/2014
 * Time: 17:49
 */
public class TaxProviderImpl implements TaxProvider {

    private final TaxService taxService;
    private final TaxConfigService taxConfigService;

    private static class TaxImpl implements Tax {

        private final BigDecimal rate;
        private final String code;
        private final boolean excluded;

        private TaxImpl(final BigDecimal rate, final String code, final boolean excluded) {
            this.rate = rate;
            this.code = code;
            this.excluded = excluded;
        }

        /** {@inheritDoc} */
        public BigDecimal getRate() {
            return rate;
        }

        /** {@inheritDoc} */
        public String getCode() {
            return code;
        }

        /** {@inheritDoc} */
        public boolean isExcluded() {
            return excluded;
        }

    }

    private static final Tax NULL = new TaxImpl(BigDecimal.ZERO, "", false);

    public TaxProviderImpl(final TaxService taxService,
                           final TaxConfigService taxConfigService) {
        this.taxService = taxService;
        this.taxConfigService = taxConfigService;
    }

    /**
     * {@inheritDoc}
     */
    public Tax determineTax(final String shopCode,
                            final String currency,
                            final String countryCode,
                            final String stateCode,
                            final String itemCode) {

        final Long taxId = taxConfigService.getTaxIdBy(shopCode, currency, countryCode, stateCode, itemCode);
        if (taxId == null) {
            return NULL;
        }
        final org.yes.cart.domain.entity.Tax tax = taxService.getById(taxId);
        return new TaxImpl(tax.getTaxRate(), tax.getCode(), tax.getExclusiveOfPrice());
    }
}

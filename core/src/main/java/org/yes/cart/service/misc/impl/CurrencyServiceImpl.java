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

package org.yes.cart.service.misc.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.misc.CurrencyService;

import java.io.IOException;
import java.util.*;

/**
 * User: denispavlov
 * Date: 18/07/2016
 * Time: 09:02
 */
public class CurrencyServiceImpl implements CurrencyService {

    private final Map<String, String> currencyName;

    private final List<String> supportedCurrencies;

    private final ShopService shopService;

    /**
     * Construct currency service.
     * @param config  property file with i18n configurations
     * @param shopService shop service
     */
    public CurrencyServiceImpl(final Resource config,
                               final ShopService shopService) throws IOException {

        final Properties properties = new Properties();
        properties.load(config.getInputStream());

        this.shopService = shopService;
        this.currencyName = new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        this.currencyName.putAll(getCurrencyNameFromConfig(properties));
        this.supportedCurrencies = new ArrayList<String>(this.currencyName.keySet());
    }

    private Map<String, String> getCurrencyNameFromConfig(final Properties properties) {

        final String langs = properties.getProperty("admin.supported.currencies", "ALL,AFN,ANG,ARS,AUD,AWG,AZN,BAM,BBD,BGN,BND,BOB,BRL,BSD,BWP,BYR,BZD,CAD,CHF,CLP,CNY,COP,CRC,CUP,CZK,DKK,DOP,EEK,EGP,EUR,FJD,FKP,GBP,GGP,GHC,GIP,GTQ,GYD,HKD,HNL,HRK,HUF,IDR,ILS,IMP,INR,IRR,ISK,JEP,JMD,JPY,KGS,KHR,KPW,KRW,KYD,KZT,LAK,LBP,LKR,LRD,LTL,LVL,MKD,MNT,MUR,MXN,MYR,MZN,NAD,NGN,NIO,NOK,NPR,NZD,OMR,PAB,PEN,PHP,PKR,PLN,PYG,QAR,RON,RSD,RUB,SAR,SBD,SCR,SEK,SGD,SHP,SOS,SRD,SVC,SYP,THB,TRL,TRY,TTD,TVD,TWD,UAH,USD,UYU,UZS,VEF,VND,XCD,YER,ZAR,ZWD");

        final Map<String, String> all = new LinkedHashMap<String, String>();

        for (final String lang : StringUtils.split(langs, ',')) {
            all.put(lang, properties.getProperty("admin.supported.currencies." + lang, lang));
        }

        return all;
    }

    /** {@inheritDoc} */
    @Override
    public String resolveCurrencyName(final String currency) {
        return currencyName.get(currency);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getCurrencyName() {
        return currencyName;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getSupportedCurrencies() {
        return supportedCurrencies;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getSupportedCurrencies(final String shopCode) {

        final Shop shop = shopService.getShopByCode(shopCode);
        if (shop != null) {
            final List<String> currencyCodes = shop.getSupportedCurrenciesAsList();
            if (!currencyCodes.isEmpty()) {
                return currencyCodes;
            }
        }

        return Collections.emptyList();
    }
}

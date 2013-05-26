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

package org.yes.cart.model {
import mx.collections.ArrayCollection;

/**
 * Currency provider is a hardcoded currencies object since those are standardised.
 */
[Bindable]
public class CurrencyModel {

    /**
     * See spring configuration at web shop. Be sure, that array is syncronized with
     * provider curreny in spring context.
     *
     * ATM CIS countries and other still use several currency at shop(s).
     *  But keep in mind, that we support not only real currencies. WMZ, EGOLD, etc can be involved.
     *  There is no reference to this table to keep schema clear as posible.
     *
     */
    private static var allCurrencyCodes:ArrayCollection = new ArrayCollection([
        "ALL","AFN","ANG","ARS","AUD","AWG","AZN","BAM","BBD","BGN","BND","BOB","BRL","BSD","BWP","BYR","BZD","CAD","CHF","CLP","CNY","COP",
        "CRC","CUP","CZK","DKK","DOP","EEK","EGP","EUR","FJD","FKP","GBP","GGP","GHC","GIP","GTQ","GYD","HKD","HNL","HRK","HUF","IDR","ILS",
        "IMP","INR","IRR","ISK","JEP","JMD","JPY","KGS","KHR","KPW","KRW","KYD","KZT","LAK","LBP","LKR","LRD","LTL","LVL","MKD","MNT","MUR",
        "MXN","MYR","MZN","NAD","NGN","NIO","NOK","NPR","NZD","OMR","PAB","PEN","PHP","PKR","PLN","PYG","QAR","RON","RSD","RUB","SAR","SBD",
        "SCR","SEK","SGD","SHP","SOS","SRD","SVC","SYP","THB","TRL","TRY","TTD","TVD","TWD","UAH","USD","UYU","UZS","VEF","VND","XCD","YER",
        "ZAR","ZWD"
    ]);
    /**
     * Yeah,  i know, that this method look sucks, but i use action script only 1 week.
     * @param providedCurrencySymbols list of already supported currency symbols.
     * @return array all currency codes minus already suppoted.
     */
    public static function getAvailableCurrencyCodes(providedCurrencySymbols:ArrayCollection):ArrayCollection {
        var result:ArrayCollection = new ArrayCollection();
        if (providedCurrencySymbols != null) {
            for each(var candidate:String in allCurrencyCodes) {
                if(!isAlreadyProvided(candidate, providedCurrencySymbols)) {
                    result.addItem(candidate);
                }
            }
            return result;
        } else {
            return new ArrayCollection(allCurrencyCodes.toArray());
        }
    }

    public static function getAllCurrencyCodes():ArrayCollection {
        return new ArrayCollection(allCurrencyCodes.toArray());
    }

    private static function isAlreadyProvided(candidate:String, providedCurrencySymbols:ArrayCollection):Boolean {
        return providedCurrencySymbols.contains(candidate);
    }

    public function CurrencyModel() {

    }
}
}
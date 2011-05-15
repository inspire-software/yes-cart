package org.yes.cart.util {
import mx.collections.ArrayList;

[Bindable]
public class CurrencyProvider {

    /**
     * See spring configuration at web shop. Be sure, that array is syncronized with
     * provider curreny in spring context.
     *
     * ATM CIS countries and other still use several currency at shop(s).
     *  But keep in mind, that we support not only real currencies. WMZ, EGOLD, etc can be involved.
     *  There is no reference to this table to keep schema clear as posible.
     *
     */
    private static var allCurrencyCodes:Array =
[
    "ALL","AFN","ANG","ARS","AUD","AWG","AZN","BAM","BBD","BGN","BND","BOB","BRL","BSD","BWP","BYR","BZD","CAD","CHF","CLP","CNY","COP",
    "CRC","CUP","CZK","DKK","DOP","EEK","EGP","EUR","FJD","FKP","GBP","GGP","GHC","GIP","GTQ","GYD","HKD","HNL","HRK","HUF","IDR","ILS",
    "IMP","INR","IRR","ISK","JEP","JMD","JPY","KGS","KHR","KPW","KRW","KYD","KZT","LAK","LBP","LKR","LRD","LTL","LVL","MKD","MNT","MUR",
    "MXN","MYR","MZN","NAD","NGN","NIO","NOK","NPR","NZD","OMR","PAB","PEN","PHP","PKR","PLN","PYG","QAR","RON","RSD","RUB","SAR","SBD",
    "SCR","SEK","SGD","SHP","SOS","SRD","SVC","SYP","THB","TRL","TRY","TTD","TVD","TWD","UAH","USD","UYU","UZS","VEF","VND","XCD","YER",
    "ZAR","ZWD"
];
    /**
     * Yeah,  i know, that this method look sucks, but i use action script only 1 week.
     * @param providedCurrencySymbols list of already supported currency symbols.
     * @return array all currency codes minus already suppoted.
     */
    public static function getAvailableCurrencyCodes(providedCurrencySymbols:Array):Array {
        var result:ArrayList = new ArrayList();
        if (providedCurrencySymbols != null) {
            for each(var candidate:String in allCurrencyCodes) {
                if(!isAlreadyProvided(candidate, providedCurrencySymbols)) {
                    result.addItem(candidate);
                }
            }
            return result.toArray();
        } else {
            return allCurrencyCodes;
        }
    }

    public static function getAllCurrencyCodes():Array {
        return allCurrencyCodes;
    }

    private static function isAlreadyProvided(candidate:String, providedCurrencySymbols:Array):Boolean {
        return (providedCurrencySymbols.indexOf(candidate) > -1);
    }

    public function CurrencyProvider() {

    }
}
}
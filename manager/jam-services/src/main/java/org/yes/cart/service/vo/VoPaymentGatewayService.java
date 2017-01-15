/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo;

import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;

import java.util.Collection;
import java.util.List;

/**
 * User: denispavlov
 * Date: 01/08/2016
 * Time: 17:44
 */
public interface VoPaymentGatewayService {


    /**
     * Get allowed payment gateways in all modules.
     *
     * @param lang ui lang
     *
     * @return list of label-name pairs .
     */
    List<VoPaymentGatewayInfo> getPaymentGateways(String lang) throws Exception;

    /**
     * Get allowed payment gateways in all modules.
     *
     * @param lang ui lang
     * @param shopCode shop code
     *
     * @return list of label-name pairs .
     */
    List<VoPaymentGatewayInfo> getPaymentGatewaysForShop(String lang, String shopCode) throws Exception;

    /**
     * Get summary information for given shop.
     *
     * @param summary summary object to fill data for
     * @param shopCode given shop
     * @param lang locale for localised names
     *
     * @throws Exception
     */
    void fillShopSummaryDetails(VoShopSummary summary, String shopCode, String lang) throws Exception;

    /**
     * Get allowed payment gateways in all modules which are system enabled.
     *
     * @param lang ui lang
     *
     * @return list of label-name pairs .
     */
    List<VoPaymentGatewayInfo> getAllowedPaymentGatewaysForShops(String lang) throws Exception;

    /**
     * Get allowed payment gateways in all modules.
     *
     * @param lang ui lang
     *
     * @return list of label-name pairs .
     */
    List<MutablePair<String, String>> getAllowedPaymentGateways(String lang) throws Exception;

    /**
     * Get allowed payment gateways in all modules.
     *
     * @param lang ui lang
     * @param shopCode shop code
     *
     * @return list of label-name pairs .
     */
    List<MutablePair<String, String>> getAllowedPaymentGatewaysForShop(String lang, String shopCode) throws Exception;

    /**
     * Get available payment gateways in all modules.
     *
     * @param lang ui lang
     *
     * @return list of label-name pairs .
     */
    List<MutablePair<String, String>> getAvailablePaymentGateways(String lang) throws Exception;

    /**
     * Get available payment gateways in all modules.
     *
     * @param lang ui lang
     * @param shopCode shop code
     *
     * @return list of label-name pairs .
     */
    List<MutablePair<String, String>> getAvailablePaymentGatewaysForShop(String lang, String shopCode) throws Exception;


    /**
     * Get allowed payment gateways in all modules.
     *
     * @param lang ui lang
     *
     * @return list of label-name pairs .
     */
    List<VoPaymentGateway> getPaymentGatewaysWithParameters(String lang) throws Exception;

    /**
     * Get allowed payment gateways in all modules.
     *
     * @param lang ui lang
     * @param shopCode shop code
     *
     * @return list of label-name pairs .
     */
    List<VoPaymentGateway> getPaymentGatewaysWithParametersForShop(String lang, String shopCode) throws Exception;

    /**
     * Update the PG attributes.
     *
     * @param pgLabel PG label
     * @param vo PG attributes to update, boolean indicates if this attribute is to be removed (true) or not (false)
     * @return PG attributes.
     * @throws Exception
     */
    List<VoPaymentGatewayParameter> update(String pgLabel, List<MutablePair<VoPaymentGatewayParameter, Boolean>> vo) throws Exception;

    /**
     * Update the PG attributes.
     *
     * @param shopCode shop code for which to update the PG parameters
     * @param pgLabel PG label
     * @param vo PG attributes to update, boolean indicates if this attribute is to be removed (true) or not (false)
     * @return PG attributes.
     * @throws Exception
     */
    List<VoPaymentGatewayParameter> update(String shopCode, String pgLabel, List<MutablePair<VoPaymentGatewayParameter, Boolean>> vo) throws Exception;


    /**
     * Update the PG disabled flag.
     *
     * @param pgLabel PG label
     * @param disabled true if shop is disabled
     * @throws Exception
     */
    void updateDisabledFlag(String pgLabel, boolean disabled) throws Exception;

    /**
     * Update the PG disabled flag.
     *
     * @param shopCode shop code for which to set flag
     * @param pgLabel PG label
     * @param disabled true if shop is disabled
     * @throws Exception
     */
    void updateDisabledFlag(String shopCode, String pgLabel, boolean disabled) throws Exception;


}

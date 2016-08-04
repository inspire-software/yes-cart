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
import org.yes.cart.domain.vo.VoPaymentGatewayInfo;

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

}

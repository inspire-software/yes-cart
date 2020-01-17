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

import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 14:36
 */
public interface VoLocationService {


    /**
     * Get all vo in the system, filtered according to rights
     *
     * @return all countries
     *
     * @throws Exception errors
     */
    VoSearchResult<VoCountryInfo> getFilteredCountries(VoSearchContext filter) throws Exception;

    /**
     * Get vo by id.
     *
     * @param id country id
     *
     * @return vo
     *
     * @throws Exception errors
     */
    VoCountry getCountryById(long id) throws Exception;

    /**
     * Create new vo
     *
     * @param vo vo
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    VoCountry createCountry(VoCountry vo)  throws Exception;


    /**
     * Update vo
     *
     * @param vo vo
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    VoCountry updateCountry(VoCountry vo)  throws Exception;


    /**
     * Remove vo.
     *
     * @param id country id
     *
     * @throws Exception errors
     */
    void removeCountry(long id) throws Exception;


    /**
     * Get all vo in the system, filtered according to rights
     *
     * @param id country id
     *
     * @return all states by country id
     *
     * @throws Exception errors
     */
    List<VoState> getCountryStatesAll(long id) throws Exception;


    /**
     * Get all vo in the system, filtered according to rights
     *
     * @return all countries states
     *
     * @throws Exception errors
     */
    VoSearchResult<VoState> getFilteredStates(VoSearchContext filter) throws Exception;

    /**
     * Get vo by id.
     *
     * @param id state id
     *
     * @return vo
     *
     * @throws Exception errors
     */
    VoState getStateById(long id) throws Exception;

    /**
     * Create new vo
     *
     * @param vo vo
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    VoState createState(VoState vo)  throws Exception;


    /**
     * Update vo
     *
     * @param vo vo
     *
     * @return persistent version
     *
     * @throws Exception errors
     */
    VoState updateState(VoState vo)  throws Exception;


    /**
     * Remove vo.
     *
     * @param id state id
     *
     * @throws Exception errors
     */
    void removeState(long id) throws Exception;


}

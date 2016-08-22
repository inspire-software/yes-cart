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

import org.yes.cart.domain.vo.VoCountry;
import org.yes.cart.domain.vo.VoState;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 14:36
 */
public interface VoLocationService {


    /**
     * Get all vo in the system, filtered according to rights
     * @return all countries
     * @throws Exception
     */
    List<VoCountry> getAllCountries() throws Exception;

    /**
     * Get vo by id.
     *
     * @param id country id
     * @return vo
     * @throws Exception
     */
    VoCountry getCountryById(long id) throws Exception;

    /**
     * Create new vo
     * @param vo vo
     * @return persistent version
     * @throws Exception
     */
    VoCountry createCountry(VoCountry vo)  throws Exception;


    /**
     * Update vo
     * @param vo vo
     * @return persistent version
     * @throws Exception
     */
    VoCountry updateCountry(VoCountry vo)  throws Exception;


    /**
     * Remove vo.
     *
     * @param id country id
     * @throws Exception
     */
    void removeCountry(long id) throws Exception;


    /**
     * Get all vo in the system, filtered according to rights
     * @param id country id
     * @return all states by country id
     * @throws Exception
     */
    List<VoState> getAllStates(long id) throws Exception;

    /**
     * Get vo by id.
     *
     * @param id state id
     * @return vo
     * @throws Exception
     */
    VoState getStateById(long id) throws Exception;

    /**
     * Create new vo
     * @param vo vo
     * @return persistent version
     * @throws Exception
     */
    VoState createState(VoState vo)  throws Exception;


    /**
     * Update vo
     * @param vo vo
     * @return persistent version
     * @throws Exception
     */
    VoState updateState(VoState vo)  throws Exception;


    /**
     * Remove vo.
     *
     * @param id state id
     * @throws Exception
     */
    void removeState(long id) throws Exception;


}

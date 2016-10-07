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
import org.yes.cart.domain.vo.VoAttribute;
import org.yes.cart.domain.vo.VoAttributeGroup;
import org.yes.cart.domain.vo.VoEtype;

import java.util.List;

/**
 * User: denispavlov
 * Date: 09/08/2016
 * Time: 17:59
 */
public interface VoAttributeService {

    /**
     * Get all attribute etypes.
     * @return etypes
     * @throws Exception
     */
    List<VoEtype> getAllEtypes() throws Exception;

    /**
     * Get all attribute groups.
     * @return groups
     * @throws Exception
     */
    List<VoAttributeGroup> getAllGroups() throws Exception;

    /**
     * Get all attributes in group
     * @param group group
     * @return list of attributes
     * @throws Exception
     */
    List<VoAttribute> getAllAttributes(String group) throws Exception;

    /**
     * Get all attributes in group
     * @param group group
     * @param filter filter
     * @param max max
     * @return list of attributes
     * @throws Exception
     */
    List<VoAttribute> getFilteredAttributes(String group, String filter, int max) throws Exception;

    /**
     * Product types that use this attribute.
     * @param code attribute code
     * @return product types
     * @throws Exception
     */
    List<MutablePair<Long, String>> getProductTypesByAttributeCode(String code) throws Exception;



    /**
     * Get vo by id.
     *
     * @param id id
     * @return vo
     * @throws Exception
     */
    VoAttribute getAttributeById(long id) throws Exception;



    /**
     * Create new vo
     * @param vo vo
     * @return persistent version
     * @throws Exception
     */
    VoAttribute createAttribute(VoAttribute vo)  throws Exception;


    /**
     * Create new vo
     * @param vo carrier
     * @return persistent version
     * @throws Exception
     */
    VoAttribute updateAttribute(VoAttribute vo)  throws Exception;


    /**
     * Remove vo.
     *
     * @param id id
     * @throws Exception
     */
    void removeAttribute(long id) throws Exception;


}

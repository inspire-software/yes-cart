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
import org.yes.cart.domain.vo.VoProductType;
import org.yes.cart.domain.vo.VoProductTypeAttr;
import org.yes.cart.domain.vo.VoProductTypeInfo;

import java.util.List;

/**
 * User: denispavlov
 * Date: 22/08/2016
 * Time: 12:45
 */
public interface VoProductTypeService {

    /**
     * Get all types in the system, filtered by criteria and according to rights, up to max
     * @return list of categories
     * @throws Exception
     */
    List<VoProductTypeInfo> getFiltered(String filter, int max) throws Exception;

    /**
     * Get type by id.
     *
     * @param id type id
     * @return type vo
     * @throws Exception
     */
    VoProductType getById(long id) throws Exception;

    /**
     * Update given type.
     *
     * @param vo type to update
     * @return updated instance
     * @throws Exception
     */
    VoProductType update(VoProductType vo) throws Exception;

    /**
     * Create new type
     *
     * @param vo given instance to persist
     * @return persisted instance
     * @throws Exception
     */
    VoProductType create(VoProductTypeInfo vo) throws Exception;

    /**
     * Get type by id.
     *
     * @param id
     * @return type vo
     * @throws Exception
     */
    void remove(long id) throws Exception;


    /**
     * Get supported attributes by given type
     * @param typeId given type id
     * @return attributes
     * @throws Exception
     */
    List<VoProductTypeAttr> getTypeAttributes(long typeId) throws Exception;


    /**
     * Update the type attributes.
     *
     * @param vo type attributes to update, boolean indicates if this attribute is to be removed (true) or not (false)
     * @return type attributes.
     * @throws Exception
     */
    List<VoProductTypeAttr> update(List<MutablePair<VoProductTypeAttr, Boolean>> vo) throws Exception;



}

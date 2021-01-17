/*
 * Copyright 2009 Inspire-Software.com
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

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoDataDescriptor;
import org.yes.cart.domain.vo.VoDataGroupImpEx;

import java.util.List;

/**
 * User: inspiresoftware
 * Date: 16/01/2021
 * Time: 10:57
 */
public interface VoDataGroupServiceSupport {

    /**
     * Get all available data groups
     *
     * @return vos
     *
     * @throws Exception errors
     */
    List<VoDataGroupImpEx> getDataGroupsByNames(List<String> names) throws Exception;


    /**
     * Get data descriptors available to group
     *
     * @param name group name
     * @return vos
     *
     * @throws Exception errors
     */
    List<VoDataDescriptor> getDataGroupDescriptors(final String name) throws Exception;

    /**
     * Generate data descriptor template
     *
     * @param id group id
     *
     * @return filename and template as byte array
     */
    Pair<String, byte[]> generateDataGroupDescriptorTemplates(long id) throws Exception;


}

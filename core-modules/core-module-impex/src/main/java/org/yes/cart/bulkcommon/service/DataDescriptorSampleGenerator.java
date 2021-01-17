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

package org.yes.cart.bulkcommon.service;

import org.yes.cart.domain.misc.Pair;

import java.util.List;

/**
 * User: inspiresoftware
 * Date: 16/01/2021
 * Time: 10:15
 */
public interface DataDescriptorSampleGenerator {

    /**
     * Whether this sample generator supports given descriptor object.
     *
     * @param descriptor to generate sample for
     *
     * @return true if supports
     */
    boolean supports(Object descriptor);

    /**
     * Generate a sample/template import file
     *
     * @param descriptor to generate sample for
     *
     * @return template file name and content (defines aas list as implementation can add supporting files such as
     *         readme files of XSD)
     */
    List<Pair<String, byte[]>> generateSample(Object descriptor);

}

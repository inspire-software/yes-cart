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

package org.yes.cart.bulkcommon.service.support.common.impl;

import org.yes.cart.bulkcommon.service.DataDescriptorSampleGenerator;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.utils.spring.ArrayListBean;

import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 16/01/2021
 * Time: 10:30
 */
public class DataDescriptorSampleGeneratorFactoryImpl implements DataDescriptorSampleGenerator {

    private final List<DataDescriptorSampleGenerator> generators;

    public DataDescriptorSampleGeneratorFactoryImpl(final ArrayListBean<DataDescriptorSampleGenerator> generators) {
        this.generators = generators;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final Object descriptor) {
        return supportsInternal(descriptor) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, byte[]>> generateSample(final Object descriptor) {

        final DataDescriptorSampleGenerator generator = supportsInternal(descriptor);
        if (generator != null) {

            return generator.generateSample(descriptor);

        }
        
        return Collections.emptyList();
    }

    private DataDescriptorSampleGenerator supportsInternal(final Object descriptor) {

        for (final DataDescriptorSampleGenerator generator : this.generators) {

            if (generator.supports(descriptor)) {
                return generator;
            }
        }

        return null;

    }

}

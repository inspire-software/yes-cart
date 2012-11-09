/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.bulkimport.service.model;

import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows to overwrite or enhance immutable JobContext.
 *
 * User: denispavlov
 * Date: 12-11-09
 * Time: 11:03 AM
 */
public class JobContextDecoratorImpl implements JobContext {

    private final JobContext wrapped;
    private final Map<String, Object> additionalAttributes = new HashMap<String, Object>();

    public JobContextDecoratorImpl(final JobContext wrapped, final Map<String, Object> additionalAttributes) {
        this.wrapped = wrapped;
        this.additionalAttributes.putAll(additionalAttributes);
    }

    /** {@inheritDoc} */
    public JobStatusListener getListener() {
        return wrapped.getListener();
    }

    /** {@inheritDoc} */
    public boolean isAsync() {
        return wrapped.isAsync();
    }

    /** {@inheritDoc} */
    public <T> T getAttribute(final String name) {
        if (this.additionalAttributes.containsKey(name)) {
            return (T) this.additionalAttributes.get(name);
        }
        return wrapped.getAttribute(name);
    }

    /** {@inheritDoc} */
    public Map<String, Object> getAttributes() {
        final Map<String, Object> all = new HashMap<String, Object>();
        all.putAll(wrapped.getAttributes());
        all.putAll(additionalAttributes);
        return Collections.unmodifiableMap(all);
    }
}

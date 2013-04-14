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

package org.yes.cart.domain.entityindexer.impl;

import org.yes.cart.domain.entityindexer.ProductIndexer;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 5/6/12
 * Time: 9:10 AM
 */
public class EmptyProductIndexerImpl implements ProductIndexer {
    /** {@inheritDoc} */

    public void submitIndexTask(final Long productPkValue) {

        //doing nothing

    }

}

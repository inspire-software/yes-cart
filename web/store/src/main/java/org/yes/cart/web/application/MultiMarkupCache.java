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

package org.yes.cart.web.application;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.MarkupCache;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 9:09 AM
 */
public class MultiMarkupCache extends MarkupCache {

    /** The markup cache key provider used by MarkupCache */
    private IMarkupCacheKeyProvider multiMarkupCacheKeyProvider;


    public MultiMarkupCache() {
        super();
    }

    /**
	 * Get the markup cache key provider to be used.
	 *
	 * @param container
	 *            The MarkupContainer requesting the markup resource stream
	 * @return IMarkupResourceStreamProvider
	 */
	public IMarkupCacheKeyProvider getMarkupCacheKeyProvider(final MarkupContainer container) {
		if (container instanceof IMarkupCacheKeyProvider)	{
			return (IMarkupCacheKeyProvider)container;
		}

		if (multiMarkupCacheKeyProvider == null)	{
			multiMarkupCacheKeyProvider = new MultiMarkupCacheKeyProvider();
		}
		return multiMarkupCacheKeyProvider;
	}

}

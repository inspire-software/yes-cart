package org.yes.cart.web.application;

import org.apache.wicket.Application;
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

package org.yes.cart.web.application;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.DefaultMarkupCacheKeyProvider;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 9:10 AM
 */
public class MultiMarkupCacheKeyProvider extends DefaultMarkupCacheKeyProvider {

    /**
	 * Construct a proper key value for the cache
	 *
	 * @param container
	 *            The container requesting the markup
	 * @param clazz
	 *            The clazz to get the key for
	 * @return Key that uniquely identifies any markup that might be associated with this markup
	 *         container.
	 */
	public String getCacheKey(final MarkupContainer container, final Class<?> clazz) {

            final StorefrontApplication app = (StorefrontApplication) container.getApplication();
            return ApplicationDirector.getCurrentShop().getCode() + '_' + super.getCacheKey(container, clazz);

    }
}

package org.yes.cart.web.application;

import org.apache.wicket.markup.IMarkupCache;
import org.apache.wicket.markup.MarkupCache;
import org.apache.wicket.markup.MarkupFactory;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 11:16 AM
 */
public class MultiMarkupFactory extends MarkupFactory {

    private MultiMarkupCache multiMarkupCache;

    /**
     * {@inheritDoc}
     */
    public IMarkupCache getMarkupCache() {
        if (multiMarkupCache == null) {
            multiMarkupCache = new MultiMarkupCache();
        }
        return multiMarkupCache;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasMarkupCache() {
        return multiMarkupCache != null;
    }


}

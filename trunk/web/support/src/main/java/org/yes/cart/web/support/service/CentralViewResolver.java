package org.yes.cart.web.support.service;

import org.apache.lucene.search.BooleanQuery;

import java.util.List;
import java.util.Map;

/**
 * Service responsible to resolve label of central view in storefront.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/26/11
 * Time: 9:35 PM
 */
public interface CentralViewResolver {

    /**
     * Resolve central renderer label.
     *
     * @param parameters            request parameters map
     * @return resolved main panel renderer label if resolved, otherwise null
     */
    String resolveMainPanelRendererLabel(Map parameters);

    /**
     * Construct <code>BooleanQuery</code> by given parameters.
     *
     * @param queriesChain  query chain obtained from url
     * @param currentQuery  current query
     * @param categoryId    currenct category id
     * @param viewLabel     resolved view label
     * @param itemId        sku or product id
     * @return <code>BooleanQuery</code> in case if renderer label has a registered query builder, otherwise null.
     */
    BooleanQuery getBooleanQuery(List<BooleanQuery> queriesChain,
                                 String currentQuery,
                                 long categoryId,
                                 String viewLabel,
                                 String itemId);

}

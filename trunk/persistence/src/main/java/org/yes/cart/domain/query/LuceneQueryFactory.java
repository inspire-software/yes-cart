package org.yes.cart.domain.query;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public interface LuceneQueryFactory {
    /**
     * Get the combined from query chain query.
     * The currecnt query will be last in this chain.
     * Chain will be truncated if <code>currentQuery</code> already present in
     * list
     *
     * @param allQueries   query chain
     * @param currentQuery optional current query
     * @return combined from chain query
     */
    BooleanQuery getSnowBallQuery(List<BooleanQuery> allQueries, String currentQuery);

    /**
     * Get the combined from query chain query.
     * The currecnt query will be last in this chain.
     * Chain will be truncated if <code>currentQuery</code> already present in
     * list
     *
     * @param allQueries   query chain as query
     * @param currentQuery optional current query
     * @return combined from chain query
     */
    BooleanQuery getSnowBallQuery(BooleanQuery allQueries, Query currentQuery);

    /**
     * Get the queries chain.
     * <p/>
     * Note about search: Search can be performed on entire shop or in particular category.
     * Entire search if size of categories is one and first category is 0
     * otherwise need to use all sub categories, that belong to given category list.
     *
     * @param shopId                the current shop id
     * @param requestParameters     web request parameters
     * @param categories            given category ids
     * @param allowedAttributeCodes for filter not allowed attribute names from request parameters
     * @param allShopcategories     optional parameter all shop caterories with child as list used in case if
     *                              user perform serach on entire shop
     * @return ordered by cookie name list of cookies
     */
    List<BooleanQuery> getFilteredNavigationQueryChain(
            Long shopId,
            List<Long> categories,
            Map<String, ?> requestParameters,
            List<Object> allowedAttributeCodes,
            List<Long> allShopcategories
    );
}

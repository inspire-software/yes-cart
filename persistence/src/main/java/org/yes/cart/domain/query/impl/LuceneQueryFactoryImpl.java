package org.yes.cart.domain.query.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.PriceNavigation;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class LuceneQueryFactoryImpl implements LuceneQueryFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    /**
     * Only this indexes allowed for attributive filtered navigaion.
     */
    private final static String[] fields = {
            ProductSearchQueryBuilder.PRODUCT_NAME_FIELD,
            ProductSearchQueryBuilder.PRODUCT_DESCIPTION_FIELD,
            ProductSearchQueryBuilder.BRAND_FIELD,
            ProductSearchQueryBuilder.ATTRIBUTE_CODE_FIELD,
            ProductSearchQueryBuilder.ATTRIBUTE_VALUE_FIELD,
            ProductSearchQueryBuilder.ATTRIBUTE_VALUE_SEARCH_FIELD,
            ProductSearchQueryBuilder.PRODUCT_CATEGORY_FIELD,
            ProductSearchQueryBuilder.PRODUCT_PRICE_CURRENCY,
            ProductSearchQueryBuilder.PRODUCT_PRICE_AMOUNT,
            ProductSearchQueryBuilder.PRODUCT_PRICE_SHOP,
            ProductSearchQueryBuilder.PRODUCT_ID_FIELD
    };

    private MultiFieldQueryParser queryParser;

    private final PriceNavigation priceNavigation;
    private final AttributeService attributeService;

    protected MultiFieldQueryParser getQueryParser() {
        return queryParser;
    }

    /**
     * Construct query builder factory.
     * @param priceNavigation   price navigation service
     * @param attributeService  attribute service to filter not allowed page parameters during filtered navigation
     */
    public LuceneQueryFactoryImpl(final PriceNavigation priceNavigation, final AttributeService attributeService) {
        this.priceNavigation = priceNavigation;
        this.attributeService = attributeService;
    }


    private MultiFieldQueryParser getMultiFieldQueryParser() {
        if (queryParser == null) {
            queryParser = new MultiFieldQueryParser(Version.LUCENE_31, fields, new AsIsAnalyzer());
        }
        return queryParser;
    }

    /**
     * Parse string into query
     *
     * @param queryString string representation of lucene query
     * @return query if string was succsesfuly parsed.
     */
    private Query parseQuery(final String queryString) {
        try {
            return getMultiFieldQueryParser().parse(queryString);
        } catch (ParseException e) {
            LOG.error(MessageFormat.format("Can not parse given query {0}", queryString), e);
        }
        return null;
    }


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
    public BooleanQuery getSnowBallQuery(final List<BooleanQuery> allQueries, final String currentQuery) {
        BooleanQuery booleanQuery = new BooleanQuery();
        for (BooleanQuery cookie : allQueries) {
            booleanQuery.add(cookie, BooleanClause.Occur.MUST);
        }
        if (currentQuery != null) {
            // add current querry
            Query query = parseQuery(currentQuery);
            if (query != null) {
                booleanQuery.add(query, BooleanClause.Occur.MUST);
            }
        }
        return booleanQuery;

    }


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
    public BooleanQuery getSnowBallQuery(final BooleanQuery allQueries, final Query currentQuery) {
        BooleanQuery booleanQuery = new BooleanQuery();
        if (allQueries != null && StringUtils.isNotBlank(allQueries.toString())) {
            booleanQuery.add(allQueries, BooleanClause.Occur.MUST);
        }
        if (currentQuery != null && StringUtils.isNotBlank(currentQuery.toString())) {
            booleanQuery.add(currentQuery, BooleanClause.Occur.MUST);
        }
        return booleanQuery;
    }


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
     * @param allShopcategories     optional parameter all shop caterories with child as list used in case if
     *                              user perform serach on entire shop
     * @return ordered by cookie name list of cookies
     */
    public List<BooleanQuery> getFilteredNavigationQueryChain(
            final Long shopId,
            final List<Long> categories,
            final Map<String, ?> requestParameters,
            final List<Long> allShopcategories) {

        final List<String> allowedAttributeCodes = attributeService.getAllAttributeCodes();

        final List<BooleanQuery> queryChain = new ArrayList<BooleanQuery>();
        for (Map.Entry<String, ?> entry : requestParameters.entrySet()) {
            final String decodedKeyName = entry.getKey();
            if (isFilteredNavigationParameter(decodedKeyName, allowedAttributeCodes)) {
                Object value = entry.getValue();
                if (value != null) {
                    Object[] values = getValues(value);
                    for (Object val : values) {
                        BooleanQuery query;
                        if (ProductSearchQueryBuilder.BRAND_FIELD.equals(decodedKeyName)) {
                            query = createBrandChain(categories, val);
                        } else if (ProductSearchQueryBuilder.PRODUCT_PRICE.equals(decodedKeyName)) {
                            query = createPriceChain(shopId, categories, val);
                        } else if (ProductSearchQueryBuilder.QUERY.equals(decodedKeyName)) {
                            query = createSearchChain(categories, allShopcategories, val);
                        } else {
                            query = createAttributeChain(categories, decodedKeyName, val);
                        }
                        queryChain.add(query);
                    }
                }
            }
        }

        return queryChain;
    }

    private BooleanQuery createAttributeChain(final List<Long> categories, final String decodedKeyName, final Object val) {
        BooleanQuery query;
        final AttributiveSearchQueryBuilderImpl attributeSearchQueryBuilder = new AttributiveSearchQueryBuilderImpl();
        //is it Single or Range value navigation, determination will be based on hyphen atm
        final String attrValue = String.valueOf(val);
        if (attrValue.indexOf('-') > -1) { // value range navigation
            final String[] attrValues = attrValue.split("-");
            final Pair<String, String> valueRange = new Pair<String, String>(attrValues[0], attrValues[1]);
            query = attributeSearchQueryBuilder.createQuery(categories, decodedKeyName, valueRange);
        } else { //single attribute value navigation
            query = attributeSearchQueryBuilder.createQuery(categories, decodedKeyName, attrValue);
        }
        return query;
    }

    private BooleanQuery createSearchChain(final List<Long> categories, final List<Long> allShopcategories, final Object val) {
        BooleanQuery query;
        final GlobalSearchQueryBuilderImpl globalSearchQueryBuilder = new GlobalSearchQueryBuilderImpl();
        if (categories.size() == 1 && categories.get(0) == 0) {
            query = globalSearchQueryBuilder.createQuery(String.valueOf(val), allShopcategories);
        } else {
            query = globalSearchQueryBuilder.createQuery(String.valueOf(val), categories);
        }
        return query;
    }

    private BooleanQuery createPriceChain(final Long shopId, final List<Long> categories, final Object val) {
        final Pair<String, Pair<BigDecimal, BigDecimal>> priceRequestParams =
                priceNavigation.decomposePriceRequestParams(String.valueOf(val));
        final PriceSearchQueryBuilderImpl priceSearchQueryBuilder = new PriceSearchQueryBuilderImpl();
        return priceSearchQueryBuilder.createQuery(
                categories,
                shopId,
                priceRequestParams.getFirst(),
                priceRequestParams.getSecond().getFirst(),
                priceRequestParams.getSecond().getSecond()
        );
    }

    private BooleanQuery createBrandChain(final List<Long> categories, final Object val) {
        final BrandSearchQueryBuilder brandSearchQueryBuilder = new BrandSearchQueryBuilder();
        return brandSearchQueryBuilder.createQuery(categories, String.valueOf(val));
    }


    private Object[] getValues(final Object value) {
        if (value instanceof Object[]) {
            return (Object[]) value;
        } else if (value instanceof Collection) {
            return ((Collection) value).toArray();
        } else {
            return new Object[]{String.valueOf(value)};
        }
    }

    private boolean isFilteredNavigationParameter(final String parameterName,
                                                  final Collection<String> uniqueAttributeCodes) {
        return uniqueAttributeCodes.contains(parameterName);
    }


}

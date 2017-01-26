/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.web.service.rest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.query.PriceNavigation;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.domain.ro.*;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.page.component.filterednavigation.AttributeFilteredNavigationSupport;
import org.yes.cart.web.page.component.filterednavigation.BrandFilteredNavigationSupport;
import org.yes.cart.web.page.component.filterednavigation.PriceFilteredNavigationSupport;
import org.yes.cart.web.service.rest.impl.BookmarkMixin;
import org.yes.cart.web.service.rest.impl.CartMixin;
import org.yes.cart.web.service.rest.impl.RoMappingMixin;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CategoryServiceFacade;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.service.CurrencySymbolService;
import org.yes.cart.web.support.service.ProductServiceFacade;
import org.yes.cart.web.support.util.ProductSortingUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 * Date: 22/03/2015
 * Time: 15:17
 */
@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private CentralViewResolver centralViewResolver;
    @Autowired
    private CategoryServiceFacade categoryServiceFacade;
    @Autowired
    private ProductServiceFacade productServiceFacade;
    @Autowired
    private CurrencySymbolService currencySymbolService;
    @Autowired
    private LuceneQueryFactory luceneQueryFactory;
    @Autowired
    private BrandFilteredNavigationSupport brandsFilteredNavigationSupport;
    @Autowired
    private PriceFilteredNavigationSupport priceFilteredNavigationSupport;
    @Autowired
    private PriceNavigation priceNavigation;
    @Autowired
    private AttributeFilteredNavigationSupport attributeFilteredNavigationSupport;

    @Autowired
    private CartMixin cartMixin;
    @Autowired
    @Qualifier("restRoMappingMixin")
    private RoMappingMixin mappingMixin;
    @Autowired
    private BookmarkMixin bookmarkMixin;

    /**
     * Interface: PUT /yes-api/rest/search
     * <p>
     * <p>
     * Perform a product search.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/json or application/xml</td></tr>
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * {
     *           "pageNumber": 0,
     *           "pageSize": 2,
     *           "parameters": {
     *               "query": [ "Mini mouse", "red" ],
     *               "price": [ "EUR-_-500-_-1000" ],
     *               "brand": [ "Trust" ]
     *         },
     *         "category": "mice",
     *         "includeNavigation": true
     * }
     * </pre></code>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * &lt;search&gt;
     *     &lt;category&gt;mice&lt;/category&gt;
     *     &lt;include-navigation&gt;true&lt;/include-navigation&gt;
     *     &lt;page-number&gt;0&lt;/page-number&gt;
     *     &lt;page-size&gt;10&lt;/page-size&gt;
     *     &lt;parameters&gt;
     *         &lt;parameter key="query"&gt;Mini mouse&lt;/entry&gt;
     *         &lt;parameter key="query"&gt;red&lt;/entry&gt;
     *         &lt;parameter key="price"&gt;EUR-_-500-_-1000&lt;/entry&gt;
     *     &lt;/parameters&gt;
     *     &lt;sort-descending&gt;false&lt;/sort-descending&gt;
     * &lt;/search&gt;
     * </pre></code>
     *     </td></tr>
     * </table>
     * <p>
     * Note that if category is not specified and/or includeNavigation is false no filtered navigation will be generated in response. This is useful for simple searches such as "search suggest".
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object SearchResultRO</td><td>
     * <pre><code>
     *
     * {
     *   "search" : {
     *     "category" : "mice",
     *     "pageSize" : 10,
     *     "pageNumber" : 0,
     *     "sortDescending" : false,
     *     "includeNavigation" : true,
     *     "sortField" : null,
     *     "parameters" : {
     *       "query" : [
     *         "Mini mouse red"
     *       ]
     *     }
     *   },
     *   "pageAvailableSize" : [
     *     "10",
     *     "20",
     *     "30"
     *   ],
     *   "pageAvailableSort" : {
     *     "byName" : "displayName_sorten",
     *     "bySKU" : "sku.code",
     *     "byPrice" : "facet_price_10_EUR"
     *   },
     *   "products" : [
     *     {
     *       "maxOrderQuantity" : null,
     *       "minOrderQuantity" : null,
     *       "displayDescription" : {
     *         "uk" : "Logitech Wireless Mini Mouse ...",
     *         "ru" : "Logitech Wireless Mini Mouse ...",
     *         "en" : "Logitech Wireless Mini Mouse ..."
     *       },
     *       "code" : "910-002",
     *       "availability" : 1,
     *       "displayName" : {
     *         "uk" : "Mini Mouse M187",
     *         "ru" : "Mini Mouse M187",
     *         "en" : "Mini Mouse M187"
     *       },
     *       "defaultSkuCode" : "910-002-742",
     *       "name" : "Mini Mouse M187",
     *       "stepOrderQuantity" : null,
     *       "availablefrom" : null,
     *       "id" : 297,
     *       "availableto" : null,
     *       "manufacturerCode" : "M001",
     *       "skus" : [
     *         {
     *           "displayName" : {
     *             "uk" : "M187 Червоний",
     *             "ru" : "M187 Красный",
     *             "en" : "M187 Red"
     *           },
     *           "id" : 306,
     *           "productId" : 297,
     *           "code" : "910-002-742",
     *           "manufacturerCode" : null,
     *           "defaultImage" : "Logitech-M187_910-002-742_a.png",
     *           "name" : "M187 Red"
     *         },
     *         {
     *           "displayName" : {
     *             "uk" : "Mini Mouse M187 Чорний",
     *             "ru" : "Mini Mouse M187 Черный",
     *             "en" : "M187 Black"
     *           },
     *           "id" : 305,
     *           "productId" : 297,
     *           "code" : "910-002-741",
     *           "manufacturerCode" : "M001-B",
     *           "defaultImage" : "Logitech-M187_910-002-741_a.png",
     *           "name" : "M187 Black"
     *         },
     *         ...
     *       ],
     *       "productAvailabilityModel" : {
     *         "firstAvailableSkuCode" : "910-002-742",
     *         "available" : true,
     *         "defaultSkuCode" : "910-002-742",
     *         "inStock" : true,
     *         "skuCodes" : [
     *           "910-002-741",
     *           "910-002-742",
     *           "910-002-743",
     *           "910-002-744"
     *         ],
     *         "perpetual" : false,
     *         "availableToSellQuantity" : {
     *           "910-002-742" : 300,
     *           "910-002-744" : 300,
     *           "910-002-741" : 300,
     *           "910-002-743" : 300
     *         }
     *       },
     *       "price" : {
     *                     "quantity" : 1,
     *                     "symbolPosition" : "before",
     *                     "taxInfoUseNet" : true,
     *                     "discount" : null,
     *                     "symbol" : "€",
     *                     "taxInfoShowAmount" : true,
     *                     "priceTaxCode" : "VAT",
     *                     "salePrice" : null,
     *                     "priceTax" : 64.6,
     *                     "taxInfoEnabled" : true,
     *                     "regularPrice" : 322.96,
     *                     "priceTaxExclusive" : false,
     *                     "priceTaxRate" : 20,
     *                     "currency" : "EUR"
     *                },
     *       "featured" : false,
     *       "defaultImage" : "Logitech-M187_910-002-742_a.png",
     *       "multisku" : true,
     *       "description" : "Logitech Wireless Mini Mouse ..."
     *     },
     *     ...
     *   ],
     *   "filteredNavigation" : {
     *     "fnAttributes" : [
     *       {
     *         "displayName" : null,
     *         "fnValues" : [
     *           {
     *             "count" : 1,
     *             "displayValue" : "Logitech",
     *             "value" : "Logitech"
     *           },
     *           {
     *             "count" : 2,
     *             "displayValue" : "Trust",
     *             "value" : "Trust"
     *           }
     *         ],
     *         "code" : "brand",
     *         "rank" : 0,
     *         "navigationType" : "S",
     *         "name" : "brand"
     *       },
     *       {
     *         "displayName" : null,
     *         "fnValues" : [
     *           {
     *             "count" : 1,
     *             "displayValue" : "€ 200 ... € 500",
     *             "value" : "EUR-_-200-_-500"
     *           },
     *           {
     *             "count" : 1,
     *             "displayValue" : "€ 500 ... € 1000",
     *             "value" : "EUR-_-500-_-1000"
     *           }
     *         ],
     *         "code" : "price",
     *         "rank" : 0,
     *         "navigationType" : "R",
     *         "name" : "price"
     *       }
     *     ]
     *   },
     *   "totalResults" : 3,
     *   "productImageWidth" : "280",
     *   "productImageHeight" : "280"
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object SearchResultRO</td><td>
     * <pre><code>
     * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
     * &lt;search-result&gt;
     *     &lt;filtered-navigation&gt;
     *         &lt;fn-attribute&gt;
     *             &lt;code&gt;brand&lt;/code&gt;
     *             &lt;fn-values&gt;
     *                 &lt;fn-value&gt;
     *                     &lt;count&gt;1&lt;/count&gt;
     *                     &lt;display-value&gt;Logitech&lt;/display-value&gt;
     *                     &lt;value&gt;Logitech&lt;/value&gt;
     *                 &lt;/fn-value&gt;
     *                 &lt;fn-value&gt;
     *                     &lt;count&gt;2&lt;/count&gt;
     *                     &lt;display-value&gt;Trust&lt;/display-value&gt;
     *                     &lt;value&gt;Trust&lt;/value&gt;
     *                 &lt;/fn-value&gt;
     *             &lt;/fn-values&gt;
     *             &lt;name&gt;brand&lt;/name&gt;
     *             &lt;navigation-type&gt;S&lt;/navigation-type&gt;
     *             &lt;rank&gt;0&lt;/rank&gt;
     *         &lt;/fn-attribute&gt;
     *         &lt;fn-attribute&gt;
     *             &lt;code&gt;price&lt;/code&gt;
     *             &lt;fn-values&gt;
     *                 &lt;fn-value&gt;
     *                     &lt;count&gt;1&lt;/count&gt;
     *                     &lt;display-value&gt;€ 200 ... € 500&lt;/display-value&gt;
     *                     &lt;value&gt;EUR-_-200-_-500&lt;/value&gt;
     *                 &lt;/fn-value&gt;
     *                 &lt;fn-value&gt;
     *                     &lt;count&gt;1&lt;/count&gt;
     *                     &lt;display-value&gt;€ 500 ... € 1000&lt;/display-value&gt;
     *                     &lt;value&gt;EUR-_-500-_-1000&lt;/value&gt;
     *                 &lt;/fn-value&gt;
     *             &lt;/fn-values&gt;
     *             &lt;name&gt;price&lt;/name&gt;
     *             &lt;navigation-type&gt;R&lt;/navigation-type&gt;
     *             &lt;rank&gt;0&lt;/rank&gt;
     *         &lt;/fn-attribute&gt;
     *     &lt;/filtered-navigation&gt;
     *     &lt;page-available-sizes&gt;
     *         &lt;page-available-size&gt;10&lt;/page-available-size&gt;
     *         &lt;page-available-size&gt;20&lt;/page-available-size&gt;
     *         &lt;page-available-size&gt;30&lt;/page-available-size&gt;
     *     &lt;/page-available-sizes&gt;
     *     &lt;page-available-sort&gt;
     *         &lt;entry key="byName"&gt;displayName_sorten&lt;/entry&gt;
     *         &lt;entry key="bySKU"&gt;sku.code&lt;/entry&gt;
     *         &lt;entry key="byPrice"&gt;facet_price_10_EUR&lt;/entry&gt;
     *     &lt;/page-available-sort&gt;
     *     &lt;product-image-height&gt;280&lt;/product-image-height&gt;
     *     &lt;product-image-width&gt;280&lt;/product-image-width&gt;
     *     &lt;products&gt;
     *         &lt;availability&gt;1&lt;/availability&gt;
     *         &lt;code&gt;910-002&lt;/code&gt;
     *         &lt;default-image&gt;Logitech-M187_910-002-742_a.png&lt;/default-image&gt;
     *         &lt;default-sku-code&gt;910-002-742&lt;/default-sku-code&gt;
     *         &lt;description&gt;Logitech Wireless Mini Mouse ...&lt;/description&gt;
     *         &lt;display-descriptions&gt;
     *             &lt;entry lang="uk"&gt;Logitech Wireless Mini Mouse ...&lt;/entry&gt;
     *             &lt;entry lang="en"&gt;Logitech Wireless Mini Mouse ...&lt;/entry&gt;
     *             &lt;entry lang="ru"&gt;Logitech Wireless Mini Mouse ...&lt;/entry&gt;
     *         &lt;/display-descriptions&gt;
     *         &lt;display-names&gt;
     *             &lt;entry lang="uk"&gt;Mini Mouse M187&lt;/entry&gt;
     *             &lt;entry lang="en"&gt;Mini Mouse M187&lt;/entry&gt;
     *             &lt;entry lang="ru"&gt;Mini Mouse M187&lt;/entry&gt;
     *         &lt;/display-names&gt;
     *         &lt;featured&gt;false&lt;/featured&gt;
     *         &lt;id&gt;297&lt;/id&gt;
     *         &lt;manufacturer-code&gt;M001&lt;/manufacturer-code&gt;
     *         &lt;multisku&gt;true&lt;/multisku&gt;
     *         &lt;name&gt;Mini Mouse M187&lt;/name&gt;
     *         &lt;price&gt;
     *             &lt;currency&gt;EUR&lt;/currency&gt;
     *             &lt;quantity&gt;1.00&lt;/quantity&gt;
     *             &lt;regular-price&gt;387.56&lt;/regular-price&gt;
     *             &lt;symbol&gt;€&lt;/symbol&gt;
     *             &lt;symbol-position&gt;before&lt;/symbol-position&gt;
     *         &lt;/price&gt;
     *         &lt;product-availability&gt;
     *             &lt;available&gt;true&lt;/available&gt;
     *             &lt;ats-quantity&gt;
     *                 &lt;entry sku="910-002-742"&gt;300.000&lt;/entry&gt;
     *                 &lt;entry sku="910-002-741"&gt;300.000&lt;/entry&gt;
     *                 &lt;entry sku="910-002-744"&gt;300.000&lt;/entry&gt;
     *                 &lt;entry sku="910-002-743"&gt;300.000&lt;/entry&gt;
     *             &lt;/ats-quantity&gt;
     *             &lt;default-sku&gt;910-002-742&lt;/default-sku&gt;
     *             &lt;first-available-sku&gt;910-002-742&lt;/first-available-sku&gt;
     *             &lt;in-stock&gt;true&lt;/in-stock&gt;
     *             &lt;perpetual&gt;false&lt;/perpetual&gt;
     *             &lt;sku-codes&gt;910-002-741&lt;/sku-codes&gt;
     *             &lt;sku-codes&gt;910-002-742&lt;/sku-codes&gt;
     *             &lt;sku-codes&gt;910-002-743&lt;/sku-codes&gt;
     *             &lt;sku-codes&gt;910-002-744&lt;/sku-codes&gt;
     *         &lt;/product-availability&gt;
     *         &lt;skus&gt;
     *             &lt;sku&gt;
     *                 &lt;code&gt;910-002-742&lt;/code&gt;
     *                 &lt;default-image&gt;Logitech-M187_910-002-742_a.png&lt;/default-image&gt;
     *                 &lt;display-names&gt;
     *                     &lt;entry lang="uk"&gt;M187 Червоний&lt;/entry&gt;
     *                     &lt;entry lang="en"&gt;M187 Red&lt;/entry&gt;
     *                     &lt;entry lang="ru"&gt;M187 Красный&lt;/entry&gt;
     *                 &lt;/display-names&gt;
     *                 &lt;id&gt;306&lt;/id&gt;
     *                 &lt;name&gt;M187 Red&lt;/name&gt;
     *                 &lt;product-id&gt;297&lt;/product-id&gt;
     *             &lt;/sku&gt;
     *             &lt;sku&gt;
     *                 &lt;code&gt;910-002-741&lt;/code&gt;
     *                 &lt;default-image&gt;Logitech-M187_910-002-741_a.png&lt;/default-image&gt;
     *                 &lt;display-names&gt;
     *                     &lt;entry lang="uk"&gt;Mini Mouse M187 Чорний&lt;/entry&gt;
     *                     &lt;entry lang="en"&gt;M187 Black&lt;/entry&gt;
     *                     &lt;entry lang="ru"&gt;Mini Mouse M187 Черный&lt;/entry&gt;
     *                 &lt;/display-names&gt;
     *                 &lt;id&gt;305&lt;/id&gt;
     *                 &lt;manufacturer-code&gt;M001-B&lt;/manufacturer-code&gt;
     *                 &lt;name&gt;M187 Black&lt;/name&gt;
     *                 &lt;product-id&gt;297&lt;/product-id&gt;
     *             &lt;/sku&gt;
     *             ...
     *         &lt;/skus&gt;
     *     &lt;/products&gt;
     *     ...
     *     &lt;search&gt;
     *         &lt;category&gt;mice&lt;/category&gt;
     *         &lt;include-navigation&gt;true&lt;/include-navigation&gt;
     *         &lt;page-number&gt;0&lt;/page-number&gt;
     *         &lt;page-size&gt;10&lt;/page-size&gt;
     *         &lt;parameters&gt;
     *             &lt;parameter key="query"&gt;Mini mouse red&lt;/entry&gt;
     *         &lt;/parameters&gt;
     *         &lt;sort-descending&gt;false&lt;/sort-descending&gt;
     *     &lt;/search&gt;
     *     &lt;total-results&gt;3&lt;/total-results&gt;
     * &lt;/search-result&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     *
     * @param search search request object
     * @param request request
     * @param response response
     *
     * @return category object
     */
    @RequestMapping(
            value = "",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes =  { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody SearchResultRO search(final @RequestBody SearchRO search,
                                               final HttpServletRequest request,
                                               final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        final long categoryId = bookmarkMixin.resolveCategoryId(search.getCategory());
        final ShoppingCart cart = cartMixin.getCurrentCart();
        final long browsingShopId = cart.getShoppingContext().getCustomerShopId();

        final SearchResultRO result = new SearchResultRO();
        result.setSearch(search);

        final Pair<String, String> templates = resolveTemplate(categoryId);
        if (templates != null) {
            result.setUitemplate(templates.getFirst());
            result.setUitemplateFallback(templates.getSecond());
        }


        final NavigationContext context = createNavigationContext(categoryId, browsingShopId, result);

        configureResultViewOptions(categoryId, browsingShopId, cart.getCurrentLocale(), cart.getCurrencyCode(), result);

        populateSearchResults(context, result, cart);

        if (!context.isGlobal() && search.getIncludeNavigation()) {

            populateFilteredNavigation(
                    categoryId,
                    browsingShopId,
                    cart.getCurrentLocale(),
                    cart.getCurrencyCode(),
                    context,
                    result
            );

        }

        return result;

    }


    private Pair<String, String> resolveTemplate(final long categoryId) {
        final Map params = new HashMap();
        params.put(WebParametersKeys.QUERY, WebParametersKeys.QUERY);
        if (categoryId > 0L) {
            params.put(WebParametersKeys.CATEGORY_ID, String.valueOf(categoryId));
        }
        return centralViewResolver.resolveMainPanelRendererLabel(params);
    }


    private void populateFilteredNavigation(final long categoryId,
                                            final long shopId,
                                            final String locale,
                                            final String currencyCode,
                                            final NavigationContext context,
                                            final SearchResultRO result) {

        final Category category = categoryServiceFacade.getCategory(categoryId, shopId);

        if (category != null) {


            final FilteredNavigationRO navigationRo = new FilteredNavigationRO();

            final boolean byBrand = category.getNavigationByBrand() == null ? false : category.getNavigationByBrand();

            if (byBrand && !context.isFilteredBy(ProductSearchQueryBuilder.BRAND_FIELD)) {

                populateFilteredNavigationRecords(navigationRo,
                        brandsFilteredNavigationSupport.getFilteredNavigationRecords(context, locale, ProductSearchQueryBuilder.BRAND_FIELD));

                enhanceFilteredNavigationByBrand(navigationRo);

            }

            if (!context.isFilteredBy(ProductSearchQueryBuilder.PRODUCT_PRICE)) {

                populateFilteredNavigationRecords(navigationRo,
                        priceFilteredNavigationSupport.getFilteredNavigationRecords(context, categoryId, currencyCode, locale, ProductSearchQueryBuilder.PRODUCT_PRICE));

                enhanceFilteredNavigationByPrice(navigationRo);

            }

            final boolean byAttr = category.getNavigationByAttributes() == null ? false : category.getNavigationByAttributes();
            final Long productType = categoryServiceFacade.getCategoryProductTypeId(category.getCategoryId(), shopId);

            if (byAttr && productType != null) {

                populateFilteredNavigationRecords(navigationRo,
                        attributeFilteredNavigationSupport.getFilteredNavigationRecords(context, locale, productType));

            }

            result.setFilteredNavigation(navigationRo);

        }

    }

    private void enhanceFilteredNavigationByPrice(final FilteredNavigationRO navigationRo) {

        FilteredNavigationAttributeRO price = null;
        for (final FilteredNavigationAttributeRO attributeRo : navigationRo.getFnAttributes()) {
            if (ProductSearchQueryBuilder.PRODUCT_PRICE.equals(attributeRo.getCode())) {
                price = attributeRo;
                break;
            }
        }

        if (price != null) {

            if (StringUtils.isBlank(price.getNavigationType())) {
                price.setNavigationType(ProductTypeAttr.NAVIGATION_TYPE_RANGE);
            }

            for (final FilteredNavigationAttributeValueRO valueRo : price.getFnValues()) {

                if (StringUtils.isBlank(valueRo.getDisplayValue())) {

                    Pair<String, Pair<BigDecimal, BigDecimal>> pair = priceNavigation.decomposePriceRequestParams(valueRo.getValue());
                    Pair<String, Boolean> symbol = currencySymbolService.getCurrencySymbol(pair.getFirst());
                    final StringBuilder displayPrice = new StringBuilder();
                    if (symbol.getSecond()) {
                        displayPrice.append(pair.getSecond().getFirst().toPlainString()).append(' ').append(symbol.getFirst());
                        displayPrice.append(" ... ");
                        displayPrice.append(pair.getSecond().getSecond().toPlainString()).append(' ').append(symbol.getFirst());
                    } else {
                        displayPrice.append(symbol.getFirst()).append(' ').append(pair.getSecond().getFirst().toPlainString());
                        displayPrice.append(" ... ");
                        displayPrice.append(symbol.getFirst()).append(' ').append(pair.getSecond().getSecond().toPlainString());
                    }

                    valueRo.setDisplayValue(displayPrice.toString());

                }

            }

        }
    }

    private void enhanceFilteredNavigationByBrand(final FilteredNavigationRO navigationRo) {

        FilteredNavigationAttributeRO brand = null;
        for (final FilteredNavigationAttributeRO attributeRo : navigationRo.getFnAttributes()) {
            if (ProductSearchQueryBuilder.BRAND_FIELD.equals(attributeRo.getCode())) {
                brand = attributeRo;
                break;
            }
        }

        if (brand != null) {

            if (StringUtils.isBlank(brand.getNavigationType())) {
                brand.setNavigationType(ProductTypeAttr.NAVIGATION_TYPE_SINGLE);
            }

            for (final FilteredNavigationAttributeValueRO valueRo : brand.getFnValues()) {

                if (StringUtils.isBlank(valueRo.getDisplayValue())) {

                    valueRo.setDisplayValue(valueRo.getValue());

                }

            }

        }
    }

    private void populateFilteredNavigationRecords(final FilteredNavigationRO navigationRo,
                                                   final List<FilteredNavigationRecord> records) {

        if (CollectionUtils.isNotEmpty(records)) {

            String head = StringUtils.EMPTY;

            FilteredNavigationAttributeRO attributeRo = null;

            for (final FilteredNavigationRecord record : records) {

                if (!record.getName().equalsIgnoreCase(head)) {

                    if (attributeRo != null && !attributeRo.getFnValues().isEmpty()) {
                        // Add previous one
                        navigationRo.getFnAttributes().add(attributeRo);

                    }

                    attributeRo = new FilteredNavigationAttributeRO();
                    attributeRo.setCode(record.getCode());
                    attributeRo.setName(record.getName());
                    attributeRo.setDisplayName(record.getDisplayName());
                    attributeRo.setRank(record.getRank());
                    attributeRo.setNavigationType(record.getType());
                    head = record.getName();

                }

                if (record.getCount() > 0) {

                    final FilteredNavigationAttributeValueRO valueRo = new FilteredNavigationAttributeValueRO();

                    valueRo.setValue(record.getValue());
                    valueRo.setDisplayValue(record.getDisplayValue());
                    valueRo.setCount(record.getCount());

                    attributeRo.getFnValues().add(valueRo);

                }

            }

            if (!attributeRo.getFnValues().isEmpty()) {
                // Add last one
                navigationRo.getFnAttributes().add(attributeRo);

            }

        }
    }

    private void populateSearchResults(final NavigationContext context,
                                       final SearchResultRO result,
                                       final ShoppingCart cart) {

        ProductSearchResultPageDTO products = productServiceFacade.getListProducts(
                context, result.getSearch().getPageNumber() * result.getSearch().getPageSize(), result.getSearch().getPageSize(),
                result.getSearch().getSortField(), result.getSearch().getSortDescending());

        result.setTotalResults(products.getTotalHits());

        final List<ProductSearchResultRO> ros = new ArrayList<ProductSearchResultRO>();
        if (CollectionUtils.isNotEmpty(products.getResults())) {

            final Pair<String, Boolean> symbol = currencySymbolService.getCurrencySymbol(cart.getCurrencyCode());

            for (final ProductSearchResultDTO hit : products.getResults()) {

                final ProductAvailabilityModel skuPam = productServiceFacade.getProductAvailability(hit, context.getShopId());

                final ProductSearchResultRO ro = mappingMixin.map(hit, ProductSearchResultRO.class, ProductSearchResultDTO.class);

                final ProductAvailabilityModelRO amRo = mappingMixin.map(skuPam, ProductAvailabilityModelRO.class, ProductAvailabilityModel.class);
                ro.setProductAvailabilityModel(amRo);

                final ProductPriceModel price = productServiceFacade.getSkuPrice(
                        cart,
                        null,
                        skuPam.getFirstAvailableSkuCode(),
                        BigDecimal.ONE
                );

                final SkuPriceRO priceRo = mappingMixin.map(price, SkuPriceRO.class, ProductPriceModel.class);
                priceRo.setSymbol(symbol.getFirst());
                priceRo.setSymbolPosition(symbol.getSecond() != null && symbol.getSecond() ? "after" : "before");

                ro.setPrice(priceRo);

                ros.add(ro);

            }
        }
        result.setProducts(ros);

    }

    private void configureResultViewOptions(final long categoryId,
                                            final long shopId,
                                            final String locale,
                                            final String currency,
                                            final SearchResultRO result) {

        final List<String> itemsPerPageValues = categoryServiceFacade.getItemsPerPageOptionsConfig(categoryId, shopId);
        final int selectedItemPerPage;
        if (itemsPerPageValues.contains(String.valueOf(result.getSearch().getPageSize()))) {
            selectedItemPerPage = result.getSearch().getPageSize();
        } else {
            selectedItemPerPage = NumberUtils.toInt(itemsPerPageValues.get(0), 10);
        }

        final List<String> pageSortingValues = categoryServiceFacade.getPageSortingOptionsConfig(categoryId, shopId);
        final Map<String, String> sortPageValues = new LinkedHashMap<String, String>();
        for (final String pageSortingValue : pageSortingValues) {
            final ProductSortingUtils.SupportedSorting sorting = ProductSortingUtils.getConfiguration(pageSortingValue);
            if (sorting != null) {
                sortPageValues.put(
                        sorting.resolveLabelKey(shopId, locale, currency),
                        sorting.resolveSortField(shopId, locale, currency)
                );
            }
        }

        final Pair<String, String> widthHeight = categoryServiceFacade.getProductListImageSizeConfig(categoryId, shopId);

        result.setPageAvailableSize(itemsPerPageValues);
        result.setPageAvailableSort(sortPageValues);
        if (result.getSearch().getPageNumber() < 0) {
            result.getSearch().setPageNumber(0); // do not allow negative start page
        }
        result.getSearch().setPageSize(selectedItemPerPage);
        result.setProductImageWidth(widthHeight.getFirst());
        result.setProductImageHeight(widthHeight.getSecond());

    }

    private NavigationContext createNavigationContext(final long categoryId,
                                                      final long shopId,
                                                      final SearchResultRO result) {

        final Pair<List<Long>, Boolean> currentCategoriesIds = categoryServiceFacade.getSearchCategoriesIds(categoryId, shopId);

        final Map<String, List> mapParams = new HashMap<String, List>();

        if (result.getSearch().getParameters() != null) {
            mapParams.putAll(result.getSearch().getParameters());
        }

        return luceneQueryFactory.getFilteredNavigationQueryChain(
                shopId,
                currentCategoriesIds.getFirst(),
                currentCategoriesIds.getSecond(),
                mapParams
        );

    }

}

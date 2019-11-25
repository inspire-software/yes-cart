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

import io.swagger.annotations.Api;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.domain.ro.CustomerRO;
import org.yes.cart.domain.ro.SearchRO;
import org.yes.cart.domain.ro.SearchResultCustomerRO;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.service.rest.impl.CartMixin;
import org.yes.cart.web.service.rest.impl.RoMappingMixin;
import org.yes.cart.web.support.service.ManagerServiceFacade;
import org.yes.cart.web.support.utils.CustomerSortingUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * User: denispavlov
 * Date: 27/10/2019
 * Time: 10:35
 */
@Controller
@Api(value = "Manager", tags = "manager")
@RequestMapping("/management")
public class ManagerController {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerController.class);

    @Autowired
    private ManagerServiceFacade managerServiceFacade;

    @Autowired
    private CartMixin cartMixin;
    @Autowired
    @Qualifier("restRoMappingMixin")
    private RoMappingMixin mappingMixin;


    @RequestMapping(
            value = "customers/search",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes =  { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody
    SearchResultCustomerRO search(final @RequestHeader(value = "yc", required = false) String requestToken,
                                  final @RequestBody SearchRO search,
                                  final HttpServletRequest request,
                                  final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final long shopId = cart.getShoppingContext().getShopId();
        final long browsingShopId = cart.getShoppingContext().getCustomerShopId();
        final String locale = cart.getCurrentLocale();

        final SearchResultCustomerRO result = new SearchResultCustomerRO();
        result.setSearch(search);

        configureResultViewOptions(browsingShopId, cart.getCurrentLocale(), cart.getCurrencyCode(), result);

        populateSearchResults(search, result, cart);

        search.setIncludeNavigation(false);

        return result;

    }

    private void populateSearchResults(final SearchRO search, final SearchResultCustomerRO result, final ShoppingCart cart) {

        final Map<String, List> all = new HashMap<>(search.getParameters());
        all.put("shopId", Collections.singletonList(String.valueOf(cart.getShoppingContext().getCustomerShopId())));

        final SearchContext searchContext = new SearchContext(
                all,
                search.getPageNumber(),
                search.getPageSize(),
                search.getSortField(),
                search.getSortDescending(),
                "any", "shopId", "email", "firstname", "lastname", "companyName1", "companyName2", "tag"
        );

        final SearchResult<Customer> results = managerServiceFacade.getCustomers(searchContext);

        final List<CustomerRO> customerROs = new ArrayList<>();
        for (final Customer customer : results.getItems()) {
            final CustomerRO ro = mappingMixin.map(customer, CustomerRO.class, Customer.class);
            customerROs.add(ro);
        }

        result.setItems(customerROs);
        result.setTotalResults(results.getTotal());

    }


    private void configureResultViewOptions(final long browsingShopId,
                                            final String locale,
                                            final String currency,
                                            final SearchResultCustomerRO result) {

        final List<String> itemsPerPageValues = managerServiceFacade.getItemsPerPageOptionsConfig(browsingShopId);
        final int selectedItemPerPage;
        if (itemsPerPageValues.contains(String.valueOf(result.getSearch().getPageSize()))) {
            selectedItemPerPage = result.getSearch().getPageSize();
        } else {
            selectedItemPerPage = NumberUtils.toInt(itemsPerPageValues.get(0), 10);
        }

        final List<String> pageSortingValues = managerServiceFacade.getPageSortingOptionsConfig(browsingShopId);
        final Map<String, String> sortPageValues = new LinkedHashMap<>();
        for (final String pageSortingValue : pageSortingValues) {
            final CustomerSortingUtils.SupportedSorting sorting = CustomerSortingUtils.getConfiguration(pageSortingValue);
            if (sorting != null) {
                sortPageValues.put(
                        sorting.resolveLabelKey(browsingShopId, locale, currency),
                        sorting.resolveSortField(browsingShopId, locale, currency)
                );
            }
        }
        if (result.getSearch().getSortField() != null && !sortPageValues.values().contains(result.getSearch().getSortField())) {
            result.getSearch().setSortField(null);
        }

        final Pair<String, String> widthHeight = managerServiceFacade.getCustomerListImageSizeConfig(browsingShopId);

        result.setPageAvailableSize(itemsPerPageValues);
        result.setPageAvailableSort(sortPageValues);
        if (result.getSearch().getPageNumber() < 0) {
            result.getSearch().setPageNumber(0); // do not allow negative start page
        }
        result.getSearch().setPageSize(selectedItemPerPage);
        result.setItemImageWidth(widthHeight.getFirst());
        result.setItemImageHeight(widthHeight.getSecond());

    }

}

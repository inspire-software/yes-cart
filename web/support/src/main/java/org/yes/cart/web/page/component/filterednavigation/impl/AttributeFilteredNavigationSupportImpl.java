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

package org.yes.cart.web.page.component.filterednavigation.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.ProductSearchResultNavDTO;
import org.yes.cart.domain.dto.ProductSearchResultNavItemDTO;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.ProductTypeAttr;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.range.DisplayValue;
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.domain.misc.navigation.range.RangeNode;
import org.yes.cart.search.SearchQueryFactory;
import org.yes.cart.search.dto.FilteredNavigationRecord;
import org.yes.cart.search.dto.FilteredNavigationRecordRequest;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.dto.impl.FilteredNavigationRecordImpl;
import org.yes.cart.search.dto.impl.FilteredNavigationRecordRequestImpl;
import org.yes.cart.search.query.impl.SearchUtil;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductTypeAttrService;
import org.yes.cart.util.log.Markers;
import org.yes.cart.web.page.component.filterednavigation.AttributeFilteredNavigationSupport;

import java.util.*;

/**
 * User: denispavlov
 * Date: 13-09-28
 * Time: 12:17 AM
 */
public class AttributeFilteredNavigationSupportImpl extends AbstractFilteredNavigationSupportImpl implements AttributeFilteredNavigationSupport {

    private final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    private static final Comparator<FilteredNavigationRecord> COMPARATOR = new Comparator<FilteredNavigationRecord>() {
        public int compare(final FilteredNavigationRecord record1, final FilteredNavigationRecord record2) {
            int rez = record1.getRank() - record2.getRank();
            if (rez == 0) {
                if (record1.getDisplayName() != null && record2.getDisplayName() != null) {
                    rez = record1.getDisplayName().compareToIgnoreCase(record2.getDisplayName());
                    if (rez != 0) {
                        return rez;
                    }
                }
                rez = record1.getName().compareToIgnoreCase(record2.getName());
                if (rez == 0 && !ProductTypeAttr.NAVIGATION_TYPE_RANGE.equals(record1.getType())) {
                    if (record1.getDisplayValue() != null && record2.getDisplayValue() != null) {
                        rez = record1.getDisplayValue().compareToIgnoreCase(record2.getDisplayValue());
                    } else {
                        rez = record1.getValue().compareToIgnoreCase(record2.getValue());
                    }
                }
            }
            return rez;
        }
    };

    private final ProductTypeAttrService productTypeAttrService;

    public AttributeFilteredNavigationSupportImpl(final SearchQueryFactory searchQueryFactory,
                                                  final ProductService productService,
                                                  final ProductTypeAttrService productTypeAttrService) {
        super(searchQueryFactory, productService);
        this.productTypeAttrService = productTypeAttrService;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "filteredNavigationSupport-attributeFilteredNavigationRecords")
    public List<FilteredNavigationRecord> getFilteredNavigationRecords(final NavigationContext navigationContext,
                                                                       final String locale,
                                                                       final long productTypeId) {

        final List<FilteredNavigationRecord> navigationList = new ArrayList<FilteredNavigationRecord>();

        if (productTypeId > 0L) {

            final List<ProductTypeAttr> ptas = productTypeAttrService.getNavigatableByProductTypeId(productTypeId);

            if (ptas.isEmpty()) {
                return Collections.emptyList();
            }

            final List<FilteredNavigationRecordRequest> requests = new ArrayList<FilteredNavigationRecordRequest>();
            final Map<String, FilteredNavigationRecordRequest> requestsMap = new HashMap<String, FilteredNavigationRecordRequest>();
            final Map<String, ProductTypeAttr> pTypes = new HashMap<String, ProductTypeAttr>();
            for (final ProductTypeAttr pta : ptas) {

                final String facetName = pta.getAttribute().getCode();
                pTypes.put(facetName, pta);

                if (ProductTypeAttr.NAVIGATION_TYPE_RANGE.equals(pta.getNavigationType())) {

                    final String fieldName = "facetr_" + facetName;

                    final List<Pair<String, String>> rangeValues = new ArrayList<Pair<String, String>>();
                    final RangeList rangeList = pta.getRangeList();
                    if (rangeList != null && rangeList.getRanges() != null) {
                        for (RangeNode node : rangeList.getRanges()) {

                            final Long from  = SearchUtil.valToLong(node.getFrom(), Constants.NUMERIC_NAVIGATION_PRECISION);
                            final Long to  = SearchUtil.valToLong(node.getTo(), Constants.NUMERIC_NAVIGATION_PRECISION);

                            if (from == null || to == null) {
                                LOGFTQ.error(Markers.alert(), "Invalid range configuration for {}", fieldName);
                                continue;
                            }

                            rangeValues.add(new Pair<String, String>(
                                    from.toString(),
                                    to.toString()
                            ));

                        }
                    }

                    if (!rangeValues.isEmpty()) {

                        final FilteredNavigationRecordRequest rangeSingleValue = new FilteredNavigationRecordRequestImpl(facetName, fieldName, rangeValues);
                        requests.add(rangeSingleValue);
                        requestsMap.put(facetName, rangeSingleValue);

                    }

                } else {

                    final String fieldName = "facet_" + facetName;

                    final boolean multi = ProductTypeAttr.NAVIGATION_TYPE_MULTI.equals(pta.getNavigationType());
                    final FilteredNavigationRecordRequest singleMultiValue = new FilteredNavigationRecordRequestImpl(facetName, fieldName, multi);
                    requests.add(singleMultiValue);
                    requestsMap.put(facetName, singleMultiValue);

                }
            }

            if (requests.isEmpty()) {
                return Collections.emptyList();
            }


            final ProductSearchResultNavDTO facets =
                    getProductService().findFilteredNavigationRecords(navigationContext, requests);

            appendNavigationRecords(navigationList, requestsMap, pTypes, navigationContext, locale, facets);


            // Alpha sort
            Collections.sort(navigationList, COMPARATOR);

        }

        return navigationList;

    }

    protected void appendNavigationRecords(final List<FilteredNavigationRecord> navigationList,
                                           final Map<String, FilteredNavigationRecordRequest> requestsMap,
                                           final Map<String, ProductTypeAttr> pTypes,
                                           final NavigationContext facetContext,
                                           final String locale,
                                           final ProductSearchResultNavDTO facets) {

        for (final String code : facets.getNavCodes()) {

            if (facetContext.isFilteredBy(code)) {
                continue; // do not show already filtered or blank ones
            }

            final FilteredNavigationRecordRequest request = requestsMap.get(code);
            if (request == null) {
                LOGFTQ.debug("Unable to get filtered navigation request for record: {}", code);
                continue;
            }
            final List<ProductSearchResultNavItemDTO> counts = facets.getItems(code);
            if (counts == null) {
                LOGFTQ.debug("Unable to get filtered navigation counts for record: {}, request: {}", code, request);
                continue;
            }

            final ProductTypeAttr pta = pTypes.get(code);
            final Attribute attribute = pta.getAttribute();

            final String displayName = new StringI18NModel(attribute.getDisplayName()).getValue(locale);

            if (ProductTypeAttr.NAVIGATION_TYPE_RANGE.equals(pta.getNavigationType())) {

                final RangeList rangeList = pta.getRangeList();
                if (rangeList != null && rangeList.getRanges() != null) {
                    for (RangeNode node : rangeList.getRanges()) {

                        final Map<String, String> i18n = getRangeValueDisplayNames(node.getI18n());
                        final String value = getRangeValueRepresentation(node.getFrom(), node.getTo());
                        final String displayValue = getRangeDisplayValueRepresentation(locale, i18n, node.getFrom(), node.getTo());

                        Integer candidateResultCount = null;
                        for (final ProductSearchResultNavItemDTO item : counts) {
                            if (item.getValue().equals(value)) {
                                candidateResultCount = item.getCount();
                                break;
                            }
                        }

                        if (candidateResultCount != null && candidateResultCount > 0) {
                            navigationList.add(new FilteredNavigationRecordImpl(
                                    attribute.getName(),
                                    displayName,
                                    code,
                                    value,
                                    displayValue,
                                    candidateResultCount,
                                    pta.getRank(),
                                    ProductTypeAttr.NAVIGATION_TYPE_RANGE,
                                    pta.getNavigationTemplate()
                            ));
                        }
                    }
                }

            } else {
                for (final ProductSearchResultNavItemDTO item : counts) {

                    final Integer candidateResultCount = item.getCount();
                    if (candidateResultCount != null && candidateResultCount > 0) {
                        navigationList.add(new FilteredNavigationRecordImpl(
                                attribute.getName(),
                                displayName,
                                code,
                                item.getValue(), item.getDisplayValue(locale),
                                item.getCount(),
                                pta.getRank(),
                                pta.getNavigationType(),
                                pta.getNavigationTemplate()
                        ));
                    }
                }
            }

        }
    }

    private String getRangeValueRepresentation(final String from, final String to) {
        return SearchUtil.valToLong(from, Constants.NUMERIC_NAVIGATION_PRECISION) + Constants.RANGE_NAVIGATION_DELIMITER + SearchUtil.valToLong(to, Constants.NUMERIC_NAVIGATION_PRECISION);
    }

    private String getRangeDisplayValueRepresentation(final String locale, final Map<String, String> display, final String from, final String to) {
        final I18NModel toI18n = new StringI18NModel(display);
        final String localName = toI18n.getValue(locale);
        if (StringUtils.isBlank(localName)) {
            return from + " - " + to;
        }
        return localName;
    }

    private Map<String, String> getRangeValueDisplayNames(final List<DisplayValue> displayValues) {

        final Map<String, String> display = new HashMap<String, String>();
        if (displayValues != null) {
            for (final DisplayValue dv :displayValues) {
                display.put(dv.getLang(), dv.getValue());
            }
        }
        return display;
    }


}

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

package org.yes.cart.web.page.component.filterednavigation.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.ProductTypeAttr;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.domain.misc.navigation.range.RangeNode;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.domain.queryobject.FilteredNavigationRecordRequest;
import org.yes.cart.domain.queryobject.NavigationContext;
import org.yes.cart.domain.queryobject.impl.FilteredNavigationRecordRequestImpl;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductTypeAttrService;
import org.yes.cart.web.page.component.filterednavigation.AttributeFilteredNavigationSupport;

import java.util.*;

/**
 * User: denispavlov
 * Date: 13-09-28
 * Time: 12:17 AM
 */
public class AttributeFilteredNavigationSupportImpl extends AbstractFilteredNavigationSupportImpl implements AttributeFilteredNavigationSupport {

    private static final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    private final ProductTypeAttrService productTypeAttrService;

    public AttributeFilteredNavigationSupportImpl(final LuceneQueryFactory luceneQueryFactory,
                                                  final ProductService productService,
                                                  final ProductTypeAttrService productTypeAttrService) {
        super(luceneQueryFactory, productService);
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

        if (!navigationContext.isGlobal()) {

            final List<ProductTypeAttr> ptas = productTypeAttrService.getNavigatableByProductTypeId(productTypeId);

            if (ptas.isEmpty()) {
                return Collections.emptyList();
            }

            final List<FilteredNavigationRecordRequest> requests = new ArrayList<FilteredNavigationRecordRequest>();
            final Map<String, FilteredNavigationRecordRequest> requestsMap = new HashMap<String, FilteredNavigationRecordRequest>();
            for (final ProductTypeAttr pta : ptas) {

                final String facetName = pta.getAttribute().getCode();
                final String fieldName = "facet_" + facetName;

                if (ProductTypeAttr.NAVIGATION_TYPE_SINGLE.equals(pta.getNavigationType())) {

                    final FilteredNavigationRecordRequest singleMultiValue = new FilteredNavigationRecordRequestImpl(facetName, fieldName, true);
                    requests.add(singleMultiValue);
                    requestsMap.put(facetName, singleMultiValue);

                } else {

                    final List<Pair<String, String>> rangeValues = new ArrayList<Pair<String, String>>();
                    final RangeList rangeList = pta.getRangeList();
                    if (rangeList != null && rangeList.getRanges() != null) {
                        for (RangeNode node : rangeList.getRanges()) {

                            rangeValues.add(new Pair<String, String>(node.getFrom(), node.getTo()));

                        }
                    }

                    if (!rangeValues.isEmpty()) {

                        final FilteredNavigationRecordRequest rangeSingleValue = new FilteredNavigationRecordRequestImpl(facetName, facetName, rangeValues);
                        requests.add(rangeSingleValue);
                        requestsMap.put(facetName, rangeSingleValue);

                    }

                }
            }

            if (requests.isEmpty()) {
                return Collections.emptyList();
            }


            final List<FilteredNavigationRecord> allNavigationRecordsTemplates =
                    getProductService().getDistinctAttributeValues(locale, productTypeId);
            final Map<String, List<Pair<String, Integer>>> facets =
                    getProductService().findFilteredNavigationRecords(navigationContext.getProductQuery(), requests);

            for (final FilteredNavigationRecord recordTemplate : allNavigationRecordsTemplates) {

                if (navigationContext.isFilteredBy(recordTemplate.getCode()) || StringUtils.isBlank(recordTemplate.getValue())) {
                    continue; // do not show already filtered or blank ones
                }

                final FilteredNavigationRecordRequest request = requestsMap.get(recordTemplate.getCode());
                if (request == null) {
                    LOGFTQ.warn("Unable to get filtered navigation request for record: {}", recordTemplate);
                    continue;
                }
                final List<Pair<String, Integer>> counts = facets.get(recordTemplate.getCode());
                if (counts == null) {
                    LOGFTQ.warn("Unable to get filtered navigation counts for record: {}, request: {}", recordTemplate, request);
                    continue;
                }


                final Iterator<Pair<String, Integer>> countIt = counts.iterator();
                while (countIt.hasNext()) {

                    final Pair<String, Integer> count = countIt.next();

                    if (request.isRangeValue()) {

                        // range record value has the following form "from_value-_-to_value"
                        // range count value has the following form "[from_value, to_value)"

                        final String[] rangeValues = StringUtils.splitByWholeSeparator(count.getFirst().substring(1, count.getFirst().length() - 1), ", ");
                        if (rangeValues.length != 2) {
                            LOGFTQ.error("Unable to parse count range: {}", count.getFirst());
                            countIt.remove();
                            continue;
                        }
                        final String compareValue = rangeValues[0] + Constants.RANGE_NAVIGATION_DELIMITER + rangeValues[1];

                        if (recordTemplate.getValue().equals(compareValue)) { // range value match

                            final Integer candidateResultCount = count.getSecond();

                            if (candidateResultCount != null && candidateResultCount > 0) {
                                final FilteredNavigationRecord record = recordTemplate.clone();
                                record.setCount(candidateResultCount);
                                navigationList.add(record);
                            }

                            countIt.remove();
                            break;
                        }

                    } else if (recordTemplate.getValue().equals(count.getFirst())) { // single value match

                        final Integer candidateResultCount = count.getSecond();

                        if (candidateResultCount != null && candidateResultCount > 0) {
                            final FilteredNavigationRecord record = recordTemplate.clone();
                            record.setCount(candidateResultCount);
                            navigationList.add(record);
                        }

                        countIt.remove();
                        break;
                    }

                }

            }
        }

        return navigationList;

    }

}

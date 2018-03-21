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

package org.yes.cart.search.dao.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.*;
import org.apache.lucene.facet.range.LongRange;
import org.apache.lucene.facet.range.LongRangeFacetCounts;
import org.apache.lucene.facet.sortedset.DefaultSortedSetDocValuesReaderState;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetCounts;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.GenericFTS;
import org.yes.cart.search.dao.LuceneIndexProvider;
import org.yes.cart.search.dto.FilteredNavigationRecordRequest;
import org.yes.cart.search.query.impl.AsIsAnalyzer;
import org.yes.cart.util.log.Markers;

import java.util.*;

/**
 * User: denispavlov
 * Date: 31/03/2017
 * Time: 08:47
 */
public class GenericFTSLuceneImpl implements GenericFTS<Long, org.apache.lucene.search.Query> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericFTSLuceneImpl.class);

    private static final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    private static final Set<String> PKS = Collections.singleton("_PK");

    private static final int MAX_FACETS = 100;

    private LuceneIndexProvider luceneIndexProvider;


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> fullTextSearchRaw(final String query) {

        final QueryParser queryParser = new QueryParser("", new AsIsAnalyzer(false));

        org.apache.lucene.search.Query parsed;

        try {
            parsed = queryParser.parse(query);
        } catch (Exception e) {
            final String msg = "Cant parse query : " + query + " Error : " + e.getMessage();
            LOG.warn(msg);
            throw new IllegalArgumentException(msg, e);
        }

        if (parsed == null) {
            return Collections.emptyList();
        }

        return fullTextSearch(parsed);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> fullTextSearch(final Query query) {

        LOGFTQ.debug("Run count query {}", query);

        final List<Long> pks = new ArrayList<>();

        IndexSearcher searcher = this.luceneIndexProvider.provideIndexReader();
        try {
            final TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            if (topDocs.totalHits > 0) {
                for (final ScoreDoc hit : topDocs.scoreDocs) {
                    final Document doc = searcher.doc(hit.doc, PKS);
                    pks.add(Long.valueOf(doc.get("_PK")));
                    logExplanation(searcher, query, null, hit.doc);
                }
            }
        } catch (IllegalStateException ise) {
            LOG.warn("Failed to run query " + query + ", caused: " + ise.getMessage());
        } catch (Exception exp) {
            LOG.error("Failed to run query " + query + ", caused: " + exp.getMessage(), exp);
        } finally {
            this.luceneIndexProvider.releaseIndexReader(searcher);
        }

        LOGFTQ.debug("Result is {} query {}", pks, query);

        return pks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> fullTextSearch(final Query query, final int firstResult, final int maxResults, final String sortFieldName, final boolean reverse) {

        LOGFTQ.debug("Run count query {}", query);

        final List<Long> pks = new ArrayList<>();

        IndexSearcher searcher = this.luceneIndexProvider.provideIndexReader();
        try {
            final TopDocs topDocs;
            Sort sort = null;
            if (StringUtils.isNotBlank(sortFieldName)) {
                sort = new Sort(new SortField(sortFieldName, SortField.Type.STRING_VAL, reverse));
                topDocs = searcher.search(query, firstResult + maxResults, sort);
            } else {
                topDocs = searcher.search(query, firstResult + maxResults);
            }
            if (topDocs.totalHits > 0) {
                for (int i = firstResult; i < firstResult + maxResults; i++) {
                    final ScoreDoc hit = topDocs.scoreDocs[i];
                    final Document doc = searcher.doc(hit.doc, PKS);
                    pks.add(Long.valueOf(doc.get("_PK")));
                    logExplanation(searcher, query, sort, hit.doc);
                }
            }
        } catch (IllegalStateException ise) {
            LOG.warn("Failed to run query " + query + ", caused: " + ise.getMessage());
        } catch (Exception exp) {
            LOG.error("Failed to run query " + query + ", caused: " + exp.getMessage(), exp);
        } finally {
            this.luceneIndexProvider.releaseIndexReader(searcher);
        }

        LOGFTQ.debug("Result is {} query {}", pks, query);

        return pks;
    }

    private static final Pair<List<Object[]>, Integer> EMPTY = new Pair<List<Object[]>, Integer>(Collections.EMPTY_LIST, 0);

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<List<Object[]>, Integer> fullTextSearch(final Query query, final int firstResult, final int maxResults, final String sortFieldName, final boolean reverse, final String... fields) {

        LOGFTQ.debug("Run count query {}", query);

        Pair<List<Object[]>, Integer> result = EMPTY;
        int lastResult = maxResults < 0 ? Integer.MAX_VALUE : firstResult + maxResults;

        IndexSearcher searcher = this.luceneIndexProvider.provideIndexReader();
        try {
            final Set<String> retrieve = new HashSet<>(Arrays.asList(fields));
            final TopDocs topDocs;
            Sort sort = null;
            if (StringUtils.isNotBlank(sortFieldName)) {
                sort = new Sort(new SortField(sortFieldName, SortField.Type.STRING_VAL, reverse));
                topDocs = searcher.search(query, lastResult, sort);
            } else {
                topDocs = searcher.search(query, lastResult);
            }
            if (topDocs.totalHits > firstResult) {

                lastResult = lastResult > topDocs.totalHits ? topDocs.totalHits : lastResult;

                final List<Object[]> resItems = new ArrayList<>(lastResult - firstResult);

                for (int i = firstResult; i < lastResult; i++) {
                    final ScoreDoc hit = topDocs.scoreDocs[i];
                    final Document doc = searcher.doc(hit.doc, retrieve);
                    final Object[] values = new Object[fields.length];
                    for (int ii = 0; ii < fields.length; ii++) {
                        values[ii] = doc.get(fields[ii]);
                    }
                    resItems.add(values);
                    logExplanation(searcher, query, sort, hit.doc);
                }

                return new Pair<>(resItems, topDocs.totalHits);
            }
        } catch (IllegalStateException ise) {
            LOG.warn("Failed to run query " + query + ", caused: " + ise.getMessage());
        } catch (Exception exp) {
            LOG.error("Failed to run query " + query + ", caused: " + exp.getMessage(), exp);
        } finally {
            this.luceneIndexProvider.releaseIndexReader(searcher);
        }

        LOGFTQ.debug("Result is {} query {}", result, query);

        return result;
    }

    private void logExplanation(final IndexSearcher searcher, final Query query, final Sort sort, final int doc) throws Exception {
        if (LOGFTQ.isTraceEnabled()) {

            final StringBuilder explanation = new StringBuilder("\n");

            explanation.append("Query: ")
                    .append(query).append("\n");

            if (sort != null) {
                explanation.append("Sort: ")
                        .append(sort).append("\n");
            }

            final Explanation exp = searcher.explain(query, doc);

            explanation.append("#").append(doc).append(" result: \n\n").append(exp);
            LOGFTQ.trace(explanation.toString());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<Pair<Pair<String, I18NModel>, Integer>>> fullTextSearchNavigation(final Query query, final List<FilteredNavigationRecordRequest> facetingRequest) {

        if (facetingRequest == null || facetingRequest.isEmpty()) {
            return Collections.emptyMap();
        }

        LOGFTQ.debug("Run facet query {}", query);

        final Map<String, List<Pair<Pair<String, I18NModel>, Integer>>> result = new LinkedHashMap<>();

        final FacetsCollector fc = new FacetsCollector();

        IndexSearcher searcher = this.luceneIndexProvider.provideIndexReader();
        try {

            FacetsCollector.search(searcher, query, 0, fc);

            for (final FilteredNavigationRecordRequest request : facetingRequest) {

                try {
                    // always reset to empty first (could be multiple attribute mappings with invalid fields type in index, so hard reset)
                    final List<Pair<Pair<String, I18NModel>, Integer>> values = new ArrayList<>();
                    result.put(request.getFacetName(), values);

                    final FacetsConfig facetsConfig = new FacetsConfig();
                    facetsConfig.setIndexFieldName(request.getField(), request.getField());
                    final boolean range = request.isRangeValue();

                    Facets facets = null;

                    if (range) {

                        final List<Pair<String, String>> ranges = request.getRangeValues();
                        if (CollectionUtils.isNotEmpty(ranges)) {

                            final LongRange[] longRanges = new LongRange[ranges.size()];
                            for (int r = 0; r < longRanges.length; r++) {
                                final Pair<String, String> rangeVals = ranges.get(r);
                                final String compareValue = rangeVals.getFirst() + Constants.RANGE_NAVIGATION_DELIMITER + rangeVals.getSecond();
                                longRanges[r] = new LongRange(compareValue, NumberUtils.toLong(rangeVals.getFirst()), true, NumberUtils.toLong(rangeVals.getSecond()), false);
                            }

                            facets = new LongRangeFacetCounts(request.getField(), fc, longRanges);
                        }

                    } else {

                        facets = new SortedSetDocValuesFacetCounts(new DefaultSortedSetDocValuesReaderState(searcher.getIndexReader(), request.getField()), fc);

                    }

                    if (facets != null) {

                        final FacetResult topValues = facets.getTopChildren(MAX_FACETS, request.getField());

                        if (topValues != null && topValues.value != null && topValues.value.intValue() > 0) {

                            final Map<String, Pair<Pair<String, I18NModel>, Integer>> distinctFacetValues =
                                    new LinkedHashMap<>(topValues.labelValues.length * 2);

                            for (final LabelAndValue lav : topValues.labelValues) {

                                final Pair<String, I18NModel> label;
                                final int pos = lav.label.indexOf(Constants.FACET_NAVIGATION_DELIMITER);
                                if (pos != -1) {
                                    final String value = lav.label.substring(0, pos);
                                    final String displayValue = lav.label.substring(pos + Constants.FACET_NAVIGATION_DELIMITER.length());
                                    label = new Pair<>(
                                            value,
                                            new StringI18NModel(displayValue)
                                    );
                                } else {
                                    label = new Pair<>(
                                            lav.label,
                                            null
                                    );
                                }

                                Pair<Pair<String, I18NModel>, Integer> existing = distinctFacetValues.get(label.getFirst());
                                if (existing != null) {
                                    // if we have this value then need to de-duplicate
                                    // we choose more complete I18n model (assumed to be the one with most translations)
                                    if (existing.getFirst().getSecond().getAllValues().size() >
                                            label.getSecond().getAllValues().size()) {
                                        distinctFacetValues.put(
                                                label.getFirst(),
                                                new Pair<>(existing.getFirst(), lav.value.intValue() + existing.getSecond())
                                        );
                                    } else {
                                        distinctFacetValues.put(
                                                label.getFirst(),
                                                new Pair<>(label, lav.value.intValue() + existing.getSecond())
                                        );
                                    }
                                } else {
                                    distinctFacetValues.put(
                                            label.getFirst(),
                                            new Pair<>(label, lav.value.intValue())
                                    );
                                }

                            }

                            // populate with distinct facet values only
                            values.addAll(distinctFacetValues.values());

                        }

                    }
                } catch (IllegalArgumentException | IllegalStateException iae) {
                    LOG.warn(Markers.alert(), "Failed to create facet for request " + request + ", caused: " + iae.getMessage());
                } catch (Exception exp) {
                    LOG.error(Markers.alert(), "Failed to create facet for request " + request + ", caused: " + exp.getMessage(), exp);
                }
            }

        } catch (IllegalStateException ise) {
            LOG.warn("Failed to run query " + query + ", caused: " + ise.getMessage());
        } catch (Exception exp) {
            LOG.error("Failed to run query " + query + ", caused: " + exp.getMessage(), exp);
        } finally {
            this.luceneIndexProvider.releaseIndexReader(searcher);
        }

        LOGFTQ.debug("Result is {} query {}", result, query);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int fullTextSearchCount(final Query query) {

        LOGFTQ.debug("Run count query {}", query);

        int count = 0;

        IndexSearcher searcher = this.luceneIndexProvider.provideIndexReader();

        try {
            count = searcher.count(query);
        } catch (IllegalStateException ise) {
            LOG.warn("Failed to run query " + query + ", caused: " + ise.getMessage());
        } catch (Exception exp) {
            LOG.error("Failed to run query " + query + ", caused: " + exp.getMessage(), exp);
        } finally {
            this.luceneIndexProvider.releaseIndexReader(searcher);
        }

        LOGFTQ.debug("Count is {} query {}", count, query);

        return count;
    }


    /**
     * Spring IoC.
     *
     * @param  luceneIndexProvider provider
     */
    public void setLuceneIndexProvider(final LuceneIndexProvider luceneIndexProvider) {
        this.luceneIndexProvider = luceneIndexProvider;
    }

}

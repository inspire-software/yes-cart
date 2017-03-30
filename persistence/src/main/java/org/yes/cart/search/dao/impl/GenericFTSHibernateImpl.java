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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.util.FixedBitSet;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.query.dsl.DiscreteFacetContext;
import org.hibernate.search.query.dsl.FacetRangeAboveBelowContext;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.engine.spi.FacetManager;
import org.hibernate.search.query.facet.Facet;
import org.hibernate.search.query.facet.FacetSortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Identifiable;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.GenericFTS;
import org.yes.cart.search.dto.FilteredNavigationRecordRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class GenericFTSHibernateImpl<PK extends Serializable>
        implements GenericFTS<PK, org.apache.lucene.search.Query> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericFTSHibernateImpl.class);

    private final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    private final Class persistentClass;
    private final boolean persistentClassIndexble;
    protected SessionFactory sessionFactory;

    /**
     * Set the Hibernate SessionFactory to be used by this DAO.
     * Will automatically create a HibernateTemplate for the given SessionFactory.
     */
    public final void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

    }

    /**
     * Default constructor.
     *
     * @param type          - entity type
     */
    @SuppressWarnings("unchecked")
    public GenericFTSHibernateImpl(final Class type) {
        this.persistentClass = type;
        this.persistentClassIndexble = null != type.getAnnotation(Indexed.class);
    }

    private Class getPersistentClass() {
        return persistentClass;
    }

    private <I> I getEntityIdentifier(final Object entity) {
        if (entity == null) {
            // That's ok - it is null
            return null;
        } if (entity instanceof HibernateProxy && !Hibernate.isInitialized(entity)) {
            // Avoid Lazy select by getting identifier from session meta
            // If hibernate proxy is initialised then DO NOT use this approach as chances
            // are that this is detached entity from cache which is not associate with the
            // session and will result in exception
            return (I) sessionFactory.getCurrentSession().getIdentifier(entity);
        } else if (entity instanceof Identifiable) {
            // If it is not proxy or it is initialised then we can use identifiable
            return (I) Long.valueOf(((Identifiable) entity).getId());
        }
        throw new IllegalArgumentException("Cannot get PK from object: " + entity);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<PK> fullTextSearch(final org.apache.lucene.search.Query query) {
        if (persistentClassIndexble) {
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
            Query fullTextQuery = fullTextSession.createFullTextQuery(query, getPersistentClass());
            List list = fullTextQuery.list();
            if (list != null) {
                final List<PK> pks = new ArrayList<PK>(list.size());
                for (final Object entity : list) {
                    pks.add((PK) getEntityIdentifier(entity));
                }
                return pks;
            }
        }
        return Collections.EMPTY_LIST;
    }

    private static final Pair<List<Object[]>, Integer> EMPTY = new Pair<List<Object[]>, Integer>(Collections.EMPTY_LIST, 0);

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Pair<List<Object[]>, Integer> fullTextSearch(final org.apache.lucene.search.Query query,
                                                        final int firstResult,
                                                        final int maxResults,
                                                        final String sortFieldName,
                                                        final boolean reverse,
                                                        final String ... fields) {

        if (persistentClassIndexble) {
            if (LOGFTQ.isDebugEnabled()) {
                LOGFTQ.debug("Run {}x{} {}@{} {}", new Object[] { firstResult, maxResults, sortFieldName, reverse, query });
            }

            final boolean explain = LOGFTQ.isTraceEnabled();
            final FullTextQuery fullTextQuery = createFullTextQuery(query, firstResult, maxResults, sortFieldName, reverse);
            if (explain) {
                final List<String> allFields = new ArrayList<String>(Arrays.asList(fields));
                allFields.add(FullTextQuery.EXPLANATION);
                fullTextQuery.setProjection(allFields.toArray(new String[allFields.size()]));
            } else {
                fullTextQuery.setProjection(fields);
            }
            final List<Object[]> list = fullTextQuery.list();
            if (list != null) {
                if (explain) {
                    final StringBuilder explanation = new StringBuilder("\n");
                    explanation.append("Query: ")
                            .append(query).append("\n");
                    explanation.append("First/Max/Sort/Reverse: ")
                            .append(firstResult).append("/").append(maxResults).append("/").append(sortFieldName).append("/").append(reverse).append("\n");
                    explanation.append(list.size()).append(" result(s): \n\n");
                    for (int i = 0; i < list.size(); i++) {
                        explanation.append(i).append(" =======================================================\n");
                        final Object[] item = list.get(i);
                        for (int ii = 0; ii < item.length - 1; ii++) {
                            explanation.append(item[ii]).append(",");
                        }
                        explanation.append("\n\nreason: ").append(item[item.length - 1]).append("\n");
                    }
                    LOGFTQ.trace(explanation.toString());
                }
                return new Pair<List<Object[]>, Integer>(list, fullTextQuery.getResultSize());
            }

        }
        return EMPTY;

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<PK> fullTextSearch(final org.apache.lucene.search.Query query,
                                   final int firstResult,
                                   final int maxResults,
                                   final String sortFieldName,
                                   final boolean reverse) {
        if (persistentClassIndexble) {
            if (LOGFTQ.isDebugEnabled()) {
                LOGFTQ.debug("Run {}x{} {}@{} {}", new Object[] { firstResult, maxResults, sortFieldName, reverse, query });
            }

            final FullTextQuery fullTextQuery = createFullTextQuery(query, firstResult, maxResults, sortFieldName, reverse);
            final List list = fullTextQuery.list();
            if (list != null) {
                final List<PK> pks = new ArrayList<PK>(list.size());
                for (final Object entity : list) {
                    pks.add((PK) getEntityIdentifier(entity));
                }
                return pks;
            }
        }
        return Collections.EMPTY_LIST;
    }

    private FullTextQuery createFullTextQuery(final org.apache.lucene.search.Query query,
                                              final int firstResult,
                                              final int maxResults,
                                              final String sortFieldName,
                                              final boolean reverse) {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query, getPersistentClass());
        if (sortFieldName != null) {
            Sort sort = new Sort(
                    new SortField(sortFieldName, SortField.STRING, reverse));
            fullTextQuery.setSort(sort);
        }
        fullTextQuery.setFirstResult(firstResult);
        if (maxResults > 0) {
            fullTextQuery.setMaxResults(maxResults);
        }
        return fullTextQuery;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<Pair<String, Integer>>> fullTextSearchNavigation(final org.apache.lucene.search.Query query,
                                                                             final List<FilteredNavigationRecordRequest> facetingRequest) {
        if (persistentClassIndexble) {
            if (LOGFTQ.isDebugEnabled()) {
                LOGFTQ.debug("Run facet request with base query {}", query);
            }

            if (facetingRequest == null || facetingRequest.isEmpty()) {
                return Collections.emptyMap();
            }

            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
            QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(getPersistentClass()).get();

            FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query, getPersistentClass());
            fullTextQuery.setMaxResults(1);
            final FacetManager facetManager = fullTextQuery.getFacetManager();
            boolean hasMultivalue = false;
            for (final FilteredNavigationRecordRequest facetingRequestItem : facetingRequest) {
                if (facetingRequestItem.isRangeValue()) {
                    final FacetRangeAboveBelowContext facetCtx = qb.facet().name(facetingRequestItem.getFacetName())
                            .onField(facetingRequestItem.getField()).range();
                    final Iterator<Pair<String, String>> rageIt = facetingRequestItem.getRangeValues().iterator();
                    while (rageIt.hasNext()) {
                        final Pair<String, String> range = rageIt.next();
                        if (rageIt.hasNext()) {
                            facetCtx.from(range.getFirst()).to(range.getSecond()).excludeLimit();
                        } else {
                            facetManager.enableFaceting(facetCtx.from(range.getFirst()).to(range.getSecond())
                                    .orderedBy(FacetSortOrder.RANGE_DEFINITION_ODER).createFacetingRequest());
                        }
                    }
                } else {
                    final DiscreteFacetContext facetCtx = qb.facet().name(facetingRequestItem.getFacetName())
                            .onField(facetingRequestItem.getField()).discrete();
                    facetManager.enableFaceting(facetCtx
                            .includeZeroCounts(facetingRequestItem.isMultiValue())
                            .createFacetingRequest());
                    if (facetingRequestItem.isMultiValue()) {
                        hasMultivalue = true;
                    }
                }
            }

            final Map<String, List<Pair<String, Integer>>> out = new HashMap<String, List<Pair<String, Integer>>>();
            IndexReader indexReader = null;
            FixedBitSet baseBitSet = null;
            try {

                if (hasMultivalue) {
                    indexReader = fullTextSession.getSearchFactory().getIndexReaderAccessor().open(getPersistentClass());
                    CachingWrapperFilter baseQueryFilter = new CachingWrapperFilter(new QueryWrapperFilter(query));
                    try {
                        DocIdSet docIdSet = baseQueryFilter.getDocIdSet(indexReader);
                        if (docIdSet instanceof FixedBitSet) {
                            baseBitSet = (FixedBitSet) docIdSet;
                        } else {
                            baseBitSet = new FixedBitSet(1);
                        }
                    } catch (IOException e) {
                        LOGFTQ.error("Unable to create base query bit set for query {} and faceting request {}", query, facetingRequest);
                        LOGFTQ.error("Stacktrace:", e);
                        baseBitSet = new FixedBitSet(1);
                    }
                }

                for (final FilteredNavigationRecordRequest facetingRequestItem : facetingRequest) {

                    final List<Pair<String, Integer>> facetsPairs =
                            new ArrayList<Pair<String, Integer>>();

                    final List<Facet> facets =  facetManager.getFacets(facetingRequestItem.getFacetName());

                    LOGFTQ.debug("Faceting request request: {}", facetingRequestItem);

                    if (facetingRequestItem.isMultiValue() && !facetingRequestItem.isRangeValue()) {
                        // Multivalue black magic
                        for (final Facet facet : facets) {

                            final org.apache.lucene.search.Query facetQuery = new TermQuery(new Term(facet.getFieldName(), facet.getValue()));
                            try {
                                CachingWrapperFilter filter = new CachingWrapperFilter(new QueryWrapperFilter(facetQuery));
                                DocIdSet docIdSet = filter.getDocIdSet(indexReader);
                                if (docIdSet instanceof FixedBitSet) {
                                    FixedBitSet filterBitSet = (FixedBitSet) docIdSet;
                                    filterBitSet.and(baseBitSet);
                                    long count = filterBitSet.cardinality();
                                    if (count > 0L) {
                                        LOGFTQ.debug("Has facet: {}", facet);
                                        facetsPairs.add(new Pair<String, Integer>(facet.getValue(), (int) count));
                                    }
                                }
                            } catch (IOException e) {
                                LOGFTQ.error("Unable to create filter query bit set for query {} and faceting query {}", query, facetQuery);
                                LOGFTQ.error("Stacktrace:", e);
                            }

                        }
                    } else {
                        // Standard discrete values and ranges
                        for (final Facet facet : facets) {
                            LOGFTQ.debug("Has facet: {}", facet);
                            facetsPairs.add(new Pair<String, Integer>(facet.getValue(), facet.getCount()));
                        }
                    }
                    out.put(facetingRequestItem.getFacetName(), facetsPairs);
                }
            } finally {
                if (hasMultivalue) {
                    fullTextSession.getSearchFactory().getIndexReaderAccessor().close(indexReader);
                }
            }
            return out;
        }
        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    public int fullTextSearchCount(final org.apache.lucene.search.Query query) {
        if (persistentClassIndexble) {
            if (LOGFTQ.isDebugEnabled()) {
                LOGFTQ.debug("Count {}", query);
            }

            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
            FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query, getPersistentClass());
            fullTextQuery.setMaxResults(1);
            return fullTextQuery.getResultSize();
        }
        return 0;
    }

}

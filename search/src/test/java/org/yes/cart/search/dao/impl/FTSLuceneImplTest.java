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

import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TermQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.LuceneDocumentAdapter;
import org.yes.cart.search.dao.LuceneIndexProvider;
import org.yes.cart.search.dao.entity.LuceneDocumentAdapterUtils;
import org.yes.cart.search.dto.FilteredNavigationRecordRequest;
import org.yes.cart.search.dto.impl.FilteredNavigationRecordRequestImpl;

import java.util.*;

import static org.junit.Assert.*;

/**
 * This is basic FTS configuration. It acts as PoC for how Lucene is working and shows several concepts and
 * pitfalls that should be taken into account.
 *
 * User: denispavlov
 * Date: 31/03/2017
 * Time: 17:34
 */
public class FTSLuceneImplTest {

    private LuceneIndexProviderImpl provider;
    private GenericFTSLuceneImpl genericFTSLucene;
    private MapLuceneDocumentAdapter documentAdapter;
    private MapIndexBuilderLucene indexBuilderLucene;


    @Before
    public void setUp() throws Exception {

        provider = new LuceneIndexProviderImpl("test", "ram");
        provider.afterPropertiesSet();
        genericFTSLucene = new GenericFTSLuceneImpl();
        genericFTSLucene.setLuceneIndexProvider(provider);
        documentAdapter = new MapLuceneDocumentAdapter();
        indexBuilderLucene = new MapIndexBuilderLucene(documentAdapter, provider);
    }

    private static class MapLuceneDocumentAdapter implements LuceneDocumentAdapter<Map<String, Object>, Long> {

        private List<String> facets = Collections.emptyList();
        private FacetsConfig facetsConfig = new FacetsConfig();
        private List<String> numeric = Collections.singletonList("_PK");

        @Override
        public Pair<Long, Document[]> toDocument(final Map<String, Object> entity) {
            final Document ldoc = new Document();

            LuceneDocumentAdapterUtils.addObjectDefaultField(ldoc, entity);

            Long pk = null;
            for (final Map.Entry<String, Object> field : entity.entrySet()) {

                final String fieldName = field.getKey();
                final Object values = field.getValue();
                final List<String> strValues = values instanceof String ? Collections.singletonList((String) values) : (List) values;
                final boolean multi = strValues.size() > 1;
                final boolean numeric = this.numeric.contains(fieldName);

                for (final String strValue : strValues) {

                    if (LuceneDocumentAdapterUtils.FIELD_PK.equals(fieldName)) {
                        pk = NumberUtils.toLong(strValue);
                        LuceneDocumentAdapterUtils.addPkField(ldoc, Object.class, strValue);
                    }

                    if (numeric) {

                        LuceneDocumentAdapterUtils.addNumericField(ldoc, fieldName + "_range", NumberUtils.toLong(strValue), false);

                    } else {

                        LuceneDocumentAdapterUtils.addStemField(ldoc, fieldName, strValue);

                    }


                    if (!multi) {

                        if (numeric) {

                            LuceneDocumentAdapterUtils.addSortField(ldoc, fieldName + "_sort", NumberUtils.toLong(strValue), true);

                        } else {

                            LuceneDocumentAdapterUtils.addSortField(ldoc, fieldName + "_sort", strValue);

                        }

                    }

                    if (facets.contains(field.getKey())) {

                        if (numeric) {

                            LuceneDocumentAdapterUtils.addFacetField(ldoc, fieldName + "_facet", NumberUtils.toLong(strValue));

                        } else {

                            LuceneDocumentAdapterUtils.addFacetField(ldoc, fieldName + "_facet", strValue);

                        }
                    }
                }
            }
            return new Pair<Long, Document[]>(pk, new Document[] { ldoc });
        }

        public void setFacets(final List<String> facets) {

            this.facets = facets;

            if (!facets.isEmpty()) {
            /*
                Facets need to be mapped in config. The basic solution is to map them to same name.
                Note that for multi value fields (i.e. when multiple fields with same name are added to
                document) need to set true for #facetsConfig.setMultiValued()
             */
                for (final String facet : facets) {
                    facetsConfig.setIndexFieldName(facet + "_facet", facet + "_facet");
                    facetsConfig.setMultiValued(facet + "_facet", true);
                }
            }

        }

        public void setNumeric(final List<String> numeric) {

            this.numeric = numeric;

        }
    }

    private static class MapIndexBuilderLucene extends IndexBuilderLuceneImpl<Map<String, Object>, Long> {

        private List<Map<String, Object>> docs = null;

        public MapIndexBuilderLucene(final LuceneDocumentAdapter<Map<String, Object>, Long> documentAdapter,
                                     final LuceneIndexProvider indexProvider) {
            super(documentAdapter, indexProvider);
        }


        public void setDocs(final List<Map<String, Object>> docs) {
            this.docs = docs;
        }

        @Override
        protected Map<String, Object> findById(final Long primaryKey) {
            for (final Map<String, Object> doc : this.docs) {
                if (doc.get(LuceneDocumentAdapterUtils.FIELD_PK).equals(String.valueOf(primaryKey))) {
                    return doc;
                }
            }
            return null;
        }

        @Override
        protected Object startTx() {
            return new Object();
        }

        @Override
        protected ResultsIterator<Map<String, Object>> findAllIterator() {

            final Iterator<Map<String, Object>> it = this.docs.iterator();

            return new ResultsIterator<Map<String, Object>>() {
                @Override
                public void remove() {
                    it.remove();
                }

                @Override
                public void close() {

                }

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public Map<String, Object> next() {
                    return it.next();
                }
            };
        }

        @Override
        protected Map<String, Object> unproxyEntity(final Map<String, Object> entity) {
            return entity;
        }

        @Override
        protected void endBatch(final Object tx) {

        }

        @Override
        protected void endTx(final Object tx) {

        }
    }

    @After
    public void tearDown() throws Exception {

        provider.destroy();

    }


    @Test
    public void testFullTextSearch() throws Exception {

        indexBuilderLucene.setDocs(
                (List) Arrays.asList(
                        new HashMap<String, String>() {{
                            put("_PK", "100000");
                            put("name", "item one");
                            put("desc", "some desc");
                        }},
                        new HashMap<String, String>() {{
                            put("_PK", "100001");
                            put("name", "item two");
                            put("desc", "other desc");
                        }},
                        new HashMap<String, String>() {{
                            put("_PK", "111111");
                            put("name", "element three");
                            put("desc", "other desc");
                        }}
                )
        );

        indexBuilderLucene.fullTextSearchReindex(false, 2);

        List<Long> pks;
        Pair<List<Object[]>, Integer> rez;

        // All documents
        assertEquals(3, genericFTSLucene.fullTextSearchCount(new MatchAllDocsQuery()));
        pks = genericFTSLucene.fullTextSearch(new MatchAllDocsQuery());
        assertNotNull(pks);
        assertEquals(3, pks.size());
        assertTrue(pks.contains(100000L));
        assertTrue(pks.contains(100001L));
        assertTrue(pks.contains(111111L));

        // Simple Criteria
        assertEquals(1, genericFTSLucene.fullTextSearchCount(new TermQuery(new Term("_PK", "100000"))));
        pks = genericFTSLucene.fullTextSearch(new TermQuery(new Term("_PK", "100000")));
        assertNotNull(pks);
        assertEquals(1, pks.size());
        assertTrue(pks.contains(100000L));

        // Range Criteria
        assertEquals(2, genericFTSLucene.fullTextSearchCount(LongPoint.newRangeQuery("_PK_range", 100001L, Math.addExact(999999L, -1))));
        pks = genericFTSLucene.fullTextSearch(LongPoint.newRangeQuery("_PK_range", 100001L, Math.addExact(999999L, -1)));
        assertNotNull(pks);
        assertEquals(2, pks.size());
        assertTrue(pks.contains(100001L));
        assertTrue(pks.contains(111111L));

        // Term Criteria single term
        assertEquals(2, genericFTSLucene.fullTextSearchCount(new TermQuery(new Term("name", "item"))));
        pks = genericFTSLucene.fullTextSearch(new TermQuery(new Term("name", "item")));
        assertNotNull(pks);
        assertEquals(2, pks.size());
        assertTrue(pks.contains(100000L));
        assertTrue(pks.contains(100001L));

        // Term Criteria single term not found
        assertEquals(0, genericFTSLucene.fullTextSearchCount(new TermQuery(new Term("name", "ite"))));
        pks = genericFTSLucene.fullTextSearch(new TermQuery(new Term("name", "ite")));
        assertNotNull(pks);
        assertEquals(0, pks.size());

        // Fuzzy Criteria
        assertEquals(2, genericFTSLucene.fullTextSearchCount(new FuzzyQuery(new Term("_PK", "100000"))));
        pks = genericFTSLucene.fullTextSearch(new FuzzyQuery(new Term("_PK", "100000")));
        assertNotNull(pks);
        assertEquals(2, pks.size());
        assertTrue(pks.contains(100000L));
        assertTrue(pks.contains(100001L));

        // Fuzzy Criteria mis-spell
        assertEquals(2, genericFTSLucene.fullTextSearchCount(new FuzzyQuery(new Term("name", "utem"))));
        pks = genericFTSLucene.fullTextSearch(new FuzzyQuery(new Term("name", "utem")));
        assertNotNull(pks);
        assertEquals(2, pks.size());
        assertTrue(pks.contains(100000L));
        assertTrue(pks.contains(100001L));

        // All documents pagination
        pks = genericFTSLucene.fullTextSearch(new MatchAllDocsQuery(), 0, 1, null, false);
        assertNotNull(pks);
        assertEquals(1, pks.size());
        assertTrue(pks.contains(100000L));
        pks = genericFTSLucene.fullTextSearch(new MatchAllDocsQuery(), 1, 1, null, false);
        assertNotNull(pks);
        assertEquals(1, pks.size());
        assertTrue(pks.contains(100001L));
        pks = genericFTSLucene.fullTextSearch(new MatchAllDocsQuery(), 2, 1, null, false);
        assertNotNull(pks);
        assertEquals(1, pks.size());
        assertTrue(pks.contains(111111L));

        // All documents pagination, sorted
        pks = genericFTSLucene.fullTextSearch(new MatchAllDocsQuery(), 0, 1, "_PK_sort", true);
        assertNotNull(pks);
        assertEquals(1, pks.size());
        assertTrue(pks.contains(111111L));
        pks = genericFTSLucene.fullTextSearch(new MatchAllDocsQuery(), 1, 1, "_PK_sort", true);
        assertNotNull(pks);
        assertEquals(1, pks.size());
        assertTrue(pks.contains(100001L));
        pks = genericFTSLucene.fullTextSearch(new MatchAllDocsQuery(), 2, 1, "_PK_sort", true);
        assertNotNull(pks);
        assertEquals(1, pks.size());
        assertTrue(pks.contains(100000L));


        // Specific field, page 2, sorted
        rez = genericFTSLucene.fullTextSearch(new MatchAllDocsQuery(), 1, 1, "_PK_sort", true, "_PK", "_OBJECT");
        assertNotNull(rez);
        assertEquals(Integer.valueOf(3), rez.getSecond());
        final List<Object[]> vals = rez.getFirst();
        assertNotNull(vals);
        assertEquals(1, vals.size());
        assertEquals("100001", vals.get(0)[0]);
        assertEquals("{\"name\":\"item two\",\"_PK\":\"100001\",\"desc\":\"other desc\"}", vals.get(0)[1]);

    }

    @Test
    public void testFullTextSearchNavigation() throws Exception {


        indexBuilderLucene.setDocs(
                (List) Arrays.asList(
                        new HashMap<String, String>() {{
                            put("_PK", "100000");
                            put("name", "item one");
                            put("desc", "some desc");
                            put("attr1", "A1");
                        }},
                        new HashMap<String, Object>() {{
                            put("_PK", "100001");
                            put("name", "item two");
                            put("desc", Arrays.asList("some desc", "other desc"));
                            put("attr1", "A1");
                        }},
                        new HashMap<String, String>() {{
                            put("_PK", "111111");
                            put("name", "element");
                            put("desc", "other desc");
                            put("attr1", "A1");
                        }},
                        new HashMap<String, String>() {{
                            put("_PK", "111112");
                            put("name", "element");
                            put("desc", "other desc");
                            put("attr1", "A2");
                        }},
                        new HashMap<String, String>() {{
                            put("_PK", "111113");
                            put("name", "element");
                            put("desc", "desc");
                            put("attr1", "A2");
                        }},
                        new HashMap<String, String>() {{
                            put("_PK", "111114");
                            put("name", "element");
                            put("desc", "desc");
                            put("attr1", "A3");
                        }}
                )
        );

        documentAdapter.setFacets(Arrays.asList("_PK", "name", "desc"));

        indexBuilderLucene.fullTextSearchReindex(false, 2);


        Map<String, List<Pair<Pair<String, I18NModel>, Integer>>> rez;
        List<Pair<Pair<String, I18NModel>, Integer>> facets;

        final FilteredNavigationRecordRequest r1 = new FilteredNavigationRecordRequestImpl("PK Range", "_PK_facet", Arrays.asList(new Pair<String, String>("000000", "100001"), new Pair<String, String>("100001", "999999")));
        final FilteredNavigationRecordRequest f1 = new FilteredNavigationRecordRequestImpl("Names", "name_facet", false);
        final FilteredNavigationRecordRequest f2 = new FilteredNavigationRecordRequestImpl("Descriptions", "desc_facet", true);

        final List<FilteredNavigationRecordRequest> fr = Arrays.asList(r1, f1, f2);

        // Global facets, with range and multi-values
        rez = genericFTSLucene.fullTextSearchNavigation(new MatchAllDocsQuery(), fr);

        assertNotNull(rez);
        assertEquals(3, rez.size());

        facets = rez.get("PK Range");
        assertEquals(2, facets.size());
        checkFacetValue(facets, "000000-_-100001", 1);
        checkFacetValue(facets, "100001-_-999999", 5);
        facets = rez.get("Names");
        assertEquals(3, facets.size());
        checkFacetValue(facets, "item one", 1);
        checkFacetValue(facets, "item two", 1);
        checkFacetValue(facets, "element", 4);
        facets = rez.get("Descriptions");
        assertEquals(3, facets.size());
        checkFacetValue(facets, "some desc", 2);
        checkFacetValue(facets, "other desc", 3);
        checkFacetValue(facets, "desc", 2);


        // Global facets, drill-down by non faceted value
        rez = genericFTSLucene.fullTextSearchNavigation(new TermQuery(new Term("attr1", "a2")), fr);

        assertNotNull(rez);
        assertEquals(3, rez.size());

        facets = rez.get("PK Range");
        assertEquals(2, facets.size());
        checkFacetValue(facets, "000000-_-100001", 0);
        checkFacetValue(facets, "100001-_-999999", 2);
        facets = rez.get("Names");
        assertEquals(1, facets.size());
        checkFacetValue(facets, "element", 2);
        facets = rez.get("Descriptions");
        assertEquals(2, facets.size());
        checkFacetValue(facets, "other desc", 1);
        checkFacetValue(facets, "desc", 1);


        // Global facets, drill-down by faceted value
        rez = genericFTSLucene.fullTextSearchNavigation(LongPoint.newRangeQuery("_PK_range", 100001L, Math.addExact(999999L, -1)), fr);

        assertNotNull(rez);
        assertEquals(3, rez.size());

        facets = rez.get("PK Range");
        assertEquals(2, facets.size());
        checkFacetValue(facets, "000000-_-100001", 0);
        checkFacetValue(facets, "100001-_-999999", 5);
        facets = rez.get("Names");
        assertEquals(2, facets.size());
        checkFacetValue(facets, "item two", 1);
        checkFacetValue(facets, "element", 4);
        facets = rez.get("Descriptions");
        assertEquals(3, facets.size());
        checkFacetValue(facets, "some desc", 1);
        checkFacetValue(facets, "other desc", 3);
        checkFacetValue(facets, "desc", 2);


    }

    private void checkFacetValue(List<Pair<Pair<String, I18NModel>, Integer>> facets, String expectedValue, Integer expectedCount) {
        for (final Pair<Pair<String, I18NModel>, Integer> facet : facets) {
            if (expectedValue.equals(facet.getFirst().getFirst())) {
                assertEquals("Unexpected count for " + facet.getFirst(), expectedCount, facet.getSecond());
                return;
            }
        }
        fail("Facet value '" + expectedValue + "' with count " + expectedCount + " not found in " + facets);
    }

}
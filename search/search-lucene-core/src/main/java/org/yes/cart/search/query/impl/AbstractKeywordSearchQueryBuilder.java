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

package org.yes.cart.search.query.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.yes.cart.search.dao.entity.LuceneDocumentAdapterUtils;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.query.ProductSearchQueryBuilder;
import org.yes.cart.search.utils.SearchUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Analysed terms are all lower case - there is no point in using fuzzy to search against them
 * as it is case sensitive. Make sure that analysed field are "word.toLowerCase()" matches.
 *
 * User: denispavlov
 * Date: 16/11/2014
 * Time: 16:19
 */
public abstract class AbstractKeywordSearchQueryBuilder extends AbstractSearchQueryBuilderImpl implements ProductSearchQueryBuilder<Query> {

    private final boolean fullStrictMatch;

    protected AbstractKeywordSearchQueryBuilder(final boolean fullStrictMatch) {
        this.fullStrictMatch = fullStrictMatch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Query> createQueryChain(final NavigationContext<Query> navigationContext, final String parameter, final Object value) {

        final StringBuilder all = new StringBuilder();
        if (value instanceof Collection) {
            for (final Object keywords : (Collection) value) {
                if (keywords instanceof String) {
                    if (all.length() > 0) {
                        all.append(' ');
                    }
                    all.append(keywords);
                }
            }
        } else if (value instanceof String) {
            all.append(value);
        } else {
            return null;
        }

        final String full = all.toString().toLowerCase();

        final List<String> words = SearchUtil.splitForSearch(full, LuceneDocumentAdapterUtils.CHAR_THRESHOLD);
        if (CollectionUtils.isEmpty(words)) {
            return null;
        }

        final List<String> stems = LuceneSearchUtil.analyseForSearch(navigationContext.getCustomerLanguage(), full);

        final boolean useStems = isUseStems(words, stems);

        final List<Query> queries = new ArrayList<>();

        final Query exactAllTerms = createKeywordQueryExactAll(words);
        if (exactAllTerms != null) {
            queries.add(exactAllTerms);
        }

        if (useStems) {
            final Query exactAllStems = createKeywordQueryExactAll(stems);
            if (exactAllStems != null) {
                queries.add(exactAllStems);
            }
        }

        final Query termsAll = createKeywordQueryTermsAll(words);
        if (termsAll != null) {
            queries.add(termsAll);
        }

        if (useStems) {
            final Query stemsAll = createKeywordQueryStemsAll(stems);
            if (stemsAll != null) {
                queries.add(stemsAll);
            }
        }

        if (words.size() > 1) {
            for (int i = 1; i <= words.size() / 2; i++) {
                final Query termsAny = createKeywordQueryTermsAny(words, i);
                if (termsAny != null) {
                    queries.add(termsAny);
                }
                if (useStems) {
                    final Query stemsAny = createKeywordQueryStemsAny(stems, i);
                    if (stemsAny != null) {
                        queries.add(stemsAny);
                    }
                }
            }
        }

        if (queries.size() > 0) {
            return queries;
        }
        return null;

    }

    /**
     * Determine if stems are valid option to use (check if they are different from words, otherwise no point in adding them)
     *
     * @param words words (basic split)
     * @param stems stems (analysed split)
     *
     * @return true if stems need to be used in queries
     */
    protected boolean isUseStems(final List<String> words, final List<String> stems) {

        if (CollectionUtils.isEmpty(stems)) {
            return false;
        }

        if (words.size() != stems.size()) {
            return true; //different set of terms (possibly removed stopwords)
        }

        for (int i = 0; i < words.size(); i++) {
            if (!words.get(i).equals(stems.get(i))) {
                return true; // word is stemmed
            }
        }

        return false;
    }

    /**
     * Query for single term.
     *
     * @param term term
     *
     * @return query
     */
    protected abstract Query createKeywordQueryExact(final String term);


    private Query createKeywordQueryExactAll(final List<String> words) {

        if (CollectionUtils.isEmpty(words)) {
            return null;
        }

        if (words.size() > 1) {

            final BooleanQuery.Builder wordsQuery = new BooleanQuery.Builder();

            for (String word : words) {

                wordsQuery.add(createKeywordQueryExact(word), fullStrictMatch ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD);
            }

            return wordsQuery.build();

        }

        return createKeywordQueryExact(words.get(0));

    }

    /**
     * Query for single term.
     *
     * @param term term
     *
     * @return query
     */
    protected abstract Query createKeywordQueryTerm(final String term);


    private Query createKeywordQueryTermsAll(final List<String> words) {

        if (CollectionUtils.isEmpty(words)) {
            return null;
        }

        if (words.size() > 1) {

            final BooleanQuery.Builder wordsQuery = new BooleanQuery.Builder();

            for (String word : words) {

                wordsQuery.add(createKeywordQueryTerm(word), fullStrictMatch ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD);
            }

            return wordsQuery.build();

        }

        return createKeywordQueryTerm(words.get(0));

    }


    /**
     * Query for single stem.
     *
     * @param stem stem
     *
     * @return query
     */
    protected abstract Query createKeywordQueryStem(final String stem);


    private Query createKeywordQueryStemsAll(final List<String> words) {

        if (CollectionUtils.isEmpty(words)) {
            return null;
        }

        if (words.size() > 1) {

            final BooleanQuery.Builder wordsQuery = new BooleanQuery.Builder();

            for (String word : words) {

                wordsQuery.add(createKeywordQueryStem(word), fullStrictMatch ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD);
            }

            return wordsQuery.build();

        }

        return createKeywordQueryStem(words.get(0));

    }


    private Query createKeywordQueryTermsAny(final List<String> words, int noMatch) {

        if (CollectionUtils.isEmpty(words)) {
            return null;
        }

        if (words.size() > 1) {

            final BooleanQuery.Builder wordsQuery = new BooleanQuery.Builder();

            for (String word : words) {

                wordsQuery.add(createKeywordQueryTerm(word), BooleanClause.Occur.SHOULD);
            }

            if (fullStrictMatch) {
                wordsQuery.setMinimumNumberShouldMatch(words.size() - noMatch);
            }

            return wordsQuery.build();

        }

        return null; // single words already tried in lvl1

    }


    private Query createKeywordQueryStemsAny(final List<String> words, int noMatch) {

        if (CollectionUtils.isEmpty(words)) {
            return null;
        }

        if (words.size() > 1) {

            final BooleanQuery.Builder wordsQuery = new BooleanQuery.Builder();

            for (String word : words) {

                wordsQuery.add(createKeywordQueryStem(word), BooleanClause.Occur.SHOULD);
            }

            if (fullStrictMatch) {
                wordsQuery.setMinimumNumberShouldMatch(words.size() - noMatch);
            }

            return wordsQuery.build();

        }

        return null; // single words already tried in lvl2

    }


}

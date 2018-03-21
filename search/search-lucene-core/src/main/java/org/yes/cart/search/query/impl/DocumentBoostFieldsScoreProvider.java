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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.search.Explanation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Uses document stored fields that hold the float boost values. The value must be 1f for no boost,
 * less than 1f to lower documents score and more than 1f to increase the score.
 *
 * User: denispavlov
 * Date: 31/05/2017
 * Time: 17:27
 */
public class DocumentBoostFieldsScoreProvider extends CustomScoreProvider {

    private final LeafReader reader;
    private final Set<String> boostFields;

    public DocumentBoostFieldsScoreProvider(final LeafReaderContext context, final Set<String> boostFields) {
        super(context);
        this.boostFields = boostFields;
        reader = context.reader();
    }

    @Override
    public float customScore(final int doc, final float subQueryScore, final float[] valSrcScores) throws IOException {
        if (valSrcScores.length == 1) {
            return customScore(doc, subQueryScore, valSrcScores[0]);
        }
        if (valSrcScores.length == 0) {
            return customScore(doc, subQueryScore, 1);
        }
        float score = 1;
        for (float valSrcScore : valSrcScores) {
            score *= valSrcScore;
        }
        return customScore(doc, subQueryScore, score);
    }

    @Override
    public float customScore(final int doc, final float subQueryScore, final float valSrcScore) throws IOException {

        Document document = reader.document(doc, boostFields);
        float boost = 1f;
        for (final String fieldName : boostFields) {
            IndexableField field = document.getField(fieldName);
            Number number = field.numericValue();
            boost *= number.floatValue();
        }

        return subQueryScore * valSrcScore * boost;
    }

    @Override
    public Explanation customExplain(final int doc, final Explanation subQueryExpl, final Explanation[] valSrcExpls) throws IOException {
        if (valSrcExpls.length == 1) {
            return customExplain(doc, subQueryExpl, valSrcExpls[0]);
        }
        if (valSrcExpls.length == 0) {
            return customExplain(doc, subQueryExpl, (Explanation) null);
        }
        float valSrcScore = 1;
        List<Explanation> subs = new ArrayList<>();
        subs.add(subQueryExpl);
        for (Explanation valSrcExpl : valSrcExpls) {
            final Explanation subExpl = customExplain(doc, subQueryExpl, valSrcExpl);
            subs.add(subExpl);
            valSrcScore += subExpl.getValue();
        }
        return Explanation.match(valSrcScore, "custom score: product of:", subs);
    }

    @Override
    public Explanation customExplain(final int doc, final Explanation subQueryExpl, final Explanation valSrcExpl) throws IOException {

        Document document = reader.document(doc, boostFields);
        float boost = 1f;
        final Explanation[] boostExpl = new Explanation[boostFields.size()];
        int i = 0;
        for (final String fieldName : boostFields) {
            IndexableField field = document.getField(fieldName);
            Number number = field.numericValue();
            boost *= number.floatValue();
            boostExpl[i++] = Explanation.match(number.floatValue(), fieldName);
        }

        if (valSrcExpl != null) {
            return Explanation.match(valSrcExpl.getValue() * subQueryExpl.getValue() * boost,
                    "custom score: product of:",
                    subQueryExpl,
                    valSrcExpl,
                    Explanation.match(boost, "document field boost:", boostExpl));
        }
        return Explanation.match(subQueryExpl.getValue() * boost,
                "custom score: product of:",
                subQueryExpl,
                Explanation.match(boost, "document field boost:", boostExpl));
    }

}

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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.core.UnicodeWhitespaceTokenizer;

/**
 * User: iazarny@yahoo.com Igor Azarny
 * Date: 2/3/12
 * Time: 12:59 PM
 */
public class AsIsAnalyzer extends Analyzer {

    private final boolean toLowerCase;

    /**
     * Construct analyser.
     *
     * @param toLowerCase make terms lower case
     */
    public AsIsAnalyzer(boolean toLowerCase) {
        this.toLowerCase = toLowerCase;
    }


    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        if (toLowerCase) {
            final UnicodeWhitespaceTokenizer tokenizer = new UnicodeWhitespaceTokenizer();
            return new TokenStreamComponents(tokenizer, new LowerCaseFilter(tokenizer));
        }
        return new TokenStreamComponents(new UnicodeWhitespaceTokenizer());
    }
}

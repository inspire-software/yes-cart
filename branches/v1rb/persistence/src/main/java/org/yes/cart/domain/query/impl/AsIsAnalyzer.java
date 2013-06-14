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

package org.yes.cart.domain.query.impl;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.ReusableAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * User: iazarny@yahoo.com Igor Azarny
 * Date: 2/3/12
 * Time: 12:59 PM
 */
public class AsIsAnalyzer extends ReusableAnalyzerBase {

    private final boolean toLowerCase;

    /**
     * COnstruct analyser.
     * @param toLowerCase
     */
    public AsIsAnalyzer(boolean toLowerCase) {
        this.toLowerCase = toLowerCase;
    }

    /** {@inheritDoc} */
    @Override
    protected TokenStreamComponents createComponents(final String fieldName, final Reader aReader) {
        if (toLowerCase) {
            final Tokenizer asIsTokenizer = new AsIsTokenizer(Version.LUCENE_31, aReader);
            return new TokenStreamComponents(asIsTokenizer, new LowerCaseFilter(Version.LUCENE_31, asIsTokenizer));
        } else {
            return new TokenStreamComponents(new AsIsTokenizer(Version.LUCENE_31, aReader));
        }
    }

}

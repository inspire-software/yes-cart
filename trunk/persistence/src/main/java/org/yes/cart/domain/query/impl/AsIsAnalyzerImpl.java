
package org.yes.cart.domain.query.impl;

import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.Reader;

/**
*
 *
 * Simple analyzer to omit additional analysis on queries, that was composed for filtered navigation.
 * I.e.
 * <code>+productCategory.category:104 +attribute.attribute:BATTERY_TYPE +attribute.val:Litium</code>
 * will not be transformed into
 * <code>+productCategory.category:104 +attribute.attribute:"battery type" +attribute.val:litium</code>
 *
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 *
 *
 */
public class AsIsAnalyzerImpl extends StandardAnalyzer {


    public TokenStream tokenStream(final String fieldName, final Reader reader) {
        return new KeywordTokenizer(reader);
    }

}

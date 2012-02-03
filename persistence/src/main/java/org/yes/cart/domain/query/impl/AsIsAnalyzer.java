package org.yes.cart.domain.query.impl;

import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.ReusableAnalyzerBase;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * User: iazarny@yahoo.com Igor Azarny
 * Date: 2/3/12
 * Time: 12:59 PM
 */
public class AsIsAnalyzer extends ReusableAnalyzerBase {

    /** {@inheritDoc} */
    @Override
    protected TokenStreamComponents createComponents(final String fieldName, final Reader aReader) {
        return new TokenStreamComponents(new AsIsTokenizer(Version.LUCENE_31, aReader));
    }


}

package org.yes.cart.domain.query.impl;

import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * User: iazarny@yahoo.com Igor Azarny
 * Date: 2/3/12
 * Time: 12:53 PM
 */
public class AsIsTokenizer extends CharTokenizer {


    public AsIsTokenizer(Version matchVersion, Reader input) {
        super(matchVersion, input);
    }

    public AsIsTokenizer(Version matchVersion, AttributeSource source, Reader input) {
        super(matchVersion, source, input);
    }

    public AsIsTokenizer(Version matchVersion, AttributeFactory factory, Reader input) {
        super(matchVersion, factory, input);
    }

    /** {@inheritDoc}*/
    protected int normalize(int c) {
        return c;
    }

    /** {@inheritDoc}*/
    protected boolean isTokenChar(int c) {
        return true;
    }
}

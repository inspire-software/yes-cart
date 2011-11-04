package org.yes.cart.domain.query.impl;

import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class TestAsIsAnalyzerImpl {

    private static final String LUCENE_QUERY = "+productCategory.category:104 +attribute.attribute:BATTERY_TYPE +attribute.val:Litium";
    private static final String[] FIELDS = {"productCategory.category", "attribute.attribute", "attribute.val"};

    @Test
    public void testThatQueryIsNotTransformaedByAnalyzerWithMultipleFiledsQueryParser() {
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(FIELDS, new AsIsAnalyzerImpl());
        try {
            Query query = queryParser.parse(LUCENE_QUERY);
            assertEquals(LUCENE_QUERY, query.toString());
        } catch (ParseException e) {
            assertTrue(false);
        }
    }
}

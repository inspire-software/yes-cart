package org.yes.cart.domain.query.impl;

import junit.framework.TestCase;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 08-May-2011
 * Time: 11:12:54
 */
public class TestAsIsAnalyzerImpl extends TestCase {

     private final String luceneQuery = "+productCategory.category:104 +attribute.attribute:BATTERY_TYPE +attribute.val:Litium";
     private final String [] fields = {"productCategory.category" , "attribute.attribute", "attribute.val"};

    /**
     * TEst, that query not transformed by analyzer.
     */
     public void testWithMultipleFiledsQueryParser() {
         MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, new AsIsAnalyzerImpl());
         try {
             Query query = queryParser.parse(luceneQuery);
             assertEquals(luceneQuery, query.toString());
         } catch (ParseException e) {
             assertTrue(false);
         }

     }

}

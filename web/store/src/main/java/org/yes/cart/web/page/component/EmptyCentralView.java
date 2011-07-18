package org.yes.cart.web.page.component;

import org.apache.lucene.search.BooleanQuery;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:04 AM
 */
public class EmptyCentralView extends AbstractCentralView {


    /**
      * Construct panel.
     *
     * @param id panel id
     * @param categoryId  current category id.
     * @param booleanQuery     boolean query.
     */
    public EmptyCentralView(String id, long categoryId, BooleanQuery booleanQuery) {
        super(id, categoryId, booleanQuery);
    }


}

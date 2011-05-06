package org.yes.cart.dao;

import org.hibernate.Criteria;

/**
 *
 * Call back interface to tune default criteria. Use to
 * override default maping behavior.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 *
 */
public interface CriteriaTuner {

    /**
     * Tune given criteria with additional restriction on higher layer.
     * @param crit criteria to tune.
     */
    void tune(Criteria crit);

}

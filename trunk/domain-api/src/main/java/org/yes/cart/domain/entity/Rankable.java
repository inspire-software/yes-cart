package org.yes.cart.domain.entity;

/**
 *
 * Represent object, that can be ranked.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Rankable {

    /**
     * Get the object rank.
     * @return object rank
     */
    int getRank();

    /**
     * Set the object rank.
     * @param rank to set.
     */
    void setRank(final int rank);

}

package org.yes.cart.domain.misc;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 *
 * @param <FIRST>  the type of the first object
 * @param <SECOND> the type of the second object
 */
public class Pair<FIRST, SECOND> implements Serializable {

    private static final long serialVersionUID = 20100711L;

    private final FIRST first;
    private final SECOND second;

    public Pair(final FIRST first, final SECOND second) {
        this.first = first;
        this.second = second;
    }

    public FIRST getFirst() {
        return first;
    }

    public SECOND getSecond() {
        return second;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Pair pair = (Pair) obj;

        if (!first.equals(pair.first)) {
            return false;
        }
        if (!second.equals(pair.second)) {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return MessageFormat.format("Pair first = [{0}] second = [{1}]", first, second);
    }
}

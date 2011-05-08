package org.yes.cart.domain.queryobject.impl;

import org.yes.cart.domain.queryobject.FiteredNavigationRecord;

import java.io.Serializable;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public class FiteredNavigationRecordImpl implements FiteredNavigationRecord, Serializable {

    private String name;

    private String code;

    private String value;

    private int count;

    private int rank;

    private String type;

    /**
     * {@inheritDoc
     */
    public String getCode() {
        return code;
    }

    /**
     * {@inheritDoc
     */
    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * {@inheritDoc
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc
     */
    public int getCount() {
        return count;
    }

    /**
     * {@inheritDoc
     */
    public void setCount(final int itemsCount) {
        this.count = itemsCount;
    }

    /**
     * {@inheritDoc
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc
     */
    public int getRank() {
        return rank;
    }

    /**
     * {@inheritDoc
     */
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /**
     * {@inheritDoc
     */
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Construct filtered navigation record.
     *
     * @param name  attribute nave
     * @param code  attribute code
     * @param value value
     * @param count count of objects.
     */
    public FiteredNavigationRecordImpl(final String name, final String code, final String value, final int count) {
        this.name = name;
        this.code = code;
        this.value = value;
        this.count = count;
    }

    /**
     * Construct filtered navigation record.
     *
     * @param name  attribute nave
     * @param code  attribute code
     * @param value value
     * @param count count of objects.
     * @param rank  rank
     */
    public FiteredNavigationRecordImpl(final String name, final String code, final String value, final int count, final int rank) {
        this.name = name;
        this.code = code;
        this.value = value;
        this.count = count;
        this.rank = rank;
    }

    /**
     * Construct filtered navigation record.
     *
     * @param name  attribute nave
     * @param code  attribute code
     * @param value value
     * @param count count of objects.
     * @param rank  rank
     * @param type  type of navigation S - single value R - range value
     */
    public FiteredNavigationRecordImpl(final String name, final String code, final String value,
                                       final int count, final int rank, final String type) {
        this.name = name;
        this.code = code;
        this.value = value;
        this.count = count;
        this.rank = rank;
        this.type = type;
    }


}

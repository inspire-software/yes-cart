package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.service.domain.GenericService;

import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class BaseGenericServiceImpl<ENTITY> implements GenericService<ENTITY> {

    private final GenericDAO<ENTITY, Long> genericDao;

    public BaseGenericServiceImpl(final GenericDAO<ENTITY, Long> genericDao) {
        this.genericDao = genericDao;
    }

    /**
     * {@inheritDoc}
     */
    public List<ENTITY> findAll() {
        return genericDao.findAll();
    }

    /**
     * {@inheritDoc}
     */
    public ENTITY getById(final long pk) {
        return genericDao.findById(pk);
    }


    /**
     * {@inheritDoc}
     */
    public ENTITY create(final ENTITY instance) {
        return genericDao.create(instance);
    }


    /**
     * {@inheritDoc}
     */
    public ENTITY update(final ENTITY instance) {
        return genericDao.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final ENTITY instance) {
        genericDao.delete(instance);
    }

    /**
     * Get generic dao
     *
     * @return generic dao
     */
    public GenericDAO<ENTITY, Long> getGenericDao() {
        return genericDao;
    }

    /**
     * {@inheritDoc}
     */
    public List<ENTITY> findByCriteria(final Criterion... criterion) {
        return genericDao.findByCriteria(criterion);

    }

    /**
     * {@inheritDoc}
     */
    public ENTITY findSingleByCriteria(Criterion... criterion) {
        return genericDao.findSingleByCriteria(criterion);

    }

    /**
     * Get null if string is empty.
     *
     * @param str given string
     * @return string if it not empty, otherwise null
     */
    protected String nullIfEmpty(final String str) {
        return StringUtils.defaultIfEmpty(str, null);
    }

    /**
     * Create like value.
     *
     * @param str given string
     * @return string value, that used in like operation if given string not null, otherwise null.
     */
    protected String likeValue(final String str) {
        if (StringUtils.isNotBlank(str)) {
            return "%" + str + "%";
        }
        return null;
    }


}

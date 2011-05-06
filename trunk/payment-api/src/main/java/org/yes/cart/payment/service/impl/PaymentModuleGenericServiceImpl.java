package org.yes.cart.payment.service.impl;

import org.hibernate.criterion.Criterion;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;
import org.yes.cart.payment.service.PaymentModuleGenericService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public class PaymentModuleGenericServiceImpl<ENTITY> implements PaymentModuleGenericService<ENTITY> {

    private final PaymentModuleGenericDAO<ENTITY, Long> genericDao;

    public PaymentModuleGenericServiceImpl(final PaymentModuleGenericDAO<ENTITY, Long> genericDao) {
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
    public PaymentModuleGenericDAO<ENTITY, Long> getGenericDao() {
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


}

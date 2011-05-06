package org.yes.cart.payment.service.impl;

import org.hibernate.criterion.Restrictions;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.persistence.service.PaymentModuleGenericDAO;
import org.yes.cart.payment.service.PaymentGatewayParameterService;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public class PaymentGatewayParameterServiceImpl
        extends PaymentModuleGenericServiceImpl<PaymentGatewayParameter>
        implements PaymentGatewayParameterService {

    /**
     * Construct service to work with pg parameters.
     *
     * @param genericDao dao to use.
     */
    public PaymentGatewayParameterServiceImpl(final PaymentModuleGenericDAO<PaymentGatewayParameter, Long> genericDao) {
        super(genericDao);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteByLabel(final String paymentGatewayLabel, final String parameterLabel) {
        PaymentGatewayParameter toDelete = getGenericDao().findSingleByCriteria(
                Restrictions.eq("pgLabel", paymentGatewayLabel),
                Restrictions.eq("label", parameterLabel)

        );
        if (toDelete != null) {
            delete(toDelete);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<PaymentGatewayParameter> findAll(String label) {
        return getGenericDao().findByCriteria(Restrictions.eq("pgLabel", label));
    }
}

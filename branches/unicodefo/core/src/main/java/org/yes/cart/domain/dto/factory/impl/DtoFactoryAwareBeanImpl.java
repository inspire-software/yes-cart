package org.yes.cart.domain.dto.factory.impl;

import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.factory.DtoFactoryAwareBean;

/**
 * Default implementation of {@link org.yes.cart.domain.dto.factory.DtoFactoryAwareBean}
 * to enable DTO bean factory injection to the complex DTO's
 * in order to undertake complex nested DTO creation..
 * <p/>
 * User: dogma
 * Date: Jan 23, 2011
 * Time: 12:10:56 PM
 */
public class DtoFactoryAwareBeanImpl implements DtoFactoryAwareBean {

    private final DtoFactory dtoFactory;

    /**
     * IoC constructor.
     *
     * @param dtoFactory DTO factory
     */
    public DtoFactoryAwareBeanImpl(final DtoFactory dtoFactory) {
        this.dtoFactory = dtoFactory;
    }

    /** {@inheritDoc} */
    public DtoFactory getFactory() {
        return dtoFactory;
    }
}

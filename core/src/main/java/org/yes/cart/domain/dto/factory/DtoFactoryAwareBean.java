package org.yes.cart.domain.dto.factory;

/**
 * Interface to enable DTO bean factory injection to the complex DTO's
 * in order to undertake complex nested DTO creation.
 * <p/>
 * User: dogma
 * Date: Jan 23, 2011
 * Time: 12:08:16 PM
 */
public interface DtoFactoryAwareBean {

    /**
     * @return DTO Factory that known the interface to class mappings.
     */
    DtoFactory getFactory();

}

package org.yes.cart.exception;

/**
 * Exception to be thrown when a domain object cannot be wrapped to DTO.
 * <p/>
 * User: dogma
 * Date: Jan 24, 2011
 * Time: 1:33:31 PM
 */
public class UnableToWrapObjectException extends Exception {

    private static final long serialVersionUID = 20100124L;

    /**
     * Default constructor.
     *
     * @param domainClass domain class to wrap
     * @param dtoClass    dto wrapper
     * @param cause       the cause of failure
     */
    public UnableToWrapObjectException(final Class<?> domainClass,
                                       final Class<?> dtoClass,
                                       final Throwable cause) {
        super(generateMessage(domainClass, dtoClass, cause));

    }

    private static String generateMessage(final Class<?> domainClass,
                                          final Class<?> dtoClass,
                                          final Throwable cause) {
        return new StringBuilder()
                .append("Unable to wrap ")
                .append(domainClass.getCanonicalName())
                .append(" to ")
                .append(dtoClass.getCanonicalName())
                .append(" due to: ")
                .append(cause.getMessage()).toString();
    }

}

package org.yes.cart.exception;

/**
 * Exception thrown in case if object could not be retrieved from persistance layer.
 * <p/>
 * User: dogma
 * Date: Jan 24, 2011
 * Time: 12:44:03 PM
 */
public class ObjectNotFoundException extends Exception {

    private static final long serialVersionUID = 20100124L;

    /**
     * Default constructor.
     *
     * @param message to explain which object was not found
     */
    public ObjectNotFoundException(final String message) {
        super(message);
    }

    /**
     * Recommended constructor.
     *
     * @param clazz      class of domain object
     * @param parameters parameters used to retrieve object. These are key-value pairs
     *                   used as follows key1, value1, key2, value2 ... keyN, valueN.
     */
    public ObjectNotFoundException(final Class<?> clazz, final Object... parameters) {
        this(generateMessage(clazz, parameters));
    }


    private static String generateMessage(final Class<?> clazz, final Object... parameters) {
        final StringBuilder msg = new StringBuilder("Object for class: " + clazz.getCanonicalName() + " cannot be found. Parameters : {");
        if (parameters != null) {
            for (int index = 0; index < parameters.length; index += 2) {
                msg.append(parameters[index]);
                msg.append('=');
                msg.append(parameters[index + 1]);
                msg.append(';');
            }
        }
        msg.append('}');
        return msg.toString();
    }
}

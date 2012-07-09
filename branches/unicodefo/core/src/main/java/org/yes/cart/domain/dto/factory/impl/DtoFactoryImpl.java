package org.yes.cart.domain.dto.factory.impl;

import org.yes.cart.domain.dto.factory.DtoFactory;

import java.util.Map;

/**
 * Basic Bean Factory with an interface to class name mapping.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 11:33:13 PM
 */
public class DtoFactoryImpl implements DtoFactory {

    private static final long serialVersionUID = 20100122L;

    private final Map<String, String> classMap;

    public DtoFactoryImpl(final Map<String, String> classMap) {
        this.classMap = classMap;
    }

    public Object get(final String entityBeanKey) {
        return getByKey(entityBeanKey);
    }

    /** {@inheritDoc} */
    public Class getImplClass(final Class interfaceClass) {
        final String ifaceName = interfaceClass.getCanonicalName();
        return getImplClass(ifaceName);
    }

    /** {@inheritDoc} */
    public Class getImplClass(final String ifaceName) {
        final String className = classMap.get(ifaceName);
        if (className != null) {
            try {
                return Class.forName(className);
            } catch (Exception e) {
                throw new InstantiationError("Cant getByKey class for interface " + ifaceName
                        + " class " + className
                        + ". Original error is: " + e.getMessage());
            }
        }
        throw new InstantiationError("Cant getByKey class for interface " + ifaceName);
    }

    /** {@inheritDoc} */
    public <T> T getByKey(final String entityBeanKey) {
        return (T) getInternal(entityBeanKey);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public <T> T getByIface(final Class iface) {

        return (T) getInternal(iface.getCanonicalName());
    }

    @SuppressWarnings("unchecked")
    private <T> T getInternal(final String entityBeanKey) {
        if (classMap.containsKey(entityBeanKey)) {
            final String clazz = classMap.get(entityBeanKey);
            try {
               return (T) Class.forName(clazz).newInstance();
            } catch (Exception exp) {
                throw new InstantiationError("Class name not found " + entityBeanKey);
            }
        }
        throw new InstantiationError("Unmapped " + entityBeanKey);
    }
}

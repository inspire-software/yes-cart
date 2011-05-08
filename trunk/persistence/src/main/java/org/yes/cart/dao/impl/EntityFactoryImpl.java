package org.yes.cart.dao.impl;

import org.yes.cart.dao.EntityFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class EntityFactoryImpl implements EntityFactory {


    private Map<String, String> classNamesMap = new HashMap<String, String>();

    /** {@inheritDoc} */
    public Class getImplClass(final Class interfaceClass) {
        final String ifaceName = interfaceClass.getCanonicalName();
        return getImplClass(ifaceName);
    }

    /** {@inheritDoc} */
    public Class getImplClass(final String ifaceName) {
        final String className = classNamesMap.get(ifaceName);
        if (className != null) {
            try {
                return Class.forName(className);
            } catch (Exception e) {
                throw new InstantiationError("Cant getByKey class for interface " + ifaceName + " class " + className);
            }
        }
        throw new InstantiationError("Cant getByKey class for interface " + ifaceName);
    }

    /** {@inheritDoc} */
    public <T> T getByKey(final String ifaceName) {
        final String className = classNamesMap.get(ifaceName);
        if (className != null) {
            try {
                return (T) Class.forName(className).newInstance();
            } catch (Exception e) {
                throw new InstantiationError("Cant create instance of " + className + " reason " + e.getMessage());
            }
        }
        throw new InstantiationError("Class instance not found for " + className + ". Interface " + ifaceName);

    }

    /** {@inheritDoc} */
    public <T> T getByIface(final Class interfaceClass) {
        return (T) getByKey(interfaceClass.getName());
    }

    /**
     * IoC.
     * @param classNamesMap map of interface and his implementation class names
     */
    public void setClassNamesMap(Map<String, String> classNamesMap) {
        this.classNamesMap = classNamesMap;
    }
}

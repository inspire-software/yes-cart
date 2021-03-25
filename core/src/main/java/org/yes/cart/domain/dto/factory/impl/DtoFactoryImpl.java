/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.domain.dto.factory.impl;

import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.utils.spring.LinkedHashMapBean;

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

    public DtoFactoryImpl(final LinkedHashMapBean<String, String> classMap) {
        this.classMap = classMap;
    }

    /** {@inheritDoc} */
    @Override
    public Class getClazz(final String entityBeanKey) {
        return getImplClass(entityBeanKey);
    }


    /** {@inheritDoc} */
    @Override
    public Object get(final String entityBeanKey) {
        return getByKey(entityBeanKey);
    }

    /** {@inheritDoc} */
    @Override
    public Class getImplClass(final Class interfaceClass) {
        final String ifaceName = interfaceClass.getCanonicalName();
        return getImplClass(ifaceName);
    }

    /** {@inheritDoc} */
    @Override
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
    @Override
    public <T> T getByKey(final String entityBeanKey) {
        return (T) getInternal(entityBeanKey);
    }

    /** {@inheritDoc} */
    @Override
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

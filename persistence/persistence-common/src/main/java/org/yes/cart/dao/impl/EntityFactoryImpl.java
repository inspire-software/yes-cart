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

package org.yes.cart.dao.impl;

import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.domain.entity.Seoable;
import org.yes.cart.utils.spring.LinkedHashMapBean;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public class EntityFactoryImpl implements EntityFactory {


    private Map<String, String> classNamesMap = new HashMap<>();

    /** {@inheritDoc} */
    @Override
    public Class getImplClass(final Class interfaceClass) {
        final String ifaceName = interfaceClass.getCanonicalName();
        return getImplClass(ifaceName);
    }

    /** {@inheritDoc} */
    @Override
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
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getByKey(final String ifaceName) {
        final String className = classNamesMap.get(ifaceName);
        if (className != null) {
            try {
                T entity = (T) Class.forName(className).newInstance();
                if (entity instanceof Seoable) {
                    ((Seoable)entity).setSeo((Seo) Class.forName(classNamesMap.get(Seo.class.getName())).newInstance());
                }
                return entity;
            } catch (Exception e) {
                throw new InstantiationError("Cant create instance of " + className + " reason " + e.getMessage());
            }
        }
        throw new InstantiationError("Class instance not found for [null]. Interface " + ifaceName);

    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getByIface(final Class interfaceClass) {
        return (T) getByKey(interfaceClass.getName());
    }

    /**
     * IoC.
     * @param classNamesMap map of interface and his implementation class names
     */
    public void setClassNamesMap(LinkedHashMapBean<String, String> classNamesMap) {
        this.classNamesMap = classNamesMap;
    }
}

/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.domain.dto.adapter.impl;

import com.inspiresoftware.lib.dto.geda.adapter.EntityRetriever;
import org.yes.cart.dao.GenericDAO;

import java.text.MessageFormat;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class GenericDtoToEntityConverter<T> implements EntityRetriever {

    private final GenericDAO<T, Long> genericDAO;

    /**
     * Construct generic dto to entity converter.
     * @param genericDAO generic dao to getByKey entity by pk value from dto.
     */
    public GenericDtoToEntityConverter(final GenericDAO<T, Long> genericDAO) {
        this.genericDAO = genericDAO;
    }

    public Object retrieveByPrimaryKey(final Class entityInterface, final Class entityClass, final Object primaryKey) {
        if (primaryKey != null) {
            try {
                return genericDAO.findById((Long) primaryKey);
            } catch (Exception e) {
                throw new RuntimeException(
                        MessageFormat.format(
                                "Cannot getByKey entity {0} [{1}] for given primary key {2}",
                                entityClass.getSimpleName(), entityInterface.getSimpleName(), primaryKey
                        ), e);
            }
        }
        return null;
    }

}

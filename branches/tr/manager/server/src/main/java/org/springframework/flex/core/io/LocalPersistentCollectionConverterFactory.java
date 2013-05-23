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

package org.springframework.flex.core.io;

/**
 *
 * Quick local fix of https://jira.springsource.org/browse/FLEX-219
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 16 - apr 12
 * Time: 11:35 PM
 */

import org.hibernate.Hibernate;
import org.hibernate.collection.spi.PersistentCollection;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * {@link ConverterFactory} implementation that supplies a {@link Converter} instance that can convert {@link PersistentCollection}
 * instances from Hibernate.  Given a specific {@code PersistentCollection} instance, the converter will:
 *
 * <ul>
 *     <li>Convert to null if the {@code PersistentCollection} instance is uninitialized</li>
 *     <li>Convert to the wrapped collection class if the {@code PersistentCollection} is initialized</li>
 * </ul>
 *
 * @author Jeremy Grelle
 */
public class LocalPersistentCollectionConverterFactory implements ConverterFactory<PersistentCollection, Object> {

    public <T> Converter<PersistentCollection, T> getConverter(Class<T> targetType) {
        return new Converter<PersistentCollection, T>() {
            @SuppressWarnings("unchecked")
            public T convert(PersistentCollection source) {
                if (!Hibernate.isInitialized(source)) {
                    return null;
                } else {
                    return (T) source.getValue();
                }
            }
        };
    }

}

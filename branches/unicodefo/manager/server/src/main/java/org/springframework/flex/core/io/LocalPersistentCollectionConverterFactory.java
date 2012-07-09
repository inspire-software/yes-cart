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

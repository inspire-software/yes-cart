package org.springframework.flex.core.io;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.springframework.core.convert.converter.ConverterRegistry;

import java.util.HashSet;
import java.util.Set;

/**
 * Quick local fix of https://jira.springsource.org/browse/FLEX-219
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 16 - apr- 12
 * Time: 8:19 AM
 */
public class LocalJpaHibernateConfigProcessor extends JpaHibernateConfigProcessor {


    private Set<ClassMetadata> classMetadata = new HashSet<ClassMetadata>();

    private Set<CollectionMetadata> collectionMetadata = new HashSet<CollectionMetadata>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureConverters(ConverterRegistry registry) {
        registry.addConverter(new HibernateProxyConverter());
        registry.addConverterFactory(new LocalPersistentCollectionConverterFactory());
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    protected Set<Class<?>> findTypesToRegister() {
        Set<Class<?>> typesToRegister = new HashSet<Class<?>>();
        if (hibernateConfigured) {
            for(ClassMetadata classMetadata : this.classMetadata) {
                if (!classMetadata.getMappedClass().isInterface()) {
                    typesToRegister.add(classMetadata.getMappedClass());
                    findComponentProperties(classMetadata.getPropertyTypes(), typesToRegister);
                }
            }
            for (CollectionMetadata collectionMetadata : this.collectionMetadata) {
                Type elementType = collectionMetadata.getElementType();
                if (elementType instanceof ComponentType) {
                    if (!elementType.getReturnedClass().isInterface()) {
                        typesToRegister.add(elementType.getReturnedClass());
                        findComponentProperties(((ComponentType)elementType).getSubtypes(), typesToRegister);
                    }
                }
            }
        }
        return typesToRegister;
    }


    /**
     * Extracts all {@link ClassMetadata} and {@link CollectionMetadata} from a given {@link org.hibernate.SessionFactory} to be
     * used in determining the types that need a {@link SpringPropertyProxy} registered in {@link #findTypesToRegister()}
     * @param sessionFactory the session factory from which to read metadata
     */
    @SuppressWarnings("unchecked")
    protected void extractHibernateMetadata(SessionFactory sessionFactory) {
        this.classMetadata.addAll(sessionFactory.getAllClassMetadata().values());
        this.collectionMetadata.addAll(sessionFactory.getAllCollectionMetadata().values());
        this.hibernateConfigured = true;
    }


    private void findComponentProperties(Type[] propertyTypes, Set<Class<?>> typesToRegister) {
        if (propertyTypes == null) {
            return;
        }
        for (Type propertyType : propertyTypes) {
            if (propertyType instanceof ComponentType) {
                typesToRegister.add(propertyType.getReturnedClass());
                findComponentProperties(((ComponentType)propertyType).getSubtypes(), typesToRegister);
            }
        }
    }



}

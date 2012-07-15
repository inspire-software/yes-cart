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

package org.yes.cart.domain.interceptor;

import org.apache.commons.lang.StringUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.yes.cart.domain.entity.Auditable;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entityindexer.ProductIndexer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 * <p/>
 * Audit interseptor for entities.
 */
public class AuditInterceptor extends EmptyInterceptor implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private ProductIndexer productIndexer;


    private synchronized ProductIndexer getProductIndexer() {
        if (productIndexer == null) {
            productIndexer = applicationContext.getBean("productReindexer", ProductIndexer.class);
        }
        return productIndexer;
    }

    /**
     * Get product id from entities in product object graph, obtained product id will be used for
     * product reindex.
     * @param entity intyty to check.
     * @return zero in case not need to perform product reindex.
     */
    long getProductId(final Object entity) {

        if (entity instanceof ProductCategory) {

            return ((ProductCategory) entity).getProduct().getId();

        } else if (entity instanceof ProductSku) {

            return ((ProductSku) entity).getProduct().getId();

        } else if (entity instanceof SkuWarehouse) {

            return ((SkuWarehouse) entity).getSku().getProduct().getId();

        }

        return 0;
    }

    private void submitProductReindex(final long productId) {

        if(productId > 0) {

            getProductIndexer().submitIndexTask(productId);

        }

    }


    private String getUserName() {

        if (SecurityContextHolder.getContext() != null) {

            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null) {

                if (auth.getPrincipal() instanceof User) {

                    return ((User) auth.getPrincipal()).getUsername();

                }

            }

        }

        return "user unknown";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onSave(final Object entity, final Serializable serializable,
                          final Object[] objects, final String[] propertyNames, final Type[] types) {
        if (entity instanceof Auditable) {
            final Auditable auditable = ((Auditable) entity);

            final Date date = new Date();
            final String userName = getUserName();

            if (StringUtils.isBlank(auditable.getGuid())) {
                final String guid = java.util.UUID.randomUUID().toString();
                setValue(objects, propertyNames, "guid", guid);
                auditable.setGuid(guid);
            }

            setValue(objects, propertyNames, "createdBy", userName);
            auditable.setCreatedBy(userName);
            setValue(objects, propertyNames, "createdTimestamp", date);
            auditable.setCreatedTimestamp(date);

            setValue(objects, propertyNames, "updatedBy", userName);
            auditable.setUpdatedBy(userName);
            setValue(objects, propertyNames, "updatedTimestamp", date);
            auditable.setUpdatedTimestamp(date);
        }

        submitProductReindex(getProductId(entity));

        return super.onSave(entity, serializable, objects, propertyNames, types);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        submitProductReindex(getProductId(entity));

        super.onDelete(entity, id, state, propertyNames, types);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onFlushDirty(final Object entity, final Serializable serializable, final Object[] currentState,
                                final Object[] previousState, final String[] propertyNames, final Type[] types) {
        if (entity instanceof Auditable) {
            final Auditable auditable = (Auditable) entity;

            final Date date = new Date();
            final String userName = getUserName();


            if (auditable.getCreatedTimestamp() == null) {
                setValue(currentState, propertyNames, "createdBy", userName);
                auditable.setCreatedBy(userName);
                setValue(currentState, propertyNames, "createdTimestamp", date);
                auditable.setCreatedTimestamp(date);
            }

            if (StringUtils.isBlank(((Auditable) entity).getGuid())) {
                final String guid = java.util.UUID.randomUUID().toString();
                setValue(currentState, propertyNames, "guid", guid);
                auditable.setGuid(guid);
            }


            setValue(currentState, propertyNames, "updatedBy", userName);
            auditable.setUpdatedBy(userName);
            setValue(currentState, propertyNames, "updatedTimestamp", date);
            auditable.setUpdatedTimestamp(date);

        }

        submitProductReindex(getProductId(entity));

        return super.onFlushDirty(entity, serializable, currentState, previousState, propertyNames, types);

    }


    private void setValue(final Object[] currentState, final String[] propertyNames,
                          final String propertyToSet, final Object value) {
        int index = Arrays.asList(propertyNames).indexOf(propertyToSet);
        if (index > -1) {
            currentState[index] = value;
        }
    }


    /** {@inheritDoc} */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

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

import org.hibernate.type.Type;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entityindexer.ProductIndexer;

import java.io.Serializable;

/**
 * Storefront indexer enhances auditing interceptor by adding FT indexing capabilities to
 * track when product index updates are necessary.
 *
 * User: denispavlov
 * Date: 13-10-03
 * Time: 8:10 AM
 */
public class StorefrontInterceptor extends AuditInterceptor implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private ProductIndexer productIndexer;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onSave(final Object entity, final Serializable serializable,
                          final Object[] objects, final String[] propertyNames, final Type[] types) {

        final boolean onSave = super.onSave(entity, serializable, objects, propertyNames, types);

        submitProductReindex(getProductId(entity));

        return onSave;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        super.onDelete(entity, id, state, propertyNames, types);

        submitProductReindex(getProductId(entity));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onFlushDirty(final Object entity, final Serializable serializable, final Object[] currentState,
                                final Object[] previousState, final String[] propertyNames, final Type[] types) {

        final boolean onFlush = super.onFlushDirty(entity, serializable, currentState, previousState, propertyNames, types);

        submitProductReindex(getProductId(entity));

        return onFlush;

    }

    /**
     * Get product id from entities in product object graph, obtained product id will be used for
     * product reindex.
     *
     * @param entity entity to check.
     * @return zero in case not need to perform product reindex.
     */
    long getProductId(final Object entity) {

        if (entity instanceof ProductCategory) {

            return ((ProductCategory) entity).getProduct().getProductId();

        } else if (entity instanceof ProductSku) {

            if (((ProductSku) entity).getProduct() != null) {

                return ((ProductSku) entity).getProduct().getProductId();
            }

        } else if (entity instanceof SkuWarehouse) {

            return ((SkuWarehouse) entity).getSku().getProduct().getProductId();

        }

        return 0;
    }

    private void submitProductReindex(final long productId) {

        if (productId > 0) {

            if (productIndexer == null) {
                synchronized (this) {
                    if (productIndexer == null) {
                        productIndexer = applicationContext.getBean("productReindexer", ProductIndexer.class);
                    }
                }
            }

            productIndexer.submitIndexTask(productId);

        }

    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}

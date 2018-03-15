/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.hibernate.Hibernate;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueWithAttribute;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.ro.AttributeRO;

/**
 * User: denispavlov
 * Date: 21/07/2017
 * Time: 12:40
 */
public class AttributeFromCodeValueConverter implements ValueConverter {

    private final TransactionTemplate transactionTemplate;
    private final GenericDAO<Object, Long> genericDAO;
    private final Cache attributeCache;

    public AttributeFromCodeValueConverter(final GenericDAO<Object, Long> genericDAO,
                                           final PlatformTransactionManager transactionManager,
                                           final CacheManager cacheManager) {
        this.genericDAO = genericDAO;
        this.attributeCache = cacheManager.getCache("attributeService-byAttributeCode");
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        if (object instanceof AttrValueWithAttribute) {
            final Attribute attr = ((AttrValueWithAttribute) object).getAttribute();
            if (attr != null) {
                final AttributeRO ro = new AttributeRO();
                final Assembler asm = DTOAssembler.newAssembler(ro.getClass(), Attribute.class);
                asm.assembleDto(ro, attr, getAdapters().getAll(), beanFactory);
                return ro;
            }
        }
        if (object instanceof AttrValue) {

            final AttrValue av = (AttrValue) object;
            final String code = av.getAttributeCode();
            Attribute attr = null;
            final Cache.ValueWrapper val = this.attributeCache.get(code);
            if (val != null) {
                attr = (Attribute) val.get();
            }

            if (attr == null) {

                attr = this.transactionTemplate.execute(status -> {
                    final Attribute attribute = (Attribute) genericDAO.findSingleByNamedQuery("ATTRIBUTE.BY.CODE", code);
                    if (attribute != null) {
                        Hibernate.initialize(attribute.getEtype());
                    }
                    return attribute;
                });

            }

            if (attr != null) {

                final AttributeRO ro = new AttributeRO();
                final Assembler asm = DTOAssembler.newAssembler(ro.getClass(), Attribute.class);
                asm.assembleDto(ro, attr, getAdapters().getAll(), beanFactory);
                return ro;

            } else {

                final AttributeRO ro = new AttributeRO();
                ro.setCode(code);
                ro.setName(code);
                return ro;

            }

        }

        return null;
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {

        if (object instanceof AttributeRO) {

            final String code = ((AttributeRO) object).getCode();
            ((AttrValue) oldEntity).setAttributeCode(code);
            return code;

        }

        return null;

    }

    /**
     * Spring IoC.
     *
     * @return adapters
     */
    public AdaptersRepository getAdapters() {
        return null;
    }

}

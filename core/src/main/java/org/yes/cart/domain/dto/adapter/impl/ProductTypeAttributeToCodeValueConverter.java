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

package org.yes.cart.domain.dto.adapter.impl;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.hibernate.Hibernate;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueWithAttribute;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.ProductTypeAttr;

/**
 * Date: 17/09/2020
 * Time: 12:40
 */
public class ProductTypeAttributeToCodeValueConverter implements ValueConverter {

    private final GenericDAO<Object, Long> genericDAO;
    private final Cache attributeCache;

    public ProductTypeAttributeToCodeValueConverter(final GenericDAO<Object, Long> genericDAO,
                                                    final CacheManager cacheManager) {
        this.genericDAO = genericDAO;
        this.attributeCache = cacheManager.getCache("attributeService-byAttributeCode");
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        if (object instanceof ProductTypeAttr) {

            final ProductTypeAttr av = (ProductTypeAttr) object;
            final String code = av.getAttributeCode();
            Attribute attr = null;
            final Cache.ValueWrapper val = this.attributeCache.get(code);
            if (val != null) {
                attr = (Attribute) val.get();
            }

            if (attr == null) {
                attr = (Attribute) this.genericDAO.findSingleByNamedQuery("ATTRIBUTE.BY.CODE", code);
                if (attr != null) {
                    Hibernate.initialize(attr.getEtype());
                }
            }

            if (attr != null) {

                final AttributeDTO dto = (AttributeDTO) beanFactory.get("org.yes.cart.domain.dto.AttributeDTO");
                final Assembler asm = DTOAssembler.newAssembler(dto.getClass(), Attribute.class);
                asm.assembleDto(dto, attr, getAdapters().getAll(), beanFactory);
                return dto;

            } else {

                final AttributeDTO dto = (AttributeDTO) beanFactory.get("org.yes.cart.domain.dto.AttributeDTO");
                dto.setCode(code);
                dto.setName(code);
                return dto;

            }

        }

        return null;
    }

    /** {@inheritDoc}*/
    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {

        if (object instanceof AttributeDTO) {

            final String code = ((AttributeDTO) object).getCode();
            ((ProductTypeAttr) oldEntity).setAttributeCode(code);
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

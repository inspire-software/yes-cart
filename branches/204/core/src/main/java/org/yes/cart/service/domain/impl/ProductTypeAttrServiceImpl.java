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

package org.yes.cart.service.domain.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ProductTypeAttr;
import org.yes.cart.service.domain.ProductTypeAttrService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductTypeAttrServiceImpl extends BaseGenericServiceImpl<ProductTypeAttr> implements ProductTypeAttrService {

    /**
     * Construct service.
     * @param genericDao product type attribute dao to use.
     */
    public ProductTypeAttrServiceImpl(final GenericDAO<ProductTypeAttr, Long> genericDao) {
        super(genericDao);
    }


    /** {@inheritDoc} */
    @Cacheable(value = "productTypeAttrService-byProductTypeId")
    public List<ProductTypeAttr> getByProductTypeId(final long productTypeId) { 
        return getGenericDao().findByNamedQuery("PRODUCT.TYPE.ATTR.BY.PROD.TYPE.ID", productTypeId);
    }

    /** {@inheritDoc}*/
    @CacheEvict(value ={ "productTypeAttrService-byProductTypeId" } , allEntries = true )
    public ProductTypeAttr create(ProductTypeAttr instance) {
        return super.create(instance);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /** {@inheritDoc}*/
    @CacheEvict(value ={ "productTypeAttrService-byProductTypeId" } , allEntries = true )
    public ProductTypeAttr update(ProductTypeAttr instance) {
        return super.update(instance);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /** {@inheritDoc}*/
    @CacheEvict(value ={ "productTypeAttrService-byProductTypeId" } , allEntries = true )
    public void delete(ProductTypeAttr instance) {
        super.delete(instance);    //To change body of overridden methods use File | Settings | File Templates.
    }
}

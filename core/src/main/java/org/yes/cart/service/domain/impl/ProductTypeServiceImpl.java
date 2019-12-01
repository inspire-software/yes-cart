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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ProductTypeService;
import org.yes.cart.utils.HQLUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductTypeServiceImpl extends BaseGenericServiceImpl<ProductType> implements ProductTypeService {

    /**
     * Construct service.
     * @param productTypeDao dao to use 
     */
    public ProductTypeServiceImpl(final GenericDAO<ProductType, Long> productTypeDao) {
        super(productTypeDao);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductType> findByAttributeCode(final String code) {
        return getGenericDao().findByNamedQuery("PRODUCT.TYPE.BY.ATTRIBUTE.CODE", code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductType> findAllAssignedToCategories() {
        return getGenericDao().findByNamedQuery("PRODUCT.TYPES.ASSIGNED.TO.CATEGORIES");
    }


    private Pair<String, Object[]> findProductTypeQuery(final boolean count,
                                                        final String sort,
                                                        final boolean sortDescending,
                                                        final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(p) from ProductTypeEntity p ");
        } else {
            hqlCriteria.append("select p from ProductTypeEntity p ");
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "p", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by p." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }



    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductType> findProductTypes(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findProductTypeQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findProductTypeCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findProductTypeQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }
}

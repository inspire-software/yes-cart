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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.BrandService;
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
public class BrandServiceImpl extends BaseGenericServiceImpl<Brand> implements BrandService {

    /**
     * Construct brand service.
     * @param brandDao brand dao to use.
     */
    public BrandServiceImpl(final GenericDAO<Brand, Long> brandDao) {
        super(brandDao);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Brand getById(final long brandId) {
        return findById(brandId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Brand findByNameOrGuid(final String nameOrGuid) {

        Brand brand = getGenericDao().findSingleByNamedQuery("BRAND.BY.NAME", nameOrGuid);
        if (brand == null) {
            brand = getGenericDao().findSingleByNamedQuery("BRAND.BY.GUID", nameOrGuid);
        }
        if (brand != null) {
            Hibernate.initialize(brand.getAttributes());
        }
        return brand;

    }


    private Pair<String, Object[]> findBrandQuery(final boolean count,
                                                  final String sort,
                                                  final boolean sortDescending,
                                                  final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(b) from BrandEntity b ");
        } else {
            hqlCriteria.append("select b from BrandEntity b ");
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "b", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by b." + sort + " " + (sortDescending ? "desc" : "asc"));

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
    public List<Brand> findBrands(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findBrandQuery(false, sort, sortDescending, filter);

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
    public int findBrandCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findBrandQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }
}

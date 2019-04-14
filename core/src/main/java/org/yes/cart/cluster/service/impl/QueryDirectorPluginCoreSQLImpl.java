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
package org.yes.cart.cluster.service.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.cluster.service.QueryDirectorPlugin;
import org.yes.cart.dao.GenericFTSCapableDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.utils.impl.ObjectUtil;

import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 16:31
 */
public class QueryDirectorPluginCoreSQLImpl implements QueryDirectorPlugin {

    private ProductService productService;

    /** {@inheritDoc} */
    @Override
    public boolean supports(final String type) {
        return "sql".equalsIgnoreCase(type) || "sql-core".equalsIgnoreCase(type);
    }

    /** {@inheritDoc} */
    @Override
    public List<Object[]> runQuery(final String query) {

        if (StringUtils.isNotBlank(query)) {

            if (query.toLowerCase().contains("select ")) {

                return ObjectUtil.transformTypedResultListToArrayList(getGenericDao().executeNativeQuery(query));

            } else {

                return Collections.singletonList(ObjectUtil.escapeXml(getGenericDao().executeNativeUpdate(query)));

            }
        }

        return Collections.emptyList();

    }

    @SuppressWarnings("unchecked")
    private GenericFTSCapableDAO<Product, Long, Object> getGenericDao() {
        return (GenericFTSCapableDAO) productService.getGenericDao();
    }


    /**
     * IoC. Set product service.
     *
     * @param productService product service to use.
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }


}

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

package org.yes.cart.web.service.ws.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.utils.impl.ObjectUtil;
import org.yes.cart.web.service.ws.BackdoorService;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 7/10/13
 * Time: 10:52 PM
 */
public class LocalBackdoorServiceImpl implements BackdoorService, ApplicationContextAware, ServletContextAware {

    private static final long serialVersionUID = 20130820L;

    private ProductService productService;

    private ApplicationContext applicationContext;

    private ServletContext servletContext;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ping() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int reindexAllProducts() {
        throw new UnsupportedOperationException("YUM does nto support product index");
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProduct(final long productPk) {
        throw new UnsupportedOperationException("YUM does nto support product index");
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProductSku(final long productPk) {
        throw new UnsupportedOperationException("YUM does nto support product index");
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProductSkuCode(final String productCode) {
        throw new UnsupportedOperationException("YUM does nto support product index");
    }

    /**
     * {@inheritDoc}
     */
    public int reindexProducts(final long[] productPks) {
        throw new UnsupportedOperationException("YUM does nto support product index");
    }

    /**
     * {@inheritDoc}
     */
    public List<Object[]> sqlQuery(final String query) {

        try {

            if (StringUtils.isNotBlank(query)) {

                if (query.toLowerCase().contains("select ")) {

                    return getGenericDao().executeNativeQuery(query);

                } else {

                    return Collections.singletonList(new Object[]{getGenericDao().executeNativeUpdate(query)});

                }
            }

            return Collections.EMPTY_LIST;

        } catch (Exception e) {
            final String msg = "Cant parse query : " + query + " Error : " + e.getMessage();
            ShopCodeContext.getLog(this).warn(msg);
            return Collections.singletonList(new Object[]{msg});
        }

    }


    /**
     * {@inheritDoc}
     */
    public List<Object[]> hsqlQuery(final String query) {
        try {

            if (StringUtils.isNotBlank(query)) {

                if (query.toLowerCase().contains("select ")) {

                    final List queryRez = getGenericDao().executeHsqlQuery(query);
                    return transformTypedResultListToArrayList(queryRez);

                } else {
                    return Collections.singletonList(new Object[]{getGenericDao().executeHsqlQuery(query)});
                }
            }
            return Collections.EMPTY_LIST;
        } catch (Exception e) {
            final String msg = "Cant parse query : " + query + " Error : " + e.getMessage();
            ShopCodeContext.getLog(this).warn(msg);
            return Collections.singletonList(new Object[]{msg});
        }

    }


    /**
     * {@inheritDoc}
     */
    public List<Object[]> luceneQuery(final String luceneQuery) {
        throw new UnsupportedOperationException("YUM does nto support product index");
    }

    private List<Object[]> transformTypedResultListToArrayList(List queryRez) {

        final List<Object[]> rezList = new ArrayList<Object[]>(queryRez.size());

        for (Object obj : queryRez) {

            rezList.add(ObjectUtil.toObjectArray(obj));

        }
        return rezList;
    }


    /**
     * IoC. Set product service.
     *
     * @param productService product service to use.
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    @SuppressWarnings("unchecked")
    private GenericDAO<Product, Long> getGenericDao() {
        return productService.getGenericDao();
    }


    /**
     * {@inheritDoc}
     */
    public String getImageVaultPath() throws IOException {

        /*final File ycsimg = new File(applicationContext.getResource("WEB-INF").getFile().getAbsolutePath()
                + File.separator + ".." + File.separator + ".." + File.separator + "yes-shop"
                + File.separator + "default" + File.separator + "imagevault" + File.separator); */

        final File ycsimg = new File(servletContext.getRealPath("/default/imagevault/"));

        return ycsimg.getAbsolutePath();
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

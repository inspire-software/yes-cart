package org.yes.cart.web.service.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import java.util.List;

/**
 * Back door administrative service.
 * Need to have ability:
 *
 * 1. reindex product on deman on storefront side instead of managment side.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/28/12
 * Time: 9:50 AM
 */
@WebService
public interface BackdoorService {

    /**
     * Reindex all products.
     * @return quantity of objects in index
     */
    @WebMethod
    @WebResult(name = "quantity")
    int reindexAllProducts();

    /**
     * Reindex single products.
     * @param productPk product pk.
     * @return quantity of objects in index
     */
    @WebMethod
    @WebResult(name = "quantity")
    int reindexProduct(@WebParam(name = "productPk") long productPk);


    /**
     * Reindex given set of products.
     * @param productPks product PKs to reindex
     * @return quantity of objects in index
     */
    @WebMethod
    @WebResult(name = "quantity")
    int reindexProducts(@WebParam(name = "productPks") long[] productPks);


    /**
     * Execute sql and return result.
     * DML operatin also allowed, in this case result has quantity of affected rows.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    @WebMethod
    @WebResult(name = "queryResult")
    List<Object[]> sqlQuery(String query);

    /**
     * Execute hsql and return result.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    @WebMethod
    @WebResult(name = "queryResult")
    List<Object[]> hsqlQuery(String query);

    /**
     * Execute lucene and return result.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    @WebMethod
    @WebResult(name = "queryResult")
    List<Object[]> luceneQuery(String query);


}

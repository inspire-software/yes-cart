package org.yes.cart.web.service.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

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
    int reindexAllProducts();

    /**
     * Reindex single products.
     * @param productPk product pk.
     * @return quantity of objects in index
     */
    @WebMethod
    int reindexProduct(@WebParam(name = "productPk") long productPk);


    /**
     * Reindex given set of products.
     * @param productPks product PKs to reindex
     * @return quantity of objects in index
     */
    @WebMethod
    int reindexProducts(@WebParam(name = "productPks") long[] productPks);


}

package org.yes.cart.web.service.ws.impl;

import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.service.ws.BackdoorService;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/28/12
 * Time: 10:02 AM
 */
@WebService(endpointInterface = "org.yes.cart.web.service.ws.BackdoorService",
        serviceName = "BackdoorService")
public class BackdoorServiceImpl implements BackdoorService {

    private static final long serialVersionUID = 20120129L;

    private ProductService productService;


    /**
     * {@inheritDoc}
     */
    @WebMethod
    public int reindexAllProducts() {
        return  productService.reindexProducts();
    }

    /**
     * {@inheritDoc}
     */
    @WebMethod
    public int reindexProduct(@WebParam(name = "productPk") long productPk) {
        return productService.reindexProduct(productPk);
    }

    /**
     * {@inheritDoc}
     */
    @WebMethod
    public int reindexProducts(@WebParam(name = "productPks") long[] productPks) {
        int rez = 0;
        for (long pk : productPks) {
            rez += productService.reindexProduct(pk);
        }
        return rez;
    }

    /**
     * Set product service.
     *
     * @param productService product service to use.
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

}

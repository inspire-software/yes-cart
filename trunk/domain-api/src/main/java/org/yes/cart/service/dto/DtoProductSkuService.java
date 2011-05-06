package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.SkuPriceDTO;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.exception.UnableToCreateInstanceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoProductSkuService extends GenericDTOService<ProductSkuDTO>, GenericAttrValueService {

    /**
     * Get all product SKUs.
     * @param productId product id
     * @return list of product skus.
     */
    List<ProductSkuDTO> getAllProductSkus(long productId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Create sku price.
     * @param skuPriceDTO to create in database
     * @return created price sku pk value
     */
    long createSkuPrice(SkuPriceDTO skuPriceDTO);

    /**
     * Update sku price.
     * @param skuPriceDTO to create in database
     * @return update price sku pk value
     */
    long updateSkuPrice(SkuPriceDTO skuPriceDTO);


    /**
     * Delete sku prioce by given pk
     * @param skuPriceId pk value.
     */
    void removeSkuPrice(long skuPriceId);

}

package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.SkuPriceDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.RemoteProductSkuService;
import org.yes.cart.service.dto.DtoProductSkuService;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteProductSkuServiceImpl
        extends AbstractRemoteService<ProductSkuDTO>
        implements RemoteProductSkuService {

    private final DtoProductSkuService dtoProductSkuService;

    /**
     * Construct remote service.
     *
     * @param dtoProductSkuService dto service.
     */
    public RemoteProductSkuServiceImpl(final DtoProductSkuService dtoProductSkuService) {
        super(dtoProductSkuService);
        this.dtoProductSkuService = dtoProductSkuService;
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoProductSkuService.getEntityAttributes(entityPk);
    }


    /**
     * Update attribute value.
     *
     * @param attrValueDTO value to update
     * @return updated value
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        return dtoProductSkuService.updateEntityAttributeValue(attrValueDTO);
    }

    /**
     * Create attribute value
     *
     * @param attrValueDTO value to persist
     * @return created value
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) {
        return dtoProductSkuService.createEntityAttributeValue(attrValueDTO);
    }

    /**
     * Delete attribute value by given pk value.
     *
     * @param attributeValuePk given pk value.
     */
    public void deleteAttributeValue(final long attributeValuePk) {
        dtoProductSkuService.deleteAttributeValue(attributeValuePk);
    }

    /**
     * Get all product SKUs.
     *
     * @param productId product id
     * @return list of product skus.
     */
    public List<ProductSkuDTO> getAllProductSkus(final long productId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoProductSkuService.getAllProductSkus(productId);
    }

    /**
     * Create sku price.
     *
     * @param skuPriceDTO to create in database
     * @return created sku dto
     */
    public long createSkuPrice(final SkuPriceDTO skuPriceDTO) {
        setNullPrices(skuPriceDTO);
        return dtoProductSkuService.createSkuPrice(skuPriceDTO);
    }

    /**
     * Update sku price.
     *
     * @param skuPriceDTO to create in database
     * @return updated sku price dto
     */
    public long updateSkuPrice(final SkuPriceDTO skuPriceDTO) {
        setNullPrices(skuPriceDTO);
        return dtoProductSkuService.updateSkuPrice(skuPriceDTO);
    }

    /**
     * Set minimal & sale price to null if they are 0
     *
     * @param skuPriceDTO {@link SkuPriceDTO}
     */
    private void setNullPrices(final SkuPriceDTO skuPriceDTO) {
        if (skuPriceDTO.getMinimalPrice() != null
                && BigDecimal.ZERO.floatValue() == skuPriceDTO.getMinimalPrice().floatValue()) {
            skuPriceDTO.setMinimalPrice(null);
        }
        if (skuPriceDTO.getSalePrice() != null &&
                BigDecimal.ZERO.floatValue() == skuPriceDTO.getSalePrice().floatValue()) {
            skuPriceDTO.setSalePrice(null);
        }

    }

    /**
     * Delete sku prioce by given pk
     *
     * @param skuPriceId pk value.
     */
    public void removeSkuPrice(final long skuPriceId) {
        dtoProductSkuService.removeSkuPrice(skuPriceId);
    }
}

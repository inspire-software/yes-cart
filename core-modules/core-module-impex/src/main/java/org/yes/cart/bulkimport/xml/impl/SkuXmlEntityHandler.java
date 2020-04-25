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

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.SkuType;
import org.yes.cart.domain.entity.AttrValueProductSku;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductSkuService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class SkuXmlEntityHandler extends AbstractAttributableXmlEntityHandler<SkuType, ProductSku, ProductSku, AttrValueProductSku> implements XmlEntityImportHandler<SkuType, ProductSku> {

    private ProductSkuService productSkuService;
    private ProductService productService;

    public SkuXmlEntityHandler() {
        super("sku");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final ProductSku sku) {
        this.productSkuService.delete(sku);
        this.productSkuService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final ProductSku domain, final SkuType xmlType, final EntityImportModeType mode) {

        if (xmlType.getManufacturer() != null) {
            domain.setManufacturerCode(xmlType.getManufacturer().getManufacturerCode());
            domain.setManufacturerPartCode(xmlType.getManufacturer().getManufacturerPartCode());
            domain.setBarCode(xmlType.getManufacturer().getBarcode());
        }

        if (xmlType.getSupplier() != null) {
            domain.setSupplierCode(xmlType.getSupplier().getSupplierCode());
            domain.setSupplierCatalogCode(xmlType.getSupplier().getSupplierCatalogCode());
        }

        updateSeo(xmlType.getSeo(), domain.getSeo());
        updateExt(xmlType.getCustomAttributes(), domain, domain.getAttributes());

        if (xmlType.getRank() != null) {
            domain.setRank(xmlType.getRank());
        }
        domain.setName(xmlType.getName());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        domain.setDescription(xmlType.getDescription());
        if (domain.getSkuId() == 0L) {
            this.productSkuService.create(domain);
        } else {
            this.productSkuService.update(domain);
        }
        this.productSkuService.getGenericDao().flush();
        this.productSkuService.getGenericDao().evict(domain);
    }

    @Override
    protected ProductSku getOrCreate(final JobStatusListener statusListener, final SkuType xmlType) {
        ProductSku sku = this.productSkuService.findSingleByCriteria(" where e.code = ?1", xmlType.getCode());
        if (sku != null) {
            return sku;
        }
        sku = this.productSkuService.getGenericDao().getEntityFactory().getByIface(ProductSku.class);
        sku.setGuid(xmlType.getCode());
        sku.setCode(xmlType.getCode());
        sku.setProduct(this.productService.findSingleByCriteria(" where e.code = ?1", xmlType.getProductCode()));
        return sku;
    }

    @Override
    protected EntityImportModeType determineImportMode(final SkuType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final ProductSku domain) {
        return domain.getSkuId() == 0L;
    }

    @Override
    protected void setMaster(final ProductSku master, final AttrValueProductSku av) {
        av.setProductSku(master);
    }

    @Override
    protected Class<AttrValueProductSku> getAvInterface() {
        return AttrValueProductSku.class;
    }

    /**
     * Spring IoC.
     *
     * @param productSkuService SKU service
     */
    public void setProductSkuService(final ProductSkuService productSkuService) {
        this.productSkuService = productSkuService;
    }

    /**
     * Spring IoC.
     *
     * @param productService product service
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }
}

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

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.*;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.BrandService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductTypeService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ProductXmlEntityHandler extends AbstractAttributableXmlEntityHandler<org.yes.cart.bulkimport.xml.internal.ProductType, Product, Product, AttrValueProduct> implements XmlEntityImportHandler<org.yes.cart.bulkimport.xml.internal.ProductType, Product> {

    private BrandService brandService;
    private ProductTypeService productTypeService;
    private ProductService productService;

    private XmlEntityImportHandler<SkuType, ProductSku> skuXmlEntityImportHandler;
    private XmlEntityImportHandler<ProductCategoriesCodeType, Product> productCategoriesCodeXmlEntityImportHandler;
    private XmlEntityImportHandler<ProductLinksCodeType, Product> productLinksCodeXmlEntityImportHandler;
    private XmlEntityImportHandler<ProductOptionsCodeType, Product> productOptionsCodeXmlEntityImportHandler;

    public ProductXmlEntityHandler() {
        super("product");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final Product product) {
        this.productService.delete(product);
        this.productService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductType xmlType, final EntityImportModeType mode) {

        if (xmlType.getManufacturer() != null) {
            domain.setManufacturerCode(xmlType.getManufacturer().getManufacturerCode());
            domain.setManufacturerPartCode(xmlType.getManufacturer().getManufacturerPartCode());
        }

        if (xmlType.getSupplier() != null) {
            domain.setSupplierCode(xmlType.getSupplier().getSupplierCode());
            domain.setSupplierCatalogCode(xmlType.getSupplier().getSupplierCatalogCode());
        }

        if (xmlType.getPim() != null) {
            domain.setPimCode(xmlType.getPim().getPimCode());
            domain.setPimDisabled(xmlType.getPim().isDisabled());
        }

        if (xmlType.getBrand() != null) {
            Brand brand = this.brandService.findByNameOrGuid(xmlType.getBrand());
            if (brand == null) {
                brand = this.brandService.getGenericDao().getEntityFactory().getByIface(Brand.class);
                brand.setName(xmlType.getBrand());
                this.brandService.create(brand);
            }
            domain.setBrand(brand);
        }

        if (xmlType.getProductType() != null) {
            ProductType productType = this.productTypeService.findSingleByCriteria(" where e.guid = ?1", xmlType.getProductType().getGuid());
            if (productType == null) {
                productType = this.productTypeService.getGenericDao().getEntityFactory().getByIface(ProductType.class);
                productType.setGuid(xmlType.getProductType().getGuid());
                productType.setName(xmlType.getProductType().getGuid());
                productType.setShippable(true);
                this.productTypeService.create(productType);
            }
            domain.setProducttype(productType);
        }

        if (xmlType.getConfiguration() != null) {
            domain.setNotSoldSeparately(xmlType.getConfiguration().isNotSoldSeparately() != null ? xmlType.getConfiguration().isNotSoldSeparately() : domain.getNotSoldSeparately());
        }

        updateSeo(xmlType.getSeo(), domain.getSeo());
        updateExt(xmlType.getCustomAttributes(), domain, domain.getAttributes());

        domain.setName(xmlType.getName());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        domain.setDescription(xmlType.getDescription());
        domain.setTag(processTags(xmlType.getTags(), domain.getTag()));
        if (domain.getProductId() == 0L) {
            this.productService.create(domain);
        } else {
            this.productService.update(domain);
        }
        this.productService.getGenericDao().flush();
        this.productService.getGenericDao().evict(domain);

        processCategories(statusListener, domain, xmlType);
        processProductAssociations(statusListener, domain, xmlType);

        if (xmlType.getProductSku() != null) {
            for (final SkuType xmlSkuType : xmlType.getProductSku().getSku()) {

                xmlSkuType.setProductCode(domain.getCode());
                skuXmlEntityImportHandler.handle(statusListener, null, (ImpExTuple) new XmlImportTupleImpl(xmlSkuType.getCode(), xmlSkuType), null, null);

            }
        }

        processProductOptions(statusListener, domain, xmlType);

    }

    private void processProductAssociations(final JobStatusListener statusListener, final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductType xmlType) {

        if (xmlType.getProductLinks() != null) {

            final ProductLinksCodeType subXmlType = new ProductLinksCodeType();
            subXmlType.setProductCode(domain.getCode());
            subXmlType.setImportMode(xmlType.getProductLinks().getImportMode());
            subXmlType.getProductLink().addAll(xmlType.getProductLinks().getProductLink());

            productLinksCodeXmlEntityImportHandler.handle(statusListener, null, (ImpExTuple) new XmlImportTupleImpl(subXmlType.getProductCode(), subXmlType), null, null);


        }


    }

    private void processProductOptions(final JobStatusListener statusListener, final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductType xmlType) {

        if (xmlType.getProductOptions() != null) {

            final ProductOptionsCodeType subXmlType = new ProductOptionsCodeType();
            subXmlType.setProductCode(domain.getCode());
            subXmlType.setConfigurable(xmlType.getProductOptions().isConfigurable() != null ? xmlType.getProductOptions().isConfigurable() : domain.getOptions().isConfigurable());
            subXmlType.setImportMode(xmlType.getProductOptions().getImportMode());
            subXmlType.getProductOption().addAll(xmlType.getProductOptions().getProductOption());

            productOptionsCodeXmlEntityImportHandler.handle(statusListener, null, (ImpExTuple) new XmlImportTupleImpl(subXmlType.getProductCode(), subXmlType), null, null);


        }


    }

    private void processCategories(final JobStatusListener statusListener, final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductType xmlType) {

        if (xmlType.getProductCategories() != null) {

            final ProductCategoriesCodeType subXmlType = new ProductCategoriesCodeType();
            subXmlType.setProductCode(domain.getCode());
            subXmlType.setImportMode(xmlType.getProductCategories().getImportMode());
            subXmlType.getProductCategory().addAll(xmlType.getProductCategories().getProductCategory());

            productCategoriesCodeXmlEntityImportHandler.handle(statusListener, null, (ImpExTuple) new XmlImportTupleImpl(subXmlType.getProductCode(), subXmlType), null, null);

        }

    }

    @Override
    protected Product getOrCreate(final JobStatusListener statusListener, final org.yes.cart.bulkimport.xml.internal.ProductType xmlType) {
        Product product = this.productService.findSingleByCriteria(" where e.code = ?1", xmlType.getCode());
        if (product != null) {
            return product;
        }
        product = this.productService.getGenericDao().getEntityFactory().getByIface(Product.class);
        product.setCreatedBy(xmlType.getCreatedBy());
        product.setCreatedTimestamp(processInstant(xmlType.getCreatedTimestamp()));
        product.setCode(xmlType.getCode());
        product.setGuid(xmlType.getCode());
        return product;
    }

    @Override
    protected EntityImportModeType determineImportMode(final org.yes.cart.bulkimport.xml.internal.ProductType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Product domain) {
        return domain.getProductId() == 0L;
    }

    @Override
    protected void setMaster(final Product master, final AttrValueProduct av) {
        av.setProduct(master);
    }

    @Override
    protected Class<AttrValueProduct> getAvInterface() {
        return AttrValueProduct.class;
    }

    /**
     * Spring IoC.
     *
     * @param productTypeService product type service
     */
    public void setProductTypeService(final ProductTypeService productTypeService) {
        this.productTypeService = productTypeService;
    }

    /**
     * Spring IoC.
     *
     * @param productService product service
     */
    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    /**
     * Spring IoC.
     *
     * @param brandService brand service
     */
    public void setBrandService(final BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * Spring IoC.
     *
     * @param skuXmlEntityImportHandler SKU handler
     */
    public void setSkuXmlEntityImportHandler(final XmlEntityImportHandler<SkuType, ProductSku> skuXmlEntityImportHandler) {
        this.skuXmlEntityImportHandler = skuXmlEntityImportHandler;
    }

    /**
     * Spring IoC.
     *
     * @param productCategoriesCodeXmlEntityImportHandler categories handler
     */
    public void setProductCategoriesCodeXmlEntityImportHandler(final XmlEntityImportHandler<ProductCategoriesCodeType, Product> productCategoriesCodeXmlEntityImportHandler) {
        this.productCategoriesCodeXmlEntityImportHandler = productCategoriesCodeXmlEntityImportHandler;
    }

    /**
     * Spring IoC.
     *
     * @param productLinksCodeXmlEntityImportHandler links handler
     */
    public void setProductLinksCodeXmlEntityImportHandler(final XmlEntityImportHandler<ProductLinksCodeType, Product> productLinksCodeXmlEntityImportHandler) {
        this.productLinksCodeXmlEntityImportHandler = productLinksCodeXmlEntityImportHandler;
    }

    /**
     * Spring IoC.
     *
     * @param productOptionsCodeXmlEntityImportHandler options handler
     */
    public void setProductOptionsCodeXmlEntityImportHandler(final XmlEntityImportHandler<ProductOptionsCodeType, Product> productOptionsCodeXmlEntityImportHandler) {
        this.productOptionsCodeXmlEntityImportHandler = productOptionsCodeXmlEntityImportHandler;
    }
}

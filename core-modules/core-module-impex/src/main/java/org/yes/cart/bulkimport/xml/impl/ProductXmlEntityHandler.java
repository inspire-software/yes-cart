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

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.*;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.service.domain.*;

import java.util.Iterator;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ProductXmlEntityHandler extends AbstractAttributableXmlEntityHandler<org.yes.cart.bulkimport.xml.internal.ProductType, Product, Product, AttrValueProduct> implements XmlEntityImportHandler<org.yes.cart.bulkimport.xml.internal.ProductType> {

    private CategoryService categoryService;
    private BrandService brandService;
    private ProductTypeService productTypeService;
    private ProductService productService;
    private ProductCategoryService productCategoryService;
    private ProductAssociationService productAssociationService;
    private AssociationService associationService;

    private XmlEntityImportHandler<SkuType> skuXmlEntityImportHandler;

    public ProductXmlEntityHandler() {
        super("product");
    }

    @Override
    protected void delete(final Product product) {
        this.productService.delete(product);
        this.productService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductType xmlType, final EntityImportModeType mode) {

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

        if (xmlType.getAvailability() != null) {
            domain.setDisabled(xmlType.getAvailability().isDisabled());
            domain.setAvailablefrom(processLDT(xmlType.getAvailability().getAvailableFrom()));
            domain.setAvailableto(processLDT(xmlType.getAvailability().getAvailableTo()));
        }

        if (xmlType.getInventoryConfig() != null) {
            domain.setAvailability(xmlType.getInventoryConfig().getType());
            domain.setMinOrderQuantity(xmlType.getInventoryConfig().getMin());
            domain.setMaxOrderQuantity(xmlType.getInventoryConfig().getMax());
            domain.setStepOrderQuantity(xmlType.getInventoryConfig().getStep());
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

        updateSeo(xmlType.getSeo(), domain.getSeo());
        updateExt(xmlType.getCustomAttributes(), domain, domain.getAttributes());
        processCategories(domain, xmlType);
        processProductAssociations(domain, xmlType);

        domain.setName(xmlType.getName());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        domain.setDescription(xmlType.getDescription());
        domain.setTag(xmlType.getTags());
        if (domain.getProductId() == 0L) {
            this.productService.create(domain);
        } else {
            this.productService.update(domain);
        }
        this.productService.getGenericDao().flush();
        this.productService.getGenericDao().evict(domain);

        if (xmlType.getProductSku() != null) {
            for (final SkuType xmlSkuType : xmlType.getProductSku().getSku()) {

                xmlSkuType.setProductCode(domain.getCode());
                skuXmlEntityImportHandler.handle(null, null, (ImpExTuple) new XmlImportTupleImpl(xmlSkuType.getCode(), xmlSkuType), null, null);

            }
        }

    }

    private void processProductAssociations(final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductType xmlType) {

        if (xmlType.getProductLinks() != null) {

            final CollectionImportModeType collectionMode = xmlType.getProductLinks().getImportMode() != null ? xmlType.getProductLinks().getImportMode() : CollectionImportModeType.MERGE;
            if (collectionMode == CollectionImportModeType.REPLACE) {
                domain.getProductAssociations().clear();
            }

            for (final ProductLinkType link : xmlType.getProductLinks().getProductLink()) {
                final EntityImportModeType itemMode = link.getImportMode() != null ? link.getImportMode() : EntityImportModeType.MERGE;
                if (itemMode == EntityImportModeType.DELETE) {
                    processProductAssociationsRemove(domain, link);
                } else {
                    processProductAssociationsSave(domain, link);
                }
            }

        }


    }

    private void processProductAssociationsSave(final Product domain, final ProductLinkType link) {

        for (final ProductAssociation pa : domain.getProductAssociations()) {
            if (link.getAssociation().equals(pa.getAssociation().getCode())
                    && link.getSku().equals(pa.getAssociatedSku())) {
                processProductAssociationsSaveBasic(link, pa);
                return;
            }
        }
        final ProductAssociation pa = this.productAssociationService.getGenericDao().getEntityFactory().getByIface(ProductAssociation.class);
        pa.setProduct(domain);
        Association assoc = this.associationService.findSingleByCriteria(" where e.code = ?1", link.getAssociation());
        if (assoc == null) {
            assoc = this.associationService.getGenericDao().getEntityFactory().getByIface(Association.class);
            assoc.setCode(link.getAssociation());
            assoc.setName(link.getAssociation());
            this.associationService.create(assoc);
        }
        pa.setAssociation(assoc);
        pa.setAssociatedSku(link.getSku());
        processProductAssociationsSaveBasic(link, pa);
        domain.getProductAssociations().add(pa);



    }

    private void processProductAssociationsSaveBasic(final ProductLinkType link, final ProductAssociation pa) {
        if (link.getRank() != null) {
            pa.setRank(link.getRank());
        }
    }

    private void processProductAssociationsRemove(final Product domain, final ProductLinkType link) {
        final Iterator<ProductAssociation> it = domain.getProductAssociations().iterator();
        while (it.hasNext()) {
            final ProductAssociation next = it.next();
            if (link.getAssociation().equals(next.getAssociation().getCode())
                    && link.getSku().equals(next.getAssociatedSku())) {
                it.remove();
                return;
            }
        }

    }

    private void processCategories(final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductType xmlType) {

        if (xmlType.getProductCategories() != null) {

            final CollectionImportModeType collectionMode = xmlType.getProductCategories().getImportMode() != null ? xmlType.getProductCategories().getImportMode() : CollectionImportModeType.MERGE;
            if (collectionMode == CollectionImportModeType.REPLACE) {
                domain.getAttributes().clear();
            }

            for (final ProductCategoryType cat : xmlType.getProductCategories().getProductCategory()) {
                final EntityImportModeType itemMode = cat.getImportMode() != null ? cat.getImportMode() : EntityImportModeType.MERGE;
                if (itemMode == EntityImportModeType.DELETE) {
                    if (cat.getGuid() != null) {
                        processCategoriesRemove(domain, cat);
                    }
                } else {
                    processCategoriesSave(domain, cat);
                }
            }

        }

    }

    private void processCategoriesSave(final Product domain, final ProductCategoryType cat) {

        for (final ProductCategory pc : domain.getProductCategory()) {
            if (cat.getGuid().equals(pc.getCategory().getGuid())) {
                processCategoriesSaveBasic(cat, pc);
                return;
            }
        }
        final ProductCategory pc = this.productCategoryService.getGenericDao().getEntityFactory().getByIface(ProductCategory.class);
        pc.setProduct(domain);
        final Category ct = this.categoryService.findSingleByCriteria(" where e.guid = ?1", cat.getGuid());
        if (ct == null) {
            throw new IllegalArgumentException("No category with code: " + cat.getGuid());
        }
        pc.setCategory(ct);
        processCategoriesSaveBasic(cat, pc);
        domain.getProductCategory().add(pc);


    }

    private void processCategoriesSaveBasic(final ProductCategoryType cat, final ProductCategory pc) {
        if (cat.getRank() != null) {
            pc.setRank(cat.getRank());
        }
    }

    private void processCategoriesRemove(final Product domain, final ProductCategoryType cat) {
        final Iterator<ProductCategory> it = domain.getProductCategory().iterator();
        while (it.hasNext()) {
            final ProductCategory next = it.next();
            if (cat.getGuid().equals(next.getCategory().getGuid())) {
                it.remove();
                return;
            }
        }

    }

    @Override
    protected Product getOrCreate(final org.yes.cart.bulkimport.xml.internal.ProductType xmlType) {
        Product product = this.productService.findSingleByCriteria(" where e.code = ?1", xmlType.getCode());
        if (product != null) {
            return product;
        }
        product = this.productService.getGenericDao().getEntityFactory().getByIface(Product.class);
        product.setCode(xmlType.getCode());
        product.setGuid(xmlType.getCode());
        product.setAvailability(Product.AVAILABILITY_STANDARD);
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
     * @param categoryService category service
     */
    public void setCategoryService(final CategoryService categoryService) {
        this.categoryService = categoryService;
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
     * @param productCategoryService product category service
     */
    public void setProductCategoryService(final ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    /**
     * Spring IoC.
     *
     * @param productAssociationService product association service
     */
    public void setProductAssociationService(final ProductAssociationService productAssociationService) {
        this.productAssociationService = productAssociationService;
    }

    /**
     * Spring IoC.
     *
     * @param associationService association service
     */
    public void setAssociationService(final AssociationService associationService) {
        this.associationService = associationService;
    }

    /**
     * Spring IoC.
     *
     * @param skuXmlEntityImportHandler SKU handler
     */
    public void setSkuXmlEntityImportHandler(final XmlEntityImportHandler<SkuType> skuXmlEntityImportHandler) {
        this.skuXmlEntityImportHandler = skuXmlEntityImportHandler;
    }
}

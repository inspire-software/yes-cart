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
import org.yes.cart.bulkimport.xml.internal.CollectionImportModeType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.ProductLinkType;
import org.yes.cart.domain.entity.Association;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductAssociation;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.AssociationService;
import org.yes.cart.service.domain.ProductAssociationService;
import org.yes.cart.service.domain.ProductService;

import java.util.Iterator;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ProductLinksXmlEntityHandler extends AbstractXmlEntityHandler<org.yes.cart.bulkimport.xml.internal.ProductLinksCodeType, Product> implements XmlEntityImportHandler<org.yes.cart.bulkimport.xml.internal.ProductLinksCodeType, Product> {

    private ProductService productService;
    private ProductAssociationService productAssociationService;
    private AssociationService associationService;

    public ProductLinksXmlEntityHandler() {
        super("product-links");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final Product product) {
        this.productService.delete(product);
        this.productService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductLinksCodeType xmlType, final EntityImportModeType mode) {

        if (domain != null) {
            processProductAssociations(domain, xmlType);

            if (domain.getProductId() == 0L) {
                this.productService.create(domain);
            } else {
                this.productService.update(domain);
            }
            this.productService.getGenericDao().flush();
            this.productService.getGenericDao().evict(domain);
        }

    }

    private void processProductAssociations(final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductLinksCodeType xmlType) {

        final CollectionImportModeType collectionMode = xmlType.getImportMode() != null ? xmlType.getImportMode() : CollectionImportModeType.MERGE;
        if (collectionMode == CollectionImportModeType.REPLACE) {
            domain.getProductAssociations().clear();
        }

        for (final ProductLinkType link : xmlType.getProductLink()) {
            final EntityImportModeType itemMode = link.getImportMode() != null ? link.getImportMode() : EntityImportModeType.MERGE;
            if (itemMode == EntityImportModeType.DELETE) {
                processProductAssociationsRemove(domain, link);
            } else {
                processProductAssociationsSave(domain, link);
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

    @Override
    protected Product getOrCreate(final JobStatusListener statusListener, final org.yes.cart.bulkimport.xml.internal.ProductLinksCodeType xmlType) {
        Product product = this.productService.findSingleByCriteria(" where e.code = ?1", xmlType.getProductCode());
        if (product != null) {
            return product;
        }
        return null;
    }

    @Override
    protected EntityImportModeType determineImportMode(final org.yes.cart.bulkimport.xml.internal.ProductLinksCodeType xmlType) {
        return EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Product domain) {
        return domain.getProductId() == 0L;
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
}

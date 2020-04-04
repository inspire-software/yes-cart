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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.CollectionImportModeType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.ProductOptionType;
import org.yes.cart.bulkimport.xml.internal.ProductOptionValueType;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductOption;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.ProductService;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ProductOptionsXmlEntityHandler extends AbstractXmlEntityHandler<org.yes.cart.bulkimport.xml.internal.ProductOptionsCodeType, Product> implements XmlEntityImportHandler<org.yes.cart.bulkimport.xml.internal.ProductOptionsCodeType, Product> {

    private ProductService productService;

    public ProductOptionsXmlEntityHandler() {
        super("product-options");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final Product product, final Map<String, Integer> entityCount) {
        this.productService.delete(product);
        this.productService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductOptionsCodeType xmlType, final EntityImportModeType mode, final Map<String, Integer> entityCount) {

        if (domain != null) {
            processProductOptions(domain, xmlType);

            domain.getOptions().setConfigurable(xmlType.isConfigurable());

            if (domain.getProductId() == 0L) {
                this.productService.create(domain);
            } else {
                this.productService.update(domain);
            }
            this.productService.getGenericDao().flush();
            this.productService.getGenericDao().evict(domain);
        }

    }

    private void processProductOptions(final Product domain, final org.yes.cart.bulkimport.xml.internal.ProductOptionsCodeType xmlType) {

        final CollectionImportModeType collectionMode = xmlType.getImportMode() != null ? xmlType.getImportMode() : CollectionImportModeType.MERGE;
        if (collectionMode == CollectionImportModeType.REPLACE) {
            domain.getProductAssociations().clear();
        }

        for (final ProductOptionType productOptionType : xmlType.getProductOption()) {
            final EntityImportModeType itemMode = productOptionType.getImportMode() != null ? productOptionType.getImportMode() : EntityImportModeType.MERGE;
            if (itemMode == EntityImportModeType.DELETE) {
                processProductOptionsRemove(domain, productOptionType);
            } else {
                processProductOptionsSave(domain, productOptionType);
            }
        }

    }

    private void processProductOptionsSave(final Product domain, final ProductOptionType opt) {

        final ProductOption po = domain.getOptions().createOrGetConfigurationOption(opt.getAttribute(), opt.getSku());
        processProductAssociationsSaveBasic(opt, po);

    }

    private void processProductAssociationsSaveBasic(final ProductOptionType opt, final ProductOption po) {
        if (opt.getRank() != null) {
            po.setRank(opt.getRank());
        }
        po.setMandatory(opt.isMandatory());
        po.setQuantity(opt.getQuantity());
        po.setOptionSkuCodes(opt.getProductOptionValues().getProductOptionValue().stream()
                .map(ProductOptionValueType::getSku).collect(Collectors.toList()));
    }

    private void processProductOptionsRemove(final Product domain, final ProductOptionType opt) {

        domain.getOptions().removeConfigurationOption(opt.getAttribute(), opt.getSku());

        final Iterator<ProductOption> it = domain.getOptions().getConfigurationOption().iterator();
        while (it.hasNext()) {
            final ProductOption next = it.next();
            if (opt.getAttribute().equals(next.getAttributeCode())
                    && (StringUtils.isBlank(opt.getSku()) && StringUtils.isBlank(next.getSkuCode())
                        || StringUtils.isNotBlank(opt.getSku()) && opt.getSku().equals(next.getSkuCode()))) {
                it.remove();
                return;
            }
        }

    }

    @Override
    protected Product getOrCreate(final JobStatusListener statusListener, final org.yes.cart.bulkimport.xml.internal.ProductOptionsCodeType xmlType, final Map<String, Integer> entityCount) {
        Product product = this.productService.findSingleByCriteria(" where e.code = ?1", xmlType.getProductCode());
        if (product != null) {
            return product;
        }
        return null;
    }

    @Override
    protected EntityImportModeType determineImportMode(final org.yes.cart.bulkimport.xml.internal.ProductOptionsCodeType xmlType) {
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

}

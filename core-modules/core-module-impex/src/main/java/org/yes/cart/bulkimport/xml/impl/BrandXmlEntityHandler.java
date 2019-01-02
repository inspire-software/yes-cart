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
import org.yes.cart.bulkimport.xml.internal.BrandType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.domain.entity.AttrValueBrand;
import org.yes.cart.domain.entity.Brand;
import org.yes.cart.service.domain.BrandService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class BrandXmlEntityHandler extends AbstractAttributableXmlEntityHandler<BrandType, Brand, Brand, AttrValueBrand> implements XmlEntityImportHandler<BrandType> {

    private BrandService brandService;

    public BrandXmlEntityHandler() {
        super("brand");
    }

    @Override
    protected void delete(final Brand brand) {
        this.brandService.delete(brand);
        this.brandService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final Brand domain, final BrandType xmlType, final EntityImportModeType mode) {

        updateExt(xmlType.getCustomAttributes(), domain, domain.getAttributes());

        domain.setName(xmlType.getName());
        domain.setDescription(xmlType.getDescription());
        if (domain.getBrandId() == 0L) {
            this.brandService.create(domain);
        } else {
            this.brandService.update(domain);
        }
        this.brandService.getGenericDao().flush();
        this.brandService.getGenericDao().evict(domain);
    }

    @Override
    protected Brand getOrCreate(final BrandType xmlType) {
        Brand brand = this.brandService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (brand != null) {
            return brand;
        }
        brand = this.brandService.getGenericDao().getEntityFactory().getByIface(Brand.class);
        brand.setGuid(xmlType.getGuid());
        return brand;
    }

    @Override
    protected EntityImportModeType determineImportMode(final BrandType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Brand domain) {
        return domain.getBrandId() == 0L;
    }

    @Override
    protected void setMaster(final Brand master, final AttrValueBrand av) {
        av.setBrand(master);
    }

    @Override
    protected Class<AttrValueBrand> getAvInterface() {
        return AttrValueBrand.class;
    }

    /**
     * Spring IoC.
     *
     * @param brandService brand service
     */
    public void setBrandService(final BrandService brandService) {
        this.brandService = brandService;
    }
}

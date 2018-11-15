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
import org.yes.cart.bulkimport.xml.internal.CategoryType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.domain.entity.AttrValueCategory;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductTypeService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class CategoryXmlEntityHandler extends AbstractAttributableXmlEntityHandler<CategoryType, Category, Category, AttrValueCategory> implements XmlEntityImportHandler<CategoryType> {

    private CategoryService categoryService;
    private ProductTypeService productTypeService;

    public CategoryXmlEntityHandler() {
        super("category");
    }

    @Override
    protected void delete(final Category category) {
        this.categoryService.delete(category);
        this.categoryService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final Category domain, final CategoryType xmlType, final EntityImportModeType mode) {

        if (xmlType.getParent() != null) {
            final Category parent = this.categoryService.findSingleByCriteria(" where e.guid = ?1", xmlType.getParent().getGuid());
            if (parent != null) {
                if (domain.getParentId() != parent.getCategoryId()) {
                    domain.setParentId(parent.getCategoryId());
                }
            }
        }
        if (xmlType.getLink() != null) {
            final Category link = this.categoryService.findSingleByCriteria(" where e.guid = ?1", xmlType.getLink().getGuid());
            if (link != null) {
                if (domain.getLinkToId() == null || domain.getLinkToId() != link.getCategoryId()) {
                    domain.setLinkToId(link.getCategoryId());
                }
            }
        }
        if (xmlType.getNavigation() != null) {
            // attribute navigation
            if (xmlType.getNavigation().getNavigationByAttributes() != null) {
                if (xmlType.getNavigation().getNavigationByAttributes().getNavigationProductType() != null) {
                    if (xmlType.getNavigation().getNavigationByAttributes().getNavigationProductType().getGuid() != null) {
                        final ProductType type = this.productTypeService.findSingleByCriteria(" where e.guid = ?1", xmlType.getNavigation().getNavigationByAttributes().getNavigationProductType().getGuid());
                        if (type != null) {
                            domain.setProductType(type);
                        }
                    } else {
                        domain.setProductType(null);
                    }
                }
                domain.setNavigationByAttributes(xmlType.getNavigation().getNavigationByAttributes().isEnabled());
            }
            // price navigation
            if (xmlType.getNavigation().getNavigationByPrice() != null) {
                if (StringUtils.isNotBlank(xmlType.getNavigation().getNavigationByPrice().getPriceTiers())) {
                    domain.setNavigationByPriceTiers(xmlType.getNavigation().getNavigationByPrice().getPriceTiers());
                } else {
                    domain.setNavigationByPriceTiers(null);
                }
                domain.setNavigationByPrice(xmlType.getNavigation().getNavigationByPrice().isEnabled());
            }
        }

        if (xmlType.getAvailability() != null) {
            domain.setDisabled(xmlType.getAvailability().isDisabled());
            domain.setAvailablefrom(processLDT(xmlType.getAvailability().getAvailableFrom()));
            domain.setAvailableto(processLDT(xmlType.getAvailability().getAvailableTo()));
        }

        if (xmlType.getTemplates() != null) {
            domain.setUitemplate(xmlType.getTemplates().getCategory());
        }

        updateSeo(xmlType.getSeo(), domain.getSeo());
        updateExt(xmlType.getCustomAttributes(), domain, domain.getAttributes());

        if (xmlType.getRank() != null) {
            domain.setRank(xmlType.getRank());
        }
        domain.setName(xmlType.getName());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        domain.setDescription(xmlType.getDescription());
        if (domain.getCategoryId() == 0L) {
            this.categoryService.create(domain);
        } else {
            this.categoryService.update(domain);
        }
        this.categoryService.getGenericDao().flush();
        this.categoryService.getGenericDao().evict(domain);
    }

    @Override
    protected Category getOrCreate(final CategoryType xmlType) {
        Category category = this.categoryService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (category != null) {
            return category;
        }
        category = this.categoryService.getGenericDao().getEntityFactory().getByIface(Category.class);
        category.setGuid(xmlType.getGuid());
        return category;
    }

    @Override
    protected EntityImportModeType determineImportMode(final CategoryType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Category domain) {
        return domain.getCategoryId() == 0L;
    }

    @Override
    protected void setMaster(final Category master, final AttrValueCategory av) {
        av.setCategory(master);
    }

    @Override
    protected Class<AttrValueCategory> getAvInterface() {
        return AttrValueCategory.class;
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
}

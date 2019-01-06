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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.*;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.ProdTypeAttributeViewGroup;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.entity.ProductTypeAttr;
import org.yes.cart.domain.misc.navigation.range.DisplayValue;
import org.yes.cart.domain.misc.navigation.range.RangeList;
import org.yes.cart.domain.misc.navigation.range.RangeNode;
import org.yes.cart.domain.misc.navigation.range.impl.DisplayValueImpl;
import org.yes.cart.domain.misc.navigation.range.impl.RangeListImpl;
import org.yes.cart.domain.misc.navigation.range.impl.RangeNodeImpl;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ProductTypeService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ProductTypeXmlEntityHandler extends AbstractXmlEntityHandler<ProductTypeTypeType, ProductType> implements XmlEntityImportHandler<ProductTypeTypeType> {

    private ProductTypeService productTypeService;
    private AttributeService attributeService;

    public ProductTypeXmlEntityHandler() {
        super("product-type");
    }

    @Override
    protected void delete(final ProductType type) {
        this.productTypeService.delete(type);
        this.productTypeService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final ProductType domain, final ProductTypeTypeType xmlType, final EntityImportModeType mode) {
        if (xmlType.getConfiguration() != null) {
            if (xmlType.getConfiguration().isService() != null) {
                domain.setService(xmlType.getConfiguration().isService());
            }
            if (xmlType.getConfiguration().isShippable() != null) {
                domain.setShippable(xmlType.getConfiguration().isShippable());
            }
            if (xmlType.getConfiguration().isDigital() != null) {
                domain.setDigital(xmlType.getConfiguration().isDigital());
            }
            if (xmlType.getConfiguration().isDownloadable() != null) {
                domain.setDownloadable(xmlType.getConfiguration().isDownloadable());
            }
        }
        if (xmlType.getTemplates() != null) {
            domain.setUitemplate(xmlType.getTemplates().getProduct());
            domain.setUisearchtemplate(xmlType.getTemplates().getCategory());
        }
        if (xmlType.getName() != null) {
            domain.setName(xmlType.getName());
        }
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        if (xmlType.getDescription() != null) {
            domain.setDescription(xmlType.getDescription());
        }
        processGroups(domain, xmlType);
        processAttributes(domain, xmlType);
        if (domain.getProducttypeId() == 0L) {
            this.productTypeService.create(domain);
        } else {
            this.productTypeService.update(domain);
        }
        this.attributeService.getGenericDao().flush();
        this.attributeService.getGenericDao().evict(domain);
    }

    private void processAttributes(final ProductType domain, final ProductTypeTypeType xmlType) {

        if (xmlType.getAttributes() != null) {
            final CollectionImportModeType collectionMode = xmlType.getAttributes().getImportMode() != null ? xmlType.getAttributes().getImportMode() : CollectionImportModeType.MERGE;
            if (collectionMode == CollectionImportModeType.REPLACE) {
                domain.getAttributes().clear();
            }

            for (final ProductTypeAttributeType attr : xmlType.getAttributes().getAttribute()) {
                final EntityImportModeType itemMode = attr.getImportMode() != null ? attr.getImportMode() : EntityImportModeType.MERGE;
                if (itemMode == EntityImportModeType.DELETE) {
                    if (attr.getAttribute() != null) {
                        processAttributesRemove(domain, attr);
                    }
                } else {
                    processAttributesSave(domain, attr);
                }
            }

        }
    }

    private void processAttributesSave(final ProductType domain, final ProductTypeAttributeType attr) {

        for (final ProductTypeAttr pta : domain.getAttributes()) {
            if (attr.getAttribute().equals(pta.getAttribute().getCode())) {
                processAttributesSaveBasic(attr, pta);
                return;
            }
        }
        final ProductTypeAttr pta = this.productTypeService.getGenericDao().getEntityFactory().getByIface(ProductTypeAttr.class);
        pta.setProducttype(domain);
        final Attribute at = this.attributeService.findByAttributeCode(attr.getAttribute());
        if (at == null) {
            throw new IllegalArgumentException("No attribute with code: " + attr.getAttribute());
        }
        pta.setAttribute(at);
        processAttributesSaveBasic(attr, pta);
        domain.getAttributes().add(pta);

    }

    private void processAttributesSaveBasic(final ProductTypeAttributeType attr, final ProductTypeAttr pta) {
        if (attr.getRank() != null) {
            pta.setRank(attr.getRank());
        }
        if (attr.isVisible() != null) {
            pta.setVisible(attr.isVisible());
        }
        if (attr.isSimilarity() != null) {
            pta.setSimilarity(attr.isSimilarity());
        }
        if (attr.getNavigation() != null) {
            pta.setNavigationTemplate(attr.getNavigation().getTemplate());
            pta.setNavigationType(attr.getNavigation().getType());

            final ProductTypeAttributeNavigationRangeListType rangeList = attr.getNavigation().getRangeList();

            if (rangeList != null && rangeList.getRanges() != null && CollectionUtils.isNotEmpty(rangeList.getRanges().getRange())) {

                final RangeList list = new RangeListImpl();
                list.setRanges(new ArrayList<>());

                for (final ProductTypeAttributeNavigationRangeListRangeType range : rangeList.getRanges().getRange()) {

                    final RangeNode node = new RangeNodeImpl();

                    node.setFrom(range.getFrom());
                    node.setTo(range.getTo());

                    if (range.getI18N() != null && CollectionUtils.isNotEmpty(range.getI18N().getDisplay())) {

                        final List<DisplayValue> dvs = new ArrayList<>();

                        for (final ProductTypeAttributeNavigationRangeListRangeDisplayValuesValueType i18n : range.getI18N().getDisplay()) {

                            final DisplayValue dv = new DisplayValueImpl();
                            dv.setLang(i18n.getLang());
                            dv.setValue(i18n.getValue());
                            dvs.add(dv);

                        }

                        node.setI18n(dvs);

                    }

                    list.getRanges().add(node);

                }

                pta.setRangeList(list);

            } else {
                pta.setRangeNavigation(null);
            }
        }
        pta.setGuid(attr.getGuid());
    }

    private void processAttributesRemove(final ProductType domain, final ProductTypeAttributeType attr) {
        final Iterator<ProductTypeAttr> it = domain.getAttributes().iterator();
        while (it.hasNext()) {
            final ProductTypeAttr next = it.next();
            if (attr.getAttribute().equals(next.getAttribute().getCode())) {
                it.remove();
                return;
            }
        }
    }

    private void processGroups(final ProductType domain, final ProductTypeTypeType xmlType) {

        if (xmlType.getGroups() != null) {
            final CollectionImportModeType collectionMode = xmlType.getGroups().getImportMode() != null ? xmlType.getGroups().getImportMode() : CollectionImportModeType.MERGE;
            if (collectionMode == CollectionImportModeType.REPLACE) {
                domain.getAttributeViewGroup().clear();
            }

            for (final ProductTypeGroupType group : xmlType.getGroups().getGroup()) {
                final EntityImportModeType itemMode = group.getImportMode() != null ? group.getImportMode() : EntityImportModeType.MERGE;
                if (itemMode == EntityImportModeType.DELETE) {
                    if (group.getName() != null) {
                        processGroupsRemove(domain, group);
                    }
                } else {
                    processGroupsSave(domain, group);
                }
            }

        }

    }

    private void processGroupsSave(final ProductType domain, final ProductTypeGroupType group) {
        for (final ProdTypeAttributeViewGroup ptavg : domain.getAttributeViewGroup()) {
            if (group.getName().equals(ptavg.getName())) {
                processGroupsSaveBasic(group, ptavg);
                return;
            }
        }
        final ProdTypeAttributeViewGroup ptavg = this.productTypeService.getGenericDao().getEntityFactory().getByIface(ProdTypeAttributeViewGroup.class);
        ptavg.setProducttype(domain);
        ptavg.setName(group.getName());
        processGroupsSaveBasic(group, ptavg);
        domain.getAttributeViewGroup().add(ptavg);
    }

    private void processGroupsSaveBasic(final ProductTypeGroupType group, final ProdTypeAttributeViewGroup ptavg) {
        ptavg.setDisplayName(processI18n(group.getDisplayName(), ptavg.getDisplayName()));
        if (group.getRank() != null) {
            ptavg.setRank(group.getRank());
        }
        ptavg.setGuid(group.getGuid());
        if (group.getGroupAttributes() != null) {
            ptavg.setAttrCodeList(StringUtils.join(group.getGroupAttributes().getGroupAttribute(), ','));
        }
    }

    private void processGroupsRemove(final ProductType domain, final ProductTypeGroupType group) {
        final Iterator<ProdTypeAttributeViewGroup> it = domain.getAttributeViewGroup().iterator();
        while (it.hasNext()) {
            final ProdTypeAttributeViewGroup next = it.next();
            if (group.getName().equals(next.getName())) {
                it.remove();
                return;
            }
        }
    }

    @Override
    protected ProductType getOrCreate(final ProductTypeTypeType xmlType) {
        ProductType productType = this.productTypeService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (productType != null) {
            return productType;
        }
        productType = this.attributeService.getGenericDao().getEntityFactory().getByIface(ProductType.class);
        productType.setGuid(xmlType.getGuid());
        productType.setShippable(true);
        return productType;
    }

    @Override
    protected EntityImportModeType determineImportMode(final ProductTypeTypeType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final ProductType domain) {
        return domain.getProducttypeId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param productTypeService product type
     */
    public void setProductTypeService(final ProductTypeService productTypeService) {
        this.productTypeService = productTypeService;
    }

    /**
     * Spring IoC.
     *
     * @param attributeService attribute service
     */
    public void setAttributeService(final AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}

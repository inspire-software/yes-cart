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

package org.yes.cart.service.domain.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Hibernate;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.GenericFTSCapableDAO;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultNavDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.dto.impl.ProductSearchResultDTOImpl;
import org.yes.cart.domain.dto.impl.ProductSearchResultNavDTOImpl;
import org.yes.cart.domain.dto.impl.ProductSearchResultPageDTOImpl;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.entity.impl.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.i18n.impl.NonI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dao.IndexBuilder;
import org.yes.cart.search.dao.entity.AdapterUtils;
import org.yes.cart.search.dao.support.ShopCategoryRelationshipSupport;
import org.yes.cart.search.dto.FilteredNavigationRecordRequest;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.ProductTypeAttrService;
import org.yes.cart.utils.HQLUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductServiceImpl extends BaseGenericServiceImpl<Product> implements ProductService {

    private final GenericFTSCapableDAO<Product, Long, Object> productDao;
    private final GenericFTSCapableDAO<ProductSku, Long, Object> productSkuDao;
    private final ProductSkuService productSkuService;
    private final ProductTypeAttrService productTypeAttrService;
    private final AttributeService attributeService;
    private final GenericDAO<ProductCategory, Long> productCategoryDao;
    private final GenericDAO<ProductTypeAttr, Long> productTypeAttrDao;
    private final ShopCategoryRelationshipSupport shopCategoryRelationshipSupport;

    /**
     * Construct product service.
     *
     * @param productDao         product dao
     * @param productSkuDao      product SKU dao
     * @param productSkuService  product service
     * @param productTypeAttrService     product type dao to deal with type information
     * @param attributeService   attribute service
     * @param productCategoryDao category dao to work with category information
     * @param productTypeAttrDao product type attributes need to work with range navigation
     * @param shopCategoryRelationshipSupport shop product category relationship support
     */
    public ProductServiceImpl(final GenericFTSCapableDAO<Product, Long, Object> productDao,
                              final GenericFTSCapableDAO<ProductSku, Long, Object> productSkuDao,
                              final ProductSkuService productSkuService,
                              final ProductTypeAttrService productTypeAttrService,
                              final AttributeService attributeService,
                              final GenericDAO<ProductCategory, Long> productCategoryDao,
                              final GenericDAO<ProductTypeAttr, Long> productTypeAttrDao,
                              final ShopCategoryRelationshipSupport shopCategoryRelationshipSupport) {
        super(productDao);
        this.productDao = productDao;
        this.productSkuDao = productSkuDao;
        this.productSkuService = productSkuService;
        this.productTypeAttrService = productTypeAttrService;
        this.attributeService = attributeService;
        this.productCategoryDao = productCategoryDao;
        this.productTypeAttrDao = productTypeAttrDao;
        this.shopCategoryRelationshipSupport = shopCategoryRelationshipSupport;
    }

    /** {@inheritDoc} */
    @Override
    public ProductSku getSkuById(final Long skuId) {
        return proxy().getSkuById(skuId, false);
    }

    /** {@inheritDoc} */
    @Override
    public ProductSku getSkuById(final Long skuId, final boolean withAttributes) {
        final ProductSku sku =  productSkuService.getGenericDao().findById(skuId);
        if (sku != null && withAttributes) {
            Hibernate.initialize(sku.getAttributes());
        }
        return sku;
    }


    /**
     * Get default image file name by given product.
     *
     * @param productId given id, which identify product
     * @return image file name if found.
     */
    @Override
    public String getDefaultImage(final Long productId) {
        final Map<Long, String> images = proxy().getAllProductsAttributeValues(AttributeNamesKeys.Product.PRODUCT_DEFAULT_IMAGE_ATTR_NAME);
        return images.get(productId);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ProductAttributesModel getProductAttributes(final long productId, final long skuId, final long productTypeId) {

        final ProductSku sku = skuId != 0L ? proxy().getSkuById(skuId, true) : null;
        final Product product = productId != 0L ? proxy().getProductById(productId, true) :
                (sku != null ? proxy().getProductById(sku.getProduct().getProductId(), true) : null);

        Collection<AttrValue> productAttrValues;
        Collection<AttrValue> skuAttrValues;

        if (sku != null) {
            productAttrValues = product.getAllAttributes();
            skuAttrValues = sku.getAllAttributes();
        } else if (product != null) {
            productAttrValues = product.getAllAttributes();
            skuAttrValues = Collections.emptyList();
        } else {
            return new ProductAttributesModelImpl(productId, null, skuId, null, productTypeId);
        }

        final List<ProductAttributesModelGroupImpl> attributeViewGroups = mapAttributeGroupsByAttributeCode(productTypeId);

        final ProductAttributesModelImpl attributesToShow;
        if (sku != null) {
            attributesToShow = new ProductAttributesModelImpl(product.getProductId(), product.getCode(), sku.getSkuId(), sku.getCode(), productTypeId, (List) attributeViewGroups);
        } else {
            attributesToShow = new ProductAttributesModelImpl(product.getProductId(), product.getCode(), 0L, null, productTypeId, (List) attributeViewGroups);
        }

        for (final AttrValue attrValue : productAttrValues) {

            loadAttributeValueToAttributesToShowMap(attributesToShow, attrValue);

        }

        for (final AttrValue attrValue : skuAttrValues) {

            loadAttributeValueToAttributesToShowMap(attributesToShow, attrValue);

        }

        return attributesToShow;
    }



    private void loadAttributeValueToAttributesToShowMap(final ProductAttributesModelImpl model, final AttrValue attrValue) {

        if (attrValue.getAttributeCode() == null) {
            return;
        }

        List<ProductAttributesModelAttribute> attributesInGroups = model.getAttributes(attrValue.getAttributeCode());
        if (attributesInGroups.isEmpty()) {
            return; // no need to show un-grouped attributes
        }

        final ProductAttributesModelValueImpl value = new ProductAttributesModelValueImpl(
                attrValue.getAttributeCode(),
                attrValue.getVal(),
                new FailoverStringI18NModel(attrValue.getDisplayVal(), attrValue.getVal(), true)
        );

        for (final ProductAttributesModelAttribute attributeInGroups : attributesInGroups) {
            ((ProductAttributesModelAttributeImpl) attributeInGroups).addValue(value);
        }
        
    }

    /*
        List of groups model pre-loaded with attributes definitions
     */
    private List<ProductAttributesModelGroupImpl> mapAttributeGroupsByAttributeCode(final long productTypeId) {

        final List<ProdTypeAttributeViewGroup> attributeViewGroup = productTypeAttrService.getViewGroupsByProductTypeId(productTypeId);

        if (CollectionUtils.isEmpty(attributeViewGroup)) {
            return Collections.emptyList();
        }

        final Map<String, I18NModel> attrDisplayNames = attributeService.getAllAttributeNames();

        final List<Attribute> multivalueAttrs = attributeService.findAttributesWithMultipleValues(AttributeGroupNames.PRODUCT);
        final Set<String> multivalueCodes = multivalueAttrs != null ? multivalueAttrs.stream().map(Attribute::getCode).collect(Collectors.toSet()) : Collections.emptySet();

        final List<ProductAttributesModelGroupImpl> groups = new ArrayList<>();
        for (final ProdTypeAttributeViewGroup group : attributeViewGroup) {
            final String[] attributesCodes = StringUtils.split(group.getAttrCodeList(),',');
            if (attributesCodes != null && attributesCodes.length > 0) {

                final ProductAttributesModelGroupImpl attrGroup = new ProductAttributesModelGroupImpl(
                        String.valueOf(group.getProdTypeAttributeViewGroupId()),
                        new FailoverStringI18NModel(group.getDisplayName(), group.getName(), true)
                );

                for (final String attrCode : attributesCodes) {

                    final I18NModel attributeI18n = attrDisplayNames.get(attrCode);
                    final boolean multivalue = multivalueCodes.contains(attrCode);

                    final ProductAttributesModelAttribute attribute;
                    if (attributeI18n != null) {
                        attribute = new ProductAttributesModelAttributeImpl(attrCode, multivalue,
                                new FailoverStringI18NModel(attributeI18n, attributeI18n.getValue(I18NModel.DEFAULT), true));
                    } else {
                        attribute = new ProductAttributesModelAttributeImpl(attrCode, multivalue, new NonI18NModel(attrCode));
                    }

                    attrGroup.addAttribute(attribute);
                }

                groups.add(attrGroup);
            }
        }
        return groups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductCompareModel getCompareAttributes(final List<Long> productId,
                                                    final List<Long> skuId) {

        final ProductCompareModelImpl pcm = new ProductCompareModelImpl();

        for (final Long id : productId) {

            final Product product = proxy().getProductById(id, true);
            if (product != null) {

                final ProductAttributesModel productView =
                        proxy().getProductAttributes(id, 0L, product.getProducttype().getProducttypeId());

                mergeCompareView(pcm, productView, "p_" + id);
            }

        }

        for (final Long id : skuId) {

            final ProductSku sku = proxy().getSkuById(id, true);
            if (sku != null) {

                final ProductAttributesModel productView =
                        proxy().getProductAttributes(sku.getProduct().getProductId(), id, sku.getProduct().getProducttype().getProducttypeId());

                mergeCompareView(pcm, productView, "s_" + id);

            }

        }

        return pcm;

    }


    private void mergeCompareView(final ProductCompareModelImpl pcm,
                                  final ProductAttributesModel add,
                                  final String id) {

        for (final ProductAttributesModelGroup addGroup : add.getGroups()) {

            ProductCompareModelGroupImpl grp = (ProductCompareModelGroupImpl) pcm.getGroup(addGroup.getCode());
            if (grp == null) {
                grp = new ProductCompareModelGroupImpl(
                        addGroup.getCode(),
                        new FailoverStringI18NModel(addGroup.getDisplayNames(), addGroup.getDisplayName(I18NModel.DEFAULT))
                );
                pcm.addGroup(grp);
            }

            for (final ProductAttributesModelAttribute addAttr : addGroup.getAttributes()) {

                ProductCompareModelAttributeImpl attr = (ProductCompareModelAttributeImpl) grp.getAttribute(addAttr.getCode());
                if (attr == null) {
                    attr = new ProductCompareModelAttributeImpl(
                            addAttr.getCode(),
                            addAttr.isMultivalue(),
                            new FailoverStringI18NModel(addAttr.getDisplayNames(), addAttr.getDisplayName(I18NModel.DEFAULT))
                    );

                    grp.addAttribute(attr);
                }

                for (final ProductAttributesModelValue addVal : addAttr.getValues()) {

                    attr.addValue(
                            new ProductCompareModelValueImpl(
                                add.getProductId(),
                                add.getProductCode(),
                                add.getSkuId(),
                                add.getSkuCode(),
                                addVal
                            )
                    );


                }

            }

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, String> getAllProductsAttributeValues(final String attributeCode) {
        final List<Object[]> values = (List) getGenericDao().findByNamedQuery("ALL.PRODUCT.ATTR.VALUE", attributeCode);
        if (values != null && !values.isEmpty()) {
            final Map<Long, String> map = new HashMap<>();
            for (final Object[] value : values) {
                map.put((Long) value[0], (String) value[1]);
            }
            return map;
        }
        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSku getProductSkuByCode(final String skuCode) {
        return productSkuService.getProductSkuBySkuCode(skuCode);
    }

    /**
     * Get product by sku code.
     *
     * @param skuCode sku code
     * @return product sku for this sku code
     */
    @Override
    public Product getProductBySkuCode(final String skuCode) {
        return (Product) productDao.getScalarResultByNamedQuery("PRODUCT.BY.SKU.CODE", skuCode);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Product getProductById(final Long productId) {
        // by default we use product with attributes, so true is better for caching
        return proxy().getProductById(productId, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product getProductById(final Long productId, final boolean withAttribute) {
        final Product prod = productDao.findById(productId); // query with
        if (prod != null && withAttribute) {
            Hibernate.initialize(prod.getAttributes());
            Hibernate.initialize(prod.getOptions().getConfigurationOption());
        }
        return prod;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSearchResultPageDTO getProductSearchResultDTOByQuery(final NavigationContext navigationContext,
                                                                       final int firstResult,
                                                                       final int maxResults,
                                                                       final String sortFieldName,
                                                                       final boolean reverse) {

        final Pair<List<Object[]>, Integer> searchRez = productDao.fullTextSearch(
                navigationContext.getProductQuery(),
                firstResult,
                maxResults,
                sortFieldName,
                reverse,
                AdapterUtils.FIELD_PK,
                AdapterUtils.FIELD_CLASS,
                AdapterUtils.FIELD_OBJECT
        );

        final List<ProductSearchResultDTO> rez = new ArrayList<>(searchRez.getFirst().size());
        for (Object[] obj : searchRez.getFirst()) {
            final ProductSearchResultDTO dto = AdapterUtils.readObjectFieldValue((String) obj[2], ProductSearchResultDTOImpl.class);
            rez.add(dto);
        }

        return new ProductSearchResultPageDTOImpl(rez, firstResult, maxResults, searchRez.getSecond(), sortFieldName, reverse);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductSearchResultNavDTO findFilteredNavigationRecords(final NavigationContext baseNavigationContext, final List<FilteredNavigationRecordRequest> request) {
        return new ProductSearchResultNavDTOImpl(productDao.fullTextSearchNavigation(baseNavigationContext.getProductQuery(), request));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getProductQty(final NavigationContext navigationContext) {
        return productDao.fullTextSearchCount(navigationContext.getProductQuery());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Integer, Integer> findProductQtyAll() {

        final int total = getGenericDao().findCountByCriteria(null);
        final int active = total; // TODO find a way to detect active, potentially count of distinct inventory?

        return new Pair<>(total, active);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> getProductByIdList(final List idList) {
        if (idList == null || idList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return productDao.findByNamedQuery("PRODUCTS.LIST.BY.IDS", idList);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Long findProductIdBySeoUri(final String seoUri) {
        List<Object> list = productDao.findQueryObjectByNamedQuery("PRODUCT.ID.BY.SEO.URI", seoUri);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Long findProductIdByGUID(final String guid) {
        List<Object> list = productDao.findQueryObjectByNamedQuery("PRODUCT.ID.BY.GUID", guid);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Long findProductIdByCode(final String code) {
        List<Object> list = productDao.findQueryObjectByNamedQuery("PRODUCT.ID.BY.CODE", code);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findProductIdsByManufacturerCode(final String code) {
        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.IDS.BY.MANUFACTURER.CODE", code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findProductIdsByBarCode(final String code) {
        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.IDS.BY.SKU.BARCODE", code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findProductIdsByBarCodes(final Collection<String> codes) {
        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.IDS.BY.SKU.BARCODES", codes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findProductIdsByPimCode(final String code) {
        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.IDS.BY.PIM.CODE", code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> findProductIdsByAttributeValue(final String attrCode, final String attrValue) {
        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.IDS.BY.ATTRIBUTE.CODE.AND.VALUE", attrCode, attrValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findSeoUriByProductId(final Long productId) {
        List<Object> list = productDao.findQueryObjectByNamedQuery("SEO.URI.BY.PRODUCT.ID", productId);
        if (list != null && !list.isEmpty()) {
            final Object[] uriAndId = (Object[]) list.get(0);
            if (uriAndId[0] instanceof String) {
                return (String) uriAndId[0];
            }
            return String.valueOf(uriAndId[1]);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findProductSkuIdBySeoUri(final String seoUri) {
        List<Object> list = productSkuService.getGenericDao().findQueryObjectByNamedQuery("SKU.ID.BY.SEO.URI", seoUri);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findProductSkuIdByGUID(final String guid) {
        List<Object> list = productSkuService.getGenericDao().findQueryObjectByNamedQuery("SKU.ID.BY.GUID", guid);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long findProductSkuIdByCode(final String code) {
        List<Object> list = productSkuService.getGenericDao().findQueryObjectByNamedQuery("SKU.ID.BY.CODE", code);
        if (list != null && !list.isEmpty()) {
            final Object id = list.get(0);
            if (id instanceof Long) {
                return (Long) id;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findSeoUriByProductSkuId(final Long skuId) {
        List<Object> list = productSkuService.getGenericDao().findQueryObjectByNamedQuery("SEO.URI.BY.SKU.ID", skuId);
        if (list != null && !list.isEmpty()) {
            final Object[] uriAndId = (Object[]) list.get(0);
            if (uriAndId[0] instanceof String) {
                return (String) uriAndId[0];
            }
            return String.valueOf(uriAndId[1]);
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public IndexBuilder.FTIndexState getProductsFullTextIndexState() {
        return productDao.getFullTextIndexState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexBuilder.FTIndexState getProductsSkuFullTextIndexState() {
        return productSkuDao.getFullTextIndexState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProducts(final int batchSize) {
        productDao.fullTextSearchReindex(true, batchSize);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProducts(final int batchSize, final boolean async) {
        productDao.fullTextSearchReindex(async, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductsSku(final int batchSize) {
        productSkuDao.fullTextSearchReindex(true, batchSize);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductsSku(final int batchSize, final boolean async) {
        productSkuDao.fullTextSearchReindex(async, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProducts(final Long shopId, final int batchSize) {
        final Set<Long> categories = shopCategoryRelationshipSupport.getShopCategoriesIds(shopId);
        productDao.fullTextSearchReindex(true, batchSize);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductsSku(final Long shopId, final int batchSize) {
        final Set<Long> categories = shopCategoryRelationshipSupport.getShopCategoriesIds(shopId);
        productSkuDao.fullTextSearchReindex(true, batchSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProduct(final Long pk) {
        final Product product = findById(pk);
        if (product != null) {
            for (final ProductSku sku : product.getSku()) {
                productSkuDao.fullTextSearchReindex(sku.getSkuId());
            }
            productDao.fullTextSearchReindex(pk);
        } else {
            productDao.fullTextSearchReindex(pk, true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductSku(final Long pk) {
        final ProductSku productSku = productSkuService.findById(pk);
        if (productSku != null) {
            productSkuDao.fullTextSearchReindex(productSku.getSkuId());
            productDao.fullTextSearchReindex(productSku.getProduct().getProductId());
        } else {
            productSkuDao.fullTextSearchReindex(pk, true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindexProductSku(final String code) {
        final ProductSku productSku = productSkuService.findProductSkuBySkuCode(code);
        if (productSku != null) {
            productSkuDao.fullTextSearchReindex(productSku.getSkuId());
            productDao.fullTextSearchReindex(productSku.getProduct().getProductId());
        }
    }

    private Pair<String, Object[]> findProductQuery(final boolean count,
                                                    final String sort,
                                                    final boolean sortDescending,
                                                    final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        final List categoryIds = currentFilter != null ? currentFilter.remove("categoryIds") : null;
        if (CollectionUtils.isNotEmpty(categoryIds)) {
            if (count) {
                hqlCriteria.append("select count(distinct p.productId) from ProductEntity p join p.productCategory c where c.category.categoryId in (?1) ");
            } else {
                hqlCriteria.append("select distinct p from ProductEntity p join p.productCategory c where c.category.categoryId in (?1) ");
            }
            params.add(categoryIds);
        } else {
            if (count) {
                hqlCriteria.append("select count(p.productId) from ProductEntity p ");
            } else {
                hqlCriteria.append("select p from ProductEntity p ");
            }
        }

        final List supplierCatalogCodes = currentFilter != null ? currentFilter.remove("supplierCatalogCodes") : null;
        if (CollectionUtils.isNotEmpty(supplierCatalogCodes)) {
            if (params.size() > 0) {
                hqlCriteria.append(" and (p.supplierCatalogCode is null or p.supplierCatalogCode in (?").append(params.size() + 1).append(")) ");
            } else {
                hqlCriteria.append(" where (p.supplierCatalogCode is null or p.supplierCatalogCode in (?1)) ");
            }
            params.add(supplierCatalogCodes);
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "p", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by p." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }





    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findProducts(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findProductQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findProductCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findProductQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findProductByCodeNameBrandType(final String code,
                                                        final String name,
                                                        final Long brandId,
                                                        final Long productTypeId) {

        return productDao.findByNamedQuery(
                "PRODUCT.BY.CODE.NAME.BRAND.TYPE",
                HQLUtils.criteriaIlikeAnywhere(code),
                NumberUtils.toLong(code),
                HQLUtils.criteriaIlikeAnywhere(name),
                brandId,
                productTypeId
        );

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findProductSupplierCatalogCodes() {

        return (List) productDao.findQueryObjectByNamedQuery("PRODUCT.SUPPLIER.CATALOG.CODES");

    }

    /**
     * Persist product. Default sku will be created.
     *
     * @param instance instance to persist
     * @return persisted instance
     */
    @Override
    public Product create(final Product instance) {

        ProductSku sku = productDao.getEntityFactory().getByIface(ProductSku.class);
        sku.setCode(instance.getCode());
        sku.setName(instance.getName());
        sku.setDisplayName(instance.getDisplayName());
        sku.setDescription(instance.getDescription());
        sku.setProduct(instance);
        sku.setRank(0);
        sku.setSupplierCatalogCode(instance.getSupplierCatalogCode());
        instance.getSku().add(sku);

        return getGenericDao().create(instance);
    }


    private ProductService proxy;

    private ProductService proxy() {
        if (proxy == null) {
            proxy = getSelf();
        }
        return proxy;
    }

    /**
     * @return self proxy to reuse AOP caching
     */
    public ProductService getSelf() {
        // Spring lookup method to get self proxy
        return null;
    }


}

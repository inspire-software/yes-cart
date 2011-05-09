package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoCollection;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.AttrValueCategoryDTO;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.entity.impl.AttrValueEntityCategory;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class CategoryDTOImpl implements CategoryDTO  {

    private static final long serialVersionUID = 20100717L;

    @DtoField(value = "categoryId")
    private long categoryId;

    @DtoField(value = "parentId")
    private long parentId;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "productType.producttypeId", readOnly = true)
    private Long productTypeId;

    @DtoField(value = "productType.name", readOnly = true)
    private String productTypeName;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "uitemplate")
    private String uitemplate;

    @DtoField(value = "availablefrom")
    private Date availablefrom;

    @DtoField(value = "availabletill")
    private Date availabletill;

    @DtoField(value = "seo.seoId", readOnly = true)
    private Long seoId;

    @DtoField(value = "navigationByAttributes")
    private Boolean navigationByAttributes;

    @DtoField(value = "navigationByBrand")
    private Boolean navigationByBrand;

    @DtoField(value = "navigationByPrice")
    private Boolean navigationByPrice;

    @DtoField(value = "navigationByPriceTiers")
    private String navigationByPriceTiers;


    @DtoCollection(
            value="attribute",
            dtoBeanKey="org.yes.cart.domain.dto.AttrValueCategoryDTO",
            entityGenericType = AttrValueEntityCategory.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = HashSet.class,
            dtoToEntityMatcher = AttrValueCategoryMatcher.class,
            readOnly = true
            )
    private Set<AttrValueCategoryDTO> attribute;


    private List<CategoryDTO> children;

    /** {@inheritDoc}*/
    public String getProductTypeName() {
        return productTypeName;
    }

    /** {@inheritDoc}*/
    public void setProductTypeName(final String productTypeName) {
        this.productTypeName = productTypeName;
    }

    /** {@inheritDoc}*/
    public List<CategoryDTO> getChildren() {
        return children;
    }

    /** {@inheritDoc}*/
    public void setChildren(List<CategoryDTO> children) {
        this.children = children;
    }

    /** {@inheritDoc}*/
    public long getCategoryId() {
        return categoryId;
    }

    /** {@inheritDoc}*/
    public void setCategoryId(final long categoryId) {
        this.categoryId = categoryId;
    }

    /** {@inheritDoc}*/
    public long getParentId() {
        return parentId;
    }

    /** {@inheritDoc}*/
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    /** {@inheritDoc}*/
    public int getRank() {
        return rank;
    }

    /** {@inheritDoc}*/
    public void setRank(final int rank) {
        this.rank = rank;
    }

    /** {@inheritDoc}*/
    public Long getProductTypeId() {
        return productTypeId;
    }

    /** {@inheritDoc}*/
    public void setProductTypeId(final Long productTypeId) {
        this.productTypeId = productTypeId;
    }

    /** {@inheritDoc}*/
    public String getName() {
        return name;
    }

    /** {@inheritDoc}*/
    public void setName(final String name) {
        this.name = name;
    }

    /** {@inheritDoc}*/
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc}*/
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc}*/
    public String getUitemplate() {
        return uitemplate;
    }

    /** {@inheritDoc}*/
    public void setUitemplate(final String uitemplate) {
        this.uitemplate = uitemplate;
    }

    /** {@inheritDoc}*/
    public Date getAvailablefrom() {
        return availablefrom;
    }

    /** {@inheritDoc}*/
    public void setAvailablefrom(final Date availablefrom) {
        this.availablefrom = availablefrom;
    }

    /** {@inheritDoc}*/
    public Date getAvailabletill() {
        return availabletill;
    }

    /** {@inheritDoc}*/
    public void setAvailabletill(final Date availabletill) {
        this.availabletill = availabletill;
    }

    /** {@inheritDoc}*/
    public Long getSeoId() {
        return seoId;
    }

    /** {@inheritDoc}*/
    public void setSeoId(final Long seoId) {
        this.seoId = seoId;
    }

    /** {@inheritDoc}*/
    public Boolean getNavigationByAttributes() {
        return navigationByAttributes;
    }

    /** {@inheritDoc}*/
    public void setNavigationByAttributes(final Boolean navigationByAttributes) {
        this.navigationByAttributes = navigationByAttributes;
    }

    /** {@inheritDoc}*/
    public Boolean getNavigationByBrand() {
        return navigationByBrand;
    }

    /** {@inheritDoc}*/
    public void setNavigationByBrand(final Boolean navigationByBrand) {
        this.navigationByBrand = navigationByBrand;
    }

    /** {@inheritDoc}*/
    public Boolean getNavigationByPrice() {
        return navigationByPrice;
    }

    /** {@inheritDoc}*/
    public void setNavigationByPrice(final Boolean navigationByPrice) {
        this.navigationByPrice = navigationByPrice;
    }

    /** {@inheritDoc}*/
    public String getNavigationByPriceTiers() {
        return navigationByPriceTiers;
    }

    /** {@inheritDoc}*/
    public void setNavigationByPriceTiers(final String navigationByPriceTiers) {
        this.navigationByPriceTiers = navigationByPriceTiers;
    }

    /** {@inheritDoc}*/
    public Set<AttrValueCategoryDTO> getAttribute() {
        return attribute;
    }

    /** {@inheritDoc}*/
    public void setAttribute(final Set<AttrValueCategoryDTO> attribute) {
        this.attribute = attribute;
    }
}

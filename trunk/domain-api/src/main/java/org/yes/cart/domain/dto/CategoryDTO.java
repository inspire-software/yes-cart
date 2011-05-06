package org.yes.cart.domain.dto;


import java.util.Date;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

/**
 *
 * Represent category as DTO object.
 * Price tiers edit perform via changing XML.    
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CategoryDTO extends Serializable {

    /**
     * Get category pk value.
     * @return category pk value
     */
    long getCategoryId();

    /**
     * Set category pk value.
     * @param categoryId pk value
     */
    void setCategoryId(long categoryId);

    /**
     * Get parrent pk value.
     * @return parrent pk value.
     */
    long getParentId();

    /**
     * Set parrent pk value.
     * @param parentId parrent pk value.
     */
    void setParentId(long parentId);

    /**
     * Get category rank inside parent category.
     * @return category rank.
     */
    int getRank();

    /**
     * Set category rank.
     * @param rank category rank
     */
    void setRank(int rank);

    /**
     * Default product type in category.
     * Set it, to allow filtered navigation by attributes.
     *
     * @return default product type in category
     */
    Long getProductTypeId();

    /**
     * Set default product type id
     * @param productTypeId default product type.
     */
    void setProductTypeId(Long productTypeId);

    /**
     * Get default product type name
     * @return default product type name if any
     */
    String getProductTypeName();

    /**
     * Set default product type name.
     * @param productTypeName  default product type name
     */
    void setProductTypeName(String productTypeName);
    

    /**
     * Get category name.
     * @return category name.
     */
    String getName();

    /**
     * Set category name.
     * @param name category name.
     */
    void setName(String name);

    /**
     * Get category description.
     * @return category decription.
     */
    String getDescription();

    /**
     * Set description
     * @param description description
     */
    void setDescription(String description);

    /**
     * Get category UI template variation.
     * @return category UI template variation.
     */
    String getUitemplate();

    /**
     * Set category UI template varioantion.
     * @param uitemplate template varioantion.
     */
    void setUitemplate(String uitemplate);

    /**
     * Get available from date.  Null value means no start.
     * @return available from date.
     */
    Date getAvailablefrom();

    /**
     * Set available from date.
     * @param availablefrom available from date.
     */
    void setAvailablefrom(Date availablefrom);

    /**
     * Get available till date.  Null value means no end date.
     * @return available till date.
     */
    Date getAvailabletill();

    /**
     * Set available till date.
     * @param availabletill available till date.
     */
    void setAvailabletill(Date availabletill);

    /**
     * Get seo id fk value.
     * @return seo id fk value
     */
    Long getSeoId();

    /**
     * Set seo id fk value.
     * @param seoId
     */
    void setSeoId(Long seoId);

    /**
     * @return true if filtered navigation by attributes allowed
     */
    Boolean getNavigationByAttributes();

    /**
     * Set nagivation by attribute values.
     * @param navigationByAttributes nagivation by attribute values flag.
     */
    void setNavigationByAttributes(Boolean navigationByAttributes);

    /**
     * @return true if filtered navigation by brand allowed
     */
    Boolean getNavigationByBrand();

    /**
     * Set nagivation by brand in category.
     * @param navigationByBrand flag to set
     */
    void setNavigationByBrand(Boolean navigationByBrand);

    /**
     * @return true if filtered navigation by price ranges allowed
     */
    Boolean getNavigationByPrice();

    /**
     * Set nagivation by price.
     * @param navigationByPrice  nagivation by price flag.
     */
    void setNavigationByPrice(Boolean navigationByPrice);

    /**
     * Optional price range configuration. Default shop price tiers configuration will used if empty.
     *
     * @return price range configuration for price filtered navigation
     */
    String getNavigationByPriceTiers();

    /**
     * Set price range configuration for price filtered navigation.
     *
     * @param navigationByPriceTiers price range configuration.
     */
    void setNavigationByPriceTiers(String navigationByPriceTiers);

    /**
     * Children of this category
     * @return Children list.
     */
    List<CategoryDTO> getChildren();

    /**
     * Set children list.
     * @param children children list.
     */
    void setChildren(List<CategoryDTO> children);


    /**
     * Get attibutes.
     * @return list of attributes
     */
    Set<AttrValueCategoryDTO> getAttribute();

    /**
     * Set list of attributes.
     * @param attribute list of attributes
     */
    void setAttribute(Set<AttrValueCategoryDTO> attribute);

}

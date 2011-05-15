package org.yes.cart.impl {
import mx.collections.ArrayCollection;

[Bindable]
[RemoteClass(alias="org.yes.cart.impl.ProductDTOImpl")]


public class ProductDTOImpl {

    public var productId:Number;

    public var code:String ;

    public var availablefrom:Date ;

    public var availabletill:Date ;

    public var availabilityDTO:AvailabilityDTOImpl;

    public var brandDTO:BrandDTOImpl;

    public var productTypeDTO:ProductTypeDTOImpl;

    public var productCategoryDTOs:ArrayCollection; //of ProductCategoryDTOImpl

    public var seoDTO:SeoDTOImpl;

    public var name:String;

    public var description:String ;

    public var featured:Boolean;

    public var attribute:ArrayCollection; // of AttrValueProductDTO


    public function ProductDTOImpl() {
    }

    /**
     * Get the org.yes.cart.ui.product rank for given org.yes.cart.ui.category id
     * @param categoryId given org.yes.cart.ui.category id
     * @return rank if found, -1 otherwise
     */
    public function getRank(categoryId:Number):Number {
        if (productCategoryDTOs  != null) {
            for each (var pcDto:ProductCategoryDTOImpl in productCategoryDTOs) {
                if (pcDto.categoryId == categoryId) {
                    return pcDto.rank;
                }
            }
        }
        return -1;
    }

    /**
     * Get the org.yes.cart.ui.product rank for given org.yes.cart.ui.category id
     * @param categoryId given org.yes.cart.ui.category id
     * @return rank if found, -1 otherwise
     */
    public function getProductCategoryDTO(categoryId:Number):ProductCategoryDTOImpl {
        if (productCategoryDTOs  != null) {
            for each (var pcDto:ProductCategoryDTOImpl in productCategoryDTOs) {
                if (pcDto.categoryId == categoryId) {
                    return pcDto;
                }
            }
        }
        return null;
    }


    public function toString():String {
        return "ProductDTOImpl{productId=" + String(productId)
                + ",code=" + String(code)
                + ",availablefrom=" + String(availablefrom)
                + ",availabletill=" + String(availabletill)
                + ",availabilityDTO=" + String(availabilityDTO)
                + ",brandDTO=" + String(brandDTO)
                + ",productTypeDTO=" + String(productTypeDTO)
                + ",productCategoryDTOs=" + String(productCategoryDTOs)
                + ",seoDTO=" + String(seoDTO)
                + ",name=" + String(name)
                + ",description=" + String(description)
                + ",featured=" + String(featured) + "}";
    }
}
}
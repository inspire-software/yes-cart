/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.web.page.component.product;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.ProductAvailabilityModel;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.*;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.AttributableImageService;
import org.yes.cart.web.util.WicketUtil;

import java.math.BigDecimal;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/8/11
 * Time: 11:43 AM
 */
public class ProductInListView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String PRODUCT_LINK_SKU = "productLinkSku";
    private final static String SKU_CODE_LABEL = "skuCode";
    private final static String PRODUCT_LINK_NAME = "productLinkName";
    private final static String NAME_LABEL = "name";
    private final static String DESCRIPTION_LABEL = "description";
    private final static String ADD_TO_CART_LINK = "addToCartLink";
    private final static String ADD_TO_CART_LINK_LABEL = "addToCartLinkLabel";
    private final static String PRICE_VIEW = "priceView";
    private final static String PRODUCT_LINK_IMAGE = "productLinkImage";
    private final static String PRODUCT_IMAGE = "productDefaultImage";
    // ------------------------------------- MARKUP IDs END ------------------------------------ //

    private final ProductSearchResultDTO product;

    private final Category category;


    @SpringBean(name = StorefrontServiceSpringKeys.ATTRIBUTABLE_IMAGE_SERVICE)
    private AttributableImageService attributableImageService;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.PRICE_SERVICE)
    protected PriceService priceService;

    @SpringBean(name = ServiceSpringKeys.IMAGE_SERVICE)
    protected ImageService imageService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_AVAILABILITY_STRATEGY)
    private ProductAvailabilityStrategy productAvailabilityStrategy;

    private final String[] defImgSize;


    /**
     * Construct product view, that show product in grid.
     *
     * @param id         view od
     * @param product    product model
     * @param category   product in category, optional parameter
     * @param defImgSize image size in given category
     */
    public ProductInListView(final String id, final ProductSearchResultDTO product, final Category category, final String[] defImgSize) {
        super(id);
        if (category == null) {
            this.category = categoryService.getRootCategory();
        } else {
            this.category = category;
        }
        this.product = product;
        this.defImgSize = defImgSize;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final LinksSupport links = getWicketSupportFacade().links();

        final String selectedLocale = getLocale().getLanguage();
        final PageParameters linkToProductParameters = links.getFilteredCurrentParameters(getPage().getPageParameters());
        linkToProductParameters.set(WebParametersKeys.PRODUCT_ID, product.getId());


        final String width = defImgSize[0];
        final String height = defImgSize[1];

        add(links.newProductLink(PRODUCT_LINK_SKU, product.getId(), getPage().getPageParameters())
                .add(new Label(SKU_CODE_LABEL, product.getCode()))
        );

        add(
                new Label(DESCRIPTION_LABEL, product.getDescription(selectedLocale)).setEscapeModelStrings(false)
        );

        add(links.newProductLink(PRODUCT_LINK_NAME, product.getId(), getPage().getPageParameters())
                .add(new Label(NAME_LABEL, product.getName(selectedLocale)))
        );


        add(links.newProductLink(PRODUCT_LINK_IMAGE, product.getId(), getPage().getPageParameters())
                .add(
                        new ContextImage(PRODUCT_IMAGE, getDefaultImage(width, height))
                                .add(new AttributeModifier(HTML_WIDTH, width))
                                .add(new AttributeModifier(HTML_HEIGHT, height))
                )
        );


        final ProductAvailabilityModel skuPam = productAvailabilityStrategy.getAvailabilityModel(ShopCodeContext.getShopId(), product);

        add(links.newAddToCartLink(ADD_TO_CART_LINK, skuPam.getFirstAvailableSkuCode(), null, getPage().getPageParameters())
                        .add(new Label(ADD_TO_CART_LINK_LABEL, skuPam.isInStock() || skuPam.isPerpetual() ?
                                getLocalizer().getString("add.to.cart", this) :
                                getLocalizer().getString("preorder.cart", this)))
                        .setVisible(skuPam.isAvailable())
        );

        add(
                new PriceView(PRICE_VIEW, new Model<SkuPrice>(getSkuPrice(skuPam.getFirstAvailableSkuCode())), true, true)
        );


        super.onBeforeRender();
    }


    /**
     * Get product or his sku price.
     * In case of multisku product the minimal regular price from multiple sku was used for single item.
     *
     * @param firstAvailableSkuCode first available sku code.
     * @return {@link org.yes.cart.domain.entity.SkuPrice}
     */
    private SkuPrice getSkuPrice(final String firstAvailableSkuCode) {
        return priceService.getMinimalRegularPrice(
                null,
                firstAvailableSkuCode,
                ApplicationDirector.getCurrentShop(),
                ApplicationDirector.getShoppingCart().getCurrencyCode(),
                BigDecimal.ONE
        );
    }

    /**
     * {@inheritDoc}
     */
    /*public String getDescription(final String locale) {
        final Pair<String, String> desc = productService.getProductAttribute(
                locale, product.getId(), 0L, AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX + locale);
        if (desc == null) {
            return product.getDescription();
        }
        return desc.getSecond();
    }  */

    /**
     * {@inheritDoc}
     */
    public String getDefaultImage(final String width, final String height) {

        final Logger log = ShopCodeContext.getLog(this);

        final String result = attributableImageService.getImageURI(
                product.getDefaultImage(),
                width,
                height,
                WicketUtil.getHttpServletRequest().getContextPath(),
                product
        );

        if (log.isInfoEnabled()) {

            log.info("Default image is [" + product.getDefaultImage() + "]  result is [" + result + "]");

        }



        return  result;
    }


}

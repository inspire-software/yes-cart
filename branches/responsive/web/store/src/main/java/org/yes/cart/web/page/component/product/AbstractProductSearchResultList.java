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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.service.domain.*;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.AttributableImageService;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * Abstract class to present products list.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 9/18/11
 * Time: 11:06 AM
 */
public abstract class AbstractProductSearchResultList extends BaseComponent {

    final static  String [] thumbnailSize =
            new String [] {
                    AttributeNamesKeys.Category.PRODUCT_IMAGE_TUMB_WIDTH,
                    AttributeNamesKeys.Category.PRODUCT_IMAGE_TUMB_HEIGHT
            };


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    protected final static String NAME = "name";
    protected final static String PRODUCT_LIST = "products";
    protected final static String PRODUCT_NAME_LINK = "productLinkName";
    protected final static String PRODUCT_LINK_IMAGE = "productLinkImage";
    protected final static String PRODUCT_IMAGE = "productDefaultImage";
    // ------------------------------------- MARKUP IDs END   ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.PRODUCT_ASSOCIATIONS_SERVICE)
    protected ProductAssociationService productAssociationService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SKU_SERVICE)
    protected ProductSkuService productSkuService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;

    @SpringBean(name = StorefrontServiceSpringKeys.ATTRIBUTABLE_IMAGE_SERVICE)
    protected AttributableImageService attributableImageService;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    protected CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.IMAGE_SERVICE)
    protected ImageService imageService;

    private final boolean nameLinkVisible;


    /**
     * Construct product list to show.
     *
     * @param id              component id.
     * @param nameLinkVisible true in case if need to show link with product name
     */
    public AbstractProductSearchResultList(final String id, final boolean nameLinkVisible) {
        super(id);
        this.nameLinkVisible = nameLinkVisible;
    }


    /**
     * Get the value map with parameter to show the product.
     *
     * @param prod given product product
     * @return value map with parameter to show the product
     */
    protected PageParameters getProductPageParameters(final Product prod) {
        return WicketUtil.getFilteredRequestParameters(
                getPage().getPageParameters())
                .set(WebParametersKeys.PRODUCT_ID, String.valueOf(prod.getProductId()));

    }

    /**
     * Get list of product to show.
     *
     * @return list of products to show.
     */
    public abstract List<ProductSearchResultDTO> getProductListToShow();


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final String selectedLocale = getLocale().getLanguage();

        final Category category;
        final long categ_id = WicketUtil.getCategoryId(getPage().getPageParameters());
        if (categ_id > 0) {
            Category ccandidate = categoryService.getById(categ_id);
            if (ccandidate == null) {
                category = categoryService.getRootCategory();
            } else {
                category = ccandidate;
            }
        } else {
            category = categoryService.getRootCategory();
        }


        add(
                new ListView<ProductSearchResultDTO>(PRODUCT_LIST, getProductListToShow()) {
                    protected void populateItem(ListItem<ProductSearchResultDTO> listItem) {

                        final ProductSearchResultDTO prod = listItem.getModelObject();

                        final String[] size = getThumbnailImageSize(category);

                        final String width = size[0];
                        final String height = size[1];

                        final LinksSupport links = getWicketSupportFacade().links();

                        final String prodName = prod.getName(selectedLocale);

                        listItem.add(
                                links.newProductLink(PRODUCT_LINK_IMAGE, prod.getId(), getPage().getPageParameters())
                                .add(
                                        new ContextImage(PRODUCT_IMAGE, getDefaultImage(prod, width, height))
                                                .add(new AttributeModifier(HTML_WIDTH, width))
                                                .add(new AttributeModifier(HTML_HEIGHT, height))
                                                .add(new AttributeModifier(HTML_TITLE, prodName))
                                                .add(new AttributeModifier(HTML_ALT, prodName))
                                )
                        );
                        listItem.add(
                                links.newProductLink(PRODUCT_NAME_LINK, prod.getId(), getPage().getPageParameters())
                                        .add(new Label(NAME, prodName).setEscapeModelStrings(false))
                                        .setVisible(nameLinkVisible)
                        );
                    }

                }
        );

        super.onBeforeRender();
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultImage(final ProductSearchResultDTO product, final String width, final String height) {

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


    public String[] getThumbnailImageSize(final Category category) {
        return categoryService.getCategoryAttributeRecursive(
                null, category,
                thumbnailSize
        );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {
        return super.isVisible() && !getProductListToShow().isEmpty();
    }


}

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
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.AttributableImageService;
import org.yes.cart.web.support.service.CategoryServiceFacade;
import org.yes.cart.web.support.service.ProductServiceFacade;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * Abstract class to present products list.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 9/18/11
 * Time: 11:06 AM
 */
public abstract class AbstractProductSearchResultList extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    protected final static String NAME = "name";
    protected final static String PRODUCT_LIST = "products";
    protected final static String PRODUCT_NAME_LINK = "productLinkName";
    protected final static String PRODUCT_LINK_IMAGE = "productLinkImage";
    protected final static String PRODUCT_IMAGE = "productDefaultImage";
    // ------------------------------------- MARKUP IDs END   ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_IMAGE_SERVICE)
    protected AttributableImageService productImageService;

    @SpringBean(name = StorefrontServiceSpringKeys.CATEGORY_SERVICE_FACADE)
    protected CategoryServiceFacade categoryServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_SERVICE_FACADE)
    protected ProductServiceFacade productServiceFacade;

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

        final long categoryId = WicketUtil.getCategoryId(getPage().getPageParameters());
        final Pair<String, String> thumbWidthHeight = categoryServiceFacade.getThumbnailSizeConfig(categoryId, ShopCodeContext.getShopId());

        addOrReplace(
                new ListView<ProductSearchResultDTO>(PRODUCT_LIST, getProductListToShow())
                {
                    protected void populateItem(ListItem<ProductSearchResultDTO> listItem)
                    {

                        onBeforeRenderPopulateListItem(listItem, selectedLocale, thumbWidthHeight);
                    }


                }
        );

        onBeforeRenderSetVisibility();

        super.onBeforeRender();
    }

    /**
     * Extension hook to set visibility flag of this component.
     */
    protected void onBeforeRenderSetVisibility() {

        setVisible(!getProductListToShow().isEmpty());

    }

    /**
     * Extension hook for list item data population.
     *
     * @param listItem list item
     * @param selectedLocale locale
     * @param thumbWidthHeight thum dimensions
     */
    protected void onBeforeRenderPopulateListItem(final ListItem<ProductSearchResultDTO> listItem,
                                                  final String selectedLocale,
                                                  final Pair<String, String> thumbWidthHeight)
    {
        final ProductSearchResultDTO prod = listItem.getModelObject();

        final String width = thumbWidthHeight.getFirst();
        final String height = thumbWidthHeight.getSecond();

        final LinksSupport links = getWicketSupportFacade().links();

        final String prodName = prod.getName(selectedLocale);

        listItem.add(
                links.newProductLink(PRODUCT_LINK_IMAGE, prod.getId(), getPage().getPageParameters())
                        .add(
                                new ContextImage(PRODUCT_IMAGE, getDefaultImage(prod, width, height, selectedLocale))
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


    /**
     * {@inheritDoc}
     */
    public String getDefaultImage(final ProductSearchResultDTO product, final String width, final String height, final String locale) {

        final Logger log = ShopCodeContext.getLog(this);

        final String result = productImageService.getImageURI(
                product, WicketUtil.getHttpServletRequest().getContextPath(), locale, width, height, product.getDefaultImage()
        );

        if (log.isInfoEnabled()) {

            log.info("Default image is [" + product.getDefaultImage() + "]  result is [" + result + "]");

        }



        return  result;
    }

}

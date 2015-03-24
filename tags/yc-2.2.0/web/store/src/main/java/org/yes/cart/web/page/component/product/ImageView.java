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
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.entity.decorator.Depictable;
import org.yes.cart.web.support.i18n.I18NWebSupport;
import org.yes.cart.web.support.service.CategoryServiceFacade;
import org.yes.cart.web.util.WicketUtil;

import java.util.Collections;
import java.util.List;

/**
 * Product or sku images view.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 9/24/11
 * Time: 11:45 AM
 */
public class ImageView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    protected final static String DEFAULT_IMAGE = "defaultImage";
    protected final static String DEFAULT_IMAGE_REF = "defaultImageRef";
    protected final static String ALT_IMAGE_LIST = "altImageList";
    protected final static String ALT_IMAGE = "altImage";
    protected final static String ALT_IMAGE_REF = "altImageRef";
    // ------------------------------------- MARKUP IDs END   ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CATEGORY_SERVICE_FACADE)
    protected CategoryServiceFacade categoryServiceFacade;

    private final Depictable depictable;


    /**
     * Construct product or sku image view.
     *
     * @param id         component id.
     * @param depictable the something that can be shown.
     */
    public ImageView(final String id, final Depictable depictable) {
        super(id);
        this.depictable = depictable;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        long shopId = ShopCodeContext.getShopId();
        long categoryId = WicketUtil.getCategoryId(getPage().getPageParameters());

        final Pair<String, String> imageSize = categoryServiceFacade.getProductListImageSizeConfig(categoryId, shopId);
        final Pair<String, String> thumbSize = categoryServiceFacade.getThumbnailSizeConfig(categoryId, shopId);

        final String width = imageSize.getFirst();
        final String height = imageSize.getSecond();

        final String tumbWidth = thumbSize.getFirst();
        final String tumbHeight = thumbSize.getSecond();

        final String lang = getLocale().getLanguage();

        final List<Pair<String, String>> allImageAttrWithFileName = depictable.getImageAttributeFileNames(lang);

        final Pair<String, String> defaultImageAttrWithFileName = allImageAttrWithFileName.get(0);
        final List<Pair<String, String>> altImageAttrWithFileName =
                allImageAttrWithFileName.size() > 1 ? allImageAttrWithFileName.subList(1, allImageAttrWithFileName.size()) : Collections.EMPTY_LIST;

        add(

                new ExternalLink(DEFAULT_IMAGE_REF, depictable.getImage("as", "is", defaultImageAttrWithFileName.getFirst(), lang))
                        .add(
                                createSeoImage(
                                        new ContextImage(DEFAULT_IMAGE, depictable.getImage(width, height, defaultImageAttrWithFileName.getFirst(), lang))
                                                .add(
                                                        new AttributeModifier(HTML_WIDTH, width),
                                                        new AttributeModifier(HTML_HEIGHT, height)
                                                ),
                                        depictable.getSeoImage(defaultImageAttrWithFileName.getSecond()), lang)

                        )

        );

        add(
                new ListView<Pair<String, String>>(ALT_IMAGE_LIST, altImageAttrWithFileName) {

                    @Override
                    protected void populateItem(ListItem<Pair<String, String>> pairListItem) {

                        final Pair<String, String> pair = pairListItem.getModelObject();


                        pairListItem.add(
                                new ExternalLink(ALT_IMAGE_REF, depictable.getImage("as", "is", pair.getFirst(), lang))
                                        .add(
                                                createSeoImage(
                                                        new ContextImage(ALT_IMAGE, depictable.getImage(tumbWidth, tumbHeight, pair.getFirst(), lang))
                                                                .add(
                                                                        new AttributeModifier(HTML_WIDTH, tumbWidth),
                                                                        new AttributeModifier(HTML_HEIGHT, tumbHeight)
                                                                ),
                                                        depictable.getSeoImage(pair.getSecond()), lang)

                                        )
                        );

                    }

                }.setVisible(altImageAttrWithFileName.size() > 0)
        );


        super.onBeforeRender();
    }

    private Component createSeoImage(final Component component, final SeoImage seoImage, final String lang) {
        if (seoImage != null) {

            final I18NWebSupport i18n = getI18NSupport();

            component.add(
                    new AttributeModifier(HTML_ALT,
                            i18n.getFailoverModel(seoImage.getDisplayAlt(), seoImage.getAlt()).getValue(lang)),
                    new AttributeModifier(HTML_TITLE,
                            i18n.getFailoverModel(seoImage.getDisplayTitle(), seoImage.getTitle()).getValue(lang))
            );

        }
        return component;
    }

}

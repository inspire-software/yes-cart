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

package org.yes.cart.web.page.component.product;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ProductServiceFacade;

import java.util.*;

/**
 * View to show attributes of particular sku.
 * Attributes - value pairs from product and sku will be merged.
 * Sku attributes have higher priority.
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 17-Sep-2011
 * Time: 13:34:05
 */
public class SkuAttributesView extends BaseComponent {

    private static final Comparator<Pair> BY_FIRST = new Comparator<Pair>() {
        @Override
        public int compare(final Pair o1, final Pair o2) {
            return ((String) o1.getFirst()).compareToIgnoreCase((String) o2.getFirst());
        }
    };

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String ATTR_GROUPS = "attrGroups";
    private final static String ATTR_GROUP = "attrGroup";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_SERVICE_FACADE)
    private ProductServiceFacade productServiceFacade;


    private final List<Pair<String, List<Pair<String, String>>>> attributesToShow;

    /**
     * Construct attribute view.
     *
     * @param id          component id.
     * @param sku         product sku
     * @param productOnly true in case if need to show product attributes only
     */
    public SkuAttributesView(final String id, final ProductSku sku, final boolean productOnly) {
        super(id);

        final String selectedLocale = getLocale().getLanguage();

        final long productTypeId = sku.getProduct().getProducttype().getProducttypeId();

        if (productOnly) {
            attributesToShow = adapt(productServiceFacade.getProductAttributes(sku.getProduct().getProductId(), 0L, productTypeId), selectedLocale);
        } else {
            attributesToShow = adapt(productServiceFacade.getProductAttributes(sku.getProduct().getProductId(), sku.getSkuId(), productTypeId), selectedLocale);
        }

    }

    private List<Pair<String, List<Pair<String, String>>>> adapt(final ProductAttributesModel pam, final String lang) {

        final List<ProductAttributesModelGroup> groups = pam.getGroups();
        final List<Pair<String, List<Pair<String, String>>>> displayGroups = new ArrayList<>(groups.size());
        for (final ProductAttributesModelGroup group : groups) {

            final List<ProductAttributesModelAttribute> attrs = group.getAttributes();
            final List<Pair<String, String>> displayAttrs = new ArrayList<>(attrs.size());

            for (final ProductAttributesModelAttribute attr : attrs) {

                final List<ProductAttributesModelValue> values = attr.getValues();

                if (CollectionUtils.isNotEmpty(values)) {
                    final StringBuilder csv = new StringBuilder();
                    for (final ProductAttributesModelValue value : values) {
                        csv.append(value.getDisplayVal(lang)).append(", ");
                    }
                    csv.delete(csv.length() - 2, csv.length());

                    displayAttrs.add(new Pair<>(attr.getDisplayName(lang), csv.toString()));
                }
            }

            if (!displayAttrs.isEmpty()) {
                displayGroups.add(new Pair<>(group.getDisplayName(lang), displayAttrs));
            }

        }

        for (final Pair<String, List<Pair<String, String>>> attrs : displayGroups) {
            attrs.getSecond().sort(BY_FIRST);
        }

        displayGroups.sort(BY_FIRST);

        return displayGroups;
    }


    @Override
    protected void onBeforeRender() {

        add(
                new ListView<Pair<String, List<Pair<String, String>>>>(ATTR_GROUPS, attributesToShow) {

                    @Override
                    protected void populateItem(ListItem<Pair<String, List<Pair<String, String>>>> pairListItem) {
                        final Pair<String, List<Pair<String, String>>> item = pairListItem.getModelObject();
                        pairListItem.add(
                                new SkuAttributesSectionView(ATTR_GROUP, item)
                        );
                    }

                }
        );

        super.onBeforeRender();
    }


}

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

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.page.component.BaseComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String ATTR_GROUPS = "attrGroups";
    private final static String ATTR_GROUP = "attrGroup";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    private ProductService productService;

    @SpringBean(name = ServiceSpringKeys.ATTRIBUTE_SERVICE)
    private AttributeService attributeService;


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

        final long productTypeId = sku.getProduct().getProducttype().getId();

        if (productOnly) {
            attributesToShow = adapt(productService.getProductAttributes(selectedLocale, sku.getProduct().getId(), 0L, productTypeId));
        } else {
            attributesToShow = adapt(productService.getProductAttributes(selectedLocale, sku.getProduct().getId(), sku.getId(), productTypeId));
        }

    }

    private List<Pair<String, List<Pair<String, String>>>> adapt(
            final Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> attributes) {

        final List<Pair<String, List<Pair<String, String>>>> displayGroups = new ArrayList<Pair<String, List<Pair<String, String>>>>(attributes.size());
        for (final Pair<String, String> group : attributes.keySet()) {

            final Map<Pair<String, String>, List<Pair<String, String>>> attrs = attributes.get(group);
            final List<Pair<String, String>> displayAttrs = new ArrayList<Pair<String, String>>(attrs.size());

            displayGroups.add(new Pair<String, List<Pair<String, String>>>(group.getSecond(), displayAttrs));

            for (final Pair<String, String> attr : attrs.keySet()) {

                final List<Pair<String, String>> values = attrs.get(attr);

                final StringBuilder csv = new StringBuilder();
                for (int i = 0; i < values.size(); i++) {
                    csv.append(values.get(i).getSecond()).append(", ");
                }
                csv.delete(csv.length() - 2, csv.length());

                displayAttrs.add(new Pair<String, String>(attr.getSecond(), csv.toString()));
            }
        }
        return displayGroups;
    }


    @Override
    protected void onBeforeRender() {

        add(
                new ListView<Pair<String, List<Pair<String, String>>>>(ATTR_GROUPS, attributesToShow) {

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

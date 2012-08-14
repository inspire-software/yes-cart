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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.page.component.BaseComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

        final String selectedLocale = ThreadContext.getSession().getLocale().getLanguage();

        final long productTypeId = sku.getProduct().getProducttype().getId();

        List<Pair<String, List<AttrValue>>> productAttributes = productService.getProductAttributes(
                selectedLocale, productService.getProductById(sku.getProduct().getProductId(), true), productTypeId);
        if (productOnly) {
            attributesToShow = getAdaptedAttributes(selectedLocale, productAttributes);
        } else {
            List<Pair<String, List<AttrValue>>> skuAttributes = productService.getProductAttributes(selectedLocale, sku, productTypeId);
            attributesToShow = getAdaptedAttributes(selectedLocale, attributeService.merge(productAttributes, skuAttributes));
        }

    }

    /**
     * Get the list of pair attr group - attr values
     *
     * @param locale locale
     * @param attributesToAdapt list of attribute sections with values to adapt.
     * @return list of pair attr group - attr values
     */
    private List<Pair<String, List<Pair<String, String>>>> getAdaptedAttributes(
            final String locale,
            final List<Pair<String, List<AttrValue>>> attributesToAdapt) {

        final List<Pair<String, List<Pair<String, String>>>> adaptedAttributesToShow =
                new ArrayList<Pair<String, List<Pair<String, String>>>>();

        for (Pair<String, List<AttrValue>> stringListPair : attributesToAdapt) {
            adaptedAttributesToShow.add(
                    new Pair<String, List<Pair<String, String>>>(
                            stringListPair.getFirst(),
                            adaptAttrValueList(locale, stringListPair.getSecond()))
            );
        }

        return adaptedAttributesToShow;
    }

    /**
     * Get list of pairs attr name - attr value.
     *
     * @param locale locale
     * @param attrValues list of {@link AttrValue}
     * @return list of pairs attr name - attr value.
     */
    private List<Pair<String, String>> adaptAttrValueList(
            final String locale,
            final List<AttrValue> attrValues) {

        Map<String, String> map = new TreeMap<String, String>();
        for (AttrValue attrValue : attrValues) {
            final I18NModel attrNameModel = getI18NSupport().getFailoverModel(attrValue.getAttribute().getDisplayName(), attrValue.getAttribute().getName());
            final String key = attrNameModel.getValue(locale);
            String existingValue = map.get(key);
            final I18NModel attrValueModel = getI18NSupport().getFailoverModel(attrValue.getDisplayVal(), attrValue.getVal());
            if (existingValue == null) {
                map.put(key, attrValueModel.getValue(locale) + ',');
            } else {
                map.put(key, existingValue + ' ' + attrValueModel.getValue(locale) + ',');
            }
        }

        List<Pair<String, String>> pairList = new ArrayList<Pair<String, String>>(attrValues.size());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            pairList.add(
                    new Pair<String, String>(
                            entry.getKey(),
                            StringUtils.chop(entry.getValue())
                    ));

        }
        return pairList;
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

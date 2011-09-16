package org.yes.cart.web.page.component.product;

import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.AttributeService;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.ArrayList;
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

        final long productTypeId = sku.getProduct().getProducttype().getId();

        List<Pair<String, List<AttrValue>>> productAttributes = productService.getProductAttributes(sku.getProduct(), productTypeId);
        if (productOnly) {
            attributesToShow = getAdaptedAttributes(productAttributes);
        } else {
            List<Pair<String, List<AttrValue>>> skuAttributes = productService.getProductAttributes(sku, productTypeId);
            attributesToShow = getAdaptedAttributes(attributeService.merge(productAttributes, skuAttributes));
        }

    }

    /**
     * Get the list of pair attr group - attr values
     * @param attributesToAdapt list of attribute sections with values to adapt.
     * @return list of pair attr group - attr values
     */
    private List<Pair<String, List<Pair<String, String>>>> getAdaptedAttributes(
            final List<Pair<String, List<AttrValue>>> attributesToAdapt) {

        final List<Pair<String, List<Pair<String, String>>>> adaptedAttributesToShow =
                new ArrayList<Pair<String, List<Pair<String, String>>>>();

        for (Pair<String, List<AttrValue>> stringListPair : attributesToAdapt) {
            adaptedAttributesToShow.add(
                    new Pair<String, List<Pair<String, String>>>(
                            stringListPair.getFirst(),
                            adaptAttrValueList(stringListPair.getSecond()))
            );
        }

        return adaptedAttributesToShow;
    }

    /**
     * Get list of pairs attr name - attr value.
     *
     * @param attrValues list of {@link AttrValue}
     * @return list of pairs attr name - attr value.
     */
    private List<Pair<String, String>> adaptAttrValueList(List<AttrValue> attrValues) {

        Map<String, String> map = new TreeMap<String, String>();
        for (AttrValue attrValue : attrValues) {
            final String key = attrValue.getAttribute().getName();
            String existingValue = map.get(key);
            if (existingValue == null) {
                map.put(key, attrValue.getVal() + ',');
            } else {
                map.put(key, existingValue + ' ' + attrValue.getVal() + ',');
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

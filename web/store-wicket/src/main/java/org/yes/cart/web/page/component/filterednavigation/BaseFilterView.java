/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.web.page.component.filterednavigation;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.service.wicketsupport.LinksSupport;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/18/11
 * Time: 8:54 PM
 */
public class BaseFilterView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String HEAD = "head";
    private final static String LINK_LIST = "linkList";
    private final static String LINK = "link";
    private final static String LINK_NAME = "linkName";
    private final static String QUANTITY = "quantity";
    // ------------------------------------- MARKUP IDs END   ---------------------------------- //

    private static final Comparator<AbstractProductFilter.Value> VALUE_COMPARATOR = new Comparator<AbstractProductFilter.Value>() {
        public int compare(final AbstractProductFilter.Value o1,
                           final AbstractProductFilter.Value o2) {
            return o1.getLabel().compareToIgnoreCase(o2.getLabel());
        }
    };

    private final String code;
    private final String head;
    private final List<AbstractProductFilter.Value> linkList;


    /**
     * Construct base filter view.
     *
     * @param id       view id.
     * @param code     attribute code
     * @param head     the head of widget
     * @param linkList the content of filtering widget.
     * @param recordLimit max number of records to display
     */
    public BaseFilterView(final String id,
                          final String code,
                          final String head,
                          final List<AbstractProductFilter.Value> linkList,
                          final int recordLimit) {
        super(id);
        this.linkList = cutTheTail(linkList, recordLimit);
        this.code = code;
        this.head = head;
    }


    /**
     * Cut the extract records.
     * @param navigationList list to reduce
     * @return  reduced nav list.
     */
    private List<AbstractProductFilter.Value> cutTheTail(final List<AbstractProductFilter.Value> navigationList, final int recordLimit) {

        if (navigationList.size() > recordLimit && recordLimit > 0) {
            Collections.sort(
                    navigationList,
                    VALUE_COMPARATOR
            );

            return navigationList.subList(0, recordLimit);

        }
        return navigationList;
    }

    /**
     * {@inheritDoc}
     */
    protected void onBeforeRender() {
        if (isVisible()) {

            final LinksSupport links = getWicketSupportFacade().links();

            add(
                    new Label(HEAD, head)
                            .setEscapeModelStrings(false)
            );
            add(
                    new ListView<AbstractProductFilter.Value>(LINK_LIST, linkList) {
                        protected void populateItem(final ListItem<AbstractProductFilter.Value> pairListItem) {
                            final AbstractProductFilter.Value value = pairListItem.getModelObject();
                            final Link link = links.newLink(LINK, value.getParameters());

                            /*
                                This css class can be used to style specific values. E.g. for COLOR attribute these can
                                be rendered as square divs with background color corresponding to the attribute value.
                             */
                            final String cssClass = getValueCssClass(value);
                            link.add(new AttributeModifier("class", cssClass));

                            final Label valueVabel = new Label(LINK_NAME, getValueLabel(value));
                            valueVabel.setEscapeModelStrings(false);
                            link.add(valueVabel);

                            final String quantity = getValueQuantity(value);
                            link.add( new Label(QUANTITY, quantity).setVisible(quantity != null));

                            pairListItem.add(link);
                        }
                    }
            );

        }
        super.onBeforeRender();
    }

    /**
     * Get the quantity badge number.
     *
     * @param keyValue key value
     *
     * @return quantity to display (use null to skip badge)
     */
    protected String getValueQuantity(final AbstractProductFilter.Value keyValue) {
        return keyValue.getCount().toString();
    }

    /**
     * Get the value label
     *
     * @param keyValue key value
     *
     * @return displayable label for this value
     */
    protected String getValueLabel(final AbstractProductFilter.Value keyValue) {
        return keyValue.getLabel();
    }

    /*
     * Construct css class based on [code][value]. E.g. if attribute code is "color" and  value is "blue"
     * then css class for link would be "colorblue".
     *
     * If attribute value contain non latin characters or non digits then hex code is used for that character instead.
     * E.g. if attribute code is "color" and  value is "синий" then css class for link would be "color10891080108510801081".
     *
     * This may not be ideal as it is not human readable but it is safer to use latin in css
     */
    protected String getValueCssClass(final AbstractProductFilter.Value keyValue) {
        return getWicketUtil().constructLatinStringValue(code, keyValue.getParameters().get(code).toString(""));
    }


    /**
     * {@inheritDoc}
     */
    public boolean isVisible() {
        return linkList != null && !linkList.isEmpty() && showGroupCounter();
    }

    /**
     * Can be used if product has only one attribute value per attribute.
     *
     * @return false if all group has only one link with one potential result;
     */
    private boolean showGroupCounter() {
        return true;
        /*int summ = 0;
        for (Pair<Pair<String, Integer>, PageParameters> keyValue :  linkList) {
            summ += keyValue.getFirst().getSecond();
            if (summ > 1 ) {
                break;
            }
        }
        return summ > 1; */
    }

}

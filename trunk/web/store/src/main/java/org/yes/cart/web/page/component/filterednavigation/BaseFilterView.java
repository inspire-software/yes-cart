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

package org.yes.cart.web.page.component.filterednavigation;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.util.WicketUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/18/11
 * Time: 8:54 PM
 */
public class BaseFilterView extends BaseComponent {

    private final static int RECORD_LIMITS = 12;

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String HEAD = "head";
    private final static String LINK_LIST = "linkList";
    private final static String LINK = "link";
    private final static String LINK_NAME = "linkName";
    private final static String QUANTITY = "quantity";
    // ------------------------------------- MARKUP IDs END   ---------------------------------- //

    private final String code;
    private final String head;
    private final List<Pair<Pair<String, Integer>, PageParameters>> linkList;


    /**
     * Construct base filter view.
     *
     * @param id       view id.
     * @param code     attribute code
     * @param head     the head of widget
     * @param linkList the content of filtering widget.
     */
    public BaseFilterView(
            final String id,
            final String code,
            final String head,
            final List<Pair<Pair<String, Integer>, PageParameters>> linkList) {
        super(id);
        this.linkList = cutTheTail(linkList);
        this.code = code;
        this.head = head;
    }


    /**
     * Cut the extrac records.
     * @param navigationList list to reduce
     * @return  reduced nav list.
     */
    private List<Pair<Pair<String, Integer>, PageParameters>> cutTheTail(final List<Pair<Pair<String, Integer>, PageParameters>> navigationList) {

        if (navigationList.size() > RECORD_LIMITS) {
            Collections.sort(
                    navigationList,
                    new Comparator<Pair<Pair<String, Integer>, PageParameters>>() {
                        public int compare(final Pair<Pair<String, Integer>, PageParameters> o1,
                                           final Pair<Pair<String, Integer>, PageParameters> o2) {
                            return o1.getFirst().getSecond() < o2.getFirst().getSecond() ? 1 : (o1.getFirst().getSecond().equals(o2.getFirst().getSecond()) ? 0 : -1);
                        }
                    }
            );

            return navigationList.subList(0, RECORD_LIMITS -1);

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
                    new ListView<Pair<Pair<String, Integer>, PageParameters>>(LINK_LIST, linkList) {
                        protected void populateItem(final ListItem<Pair<Pair<String, Integer>, PageParameters>> pairListItem) {
                            final Pair<Pair<String, Integer>, PageParameters> keyValue = pairListItem.getModelObject();
                            final Link link = links.newLink(LINK, keyValue.getSecond());

                            /*
                                This css class can be used to style specific values. E.g. for COLOR attribute these can
                                be rendered as square divs with background color corresponding to the attribute value.
                             */
                            final String cssClass = constructValueCssClass(keyValue.getSecond());
                            link.add(new AttributeModifier("class", cssClass));

                            final Label valueVabel = new Label(LINK_NAME, keyValue.getFirst().getFirst());
                            valueVabel.setEscapeModelStrings(false);

                            link.add(valueVabel);
                            link.add( new Label(QUANTITY, '[' +keyValue.getFirst().getSecond().toString() + ']'));

                            pairListItem.add(link);
                        }
                    }
            );

        }
        super.onBeforeRender();
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
    private String constructValueCssClass(final PageParameters second) {
        return WicketUtil.constructLatinStringValue(code, second.get(code).toString(""));
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

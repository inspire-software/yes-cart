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

package org.yes.cart.web.page.component.navigation;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.service.wicketsupport.PaginationSupport;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/14/11
 * Time: 1:41 PM
 */
public class ProductPerPageListView extends ListView<String> {

    /**
     * Item per page link selector.
     */
    private final static String ITEMS_PER_PAGE = "itemsPerPage";

    /**
     * Constructor.
     *
     * @param componentId component id
     * @param items       list of possible item per page values
     */
    public ProductPerPageListView(final String componentId,
                                  final List<String> items) {
        super(componentId, items);
    }

    /**
     * {@inheritDoc}
     */
    protected void populateItem(ListItem<String> stringListItem) {

        final String pageSize = stringListItem.getModelObject();
        final Label label = new Label(WebParametersKeys.QUANTITY, pageSize);

        final AbstractWebPage page = ((AbstractWebPage) getPage());
        final PageParameters pageParameters = page.getPageParameters();
        final LinksSupport links = page.getWicketSupportFacade().links();
        final PaginationSupport pagination = page.getWicketSupportFacade().pagination();

        final PageParameters params = links.getFilteredCurrentParameters(pageParameters);
        params.set(WebParametersKeys.QUANTITY, pageSize);

        final Link pageSizeLink = links.newLink(ITEMS_PER_PAGE, params);
        pageSizeLink.add(label);
        pagination.markSelectedPageSizeLink(pageSizeLink, pageParameters, NumberUtils.toInt(pageSize));

        stringListItem.add(pageSizeLink);

    }

}

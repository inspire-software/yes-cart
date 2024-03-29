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

package org.yes.cart.web.page.component.breadcrumbs;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.utils.spring.LinkedHashMapBean;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;

import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/30/11
 * Time: 10:42 AM
 */
public class BreadCrumbsView extends BaseComponent {

    private static final String BREADCRUMBS_LIST = "breadcrumbs";
    private static final String BREADCRUMBS_HOME_LINK = "homeLink";
    private static final String BREADCRUMBS_LINK = "link";
    private static final String BREADCRUMBS_REMOVE_LINK = "remLink";
    private static final String BREADCRUMBS_LINK_NAME = "linkName";

    @SpringBean(name = StorefrontServiceSpringKeys.BREAD_CRUMBS_BUILDER)
    private BreadCrumbsBuilder breadCrumbsBuilder;

    @SpringBean(name = ServiceSpringKeys.SHOP_SERVICE)
    private ShopService shopService;

    @SpringBean(name = "wicketBreadCrumbsMapping")
    private LinkedHashMapBean<String, CrumbNameFormatter> breadCrumbMapping;

    private final long shopId;
    private final long customerShopId;
    private final long categoryId;

    /**
     * Build bread crumbs navigation view.
     *
     * @param id              component id
     * @param shopId          current shop id
     * @param customerShopId  current shop id
     * @param categoryId      current category id
     */
    public BreadCrumbsView(final String id,
                           final long shopId,
                           final long customerShopId,
                           final long categoryId) {

        super(id);

        this.categoryId = categoryId;
        this.shopId = shopId;
        this.customerShopId = customerShopId;

    }

    private List<Crumb> crumbs = null;

    /**
     * Get the crumbs
     * @return bread crumbs
     */
    public List<Crumb> getCrumbs() {
        if (crumbs == null) {

            crumbs = breadCrumbsBuilder.getBreadCrumbs(getLocale().getLanguage(),
                    shopId, customerShopId, categoryId, getPage().getPageParameters());

        }
        return crumbs;
    }

    /** {@inheritDoc} */
    @Override
    protected void onBeforeRender() {

        add(
                new BookmarkablePageLink(BREADCRUMBS_HOME_LINK, Application.get().getHomePage())
        );
        add(
                new ListView<Crumb>(
                        BREADCRUMBS_LIST,
                        getCrumbs() ) {

                    @Override
                    protected void populateItem(final ListItem<Crumb> crumbListItem) {
                        final Crumb crumb = crumbListItem.getModelObject();
                        final CrumbNameFormatter crumbName = breadCrumbMapping.get(crumb.getKey());
                        final String crumbModel;
                        if (crumbName != null) {
                            crumbModel = crumbName.format(this, crumb, getI18NSupport(), getLocale().getLanguage());
                        } else {
                            final String selectedLocale = getLocale().getLanguage();
                            crumbModel = getI18NSupport().getFailoverModel(crumb.getDisplayName(), crumb.getName()).getValue(selectedLocale);
                        }

                        crumbListItem
                                .add(getPageLink(BREADCRUMBS_LINK, crumbModel, new PageParameters(crumb.getCrumbLinkParameters()), true))
                                .add(getPageLink(BREADCRUMBS_REMOVE_LINK, crumbModel, new PageParameters(crumb.getRemoveCrumbLinkParameters()), false)
                                );
                    }

                    private Link getPageLink(final String linkKey, final String linkName,
                                             final PageParameters parameters, final boolean addLabel) {

                        final LinksSupport links = getWicketSupportFacade().links();
                        final Link pageLink = links.newLink(linkKey, parameters);
                        if (addLabel) {
                            final Label label = new Label(BREADCRUMBS_LINK_NAME, linkName);
                            label.setEscapeModelStrings(false);
                            pageLink.add(label);
                        }
                        return pageLink;
                    }

                }
        );

        super.onBeforeRender();
    }

}

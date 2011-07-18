package org.yes.cart.web.page.component.navigation;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.HomePage;
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

    private final String selectedItemPerPage;

    private final PageParameters pageParameters;


    /**
     * Constructor.
     *
     * @param componentId componrnt id
     * @param items       list of possible item per page values
     * @param pageParameters page parameters.
     */
    public ProductPerPageListView(final String componentId,
                                  final List<String> items,
                                  final PageParameters pageParameters) {
        super(componentId, items);

        this.pageParameters = pageParameters;

        this.selectedItemPerPage = String.valueOf(
                WicketUtil.getSelectedItemsPerPage(pageParameters, items)
        );

    }

    /**
     * {@inheritDoc}
     */
    protected void populateItem(ListItem<String> stringListItem) {

        final String quantity = stringListItem.getModelObject();

        final Label label = new Label(WebParametersKeys.QUANTITY, quantity);

        if (selectedItemPerPage.equalsIgnoreCase(quantity)) {
            label.add(new AttributeModifier("class", "items-per-page-active"));
        }

        stringListItem.add(
                new BookmarkablePageLink<Link>(
                        ITEMS_PER_PAGE,
                        HomePage.class,
                        WicketUtil.getFilteredRequestParameters(pageParameters)
                                .set(
                                        WebParametersKeys.QUANTITY,
                                        quantity))
                        .add(
                                label)
        );

    }


}

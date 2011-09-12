package org.yes.cart.web.page.component.filterednavigation;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.page.component.BaseComponent;

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

    private final String head;
    private final List<Pair<Pair<String, Integer>, PageParameters>> linkList;


    /**
     * Construct base filter view.
     *
     * @param id       view id.
     * @param head     the head of widget
     * @param linkList the content of filtering widget.
     */
    public BaseFilterView(
            final String id,
            final String head,
            final List<Pair<Pair<String, Integer>, PageParameters>> linkList) {
        super(id);
        this.linkList = linkList;
        this.head = head;
    }

    /**
     * {@inheritDoc}
     */
    protected void onBeforeRender() {
        if (isVisible()) {
            add(
                    new Label(HEAD, head)
                            .setEscapeModelStrings(false)
            );
            add(
                    new ListView<Pair<Pair<String, Integer>, PageParameters>>(LINK_LIST, linkList) {
                        protected void populateItem(final ListItem<Pair<Pair<String, Integer>, PageParameters>> pairListItem) {
                            final Pair<Pair<String, Integer>, PageParameters> keyValue = pairListItem.getModelObject();
                            final Link link = new BookmarkablePageLink<HomePage>(LINK, HomePage.class, keyValue.getSecond());
                            final Label valueVabel = new Label(LINK_NAME, keyValue.getFirst().getFirst());
                            valueVabel.setEscapeModelStrings(false);
                            link.add(valueVabel);
                            pairListItem.add(link);
                            pairListItem.add(new Label(QUANTITY, keyValue.getFirst().getSecond().toString()));
                        }
                    }
            );

        }
        super.onBeforeRender();
    }

    

    /**
     * {@inheritDoc}
     */
    public boolean isVisible() {
        return linkList != null && !linkList.isEmpty() && showGroupCounter();
    }

    /**
     * Can be used if product has only one attrubute value per attibute.
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

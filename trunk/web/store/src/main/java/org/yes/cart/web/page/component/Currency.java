package org.yes.cart.web.page.component;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.shoppingcart.impl.ChangeCurrencyEventCommandImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 9:17 PM
 */
public class Currency extends BaseComponent {

     // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CURRENCY_LIST = "supportedCurrencies";
    private final static String CURRENCY_LINK = "switchCurrencyLink";
    private final static String CURRENCY_NAME = "currencyName";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.CURRENCY_SYMBOL_SERVICE)
    private CurrencySymbolService currencySymbolService;


    /**
     * Construct panel.
     *
     * @param id panel id
     */
    public Currency(final String id) {
        super(id);
    }

    /**
     * Construct panel.
     *
     * @param id    panel id
     * @param model model.
     */
    public Currency(final String id, final IModel<?> model) {
        super(id, model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        if (isVisible()) {

            final List<String> supportedCurrencies = ApplicationDirector.getCurrentShop().getSupportedCurrensiesAsList();

            final PageParameters basePageParameters = WicketUtil.getFilteredRequestParameters(getPage().getPageParameters());

            add( new ListView<String>(CURRENCY_LIST, supportedCurrencies) {

                protected void populateItem(ListItem<String> stringListItem) {

                    final String currencyCode = stringListItem.getModelObject();
                    final String currencySymbol = currencySymbolService.getCurrencySymbol(currencyCode);

                    final PageParameters itemPageParameters =  new PageParameters(basePageParameters);

                    itemPageParameters.add(ChangeCurrencyEventCommandImpl.CMD_KEY, currencyCode);

                    final BookmarkablePageLink<HomePage> pageLink = new BookmarkablePageLink<HomePage>(
                            CURRENCY_LINK,
                            getPage().getClass(),
                            itemPageParameters);

                    final Label currencyLabel = new Label(CURRENCY_NAME, currencySymbol);

                    currencyLabel.setEscapeModelStrings(false);

                    if (currencyCode.equals(ApplicationDirector.getShoppingCart().getCurrencyCode())) {

                        pageLink.add(new SimpleAttributeModifier(HTML_CLASS, "currency-active"));
                    }

                    pageLink.add(currencyLabel);

                    stringListItem.add(pageLink);
                }
            }
            );

        }
        super.onBeforeRender();
    }


}

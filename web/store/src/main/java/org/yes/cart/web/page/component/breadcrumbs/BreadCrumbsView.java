package org.yes.cart.web.page.component.breadcrumbs;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.PriceNavigation;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/30/11
 * Time: 10:42 AM
 */
public class BreadCrumbsView extends BaseComponent implements CrumbNamePrefixProvider {

    private static final String BREADCRUMBS_LIST = "breadcrumbs";
    private static final String BREADCRUMBS_LINK = "link";
    private static final String BREADCRUMBS_REMOVE_LINK = "remLink";
    private static final String BREADCRUMBS_LINK_NAME = "linkName";

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.ATTRIBUTE_SERVICE)
    private AttributeService attributeService;

    @SpringBean(name = StorefrontServiceSpringKeys.CURRENCY_SYMBOL_SERVICE)
    private CurrencySymbolService currencySymbolService;

    @SpringBean(name = ServiceSpringKeys.PRICE_NAVIGATION)
    private PriceNavigation priceNavigation;

    private final Map<String, String> attributeCodeName;

    private final List<Long> shopCategoryIds;

    private final List<String> allowedAttributeNames;

    private final long categoryId;

    /**
     * Build bread crumbs navigation view.
     *
     * @param id              component id
     * @param categoryId      current category id
     */
    public BreadCrumbsView(
            final String id,
            final long categoryId,
            final List<Long> shopCategoryIds) {

        super(id);

        this.allowedAttributeNames = attributeService.getAllAttributeCodes();

        this.attributeCodeName = attributeService.getAttributeNamesByCodes(allowedAttributeNames);

        this.shopCategoryIds = shopCategoryIds;

        this.categoryId = categoryId;


    }

    private List<Crumb> crumbs = null;

    /**
     * Get the crumbs
     * @return bread crumbs
     */
    public List<Crumb> getCrumbs() {
        if (crumbs == null) {
            BreadCrumbsBuilder breadCrumbsBuilder = new BreadCrumbsBuilder(
                    categoryId,
                    getPage().getPageParameters(),
                    allowedAttributeNames,
                    shopCategoryIds,
                    this,
                    categoryService);
            crumbs = breadCrumbsBuilder.getBreadCrumbs();

        }
        return crumbs;
    }

    /** {@inheritDoc} */
    protected void onBeforeRender() {

        add(
                new ListView<Crumb>(
                        BREADCRUMBS_LIST,
                        getCrumbs() ) {

                    protected void populateItem(final ListItem<Crumb> crumbListItem) {
                        final Crumb crumb = crumbListItem.getModelObject();
                        crumbListItem
                                .add(getPageLink(BREADCRUMBS_LINK, crumb.getName(), new PageParameters(crumb.getCrumbLinkParameters()), true))
                                .add(getPageLink(BREADCRUMBS_REMOVE_LINK, crumb.getName(), new PageParameters(crumb.getRemoveCrumbLinkParameters()), false)
                                );
                    }

                    private BookmarkablePageLink getPageLink(final String linkKey, final String linkName,
                                                             final PageParameters parameters, final boolean addLabel) {

                        final BookmarkablePageLink pageLink = new BookmarkablePageLink<HomePage>(linkKey, HomePage.class, parameters);
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


    /*@Override
    public boolean isVisible() {
        return !getCrumbs().isEmpty();
    }  */

    /**
     * Get the explanation prefix for link text.
     *
     * @param key key name.
     * @return explanation prefix
     */
    public String getLinkNamePrefix(final String key) {
        String name = attributeCodeName.get(key);
        if (ProductSearchQueryBuilder.BRAND_FIELD.equals(key)) {
            name = getLocalizer().getString(ProductSearchQueryBuilder.BRAND_FIELD, this);
        } else if (ProductSearchQueryBuilder.PRODUCT_PRICE.equals(key)) {
            name = getLocalizer().getString(ProductSearchQueryBuilder.PRODUCT_PRICE, this);
        } else if (WebParametersKeys.QUERY.equals(key)) {
            name = getLocalizer().getString(WebParametersKeys.QUERY, this);
        }
        return name;
    }

    /**
     * Get the link text for given key-value pair.
     *
     * @param key   key name.
     * @param value key value.
     * @return link text.
     */
    public String getLinkName(final String key, final String value) {
        if (ProductSearchQueryBuilder.PRODUCT_PRICE.equals(key)) {
            Pair<String, Pair<BigDecimal, BigDecimal>> pair = priceNavigation.decomposePriceRequestParams(value);
            return priceNavigation.composePriceRequestParams(
                    currencySymbolService.getCurrencySymbol(pair.getFirst()),
                    pair.getSecond().getFirst(),
                    pair.getSecond().getSecond(),
                    " ",
                    "..."
            );
        }
        return value;
    }


}

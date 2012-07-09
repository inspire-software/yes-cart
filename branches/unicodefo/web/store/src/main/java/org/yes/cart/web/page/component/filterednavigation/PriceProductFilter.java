package org.yes.cart.web.page.component.filterednavigation;

import org.apache.lucene.search.BooleanQuery;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.query.PriceNavigation;
import org.yes.cart.domain.query.ProductSearchQueryBuilder;
import org.yes.cart.domain.query.impl.PriceSearchQueryBuilderImpl;
import org.yes.cart.domain.queryobject.FiteredNavigationRecord;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.CurrencySymbolService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Component responsible to build  list view of available price ranges to filtering.
 * Currently included price ranges not supported.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/30/11
 * Time: 5:07 PM
 */
public class PriceProductFilter extends AbstractProductFilter {

    protected boolean filteredNavigationByPrice = false;

    private PriceTierTree priceTierTree = null;

    private String currency;

    private Shop shop;

    @SpringBean(name = ServiceSpringKeys.PRICE_NAVIGATION)
    private PriceNavigation priceNavigation;

    @SpringBean(name = ServiceSpringKeys.PRICE_SERVICE)
    private PriceService priceService;

    @SpringBean(name = StorefrontServiceSpringKeys.CURRENCY_SYMBOL_SERVICE)
    private CurrencySymbolService currencySymbolService;


     /**
      * Create price product filter component.
     * @param id         panel id
     * @param query      current query.
     * @param categoryId current category id
     */
    public PriceProductFilter(final String id, final BooleanQuery query, final long categoryId) {

        super(id, query, categoryId);

        if (categoryId > 0) {

            priceTierTree = getCategory().getNavigationByPriceTree();

            filteredNavigationByPrice = (getCategory().getNavigationByPrice() == null || priceTierTree == null)
                    ? false : getCategory().getNavigationByPrice();

            if (filteredNavigationByPrice) {

                currency = ApplicationDirector.getShoppingCart().getCurrencyCode();

                shop = ApplicationDirector.getCurrentShop();

                final List<FiteredNavigationRecord> allNavigationRecords = priceService.getPriceNavigationRecords(
                        priceTierTree,
                        currency,
                        shop);

                setNavigationRecords(
                        getFilteredNavigationRecords(allNavigationRecords)
                );


            }

        }

    }

    /**
     * {@inheritDoc}
     */
    List<FiteredNavigationRecord> getFilteredNavigationRecords(
            final List<FiteredNavigationRecord> allNavigationRecords) {
        final List<FiteredNavigationRecord> navigationList = new ArrayList<FiteredNavigationRecord>();
        if (!isAttributeAlreadyFiltered(ProductSearchQueryBuilder.PRODUCT_PRICE_AMOUNT)) {
            PriceSearchQueryBuilderImpl queryBuilder = new PriceSearchQueryBuilderImpl();

            for (FiteredNavigationRecord record : allNavigationRecords) {
                Pair<String, Pair<BigDecimal, BigDecimal>> priceNavigationRecord = priceNavigation.decomposePriceRequestParams(record.getValue());
                BooleanQuery candidateQuery = getLuceneQueryFactory().getSnowBallQuery(
                        getQuery(),
                        queryBuilder.createQuery(
                                getCategories(),
                                shop.getShopId(),
                                currency,
                                priceNavigationRecord.getSecond().getFirst(),
                                priceNavigationRecord.getSecond().getSecond())
                );
                int candidateResultCount = getProductService().getProductQty(candidateQuery);
                if (candidateResultCount > 0) {
                    record.setName(getLocalizer().getString("price", this));
                    record.setCode(ProductSearchQueryBuilder.PRODUCT_PRICE);
                    record.setCount(candidateResultCount);
                    navigationList.add(record);
                }
            }


        }
        return navigationList;
    }

    /**
     * {@inheritDoc}
     * Convert value from filtered navigation parameter to more human readable form. Example
     * USD-500-1000 to  $ 500...1000
     *
     * @param valueToAdapt - expected value in following format CUR-LOW-HIGH
     * @return currency symbol  low high
     */
    protected String adaptValueForLinkLabel(final String valueToAdapt, final String displayValue) {
        Pair<String, Pair<BigDecimal, BigDecimal>> pair = priceNavigation.decomposePriceRequestParams(valueToAdapt);
        return priceNavigation.composePriceRequestParams(
                currencySymbolService.getCurrencySymbol(pair.getFirst()),
                pair.getSecond().getFirst(),
                pair.getSecond().getSecond(),
                " ",
                "..."
        );

    }


    /**
     * {@inheritDoc}
     */
    public boolean isVisible() {
        return  super.isVisible()
                && filteredNavigationByPrice
                && getNavigationRecords() != null
                && !getNavigationRecords().isEmpty();
    }

}

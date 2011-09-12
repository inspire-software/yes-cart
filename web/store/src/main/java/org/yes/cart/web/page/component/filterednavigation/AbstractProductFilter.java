package org.yes.cart.web.page.component.filterednavigation;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.search.BooleanQuery;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.LuceneQueryFactory;
import org.yes.cart.domain.queryobject.FiteredNavigationRecord;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.util.HttpUtil;
import org.yes.cart.web.util.WicketUtil;

import java.util.*;

/**
 *
 * Responsible to compose widget for filtering navigation.
 * The base widget look following:
 * <pre>
 * -------------
 * |   Color   |
 * -------------
 * |Green     4|
 * |Red       2|
 * |Black     7|
 * -------------
 * </pre>
 * Where Color - atribute name
 * and Green, Red, Black available attribute values and available quantity of products.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/18/11
 * Time: 10:29 AM
 */
public abstract class AbstractProductFilter extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    protected static final String FILTERED_NAVIGATION_LIST = "filteredNavigationList";
    protected static final String FILTER = "filteredNavigation";
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //


    private final long categoryId;
    private Category category;

    private List<FiteredNavigationRecord> navigationRecords = null;

    private List<Long> categories = null;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    private ProductService productService;

    @SpringBean(name = ServiceSpringKeys.LUCENE_QUERY_FACTORY)
    private LuceneQueryFactory luceneQueryFactory;

    private final BooleanQuery query;


    /**
     * Construct panel.
     *
     * @param id    panel id
     * @param query current query.
     * @param categoryId current category id
     */
    public AbstractProductFilter(final String id, final BooleanQuery query, final long categoryId) {
        super(id);
        this.query = query;
        //categoryId = NumberUtils.toLong(HttpUtil.getSingleValue(getPage().getPageParameters().get(WebParametersKeys.CATEGORY_ID)));
        this.categoryId  = categoryId;
        categories = getCategories();
    }

    /**
     * Get current category.
     *
     * @return {@link Category}
     */
    public Category getCategory() {
        if (category == null) {
            category = categoryService.getById(categoryId);
        }
        return category;
    }

    /**
     * Get {@link CategoryService}.
     *
     * @return Category service
     */
    public CategoryService getCategoryService() {
        return categoryService;
    }

    /**
     * Get product service.
     *
     * @return product service.
     */
    public ProductService getProductService() {
        return productService;
    }

    /**
     * Get Lucene query factory.
     *
     * @return {@link LuceneQueryFactory}
     */
    public LuceneQueryFactory getLuceneQueryFactory() {
        return luceneQueryFactory;
    }


    /**
     * Get the current subcategories sub tree as list.
     *
     * @return current subcategories sub tree as list.
     */
    protected List<Long> getCategories() {

        if (categoryId > 0) {
            return categoryService.transform(
                    categoryService.getChildCategoriesRecursive(categoryId));
        } else {
            return Arrays.asList(categoryId);
        }
    }

    /**
     * Filter navigation records. To calculate real quantity of products, that can be selected with this attribute value
     * 1. getByKey "last" appplied query
     * 2. add to it new boolean query, that can be build with FilteredNavigationQueryBuilderImpl
     * 3. ask GenericDAO#getResultCount through product service for real quantity
     * 4. add to result if quanrity more that 0
     *
     * @param allNavigationRecords all navigation records
     * @return list of navigatior records, that have product or empty list if no records selected for navigation
     */
    abstract List<FiteredNavigationRecord> getFilteredNagigationRecords(
            final List<FiteredNavigationRecord> allNavigationRecords);


    /**
     * @param luceneQuerySubString query substring
     * @return true if filter already present in applyed query
     */
    protected boolean isAttributeAlreadyFiltered(final String luceneQuerySubString) {
        boolean result = false;
        if (query != null) {
            final String appliedQueryString = query.toString();
            if (StringUtils.isNotBlank(appliedQueryString)) {
                result = (appliedQueryString.indexOf(luceneQuerySubString) != -1);
            }
        }
        return result;
    }

    /**
     * Adapt list of  {@link FiteredNavigationRecord} into ugly   list of attribute name - list of attributes values pair .
     * @param records
     * @return
     */
    protected List<Pair<String, List<Pair<Pair<String, Integer>, PageParameters>>>>
                        adaptNagigationRecords(final List<FiteredNavigationRecord> records) {
        String head = StringUtils.EMPTY;
        Pair<String, List<Pair<Pair<String, Integer>, PageParameters>>> currentPair = null;

        final List<Pair<String, List<Pair<Pair<String, Integer>, PageParameters>>>> headValueList =
                new ArrayList<Pair<String, List<Pair<Pair<String, Integer>, PageParameters>>>>();

        for (FiteredNavigationRecord navigationRecord : records) {
            if (!navigationRecord.getName().equalsIgnoreCase(head)) {
                currentPair = new Pair<String, List<Pair<Pair<String, Integer>, PageParameters>>>(
                        navigationRecord.getName(),
                        new ArrayList<Pair<Pair<String, Integer>, PageParameters>>());
                headValueList.add(currentPair);
                head = navigationRecord.getName();
            }
            currentPair.getSecond().add(
                    new Pair<Pair<String, Integer>, PageParameters>(
                            new Pair<String, Integer>(
                                    adaptValueForLinkLabel(navigationRecord.getValue(), navigationRecord.getDisplayValue()),
                                    navigationRecord.getCount()),
                            getNavigationParameter(navigationRecord.getCode(), navigationRecord.getValue())
                    )
            );
        }
        return headValueList;
    }

    /**
     * Adapt parameter value to be represented as filtered navigation label.
     *
     * @param valueToAdapt value to adapt
     * @param displayValue display value
     * @return  adapted value
     */
    protected String adaptValueForLinkLabel(final String valueToAdapt, final String displayValue) {
        if (displayValue == null) {
            return valueToAdapt;
        }
        return displayValue;
    }

    /**
     * Get page parameters for navigate to products, that will be filtering by given attribute value.
     * @param attributeCode  given attribute code.
     * @param attributeValue  given attribute value.
     * @return composed page parameters.
     */
    protected PageParameters getNavigationParameter(final String attributeCode, final String attributeValue) {
        return WicketUtil.getFilteredRequestParameters(getPage().getPageParameters())
                .remove(WebParametersKeys.PRODUCT_ID)
                .set(WebParametersKeys.CATEGORY_ID, categoryId)
                .set(attributeCode, attributeValue);

    }

    /** {@inheritDoc} */
    protected void onBeforeRender() {
        if (isVisible()) {
            add(
                    new ListView<Pair<String, List<Pair<Pair<String, Integer>, PageParameters>>>>(
                            FILTERED_NAVIGATION_LIST,
                            adaptNagigationRecords(navigationRecords)) {
                        protected void populateItem(ListItem<Pair<String, List<Pair<Pair<String, Integer>, PageParameters>>>> pairListItem) {
                            final Pair<String, List<Pair<Pair<String, Integer>, PageParameters>>> headValues = pairListItem.getModelObject();
                            pairListItem.add(
                                    new BaseFilterView(FILTER, headValues.getFirst(), headValues.getSecond())
                            );
                        }
                    }
            );
        }

        super.onBeforeRender();
    }

    @Override
    public boolean isVisible() {
        return  super.isVisible() && !isShowProduct();
    }


    /**
     *
     * @return true if product or his sku is shown;
     */
    protected boolean isShowProduct() {
        final Set<String> paramNames = getPage().getPageParameters().getNamedKeys();
        return paramNames.contains(WebParametersKeys.PRODUCT_ID)
                || paramNames.contains(WebParametersKeys.SKU_ID);
    }

    /**
     * Get current category id.
     * @return current category id.
     */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * Set particular navigation record to use.
     * @param navigationRecords navigation records.
     */
    public void setNavigationRecords(final List<FiteredNavigationRecord> navigationRecords) {
        this.navigationRecords = navigationRecords;
    }

    /**
     * Get navigation records.
     * @return   list of navigation records.
     */
    public List<FiteredNavigationRecord> getNavigationRecords() {
        return navigationRecords;
    }

    /**
     * Get current lucene product  query.
     * @return current lucene product  query.
     */
    public BooleanQuery getQuery() {
        return query;
    }
}

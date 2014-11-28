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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.NavigationContext;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CategoryServiceFacade;
import org.yes.cart.web.util.WicketUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @SpringBean(name = StorefrontServiceSpringKeys.CATEGORY_SERVICE_FACADE)
    private CategoryServiceFacade categoryServiceFacade;

    private final long categoryId;
    private Category category;

    private List<FilteredNavigationRecord> navigationRecords = null;

    private final List<Long> categories;

    private final NavigationContext navigationContext;

    /**
     * Construct panel.
     *
     * @param id    panel id
     * @param categoryId current category id
     * @param navigationContext navigation context.
     */
    public AbstractProductFilter(final String id, final long categoryId, final NavigationContext navigationContext) {
        super(id);
        this.navigationContext = navigationContext;
        this.categoryId  = categoryId;
        this.categories = categoryServiceFacade.getSearchCategoriesIds(categoryId, ShopCodeContext.getShopId());
    }

    /**
     * Get current category.
     *
     * @return {@link Category}
     */
    public Category getCategory() {
        if (category == null) {
            category = categoryServiceFacade.getCategory(categoryId, ShopCodeContext.getShopId());
        }
        return category;
    }

    /**
     * Get the current subcategories sub tree as list.
     *
     * @return current subcategories sub tree as list.
     */
    protected List<Long> getCategories() {
        return categories;
    }

    /**
     * Adapt list of  {@link org.yes.cart.domain.queryobject.FilteredNavigationRecord} into ugly
     * list of attribute name - list of attributes values pair .
     *
     * @param records navigation records
     * @return list of pairs
     */
    protected List<Pair<Pair<String, String>, List<Pair<Pair<String, Integer>, PageParameters>>>>
                adaptNavigationRecords(final List<FilteredNavigationRecord> records) {
        String head = StringUtils.EMPTY;
        Pair<Pair<String, String>, List<Pair<Pair<String, Integer>, PageParameters>>> currentPair = null;

        final List<Pair<Pair<String, String>, List<Pair<Pair<String, Integer>, PageParameters>>>> headValueList =
                new ArrayList<Pair<Pair<String, String>, List<Pair<Pair<String, Integer>, PageParameters>>>>();

        for (FilteredNavigationRecord navigationRecord : records) {
            if (!navigationRecord.getName().equalsIgnoreCase(head)) {
                currentPair = new Pair<Pair<String, String>, List<Pair<Pair<String, Integer>, PageParameters>>>(
                        new Pair<String, String>(
                                navigationRecord.getCode(),
                                (String) ObjectUtils.defaultIfNull(navigationRecord.getDisplayName(), navigationRecord.getName())
                        ),
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
        add(
                new ListView<Pair<Pair<String, String>, List<Pair<Pair<String, Integer>, PageParameters>>>>(
                        FILTERED_NAVIGATION_LIST,
                        adaptNavigationRecords(navigationRecords)) {
                    protected void populateItem(ListItem<Pair<Pair<String, String>, List<Pair<Pair<String, Integer>, PageParameters>>>> pairListItem) {
                        final Pair<Pair<String, String>, List<Pair<Pair<String, Integer>, PageParameters>>> headValues = pairListItem.getModelObject();
                        pairListItem.add(
                                new BaseFilterView(FILTER, headValues.getFirst().getFirst(), headValues.getFirst().getSecond(), headValues.getSecond())
                        );
                    }
                }
        );

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
    public void setNavigationRecords(final List<FilteredNavigationRecord> navigationRecords) {
        this.navigationRecords = navigationRecords;
    }

    /**
     * Get navigation records.
     * @return   list of navigation records.
     */
    public List<FilteredNavigationRecord> getNavigationRecords() {
        return navigationRecords;
    }

    /**
     * Get current lucene product  query.
     *
     * @return current lucene product  query.
     */
    public NavigationContext getNavigationContext() {
        return navigationContext;
    }
}
